module Pocogese
  module DoCommandDoSelection
    
    # This is the entrypoint for a service request on the controller
    # Based the Http method GET or POST, the request is either a Selection or a Command
    def service
      request.post? ? self.doCommand : self.doSelection
    end
    
    # Handles a POST request to the route :controller/doCommand
    # Sends a message from the command_api
    # returns Reply || RequestFault in XML
    def doCommand
      # if request.xml_post?
      cmd = Command.from_parameters(request.parameters)
      #@current_command = cmd
      reply = self.execute_command(cmd)
      render :xml => reply.to_xml
    rescue
     unless reply.nil? 
       logger.error("[pocogese#command] reply respond to :to_xml = #{reply.respond_to?:to_xml}")    
       logger.error("[pocogese#command] reply = #{reply}")     
     end
     logger.error("[pocogese#command] - execution of command failed, returning a request fault")
     logger.error("Controller=#{self.to_s}")
     logger.error("Command=#{cmd.to_s}")
     logger.error($!.backtrace.join("\n"))
     logger.error($!)
     render :xml => RequestFault.new(request,self,$!).to_xml
    end
    
    # Executes the command by sending a message using the name and parameters of the command
    # returns Reply || RequestFault in XML
    def execute_command(cmd)
      values = CommandAPI.ordered_parameter_values(self,cmd)
      self.send(cmd.name.to_sym,*values)
    end
    
    # Handles a GET request to the route :controller/doSelection
    # Send a message from the selection_api
    # Use the request parameters to construct the method and argument values in the correct order
    # returns XML || RequestFault in XML
    def doSelection
      sel = Selection.from_parameters(params)
      #@current_selection = sel
      result = self.execute_selection(sel)
      if result.nil?
        render :xml => '<nil/>'
      elsif result.instance_of?(String)
        render :xml => result 
      else 
        render :xml => result.to_xml
      end
    rescue
     logger.error("[pocogese#selection] - execution of selection failed, returning a request fault")
     logger.error("Controller=#{self.to_s}")
     logger.error("Selection=#{sel.to_s}")
     logger.error($!.backtrace.join("\n"))
     logger.error($!)
     render :xml => RequestFault.new(request,self,$!).to_xml       
    end 
    
    # Executes the selection by sending a message using the name and parameters of the selection
    # returns an Object that responds to to_xml
    def execute_selection(sel)
      method_sym = sel.name.to_sym
      values = SelectionAPI.ordered_parameter_values(self,method_sym,sel.parameters) 
      self.send(method_sym, *values)
    end    
    
    # Overwrite from super ; return RequestFault as XML
    def rescue_action(exception)
      # detect if XML was requested
      # if @request.request_uri ~= /\.xml$/
      logger.error("[pocogese#rescue_action] execution of command failed, returning a request fault")
      logger.error "\n\nProcessing #{controller_class_name}\##{action_name} (for #{request_origin}) [#{request.method.to_s.upcase}]"
      logger.error "  Session ID: #{@_session.session_id}" if @_session and @_session.respond_to?(:session_id)
      logger.error "  Parameters: #{respond_to?(:filter_parameters) ? filter_parameters(params).inspect : params.inspect}"      
      fault = RequestFault.new(request,self,exception)
      logger.error(fault.to_xml)
      render :xml => fault.to_xml
    end    

  end # DoCommandDoSelection
end # Pocogese