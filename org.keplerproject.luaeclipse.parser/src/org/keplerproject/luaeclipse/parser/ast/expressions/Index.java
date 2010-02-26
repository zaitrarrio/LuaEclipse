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
 * @date $Date: 2009-07-23 12:07:30 +0200 (jeu., 23 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: Index.java 2161 2009-07-23 10:07:30Z kkinfoo $
 */
package org.keplerproject.luaeclipse.parser.ast.expressions;

import org.eclipse.dltk.ast.expressions.Expression;
import org.keplerproject.luaeclipse.parser.LuaExpressionConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class Index.
 */
public class Index extends BinaryExpression implements LeftHandSide,
	LuaExpressionConstants,
	org.keplerproject.luaeclipse.internal.parser.Index {

    private long id;

    /**
     * Instantiates a new index.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     * @param key
     *            the table
     * @param value
     *            the index
     */
    public Index(int start, int end, Expression key, Expression value) {
	this(start, end, key, E_INDEX, value);
    }

    protected Index(int start, int end, Expression key, int kind,
	    Expression value) {
	super(start, end, key, kind, value);
    }



    /**
     * Gets the index.
     * 
     * @return the index
     */
    public Expression getValue() {
	return getRight();
    }

    public long getID() {
	return id;
    }

    public void setID(long id) {
	this.id = id;
    }

    /**
     * Gets the table.
     * 
     * @return the table
     */
    public Expression getKey() {
	return getLeft();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.anwrt.ldt.parser.ast.expressions.LeftHandSide#isLeftHandSide()
     */
    @Override
    public boolean isLeftHandSide() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.dltk.ast.statements.Statement#traverse(org.eclipse.dltk.ast
     * .ASTVisitor)
     */
//    public void traverse(ASTVisitor visitor) throws Exception {
//	if (visitor.visit(this)) {
//	    super.traverse(visitor);
//	    visitor.endvisit(this);
//	}
//    }
}
