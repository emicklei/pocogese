module Pocogese
  
  # Author: ernest.micklei@philemonworks.com, 2007
  # License: Apache 2.0
  class FlexControllerGenerator < TemplateProcessor
    
    # generate the client Actionscript class
    def controller(class_sym,options)
      # initialize options
      default_route = class_sym.to_s.slice(0,(class_sym.to_s.index('Controller'))).underscore.downcase
      @options = {:overwrite => true, :stub => false , :route => default_route}.merge(options)
      
      # initialize controller
      @controller_class_name = class_sym.to_s
      
      # always reload the controller to get latest definition
      load class_sym.to_s.underscore.downcase + '.rb'
      
      self.interface    
      self.client
      self.stub if @options[:stub]
    end
    
    def client
      log "#{@controller_class_name} client"
      # build template variable map
      map = { 'class' => @controller_class_name + 'Client', 'package' => @package_root+'.controllers.impl' } 
      map['functions'] = self.compose_functions(:class )
      map['controller'] = @options[:route]
      map['interface'] = @package_root+'.controllers.api.' + @controller_class_name
      self.generate("#{@controller_class_name}Client.as", 'client', map)    
    end
    
    def interface
      log "#{@controller_class_name} client interface"     
      # build template variable map
      map = { 'interface' => @controller_class_name , 'package' => @package_root+'.controllers.api' } 
      map['functions'] = self.compose_functions(:interface )    
      self.generate("#{@controller_class_name}.as", 'interface', map)     
    end
    
    def stub
      log "#{@controller_class_name} client stub" 
      # build template variable map
      map = { 'class' => @controller_class_name + 'Stub' , 'package' => @package_root+'.controllers.stub' } 
      map['functions'] = self.compose_functions(:stub )
      map['interface'] = @package_root+'.controllers.api.' + @controller_class_name
      self.generate("#{@controller_class_name}Stub.as", 'stub', map)    
    end
    
    def compose_functions(class_interface_stub)
      if :class == class_interface_stub
        cmd_template = 'command_function' 
        sel_template = 'selection_function' 
      elsif :interface == class_interface_stub
        cmd_template = 'command_function_interface'
        sel_template = 'selection_function_interface' 
      elsif :stub == class_interface_stub
        cmd_template = 'command_function_stub' 
        sel_template = 'selection_function_stub' 
      end  
      # inspect the registy to find command functions for this controller
      class_cmd_apis = Pocogese::CommandAPI.api_registry[@controller_class_name]
      functions = self.compose_command_functions("#{@options[:route]}/service", class_cmd_apis, cmd_template)
      # inspect the registy to find fetch functions for this controller     
      functions << "\n"
      class_selection_apis = Pocogese::SelectionAPI.api_registry[@controller_class_name]
      functions << self.compose_selection_functions("#{@options[:route]}/service", class_selection_apis, sel_template)
    end
    
    # for client template
    def compose_selection_functions(controller_route, operation_to_parameters_map, template)
      return '' unless !operation_to_parameters_map.nil?
      buffer = ''    
      operation_to_parameters_map.keys.sort{|s1,s2|s1.to_s <=> s2.to_s}.each do |k|
        v = operation_to_parameters_map[k]
        log("\tselection: #{k.to_s}(#{v.join(',')})")
        map = {}
        map['models'] = @tmap['models']
        map['name'] = k.to_s
        map['class'] = @controller_class_name      
        map['parameter_declarations'] = self.compose_parameter_declarations(v)
        map['setup_urlvars'] = self.compose_setup_urlvars(v)
        map['controller_path'] = controller_route
        map['parameter_values'] = self.compose_parameter_values(v)  
        buffer << self.process_template(template,map)
        buffer << "\n"
      end        
      buffer
    end  
    
    # for client template
    def compose_command_functions(command_route, operation_to_parameters_map, template)
      return '' unless !operation_to_parameters_map.nil?
      buffer = ''
      operation_to_parameters_map.keys.sort{|s1,s2|s1.to_s <=> s2.to_s}.each do |k|
        v = operation_to_parameters_map[k]
        log("\tcommand: #{k.to_s}(#{v.join(',')})")
        map = {}
        map['name'] = k.to_s
        map['class'] = @controller_class_name
        map['parameter_assignments'] = self.compose_parameter_assignments(v)
        map['parameter_declarations'] = self.compose_parameter_declarations(v)
        map['parameter_values'] = self.compose_parameter_values(v)
        map['command_path'] = command_route
        buffer << self.process_template(template,map)
        buffer << "\n"
      end        
      buffer
    end
    
    # for command function template
    def compose_parameter_assignments(names)
     (names.collect {|name| "_cmd.setParameter(\"#{name.to_s}\",#{name.to_s})"}).join("\n\t\t\t")
    end
    
    # for function template
    def compose_parameter_declarations(names)
      names.collect {|n| n.to_s + ':String,'}
    end
    
    # for selection function template
    def compose_setup_urlvars(names)
      buffer = ''
      names.each do |n|
        buffer << "\t\t\tHttpClient.storeQueryPair(_vars,'#{n.to_s}',#{n.to_s})\n"
      end
      buffer
    end
    
    def compose_parameter_values(names)
      names.join(',')
    end
    
  end #class
  
end #module