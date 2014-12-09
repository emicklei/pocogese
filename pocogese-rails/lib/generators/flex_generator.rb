require 'fileutils'
# add ApplicationController, superclass of all ActionControllers
require 'application'

# add current path so relative requires work
$LOAD_PATH << File.expand_path(File.dirname(__FILE__))
require 'flex_template_processor'
require 'flex_controller_generator'

  
module Pocogese
# Class FlexGenerator is a development tool to generate ActionScript class and Flex components 
# using meta information available and provided by the script that invokes the generator
# Database connection is required because it reflects on Rails Controllers
# 
# Author: ernest.micklei@philemonworks.com, 2007
# License: Apache 2.0
# 
class FlexGenerator
  attr_accessor :source, :target , :package_root
  
  def controller_from(class_sym,options = {})
    FlexControllerGenerator.new(@source,@target,@package_root || 'controllers').controller(class_sym,options)
  end
  
end # class

end #module

# DSL globals
@@flexgen = Pocogese::FlexGenerator.new


def project(path)
  @@flexgen.target = path
  yield if block_given?
end

def package(dottedName)
  @@flexgen.package_root = dottedName
  yield if block_given?
end

def controller(class_sym,options = {})
  @@flexgen.controller_from(class_sym,options)
end

## DEPRECATED ########################################

# DSL functions
def rails_project_root(path)
  puts 'call to DEPRECATED method in FlexGenerator'
  @@flexgen.source = path
end

def flex_project_root(path)
  puts 'call to DEPRECATED method in FlexGenerator'
  @@flexgen.target = path
end

def flex_package_root(dottedName)
  puts 'call to DEPRECATED method in FlexGenerator'
  @@flexgen.package_root = dottedName
end

def flex_controller_from(class_sym,options = {})
  puts 'call to DEPRECATED method in FlexGenerator'
  @@flexgen.controller_from(class_sym,options)
end
