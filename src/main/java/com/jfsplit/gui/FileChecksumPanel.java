/*
 * FileChecksumPanel.java
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

import com.jfsplit.core.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Panel which contains required gui elements for checksum calculator
 */
public class FileChecksumPanel extends JPanel implements ActionListener,
		ProcessCaller {

	private static final long serialVersionUID = -2137395977448568242L;

	// Source file
	private File src_file;

	// Required components for GUI
	private JLabel lbl_filesize = new JLabel("File Size : ", JLabel.RIGHT);
	private JLabel lbl_filestatus = new JLabel("Copying file");
	private JLabel lbl_sizestatus = new JLabel("Bytes", JLabel.RIGHT);
	private JLabel lbl_elap_timestatus = new JLabel("Time Elapsed ");
	private JLabel lbl_rem_timestatus = new JLabel("Estimated Time Remaining ",
			JLabel.RIGHT);

	private JTextField tf_srcpath = new JTextField();
	private JTextField tf_checksum = new JTextField();

	private JProgressBar pb_checksum_status = new JProgressBar();

	private JButton btn_srcbrowse = new JButton("Browse");
	private JButton btn_copy = new JButton("Copy");
	private JButton btn_calculate = new JButton("Calculate");
	private JButton btn_cancel = new JButton("Cancel");

	// FileProcess for calculating checksum
	private FileProcess checksum_process;

	public FileChecksumPanel() {

		// add required components to the panel

		super(new MigLayout("inset 5", "[grow,fill]"));

		JPanel panel_checksum = new JPanel(new MigLayout("inset 5",
				"[grow,fill]"));

		JPanel panel_checksum_top = new JPanel(new MigLayout("inset 0",
				"[grow,fill]5[]"));
		panel_checksum_top.add(new JLabel("Source File :"),
				"align label, split");
		panel_checksum_top.add(lbl_filesize, "align label,push,wrap");
		panel_checksum_top.add(tf_srcpath);
		panel_checksum_top.add(btn_srcbrowse);
		panel_checksum.add(panel_checksum_top, "wrap");

		JPanel panel_checksum_result = new JPanel(new MigLayout("inset 0 5",
				"[]5[grow,fill]5[]"));
		panel_checksum_result.add(new JLabel("CRC-32 Checksum : "),
				"align label");
		panel_checksum_result.add(tf_checksum);
		panel_checksum_result.add(btn_copy);
		panel_checksum.add(panel_checksum_result, "wrap");

		JPanel panel_checksum_buttons = new JPanel(new MigLayout(
				"inset 5 0, right", "[]5[]"));
		panel_checksum_buttons.add(btn_calculate, "sizegroup bttn");
		panel_checksum_buttons.add(btn_cancel, "sizegroup bttn");
		panel_checksum.add(panel_checksum_buttons, "wrap");

		JPanel panel_checksum_span = new JPanel();
		panel_checksum.add(panel_checksum_span, "height 270!,wrap");

		JPanel panel_checksum_status = new JPanel(new MigLayout(
				"inset 0 5 0 4", "[grow,fill]"));
		panel_checksum_status.setBorder(new TitledBorder("Status"));
		panel_checksum_status.add(lbl_filestatus, "align label, split");
		panel_checksum_status.add(lbl_sizestatus, "align label,push,wrap");
		panel_checksum_status.add(pb_checksum_status, "wrap");
		panel_checksum_status.add(lbl_elap_timestatus, "align label, split");
		panel_checksum_status.add(lbl_rem_timestatus, "align label,push");
		panel_checksum.add(panel_checksum_status);

		add(panel_checksum);

		reset();

		src_file = null;
		tf_srcpath.setText("");
		tf_checksum.setText("");
		btn_copy.setEnabled(false);
		btn_calculate.setEnabled(false);
		lbl_filesize.setVisible(false);

		addActionListeners();

	}

	// adding action listeners
	private void addActionListeners() {
		btn_srcbrowse.addActionListener(this);
		btn_copy.addActionListener(this);
		btn_calculate.addActionListener(this);
		btn_cancel.addActionListener(this);
	}

	// for reseting components to starting state
	private void reset() {
		checksum_process = null;

		pb_checksum_status.setValue(0);

		lbl_filestatus.setVisible(false);
		lbl_sizestatus.setVisible(false);
		lbl_elap_timestatus.setVisible(false);
		lbl_rem_timestatus.setVisible(false);

		tf_srcpath.setEditable(false);
		tf_checksum.setEditable(false);

		btn_cancel.setEnabled(false);
		btn_srcbrowse.setEnabled(true);
	}

	// show file select dialog and return selected file
	private File browseForFile() {
		File selected_file = null;
		// create file select dialog
		JFileChooser jfilechooser_selectfolder = new JFileChooser(new File("."));

		// show dialog
		if (jfilechooser_selectfolder.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			selected_file = jfilechooser_selectfolder.getSelectedFile();
		}
		return selected_file;
	}

	@Override
	public void updateStatus(Status cur_status) {
		/*
		 * if process running then update the status of the process in the gui
		 */
		if (checksum_process != null) {
			// set progress bar value
			pb_checksum_status.setValue(cur_status.getProgressValue());

			// show and set various status
			lbl_filestatus.setVisible(true);
			lbl_sizestatus.setVisible(true);
			lbl_elap_timestatus.setVisible(true);
			lbl_rem_timestatus.setVisible(true);

			lbl_filestatus.setText(cur_status.getFileStatus());
			lbl_sizestatus.setText(cur_status.getSizeStatus());
			lbl_elap_timestatus.setText(cur_status.getElapTimeStatus());
			lbl_rem_timestatus.setText(cur_status.getRemTimeStatus());
		}
	}

	@Override
	public void startProcess() {

		// check whether there is a source file
		if (src_file == null) {
			showError("Source file doesn't exists");
			return;
		}

		// clear previous checksum
		tf_checksum.setText("");

		/*
		 * start process for calculating checksum in a separate thread
		 */
		checksum_process = new FileChecksumProcess(this, src_file);
		Thread process_thread = new Thread(checksum_process,
				"File Checksum Process Thread");
		process_thread.start();

		btn_calculate.setEnabled(false);
		btn_cancel.setEnabled(true);
		btn_srcbrowse.setEnabled(false);
	}

	@Override
	public boolean stopProcess() {
		// if process running
		if (checksum_process != null) {
			/*
			 * show confirmation dialog whether to stop process or not
			 */
			int option = JOptionPane.showConfirmDialog(this,
					"Do you want to stop calculating checksum ?",
					FileSplitterFrame.APP_TITLE, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (option == JOptionPane.CANCEL_OPTION) {
				return false;
			}

			// if ok and process running then stop it
			if (checksum_process != null) {
				checksum_process.forceStop();
			}
			reset();

			src_file = null;
			tf_srcpath.setText("");
			lbl_filesize.setVisible(false);
			btn_calculate.setEnabled(false);
		}
		return true;
	}

	@Override
	public void completed(Object[] result) {

		long checksum = (Long) result[0];
		// set checksum (CRC-32 Hex value)
		tf_checksum.setText(Long.toHexString(checksum).toUpperCase());

		// show confirmation message
		String msg = "Checksum calculated";
		JOptionPane.showMessageDialog(this, msg, FileSplitterFrame.APP_TITLE,
				JOptionPane.INFORMATION_MESSAGE);
		reset();

		btn_calculate.setEnabled(true);
		btn_copy.setEnabled(true);
	}

	@Override
	public void showError(String errmsg) {
		// show error message
		JOptionPane.showMessageDialog(this, errmsg, FileSplitterFrame.APP_TITLE
				+ " - Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_srcbrowse) {
			src_file = browseForFile();

			if (src_file != null) {

				// check whether selected file exists
				if (!src_file.exists()) {
					showError("File not exists");
					src_file = null;
					tf_srcpath.setText("");
					tf_checksum.setText("");
					btn_calculate.setEnabled(false);
					btn_copy.setEnabled(false);
					lbl_filesize.setVisible(false);
					return;
				}

				// show file path
				tf_srcpath.setText(src_file.getAbsolutePath());

				// show file size
				String str_filesize = "File Size : "
						+ FileOperations.getFileSizeStr(src_file.length());
				lbl_filesize.setText(str_filesize);
				lbl_filesize.setVisible(true);

				btn_calculate.setEnabled(true);
				btn_copy.setEnabled(false);

				tf_checksum.setText("");
			}
		} else if (e.getSource() == btn_calculate) {
			// start the process
			startProcess();
		} else if (e.getSource() == btn_cancel) {
			// stop the process
			stopProcess();
		} else if (e.getSource() == btn_copy) {
			// copy checksum to clipboard
			tf_checksum.selectAll();
			tf_checksum.copy();
			String msg = "Checksum copied to clipboard";
			JOptionPane.showMessageDialog(this, msg,
					FileSplitterFrame.APP_TITLE,
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
