local path = [[;/home/kkinfoo/dev/luaEclipse/LuaEclipseM6/plugins/org.keplerproject.luaeclipse.metalua.32bits/]]
package.path = package.path ..path.."?.lua"..path.."?.luac"
--
-- The aim of this package is to binf `Ids occurences to their declaration
--
require 'metalua.walk.bindings'

local function dump( var )
	assert( type(var) == "table", "Table expected.")
	local function _dump( t, spaces )
		for k,v in pairs(t) do
			print (spaces .."["..tostring(k).."]\t"..tostring(v) )
			if type(k) == "table" then
				_dump(k, spaces.."\t")
			end
			if type(v) == "table" then
				_dump(v, spaces.."\t")
			end
		end
	end
	_dump(var, "")
end
local function analyze(source)
	-- Type check
	assert(
		type(source)=="string",
		"Source code to analyze should be typed as string."
	)
	--
	-- Parse provided source
	--
	require 'metalua.mlc'
	ast = mlc.luastring_to_ast(source)
	-- Sort variables in source 
	local declared, leftovers = bindings( ast )
	return declared
end
local bind = {
	declaredIDs = function ( source )
		-- Type check
		assert(
			type(source)=="string",
			"Source code to analyze should be typed as string."
		)
		declarations = analyse( source )
		local ids, i= {}, 1
	end
}
return bind
