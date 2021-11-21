/*
 * FileSplitterAboutPanel.java
 * A panel in the JFSplit GUI
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
 * 
 */

package com.jfsplit.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;

/**
 * Panel which contains necessary information about the utility (About Panel)
 */
public class FileSplitterAboutPanel extends JPanel {

	private static final long serialVersionUID = -6771479064358161967L;

	public FileSplitterAboutPanel() {
		super(new MigLayout("inset 5", "[grow,fill]"));

		// to display version
		StringBuffer str_version = new StringBuffer();
		str_version.append("<HTML>");
		str_version.append("<FONT SIZE='2'><B>");
		str_version.append("VERSION ");
		str_version.append(FileSplitterFrame.VERSION);
		str_version.append("</B><FONT>");
		str_version.append("</HTML>");

		// to display details
		StringBuffer sr_others = new StringBuffer();
		sr_others.append("<HTML>");
		sr_others.append("<FONT SIZE='2'>");
		sr_others
				.append("<B>CREATED BY : <FONT SIZE='3'> SUHAIB KHAN</FONT></B><BR />");
		sr_others.append("<B>JFSPLIT HOMEPAGE : <FONT COLOR='BLUE' SIZE='3'>");
		sr_others.append("http://jfsplit.sourceforge.net</FONT></B>");
		sr_others.append("</FONT>");
		sr_others.append("</HTML>");

		// to display copyright details
		StringBuffer sr_copyright = new StringBuffer();
		sr_copyright.append("<HTML>");
		sr_copyright.append("<FONT SIZE='2'>");
		sr_copyright.append("<B>COPYRIGHT (C) 2012 SUHAIB KHAN</B>");
		sr_copyright.append("</FONT>");
		sr_copyright.append("</HTML>");

		JPanel panel_about = new JPanel(new MigLayout("inset 5", "[grow,fill]"));

		JPanel panel_about_bac = new JPanel(new MigLayout("inset 0, center"));
		ImageIcon imageicon_logo = new ImageIcon(
				FileSplitterAboutPanel.class.getClassLoader().getResource("jfsplit_logo.png"));
		int img_width = imageicon_logo.getImage().getWidth(null);
		panel_about_bac.add(new JLabel(imageicon_logo),
				"center, wrap, gaptop 10");
		panel_about_bac
				.add(new JLabel(str_version.toString(), SwingConstants.RIGHT),
						"center, wrap, width " + img_width + "!");

		// Create an editor pane.
		JEditorPane ep_about = createAboutEditorPane();
		JScrollPane sp_ep_about = new JScrollPane(ep_about);
		panel_about_bac.add(sp_ep_about, "gaptop 10, wrap, width 450!");

		panel_about_bac.add(new JLabel(sr_others.toString()), "gaptop 5,wrap");
		panel_about_bac.add(new JLabel(sr_copyright.toString(),
				SwingConstants.RIGHT), "gaptop 5, width 450!");

		panel_about.add(panel_about_bac);

		add(panel_about);
	}

	// create and returns EditorPane with loaded about html
	private JEditorPane createAboutEditorPane() {
		JEditorPane editor_pane = new JEditorPane();
		editor_pane.setEditable(false);
		// get about url
		URL about_url = Class.class
				.getResource("/resources/jfsplit_about.html");
		if (about_url != null) {
			try {
				// set about page in editor pane
				editor_pane.setPage(about_url);
			} catch (IOException e) {/* Do nothing */
			}
		}
		return editor_pane;
	}
}
