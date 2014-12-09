module Pocogese
  module SelectionAPI
    
    def selection_api *args
      # self.logger.debug "[pocogese] #{self} adds selection_api: #{args.join(',')}"
      Pocogese::SelectionAPI.register_selection(self.name,args[0],args[1..args.size])
      unless self.method_defined?(:doSelection)          
        class_eval 'include Pocogese::DoCommandDoSelection' 
        class_eval 'include Pocogese::SelfDocumentation'        
        class_eval 'include Pocogese::SaveFindDestroyMethods' 
      end
    end
  
    def self.register_selection(class_name,operation_name,parameter_names)
      class_ops = self.api_registry
      class_ops[class_name] = {} unless !class_ops[class_name].nil?
      op_params = class_ops[class_name]
      op_params[operation_name] = parameter_names 
    end
  
    # { controller_class_name_string => { selection_name_sym => parameter_name_sym_array }
    def self.api_registry
      @@pocogese_selection_apis ||= {}
    end
    
    # Return parameter values for a selection by looking at the Selection registry
    # Raise an exception if an attempt is made to invoke an unregistered method
    def self.ordered_parameter_values(controller,method_sym,parameter_hash)
      parameter_syms = self.api_registry[controller.class.name][method_sym]
      raise "Method: #{method_sym} is not registered as a selection_api for #{controller}" if parameter_syms.nil?
      parameter_syms.collect{|each| parameter_hash[each.to_s]}
    end    
    
  end # SelectionAPI
end # Pocogese