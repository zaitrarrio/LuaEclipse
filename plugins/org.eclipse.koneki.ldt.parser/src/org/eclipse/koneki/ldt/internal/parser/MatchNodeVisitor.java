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

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.statements.Block;

/**
 * Visits ASTs in order to find node under start and end offsets.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class MatchNodeVisitor extends ASTVisitor {
	private ASTNode result = null;
	private final int end, start;

	/**
	 * Constructor initialized with offsets to seek for
	 * 
	 * @param start
	 *            offset
	 * @param end
	 *            offset
	 */
	public MatchNodeVisitor(int start, int end) {
		this.end = end;
		this.start = start;
	}

	/**
	 * Retrieve matching node, available after using current object in {@link ASTNode#traverse(ASTVisitor)}
	 * 
	 * @return matching {@link ASTNode} or null if not available
	 */
	public ASTNode getNode() {
		return result;
	}

	protected void setNode(ASTNode node) {
		result = node;
	}

	/**
	 * Browse given {@link ASTNode} tree to found a node matching offset provided in {@link #MatchNodeVisitor(int, int)}
	 * 
	 * @see ASTVisitor#visitGeneral(ASTNode)
	 * @see #MatchNodeVisitor(int, int)
	 */
	public boolean visitGeneral(ASTNode s) throws Exception {
		int realStart = s.sourceStart();
		int realEnd = s.sourceEnd();
		if (s instanceof Declaration) {
			Declaration declaration = (Declaration) s;
			realStart = declaration.getNameStart();
			realEnd = declaration.getNameEnd();
		} else if (s instanceof Block) {
			// Ignore composite nodes like Chunk and Block
			realStart = realEnd = -42;
		}
		if (realStart >= start && realEnd <= end) {
			if (getNode() != null && s.sourceStart() >= getNode().sourceStart() && s.sourceEnd() <= getNode().sourceEnd()) {
				setNode(s);
			} else {
				setNode(s);
			}
		}
		return true;
	}

}