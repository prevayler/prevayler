package org.prevayler.implementation.journal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class ChunkedInputStream {

	private static final String ASCII = "US-ASCII";
	private static final String SIZE = "0|[1-9A-F][0-9A-F]{0,6}|[1-7][0-9A-F]{7}";
	private static final String TOKEN = "[^\u0000-\u0020()<>@,;:\\\\\"/\\[\\]?={}\u007F-\uFFFF]+";
	private static final String HEADER = "(" + SIZE + ")(;" + TOKEN + "=" + TOKEN + ")*\r\n";
	private static final Pattern TOKEN_PATTERN = Pattern.compile(TOKEN);
	private static final Pattern HEADER_PATTERN = Pattern.compile(HEADER);

	static boolean validToken(String token) {
		return TOKEN_PATTERN.matcher(token).matches();
	}

	private InputStream stream;
	private Map parameters;

	public ChunkedInputStream(InputStream stream) {
		this.stream = stream;
	}

	public byte[] readChunk() throws IOException {
		String header = readLine();

		if (header == null) {
			return null;
		}

		if (!HEADER_PATTERN.matcher(header).matches()) {
			throw new IOException("Chunk header corrupted");
		}

		StringTokenizer tokenizer = new StringTokenizer(header, ";=\r\n");

		int size = Integer.parseInt(tokenizer.nextToken(), 16);

		parameters = new LinkedHashMap();
		while (tokenizer.hasMoreTokens()) {
			String name = tokenizer.nextToken();
			String value = tokenizer.nextToken();
			parameters.put(name, value);
		}

		byte[] chunk = new byte[size];
		int total = 0;
		while (total < size) {
			int read = stream.read(chunk, total, size - total);
			if (read == -1) {
				throw new IOException("Unexpected end of stream in chunk data");
			}
			total += read;
		}

		if (stream.read() != '\r' || stream.read() != '\n') {
			throw new IOException("Chunk trailer corrupted");
		}

		return chunk;
	}

	private String readLine() throws IOException {
		ByteArrayOutputStream header = new ByteArrayOutputStream();
		while (true) {
			int b = stream.read();
			if (b == -1) {
				if (header.size() == 0) {
					return null;
				} else {
					throw new IOException("Unexpected end of stream in chunk header");
				}
			}
			header.write(b);
			if (b == '\n') {
				return header.toString(ASCII);
			}
		}
	}

	public Map getParameters() {
		return parameters;
	}

}
