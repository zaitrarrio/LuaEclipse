/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *     Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 *******************************************************************************/
package org.eclipse.koneki.ldt.editor.internal.completion;

import org.eclipse.dltk.ast.parser.ASTCacheManager;
import org.eclipse.dltk.ast.parser.IASTCache;
import org.eclipse.dltk.ast.parser.IASTCache.ASTCacheEntry;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.koneki.ldt.core.LuaLanguageToolkit;
import org.eclipse.koneki.ldt.editor.Activator;
import org.eclipse.koneki.ldt.editor.lang.Messages;

/**
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 * 
 */
public class LuaCompletionEngine extends ScriptCompletionEngine {

	@Override
	public void complete(IModuleSource module, int position, int k) {
		IModelElement modelElement = module.getModelElement();
		if (!(modelElement instanceof ISourceModule)) {
			// TODO log error
		} else {
			try {
				final String start = getWordStarting(module.getSourceContents(), position, 10).toLowerCase();
				this.actualCompletionPosition = position;
				this.offset = actualCompletionPosition - start.length();
				this.requestor.beginReporting();

				ISourceModule sourceModule = (ISourceModule) modelElement;

				// search local declaration in AST
				addLocalDeclarations(sourceModule, start);

				// search global declaration in DLTK model
				addGlobalDeclarations(sourceModule, start);

				// add keywords
				addKeywords(start);
			} catch (ModelException e) {
				Activator.logError(Messages.LuaCompletionEngineIniTialization, e);
			} finally {
				this.requestor.endReporting();
			}
		}
	}

	private void addGlobalDeclarations(ISourceModule sourceModule, final String start) throws ModelException {

		IScriptProject project = sourceModule.getScriptProject();
		IProjectFragment[] allProjectFragments = project.getAllProjectFragments();
		for (IProjectFragment iProjectFragment : allProjectFragments) {
			iProjectFragment.accept(new IModelElementVisitor() {
				public boolean visit(IModelElement element) {
					String lowerName = element.getElementName().toLowerCase();
					if (element.getElementType() > IModelElement.SOURCE_MODULE && lowerName.startsWith(start)) {
						createProposal(element.getElementName(), element);
					}
					return true;
				}
			});
		}
	}

	private void addKeywords(String start) {
		String[] keywords = new String[] { "and", "break", "do", "else", "elseif", "end", "false", "in", "nil", "not", "or", "return", "then",
				"true", };
		for (int j = 0; j < keywords.length; j++) {
			if (keywords[j].startsWith(start)) {
				createProposal(keywords[j], null);
			}
		}
	}

	// @Override
	// public void complete2(IModuleSource module, int position, int k) {
	// IASTCache[] providers = ASTCacheManager.getProviders(LuaLanguageToolkit.getDefault().getNatureId());
	// IModuleDeclaration moduleDeclaration;
	// if (providers != null) {
	// for (IASTCache provider : providers) {
	// IModelElement modelElement = module.getModelElement();
	// // TODO check if it's a ISourceModule
	// ASTCacheEntry cacheEntry = provider.restoreModule((ISourceModule) modelElement);
	// moduleDeclaration = cacheEntry.module;
	// }
	// }
	//
	// final String start = getWordStarting(module.getSourceContents(), position, 10).toLowerCase();
	// this.actualCompletionPosition = position;
	// this.offset = actualCompletionPosition - start.length();
	// this.setSourceRange(this.offset, position);
	// this.requestor.beginReporting();
	// // Some keywords are not listed hereby, they are defined in default templates
	// String[] keywords = new String[] { "and", "break", "do", "else", "elseif", "end", "false", "in", "nil", "not", "or", "return", "then",
	// "true", };
	// for (int j = 0; j < keywords.length; j++) {
	// if (keywords[j].startsWith(start)) {
	// createProposal(keywords[j], null);
	// }
	// }
	//
	// // Completion for model elements.
	// try {
	// module.getModelElement().accept(new IModelElementVisitor() {
	// public boolean visit(IModelElement element) {
	// String lowerName = element.getElementName().toLowerCase();
	// if (element.getElementType() > IModelElement.SOURCE_MODULE && lowerName.startsWith(start)) {
	// createProposal(element.getElementName(), element);
	// }
	// return true;
	// }
	// });
	// } catch (ModelException e) {
	// Activator.logError(Messages.LuaCompletionEngineIniTialization, e);
	// } finally {
	// this.requestor.endReporting();
	// }
	// }

	private void addLocalDeclarations(ISourceModule sourceModule, String start) {
		IASTCache[] providers = ASTCacheManager.getProviders(LuaLanguageToolkit.getDefault().getNatureId());
		if (providers != null) {
			for (IASTCache provider : providers) {
				ASTCacheEntry cacheEntry = provider.restoreModule(sourceModule);
				IModuleDeclaration moduleDeclaration = cacheEntry.module;
				if (moduleDeclaration != null) {
					// TODO create proposal
					break;
				}
			}
		}
	}

	private void createProposal(String name, IModelElement element) {
		CompletionProposal proposal = null;
		int relevance = 2;
		try {
			if (element == null) {
				proposal = this.createProposal(CompletionProposal.KEYWORD, this.actualCompletionPosition);
			} else {
				switch (element.getElementType()) {
				case IModelElement.METHOD:
					proposal = this.createProposal(CompletionProposal.METHOD_REF, this.actualCompletionPosition);
					IMethod method = (IMethod) element;
					proposal.setParameterNames(method.getParameterNames());
					proposal.setFlags(method.getFlags());
					break;
				case IModelElement.FIELD:
					proposal = this.createProposal(CompletionProposal.FIELD_REF, this.actualCompletionPosition);
					proposal.setFlags(((IField) element).getFlags());
					break;
				case IModelElement.TYPE:
					proposal = this.createProposal(CompletionProposal.TYPE_REF, this.actualCompletionPosition);
					proposal.setFlags(((IType) element).getFlags());
					break;
				case IModelElement.LOCAL_VARIABLE:
					proposal = this.createProposal(CompletionProposal.LOCAL_VARIABLE_REF, this.actualCompletionPosition);
					proposal.setFlags(Flags.AccPrivate);
					break;
				default:
					// Lower relevance for key words
					relevance = 1;
					proposal = this.createProposal(CompletionProposal.KEYWORD, this.actualCompletionPosition);
					proposal.setFlags(Flags.AccDefault);
					break;
				}
			}
			proposal.setModelElement(element);
			proposal.setName(name);
			proposal.setCompletion(name);
			// proposal.setReplaceRange(this.startPosition - this.offset, this.endPosition - this.offset);
			proposal.setReplaceRange(offset, offset + name.length());
			proposal.setRelevance(relevance);
			this.requestor.accept(proposal);
		} catch (ModelException e) {
			Activator.logWarning("Problem during completion processing.", e); //$NON-NLS-1$
		}
	}

	private String getWordStarting(String content, int position, int maxLen) {
		if (position <= 0 || position > content.length())
			return Util.EMPTY_STRING;
		final int original = position;
		while (position > 0
				&& maxLen > 0
				&& ((content.charAt(position - 1) == ':') || (content.charAt(position - 1) == '\'') || (content.charAt(position - 1) == '"') || isLessStrictIdentifierCharacter(content
						.charAt(position - 1)))) {
			--position;
			--maxLen;
		}
		return content.substring(position, original);
	}

	private static boolean isLessStrictIdentifierCharacter(char ch) {
		boolean isLowercaseLetter = ch >= 'a' && ch <= 'z';
		boolean isUppercaseLetter = ch >= 'A' && ch <= 'Z';
		return isLowercaseLetter || isUppercaseLetter || ch == '_';
	}
}
