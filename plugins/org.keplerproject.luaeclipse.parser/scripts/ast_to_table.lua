
--[[
local oldreq=require
local n=1
function require(...)
	local l=package.loaded[...]
	if l then return l end
	print(string.rep("| ",n), "BEGIN", ...)
	n=n+1
	local r = oldreq(...)
	n=n-1
	print(string.rep("| ",n), "END", ...)
	return r	
end
local path = "/home/kkinfoo/dev/luaEclipse/LuaEclipseM6/plugins/org.keplerproject.luaeclipse.metalua.32bits/"
package.path = path.."?.luac;"..path.."?.lua;"..package.path 
]]
--print (package.path)
--local path = "?.luac;?.lua"
--package.path = package.path ..path.."?.luac"..path.."?.lua"

--for _, name in ipairs{'path','cpath','mpath'} do
--	print('package.' .. name .. ' = ' .. (package[name] or 'nil'))
--end 

--package.path = "?.luac;?.lua;/usr/lib/lua/5.1/?.luac;/usr/lib/lua/5.1/?.lua;/home/kkinfoo/dev/luaEclipse/LuaEclipseM6/plugins/org.keplerproject.luaeclipse.metalua.32bits/?.luac;/home/kkinfoo/dev/luaEclipse/LuaEclipseM6/plugins/org.keplerproject.luaeclipse.metalua.32bits/?.lua"


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
require 'metalua.compiler'
require 'metalua.walk.bindings'
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
local publicDeclarationsIDs = {}
local localDeclarationsIDs = {}
local parent = {}

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
	local function registered( node )
		assert(
			type(node) == 'table',
			"Node of 'table' type expected.'"..type(node).."' found"
		)
		return nodeToId[ node ] ~= nil
	end
	local function cacheDeclaration(hash, t)
		assert( type(hash) == 'table' )
		assert( type(t)    == 'table' )
		for declaration, occurrences in pairs(t) do
			if registered(declaration) then
				local id = nodeToId[ declaration ]
				table.insert(hash, id)
			end
		end
	end
	local function cacheGlobalDeclaration(hash, t)
		assert( type(hash) == 'table' )
		assert( type(t)    == 'table' )
		for name, occurences in pairs( t ) do
			for k, node in pairs( occurences ) do
				if registered(node) then
				--local id = nodeToId[ node ]
					table.insert(hash, nodeToId[ node ])
				end
			end
		end
	end

	-- Sort variables in source 
	assert(
		type(ast) == 'table',
		"AST of 'table' type expected, "..type(ast).." found."
	)

	local declareds, leftovers = bindings( ast )
	cacheDeclaration(localDeclarationsIDs, declareds)
	cacheGlobalDeclaration(publicDeclarationsIDs, leftovers)
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
	    	if type(v) == "table" then
	      		doIndex( v )
	    	end
		end
	end
  	local function rememberParents( id )
  		assert( type(id) == 'number' )
  		for k, child in pairs( children(id) ) do
  			table.insert(parent, child, id)
  			rememberParents(child)
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
 	-- Append declaration data on declaration nodes
	--
	publicDeclarationsIDs = {}
	localDeclarationsIDs = {}
	matchDeclaration( ast )
	if #idToNode > 0 then
		rememberParents( 1 )
	end
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

function hasUsage( id )
	assert(type(id) == 'number', "Number expected '"..type(id).."' found.")
	local doesNodeExists = type(idToNode[ id ]) == 'table'
	return hasDeclarations( id )and #(idToNode[ id ].occurrences) > 0
end

function getOccurrencies( id )
	--
	--	Find declarations assotiated with this ID
	--
	assert(type(id) == 'number', "Number expected '"..type(id).."' found.")
	local ids = {}
	
	-- Fetch available declarations
	if hasDeclarations(id) then
		-- Search for nodes assotiated with declaration is hash map
		local occur = idToNode[ id ].occurrences
		assert( occur ~= nil )
		for k, v in pairs( occur ) do
			-- Register every occurence defnition in table
			if type(v) == 'table' then
				for k, occ in ipairs( v ) do
					table.insert(ids, occ)
				end
			end
		end
	end
	-- Return empty table where no declarations are available
	return ids
end

function getDeclarationsIDs ()
	return localDeclarationsIDs,#localDeclarationsIDs
end

function getGlobalDeclarationsIDs ()
	return publicDeclarationsIDs,#publicDeclarationsIDs
end
function getParent(id)
	if type(parent[id]) == 'number' then
		return parent[ id ]
	end
	return nil
end
--[[
function getRelatedIDs ()
	local function concatTable (main, t)
		assert( type(main)	== 'table' )
		assert( type(t)		== 'table' )
		for k,v in pairs( t ) do
			table.insert(main, v)
		end
		return main
	end
	local function extractChildrenFromID( ids )
		assert( type(ids) == 'table' )
		local child = {}
		for k,id in pairs( ids ) do
			local nodes = children(id)
			for k, related in pairs(nodes) do
				table.insert(child, related)
			end
		end
		return child
	end
	local declared = getDeclarationsIDs()
	local free = getGlobalDeclarationsIDs()
	local ids = concatTable (declared, free)
	ids = extractChildrenFromID( ids )
	return ids,#ids
end
]]
--[[ ---------------------------------------------------------------------------


local path = "/home/kkinfoo/dev/luaEclipse/LuaEclipseM6/plugins/org.keplerproject.luaeclipse.metalua.32bits/"
package.path = path.."?.luac;"..path.."?.lua;"..package.path 
--print (package.path)
local source = "tab = {} local var"--" local yy , xx = {}, function()end xx()"
require 'metalua.compiler'
local ast = mlc.luastring_to_ast( source )
--table.print(ast)
index( ast )
print ( source )
table.print(parent)
]]
