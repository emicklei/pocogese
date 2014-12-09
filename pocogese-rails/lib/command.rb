module Pocogese

class Command
  # parameters is a hash
  attr_accessor :name , :parameters
  
  def self.name (operationName)
    cmd = Command.new
    cmd.name = operationName
    cmd.parameters = {}
    cmd
  end
  
  def self.from_parameters(params)
    cmd = Command.name((params[:command])[:name])
    parameter_hash_or_array = (params[:command])[:parameter]
     if (parameter_hash_or_array.class == Array)      
      parameter_hash_or_array.each do |pair|
        cmd.parameters[pair['name']] = pair['value']
      end
     else
      cmd.parameters[parameter_hash_or_array['name']] = parameter_hash_or_array['value']
     end
    cmd
  end
  
  def to_s
    'Pocogese::Command ' + self.to_event_info
  end
  
  def to_event_info
    "#{@name}(#{@parameters.values.join(',')})"
  end
  
  def to_xml
    x = Builder::XmlMarkup.new(:target => (reply = ""), :indent => 1)
    x.instruct!
    x.command(:name => name) {
        unless parameters.nil?
          parameters.each_pair { |key,value| 
            value_as_string = value
            # if the value is not a literal then try to get its XML representation
            if value_as_string.respond_to?:to_xml
              value_as_string = value_as_string.to_xml
            end
            x.parameter(:name => key, :value => value_as_string)
          }
      end       
    }
    reply
  end  
  
end #class

end #module
