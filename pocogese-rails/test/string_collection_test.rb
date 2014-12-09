require File.join(File.dirname(__FILE__), '/test_helper.rb')

class StringCollectionTest < Test::Unit::TestCase
  
  def test_from_xml
    xml = "<strings><s>Hello</s><s>World</s></strings>"
    sc = StringCollection.from_xml xml
    assert sc.size == 2
  end

end