/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.parser;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.koneki.ldt.internal.parser.DLTKObjectFactory;
import org.eclipse.koneki.ldt.metalua.MetaluaStateFactory;
import org.eclipse.koneki.ldt.parser.ast.LuaModuleDeclaration;

import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;

/**
 * TODO Comment this class
 */
public class AlternativeLuaSourceParser extends AbstractSourceParser {

	private final String metaluaScriptPath = "/scriptMetalua/dltk_ast_builder.mlua";//$NON-NLS-1$

	/**
	 * @see org.eclipse.dltk.ast.parser.ISourceParser#parse(org.eclipse.dltk.compiler.env.IModuleSource,
	 *      org.eclipse.dltk.compiler.problem.IProblemReporter)
	 */
	@Override
	public IModuleDeclaration parse(IModuleSource input, IProblemReporter reporter) {
		LuaModuleDeclaration module = new LuaModuleDeclaration(input.getSourceContents().length());
		// Create a lua instance
		LuaState lua = MetaluaStateFactory.newLuaState();

		// Load module which helps avoiding reflection between Lua and Java
		DLTKObjectFactory.register(lua);

		/*
		 * Load Metalua ast parser utility module
		 */
		URL url = Platform.getBundle(Activator.PLUGIN_ID).getEntry(metaluaScriptPath);
		try {
			// Enable
			String path = FileLocator.toFileURL(url).getFile();
			lua.getGlobal("mlc"); //$NON-NLS-1$
			lua.getField(-1, "luafile_to_function"); //$NON-NLS-1$
			lua.remove(-2); // remove mlc table
			lua.pushString(path); // arg
			lua.call(1, 2);
			lua.pop(1); // remove nil
			lua.call(0, 1);
			// Call module's parsing function
			lua.getField(-1, "ast_builder"); //$NON-NLS-1$
			lua.pushString(input.getSourceContents());
			lua.call(1, 1);
			module = lua.checkJavaObject(-1, LuaModuleDeclaration.class);
			// Deal with errors on lua side
			if (module.hasError()) {
				DefaultProblem problem = module.getProblem();
				problem.setOriginatingFileName(input.getFileName());
				reporter.reportProblem(problem);
			}
			return module;
		} catch (IOException e) {
			Activator.logError("Unable to read metalua ast builder file", e); //$NON-NLS-1$
			return module;
		} catch (LuaException e) {
			Activator.logError("Unable to load metalua ast builder", e); //$NON-NLS-1$
			return module;
		}
	}
}
