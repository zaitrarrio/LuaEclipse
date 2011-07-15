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
	end
	return ast
end
---
-- Indicates wether given node represent a declaration or not
--
-- @param	node	Metalua node to check
-- @return true if node is defined and has a scope 
mark.is_declaration = function( node )
	return type(node)=='table' and node.scope
end
---
-- Provides type of initialization
--
-- @param node Metalua node to inspect
-- @return String representing initialization node type, such as 'Function', 'Table', and so on. Nil is returned when node is invalid
mark.declaration_type= function( node )
	return node and node.type or nil
end
---
-- Provides initialization node
--
-- @param node Metalua node representing Declaration
-- @return Table representing initialization node. Nil is returned when node is unavailable.
mark.declaration_initialization= function( node )
	return node and node.init or nil
end
---
-- Provides initialization node scope
--
-- @param node Metalua node representing Declaration
-- @return String representing initialization node scope 'local' or 'global'. Nil is returned when scope is unavailable.
mark.declaration_scope= function( node )
	return node and node.scope or nil
end
return mark
