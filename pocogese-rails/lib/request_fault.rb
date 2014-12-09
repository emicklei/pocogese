require 'builder'

module Pocogese

class RequestFault
  attr_accessor :request_uri,:method,:code,:message,:stack,:controller
  
  def initialize(httpRequest,actionController,exception)
    @request_uri = httpRequest.request_uri
    @method = httpRequest.method.to_s.upcase
    @controller = actionController.class.name
    @stack = exception.backtrace.join("\n") unless exception.nil?
    @message = exception.message unless exception.nil?
    @code = 500
  end
    
  def to_xml
    x = Builder::XmlMarkup.new(:target => (fault = ""), :indent => 1)
    x.instruct!
    x.requestfault(:code => code) {
      x.request(@request_uri) unless @request_uri.nil?
      x.method(@method) unless @method.nil?
      x.message(@message) unless @message.nil?    
      x.stack(@stack) unless @stack.nil?    
      x.controller(@controller) unless @controller.nil?          
    }
    fault
  end

end # class

end # module