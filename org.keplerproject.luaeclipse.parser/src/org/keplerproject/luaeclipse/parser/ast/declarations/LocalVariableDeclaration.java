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
package org.keplerproject.luaeclipse.parser.ast.declarations;

import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;

public class LocalVariableDeclaration extends FieldDeclaration {

    public LocalVariableDeclaration(String name, int nameStart, int nameEnd,
	    int declStart, int declEnd) {
	super(name, nameStart, nameEnd, declStart, declEnd);
    }

    public LocalVariableDeclaration(SimpleReference name, int declStart,
	    int declEnd) {
	this(name.getName(), name.sourceStart(), name.sourceEnd(), declStart,
		declEnd);
    }
}
