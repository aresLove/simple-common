/**
 * MVEL (The MVFLEX Expression Language)
 *
 * Copyright (C) 2007 Christopher Brock, MVFLEX/Valhalla Project and the
 * Codehaus
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
 *
 */
package net.simpleframework.lib.org.mvel2.optimizers.impl.refl.nodes;

import java.lang.reflect.Array;

import net.simpleframework.lib.org.mvel2.compiler.AccessorNode;
import net.simpleframework.lib.org.mvel2.integration.VariableResolverFactory;

public class ArrayAccessor implements AccessorNode {
	private AccessorNode nextNode;
	private int index;

	public ArrayAccessor() {
	}

	public ArrayAccessor(final int index) {
		this.index = index;
	}

	@Override
	public Object getValue(final Object ctx, final Object elCtx, final VariableResolverFactory vars) {
		if (nextNode != null) {
			return nextNode.getValue(Array.get(ctx, index), elCtx, vars);
		} else {
			try {
				return Array.get(ctx, index);
			} catch (final IllegalArgumentException e) {
				// This isn't great, but the mechanism for deoptimizing a stale
				// accessor is currently based on
				// Accessor's throwing a ClassCastException. Catching
				// IllegalArgumentException in
				// net.simpleframework.lib.org.mvel2.ast.ASTNode.getReducedValueAccelerated(Object,
				// Object, VariableResolverFactory)
				// is a bad idea and currently there is nowhere to easily introduce
				// pre-emptive accessor validity.
				throw new ClassCastException("Argument of type '" + ctx.getClass()
						+ "' is not an Array");
			}
		}
	}

	@Override
	public Object setValue(final Object ctx, final Object elCtx,
			final VariableResolverFactory variableFactory, final Object value) {
		if (nextNode != null) {
			return nextNode.setValue(Array.get(ctx, index), elCtx, variableFactory, value);
		} else {
			Array.set(ctx, index, value);
			return value;
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	@Override
	public AccessorNode getNextNode() {
		return nextNode;
	}

	@Override
	public AccessorNode setNextNode(final AccessorNode nextNode) {
		return this.nextNode = nextNode;
	}

	@Override
	public Class getKnownEgressType() {
		return Object[].class;
	}

	@Override
	public String toString() {
		return "Array Accessor -> [" + index + "]";
	}
}
