/*
 * FileSplitProcess.java
 * A Runnable FileProcess in JFSplit core.
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

package com.jfsplit.core;

import java.io.File;
import java.io.IOException;

/**
 * <code>FileSplitProcess</code> class is an implementation of the
 * <code>FileProcess</code> interface which is used for the process of splitting
 * files into parts.
 */
public class FileSplitProcess implements FileProcess {

	// source file
	private File source_file;
	// destination folder
	private File dest_folder;

	// Size of each split file (Segment or Part).
	private long part_size;
	// Current position of the source file being processed.
	private long srcfile_curpos;
	// Size of source file.
	private long srcfile_size;
	// Process startup time in nano seconds.
	private long startup_time;

	// Number of split files (Segments or Parts).
	private int part_nos;
	// Number of split files should be skipped from starting.
	private int skip_first_nos;
	// Number of split files should be skipped from last.
	private int skip_last_nos;
	// Which file is being processed.
	private int cur_file;

	// ProcessCaller which invoked this process.
	private ProcessCaller process_caller;

	/*
	 * Used for performing some file operations. Here used to perform file
	 * copying.
	 */
	private FileOperations file_operations;

	// For passing status of the process.
	private Status status;

	/*
	 * For stopping a process while execution. If true then the operation is
	 * stopped
	 */
	private boolean force_stop;

	/**
	 * Creates a file split process
	 * 
	 * @param process_caller
	 *            <code>ProcessCaller</code> which invoked this process
	 * @param source_file
	 *            source file as <code>File</code>
	 * @param dest_folder
	 *            destination folder as <code>File</code>
	 * @param part_size
	 *            Size of each split file
	 * @param part_nos
	 *            Number of split files
	 * @param skip_first_nos
	 *            Number of split files should be skipped from starting
	 * @param skip_last_nos
	 *            Number of split files should be skipped from last
	 */
	public FileSplitProcess(ProcessCaller process_caller, File source_file,
			File dest_folder, long part_size, int part_nos, int skip_first_nos,
			int skip_last_nos) {
		this.process_caller = process_caller;
		this.source_file = source_file;
		this.dest_folder = dest_folder;
		this.part_size = part_size;
		this.part_nos = part_nos;
		this.skip_first_nos = skip_first_nos;
		this.skip_last_nos = skip_last_nos;

		file_operations = new FileOperations(this);
		status = new Status();

		force_stop = false;
	}

	/**
	 * Performs the file splitting operation
	 * 
	 * @throws IOException
	 */
	public void splitFile() throws IOException {

		srcfile_curpos = 0;
		srcfile_size = source_file.length();

		// creates split files one on every loop
		for (int i = 0; srcfile_curpos < srcfile_size; i++) {

			// for stopping operation while execution
			if (force_stop) {
				break;
			}

			// Destination split file is created
			String dest_file_name = source_file.getName() + "."
					+ SPLIT_FILE_EXT + String.valueOf(i + 1);
			File dest_file = new File(dest_folder, dest_file_name);

			if (dest_file.exists()) {
				dest_file.delete();
			}
			// for File status
			cur_file = i + 1;

			// finds how much of the source file should be copied to destination
			// file
			// here it is the size of each split file
			long bytes_tobe_copied = ((srcfile_curpos + part_size) > srcfile_size) ? (srcfile_size - srcfile_curpos)
					: part_size;

			// for skipping split files from starting and last
			// as per skip_first_nos and skip_last_nos
			if ((i < skip_first_nos) || (i >= (part_nos - skip_last_nos))) {
				updateFileStatus(srcfile_curpos + bytes_tobe_copied);
				continue;
			}

			// copy from source file to destination file
			file_operations.copyFile(source_file, srcfile_curpos,
					bytes_tobe_copied, dest_file);
		}
	}

	@Override
	public void run() {
		// record starting time
		startup_time = System.nanoTime();
		try {
			// do operation
			splitFile();
		} catch (IOException e) {
			String errmsg = "Error during splitting operation";
			process_caller.showError(errmsg);
		}
		if (!force_stop) {
			// inform ProcessCaller that operation is completed
			process_caller.completed(null);
		}
	}

	@Override
	public void updateFileStatus(long file_curpos) {
		// store file position passed from copy operation
		srcfile_curpos = file_curpos;

		// calculated time elapsed and remaining in nano seconds
		long elapsed_time = System.nanoTime() - startup_time;
		long time_for_byte = elapsed_time / srcfile_curpos;
		long remaining_time = time_for_byte * (srcfile_size - srcfile_curpos);

		// converting time elapsed in nano seconds to minutes : seconds
		long elapsed_time_insecs = (long) (elapsed_time * 1E-9);
		long elap_time_sec = elapsed_time_insecs % 60;
		long elap_time_min = elapsed_time_insecs / 60;

		// converting time remaining in nano seconds to minutes : seconds
		long remaining_time_insecs = (long) (remaining_time * 1E-9);
		long rem_time_sec = remaining_time_insecs % 60;
		long rem_time_min = remaining_time_insecs / 60;

		// calculating progress percentage
		int progress_value = (int) (((double) srcfile_curpos / srcfile_size) * 100.0);

		// set status
		status.setProgressValue(progress_value);
		status.setElapTimeStatus("Time Elapsed " + elap_time_min + " : "
				+ elap_time_sec);
		status.setRemTimeStatus("Estimated Time Remaining " + rem_time_min
				+ " : " + rem_time_sec);
		status.setFileStatus("Copying file " + cur_file + " of " + part_nos);
		status.setSizeStatus(srcfile_curpos + " / " + srcfile_size + " Bytes");

		// pass status to ProcessCaller
		process_caller.updateStatus(status);
	}

	@Override
	public void forceStop() {
		force_stop = true;
		// stop copy operation
		file_operations.forceStopOperation();
	}
}
