package hemera.core.structure.hab.debug;

import hemera.core.structure.hab.ResourceNode;
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
 * <code>DebugResourceNode</code> defines the extension
 * of a resource node that provides debugging support.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.4
 */
public class DebugResourceNode extends ResourceNode {
	/**
	 * The <code>String</code> optional shared resource
	 * configuration file path.
	 */
	final String sharedConfigPath;

	/**
	 * Constructor of <code>DebugResourceNode</code>.
	 * @param resourceNode The <code>ResourceNode</code>.
	 * @param sharedConfigPath The <code>String</code>
	 * optional shared resource configuration file path.
	 */
	public DebugResourceNode(final ResourceNode resourceNode, final String sharedConfigPath) {
		super(resourceNode.jarLocation, resourceNode.classname, resourceNode.configLocation,
				resourceNode.resourcesDir, resourceNode.sharedResourcesDir);
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
	public File processConfig(final String tempDir) throws IOException, SAXException, ParserConfigurationException, TransformerException {
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
