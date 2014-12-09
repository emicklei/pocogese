module Pocogese
  module CommandAPI  
 
    def command_api *args
       self.logger.debug "[pocogese] #{self} adds command_api: #{args.join(',')}"    
      Pocogese::CommandAPI.register_command(self.name,args[0],args[1..args.size])
    end
    
    def self.register_command(class_name,operation_name,parameter_names)
      class_ops = self.api_registry
      class_ops[class_name] = {} unless !class_ops[class_name].nil?
      op_params = class_ops[class_name]
      op_params[operation_name] = parameter_names 
    end

    # { controller_class_name_string => { command_name_sym => parameter_name_sym_array }
    def self.api_registry
      @@pocogese_command_apis ||= {}
    end
    
    # Return parameter values for a command by looking at the Command registry
    def self.ordered_parameter_values(controller,command)
      parameter_syms = self.api_registry[controller.class.name][command.name.to_sym]
      parameter_syms.collect{|each| command.parameters[each.to_s] }
    end    
    
  end # CommandAPI
end #Pocogese