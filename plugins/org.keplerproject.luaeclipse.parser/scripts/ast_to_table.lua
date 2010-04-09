-------------------------------------------------------------------------------
--  Copyright (c) 2009 KeplerProject, Sierra Wireless.
--  All rights reserved. This program and the accompanying materials
--  are made available under the terms of the Eclipse Public License v1.0
--  which accompanies this distribution, and is available at
--  http://www.eclipse.org/legal/epl-v10.html
-- 
--  Contributors:
--       Kevin KIN-FOO <kkin-foo@sierrawireless.com>
--           - initial API and implementation and initial documentation
---------------------------------------------------------------------------------
local function dump( var )
	local function _dump( t, spaces )
		if type(t) == 'table' then 
			for k,v in pairs(t) do
				_dump(k, spaces.."\t")
				_dump(v, spaces.."\t")
			end
		else
			print (spaces .. type(t)..'('..tostring(t)..')')
		end
	end
	_dump(var, "")
end
--
-- Thoses are hashmaps to link nodes to their ID,
-- in order to enable direct access, time efficient
--
local idToNode = {}
local nodeToId = {}

--
-- Provides distinct IDs calls after calls
-- @return int
--
local id = 0
local function getID()
  id = id + 1
  return id
end

--
--
--
local function matchDeclaration( ast )
	--
	--	Checks if node is already indexed in internal hashmaps
	--	@param node	table Node to localize in hashmaps
	--
	local function registrated( node )
		assert(type(node) == 'table', "Node of 'table' type expected.")
		return nodeToId[ node ] ~= nil
	end
	local function store( node, occurrences )
		assert( type(occurrences) == 'table' )
		local id = nodeToId[ node ]
		idToNode[ id ]['occurrences'] = occurrences
	end 

	-- Sort variables in source 
	assert(
		type(ast) == 'table',
		"AST of 'table' type expected, "..type(ast).." found."
	)
	require 'metalua.walk.bindings'
	local declared, leftovers = bindings( ast )
	for declaration, occurrences in pairs(declared) do
		if registrated(declaration) then
			store(declaration, occurrences)
			dump( occurrences )
		end
	end
end

--
-- Assign an ID to every node in the AST
-- 
-- Assign an id to every table and sub-table of the given one,
-- except the ones named "lineinfo"
--
-- @param ADT to index
--
function index( ast )
 	local function doIndex( adt )
		local function childNodes( ast )
			local nodes = {}
	    	for k, v in ipairs( ast ) do
	      		if type(v) == "table" then
					nodes[ #nodes + 1 ] = v
	      		end
	    	end
	    	return nodes
	  	end
 		-- Index node
		local id = getID()
		idToNode[ id ] = adt
	 	nodeToId[ adt ] = id

		-- Index child nodes
		for k,v in ipairs( childNodes(adt) )do
	    	if ( type(v) == "table" ) then
	      		doIndex( v )
	    	end
		end
	end
	--
	-- Flush previous indexes to ensure consistance
	--
 	idToNode = {}
 	nodeToId = {}

 	--
 	-- Reference nodes in hash table in order to have direct access
	--
 	doIndex( ast )

	--
 	-- Apprend declaration data on declaration nodes
	--
	matchDeclaration ( ast )
end



--
-- Retrurn children of a node
--
-- @param  int	 ID of node to parse
-- @return table Child nodes
-- @return int	 Count of child nodes
--
function children( id )
  local child = {}
  for  k,v in ipairs( idToNode[ id ] ) do
    if ( type(v) == "table" and k ~= "lineinfo" ) then
      child[ #child + 1 ] = nodeToId[ v ]
    end
  end
  return child, #child
end

--
-- Lists key and value of asked node, exept tables
--
-- @param ast	Tree to parse
-- @param id	ID of node to describe in tree
--
function describe(ast, id)
  -- Use root if no index is given
  local node = id ~=nil and find_node(ast,id) or ast

  -- Make sure we deal with an array
  assert( type(node) == "table" )
  for key,value in pairs( node ) do
   if ( type(value) ~= "table" ) then
      print( key )
      print( value )
   end
  end
end
-- 
-- Get node's tag
--
-- @param  Number ID of node of requested tag
-- @return String
--
function getTag( id )
  return idToNode[id]["tag"]
end

function getNode( id )
  return idToNode[ id ]
end

-- 
-- Get node's value
--
-- @param int ID of requested node
-- 
function getValue( id )
  return idToNode[ id ][ 1 ]
end

-- 
-- Get node's start position in source
--
-- @param int ID of requested node
-- 
function getStart( id )
  return tonumber(idToNode[ id ][ 'lineinfo' ]['first'][3])
end

-- 
-- Get node's end position in source
--
-- @param int ID of requested node
-- 
function getEnd( id )
  assert (
    type(idToNode[ id ].lineinfo) == "table",
    "No line info for node "..id
  )
  return tonumber(idToNode[ id ][ 'lineinfo' ]['last'][3])
end

--
-- Indicates if line informations are available for a node
-- @param id	ID of requested node
-- 
function hasLineInfo( id )
  return type(idToNode[ id ].lineinfo) == "table"
end

function hasDeclarations( id )
	assert(type(id) == 'number', "Number expected '"..type(id).."' found.")
	local doesNodeExists = type(idToNode[ id ]) == 'table'
	return doesNodeExists and type(idToNode[ id ].occurrences) == 'table'
end

function getDeclarationsIDs( id )
	assert(type(id) == 'number', "Number expected '"..type(id).."' found.")
	-- Return empty table where no declarations are available
	local ids = {}
	if hasDeclarations(id) then
		local declarations = idToNode[ id ].declarations
		-- Search for nodes assotiated with declaration is hash map
		dump(idToNode[ id ].occurrences)
	end
	return ids
end
--[[ ---------------------------------------------------------------------------
]]
local path = ";/home/kkinfoo/dev/luaEclipse/LuaEclipseM6/plugins/org.keplerproject.luaeclipse.metalua.32bits/"
package.path = package.path ..path.."?.lua"..path.."?.luac"

require 'metalua.compiler'
local ast = mlc.luastring_to_ast( " local yy , xx= {}, function()end xx()" )

index( ast )
for k=0, id do
	getDeclarationsIDs( k )
end
