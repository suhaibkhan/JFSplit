/*
 * ExtensionFileFilter.java
 * Part of JFSplit GUI
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

import java.io.File;

import javax.swing.filechooser.FileFilter;

import jfsplit.core.FileProcess;

/**
 * This class is a FileFilter implementation used to filter files in a
 * JFileChooser dialog with respect to provided extensions. Only files with
 * extensions that are specified in this filter can be selected with a
 * JFileChooser dialog which uses this filter.
 */
public class ExtensionFileFilter extends FileFilter {

	private String description;
	// array containing extensions to be selected
	private String[] extensions;

	public ExtensionFileFilter(String description, String extension) {
		this(description, new String[] { extension });
	}

	public ExtensionFileFilter(String description, String extensions[]) {
		if (description == null) {
			this.description = extensions[0] + "{ " + extensions.length + "} ";
		} else {
			this.description = description;
		}
		this.extensions = extensions.clone();
		toLower(this.extensions);
	}

	/*
	 * converts all elements in String array to lower case
	 */
	private void toLower(String array[]) {
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i].toLowerCase();
		}
	}

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			String path = file.getAbsolutePath().toLowerCase();
			for (int i = 0; i < extensions.length; i++) {
				String ext = extensions[i];
				if (ext.equals(FileProcess.SPLIT_FILE_EXT + "1...")) {
					/*
					 * support for split file extensions .jfs1, .jfs2, ... by
					 * specifying jfs1... in the file filter
					 */
					if (path.matches(".*\\." + FileProcess.SPLIT_FILE_EXT
							+ "[\\d]*")) {
						return true;
					}
				} else if (ext.equals("001...")) {
					/*
					 * support for extensions .001, .002, ... by specifying
					 * 001... in the file filter
					 */
					if (path.matches(".*\\.[\\d]{3}")) {
						return true;
					}
				} else {
					// support for other specified extensions
					if ((path.endsWith(ext) && (path.charAt(path.length()
							- ext.length() - 1)) == '.')) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
