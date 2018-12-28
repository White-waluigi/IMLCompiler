package debugger;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.NumericShaper.Range;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.management.RuntimeErrorException;
import javax.swing.*;
import javax.swing.border.*;


import ch.fhnw.lederer.virtualmachineFS2015.Data.IBaseData;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray.CodeTooSmallError;
import ch.fhnw.lederer.virtualmachineFS2015.Data;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions;
import ch.fhnw.lederer.virtualmachineFS2015.IVirtualMachine.ExecutionError;
import ch.fhnw.lederer.virtualmachineFS2015.VirtualMachine;
import imlcompiler.Codegenerator.Codegenerator;
import imlcompiler.Scanner.Token;

public class Debugger extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	static int ctr = 0;

	public class Step implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			step.setText("Step");

			try {
				int x = dvm.step();
				if (x != 0)
					throw new ExecutionError("End of Program reaced!");

			} catch (ExecutionError e) {
				JOptionPane.showMessageDialog(null,
						"Programm terminatet because:\n\"" + e.getMessage().toString() + "\"", "Done",
						JOptionPane.INFORMATION_MESSAGE);
			}

			rebuildView();
			repaint();

		}

	}

	public void rebuildView() {
		memory.clear();

		int addr = 0;
		for (Data.IBaseData a : dvm.getStore()) {
			MemoryCell ins = new MemoryCell(a);
			if (dvm.getSp() < addr && ins.state == State.RESERVED)
				ins.state = State.FREED;

			if (watchdog == addr)
				vall.setText(ins.getValue());
			addr++;
			memory.add(ins);
		}

		if (dvm.getPc() < dvm.getCodeParent().length && dvm.getPc() >= 0) {
			jAssL.setSelectedValue(dvm.getCodeParent()[dvm.getPc()], true);
		}
	}

	public enum State {
		UNTOUCHED(Color.GRAY), FREED(Color.WHITE), RESERVED(Color.BLACK);

		public Color col;

		State(Color col) {
			this.col = col;
		}

	}

	public class MemoryCell {
		public State state = State.UNTOUCHED;
		public String text = "";
		Data.IBaseData parent;

		public MemoryCell(Data.IBaseData a) {
			state = a != null ? State.RESERVED : State.UNTOUCHED;

			parent = a;

		}

		public String getValue() {
			if (parent instanceof Data.IntData)
				return "" + ((Data.IntData) parent).getData();

			return "E";
		}
	}

	public ArrayList<MemoryCell> memory;

	private int tool = 1;
	int currentX, currentY, oldX, oldY;
	private VirtualMachine dvm;
	private JList jAssL;
	private JLabel vall;
	private int watchdog;
	private String refCode;
	private JTextArea outPut;

	public Debugger(int memorySize, Codegenerator codegenerator, String code) {
		super("Tupel Debugger");
		try {
			this.setIconImage(ImageIO.read(new File("deb.png")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.refCode = code;
		memory = new ArrayList<MemoryCell>(memorySize);

		initComponents(codegenerator.getCode().getSize());

		for (int i = 0; i < codegenerator.getCode().getSize(); i++) {
			jAssembly.addElement(codegenerator.getCode().get(i));
		}

		//initComponents();

		try {
			dvm = new VirtualMachine(codegenerator.getCode(), memorySize, true,new PrintDevice() {
				
				@Override
				public void print(String s) {
					outPut.setText(outPut.getText()+s+"\n");
				}
			});
		} catch (ExecutionError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dvm.init();

		rebuildView();

	}

	private void initComponents(int numberOfInstr) {

		// we want a custom Panel2, not a generic JPanel!
		jPanel2 = new MemoryView();

		jPanel2.setBackground(new java.awt.Color(255, 255, 255));
		jPanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		jPanel2.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				jPanel2MousePressed(evt);
			}

			public void mouseReleased(MouseEvent evt) {
				jPanel2MouseReleased(evt);
			}

		});
		jPanel2.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent evt) {
				jPanel2MouseDragged(evt);
			}
		});

		// be nice to testers..
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel DebugPanel = new JPanel();
		DebugPanel.setLayout(new BoxLayout(DebugPanel, BoxLayout.Y_AXIS));

		step = new JButton("Start");
		step.addActionListener(new Step());

		DebugPanel.add(step);

		this.jCode = new DefaultListModel<CodeLine>();
		this.jAssembly = new DefaultListModel<IInstructions.IInstr>();

		this.jAssL = new JList<>(this.jAssembly);
		// JList<IInstructions.IInstr> b=new JList<>(jAssembly);

		Integer[] numbers = new Integer[numberOfInstr];
		for(int i = 0 ; i < numberOfInstr; i++){
			numbers[i] = i;
		}
		DefaultListModel<Integer> integerDefaultListModel = new DefaultListModel<>();
		for(Integer i : numbers) integerDefaultListModel.addElement(i);
		JList<Integer> jList = new JList<>(integerDefaultListModel);
		jList.setFixedCellWidth(20);

		JPanel instructionPanel = new JPanel();
		instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.X_AXIS));
		instructionPanel.add(jList);
		instructionPanel.add(this.jAssL);


		vall = new JLabel("None");
		DebugPanel.add(vall);
		//DebugPanel.add(jAssL);
		//DebugPanel.add(jList);
		DebugPanel.add(instructionPanel);
		DebugPanel.add(new JSeparator());

		try (BufferedReader br = new BufferedReader(new FileReader(refCode))) {
			StringBuffer stringBuffer = new StringBuffer();

			String line;
			while ((line = br.readLine()) != null) {
				stringBuffer.append(line+"\n");
			}
			DebugPanel.add(new JTextArea(new String(stringBuffer.toString())));
			DebugPanel.add(new JLabel("Output:"));
			outPut=new JTextArea(new String());
			DebugPanel.add(outPut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// DebugPanel.add(b);
		// DebugPanel.add(new JSeparator());

		JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		main.setLeftComponent(jPanel2);
		main.setRightComponent(DebugPanel);
		this.add(main);

		pack();
	}// </editor-fold>

	private void jPanel2MouseDragged(MouseEvent evt) {

	}

	private void jPanel2MousePressed(MouseEvent evt) {
		int offsetx = 1;
		int offsety = 1;

		int addr = 0;
		for (MemoryCell mc : memory) {
			int ax = evt.getX() - offsetx * 20;
			int ay = evt.getY() - (110 + offsety * 20);

			if (0 <= ax && ax <= 20 && 0 <= ay && ay <= 20) {
				this.vall.setText(mc.getValue());
				watchdog = addr;
				// mc.state=State.FREED;
			}
			addr++;

			offsetx += 1;

			if (offsetx > 40) {
				offsetx = 1;
				offsety += 1;
			}

		}
		repaint();

	}

	// mouse released//
	private void jPanel2MouseReleased(MouseEvent evt) {

	}

//	// set ui visible//
//	public static void main(String args[]) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					new Debugger(1024, new Codegenerator(null, null),"******").setVisible(true);
//				} catch (CodeTooSmallError e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	// Variables declaration - do not modify
	private JPanel jPanel2;
	private DefaultListModel<IInstructions.IInstr> jAssembly;
	private DefaultListModel<CodeLine> jCode;
	private JButton step;

	// End of variables declaration

	// This class name is very confusing, since it is also used as the
	// name of an attribute!
	// class jPanel2 extends JPanel {

	public class MemoryView extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MemoryView() {
			// set a preferred size for the custom panel.
			setPreferredSize(new Dimension(450, 840));
			setBorder(BorderFactory.createBevelBorder(2));
		}

		int tableWidth = 40;

		private Point AddrToPos(int i) {
			return new Point((i % tableWidth) * 20 + 20, 130 + (i / tableWidth) * 20);
		}

		int ctr = 0;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.drawString("Memory", 20, 20);

			g.setColor(Color.BLUE);
			g.drawString("Extreme Pointer", 20, 40);
			g.setColor(Color.PINK);
			g.drawString("Frame Pointer", 20, 60);
			g.setColor(Color.RED);
			g.drawString("Stack Pointer", 20, 80);
			g.setColor(Color.CYAN);
			g.drawString("Heap Pointer", 200, 40);
			g.setColor(Color.GREEN);
			g.drawString("Program Counter", 200, 60);

			int offsetx = 1;
			int offsety = 1;
			for (MemoryCell mc : memory) {
				g.setColor(mc.state.col);
				g.fillRect(offsetx * 20, 110 + offsety * 20, 19, 19);
				g.setColor(Color.GREEN);
				g.drawString(mc.getValue(), offsetx * 20 + 5, (110 + offsety * 20) + 12);
				offsetx += 1;

				if (offsetx > tableWidth) {
					offsetx = 1;
					offsety += 1;
				}
			}
			g.setColor(Color.BLACK);
			for (int i = 0; i < tableWidth; i++) {
				g.drawString("" + (i - 1), i * 20 + 5, (110 + 0 * 20) + 12);

			}
			for (int i = 0; i < 1 + memory.size() / tableWidth; i++) {
				g.drawString("" + (i - 1), 0 * 20 + 5, (110 + i * 20) + 12);
			}

			Point a = AddrToPos(dvm.getEp());
			g.setColor(Color.BLUE);
			g.fillRect(a.x + 0, a.y, 5, 5);

			a = AddrToPos(dvm.getFp());
			g.setColor(Color.PINK);
			g.fillRect(a.x + 5, a.y, 5, 5);

			a = AddrToPos(dvm.getSp());
			g.setColor(Color.RED);
			g.fillRect(a.x + 10, a.y, 5, 5);

			a = AddrToPos(dvm.getHp());
			g.setColor(Color.CYAN);
			g.fillRect(a.x + 15, a.y, 5, 5);

			a = AddrToPos(dvm.getPc());
			g.setColor(Color.GREEN);
			g.fillRect(a.x + 5, a.y - 25, 6, 6);

		}
	}
}