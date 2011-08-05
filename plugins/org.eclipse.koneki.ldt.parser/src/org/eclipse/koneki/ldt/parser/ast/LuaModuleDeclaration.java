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
 * $Id: LuaModuleDeclaration.java 2190 2009-07-29 15:56:04Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast;

import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.utils.CorePrinter;

// TODO: Auto-generated Javadoc
/**
 * The Class LuaModuleDeclaration.
 */
public class LuaModuleDeclaration extends ModuleDeclaration {

	/** Indicates if any problem occurred during parsing */
	private DefaultProblem problem = null;

	/**
	 * Instantiates a new Lua module declaration.
	 * 
	 * @param sourceLength
	 *            the source length
	 */
	public LuaModuleDeclaration(int sourceLength) {
		super(sourceLength);
	}

	/**
	 * Instantiates a new Lua module declaration.
	 * 
	 * @param length
	 *            the length
	 * @param rebuild
	 *            the rebuild
	 */
	public LuaModuleDeclaration(int length, boolean rebuild) {
		super(length, rebuild);
	}

	//
	// @Override
	// public boolean equals(Object o) {
	// return o instanceof LuaModuleDeclaration;
	// }

	public void setProblem(final int line, final int column, final int offset, final String message) {
		IProblemIdentifier id = DefaultProblemIdentifier.decode(offset);
		problem = new DefaultProblem(new String(), message, id, new String[0], ProblemSeverity.ERROR, offset, -1, line, column);
	}

	public boolean hasError() {
		return problem != null;
	}

	public DefaultProblem getProblem() {
		return problem;
	}

	@Override
	public void printNode(CorePrinter output) {
		MethodDeclaration[] functions = this.getFunctions();
		if (functions.length > 0) {
			output.print("functions: ");
			for (MethodDeclaration function : functions) {
				output.print(function.getName());
				output.print(' ');
			}
			output.println();
		}
		FieldDeclaration[] fields = this.getVariables();
		if (fields.length > 0) {
			output.print("fields: ");
			for (FieldDeclaration field : fields) {
				output.print(field.getName());
				output.print(' ');
			}
			output.println();
		}
		TypeDeclaration[] types = this.getTypes();
		if (fields.length > 0) {
			output.print("types: ");
			for (TypeDeclaration type : types) {
				output.print(type.getName());
				output.print(' ');
			}
			output.println();
		}
		output.indent();
		for (Object o : getStatements()) {
			if (o instanceof Statement) {
				((Statement) o).printNode(output);
			}
		}
		output.dedent();
	}
}
