module Pocogese
  module SaveFindDestroyMethods

   def self.included(base)
      SelectionAPI.register_selection(base.name,:listPage,[:from,:to,:sortkey,:sortmethod,:searchpattern])
      SelectionAPI.register_selection(base.name,:find,[:record_id])
      
      CommandAPI.register_command(base.name,:saveOrUpdate,[:recordXML])
      CommandAPI.register_command(base.name,:destroy,[:record_id])
   end

   def pocogese_registered_record_class
     model_sym = Pocogese::ModelRegistry.model_served_by(self.class.name)
     Kernel.const_get(model_sym.to_s.camelize)
   end

   # Fetch a window of records for a given model
   # from starts at 1
   def listPage(from,to,sortkey,sortmethod,searchpattern='')
     cls = self.pocogese_registered_record_class
     page_size = to.to_i - from.to_i + 1
     page = (to.to_i - 1) / page_size + 1     
     
     find_options = {:limit => page_size ,:offset => (page_size * (page-1)) }
     unless sortkey.nil? || sortkey.empty?
       order_method = sortmethod == 'descending' ? ' DESC' : ' ASC'
       find_options[:order]=sortkey.gsub('-','_') + order_method  # undasherize?
     end
     unless searchpattern.nil? || searchpattern.empty?
       if searchpattern.reverse.chop != '*'
         searchpattern += '*'
       end
       search = searchpattern.gsub('*','%').downcase
       # compose conditions 
       # TODO check whether strings,dates or numbers are searched for
       condition_query = ''
       condition_values = []
       cls.columns.each do |col|
         if col.type == :string
           condition_query << ' or ' unless condition_query.empty?
           condition_query << "lower(#{col.name}) like ?"
           condition_values << search
         end
       end
       find_options[:conditions] = condition_values.unshift(condition_query)
     end
     records_on_page = cls.find(:all, find_options)
     #debugger
     
     result = Pocogese::Page.new
     result.model = Pocogese::ModelRegistry.model_served_by(self.class.name).to_s
     result.from = from
     result.to = from.to_i + records_on_page.size - 1
     # modify the find_options to compute the total
     find_options.delete(:limit)
     find_options.delete(:offset) 
     find_options.delete(:order)

     result.total = cls.count(:all, find_options)
     result.records = records_on_page
     result.sortkey = sortkey
     result.sortmethod = sortmethod
     result.searchpattern = searchpattern
     result
   end  
   
   def find(record_id)
     self.pocogese_registered_record_class.find_by_id(record_id)
   end
        
   def saveOrUpdate(recordXML)
     record_class = self.pocogese_registered_record_class
     root_hash = Hash.from_xml(recordXML)
     record_hash = root_hash[root_hash.keys[0]]
     begin
       if (record_hash['id']=='0') 
         record_hash.delete('id')
         # new, throws RecordNotSaved 
         record = record_class.create!(record_hash)
       else 
         #update, throws ?
         record = record_class.update(record_hash['id'],record_hash)       
       end
       result = Reply.status("RecordSaved", record.id)
     rescue ActiveRecord::RecordNotFound => ex_not_found
       result = Reply.status("RecordNotFound",record_hash['id'],['Object no longer exists'])       
     rescue ActiveRecord::RecordInvalid => ex_invalid
       result = Reply.status("RecordInvalid", nil, ex_invalid.record.errors.full_messages)
     rescue Exception => ex
       logger.error("[pocogese#saveOrUpdate] #{$!}, #{ex.class}")
       logger.error("record=#{record_hash.literal}")
       logger.error(ex)
       result = Reply.status("Exception" ,record_hash['id'])
       result.messages << 'Sorry but for some reason, changes could not be saved. TODO'
     end
     result
   end

   def destroy(record_id)
     record_class = self.pocogese_registered_record_class
     begin
      record_class.destroy(record_id)
      result = Reply.status("destroyed", record_id)
     rescue ActiveRecord::RecordNotFound => ex_not_found
       result = Reply.status("RecordNotFound",record_id , ['Object no longer exists'])
     rescue Exception => ex
       logger.error("saveOrUpdate:#{$!}, #{ex.class}")
       result = Reply.status("Exception",record_id)
       result.messages << 'Sorry but for some reason, record could not be deleted. TODO'
     end
     result
   end
   
  end # SaveFindDestroyMethods
end #Pocogese