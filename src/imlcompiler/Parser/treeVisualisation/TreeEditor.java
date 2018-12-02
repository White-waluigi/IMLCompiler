package imlcompiler.Parser.treeVisualisation;

import imlcompiler.Parser.ImlComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.management.RuntimeErrorException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class TreeEditor extends JFrame {
	private static final long serialVersionUID = -1350806931333831745L;
	Tree<String, ?> treeModel = null;
	TreeComponent treeView = null;
	JTextField text;
	static Wrapper wrapper;

	public TreeEditor(Tree<String, ?> t, Wrapper wrapper) {
		
		this. wrapper = wrapper;
		setTitle("Parse Tree Editor (" + t.getClass().getName() + ")");
		treeModel = t;
		treeView = new TreeComponent(treeModel);
		getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 1));
		getContentPane().add("Center", treeView);
		getContentPane().add("East", panel);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		pack();
		setLocationRelativeTo(null);
	}

	private static class TreeComponent extends JComponent {
		private static final long serialVersionUID = -3958752705443429232L;
		static final int MIN_HEIGHT = 3;
		Tree<String, ?> treeModel;
		Integer selection = null;
		Font font;

		TreeComponent(Tree<String, ?> t) {
			treeModel = t;
		}

		@Override
		public Font getFont() {
			return font;
		}

		int depth = 0;
		int spread = 0;
		int col = 255;


		void drawTree(Graphics g, Tree.Node<String, ?> t, int x, int y, int dx,
				int dy, int c,int depth) {

	    	
	    	
			if(t == null) return;

			int numberOfChildren = t.getNumberOfChildren();
			//System.out.println("numberOfChildren: " +numberOfChildren + " " + t.getKey());

			for (int i = 0 ; i < numberOfChildren ; i++) {

				spread = -(numberOfChildren-1) * 120;
				Tree.Node next = t.getNext();

				g.drawLine(x, y, x + spread + i * 60 , y + dy);
				drawTree(g, next, x + spread + i * 60, y + dy, dx / 2, dy, c,depth);


				for(ImlComponent.Node n : wrapper.wrapperArray){
					if (n != null && n.getKey().equals(next.getKey())){
						drawTree(g, n, x + spread + i * 100 , y + dy, dx / 2, depth, c,depth);
						depth += 2;
						col -= 1;
					}
				}

			}
			if (numberOfChildren == 0)
				g.setColor(Color.RED);
			else g.setColor(Color.lightGray);
			g.fillOval(x - c, y - c, 2 * c + 1, 2 * c + 1);
			g.setColor(Color.black);
			g.drawOval(x - c, y - c, 2 * c, 2 * c);
			String str;
			str = t.getKey();
			FontMetrics m = g.getFontMetrics(font);
			g.drawString(str, x - m.stringWidth(str) / 2, y + m.getAscent() / 2 - 3);

		}

		void drawTree2(Graphics g, Tree.Node<String, ?> t, int x, int y, int dx,
					  int dy, int c) {

			drawTree(g, wrapper.wrapperArray[0], x, y, dx, dy, c,0);
		}


		@Override
		public void paint(Graphics g) {
			super.paint(g);
			int w = getWidth();
			int h = getHeight();
			int dy = 70;//h / (1 + Math.max(MIN_HEIGHT, treeModel.height()));
			int y = dy / 2;
			int c = y / 2;
			int x = w/2+600;
			int dx = (x - c) / 2;
			g.setFont(font = new Font("Helvetica", Font.BOLD, 11));
			drawTree2(g, null, x, y, dx, dy, c);
		}
	}
}