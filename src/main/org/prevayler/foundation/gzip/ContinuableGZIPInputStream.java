package org.prevayler.foundation.gzip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.zip.GZIPInputStream;

public class ContinuableGZIPInputStream extends GZIPInputStream {

	public ContinuableGZIPInputStream(InputStream stream) throws IOException {
		super(stream);
	}

	public InputStream remainingInput() {
		int afterTrailer = inf.getRemaining() - 8;
		if (afterTrailer > 0) {
			return new SequenceInputStream(new ByteArrayInputStream(buf, len - afterTrailer, afterTrailer), in);
		} else {
			return in;
		}
	}

}
