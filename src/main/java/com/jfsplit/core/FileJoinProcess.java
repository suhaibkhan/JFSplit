/*
 * FileJoinProcess.java
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
 * <code>FileJoinProcess</code> class is an implementation of the
 * <code>FileProcess</code> interface which is used for the process of joining
 * multiple source files into a single file.
 */
public class FileJoinProcess implements FileProcess {

	// File array of source files
	private File[] source_files;
	// destination file
	private File dest_file;

	// Which file is being processed.
	private int cur_file;

	// Process startup time in nano seconds.
	private long startup_time;
	// Size of target file.
	private long desfile_size;
	// Current position of the target file being processed.
	private long desfile_curpos;
	// Total size of source files that completed processing
	private long completed_src_files_size;

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
	 * Creates a file join process
	 * 
	 * @param process_caller
	 *            <code>ProcessCaller</code> which invoked this process
	 * @param source_files
	 *            <code>File</code> array of source files
	 * @param dest_file
	 *            destination file as <code>File</code>
	 */
	public FileJoinProcess(ProcessCaller process_caller, File[] source_files,
			File dest_file) {
		this.process_caller = process_caller;
		this.source_files = source_files;
		this.dest_file = dest_file;

		file_operations = new FileOperations(this);
		status = new Status();

		force_stop = false;

		// estimate the target size from the source files
		findDestFileSize();
	}

	// For calculating the target size from the source files
	private void findDestFileSize() {
		desfile_size = 0;
		for (int i = 0; i < source_files.length; i++) {
			// add length of each souce file
			if (source_files[i].exists()) {
				desfile_size += source_files[i].length();
			} else {
				String errmsg = "Error during joining.\n"
						+ source_files[i].getName() + " not found";
				process_caller.showError(errmsg);
				break;
			}
		}
	}

	/**
	 * Performs the file joining operation
	 * 
	 * @throws IOException
	 */
	public void joinFiles() throws IOException {

		desfile_curpos = 0;
		completed_src_files_size = 0;

		// if specified target file exits replace it with new joined file
		if (dest_file.exists()) {
			dest_file.delete();
		}

		// copy and append each source file to target file
		for (int i = 0; i < source_files.length; i++) {

			// for stopping operation while execution
			if (force_stop) {
				break;
			}
			// for File status
			cur_file = i + 1;

			// check whether source files exists
			if (!source_files[i].exists()) {
				String errmsg = "Error during joining.\n"
						+ source_files[i].getName() + " not found";
				process_caller.showError(errmsg);
				break;
			}

			long srcfile_size = source_files[i].length();
			// copy from source file to destination file
			file_operations.copyFile(source_files[i], 0, srcfile_size,
					dest_file);
			// update completed_src_files_size
			completed_src_files_size += srcfile_size;
		}
	}

	@Override
	public void run() {
		// record starting time
		startup_time = System.nanoTime();
		try {
			// do operation
			joinFiles();
		} catch (IOException e) {
			String errmsg = "Error during joining operation";
			process_caller.showError(errmsg);
		}
		if (!force_stop) {
			// inform ProcessCaller that operation is completed
			process_caller.completed(null);
		}
	}

	@Override
	public void updateFileStatus(long file_curpos) {

		/*
		 * calculate target file position from source file position passed from
		 * copy operation and completed_src_files_size (how much completed
		 * early)
		 */
		desfile_curpos = completed_src_files_size + file_curpos;

		// calculated time elapsed and remaining in nano seconds
		long elapsed_time = System.nanoTime() - startup_time;
		long time_for_byte = elapsed_time / desfile_curpos;
		long remaining_time = time_for_byte * (desfile_size - desfile_curpos);

		// converting time elapsed in nano seconds to minutes : seconds
		long elapsed_time_insecs = (long) (elapsed_time * 1E-9);
		long elap_time_sec = elapsed_time_insecs % 60;
		long elap_time_min = elapsed_time_insecs / 60;

		// converting time remaining in nano seconds to minutes : seconds
		long remaining_time_insecs = (long) (remaining_time * 1E-9);
		long rem_time_sec = remaining_time_insecs % 60;
		long rem_time_min = remaining_time_insecs / 60;

		// calculating progress percentage
		int progress_value = (int) (((double) desfile_curpos / desfile_size) * 100.0);

		// set status
		status.setProgressValue(progress_value);
		status.setElapTimeStatus("Time Elapsed " + elap_time_min + " : "
				+ elap_time_sec);
		status.setRemTimeStatus("Estimated Time Remaining " + rem_time_min
				+ " : " + rem_time_sec);
		status.setFileStatus("Copying file " + cur_file + " of "
				+ source_files.length);
		status.setSizeStatus(desfile_curpos + " / " + desfile_size + " Bytes");

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
