/*
 * FileSplitterPanel.java
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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import jfsplit.core.FileOperations;
import jfsplit.core.FileProcess;
import jfsplit.core.FileSplitProcess;
import jfsplit.core.Status;
import jfsplit.core.ProcessCaller;

import net.miginfocom.swing.MigLayout;

/**
 * Panel which contains required gui elements for file splitter
 */
public class FileSplitterPanel extends JPanel implements ActionListener,
		ProcessCaller {

	private static final long serialVersionUID = -399878751770202230L;

	// Required components for GUI
	private JLabel lbl_filesize = new JLabel("File Size : ", JLabel.RIGHT);
	private JLabel lbl_outputfile = new JLabel("Output files format : ",
			JLabel.RIGHT);
	private JLabel lbl_filestatus = new JLabel("Copying file");
	private JLabel lbl_sizestatus = new JLabel("Bytes", JLabel.RIGHT);
	private JLabel lbl_elap_timestatus = new JLabel("Time Elapsed ");
	private JLabel lbl_rem_timestatus = new JLabel("Estimated Time Remaining ",
			JLabel.RIGHT);

	private JTextField tf_srcpath = new JTextField();
	private JTextField tf_despath = new JTextField();
	private JTextField tf_partsize = new JTextField();
	private JTextField tf_partnos = new JTextField();

	private JSpinner sp_spiltfirst_partnos = new JSpinner();
	private JSpinner sp_spiltlast_partnos = new JSpinner();

	private JComboBox cb_sizeunits = new JComboBox(new String[] { " Bytes",
			" Kilo Bytes (KB)", " Mega Bytes (MB)", " Giga Bytes (GB)" });

	private JRadioButton rb_spiltbysize = new JRadioButton(
			"Split after every :");
	private JRadioButton rb_spiltbypart = new JRadioButton("Split into :");
	private JRadioButton rb_spiltall = new JRadioButton("Split all parts");
	private JRadioButton rb_spiltfirst = new JRadioButton(
			"Split only the first :");
	private JRadioButton rb_spiltlast = new JRadioButton(
			"Split only the last :");

	private JProgressBar pb_split_status = new JProgressBar();

	private JButton btn_srcbrowse = new JButton("Browse");
	private JButton btn_desbrowse = new JButton("Browse");
	private JButton btn_split = new JButton("Split");
	private JButton btn_cancel = new JButton("Cancel");

	// Source file
	private File src_file;
	// destination folder
	private File des_folder;

	// FileProcess for file splitting
	private FileProcess split_process;

	public FileSplitterPanel() {

		// add required components to the panel
		super(new MigLayout("inset 5", "[grow,fill]"));

		JPanel panel_split = new JPanel(new MigLayout("inset 5", "[grow,fill]"));

		JPanel panel_split_top = new JPanel(new MigLayout("inset 0",
				"[grow,fill]5[]"));
		panel_split_top.add(new JLabel("Source File (File to be splitted) :"),
				"align label, split");
		panel_split_top.add(lbl_filesize, "align label,push,wrap");
		panel_split_top.add(tf_srcpath);
		panel_split_top.add(btn_srcbrowse, "wrap");
		panel_split_top.add(new JLabel("Output Folder :"), "align label,split");
		panel_split_top.add(lbl_outputfile, "align label,push,wrap");
		panel_split_top.add(tf_despath);
		panel_split_top.add(btn_desbrowse);
		panel_split.add(panel_split_top, "wrap");

		JPanel panel_split_options = new JPanel(new MigLayout("inset 0"));
		panel_split_options.setBorder(new TitledBorder("Split Options"));

		JPanel panel_split_options_left = new JPanel(new MigLayout("inset 0 5",
				"[]5[]5[]"));
		ButtonGroup rb_split_group = new ButtonGroup();
		rb_split_group.add(rb_spiltbysize);
		rb_split_group.add(rb_spiltbypart);
		panel_split_options_left.add(rb_spiltbysize);
		panel_split_options_left.add(tf_partsize, "width 90!");
		panel_split_options_left.add(cb_sizeunits, "wrap");
		panel_split_options_left.add(rb_spiltbypart);
		panel_split_options_left.add(tf_partnos, "width 90!");
		panel_split_options_left.add(new JLabel("equal-size part(s)"),
				"align label");
		panel_split_options.add(panel_split_options_left);

		JPanel panel_split_options_right = new JPanel(new MigLayout(
				"inset 0 5", "[]5[]5[]"));
		ButtonGroup rb_splitskip_group = new ButtonGroup();
		rb_splitskip_group.add(rb_spiltall);
		rb_splitskip_group.add(rb_spiltfirst);
		rb_splitskip_group.add(rb_spiltlast);
		panel_split_options_right.add(rb_spiltall, "wrap");
		panel_split_options_right.add(rb_spiltfirst);
		panel_split_options_right.add(sp_spiltfirst_partnos, "width 90!");
		panel_split_options_right
				.add(new JLabel("part(s)"), "align label,wrap");
		panel_split_options_right.add(rb_spiltlast);
		panel_split_options_right.add(sp_spiltlast_partnos, "width 90!");
		panel_split_options_right.add(new JLabel("part(s)"), "align label");
		panel_split_options.add(panel_split_options_right);

		panel_split.add(panel_split_options, "wrap");

		JPanel panel_split_buttons = new JPanel(new MigLayout(
				"inset 5 0, right", "[]5[]"));
		panel_split_buttons.add(btn_split, "sizegroup bttn");
		panel_split_buttons.add(btn_cancel, "sizegroup bttn");
		panel_split.add(panel_split_buttons, "wrap");

		JPanel panel_split_span = new JPanel();
		panel_split.add(panel_split_span, "height 141!,wrap");

		JPanel panel_split_status = new JPanel(new MigLayout("inset 0 5 0 4",
				"[grow,fill]"));
		panel_split_status.setBorder(new TitledBorder("Status"));
		panel_split_status.add(lbl_filestatus, "align label, split");
		panel_split_status.add(lbl_sizestatus, "align label,push,wrap");
		panel_split_status.add(pb_split_status, "wrap");
		panel_split_status.add(lbl_elap_timestatus, "align label, split");
		panel_split_status.add(lbl_rem_timestatus, "align label,push");
		panel_split.add(panel_split_status);

		add(panel_split);

		reset();

		addActionListeners();
	}

	// adding action listeners
	private void addActionListeners() {
		rb_spiltbysize.addActionListener(this);
		rb_spiltbypart.addActionListener(this);

		rb_spiltall.addActionListener(this);
		rb_spiltfirst.addActionListener(this);
		rb_spiltlast.addActionListener(this);

		btn_srcbrowse.addActionListener(this);
		btn_desbrowse.addActionListener(this);
		btn_split.addActionListener(this);
		btn_cancel.addActionListener(this);
	}

	// for reseting components to starting state
	private void reset() {

		src_file = null;
		des_folder = null;
		split_process = null;

		cb_sizeunits.setSelectedIndex(0);
		pb_split_status.setValue(0);

		sp_spiltfirst_partnos.setValue(0);
		sp_spiltlast_partnos.setValue(0);

		lbl_filesize.setVisible(false);
		lbl_outputfile.setVisible(false);
		lbl_filestatus.setVisible(false);
		lbl_sizestatus.setVisible(false);
		lbl_elap_timestatus.setVisible(false);
		lbl_rem_timestatus.setVisible(false);

		cb_sizeunits.setEditable(false);

		rb_spiltbysize.setSelected(true);
		rb_spiltall.setSelected(true);

		tf_srcpath.setEditable(false);
		tf_despath.setEditable(false);

		tf_srcpath.setText("");
		tf_despath.setText("");
		tf_partsize.setText("");
		tf_partnos.setText("");

		btn_split.setEnabled(false);
		btn_cancel.setEnabled(false);
		btn_srcbrowse.setEnabled(true);
		btn_desbrowse.setEnabled(true);

		// enableOptions(false);

		firstOptionSelected();
		secondOptionSelected();
	}

	/*
	 * private void enableOptions(boolean enable){
	 * rb_spiltbysize.setEnabled(enable); rb_spiltbypart.setEnabled(enable);
	 * 
	 * rb_spiltall.setEnabled(enable); rb_spiltfirst.setEnabled(enable);
	 * rb_spiltlast.setEnabled(enable);
	 * 
	 * if (enable){ firstOptionSelected(); secondOptionSelected(); }else{
	 * tf_partsize.setEnabled(false); tf_partnos.setEnabled(false);
	 * cb_sizeunits.setEnabled(false); sp_spiltfirst_partnos.setEnabled(false);
	 * sp_spiltlast_partnos.setEnabled(false); } }
	 */

	// disable and enable components when first option changed
	private void firstOptionSelected() {
		if (rb_spiltbysize.isSelected()) {
			tf_partnos.setEnabled(false);
			tf_partsize.setEnabled(true);
			cb_sizeunits.setEnabled(true);
		} else if (rb_spiltbypart.isSelected()) {
			tf_partnos.setEnabled(true);
			tf_partsize.setEnabled(false);
			cb_sizeunits.setEnabled(false);
		}
	}

	// disable and enable components when second option changed
	private void secondOptionSelected() {
		if (rb_spiltall.isSelected()) {
			sp_spiltfirst_partnos.setEnabled(false);
			sp_spiltlast_partnos.setEnabled(false);
		} else if (rb_spiltfirst.isSelected()) {
			sp_spiltfirst_partnos.setEnabled(true);
			sp_spiltlast_partnos.setEnabled(false);
		} else if (rb_spiltlast.isSelected()) {
			sp_spiltfirst_partnos.setEnabled(false);
			sp_spiltlast_partnos.setEnabled(true);
		}
	}

	/*
	 * show file select dialog that can select a file or a folder and return
	 * selected as File
	 */
	private File browseForFile(boolean isfile) {
		File selected_file = null;
		// create file select dialog
		JFileChooser jfilechooser_selectfile = new JFileChooser(new File("."));

		if (!isfile) {
			// select a folder
			jfilechooser_selectfile.setDialogTitle("Browse For Folder");
			jfilechooser_selectfile
					.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			// disable the "All files" option.
			jfilechooser_selectfile.setAcceptAllFileFilterUsed(false);
		}

		// show dialog
		if (jfilechooser_selectfile.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			selected_file = jfilechooser_selectfile.getSelectedFile();
		}
		return selected_file;
	}

	@Override
	public void startProcess() {

		long part_size = 0;
		int part_nos = 0;
		int skip_first_nos = 0;
		int skip_last_nos = 0;

		// check whether there is a source file and destination folder
		if (src_file == null || des_folder == null) {
			showError("Specified file or folder doesn't exists");
			return;
		}

		if (rb_spiltbysize.isSelected()) {
			// split by size
			try {
				part_size = Long.parseLong(tf_partsize.getText());
			} catch (NumberFormatException e) {
				part_size = 0;
			}

			// size must be > 0
			if (part_size <= 0) {
				String errmsg = "Invalid size of parts for split files";
				showError(errmsg);
				return;
			}

			// converting size of each part in to bytes
			int selected_file_unit = cb_sizeunits.getSelectedIndex();
			if (selected_file_unit > 0) {
				part_size *= Math.pow(1024, selected_file_unit);
			}

			// calculating number of parts
			part_nos = ((src_file.length() % part_size) == 0) ? (int) (src_file.length() / part_size) : (int) (src_file.length() / part_size) + 1;

		} else if (rb_spiltbypart.isSelected()) {
			// split by number of parts
			try {
				part_nos = Integer.parseInt(tf_partnos.getText());
			} catch (NumberFormatException e) {
				part_nos = 0;
			}
			// number > 0
			if (part_nos <= 0) {
				String errmsg = "Invalid number of parts for split files";
				showError(errmsg);
				return;
			}
			// calculating size of each part
			part_size = ((src_file.length() % part_nos) == 0) ? (src_file.length() / part_nos) : (src_file.length() / part_nos) + 1;
		}

		// skipping options
		if (rb_spiltfirst.isSelected()) {

			// if split only first some parts
			int split_first_nos = (Integer) sp_spiltfirst_partnos.getValue();
			if (split_first_nos < 0) {
				String errmsg = "Invalid number of parts in skipping option";
				showError(errmsg);
				return;
			} else if (split_first_nos > part_nos) {
				split_first_nos = part_nos;
			}

			/*
			 * splitting only some first parts means skipping other parts from
			 * last
			 */
			skip_last_nos = part_nos - split_first_nos;

		} else if (rb_spiltlast.isSelected()) {

			// if split only last some parts
			int split_last_nos = (Integer) sp_spiltlast_partnos.getValue();
			if (split_last_nos < 0) {
				String errmsg = "Invalid number of parts in skipping option";
				showError(errmsg);
				return;
			} else if (split_last_nos > part_nos) {
				split_last_nos = part_nos;
			}

			/*
			 * splitting only last some parts means skipping other parts from
			 * first
			 */
			skip_first_nos = part_nos - split_last_nos;
		}

		/*
		 * start process for splitting files in a separate thread
		 */
		split_process = new FileSplitProcess(this, src_file, des_folder,
				part_size, part_nos, skip_first_nos, skip_last_nos);

		Thread process_thread = new Thread(split_process,
				"File Splitting Process Thread");
		process_thread.start();

		btn_split.setEnabled(false);
		btn_cancel.setEnabled(true);
		btn_srcbrowse.setEnabled(false);
		btn_desbrowse.setEnabled(false);
	}

	@Override
	public boolean stopProcess() {
		// if process running
		if (split_process != null) {

			// show confirmation dialog whether to stop process or not
			int option = JOptionPane.showConfirmDialog(this,
					"Do you want to stop splitting ?",
					FileSplitterFrame.APP_TITLE, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (option == JOptionPane.CANCEL_OPTION) {
				return false;
			}

			// if ok and process running then stop it
			if (split_process != null) {
				split_process.forceStop();
			}

			reset();
		}
		return true;
	}

	@Override
	public void updateStatus(Status cur_status) {
		/*
		 * if process running then update the status of the process in the gui
		 */
		if (split_process != null) {
			// set progress bar value
			pb_split_status.setValue(cur_status.getProgressValue());

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
	public void completed(Object[] result) {

		// result is not needed in this case

		// show confirmation message
		String msg = "File splitting completed";
		JOptionPane.showMessageDialog(this, msg, FileSplitterFrame.APP_TITLE,
				JOptionPane.INFORMATION_MESSAGE);
		reset();
	}

	@Override
	public void showError(String errmsg) {
		// show error message
		JOptionPane.showMessageDialog(this, errmsg, FileSplitterFrame.APP_TITLE
				+ " - Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == rb_spiltbysize || e.getSource() == rb_spiltbypart) {
			// first option is changed
			firstOptionSelected();
		} else if (e.getSource() == rb_spiltall
				|| e.getSource() == rb_spiltfirst
				|| e.getSource() == rb_spiltlast) {
			// second option is changed
			secondOptionSelected();
		} else if (e.getSource() == btn_srcbrowse) {
			// browse for source file
			src_file = browseForFile(true);
			if (src_file != null) {

				// check whether selected file exists
				if (!src_file.exists()) {
					showError("File not exists");
					src_file = null;
					tf_srcpath.setText("");
					btn_split.setEnabled(false);
					lbl_filesize.setVisible(false);
					lbl_outputfile.setVisible(false);
					return;
				}

				tf_srcpath.setText(src_file.getAbsolutePath());

				// show file size
				String str_filesize = "File Size : "
						+ FileOperations.getFileSizeStr(src_file.length());
				lbl_filesize.setText(str_filesize);
				lbl_filesize.setVisible(true);

				String output_status = "Output files format : ";
				output_status += src_file.getName() + "."
						+ FileProcess.SPLIT_FILE_EXT + "1" + " , ...";
				lbl_outputfile.setText(output_status);
				lbl_outputfile.setVisible(true);

				if (des_folder != null) {
					btn_split.setEnabled(true);
				}
				// enableOptions(true);
			}
		} else if (e.getSource() == btn_desbrowse) {
			des_folder = browseForFile(false);
			if (des_folder != null) {

				// checking whether folder exists
				if (!des_folder.exists() || !des_folder.isDirectory()) {
					showError("Folder not exists");
					des_folder = null;
					tf_despath.setText("");
					btn_split.setEnabled(false);
					return;
				}

				tf_despath.setText(des_folder.getAbsolutePath());
				if (src_file != null) {
					btn_split.setEnabled(true);
				}
			}
		} else if (e.getSource() == btn_split) {
			// start splitting process
			startProcess();
		} else if (e.getSource() == btn_cancel) {
			// stop process
			stopProcess();
		}
	}
}
