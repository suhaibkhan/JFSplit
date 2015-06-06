/*
 * FileProcess.java
 * An interface in JFSplit core.
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

package jfsplit.core;

/**
 * The <code>FileProcess</code> interface specifies the methods a
 * <code>ProcessCaller</code> will use to interact with a file related process
 * which runs as separate thread.
 * 
 * <pre>
 * FileProcess split_process = new new FileChecksumProcess(process_caller, src_file);
 * </pre>
 * 
 * @see ProcessCaller
 * 
 */
public interface FileProcess extends Runnable {
	/**
	 * Extension (File Format) of a split file
	 */
	public String SPLIT_FILE_EXT = "jfs";

	/**
	 * This method is used to update file progress from an external entity (if a
	 * file uses an external entity in a for processing of file).
	 * 
	 * <pre>
	 * file_process.updateFileStatus(src_file_pos);
	 * </pre>
	 * 
	 * This method is manually called whenever a change occurs in file process
	 * progress so it is used to inform <code>ProcessCaller</code> current
	 * status of the <code>FileProcess</code>.
	 * 
	 * @param file_curpos
	 *            Current position of the specified file
	 */
	public void updateFileStatus(long file_curpos);

	/**
	 * This method is used to stop execution of a <code>FileProcess</code>.
	 */
	public void forceStop();
}
