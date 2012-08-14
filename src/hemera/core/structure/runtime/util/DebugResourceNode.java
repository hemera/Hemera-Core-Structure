package hemera.core.structure.runtime.util;

import hemera.core.utility.FileUtils;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <code>DebugResourceNode</code> defines the immutable
 * extension of a resource node in debugging mode.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
class DebugResourceNode extends ResourceNode {
	/**
	 * The <code>String</code> optional shared resource
	 * configuration file path.
	 */
	final String sharedConfigPath;

	/**
	 * Constructor of <code>JarResourceNode</code>.
	 * @param classname The <code>String</code>
	 * fully qualified class name of the resource
	 * implementation.
	 * @param configLocation The <code>String</code>
	 * resource configuration file location. This is
	 * an optional value and can be <code>null</code>.
	 * @param resourcesDir The <code>String</code>
	 * optional resources directory.
	 * @param sharedResourcesDir The optional shared
	 * resources directory <code>String</code>.
	 * @param sharedConfigPath The <code>String</code>
	 * optional shared resource configuration file path.
	 */
	DebugResourceNode(final String classname, final String configLocation, final String resourcesDir,
			final String sharedResourcesDir, final String sharedConfigPath) {
		super(classname, configLocation, resourcesDir, sharedResourcesDir);
		this.sharedConfigPath = sharedConfigPath;
	}
	
	/**
	 * Process the resource configuration by appending
	 * the application shared configuration content to
	 * the resource's local configuration content and
	 * export the file into the given temporary directory.
	 * @param tempDir The <code>String</code> path of
	 * the temporary directory.
	 * @return The <code>File</code> of the processed
	 * appended configuration. <code>null</code> if
	 * the resource does not have a configuration and the
	 * given shared configuration is <code>null</code>.
	 * @throws IOException If reading file failed.
	 * @throws SAXException If parsing file failed.
	 * @throws ParserConfigurationException If parsing
	 * file failed.
	 * @throws TransformerException If writing the
	 * XML document failed.
	 */
	File processConfig(final String tempDir) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		if (this.sharedConfigPath == null) {
			if (this.configLocation == null) return null;
			else return new File(this.configLocation);
		} else {
			// Read in shared configuration.
			final File sharedFile = new File(this.sharedConfigPath);
			final Document sharedDoc = FileUtils.instance.readAsDocument(sharedFile);
			// Retrieve all shared configuration children tags.
			final NodeList sharedChildren = sharedDoc.getDocumentElement().getChildNodes();
			// Read in local configuration.
			Document localDoc = null;
			if (this.configLocation == null) {
				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				final DocumentBuilder docBuilder = factory.newDocumentBuilder();
				localDoc = docBuilder.newDocument();
				// Create a root tag.
				final Element root = localDoc.createElement(this.classname);
				localDoc.appendChild(root);
			} else {
				localDoc = FileUtils.instance.readAsDocument(new File(this.configLocation));
			}
			// Append all shared children to local document root tag.
			final int size = sharedChildren.getLength();
			final Node localRoot = localDoc.getFirstChild();
			for (int i = 0; i < size; i++) {
				localRoot.appendChild(localDoc.importNode(sharedChildren.item(i), true));
			}
			// Write the appended local document to a temporary file.
			String fileName = null;
			if (this.configLocation == null) {
				fileName = sharedFile.getName();
			} else {
				fileName = new File(this.configLocation).getName();
			}
			return FileUtils.instance.writeDocument(localDoc, tempDir+fileName);
		}
	}
}
