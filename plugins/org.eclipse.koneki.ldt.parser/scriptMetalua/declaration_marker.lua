--------------------------------------------------------------------------------
--  Copyright (c) 2011 Sierra Wireless.
--  All rights reserved. This program and the accompanying materials
--  are made available under the terms of the Eclipse Public License v1.0
--  which accompanies this distribution, and is available at
--  http://www.eclipse.org/legal/epl-v10.html
-- 
--  Contributors:
--       Kevin KIN-FOO <kkinfoo@sierrawireless.com>
--           - initial API and implementation and initial documentation
--------------------------------------------------------------------------------

---
-- The aim of this module is to locate intersting nodes in AST then tag it with valuable information.
local mark = {}
---
-- Appends information on declaration nodes. The format of tagging is the folowing
-- <ul>
-- <li><strong>node.init:</strong>			Metalua node, representing initialisation value of declaration</li>
-- <li><strong>node.occurrences:</strong>	Table containing all references to declaration in current file</li>
-- <li><strong>node.scope:</strong>			String 'local' or 'global' according to scope of declaration</li>
-- <li><strong>node.type:</strong>			Indicates type of declaration, simply declaration's initialization value type</li>
-- </ul>
--
-- @param	ast	Metalua AST to browse for declarations
-- @return	Given AST but with declaration flagged
mark.declaration = function ( ast )
	require 'metalua.walk.bindings'
	local locals, globals = bindings( ast )
	--
	-- Dealing with explicits declarations ( often called local ones )
	--
	for node, namesAndOccurrences in pairs(locals) do
		--
		-- Local and Localrec
		--
		if node.tag == 'Local' or node.tag == 'Localrec' then
			local left, right = node[1], node[2]
			for k, identifier in ipairs(left)do
				-- There is an initialization
				if right and right[k] then
					identifier.init = right[k] or nil 
					identifier.type = right[k] and right[k].tag or nil 
				else
					-- No initialization available
					identifier.init = nil 
					identifier.type = nil 
				end
				-- Occurrences are index by variable names
				local identifierName = identifier[1]
				identifier.occurences = namesAndOccurrences[identifierName]
				-- All identifier are local with `Local and `Localrec
				identifier.scope = 'local'
			end
		end
--[[		for name, occurrences in pairs( namesAndOccurrences ) do
			for i, occurrence in pairs( occurrences ) do
				local meta  = {
					occurrences = occurrences,
				match node with
					| `Local {identifiers} ->
						object[ node ] = DLTK.Local(first, last, statListToChunk(identifiers))
					| `Local {identifiers, inits} ->
						object[ node ] = DLTK.Local(first, last, statListToChunk(identifiers), statListToChunk(inits))
					| `Localrec {identifiers} ->
						object[ node ] = DLTK.LocalRec(first, last, statListToChunk(identifiers))
					| `Localrec {identifiers, inits} ->
				end	
			end
		end]]
	end
	return ast
end
return mark
