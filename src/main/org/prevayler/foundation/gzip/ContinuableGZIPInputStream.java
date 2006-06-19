// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

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
