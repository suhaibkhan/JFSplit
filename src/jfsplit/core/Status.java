/*
 * Status.java
 * A part(Status class) of JFSplit core.
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

/**
 * The <code>Status</code> class is used to pass necessary information from
 * <code>FileProcess</code> to <code>ProcessCaller</code>.
 */
public class Status {

	// String indicating which file is being processed
	private String file_status;
	// String indicating how much of the file is processed
	private String size_status;
	// String indicating time elapsed for the process
	private String elaptime_status;
	// String indicating estimated remaining time needed for the process
	private String rem_timestatus;
	// Integer indicating current progress of the process out of 100
	private int progress_value;

	/**
	 * Sets blank (nothing) for all status
	 */
	public Status() {
		file_status = "";
		size_status = "";
		elaptime_status = "";
		rem_timestatus = "";
		progress_value = 0;
	}

	/*
	 * Set Methods for various status
	 */

	/**
	 * Set progress of process out of 100
	 * 
	 * @param progress_value
	 *            Integer indicating progress value (should be <= 100)
	 */
	public void setProgressValue(int progress_value) {
		this.progress_value = progress_value;
	}

	/**
	 * Set file status (which file is being processed)
	 * 
	 * @param file_status
	 *            String indicating file status
	 */
	public void setFileStatus(String file_status) {
		this.file_status = file_status;
	}

	/**
	 * Set size status (how much of the file is processed)
	 * 
	 * @param size_status
	 *            String indicating size status
	 */
	public void setSizeStatus(String size_status) {
		this.size_status = size_status;
	}

	/**
	 * Set elapsed time status (time elapsed for the process)
	 * 
	 * @param elaptime_status
	 *            String indicating elapsed time status
	 */
	public void setElapTimeStatus(String elaptime_status) {
		this.elaptime_status = elaptime_status;
	}

	/**
	 * Set remaining time status (estimated remaining time for the process)
	 * 
	 * @param rem_timestatus
	 *            String indicating remaining time status
	 */
	public void setRemTimeStatus(String rem_timestatus) {
		this.rem_timestatus = rem_timestatus;
	}

	/*
	 * Get Methods for various status
	 */

	/**
	 * Get progress value (progress of process out of 100)
	 * 
	 * @return Progress value as <code>int</code>
	 */
	public int getProgressValue() {
		return progress_value;
	}

	/**
	 * Get file status (which file is being processed)
	 * 
	 * @return File status as </code>String<code>
	 */
	public String getFileStatus() {
		return file_status;
	}

	/**
	 * Get size status (how much of the file is processed)
	 * 
	 * @return Size status as <code>String</code>
	 */
	public String getSizeStatus() {
		return size_status;
	}

	/**
	 * Get elapsed time status (time elapsed for the process)
	 * 
	 * @return Elapsed time status as <code>String</code>
	 */
	public String getElapTimeStatus() {
		return elaptime_status;
	}

	/**
	 * Get remaining time status (estimated remaining time for the process)
	 * 
	 * @return Remaining time status as <code>String</code>
	 */
	public String getRemTimeStatus() {
		return rem_timestatus;
	}

}
