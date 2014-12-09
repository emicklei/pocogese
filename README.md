Pocogese
is short for "POST Commands and GET Selections".

Pocogese implements the concept of Commands and Selections applied to the remote invocation of Services from Adobe Flex applications. This design choice is based on the observation that most Services either read or manipulate Resources.

Reading is realized by sending GET requests with Selections ; these should not change the state of the Resource requested. Selections return with a single Resource or a list of Resources based on some selection criteria.
Manipulation is realized by sending POST requests with Commands ; these change the state of one or multiple Resources. Typical commands include create,update and delete, but any other modifying action can be modeled this way. Commands return with information about the status of the manipulation and may include one result and explanation messages.
Current implementation uses POX (Plain Old XML) for data transport. AMF3 could be a future alternative. Take a look at the source

Subproject: Pocogese-Rails
Pocogese-Rails provides a Ruby on Rails plugin that extends ActiveController to dispatch both commands and selections. In addition, this plugin includes a Rake task to support the generation of Flex controller clients.

After RailsPluginInstall and FlexLibraryInstall, you start with SetupRailsService, then GenerateFlexFromRails and finally CallServiceFromFlex to make it work.

Subproject: Pocogese-Java
Pocogese-Java provides a Servlet-based implementation of a controller dispatcher that can handle commands and selections. A Servlet receives invocations which are dispatched to registered !Controllers. In addition, this library includes a FlexGenerator to support the generation of Flex controller clients.

After JavaLibraryInstall and FlexLibraryInstall, you start with SetupJavaService, then GenerateFlexFromJava and finally CallServiceFromFlex to make it work.

Subproject: Pocogese-Test
Pocogese-Test provides FlexUnit testcases and a TestRunner to test components and classes from Pocogese. Unit tests for other subprojects (Rails and Java) are included in their own project structure. This project requires the flexunit library.

Related Projects
Dunelox - Flex library of application components
Dunelox-Flexgen - Flex source generator of models,views,forms and grids from Rails ActiveRecord models. Currently there is no such generator that can process Java classes.
