/*******************************************************************************
 *                                                                              *
 * This file is part of IfcOpenShell.                                           *
 *                                                                              *
 * IfcOpenShell is free software: you can redistribute it and/or modify         *
 * it under the terms of the Lesser GNU General Public License as published by  *
 * the Free Software Foundation, either version 3.0 of the License, or          *
 * (at your option) any later version.                                          *
 *                                                                              *
 * IfcOpenShell is distributed in the hope that it will be useful,              *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of               *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                 *
 * Lesser GNU General Public License for more details.                          *
 *                                                                              *
 * You should have received a copy of the Lesser GNU General Public License     *
 * along with this program. If not, see <http://www.gnu.org/licenses/>.         *
 *                                                                              *
 ********************************************************************************/

/*******************************************************************************
 *                                                                              *
 * This class ensures that a valid binary is available for the platform the     *
 * code is running on.                                                          *
 *                                                                              *
 ********************************************************************************/

package blueprints.wrapper.ifcopenshell;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blueprints.interfaces.ifcconverter.RenderEngine;
import blueprints.interfaces.ifcconverter.exception.RenderEngineException;
import blueprints.wrapper.ifcopenshell.utils.ResourceUtils;

public class IfcOpenShellEnginePlugin {
	private static final String IFC_OPEN_SHELL_ENGINE_PLUGIN_FILENAME = "IfcOpenShellEnginePlugin.filename";
	private static final String WINDOWS_OS = "windows";
	private static final String LINUX_OS = "linux";
	private static final String tempDirPrefix = "IfcGeomServer_";
	private static final String WINDOWS_IFC_GEOM_SERVER_EXE = "IfcGeomServer.exe";
	private static final String LINUX_IFC_GEOM_SERVER_EXE = "IfcGeomServer";

	private static final Logger LOGGER = LoggerFactory.getLogger(IfcOpenShellEnginePlugin.class);

	private boolean initialized = false;
	private String filename;

	public RenderEngine createRenderEngine() throws RenderEngineException {
		try {
			return new IfcOpenShellEngine(filename);
		} catch (IOException e) {
			throw new RenderEngineException(e);
		}
	}

	public void init() throws RenderEngineException {
		final String os = System.getProperty("os.name").toLowerCase();
		final String executableName;
		final String operatingSystem;

		Preferences preferences = Preferences.systemNodeForPackage(IfcOpenShellEnginePlugin.class);
		filename = preferences.get(IFC_OPEN_SHELL_ENGINE_PLUGIN_FILENAME, null);
		File exeFile;

		if (filename == null || !new File(filename).exists()) {
			try {

				if (SystemUtils.IS_OS_WINDOWS) {
					operatingSystem = WINDOWS_OS;
					executableName = WINDOWS_IFC_GEOM_SERVER_EXE;
				} else if (SystemUtils.IS_OS_LINUX) {
					operatingSystem = LINUX_OS;
					executableName = LINUX_IFC_GEOM_SERVER_EXE;
				} else {
					throw new RenderEngineException(
							String.format("IfcOpenShell is not available on the %s platorm", os));
				}

				final String bitness = SystemUtils.OS_ARCH.toLowerCase();
				final String exePathFile = String.format("exe/%s/%s/%s", bitness, operatingSystem, executableName);
				final String exeResourcePath = String.format("/%s", exePathFile);

				Path createTempDirectory = Files.createTempDirectory(tempDirPrefix);

				Path exePath = createTempDirectory.resolve(exePathFile);
				exeFile = exePath.toFile();

				if (!exeFile.exists()) {
					ResourceUtils resourceUtils = new ResourceUtils();
					resourceUtils.saveResource(exePath, exeResourcePath, false);
				}

				try {
					Files.setPosixFilePermissions(exePath, Collections.singleton(PosixFilePermission.OWNER_EXECUTE));
				} catch (Exception e) {
					// Ignore.. permission bit tested below
				}
				filename = exeFile.toString();
				preferences.put(IFC_OPEN_SHELL_ENGINE_PLUGIN_FILENAME, filename);
			} catch (Exception e) {
				throw new RenderEngineException(e);
			}
		} else {
			exeFile = new File(filename);
		}

		initialized = exeFile.canExecute();

		if (!initialized) {
			throw new RenderEngineException(String.format("No executable found for the %s platorm", os));
		} else {
			LOGGER.info("Using " + exeFile.toString());
		}
	}

}
