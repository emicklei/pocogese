require 'builder'
module Pocogese
  
  class Page
    attr_accessor :from, :to, :total, :sortkey, :sortmethod , :records , :model , :searchpattern
  
    def to_xml
       x = Builder::XmlMarkup.new(:target => (result = ""), :indent => 1)
       x.instruct!     
       x.tag!(@model.to_s.pluralize, 
            :to => @to,
            :from => @from,
            :sortkey => @sortkey ,
            :sortmethod => @sortmethod , 
            :searchpattern => @searchpattern ,
            :total => @total) {
          @records.each do | each |
              result << each.to_xml(:skip_instruct => true)
            end
       }
       result
    end  
  
  end  # class
end # module
