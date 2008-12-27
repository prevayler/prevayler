package org.prevayler.foundation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.EOFException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Chunking {

	private static final String ASCII = "US-ASCII";
	private static final byte[] CRLF = new byte[] {'\r', '\n'};
	private static final String SIZE = "0|[1-9A-F][0-9A-F]{0,6}|[1-7][0-9A-F]{7}";
	private static final String TOKEN = "[^\u0000-\u0020()<>@,;:\\\\\"/\\[\\]?={}\u007F-\uFFFF]+";
	private static final String HEADER = "(" + SIZE + ")(;" + TOKEN + "=" + TOKEN + ")*\r\n";
	private static final Pattern TOKEN_PATTERN = Pattern.compile(TOKEN);
	private static final Pattern HEADER_PATTERN = Pattern.compile(HEADER);

	private static boolean validToken(String token) {
		return TOKEN_PATTERN.matcher(token).matches();
	}

	public static void writeChunk(OutputStream stream, Chunk chunk) throws IOException {
		stream.write(Integer.toHexString(chunk.getBytes().length).toUpperCase().getBytes(ASCII));
		Iterator iterator = chunk.getParameters().entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (!validToken(name)) {
				throw new IOException("Invalid parameter name '" + name + "'");
			}
			if (!validToken(value)) {
				throw new IOException("Invalid parameter value '" + value + "'");
			}
			stream.write(';');
			stream.write(name.getBytes(ASCII));
			stream.write('=');
			stream.write(value.getBytes(ASCII));
		}
		stream.write(CRLF);
		stream.write(chunk.getBytes());
		stream.write(CRLF);
	}

	public static Chunk readChunk(InputStream stream) throws IOException {
		String header = readLine(stream);

		if (header == null) {
			return null;
		}

		if (!HEADER_PATTERN.matcher(header).matches()) {
			throw new IOException("Chunk header corrupted");
		}

		StringTokenizer tokenizer = new StringTokenizer(header, ";=\r\n");

		int size = Integer.parseInt(tokenizer.nextToken(), 16);

		Map parameters = new LinkedHashMap();
		while (tokenizer.hasMoreTokens()) {
			String name = tokenizer.nextToken();
			String value = tokenizer.nextToken();
			parameters.put(name, value);
		}

		byte[] bytes = new byte[size];
		int total = 0;
		while (total < size) {
			int read = stream.read(bytes, total, size - total);
			if (read == -1) {
				throw new EOFException("Unexpected end of stream in chunk data");
			}
			total += read;
		}

		int cr = stream.read();
		int lf = stream.read();
		if (cr == -1 || cr == '\r' && lf == -1) {
			throw new EOFException("Unexpected end of stream in chunk trailer");
		} else if (cr != '\r' || lf != '\n') {
			throw new IOException("Chunk trailer corrupted");
		}

		return new Chunk(bytes, parameters);
	}

	private static String readLine(InputStream stream) throws IOException {
		ByteArrayOutputStream header = new ByteArrayOutputStream();
		while (true) {
			int b = stream.read();
			if (b == -1) {
				if (header.size() == 0) {
					return null;
				} else {
					throw new EOFException("Unexpected end of stream in chunk header");
				}
			}
			header.write(b);
			if (b == '\n') {
				return header.toString(ASCII);
			}
		}
	}

}
