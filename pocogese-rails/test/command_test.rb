require File.join(File.dirname(__FILE__), '/test_helper.rb')

class CommandTest < Test::Unit::TestCase
  
  def test_no_parameters
    Command.new
  end
  
  def test_from_parameters_one_arg
    params = {}
    params[:command] = { :name => "test" , :parameter => {"name" => "forename", "value"=>"ernest"} }
    cmd = Command.from_parameters(params)
    puts cmd
  end
  
  def test_from_parameters_two_args
    params = {}
    params[:command] = { :name => "test" , :parameter => [{"name"=>"forename", "value"=>"ernest"}, {"name"=>"surname", "value"=>"micklei"}] }
    cmd = Command.from_parameters(params)
    assert_equal 'ernest' , cmd.parameters['forename'] , "forename key"
    assert_equal 'micklei' , cmd.parameters['surname'] , "surename key"
    puts cmd
  end  
    
  
end