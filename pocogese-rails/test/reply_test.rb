require File.join(File.dirname(__FILE__), '/test_helper.rb')

class ReplyTest < Test::Unit::TestCase
  def test_panic
    r = Pocogese::Reply.new
    r.status = "panic"
    r.messages = [ "help" , "me" ]
    puts r.to_xml
  end
  
  def test_add
    r = Pocogese::Reply.status("invalid")
    r.messages << "wrong search criteria"
    puts r.to_xml
  end
end