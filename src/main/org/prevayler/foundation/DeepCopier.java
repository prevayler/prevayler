package org.prevayler.foundation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.prevayler.implementation.snapshot.SnapshotManager;

public class DeepCopier {

	public static Object deepCopy(Object original, SnapshotManager snapshotManager, String errorMessage) {  //TODO Receive a generic "Serializer" instead of the SnapshotManager.
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			snapshotManager.writeSnapshot(original, out);
			return snapshotManager.readSnapshot(new ByteArrayInputStream(out.toByteArray()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(errorMessage);
		}
	}
	
}
