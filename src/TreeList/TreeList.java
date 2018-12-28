/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package TreeList;

/**
 * This application that requires the following additional files:
 *   TreeDemoHelp.html
 *    arnold.html
 *    bloch.html
 *    chan.html
 *    jls.html
 *    swingtutorial.html
 *    tutorial.html
 *    tutorialcont.html
 *    vm.html
 */
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import imlcompiler.Parser.ImlComponent;
import imlcompiler.Parser.ImlComposite;
import imlcompiler.Parser.ImlItem;
import imlcompiler.Parser.treeVisualisation.Tree;
import imlcompiler.Parser.treeVisualisation.Wrapper;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridLayout;

public class TreeList extends JPanel implements TreeSelectionListener {
	private JEditorPane htmlPane;
	private JTree tree;
	private URL helpURL;
	private static boolean DEBUG = false;

	// Optionally play with line styles. Possible values are
	// "Angled" (the default), "Horizontal", and "None".
	private static boolean playWithLineStyle = false;
	private static String lineStyle = "Horizontal";

	// Optionally set the look and feel.
	private static boolean useSystemLookAndFeel = false;
	public Wrapper wrapper;
	private JTree sree;

	public TreeList(ImlComponent t, ImlComponent s, Wrapper wrapper, String file) {

		super(new GridLayout(1, 0));

		this.wrapper = wrapper;
		// Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("concrete (parse)");
		list(top, t, 0);
		DefaultMutableTreeNode sop = new DefaultMutableTreeNode("abstract (syntax)");
		list(sop, s, 0);

		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		sree = new JTree(sop);
		sree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		expandAllNodes(tree, 0, tree.getRowCount());
		expandAllNodes(sree, 0, tree.getRowCount());

		// Listen for when the selection changes.
		// tree.addTreeSelectionListener(this);

		if (playWithLineStyle) {
			System.out.println("line style = " + lineStyle);
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);
		JScrollPane sreeView = new JScrollPane(sree);

		// Create the HTML viewing pane.
		try {
			File f = new File(file);
			//System.out.println("file://" + f.getAbsolutePath());
			htmlPane = new JEditorPane("file://" + f.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		htmlPane.setEditable(false);
		initHelp();
		JScrollPane htmlView = new JScrollPane(htmlPane);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(sreeView);

		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);

		// splitPane.setDividerLocation(200);

		// splitPane.setPreferredSize(new Dimension(500, 300));

		JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPane.setTopComponent(splitPane);
		mainPane.setBottomComponent(htmlView);

		// Add the split pane to this panel.
		add(mainPane);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {

	}

	private class BookInfo {
		public String bookName;
		public URL bookURL;

		public BookInfo(String book, String filename) {
			bookName = book;
			bookURL = getClass().getResource(filename);
			if (bookURL == null) {
				System.err.println("Couldn't find file: " + filename);
			}
		}

		public String toString() {
			return bookName;
		}
	}

	private void initHelp() {

	}

	private void displayURL(URL url) {
//		try {
//			if (url != null) {
//				htmlPane.setPage(url);
//			} else { // null url
//				htmlPane.setText("File Not Found");
//				if (DEBUG) {
//					System.out.println("Attempted to display a null URL.");
//				}
//			}
//		} catch (IOException e) {
//			System.err.println("Attempted to read a bad URL: " + url);
//		}
	}

	public void list(DefaultMutableTreeNode top, ImlComponent t, int depth) {
		depth++;
		String name = t.getName();
		if ((t).getToken() != null)
			name = "*" + (t).getToken();

		DefaultMutableTreeNode category = new DefaultMutableTreeNode(name);
		top.add(category);
		if ((t instanceof ImlComposite)) {

			for (ImlComponent a : ((ImlComposite) t).getImlComponents()) {
				list(category, a, depth);

			}
		}

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event dispatch thread.
	 */
	private static void createAndShowGUI(ImlComponent t, ImlComponent s, Wrapper wrapper, String file) {
		if (useSystemLookAndFeel) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				System.err.println("Couldn't use system look and feel.");
			}
		}

		// Create and set up the window.
		JFrame frame = new JFrame(file);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);

		// Add content to the window.
		frame.add(new TreeList(t, s, wrapper, file));

		// Display the window.
		// frame.pack();
		frame.setVisible(true);
	}

	private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
		for (int i = startingIndex; i < rowCount; ++i) {
			tree.expandRow(i);
		}

		if (tree.getRowCount() != rowCount) {
			expandAllNodes(tree, rowCount, tree.getRowCount());
		}
	}

	public static void startNew(ImlComponent t, ImlComponent s, Wrapper wrapper, String file) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(t, s, wrapper, file);
			}
		});
	}
}
