package org.prevayler.implementation.snapshot;
import java.io.*;

import javax.xml.transform.stream.*;

import com.skaringa.javaxml.*;

/**
 * @author Alexandre Nodari
 *
 * Writes and reads snapshots to/from XML files.
 * @see org.prevayler.implementation.SnapshotManager
 */
public class XmlSnapshotManager extends SnapshotManager {


	public XmlSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		super(newPrevalentSystem, snapshotDirectoryName);
	}


	/**
	 * @see org.prevayler.implementation.SnapshotManager#readSnapshot(InputStream)
	 */
	public Object readSnapshot(InputStream in) throws IOException {
		StreamSource source = new StreamSource(in);
		try {
			return transformer().deserialize(source);
		} catch (DeserializerException de) {
			throw new IOException("Unable to deserialize with Skaringa: " + de.getMessage());
		} finally {
			source.getInputStream().close();
		}
	}


	/**
	 * @see org.prevayler.implementation.SnapshotManager#suffix()
	 */
	protected String suffix() {
		return "xml";
	}


	/**
	 * @see org.prevayler.implementation.SnapshotManager#writeSnapshot(Object, OutputStream)
	 */
	public void writeSnapshot(Object prevalentSystem, OutputStream out) throws IOException {
		StreamResult result = new StreamResult(out);
		try {
			transformer().serialize(prevalentSystem, result);
		} catch (SerializerException se) {
			throw new IOException("Unable to serialize with Skaringa: " + se.getMessage());
		} finally {
			result.getOutputStream().close();
		}
	}


	private ObjectTransformer transformer() throws IOException {
		try {
			return ObjectTransformerFactory.getInstance().getImplementation();
//			Other options you can try:
//			trans.setProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
//			trans.setProperty(javax.xml.transform.OutputKeys.ENCODING, "ISO-8859-1");
//			trans.setProperty(com.skaringa.javaxml.PropertyKeys.OMIT_XSI_TYPE, "true");
		}
		catch (NoImplementationException nie) {
			throw new IOException("Unable to start Skaringa: " + nie.getMessage());
		}
	}
}
