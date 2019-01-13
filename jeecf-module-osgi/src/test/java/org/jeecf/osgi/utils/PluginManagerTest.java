package org.jeecf.osgi.utils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.jeecf.osgi.enums.BoundleEnum;
import org.jeecf.osgi.model.BoundleContext;
import org.jeecf.osgi.model.PluginRequest;
import org.jeecf.osgi.plugin.Plugin;

public class PluginManagerTest {
	
	public static void main(String[] args) {
		PluginManager pluginManager = new PluginManager();
		try {
			pluginManager.install(new URL[]{new URL("file:/Users/jianyiming/eclipse-workspace/jeecf-osgi-java/target/jeecf-osgi-java-0.0.1-SNAPSHOT.jar")}, BoundleEnum.GEN_HANDLER_PLUGIN_BOUNDLE, true);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Plugin> plugins = pluginManager.getInstances(BoundleEnum.GEN_HANDLER_PLUGIN_BOUNDLE);
		plugins.forEach(plugin->{
			BoundleContext context = pluginManager.getContext(BoundleEnum.GEN_HANDLER_PLUGIN_BOUNDLE);
			PluginRequest request = new PluginRequest();
			request.setAttribute("test", "11111");
			plugin.process(request);
		});
//		try {
//			PluginManager.uninstall(PluginEnum.GEN_HANDLER_PLUGIN,new URL("file:/Users/jianyiming/eclipse-workspace/jeecf-osgi-java/target/jeecf-osgi-java-0.0.1-SNAPSHOT.jar"));
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
