/*
 * FileOperations.java
 * A part of JFSplit core.
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

package jfsplit.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.channels.FileChannel;

import java.text.DecimalFormat;

/**
 * This class defines some common file operations so that it can be used in
 * other processes
 */
public class FileOperations {

	/**
	 * File copying is done not in one step but in a number of steps so as to
	 * monitor the progress of the copying opration and on each step this much
	 * amount(CHUNK_SIZE) of file is copied from source file to destination file
	 */
	public static long CHUNK_SIZE = 1048576; // 1 MB

	// FileProcess which uses this
	private FileProcess file_process;
	// For stopping an operation
	private boolean force_stop_operation;

	/**
	 * Creates a FileOperation object for performing some operations
	 * 
	 * @param file_process
	 *            <code>FileProcess</code> which uses this.
	 */
	public FileOperations(FileProcess file_process) {
		this.file_process = file_process;
		force_stop_operation = false;
	}

	/**
	 * Stop the executing operation
	 */
	public void forceStopOperation() {
		force_stop_operation = true;
	}

	/**
	 * Converts file size in bytes to its highest unit
	 * 
	 * @param file_size
	 *            File size in Bytes as <code>double</code>
	 * @return <code>String</code> which indicates file size with highest unit
	 */
	public static String getFileSizeStr(double file_size) {

		String unit = " Bytes";
		if ((file_size / 1024) >= 1) {
			file_size /= 1024;
			unit = " KB";
		}

		if ((file_size / 1024) >= 1) {
			file_size /= 1024;
			unit = " MB";
		}

		if ((file_size / 1024) >= 1) {
			file_size /= 1024;
			unit = " GB";
		}

		DecimalFormat two_dform = new DecimalFormat("#.##");
		return String.valueOf(two_dform.format(file_size) + unit);
	}

	/**
	 * File copy operation from source file to destination file
	 * 
	 * @param src_file
	 *            Source file as <code>File</code>
	 * @param src_file_pos
	 *            Starting position of the source file from which the copying
	 *            begins
	 * @param src_file_len
	 *            Number of bytes that should be copied from source to
	 *            destination
	 * @param dest_file
	 *            Destination file as <code>File</code>
	 * @throws IOException
	 */
	public void copyFile(File src_file, long src_file_pos, long src_file_len,
			File dest_file) throws IOException {
		// if destination file not exists then create it
		if (!dest_file.exists()) {
			dest_file.createNewFile();
		}

		FileChannel src_channel = null;
		FileChannel des_channel = null;

		try {
			// create necessary file channels
			src_channel = new FileInputStream(src_file).getChannel();
			// Content is appended to destination file
			des_channel = new FileOutputStream(dest_file, true).getChannel();

			// calculate source file size up to the current position
			long filesize_upto_pos = src_file_pos + src_file_len;

			while (src_file_pos < filesize_upto_pos) {

				// for stopping operation while execution
				if (force_stop_operation) {
					break;
				}

				/*
				 * determine chunk size if remaining is less than chunk size
				 * then remaining bytes are taken as chunk size
				 */
				long chunk_size = ((src_file_pos + CHUNK_SIZE) > filesize_upto_pos) ? filesize_upto_pos
						- src_file_pos
						: CHUNK_SIZE;
				// begin transfer
				src_file_pos += src_channel.transferTo(src_file_pos,
						chunk_size, des_channel);
				// update file status
				file_process.updateFileStatus(src_file_pos);
			}
		} finally {
			// close file channels
			if (src_channel != null) {
				src_channel.close();
			}
			if (des_channel != null) {
				des_channel.close();
			}
		}
	}
}
