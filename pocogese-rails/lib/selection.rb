module Pocogese

class Selection
  # parameters is a hash
  attr_accessor :name , :parameters
  
  def self.name (operationName)
    sel = Selection.new
    sel.name = operationName
    sel.parameters = {}
    sel
  end
  
  def self.from_parameters(params)
    sel = Selection.name(params['id'])
    sel.parameters = params.clone
    # is this the right place? TODO
    ["id","action","controller"].each do |key| sel.parameters.delete(key) end
    sel
  end
  
  def to_s
    "Pocogese::Selection #{@name}(#{@parameters.values.join(',')})"
  end  
end

end #module