package org.keplerproject.luaeclipse.internal.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.statements.Statement;
import org.keplerproject.luaeclipse.parser.ast.declarations.TableDeclaration;
import org.keplerproject.luaeclipse.parser.ast.expressions.Identifier;
import org.keplerproject.luaeclipse.parser.ast.expressions.Pair;
import org.keplerproject.luaeclipse.parser.ast.statements.BinaryStatement;
import org.keplerproject.luaeclipse.parser.ast.statements.Chunk;
import org.keplerproject.luaeclipse.parser.ast.statements.Local;
import org.keplerproject.luaeclipse.parser.ast.statements.LocalRec;
import org.keplerproject.luaeclipse.parser.ast.statements.Set;

public class DeclarationBinder {
	private List<Long> localsIDs;

	private List<Long> globalsIDs;

	private List<Long> parentsIDs;
	private Map<Long, Statement> declaration;

	public DeclarationBinder(List<Long> declared, List<Long> free,
			List<Long> parents) {
		this.localsIDs = declared;
		this.globalsIDs = free;
		this.parentsIDs = parents;
		this.declaration = new HashMap<Long, Statement>();
	}

	private void rememberDeclaration(long id, Statement s) {
		this.declaration.put(id, s);
	}

	private void matchDeclarations(Chunk ids, Chunk values, int mod) {
		matchDeclarations(ids, values, mod, false);
	}

	private void matchDeclarations(Chunk ids, Chunk values, int mod,
			boolean declareAsLocal) {
		/*
		 * Ensure to loop as less as possible to avoid out of range access
		 */
		int limit;
		if (ids.getChilds().size() > values.getChilds().size()) {
			limit = values.getChilds().size();
		} else {
			limit = ids.getChilds().size();
		}

		/*
		 * Declare variables
		 */
		for (int c = 0; c < limit; c++) {
			Statement s = (Statement) values.getChilds().get(c);
			if (s instanceof Declaration) {
				Identifier id = (Identifier) ids.getChilds().get(c);
				backPatchDeclarationName(id, (Declaration) s, mod);
			}
		}
	}

	private void matchTable(TableDeclaration table) {
		for (Object o : table.getStatements()) {
			if (o instanceof Pair) {
				Pair pair = (Pair) o;
				if (pair.getData() instanceof Declaration) {
					Declaration dec = (Declaration) pair.getData();
					backPatchDeclarationName(pair, dec);
				}
			}
		}

	}

	private void backPatchDeclarationName(SimpleReference reference,
			Declaration declaration) {
		backPatchDeclarationName(reference, declaration, Declaration.AccPublic);
	}

	private void backPatchDeclarationName(SimpleReference reference,
			Declaration declaration, int modifier) {
		declaration.setName(reference.getName());
		declaration.setNameEnd(reference.sourceEnd());
		declaration.setNameStart(reference.sourceStart());
		declaration.setModifier(modifier);
	}

	public void toBind(Declaration d) {

	}

	public boolean isLinked(long id) {
		return this.localsIDs.contains(id);
	}

	public boolean isFree(long id) {
		return this.globalsIDs.contains(id);
	}

	public boolean isDeclaration(long id) {
		return isLinked(id) || isFree(id);
	}

	public boolean isDeclaration(Statement s) {
		return declaration.containsValue(s);
	}

	public boolean isParent(long id) {
		return this.parentsIDs.contains(id);
	}

	// public boolean isValuable(long id) {
	// return isDeclaration(id) || isParent(id);
	// }

	public void bind(Statement s, long id) {
		if (isParent(id)) {
			System.out.println("[parent]" + s.getClass().getName());
		} else if (isDeclaration(id)) {
			rememberDeclaration(id, s);
			System.out.println("[declaration]" + s.getClass().getName());
			if (s instanceof BinaryStatement) {
				int mod;
				if (s instanceof Local) {
					mod = Declaration.AccPrivate;
				} else {
					mod = Declaration.AccPublic;
				}
				BinaryStatement b = (BinaryStatement) s;
				System.out.println(s.getClass().getName());
				if (b.getRight() != null) {
					matchDeclarations(b.getLeft(), b.getRight(), mod, true);
				}
			}
		} else if (s instanceof TableDeclaration) {
			// Search for pairs
			TableDeclaration table = (TableDeclaration) s;
			matchTable(table);
		}
		System.err.println("[regular]" + s.getClass().getName());
	}

	public void bind(Statement node, long id, List<Long> childNodes) {
		/*
		 * Most of the time a Lua declarations are nameless. As instance, a
		 * function can be assigned to a variable and the assigned to another
		 * without any kind of trouble.
		 * 
		 * That's why in order to be able to detect which identifier is used it
		 * is necessary to parse the parent node that contains both declaration
		 * and assignment variable
		 */
		for (Long child : childNodes) {
			if (isParent(child)) {
				System.out.println("[greatparent]" + node.getClass().getName());
				if (node instanceof Set) {
					Set set = (Set) node;
					matchDeclarations(set.getLeft(), set.getRight(),
							Declaration.AccPublic);
				} else if (node instanceof LocalRec) {
					LocalRec rec = (LocalRec) node;
					matchDeclarations(rec.getLeft(), rec.getRight(),
							Declaration.AccPrivate);
				}
			}
		}
		bind(node, id);
	}
}
