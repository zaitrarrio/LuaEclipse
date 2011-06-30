/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 ******************************************************************************/
package org.eclipse.koneki.ldt.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.koneki.ldt.internal.parser.MatchNodeVisitor;

public class LuaSelectionEngine extends ScriptSelectionEngine {
	private LuaAssistParser parser = new LuaAssistParser();
	private List<IModelElement> elements = new ArrayList<IModelElement>();

	/**
	 * Fetch {@link IModelElement} in an {@link IModuleSource} AST from source offset.
	 * 
	 * @param module
	 *            Browsed AST
	 * @param start
	 *            offset
	 * @param end
	 *            offset
	 * @return {@link IModelElement} representing declaration of node at given offset, if node under given offset is an instance of a declared element
	 *         its declaration will be returned.
	 * @see org.eclipse.dltk.codeassist.ISelectionEngine#select(IModuleSource, int, int)
	 */
	@Override
	public IModelElement[] select(IModuleSource module, int start, int end) {
		ModuleDeclaration ast = null;
		if (module instanceof ISourceModule) {
			ast = SourceParserUtil.getModuleDeclaration((ISourceModule) module);
		}
		if (ast == null) {
			ast = parser.parse(module);
		}
		ASTNode node = findMinimalDeclaration(ast, start, end);
		if (node == null) {
			return new IModelElement[0];
		}
		try {
			IModelElement elt = ((ISourceModule) module).getElementAt(node.sourceStart());
			if (elt != null) {
				elements.add(elt);
			}
		} catch (ModelException e) {
			Activator.logWarning("Unable to get model element.", e); //$NON-NLS-1$
		}
		ArrayList<IModelElement> result;
		result = new ArrayList<IModelElement>(elements.size());
		for (final IModelElement element : elements) {
			result.add(element);
		}
		return result.toArray(new IModelElement[result.size()]);
	}

	/**
	 * Fetch an {@link ASTNode} of a {@link ModuleDeclaration} from source code offsets
	 * 
	 * @param ast
	 *            Syntax tree to browse
	 * @param start
	 *            offset in source code
	 * @param end
	 *            offset in source code
	 * @return ASTNode in ModuleDeclaration at given offsets
	 */
	public static ASTNode findMinimalDeclaration(ModuleDeclaration ast, int start, int end) {
		MatchNodeVisitor visitor = new MatchNodeVisitor(start, end + 1);
		try {
			ast.traverse(visitor);
		} catch (Exception e) {
			Activator.logWarning("Problem occured while seeking for minimal node.", e); //$NON-NLS-1$
		}
		return visitor.getNode();
	}
}
