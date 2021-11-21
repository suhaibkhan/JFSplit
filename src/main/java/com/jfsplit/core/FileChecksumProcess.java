/*
 * FileChecksumProcess.java
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
 * 
 */

package com.jfsplit.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * <code>FileChecksumProcess</code> class is an implementation of the
 * <code>FileProcess</code> interface which is used for the process of
 * calculating checksum of a file.
 */
public class FileChecksumProcess implements FileProcess {

	// source file
	private File source_file;

	// Process startup time in nano seconds.
	private long startup_time;
	// Current position of the source file being processed.
	private long srcfile_curpos;
	// Size of source file.
	private long srcfile_size;
	// calculated checksum is stored in this long variable
	private long checksum;

	// ProcessCaller which invoked this process.
	private ProcessCaller process_caller;

	// For passing status of the process.
	private Status status;

	/*
	 * For stopping a process while execution. If true then the operation is
	 * stopped
	 */
	private boolean force_stop;

	/**
	 * Creates a process to calculate checksum
	 * 
	 * @param process_caller
	 *            <code>ProcessCaller</code> which invoked this process
	 * @param source_file
	 *            Source file as <code>File</code>
	 */
	public FileChecksumProcess(ProcessCaller process_caller, File source_file) {
		this.process_caller = process_caller;
		this.source_file = source_file;

		status = new Status();

		force_stop = false;
	}

	/**
	 * Calculates checksum of the source file
	 * 
	 * @throws IOException
	 */
	public void calculateCheksum() throws IOException {

		srcfile_curpos = 0;
		srcfile_size = source_file.length();

		// CheckedInputStream to find CRC32 checksum
		CheckedInputStream checked_istream = new CheckedInputStream(
				new FileInputStream(source_file), new CRC32());

		byte[] buffer = new byte[1024];
		int bytes_read;
		while ((bytes_read = checked_istream.read(buffer)) >= 0) {

			// for stopping operation while execution
			if (force_stop) {
				break;
			}
			srcfile_curpos += bytes_read;
			// update file status
			updateFileStatus(0);
		}

		// get checksum
		checksum = checked_istream.getChecksum().getValue();
	}

	@Override
	public void run() {
		// record starting time
		startup_time = System.nanoTime();
		try {
			// do operation
			calculateCheksum();
		} catch (IOException e) {
			String errmsg = "Error during checksum operation";
			process_caller.showError(errmsg);
		}
		if (!force_stop) {
			// inform ProcessCaller that operation is completed
			// with the calculated checksum
			process_caller.completed(new Long[] { checksum });
		}
	}

	@Override
	public void updateFileStatus(long file_curpos) {
		// file_curpos is not needed in this method

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
		status.setFileStatus("Calculating Checksum");
		status.setSizeStatus(srcfile_curpos + " / " + srcfile_size + " Bytes");

		// pass status to ProcessCaller
		process_caller.updateStatus(status);
	}

	@Override
	public void forceStop() {
		// stop operation
		force_stop = true;
	}

}
