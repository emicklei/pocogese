class Hash
  def keys_at_value(value)
    found = []
    self.each_pair do |k,v|
      found << k if v == value
    end
    found
  end
end