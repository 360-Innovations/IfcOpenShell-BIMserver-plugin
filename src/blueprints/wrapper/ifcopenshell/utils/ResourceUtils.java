package blueprints.wrapper.ifcopenshell.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/******************************************************************************
 * Copyright (C) 2009-2017  BIMserver.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see {@literal<http://www.gnu.org/licenses/>}.
 *****************************************************************************/

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class ResourceUtils {
	
	public File saveResource(Path outputPath, String resourcePath, boolean replace) throws IOException {
		
		File folder = outputPath.getParent().toFile();
		if (!folder.exists()) {
			FileUtils.forceMkdir(folder);
		}

		File file = outputPath.toFile();

		if (replace || !file.exists()) {
			InputStream resource = null;
			OutputStream outputStream = null;
			try {
				resource = openResourceStream(resourcePath);
				outputStream = new BufferedOutputStream(new FileOutputStream(outputPath.toString()));
				IOUtils.copy(resource, outputStream);
			} finally {
				if (outputStream != null)
					outputStream.close();
				
				if (resource != null)
					resource.close();
			}
		}

		return file;
	}

	private InputStream openResourceStream(String resourcePath) throws FileNotFoundException {
		InputStream resource = this.getClass().getResourceAsStream(resourcePath);
		if (resource == null)
			throw new FileNotFoundException(resourcePath + " (resource not found)");
		return resource;
	}
}
