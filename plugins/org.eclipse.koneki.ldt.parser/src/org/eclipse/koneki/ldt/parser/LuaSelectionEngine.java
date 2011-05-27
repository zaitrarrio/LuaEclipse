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
import org.eclipse.koneki.ldt.internal.parser.MatchNodeVisitor;

public class LuaSelectionEngine extends ScriptSelectionEngine {
	private LuaAssistParser parser = new LuaAssistParser();
	private List<IModelElement> elements = new ArrayList<IModelElement>();

	@Override
	public IModelElement[] select(IModuleSource module, int start, int end) {
		ModuleDeclaration ast = parser.parse(module);
		ASTNode node = findMinimalNode(ast, start, end);
		if (node == null) {
			return new IModelElement[0];
		}
		try {
			IModelElement elt = ((ISourceModule) module).getElementAt(node.sourceStart());
			// IModelElement[] code = ((ISourceModule) module).
			// for (IModelElement elt : code) {
			if (elt instanceof IModelElement)
				elements.add(elt);
			// }
		} catch (ModelException e) {
			Activator.logWarning("Unable to get element.", e); //$NON-NLS-1$
			e.printStackTrace();
		}
		ArrayList<IModelElement> result;
		result = new ArrayList<IModelElement>(elements.size());
		for (final IModelElement element : elements) {
			result.add(element);
		}
		return result.toArray(new IModelElement[result.size()]);
	}

	public static ASTNode findMinimalNode(ModuleDeclaration unit, int start, int end) {
		MatchNodeVisitor visitor = new MatchNodeVisitor(start, end + 1);
		try {
			unit.traverse(visitor);
		} catch (Exception e) {
			Activator.logWarning("Problem occured while seeking for minimal node.", e); //$NON-NLS-1$
		}
		return visitor.getNode();
	}
}
