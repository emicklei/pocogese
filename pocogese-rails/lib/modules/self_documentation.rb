module Pocogese
  module SelfDocumentation
    
  def api
    selections = Pocogese::SelectionAPI.api_registry[self.class.name.to_s] || {}
    commands = Pocogese::CommandAPI.api_registry[self.class.name.to_s] || {}
    
    x = Builder::XmlMarkup.new(:target => (api_xml = ""), :indent => 1)
    x.instruct!
    x.pocogese {
      x.controller(:path => request.request_parameters['controller']) {    
        selections.each_key { |op_name| 
           x.selection(:name => op_name) {
              selections[op_name].each { |param|
                x.parameter(:name => param)
              }
           }
        }
        commands.each_key { |op_name| 
           x.command(:name => op_name) {
              commands[op_name].each { |param|
                x.parameter(:name => param)
              }
           }
        }
      }     
    }
    render :xml => api_xml    
  end 
        
  end #SelfDocumentation
end #Pocogese