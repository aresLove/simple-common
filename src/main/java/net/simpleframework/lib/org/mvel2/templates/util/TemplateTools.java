/**
 * MVEL 2.0
 * Copyright (C) 2007 The Codehaus
 * Mike Brock, Dhanji Prasanna, John Graham, Mark Proctor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.simpleframework.lib.org.mvel2.templates.util;

import static java.nio.ByteBuffer.allocateDirect;
import static net.simpleframework.lib.org.mvel2.util.ParseTools.balancedCapture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.simpleframework.lib.org.mvel2.templates.TemplateError;
import net.simpleframework.lib.org.mvel2.templates.res.Node;
import net.simpleframework.lib.org.mvel2.templates.res.TerminalNode;

public class TemplateTools {
	public static Node getLastNode(final Node node) {
		Node n = node;
		while (true) {
			if (n.getNext() instanceof TerminalNode) {
				return n;
			}
			n = n.getNext();
		}
	}

	public static int captureToEOS(final char[] expression, int cursor) {
		final int length = expression.length;
		while (cursor != length) {
			switch (expression[cursor]) {
			case '(':
			case '[':
			case '{':
				cursor = balancedCapture(expression, cursor, expression[cursor]);
				break;

			case ';':
			case '}':
				return cursor;

			}
			cursor++;
		}

		return cursor;
	}

	public static String readInFile(final String file) {
		return readInFile(new File(file));
	}

	public static String readInFile(final File file) {
		try {
			final FileChannel fc = new FileInputStream(file).getChannel();
			final ByteBuffer buf = allocateDirect(10);
			final StringBuilder appender = new StringBuilder();
			int read;

			while (true) {
				buf.rewind();
				if ((read = fc.read(buf)) != -1) {
					buf.rewind();
					for (; read != 0; read--) {
						appender.append((char) buf.get());
					}
				} else {
					break;
				}
			}

			fc.close();

			return appender.toString();
		} catch (final FileNotFoundException e) {
			throw new TemplateError(
					"cannot include template '" + file.getName() + "': file not found.");
		} catch (final IOException e) {
			throw new TemplateError(
					"unknown I/O exception while including '" + file.getName() + "' (stacktrace nested)",
					e);
		}
	}

	public static String readStream(final InputStream instream) {
		try {
			final byte[] buf = new byte[10];
			final StringBuilder appender = new StringBuilder();
			int read;
			while ((read = instream.read(buf)) != -1) {
				for (int i = 0; i < read; i++) {
					appender.append((char) buf[i]);
				}
			}

			return appender.toString();
		} catch (final NullPointerException e) {
			if (instream == null) {
				throw new TemplateError("null input stream", e);
			} else {
				throw e;
			}
		} catch (final IOException e) {
			throw new TemplateError("unknown I/O exception while including (stacktrace nested)", e);
		}
	}
}
