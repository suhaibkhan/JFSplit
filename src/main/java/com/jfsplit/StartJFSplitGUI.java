/*
 * StartJFSplitGUI.java
 * Main class which contains the starting point of the JFSplit
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

package com.jfsplit;

import com.jfsplit.gui.FileSplitterFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Starts JFSplit tool. The starting point (main method) 
 * is contained in this class
 */
public class StartJFSplitGUI {
	public static void main(String[] args) {

		try {
			// Change GUI to current system (OS) look
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			String errmsg = "Error : Cannot switch to system look and feel.";
			JOptionPane.showMessageDialog(null, errmsg,
					FileSplitterFrame.APP_TITLE + " - Error",
					JOptionPane.ERROR_MESSAGE);
		}

		Runnable runner = new Runnable() {
			@Override
			public void run() {
				// show JFSplit GUI
				new FileSplitterFrame().setVisible(true);
			}
		};
		EventQueue.invokeLater(runner);
	}
}
