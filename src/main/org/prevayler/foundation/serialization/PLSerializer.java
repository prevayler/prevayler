package org.prevayler.foundation.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import alt.dev.pl.PLObjectInput;
import alt.dev.pl.PLObjectOutput;

/**
 * Writes and reads objects using a <a href="http://alt.textdrive.com/pl/3/pl-the-format">Property List (plist) format</a>. This serializer can be used for snapshots, journals or both.
 *
 * <p>This implementation requires the <a href="http://alt.textdrive.com/pl/">PL</a>
 * serialization framework which provides for Java object plist serialization.</p>
 *
 * <p>Note that PL has some dependencies of its own.  It requires j2sdk1.4+</p>
 *
 * @author jkjome
 */
public class PLSerializer implements Serializer {

    public PLSerializer() {}

    public void writeObject(OutputStream stream, Object object) throws IOException {
        new PLObjectOutput(stream).writeObject(object);
    }

    public Object readObject(InputStream stream) throws IOException, ClassNotFoundException {
        return new PLObjectInput(stream).readObject();
    }

}
