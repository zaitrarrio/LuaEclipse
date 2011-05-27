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

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.codeassist.IAssistParser;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.SourceParserUtil;

public class LuaAssistParser implements IAssistParser {

	private ModuleDeclaration source;

	/**
	 * Initialize source to parse
	 * 
	 * @param unit
	 *            Source to parse
	 */
	@Override
	public void setSource(ModuleDeclaration unit) {
		this.source = unit;
	}

	@Override
	public void parseBlockStatements(ASTNode node, ASTNode unit, int position) {

	}

	@Override
	public ModuleDeclaration parse(IModuleSource sourceModule) {
		return SourceParserUtil
				.getModuleDeclaration((org.eclipse.dltk.core.ISourceModule) sourceModule
						.getModelElement());
	}

	@Override
	public ASTNode getAssistNodeParent() {
		return null;
	}

	@Override
	public void handleNotInElement(ASTNode unit, int position) {
	}

	@Override
	public ModuleDeclaration getModule() {
		return source;
	}

}
