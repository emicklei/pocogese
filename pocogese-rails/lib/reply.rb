require 'builder'
module Pocogese

class Reply
  attr_accessor :status, :messages, :result
  
  def self.warning(message)
    Reply.status("warning",nil,[message])
  end
  
  def self.error(message)
    Reply.status("error",nil,[message])
  end  

  def self.failed(message)
    Reply.status("failed",nil,[message])
  end   
  
  def self.ok(message)
    Reply.status("ok",nil,[message])
  end  
  
  def self.status(_status, _result = nil, _messages = [])
    r = Reply.new
    r.status = _status
    r.messages = _messages
    r.result = _result
    r
  end
  
  def to_xml
    x = Builder::XmlMarkup.new(:target => (reply = ""), :indent => 1)
    x.instruct!
    x.reply(:status => status) {
      x.result(@result) unless @result.nil?
        unless messages.nil?
          messages.each { |msg| 
            x.message msg
          }
      end       
    }
    reply
  end
    
  def to_s
    'Pocogese::Reply [' + self.to_event_info + ']'
  end
  
  def to_event_info
    "status=#{@status},result=#{@result},#messages=#{@messages.size unless @messages.nil?}"
  end  
  
end #class

end #module
