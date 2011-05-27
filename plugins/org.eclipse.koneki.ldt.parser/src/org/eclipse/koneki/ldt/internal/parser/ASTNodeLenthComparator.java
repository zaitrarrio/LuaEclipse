/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *     Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 ******************************************************************************/
package org.eclipse.koneki.ldt.internal.parser;

import java.util.Comparator;

import org.eclipse.dltk.ast.ASTNode;

public class ASTNodeLenthComparator implements Comparator<ASTNode> {

	private int start;
	private int end;
	private int length;

	public ASTNodeLenthComparator(int start, int end) {
		this.start = start;
		this.end = end;
		this.length = end - start;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(ASTNode actual, ASTNode challenger) {
		int aLength = actual.sourceEnd()-actual.sourceStart();
		int cLength = challenger.sourceEnd()-challenger.sourceStart();
		int aStart = actual.sourceStart(), cStart = challenger.sourceStart();
		// Same position
		if (aLength == cLength && aStart == cStart) {
			return 0;
		}

		int aFarStart = farFromStart(actual);
		int cFarStart = farFromStart(challenger);

		if (aFarStart < cFarStart) {
			// In boundaries
			return aLength - cLength;
		}
		return cLength - aLength;
	}
	private int farFromStart(ASTNode node) {
		return Math.abs(start - node.sourceStart());
	}

}
