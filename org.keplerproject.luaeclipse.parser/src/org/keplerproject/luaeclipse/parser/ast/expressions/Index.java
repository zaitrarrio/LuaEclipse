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
package org.keplerproject.luaeclipse.parser.ast.expressions;

import org.eclipse.dltk.ast.expressions.Expression;
import org.keplerproject.luaeclipse.parser.LuaExpressionConstants;

/**
 * The Class Index represents a couple of identifiers. As instance, in statement
 * <code>table.field = nil</code> Metalua sees
 * <code>`Set{ { `Index{ `Id "table", `String "field" } }, { `Nil } }</code>.
 * So, the node <code>table.field</code> is represented by an index node.
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class Index extends BinaryExpression implements LeftHandSide,
	LuaExpressionConstants,
	org.keplerproject.luaeclipse.internal.parser.Index {

    private long id;

    /**
     * Instantiates a new index node
     * 
     * @param start
     *            start offset of couple
     * @param end
     *            end offset of couple
     * @param key
     *            identifier left side
     * @param value
     *            expression on right side
     */
    public Index(int start, int end, Expression key, Expression value) {
	this(start, end, key, E_INDEX, value);
    }

    protected Index(int start, int end, Expression key, int kind,
	    Expression value) {
	super(start, end, key, kind, value);
    }

    /**
     * Just the right parent of this couple
     * 
     * @return {@link Expression} on right side
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
     * Just the left parent of this couple
     * 
     * @return {@link Expression} on left side
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
}
