# add current path so relative requires work
$LOAD_PATH << File.expand_path(File.dirname(__FILE__))

# new classes
require 'lib/command'
require 'lib/selection'
require 'lib/reply'
require 'lib/request_fault'
require 'lib/page'
#require 'lib/http_client'

# mixins
require 'lib/pocogese'

# class and library extensions
require 'lib/extensions/string_collection'
require 'lib/extensions/string_dictionary'
require 'lib/extensions/literal'
require 'lib/extensions/hash'

# Inject includes for Pocogese libraries
ActionController::Base.send(:include, Pocogese)