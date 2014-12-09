# Core

class Symbol
  def literal
    ':' + self.to_s
  end
end

class String
  def literal
    "'" + self.to_s + "'"
  end
end

class Array
  def literal
    '[' + (self.collect {|each| each.literal}).join(',') + ']'
  end
end

class Hash
  def literal
     line = '{'
     self.each_pair do |k,v|
        line << ',' if(line.size>1)
        line << k.literal
        line << ' => '
        line << v.literal
     end
     line << '}'
  end
end

class Object
  #default implementation
  def literal
    self.to_s
  end
end

# for testing
if (__FILE__ == $0)
  puts [1].literal
  puts "#{{:a => 'b'}.literal}"
end

