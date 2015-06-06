/*
 * FileJoinerPanel.java
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

package jfsplit.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import jfsplit.core.FileJoinProcess;
import jfsplit.core.FileOperations;
import jfsplit.core.FileProcess;
import jfsplit.core.ProcessCaller;
import jfsplit.core.Status;
import jfsplit.gui.ExtensionFileFilter;

import net.miginfocom.swing.MigLayout;

/**
 * Panel which contains required gui elements for file joiner
 */
public class FileJoinerPanel extends JPanel implements ActionListener,
		ProcessCaller {

	private static final long serialVersionUID = -8025755473565926873L;

	// Required components for GUI
	private JLabel lbl_filesize = new JLabel("File Size : ", JLabel.RIGHT);
	private JLabel lbl_filestatus = new JLabel("Copying file");
	private JLabel lbl_sizestatus = new JLabel("Bytes", JLabel.RIGHT);
	private JLabel lbl_elap_timestatus = new JLabel("Time Elapsed ");
	private JLabel lbl_rem_timestatus = new JLabel("Estimated Time Remaining ",
			JLabel.RIGHT);

	private JTextField tf_firstsrc_path = new JTextField();
	private JTextField tf_despath = new JTextField();

	private JButton btn_srcbrowse = new JButton("Browse");
	private JButton btn_desbrowse = new JButton("Browse");
	private JButton btn_findfile = new JButton("Find Files");
	private JButton btn_addfile = new JButton("Add Files");
	private JButton btn_remfile = new JButton("Remove Files");
	private JButton btn_join = new JButton("Join");
	private JButton btn_cancel = new JButton("Cancel");

	private JProgressBar pb_join_status = new JProgressBar();

	private JRadioButton rb_autojoin = new JRadioButton(
			"Automatic Joining (Based on Source File)");
	private JRadioButton rb_manjoin = new JRadioButton(
			"Manual Joining (Based on Selected Files)");

	private JCheckBox cb_delete = new JCheckBox(
			"Delete source Split Files after joining");

	private JTable table_files = new JTable();
	private DefaultTableModel table_model;

	// Source files in case of manual joining
	private ArrayList<File> src_files = new ArrayList<File>();
	/*
	 * Source file (any of the split file) in case of automatic joining
	 */
	private File src_file;
	// destination file
	private File des_file;

	// FileProcess for file joining
	private FileProcess join_process;

	public FileJoinerPanel() {

		// add required components to the panel
		super(new MigLayout("inset 5", "[grow,fill]"));

		JPanel panel_join = new JPanel(new MigLayout("inset 5", "[grow,fill]"));

		JPanel panel_join_top = new JPanel(new MigLayout("inset 0",
				"[grow,fill]5[]"));
		panel_join_top.add(new JLabel("Source File (Select any segment) :"),
				"align label, split");
		panel_join_top.add(lbl_filesize, "align label,push,wrap");
		panel_join_top.add(tf_firstsrc_path);
		panel_join_top.add(btn_srcbrowse, "growx,wrap");
		panel_join_top.add(new JLabel("Selected Files (For manual joining) :"),
				"align label,wrap");
		table_model = new DefaultTableModel(null, new String[] { "File Name",
				"Path", "Size" }) {

			private static final long serialVersionUID = -3113467772688015325L;

			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		table_files.setModel(table_model);
		JScrollPane spane_table_files = new JScrollPane(table_files);
		panel_join_top.add(spane_table_files, "height 150!");

		JPanel panel_join_top_table_right = new JPanel(new MigLayout("inset 0",
				""));
		panel_join_top_table_right.add(btn_findfile, "sizegroup bttn,wrap");
		panel_join_top_table_right.add(btn_addfile, "sizegroup bttn,wrap");
		panel_join_top_table_right.add(btn_remfile, "sizegroup bttn");
		panel_join_top.add(panel_join_top_table_right, "wrap");

		panel_join_top.add(new JLabel("Output File :"), "align label,wrap");
		panel_join_top.add(tf_despath);
		panel_join_top.add(btn_desbrowse, "growx");
		panel_join.add(panel_join_top, "wrap");

		JPanel panel_join_options = new JPanel(new MigLayout("inset 0",
				"[grow,fill]"));
		panel_join_options.setBorder(new TitledBorder("Join Options"));

		JPanel panel_join_options_left = new JPanel(new MigLayout("inset 0 5"));
		ButtonGroup rb_join_group = new ButtonGroup();
		rb_join_group.add(rb_autojoin);
		rb_join_group.add(rb_manjoin);
		panel_join_options_left.add(rb_autojoin, "wrap");
		panel_join_options_left.add(rb_manjoin);
		panel_join_options.add(panel_join_options_left);

		JPanel panel_join_options_right = new JPanel(new MigLayout("inset 0 5"));
		panel_join_options_right.add(cb_delete);
		panel_join_options.add(panel_join_options_right);

		panel_join.add(panel_join_options, "wrap");

		JPanel panel_join_buttons = new JPanel(new MigLayout(
				"inset 5 0, right", "[]5[]"));
		panel_join_buttons.add(btn_join, "sizegroup bttn");
		panel_join_buttons.add(btn_cancel, "sizegroup bttn");
		panel_join.add(panel_join_buttons, "wrap");

		JPanel panel_join_status = new JPanel(new MigLayout("inset 0 5 0 4",
				"[grow,fill]"));
		panel_join_status.setBorder(new TitledBorder("Status"));
		panel_join_status.add(lbl_filestatus, "align label, split");
		panel_join_status.add(lbl_sizestatus, "align label,push,wrap");
		panel_join_status.add(pb_join_status, "wrap");
		panel_join_status.add(lbl_elap_timestatus, "align label, split");
		panel_join_status.add(lbl_rem_timestatus, "align label,push");
		panel_join.add(panel_join_status);

		add(panel_join);

		reset();

		addActionListeners();
	}

	// adding action listeners
	private void addActionListeners() {
		btn_srcbrowse.addActionListener(this);
		btn_desbrowse.addActionListener(this);

		btn_findfile.addActionListener(this);
		btn_addfile.addActionListener(this);
		btn_remfile.addActionListener(this);

		btn_join.addActionListener(this);
		btn_cancel.addActionListener(this);
	}

	// for reseting components to starting state
	private void reset() {

		des_file = null;
		join_process = null;

		pb_join_status.setValue(0);

		// clear table
		if (!src_files.isEmpty()) {
			src_files.clear();
		}
		int table_row_count = table_model.getRowCount();
		for (int i = table_row_count - 1; i >= 0; i--) {
			table_model.removeRow(i);
		}

		lbl_filesize.setVisible(false);
		lbl_filestatus.setVisible(false);
		lbl_sizestatus.setVisible(false);
		lbl_elap_timestatus.setVisible(false);
		lbl_rem_timestatus.setVisible(false);

		rb_autojoin.setSelected(true);
		cb_delete.setEnabled(true);
		cb_delete.setSelected(false);

		tf_firstsrc_path.setEditable(false);
		tf_despath.setEditable(false);

		tf_firstsrc_path.setText("");
		tf_despath.setText("");

		btn_join.setEnabled(false);
		btn_cancel.setEnabled(false);
		btn_srcbrowse.setEnabled(true);
		btn_desbrowse.setEnabled(true);
		btn_findfile.setEnabled(true);
		btn_addfile.setEnabled(true);
		btn_remfile.setEnabled(true);
	}

	/*
	 * show file select dialog that can select multiple files and return
	 * selected files as File[]
	 */
	private File[] browseForFiles() {
		File[] selected_files = null;

		JFileChooser jfilechooser_selectfiles = new JFileChooser(new File("."));

		// code removed so that Manual Joining supports
		// all files
		/*
		 * String str_des = "Split Files (" + FileProcess.SPLIT_FILE_EXT + "1, "
		 * + FileProcess.SPLIT_FILE_EXT + "2, ... || 001, 002, ...)"; String[]
		 * exts = {FileProcess.SPLIT_FILE_EXT + "1...","001..."};
		 * jfilechooser_selectfolder.setAcceptAllFileFilterUsed(false);
		 * jfilechooser_selectfolder.setFileFilter(new
		 * ExtensionFileFilter(str_des,exts));
		 */

		// enable multiple file selection
		jfilechooser_selectfiles.setMultiSelectionEnabled(true);
		// show select files dialog
		if (jfilechooser_selectfiles.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			selected_files = jfilechooser_selectfiles.getSelectedFiles();
		}

		return selected_files;
	}

	// show file select dialog and return selected file
	private File browseForFile(File file, boolean opendialog, String[] exts,
			String str_des) {
		File selected_file = null;
		// create file select dialog
		JFileChooser jfilechooser_selectfile = new JFileChooser(file);

		if (exts != null) {
			// disable the "All files" option.
			jfilechooser_selectfile.setAcceptAllFileFilterUsed(false);
			// add extension filter
			jfilechooser_selectfile.setFileFilter(new ExtensionFileFilter(
					str_des, exts));
		}

		if (opendialog) {
			// show open dialog
			if (jfilechooser_selectfile.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				selected_file = jfilechooser_selectfile.getSelectedFile();
			}
		} else {
			// show save dialog
			if (jfilechooser_selectfile.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				selected_file = jfilechooser_selectfile.getSelectedFile();
			}
		}

		return selected_file;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_srcbrowse) {
			// browse for source file
			String str_des = "Split Files (" + FileProcess.SPLIT_FILE_EXT
					+ "1, " + FileProcess.SPLIT_FILE_EXT
					+ "2, ... || 001, 002, ...)";
			src_file = browseForFile(new File("."), true, new String[] {
					FileProcess.SPLIT_FILE_EXT + "1...", "001..." }, str_des);
			if (src_file != null) {

				// check whether selected file exists
				if (!src_file.exists()) {
					showError("File not exists");
					src_file = null;
					tf_firstsrc_path.setText("");
					if (src_files.isEmpty()) {
						btn_join.setEnabled(false);
					}
					return;
				}

				tf_firstsrc_path.setText(src_file.getAbsolutePath());

				// show file size
				String str_filesize = "File Size : "
						+ FileOperations.getFileSizeStr(src_file.length());
				lbl_filesize.setText(str_filesize);
				lbl_filesize.setVisible(true);

				// get the folder of source file
				File src_folder = src_file.getParentFile();
				// get filename of source file
				String src_filename = src_file.getName();
				// get the extension of source file
				String src_fileext = src_filename.substring(src_filename
						.lastIndexOf(".") + 1);
				// find the filename without extension
				String src_filename_withoutext = src_filename.substring(0,
						src_filename.lastIndexOf(src_fileext) - 1);

				des_file = new File(src_folder, src_filename_withoutext);

				if (des_file != null) {
					if (des_file.exists()) {
						// show warning if tries to overwrite existing file
						int option = JOptionPane.showConfirmDialog(
								this,
								"Do you want to replace existing "
										+ des_file.getName()
										+ " with the joined file ?",
								FileSplitterFrame.APP_TITLE,
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (option == JOptionPane.OK_OPTION) {
							tf_despath.setText(des_file.getAbsolutePath());
							btn_join.setEnabled(true);
						} else {
							des_file = null;
							tf_despath.setText("");
							btn_join.setEnabled(false);
						}
					} else {
						tf_despath.setText(des_file.getAbsolutePath());
						btn_join.setEnabled(true);
					}
				}
			}
		} else if (e.getSource() == btn_desbrowse) {
			// show save dialog
			des_file = browseForFile(new File("."), false, null, null);
			if (des_file != null) {

				if (des_file.exists()) {
					// show warning if tries to overwrite existing file
					int option = JOptionPane.showConfirmDialog(
							this,
							"Do you want to replace existing "
									+ des_file.getName()
									+ " with the joined file ?",
							FileSplitterFrame.APP_TITLE,
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (option == JOptionPane.OK_OPTION) {
						tf_despath.setText(des_file.getAbsolutePath());
						if (src_file != null || !src_files.isEmpty()) {
							// enable join button if source file(s) present
							btn_join.setEnabled(true);
						}
					} else {
						des_file = null;
						tf_despath.setText("");
						btn_join.setEnabled(false);
					}
				} else {

					// checking whether parent folder exists
					if (!des_file.getParentFile().exists()
							|| !des_file.getParentFile().isDirectory()) {
						showError("Specified folder not exists");
						des_file = null;
						tf_despath.setText("");
						btn_join.setEnabled(false);
						return;
					}

					tf_despath.setText(des_file.getAbsolutePath());
					if (src_file != null || !src_files.isEmpty()) {
						// enable join button if source file(s) present
						btn_join.setEnabled(true);
					}
				}
			}
		} else if (e.getSource() == btn_findfile) {
			/*
			 * find other parts of file with selected segment
			 */
			if (src_file != null) {
				findFilesFromSegment(true);
			} else {
				showError("No source file selected");
			}
		} else if (e.getSource() == btn_addfile) {

			// adding files to table for manual joining
			File[] selected_files = browseForFiles();
			if (selected_files != null) {
				// add all files to table
				for (int i = 0; i < selected_files.length; i++) {
					addToFileTable(selected_files[i], true);
				}

				if (!src_files.isEmpty() && des_file != null) {
					btn_join.setEnabled(true);
				}
			}

		} else if (e.getSource() == btn_remfile) {
			// remove selected files from table
			int[] selected_rows = table_files.getSelectedRows();

			if (selected_rows.length <= 0) {
				showError("Nothing selected");
				return;
			}

			for (int i = 0; i < selected_rows.length; i++) {
				table_model.removeRow(selected_rows[i] - i);
				src_files.remove(selected_rows[i] - i);
			}

			if (src_files.isEmpty() && src_file == null) {
				btn_join.setEnabled(false);
			}
		} else if (e.getSource() == btn_join) {
			// start process
			startProcess();
		} else if (e.getSource() == btn_cancel) {
			// stop running process
			stopProcess();
		}
	}

	/*
	 * find other parts of file with selected segment
	 * 
	 * add_to_table is false then files found are not added to table but added
	 * to source files ArrayList
	 */
	private void findFilesFromSegment(boolean add_to_table) {

		// clear table
		if (!src_files.isEmpty()) {
			src_files.clear();
		}

		int table_row_count = table_model.getRowCount();
		for (int i = table_row_count - 1; i >= 0; i--) {
			table_model.removeRow(i);
		}

		// get the folder of source file
		File src_folder = src_file.getParentFile();

		String src_filename = src_file.getName();
		String src_fileext = src_filename.substring(src_filename
				.lastIndexOf(".") + 1);
		// find the filename without extension
		String src_filename_withoutext = src_filename.substring(0,
				src_filename.lastIndexOf(src_fileext) - 1);

		// find which part of the target file is this source file
		int cur_split_segment;
		try {
			cur_split_segment = Integer.parseInt(src_fileext);
		} catch (NumberFormatException ex) {
			cur_split_segment = Integer.parseInt(src_fileext.substring(3));
		}

		// for creating file extension line (001,002)
		DecimalFormat formater = new DecimalFormat("000");

		// find the first part available
		// if its not the first part of the file
		while (true) {
			File new_src_file;
			if (src_fileext.matches("[\\d]{3}")) {
				new_src_file = new File(src_folder, src_filename_withoutext
						+ "." + formater.format(--cur_split_segment));
			} else {
				new_src_file = new File(src_folder, src_filename_withoutext
						+ "." + FileProcess.SPLIT_FILE_EXT
						+ --cur_split_segment);
			}

			if (!new_src_file.exists()) {
				cur_split_segment++;
				break;
			}
		}

		// add all available file parts to table
		while (true) {
			File new_src_file;
			if (src_fileext.matches("[\\d]{3}")) {
				new_src_file = new File(src_folder, src_filename_withoutext
						+ "." + formater.format(cur_split_segment++));
			} else {
				new_src_file = new File(src_folder, src_filename_withoutext
						+ "." + FileProcess.SPLIT_FILE_EXT
						+ cur_split_segment++);
			}

			if (!new_src_file.exists()) {
				cur_split_segment--;
				break;
			} else {
				addToFileTable(new_src_file, add_to_table);
			}
		}
	}

	/*
	 * add a file to table and source files ArrayList
	 * 
	 * add_to_table is false then files found are not added to table but added
	 * to source files ArrayList
	 */
	private void addToFileTable(File file, boolean add_to_table) {
		/*
		 * check whether the file exists and is not a folder
		 */
		if (file.exists() && file.isFile()) {
			if (!src_files.contains(file)) {
				if (add_to_table) {
					table_model.addRow(new String[] { file.getName(),
							file.getAbsolutePath(),
							FileOperations.getFileSizeStr(file.length()) });
				}
				// add to ArrayList
				src_files.add(file);
			}
		}
	}

	@Override
	public void updateStatus(Status cur_status) {
		/*
		 * if process running then update the status of the process in the gui
		 */
		if (join_process != null) {
			// set progress bar value
			pb_join_status.setValue(cur_status.getProgressValue());

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

		/*
		 * if automatic joining then find other parts of the file from the
		 * selected segment
		 */
		if (rb_autojoin.isSelected()) {
			if (src_file != null) {
				findFilesFromSegment(false);
			} else {
				showError("Source file not found");
				return;
			}
		}

		/*
		 * check if there are some source files and a destination file
		 */
		if (src_files.isEmpty() || des_file == null) {
			showError("Required file(s) or folder doesn't exists");
			return;
		}

		// convert ArrayList to an array
		File[] selected_files = new File[src_files.size()];
		for (int i = 0; i < src_files.size(); i++) {
			selected_files[i] = src_files.get(i);
		}

		/*
		 * start process for joining files in a separate thread
		 */
		join_process = new FileJoinProcess(this, selected_files, des_file);
		Thread process_thread = new Thread(join_process,
				"File Joining Process Thread");
		process_thread.start();

		btn_join.setEnabled(false);
		btn_cancel.setEnabled(true);
		btn_srcbrowse.setEnabled(false);
		btn_desbrowse.setEnabled(false);
		btn_findfile.setEnabled(false);
		btn_addfile.setEnabled(false);
		btn_remfile.setEnabled(false);

		cb_delete.setEnabled(false);
	}

	@Override
	public boolean stopProcess() {
		// if process running
		if (join_process != null) {

			// show confirmation dialog whether to stop process or not
			int option = JOptionPane.showConfirmDialog(this,
					"Do you want to stop joining ?",
					FileSplitterFrame.APP_TITLE, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (option == JOptionPane.CANCEL_OPTION) {
				return false;
			}

			// if ok and process running then stop it
			if (join_process != null) {
				join_process.forceStop();
			}

			reset();
		}
		return true;
	}

	@Override
	public void completed(Object[] result) {

		// result is not needed in this case

		/*
		 * if Delete source Split Files checked then delete them
		 */
		if (cb_delete.isSelected()) {
			deleteSourceFiles();
		}

		// show confirmation message
		String msg = "File joining completed";
		JOptionPane.showMessageDialog(this, msg, FileSplitterFrame.APP_TITLE,
				JOptionPane.INFORMATION_MESSAGE);

		reset();
	}

	// delete source files
	private void deleteSourceFiles() {
		Iterator<File> file_it = src_files.iterator();
		while (file_it.hasNext()) {
			File file = file_it.next();
			if (file.exists()) {
				file.delete();
			}
		}
	}

	@Override
	public void showError(String errmsg) {
		// show error message
		JOptionPane.showMessageDialog(this, errmsg, FileSplitterFrame.APP_TITLE
				+ " - Error", JOptionPane.ERROR_MESSAGE);
	}
}
