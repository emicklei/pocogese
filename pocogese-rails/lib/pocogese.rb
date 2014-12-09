require 'modules/selection_api'
require 'modules/command_api'
require 'modules/command_selection'
require 'modules/save_find_destroy'
require 'modules/self_documentation'

module Pocogese
  
  def self.included(base) 
    # Add command_api availability by extending the module that owns the function.
    base.extend CommandAPI
    base.extend SelectionAPI
    base.extend ModelRegistry
    #puts "=> Pocogese plugin initialized"
  end 
  
  module ModelRegistry
  
    def pocogese(model_sym = nil, options = {})
      if (!model_sym.nil?)
        # self.logger.debug "[pocogese] #{self} serves instances of: #{model_sym}"
        Pocogese::ModelRegistry.model_registry[self.name] = model_sym
      end
      unless self.method_defined?(:doCommand)          
        class_eval 'include Pocogese::DoCommandDoSelection' 
        class_eval 'include Pocogese::SelfDocumentation'         
        class_eval 'include Pocogese::SaveFindDestroyMethods' if !model_sym.nil? 
      end
    end
    
    def self.model_registry
      @@pocogese_models ||= {}
    end
    
    def self.model_served_by(controller_name)
      self.model_registry[controller_name.to_s]      
    end
    
    def self.controller_serving(model_sym)
      Pocogese::ModelRegistry.model_registry.keys_at_value(model_sym).first
    end
    
  end #ModelRegistry

end # Pocogese