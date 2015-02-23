// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package net.simpleframework.common.buffer;

/**
 * Fast, fast <code>long</code> buffer.
 * This buffer implementation does not store all data
 * in single array, but in array of chunks.
 */
public class FastLongBuffer {

	private long[][] buffers = new long[16][];
	private int buffersCount;
	private int currentBufferIndex = -1;
	private long[] currentBuffer;
	private int offset;
	private int size;
	private final int minChunkLen;

	/**
	 * Creates a new <code>long</code> buffer. The buffer capacity is
	 * initially 1024 bytes, though its size increases if necessary.
	 */
	public FastLongBuffer() {
		this.minChunkLen = 1024;
	}

	/**
	 * Creates a new <code>long</code> buffer, with a buffer capacity of
	 * the specified size, in bytes.
	 *
	 * @param size
	 *        the initial size.
	 * @throws IllegalArgumentException
	 *         if size is negative.
	 */
	public FastLongBuffer(final int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Invalid size: " + size);
		}
		this.minChunkLen = size;
	}

	/**
	 * Prepares next chunk to match new size.
	 * The minimal length of new chunk is <code>minChunkLen</code>.
	 */
	private void needNewBuffer(final int newSize) {
		final int delta = newSize - size;
		final int newBufferSize = Math.max(minChunkLen, delta);

		currentBufferIndex++;
		currentBuffer = new long[newBufferSize];
		offset = 0;

		// add buffer
		if (currentBufferIndex >= buffers.length) {
			final int newLen = buffers.length << 1;
			final long[][] newBuffers = new long[newLen][];
			System.arraycopy(buffers, 0, newBuffers, 0, buffers.length);
			buffers = newBuffers;
		}
		buffers[currentBufferIndex] = currentBuffer;
		buffersCount++;
	}

	/**
	 * Appends <code>long</code> array to buffer.
	 */
	public FastLongBuffer append(final long[] array, final int off, final int len) {
		final int end = off + len;
		if ((off < 0) || (len < 0) || (end > array.length)) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return this;
		}
		final int newSize = size + len;
		int remaining = len;

		if (currentBuffer != null) {
			// first try to fill current buffer
			final int part = Math.min(remaining, currentBuffer.length - offset);
			System.arraycopy(array, end - remaining, currentBuffer, offset, part);
			remaining -= part;
			offset += part;
			size += part;
		}

		if (remaining > 0) {
			// still some data left
			// ask for new buffer
			needNewBuffer(newSize);

			// then copy remaining
			// but this time we are sure that it will fit
			final int part = Math.min(remaining, currentBuffer.length - offset);
			System.arraycopy(array, end - remaining, currentBuffer, offset, part);
			offset += part;
			size += part;
		}

		return this;
	}

	/**
	 * Appends <code>long</code> array to buffer.
	 */
	public FastLongBuffer append(final long[] array) {
		return append(array, 0, array.length);
	}

	/**
	 * Appends single <code>long</code> to buffer.
	 */
	public FastLongBuffer append(final long element) {
		if ((currentBuffer == null) || (offset == currentBuffer.length)) {
			needNewBuffer(size + 1);
		}

		currentBuffer[offset] = element;
		offset++;
		size++;

		return this;
	}

	/**
	 * Appends another fast buffer to this one.
	 */
	public FastLongBuffer append(final FastLongBuffer buff) {
		if (buff.size == 0) {
			return this;
		}
		for (int i = 0; i < buff.currentBufferIndex; i++) {
			append(buff.buffers[i]);
		}
		append(buff.currentBuffer, 0, buff.offset);
		return this;
	}

	/**
	 * Returns buffer size.
	 */
	public int size() {
		return size;
	}

	/**
	 * Tests if this buffer has no elements.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns current index of inner <code>long</code> array chunk.
	 * Represents the index of last used inner array chunk.
	 */
	public int index() {
		return currentBufferIndex;
	}

	/**
	 * Returns the offset of last used element in current inner array chunk.
	 */
	public int offset() {
		return offset;
	}

	/**
	 * Returns <code>long</code> inner array chunk at given index.
	 * May be used for iterating inner chunks in fast manner.
	 */
	public long[] array(final int index) {
		return buffers[index];
	}

	/**
	 * Resets the buffer content.
	 */
	public void clear() {
		size = 0;
		offset = 0;
		currentBufferIndex = -1;
		currentBuffer = null;
		buffersCount = 0;
	}

	/**
	 * Creates <code>long</code> array from buffered content.
	 */
	public long[] toArray() {
		int pos = 0;
		final long[] array = new long[size];

		if (currentBufferIndex == -1) {
			return array;
		}

		for (int i = 0; i < currentBufferIndex; i++) {
			final int len = buffers[i].length;
			System.arraycopy(buffers[i], 0, array, pos, len);
			pos += len;
		}

		System.arraycopy(buffers[currentBufferIndex], 0, array, pos, offset);

		return array;
	}

	/**
	 * Creates <code>long</code> subarray from buffered content.
	 */
	public long[] toArray(int start, final int len) {
		int remaining = len;
		int pos = 0;
		final long[] array = new long[len];

		if (len == 0) {
			return array;
		}

		int i = 0;
		while (start >= buffers[i].length) {
			start -= buffers[i].length;
			i++;
		}

		while (i < buffersCount) {
			final long[] buf = buffers[i];
			final int c = Math.min(buf.length - start, remaining);
			System.arraycopy(buf, start, array, pos, c);
			pos += c;
			remaining -= c;
			if (remaining == 0) {
				break;
			}
			start = 0;
			i++;
		}
		return array;
	}

	/**
	 * Returns <code>long</code> element at given index.
	 */
	public long get(int index) {
		if ((index >= size) || (index < 0)) {
			throw new IndexOutOfBoundsException();
		}
		int ndx = 0;
		while (true) {
			final long[] b = buffers[ndx];
			if (index < b.length) {
				return b[index];
			}
			ndx++;
			index -= b.length;
		}
	}

}