/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-07-29 17:56:04 +0200 (mer., 29 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: Set.java 2190 2009-07-29 15:56:04Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.statements;

import org.eclipse.dltk.ast.expressions.ExpressionConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class Set.
 */
public class Set extends BinaryStatement {
	/**
	 * Construct default strict assignment.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 */
	public Set(int start, int end, Chunk left, Chunk right) {
		super(start, end, left, E_ASSIGN, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anwrt.ldt.parser.ast.expressions.BinaryExpression#getKind()
	 */
	@Override
	public int getKind() {
		return ExpressionConstants.E_ASSIGN;
	}

	/**
	 * Convert to string in pattern: "left = right".
	 * 
	 * @return the string
	 */
	public String toString() {
		return getLeft().toString() + '=' + getRight().toString();
	}
}
