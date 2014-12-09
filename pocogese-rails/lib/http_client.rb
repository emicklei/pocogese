require 'rfuzz/client'

module Pocogese

class HttpClient
  def initialize(host,port,base="")
    @host, @port = host,port
    @base = base
    @client = RFuzz::HttpClient.new(host,port)
  end
  
  def target_uri(url)
    @base + '/' + url
  end
  
  # Returns the raw body of the repsonse or evaluates the block with the body.
  #
  def send_get(url)
    result = @client.get(self.target_uri(url))
    raise "Invalid status #{result.http_status} from server #{@host}:#{@port}" if(result.http_status != '200')
    if block_given?
      yield(result.http_body)
    else
      result.http_body
    end
  end
  
  # Returns a Reply or evaluates the block with a Reply.
  #
  def send_post(data_xml,url)
    result = @client.post(self.target_uri(url), :body => data_xml , :head => {'Content-Type' => 'application/xml'} ) 
    raise "Invalid status #{result.http_status} from server #{@host}:#{@port}" if(result.http_status != '200')    
    #reply = Reply.from_xml(result.http_body)
    if block_given?
      yield(result.http_body)
    else
      result.http_body
    end
  end
  
end

#rcl = Pocogese::HttpClient.new('localhost', 3003, '/user-services')
#1.times {
#  rcl.send_get('account/show/1.xml') { |data| puts data }
#}
#cmd = <<CMD
#  <?xml version="1.0" encoding="UTF-8"?>
#  <command name="login">
#    <parameter name="name" value="ernest" />
#    <parameter name="password" value="micklei" />    
#  </command>
#CMD
#rcl.send_post(cmd, 'login/doCommand') { |reply|
#  puts reply
#}

end # module