require File.join(File.dirname(__FILE__), '/test_helper')

# Sample MyActionController class

include Pocogese

class MyAccountController < ActionController::Base

  command_api :login, :user , :password   
  def login(user,password)
    Reply.status(user,password)
  end
  
  command_api :logout
  # definition
  def logout
    Reply.status("logout ok")
  end

end

# Now test it

class MyAccountControllerTest < Test::Unit::TestCase

  def test_login
    ac = MyAccountController.new
    reply  = ac.login("usr","pwd")
    assert !reply.nil? , "reply not null"
    assert reply.status == "usr" , "parameter order"
  end

end