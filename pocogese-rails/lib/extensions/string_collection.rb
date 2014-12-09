require 'builder'

# StringCollection is a wrapper of a collection of Strings that knows how to convert itself to compact XML
#
#  def array
#    a = [ 'Welcome' , '@' , 'Pocogese' ]
#    render :xml => StringCollection.new(a,'sample').to_xml
#  end
#
# <?xml version="1.0" encoding="UTF-8"?>
# <!-- 3 entries -->
#   <strings each="sample">
#   <s>Welcome</s>
#   <s>@</s>
#   <s>RestWorks</s>
# </strings>
#
# ernest.micklei@philemonworks.com, 2007
class StringCollection
  def initialize(strings = nil,their_meaning = nil)
    @collection = strings || []
    @each = their_meaning
  end
  
  # Create a new StringCollection by parsing the xmlString
  # Pre: xmlString not nil
  def self.from_xml(xmlString)
    raise ArgumentError , 'xmlString' if xmlString.nil?
    dict = Hash.from_xml(xmlString)
    strings = dict['strings']
    strings.nil? ? self.new : self.new(strings['s'])
  end
  
  # Return the xmlString for the StringCollection
  def to_xml
    x = Builder::XmlMarkup.new(:target => (reply = ''))
    x.instruct!
    x.comment! "#{@collection.size} entries"    
    x.strings(:each => @each) {
      @collection.each do |each|
        x.s(each)
      end
    }
    reply
  end
  
  # Iterator method
  def each
    @collection.each {|s| yield s }
  end

  # Iterator method
  def collect
    @collection.collect {|s| yield s }
  end  
  
  # Iterator method  
  def size
    @collection.size
  end
end