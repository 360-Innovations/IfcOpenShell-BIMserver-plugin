package blueprints.interfaces.ifcconverter.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blueprints.interfaces.ifcconverter.RenderEngine;
import blueprints.interfaces.ifcconverter.RenderEngineInstance;
import blueprints.interfaces.ifcconverter.RenderEngineModel;
import blueprints.interfaces.ifcconverter.exception.RenderEngineException;
import blueprints.wrapper.ifcopenshell.IfcOpenShellEnginePlugin;

public class Test {

	Logger logger = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) {
		new Test().start();
	}

	private void start() {
		try {
			IfcOpenShellEnginePlugin ifcOpenShellEnginePlugin = new IfcOpenShellEnginePlugin();
			ifcOpenShellEnginePlugin.init();
			RenderEngine renderEngine = ifcOpenShellEnginePlugin.createRenderEngine();
			renderEngine.init();
			RenderEngineModel model = renderEngine
					.openModel(new FileInputStream(new File("C:/Users/jddaigle/Desktop/Blueprint/simple_revit2.ifc")));
			model.generateGeneralGeometry();
			Collection<RenderEngineInstance> listInstances = model.listInstances();

			for (RenderEngineInstance renderEngineInstance : listInstances) {
				logger.info(renderEngineInstance.getName());
				logger.info(renderEngineInstance.getCheckSum());
				logger.info(String.valueOf(renderEngineInstance.getId()));
			}

			model.close();
			renderEngine.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RenderEngineException e) {
			e.printStackTrace();
		}
	}
}
