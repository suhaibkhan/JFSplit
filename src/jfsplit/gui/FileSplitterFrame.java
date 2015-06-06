/*
 * FileSplitterFrame.java
 * Main Frame of the JFSplit GUI
 * Copyright (C) 2011 Suhaib Khan
 * suhaibklm@gmail.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jfsplit.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

/**
 * Main Frame (Starting Frame) in GUI of the utility
 */
public class FileSplitterFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1411977658598865411L;

	/**
	 * Name of the utility
	 */
	public static final String APP_TITLE = "JFSplit";
	/**
	 * Its current version
	 */
	public static final String VERSION = "1.0.1";

	/**
	 * For holding each panel separated that corresponds to an operation
	 */
	protected JTabbedPane tpane_utils;

	private JButton btn_exit = new JButton("Exit");

	/*
	 * Required panels holds GUI elements necessary for each operation
	 */
	private FileSplitterPanel split_panel;
	private FileJoinerPanel join_panel;
	private FileChecksumPanel checksum_panel;
	private FileSplitterAboutPanel about_panel;

	public FileSplitterFrame() {
		super(APP_TITLE + " " + VERSION);

		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLayout(new MigLayout("inset 5", "[grow,fill]", "[grow,fill]"));

		/*
		 * add a JTabbedPane for a distinguished view of available operations
		 */
		tpane_utils = new JTabbedPane();
		addPanels();
		add(tpane_utils, "wrap");

		JPanel panel_bottom = new JPanel(new MigLayout("inset 0, right"));
		panel_bottom.add(btn_exit, "gaptop 3, width 80!");

		add(panel_bottom);
		pack();
		setLocationRelativeTo(null);

		addActionListeners();
	}

	private void addActionListeners() {
		btn_exit.addActionListener(this);
	}

	/**
	 * Adding panels to the JTabbedPane Each tab indicates an operation
	 */
	protected void addPanels() {
		// creates necessary panels
		split_panel = new FileSplitterPanel();
		join_panel = new FileJoinerPanel();
		checksum_panel = new FileChecksumPanel();
		about_panel = new FileSplitterAboutPanel();
		// add them to JTabbedPane
		tpane_utils.add(split_panel, "Split Files");
		tpane_utils.add(join_panel, "Join Files");
		tpane_utils.add(checksum_panel, "Checksum");
		tpane_utils.add(about_panel, "About");
	}

	@Override
	public void dispose() {

		/*
		 * check whether any of the process is running and then exit
		 */
		boolean exit = true;

		if (exit) {
			exit = split_panel.stopProcess();
		}
		if (exit) {
			exit = join_panel.stopProcess();
		}
		if (exit) {
			exit = checksum_panel.stopProcess();
		}
		if (exit) {
			System.exit(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_exit) {
			dispose();
		}
	}
}
