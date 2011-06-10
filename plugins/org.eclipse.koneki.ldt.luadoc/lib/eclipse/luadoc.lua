#!/usr/bin/env lua

local require = require
local luadoc=require"luadoc"

module ("eclipse.luadoc")


local function load_options (outputdir)
	local options = require "luadoc.config"
	options.output_dir = outputdir
	return options
end 

-------------------------------------------------------------------------------
-- Main function. Process command-line parameters and call luadoc processor.
function generate (sourcedir,outputdir)
	local files = { sourcedir}
	return luadoc.main(files, load_options(outputdir))
end

function generateDocEntry (filename)
 local files = {filename}
 local options = require "luadoc.config"
 options.doclet = 'eclipse.doclet'
 luadoc.main(files, options)
end