require 'lunatest'
local function parse( source )
	if type(source) ~= 'string' then return false, "'string' expected" end
	require 'metalua.compiler'
	local tree = mlc.luastring_to_ast( source )
	return pcall( index, tree )
end

function test_set()
	local status, err = parse ("set = nil")
	assert_true(status, err)
end
