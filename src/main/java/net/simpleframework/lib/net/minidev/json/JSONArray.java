package net.simpleframework.lib.net.minidev.json;

/*
 * Copyright 2011 JSON-SMART authors
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A JSON array. JSONObject supports java.util.List interface.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 * @author Uriel Chemouni <uchemouni@gmail.com>
 */
public class JSONArray extends ArrayList<Object> implements List<Object>, JSONAwareEx,
		JSONStreamAwareEx {
	private static final long serialVersionUID = 9106884089231309568L;

	public static String toJSONString(final List<? extends Object> list) {
		return toJSONString(list, JSONValue.COMPRESSION);
	}

	/**
	 * Convert a list to JSON text. The result is a JSON array. If this list is
	 * also a JSONAware, JSONAware specific behaviours will be omitted at this
	 * top level.
	 * 
	 * @see net.simpleframework.lib.net.minidev.json.JSONValue#toJSONString(Object)
	 * 
	 * @param list
	 * @param compression
	 *        Indicate compression level
	 * @return JSON text, or "null" if list is null.
	 */
	public static String toJSONString(final List<? extends Object> list, final JSONStyle compression) {
		final StringBuilder sb = new StringBuilder();
		try {
			writeJSONString(list, sb, compression);
		} catch (final IOException e) {
			// Can not append on a string builder
		}
		return sb.toString();
	}

	/**
	 * Encode a list into JSON text and write it to out. If this list is also a
	 * JSONStreamAware or a JSONAware, JSONStreamAware and JSONAware specific
	 * behaviours will be ignored at this top level.
	 * 
	 * @see JSONValue#writeJSONString(Object, Appendable)
	 * 
	 * @param list
	 * @param out
	 */
	public static void writeJSONString(final Iterable<? extends Object> list, final Appendable out,
			final JSONStyle compression) throws IOException {
		if (list == null) {
			out.append("null");
			return;
		}

		// JSONStyler styler = compression.getStyler();
		// if (styler != null) {
		// styler.arrayIn();
		// }
		boolean first = true;
		out.append('[');
		for (final Object value : list) {
			if (first) {
				first = false;
			} else {
				out.append(',');
			}
			if (value == null) {
				out.append("null");
			} else {
				JSONValue.writeJSONString(value, out, compression);
			}
		}
		out.append(']');
		// if (styler != null) {
		// styler.arrayOut();
		// out.append(styler.getNewLine());
		// }
	}

	public static void writeJSONString(final List<? extends Object> list, final Appendable out)
			throws IOException {
		writeJSONString(list, out, JSONValue.COMPRESSION);
	}

	public void merge(final Object o2) {
		JSONObject.merge(this, o2);
	}

	/**
	 * Explicitely Serialize Object as JSon String
	 */
	@Override
	public String toJSONString() {
		return toJSONString(this, JSONValue.COMPRESSION);
	}

	@Override
	public String toJSONString(final JSONStyle compression) {
		return toJSONString(this, compression);
	}

	/**
	 * Override natif toStirng()
	 */
	@Override
	public String toString() {
		return toJSONString();
	}

	/**
	 * JSONAwareEx inferface
	 * 
	 * @param compression
	 *        compression param
	 */
	public String toString(final JSONStyle compression) {
		return toJSONString(compression);
	}

	@Override
	public void writeJSONString(final Appendable out) throws IOException {
		writeJSONString(this, out, JSONValue.COMPRESSION);
	}

	@Override
	public void writeJSONString(final Appendable out, final JSONStyle compression)
			throws IOException {
		writeJSONString(this, out, compression);
	}
}
