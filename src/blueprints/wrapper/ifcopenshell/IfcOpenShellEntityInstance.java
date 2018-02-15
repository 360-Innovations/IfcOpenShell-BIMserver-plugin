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

package blueprints.wrapper.ifcopenshell;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;

import blueprints.interfaces.ifcconverter.RenderEngineGeometry;
import blueprints.interfaces.ifcconverter.RenderEngineInstance;
import blueprints.interfaces.ifcconverter.exception.RenderEngineException;

public class IfcOpenShellEntityInstance implements RenderEngineInstance {
	private IfcGeomServerClientEntity entity;
	private RenderEngineGeometry renderEngineGeometry;
	private String checkSum;
	private String json;

	public IfcOpenShellEntityInstance(IfcGeomServerClientEntity entity) throws RenderEngineException {
		this.entity = entity;
	}

	@Override
	public double[] getTransformationMatrix() {

		return entity.getMatrix();
	}

	@Override
	public RenderEngineGeometry getGeometry() throws RenderEngineException {
		if (renderEngineGeometry == null) {
			renderEngineGeometry = new RenderEngineGeometry(entity.getIndices(), entity.getPositions(),
					entity.getNormals(), entity.getColors(), entity.getMaterialIndices());
		}

		return renderEngineGeometry;
	}

	@Override
	public String getName() throws RenderEngineException {
		return entity.getName();
	}

	@Override
	public int getId() throws RenderEngineException {
		return entity.getId();
	}

	@Override
	public String getCheckSum() throws RenderEngineException {
		if (checkSum == null) {
			checkSum = DigestUtils.md5Hex(getJSon());
		}

		return checkSum;
	}

	@Override
	public String getJSon() throws RenderEngineException {
		if (json == null) {
			Gson gson = new Gson();
			json = gson.toJson(getGeometry());
		}

		return json;
	}

}
