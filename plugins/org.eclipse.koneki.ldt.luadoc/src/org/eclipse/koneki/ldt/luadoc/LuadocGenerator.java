/*
 * Copyright (C) 2003-2007 Kepler Project. Permission is hereby granted, free of
 * charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions: The above copyright notice and this permission
 * notice shall be included in all copies or substantial portions of the
 * Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.eclipse.koneki.ldt.luadoc;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;

/**
 * Runs a luadoc engine to generate entries.
 * 
 * @author jasonsantos
 * @version $Id$
 */

public class LuadocGenerator {
	private static LuadocGenerator		singleton;
	protected Map<String, ILuaEntry>	luaEntryIndex;
	private LuaState l;

	public LuadocGenerator() {
		super();
		luaEntryIndex = new HashMap<String, ILuaEntry>();
		
		Luadoc luadoc = new Luadoc();
		
		l = luadoc.getLuaState();
		//add generatedocentry function
		l.pushJavaFunction(new JavaFunction() {
			
			@Override
			public int invoke(LuaState L) {
				// String t = getParam(1).toString();
				String fileOrModuleName = L.checkString(1);

				String entryName =  L.checkString(2);
				String entryType =  L.checkString(3);

				String entrySummary =  L.checkString(4);
				String entryDescription =  L.checkString(5);
				String entryComment =  L.checkString(6);
				String entryHTML =  L.checkString(7);

				LuadocEntry e = (LuadocEntry) createLuaEntry(
						fileOrModuleName.toString(), entryName.toString(),
						entryType.toString(), entrySummary.toString(),
						entryDescription.toString(), entryComment
								.toString(), entryHTML.toString());

				luaEntryIndex.put(entryName.toString(), e);
				return 0;
			}
		});
		
		l.setGlobal("addDocumentationEntry");
		
		l.pushJavaFunction(new JavaFunction() {
			
			@Override
			public int invoke(LuaState L) {
				String sFileName = l.checkString(1);

				URL u = ResourceUtils.findmodule(sFileName,
						"org.eclipse.koneki.ldt.luadoc");
				try {
					if (u != null) {
						L.pushString(ResourceUtils.getFileContents(u));
					} else {
						l.pushNil();
						String err = "error loading file " + sFileName
								+ " from resource " + u.getPath() + ":\n\t"
								+ L.toString(-1);
						System.out.println(err);
						L.pushString(err);
						return 2;
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return 1;
			}
		});

		l.setGlobal("getfilecontents"); 
	}

	public static LuadocGenerator getInstance() {
		if (singleton == null)
			singleton = new LuadocGenerator();
		return singleton;
	}

	public Map<String, ILuaEntry> generate(String fileName) {
		Luadoc.getNestedField(l, "eclipse","luadoc","generateDocEntry");
		l.pushString(fileName);
	    l.call(1,0);
	    
	    return getLuaEntryIndex();
	}
	

	public Map<String, ILuaEntry> getLuaEntryIndex() {
		return luaEntryIndex;
	}

	public ILuaEntry getBestEntryIndex(String token) {
		Map<String, ILuaEntry> index = getLuaEntryIndex();
		ILuaEntry entry;
		token = token.replaceAll("[:]", ".");
		while ((entry = index.get(token)) == null && token.indexOf('.') > 1)
			token = token.substring(token.indexOf('.') + 1);

		return entry;
	}

	public void generateIndexes(Map<String, ILuaEntry> generatedEntries) {
		for (String s : generatedEntries.keySet()) {
			// store every entry into the generator's flat index
			// --- "by my hand ish ill done"

			// TODO: create a way of navigating module dependencies to determine
			// priority for selecting symbols

			getLuaEntryIndex().put(s, generatedEntries.get(s));
		}
	}

	public String getDocumentationText(String token) {
		LuadocEntry l = (LuadocEntry) getBestEntryIndex(token);
		String doc = null;
		if (l != null) {

			doc = l.getHtml();

			// TODO: enhance the non-summary value with module information
			if (doc == null || doc.length() == 0)
				doc = l.getComment();

			if (doc == null || doc.length() == 0)
				doc = l.getName();
		}
		return doc;
	}

	/**
	 * @param fileOrModuleName
	 * @param entryName
	 * @param entryType
	 * @param entrySummary
	 * @param entryDescription
	 * @param entryComment
	 * @param entryHTML
	 * @return
	 */
	public ILuaEntry createLuaEntry(String fileOrModuleName, String entryName,
			String entryType, String entrySummary, String entryDescription,
			String entryComment, String entryHTML) {
		LuadocEntry e = new LuadocEntry();

		e.setModule(fileOrModuleName);
		e.setEntryType(entryType);
		e.setName(entryName);
		e.setSummary(entrySummary);
		e.setDescription(entryDescription);
		e.setComment(entryComment);
		e.setHTML(entryHTML);
		return e;
	}
}
