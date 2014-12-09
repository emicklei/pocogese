require 'test/unit'

require 'rubygems'
require 'action_controller'
require 'action_view'
require 'active_support'
require 'active_record'

require File.expand_path(File.dirname(__FILE__)) + '/../init'

ActionController::Base.logger = Logger.new(STDOUT)

