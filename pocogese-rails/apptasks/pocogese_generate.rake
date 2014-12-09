namespace :pocogese do

desc "Generates ActionScript classes with Command and Selection API methods for ActionControllers"
task :generate  => :environment do
  require 'lib/generators/flex_generator'

  # Generates clients and interfaces in Flex
  #  
  project('c:/dev/myflex') {
    package('com.company.app.my') {
      controller :MyController     
    }
  }
  
end # task

end # namespace