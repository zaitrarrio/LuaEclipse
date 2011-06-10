package org.eclipse.koneki.ldt.luadoc;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;

public class Luadoc {
	private static final String SOURCE_FOLDER = "lib/";

	private static final String[] REQUIRED_MODULES = new String[] { "eclipse.luadoc" };
	
	
	/**
	 * Returns the path where Lua resources can be found..
	 * 
	 * @return Source path
	 */
	private static File sourcePath() {
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		try {

			URL resource = bundle.getResource("/" + SOURCE_FOLDER);
			File result = new File(FileLocator.toFileURL(resource).getPath());

			return result;
		} catch (IOException e) {
			// TODO log error
			return null;
		}
	}
		
	private static LuaState newLuaState() {
		LuaState l = new LuaState();
		l.openLibs();

				
		// FIXME: JNLua uses a quite big buffer on output, remove it (for debug purposes only)
		getNestedField(l, "io", "stdout");
		l.getMetafield(-1, "setvbuf");
		l.pushValue(-2);
		l.pushString("no");
		l.call(2, 0);
		l.pop(1);

		JLuaFileSystem.register(l);
		
		// setup package.path by string concatenation
		l.getGlobal("package");
		// get separator. NOTE: package.config is *not* documented in Lua 5.1.x, but it is on Lua 5.2
		// see http://www.lua.org/work/doc/manual.html#pdf-package.config
		l.getField(-1, "config");
		String separator = l.toString(-1).split("\\n")[1];
		l.pop(1);

		// build package.path (loads both lua files and folder modules with a init.lua file)
		File path = sourcePath();
		l.pushString((new File(path, "?.lua").getAbsolutePath()) + separator + new File(path, "?/init.lua").getAbsolutePath());
		l.setField(-2, "path");
		// binary modules are not used, avoid any load
		l.pushString("");
		l.setField(-2, "cpath");

		l.pop(1); // pops package table
		
		l.getGlobal("require"); // global lookup is only done once

		for (String module : REQUIRED_MODULES) {
			l.pushValue(-1); // copy require
			l.pushString(module);
			l.call(1, 0);
		}
		l.pop(1); // pops require		
		
		Assert.isTrue(l.getTop() == 0);
		return l;
	}
	
	private LuaState l;
	
	public LuaState getLuaState() {
		return l;
	}

	public Luadoc() {
		l = newLuaState();
		Assert.isTrue(l.getTop() == 0);
	}
	
	public void generate (String sourceDirectory, String outputDirectory)
	{
		getNestedField(l, "eclipse","luadoc","generate");
		l.pushString(sourceDirectory);
		l.pushString(outputDirectory);
	    l.call(2,0);
	    
	}
	
	/**
	 * Helper to get a value nested into multiple tables. Resulting value is left on the top of the stack without.
	 * 
	 * TODO maybe move it to a more suitable place (LuaState extension ?)
	 * 
	 * @param l
	 *            State to work on
	 * @param fields
	 *            Table fields (first one is a global value)
	 */
	public static void getNestedField(LuaState l, String... fields) {
		int initialStackSize = l.getTop();

		l.pushValue(LuaState.GLOBALSINDEX);
		for (String f : fields) {
			l.getField(-1, f);
			l.replace(-2); // pops parent table
		}

		Assert.isTrue(l.getTop() == initialStackSize + 1);
	}
}
