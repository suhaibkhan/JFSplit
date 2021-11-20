/*
 * ProcessCaller.java
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

package com.jfsplit.core;

/**
 * The <code>ProcessCaller</code> interface specifies the methods an object
 * invoking a <code>FileProcess</code> should be defined.
 * 
 * @see FileProcess
 */
public interface ProcessCaller {

	/**
	 * The method by which <code>FileProcess</code> informs the current status
	 * of the running process to the <code>ProcessCaller</code>.
	 * 
	 * @param cur_status
	 *            A <code>Status</code> which provides the current status of a
	 *            <code>FileProcess</code>.
	 * 
	 * @see <code>Status</code>
	 */
	public void updateStatus(Status cur_status);

	/**
	 * The method is used to start the process associated with the
	 * <code>ProcessCaller</code>.
	 */
	public void startProcess();

	/**
	 * The method is used to stop the running process associated with the
	 * <code>ProcessCaller</code>.
	 */
	public boolean stopProcess();

	/**
	 * This method is used by <code>FileProcess</code> to inform
	 * <code>ProcessCaller</code> that a process is completed.
	 * 
	 * @param result
	 *            An <code>Object</code> array used by <code>FileProcess</code>
	 *            to pass needed informations to the <code>ProcessCaller</code>.
	 */
	public void completed(Object[] result);

	/**
	 * Used to notify the <code>ProcessCaller</code> that an error occurred.
	 * 
	 * @param errmsg
	 *            Error message as <code>String</code>
	 */
	public void showError(String errmsg);
}
