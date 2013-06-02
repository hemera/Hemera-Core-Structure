package hemera.core.structure.hab;

import hemera.core.environment.ham.HAM;
import hemera.core.environment.ham.HAMResource;
import hemera.core.environment.util.UEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>HAB</code> defines the data structure that
 * represents a Hemera Application Bundle.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.4
 */
public class HAB {
	/**
	 * The optional <code>String</code> application
	 * path.
	 */
	public final String applicationPath;
	/**
	 * The <code>Iterable</code> of all the resources
	 * <code>ResourceNode</code> of the application.
	 */
	public final Iterable<ResourceNode> resources;
	
	/**
	 * Constructor of <code>HAB</code>.
	 * @param ham The <code>HAM</code> instance.
	 */
	public HAB(final HAM ham) {
		this.applicationPath = ham.applicationPath;
		this.resources = this.parseResourceNodes(ham);
	}
	
	/**
	 * Parse the HAM file and retrieve the application
	 * resource nodes.
	 * @param ham The <code>HAM</code> instance.
	 * @return The <code>List</code> of all the instances
	 * of <code>ResourceNode</code>.
	 */
	private List<ResourceNode> parseResourceNodes(final HAM ham) {
		final String sharedResourcesDir = (ham.shared==null) ? null : ham.shared.resourcesDir;
		// Parse all resources.
		final String appDir = UEnvironment.instance.getApplicationDir(ham.applicationName);
		final int size = ham.resources.size();
		final List<ResourceNode> list = new ArrayList<ResourceNode>(size);
		for (int i = 0; i < size; i++) {
			final HAMResource hamResource = ham.resources.get(i);
			final StringBuilder jarPath = new StringBuilder();
			jarPath.append(appDir).append(hamResource.classname).append(File.separator);
			jarPath.append(hamResource.classname).append(".jar");
			final ResourceNode resource = new ResourceNode(jarPath.toString(), hamResource.classname, hamResource.configFile,
					hamResource.resourcesDir, sharedResourcesDir);
			list.add(resource);
		}
		return list;
	}
}
