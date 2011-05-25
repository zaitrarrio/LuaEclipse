package org.keplerproject.luaeclipse.parser.lua.internal.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.keplerproject.luaeclipse.internal.parser.MetaluaASTWalker;
import org.keplerproject.luaeclipse.metalua.Metalua;
import org.keplerproject.luaeclipse.parser.Activator;
import org.osgi.framework.Bundle;

import com.github.lunatest.LunaTest;
import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;

public class TestIndex extends TestCase {

	private LuaState state;
	private int stackSize;
	private String testFile = "/script/tests.lua"; //$NON-NLS-1$

	@Before
	public void setUp() {
		String code, testedCodeFile;
		try {
			// Retrieve home made index file
			setState(Metalua.newState());
			stackSize = getState().getTop();
			Bundle bundle = Activator.getDefault().getBundle();
			URL ressource = bundle.getResource(MetaluaASTWalker.luaFile);
			testedCodeFile = FileLocator.toFileURL(ressource).getPath();

			// Load it in Lua instance
			FileInputStream input = new FileInputStream(testedCodeFile);
			getState().load(input, "loadingLuaParserIndex");
			getState().call(0, 0);
			input.close();

			// Retrieve lunatest path
			Path path = LunaTest.getPath();
			code = "package.path = package.path  .. [[;" + path //$NON-NLS-1$
					+ "?.luac;" + path + "?.lua]]"; //$NON-NLS-1$ //$NON-NLS-2$	
			getState().load(code, "lunatestPathLoading");
			getState().call(0, 0);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (LuaException e) {
			fail(e.getMessage());
		}
	}

	private LuaState getState() {
		return state;
	}

	private void setState(final LuaState s) {
		state = s;
	}

	@Test
	public void testLuaSide() {
		// Locate test file
		Bundle bundle = Activator.getDefault().getBundle();
		URL ressource = bundle.getResource(testFile);
		try {
			String filename = FileLocator.toFileURL(ressource).getPath();
			// Load lua unit test file
			FileInputStream input = new FileInputStream(filename);
			getState().load(input, "loadingLuaParserIndex");
			getState().call(0, 0);
			input.close();

			// Load lunatest runner
			getState().getGlobal("lunatest");
			getState().getField(-1, "run");
			getState().remove(-2);
			// Run tests
			getState().call(0, 2);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (LuaException e) {
			fail(e.getMessage());
		}
	}

	@After
	public void tearDown() {
		getState().pop(getState().getTop() - stackSize);
	}
}
