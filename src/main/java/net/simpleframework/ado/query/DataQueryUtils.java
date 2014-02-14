package net.simpleframework.ado.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.simpleframework.common.coll.CollectionUtils.AbstractIterator;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class DataQueryUtils {

	public static <T> IDataQuery<T> nullQuery() {
		return new ListDataQuery<T>();
	}

	public static <T> Iterator<T> toIterator(final IDataQuery<T> dataQuery) {
		return new AbstractIterator<T>() {
			private T t;

			@Override
			public boolean hasNext() {
				return dataQuery != null && (t = dataQuery.next()) != null;
			}

			@Override
			public T next() {
				return t;
			}
		};
	}

	public static <T> List<T> toList(final IDataQuery<T> dataQuery) {
		T t;
		final List<T> al = new ArrayList<T>();
		while (dataQuery != null && (t = dataQuery.next()) != null) {
			al.add(t);
		}
		return al;
	}
}
