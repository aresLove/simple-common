/*
 * Copyright 2003 The Apache Software Foundation
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
package net.simpleframework.lib.net.sf.cglib.beans;

public class BulkBeanException extends RuntimeException {
	private final int index;
	private Throwable cause;

	public BulkBeanException(final String message, final int index) {
		super(message);
		this.index = index;
	}

	public BulkBeanException(final Throwable cause, final int index) {
		super(cause.getMessage());
		this.index = index;
		this.cause = cause;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}
}
