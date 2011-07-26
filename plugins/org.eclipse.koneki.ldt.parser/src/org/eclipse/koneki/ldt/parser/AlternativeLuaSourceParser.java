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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ast.ASTNode;
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
 * Generates AST from Metalua analysis, {@link ASTNode}s are created straight from Lua
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class AlternativeLuaSourceParser extends AbstractSourceParser {
	public final static String libPath = "/scriptMetalua/";//$NON-NLS-1$
	public final static String builderScript = "dltk_ast_builder.mlua";//$NON-NLS-1$
	public final static String markerScript = "declaration_marker.lua";//$NON-NLS-1$
	private static Dictionary<String, IModuleDeclaration> cache = new Hashtable<String, IModuleDeclaration>();

	/** Load Metalua ast parser utility module */
	private static LuaState lua = null;

	private synchronized static LuaState getLuaState() {
		if (lua == null) {
			lua = MetaluaStateFactory.newLuaState();
			// Load module which helps avoiding reflection between Lua and Java
			DLTKObjectFactory.register(lua);
			// Load needed files
			try {
				// Ensure folder extraction on disk
				URL folderUrl = FileLocator.toFileURL(Platform.getBundle(Activator.PLUGIN_ID).getEntry(libPath));
				File folder = new File(folderUrl.getFile());
				File builder = new File(folder, builderScript);
				File marker = new File(folder, markerScript);

				// Module used to detect declaration
				// String path = new File(FileLocator.toFileURL(marker).getFile()).getPath();
				lua.load(new FileInputStream(marker), "Loading AST marker."); //$NON-NLS-1$
				lua.call(0, 1);

				// Associate module and variable name
				lua.setGlobal("mark"); //$NON-NLS-1$

				// Module used to generate AST
				// path = new File(FileLocator.toFileURL(builder).getFile()).getPath();
				lua.getGlobal("mlc"); //$NON-NLS-1$
				lua.getField(-1, "luafile_to_function"); //$NON-NLS-1$

				// Remove mlc table
				lua.remove(-2);

				// Provide path of module as argument
				lua.pushString(builder.getPath());

				// Metalua code loading
				lua.call(1, 2);

				// Remove nil awkward nil, returned every time
				lua.pop(1);

				// Metalua code compilation
				lua.call(0, 1);
				lua.setGlobal("parsemod"); //$NON-NLS-1$
			} catch (IOException e) {
				Activator.logError("Unable to read metalua ast builder and marker files", e); //$NON-NLS-1$
			}
		}
		return lua;
	}

	/**
	 * Generate DLTK AST straight from Lua
	 * 
	 * @param input
	 *            Source to parse
	 * @param reporter
	 *            Enable to report errors in parsed source code
	 */
	@Override
	public IModuleDeclaration parse(IModuleSource input, IProblemReporter reporter) {
		LuaModuleDeclaration module = new LuaModuleDeclaration(input.getSourceContents().length());

		try {
			synchronized (getLuaState()) {
				// Call module's parsing function
				getLuaState().getGlobal("parsemod");//$NON-NLS-1$
				getLuaState().getField(-1, "ast_builder"); //$NON-NLS-1$
				getLuaState().pushString(input.getSourceContents());
				getLuaState().call(1, 1);
				module = getLuaState().checkJavaObject(-1, LuaModuleDeclaration.class);
				getLuaState().pop(2);
			}
			// Deal with errors on lua side
			if (module.hasError()) {
				DefaultProblem problem = module.getProblem();
				problem.setOriginatingFileName(input.getFileName());
				reporter.reportProblem(problem);
				IModuleDeclaration cached = cache.get(input.getFileName());
				if (cached != null) {
					return cached;
				}
			} else {
				cache.put(input.getFileName(), module);
			}
			return module;
		} catch (LuaException e) {
			Activator.logError("Unable to load metalua ast builder", e); //$NON-NLS-1$
			return module;
		}
	}
}
