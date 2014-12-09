require 'builder'

# StringDictionary is a Hash with key=>value String pairs that knows how to convert itself to compact XML
#
#  def nls_locale_nl
#   h = {:hello => '<"Hallo">' , :welcome => '&\'Welkom\'' }
#    render :xml => StringDictionary.new(h,'nls_locale_nl.entry').to_xml
#  end
#
# <?xml version="1.0" encoding="UTF-8"?>
# <!-- 2 entries -->
# <dictionary each="nls_locale_nl.entry">
#   <s k="hello" v="&lt;&quot;Hallo&quot;&gt;"/>
#   <s k="welcome" v="&amp;'Welkom'"/>
# </dictionary>
#
# ernest.micklei@philemonworks.com, 2007
class StringDictionary
  def initialize(hash,their_meaning = nil)
    @collection = hash
    @each = their_meaning
  end
  
  def to_xml
    x = Builder::XmlMarkup.new(:target => (reply = ''))
    x.instruct!
    x.comment! "#{@collection.size} entries"
    x.dictionary(:each => @each) {
      @collection.each_pair do |key,value|
        x.e(:k=>key , :v=>value)
      end
    }
    reply
  end
  
  def size
    @collection.size
  end  
end