module Pocogese

class TemplateProcessor
  attr_accessor :source, :target , :test_base_url , :package_root
  
  # Initialize the directories and the rails-to-flex class map
  def initialize(src,trgt,rootpackage,test_url = "http://localhost:3000")
    @source,@target,@package_root,@test_base_url = src,trgt,rootpackage,test_url
    
    @typemap = {}
    @typemap[:integer] = 'int'
    @typemap[:float] = 'Number'
    @typemap[:datetime] = 'Date'
    @typemap[:date] = 'Day'
    @typemap[:time] = 'Time'
    @typemap[:string] = 'String'
    @typemap[:text] = 'String'
    @typemap[:boolean] = 'Boolean'
    
    @defaultvaluemap = {}
    @defaultvaluemap[:integer] = '0'
    @defaultvaluemap[:float] = '0.0'
    @defaultvaluemap[:datetime] = 'new Date()'
    @defaultvaluemap[:date] = 'new Day()'
    @defaultvaluemap[:time] = 'new Time()'
    @defaultvaluemap[:string] = '""'
    @defaultvaluemap[:text] = '""'
    @defaultvaluemap[:boolean] = 'false'    
    
    # template variables hash
    @tmap = { 'timestamp' => Time.now.httpdate}
    @tmap['testbaseurl'] = test_url
  end
  
  def setup_for_model(model_id,options = nil)
    @tmap['model'] = model_id.to_s
    @tmap['modeltag'] = model_id.to_s.gsub('_','-')  # tag is used in XML serialized form
    @tmap['class'] = model_id.to_s.camelize
  end
  
  def log(what)
    puts "[pocogese::flexgen] " + what
  end
  
  # Read and transform a template using a map of key-value pairs
  # 
  # template must refer to a relative filename (without extension ".st") 
  # that uses the syntax: $var$ (StringTemplate lib for Java)
  def process_template(template_name,map)
    template = File.dirname(__FILE__) + "/../templates/#{template_name}.st"
    map['timestamp']=Time.now.httpdate
    File.open(template,'r') do | f |  
      # detect words inside dollar sign pairs
      replaced = f.read.gsub(/(\$\w*\$)/) do | match |
          key = match[1..match.size-2]
          (map.has_key? key) ? map[key] : 'MISSING:'+key
        end   
      return replaced
    end # file
  end # def

  # Lookup the Ruby class refered to by a model name
  def to_class(model_name)
    Kernel.const_get(model_name.to_s.camelize)
  end

  # Create a new file using a template name and a hash with value for all template variables
  def generate(file_name,template,map, overwrite = true)
    # make sure the package folder exists
    pkg_path = (map['package'] || '').gsub('.','/') 
    FileUtils.makedirs "#{target}/#{pkg_path}"
    File.open("#{target}/#{pkg_path}/#{file_name}",'w') do |f|
      f << self.process_template(template,map)
      log ">> writing #{f.path}"
    end #file
  end

  # Map a ActiveRecord column type of a ActionScript Class name. Returns "missing_type" is no mapping was found.
  def flex_type_from(column_type)
    @typemap[column_type] ||= "MISSING in template_processor.rb=#{column_type}"
  end

  # Return a source snippet for initializing a Flex variable
  def flex_default_from(column_type)
    @defaultvaluemap[column_type] ||= "MISSING in template_processor.rb=#{column_type}"
  end

end #class

end #module