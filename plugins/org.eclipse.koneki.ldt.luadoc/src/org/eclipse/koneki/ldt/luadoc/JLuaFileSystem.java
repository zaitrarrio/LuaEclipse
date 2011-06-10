package org.eclipse.koneki.ldt.luadoc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public class JLuaFileSystem {

	public static boolean register(LuaState L) {
		// check Luastate
		if (L == null) {
			return false;
		}

		// create module functions
		NamedJavaFunction[] lfsfunctions = new NamedJavaFunction[4];

		// declare mkdir function
		lfsfunctions[0] = new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				// get and check function parameters
				String dirname = l.checkString(1, null);
				if (dirname == null) {
					l.pushNil();
					l.pushString("Expected paramenter 'dirname'");
					return 2;
				}

				// create dir
				File file = new File(dirname.toString());
				if (file.mkdir()) {
					l.pushBoolean(true);
					return 1;
				} else {
					l.pushNil();
					l.pushString("Directory can not be created.");
					return 2;
				}
			}

			@Override
			public String getName() {
				return "mkdir";
			}
		};

		// declare currentdir function
		lfsfunctions[1] = new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				// create dir
				try {
					l.pushString(new File(".").getCanonicalPath());
					return 1;
				} catch (IOException e) {
					l.pushNil();
					l.pushString("currentdir failed : " + e.getMessage());
					return 2;
				}
			}

			@Override
			public String getName() {
				return "currentdir";
			}
		};

		// declare dir function
		lfsfunctions[2] = new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				// get and check function parameters
				String dirname = l.checkString(1, null);
				if (dirname == null) {
					l.pushNil();
					l.pushString("Expected paramenter 'dirname'");
					return 2;
				}

				File f = new File(dirname.toString());

				if (!f.exists()) {
					l.pushNil();
					l.pushString("cannot open " + f.getName()
							+ ": File not found");
					return 2;
				}

				if (!f.isDirectory()) {
					l.pushNil();
					l.pushString("cannot open " + f.getName()
							+ ": Not a directory");
					return 2;
				}

				List<?> ls = Arrays.asList(f.listFiles());
				Iterator<?> it = ls.iterator();

				JavaFunction fcn = new JavaFunction() {
					public int invoke(LuaState ls) {
						Iterator<?> i = ls.checkJavaObject(1, Iterator.class,
								null);
						if (i == null) {
							ls.pushNil();
							ls.pushString("Expected paramenter 'iterator'");
							return 2;
						}

						if (i.hasNext())
							ls.pushString(((File) i.next()).getName());
						else {
							ls.pushNil();
						}
						return 1;
					}
				};
				l.pushJavaFunction(fcn);
				l.pushJavaObject(it);
				l.pushNil();
				return 3;
			}

			@Override
			public String getName() {
				return "dir";
			}
		};

		// declare attributes function
		lfsfunctions[3] = new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				// get and check function parameters
				String dirname = l.checkString(1, null);
				if (dirname == null) {
					l.pushNil();
					l.pushString("Expected paramenter 'dirname'");
					return 2;
				}

				File f = new File(dirname.toString());

				if (!f.exists()) {
					return 0;
				}
				
				l.newTable();

				if (f.isDirectory())
					l.pushString("directory");
				else if (f.isFile())
					l.pushString("file");
				else {
					return 0;
				}
				l.setField(-2, "mode");

				l.pushNumber(f.length());
				l.setField(-2, "size");

				return 1;
			}

			@Override
			public String getName() {
				return "attributes";
			}
		};

		// register module with its functions
		L.register("lfs", lfsfunctions);
		L.pop(1);
		return true;
	}
}