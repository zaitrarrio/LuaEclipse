package org.keplerproject.luaeclipse.internal.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.Reference;
import org.eclipse.dltk.ast.statements.Statement;
import org.keplerproject.luaeclipse.parser.ast.declarations.LocalVariableDeclaration;
import org.keplerproject.luaeclipse.parser.ast.declarations.TableDeclaration;
import org.keplerproject.luaeclipse.parser.ast.declarations.TableField;
import org.keplerproject.luaeclipse.parser.ast.expressions.Identifier;
import org.keplerproject.luaeclipse.parser.ast.expressions.Index;
import org.keplerproject.luaeclipse.parser.ast.expressions.Pair;
import org.keplerproject.luaeclipse.parser.ast.statements.BinaryStatement;
import org.keplerproject.luaeclipse.parser.ast.statements.Chunk;
import org.keplerproject.luaeclipse.parser.ast.statements.Local;
import org.keplerproject.luaeclipse.parser.ast.statements.LocalRec;
import org.keplerproject.luaeclipse.parser.ast.statements.Set;

/**
 * Parsing in {@link NodeFactory} is top down, which means AST is recursively
 * constructed from leaves to root. Some information is lost due to top down
 * parsing. This class is for fetching this information back in order to
 * initialize outline decorators.
 * 
 * @author Kevin KIN-FOO<kevinkinfoo@gmail.com>
 * 
 */
public class DeclarationBinder {

	/** Local declaration nodes IDs */
	private List<Long> localsIDs;

	/** Global declaration node IDs */
	private List<Long> globalsIDs;

	/**
	 * IDs of parent nodes of declaration which allows dealing with both sides
	 * of {@linkplain BinaryStatement}
	 */
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

	/**
	 * Just like {@link #matchDeclarations(Chunk, Chunk, int, boolean)} but for
	 * global declarations.
	 * 
	 * @param ids
	 * @param values
	 * @param mod
	 */
	private void matchDeclarations(Chunk ids, Chunk values, int mod) {
		matchDeclarations(ids, values, mod, false);
	}

	/**
	 * In {@linkplain BinaryStatement} name declaration of right side with
	 * identifiers of left side.
	 * 
	 * @param ids
	 *            {@linkplain Chunk} gathering declaration identifiers
	 * @param values
	 *            {@linkplain Chunk} gathering values for
	 * @param mod
	 *            Integer combination allowing decorator selection
	 * @param declareAsLocal
	 *            true if matched declaration must appears as private statement
	 */
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
			Statement left = (Statement) ids.getChilds().get(c);
			if (s instanceof Declaration) {
				Identifier id = (Identifier) left;
				backPatchDeclarationName(id, (Declaration) s, mod);
			} else if (declareAsLocal && left instanceof Identifier) {
				Identifier id = (Identifier) left;
				LocalVariableDeclaration local = new LocalVariableDeclaration(
						id, id.sourceStart(), id.sourceEnd());
				ids.addStatement(local);

			}// TODO: avoid duplication
			if (left instanceof Index) {
				Index index = (Index) left;
				Expression e = rootContainer(index);
				e.getChilds();
			}
		}
	}

	private Expression rootContainer(Index index) {
		if (index.getContainer() instanceof Index) {
			return rootContainer(index);
		}
		return index.getContainer();
	}

	private Statement matchTable(TableDeclaration table) {
		Chunk body = new Chunk(table.sourceStart(), table.sourceEnd());
		for (Object o : table.getStatements()) {
			if (o instanceof Pair) {
				Pair pair = (Pair) o;
				if (pair.getData() instanceof Declaration) {
					Declaration dec = (Declaration) pair.getData();
					backPatchDeclarationName(pair, dec);
				} else {
					int start = pair.getData().sourceStart();
					int end = pair.sourceEnd();
					TableField dec = new TableField(pair, start, end);
					dec.setModifier(Declaration.AccPublic);
					o = dec;
				}
			}
			body.addStatement((Statement) o);
		}
		table.setBody(body);
		return table;
	}

	private void backPatchDeclarationName(Reference reference,
			Declaration declaration) {
		backPatchDeclarationName(reference, declaration, Declaration.AccPublic);
	}

	private void backPatchDeclarationName(Reference reference,
			Declaration declaration, int modifier) {
		declaration.setName(reference.getStringRepresentation());
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

	public Statement bind(Statement s, long id) {
		if (isParent(id)) {
			// System.out.print(s.getClass().getName());
		} else if (isDeclaration(id)) {
			// System.out.print(s.getClass().getName());
			rememberDeclaration(id, s);
			if (s instanceof BinaryStatement) {
				int mod;
				if (s instanceof Local) {
					mod = Declaration.AccPrivate;
				} else {
					mod = Declaration.AccPublic;
				}
				BinaryStatement b = (BinaryStatement) s;
				if (b.getRight() != null) {
					matchDeclarations(b.getLeft(), b.getRight(), mod, true);
				}
			}
			// System.out.println(s instanceof Identifier ? "["+((Identifier) s)
			// .getName()+"]" : "");
		} else if (s instanceof TableDeclaration) {
			// Search for pairs
			TableDeclaration table = (TableDeclaration) s;
			return matchTable(table);
		}
		return s;
	}

	public Statement bind(Statement node, long id, List<Long> childNodes) {
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
		return bind(node, id);
	}
}
