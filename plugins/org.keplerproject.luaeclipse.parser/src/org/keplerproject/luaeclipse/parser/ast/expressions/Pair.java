/******************************************************************************
 * Copyright (c) 2009 KeplerProject, Sierra Wireless.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 *          - initial API and implementation and initial documentation
 *****************************************************************************/

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-07-29 17:56:04 +0200 (mer., 29 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: Pair.java 2190 2009-07-29 15:56:04Z kkinfoo $
 */
package org.keplerproject.luaeclipse.parser.ast.expressions;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.Literal;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.statements.Statement;
import org.keplerproject.luaeclipse.parser.LuaExpressionConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class Pair.
 */
public class Pair extends SimpleReference {

	private Statement data = null;

	/**
	 * Instantiates a new pair.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 */
	public Pair(Identifier name, Statement s) {
		// super(start, end, left, LuaExpressionConstants.E_PAIR, right);
		super(name.sourceStart(), name.sourceEnd(), name.getName());
		this.data = s;
	}
	public Pair(Literal name, Statement s) {
		// super(start, end, left, LuaExpressionConstants.E_PAIR, right);
		super(name.sourceStart(), name.sourceEnd(),name.getValue());
		this.data = s;
	}

	// @Override
	// public Statement getParent() {
	// return this.parent;
	// }

	// @Override
	// public void setParent(Statement s) {
	// this.parent = s;
	// }
	@Override
	public int getKind() {
		return LuaExpressionConstants.E_PAIR;
	}

	public Statement getData() {
		return this.data;
	}

	@Override
	public void traverse(ASTVisitor pVisitor) throws Exception {
		if (pVisitor.visit(this)) {
			super.traverse(pVisitor);
			this.data.traverse(pVisitor);
			pVisitor.endvisit(this);
		}
	}
}
