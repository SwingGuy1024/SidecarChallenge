# Code Generator

To generate the code, run `mvn clean install` on this module. Generated code will be placed in the target folder, and will be packaged into a jar file. Modules that need to use the generated code may add a dependency to this module.

Code is generated from the spec in the `OpenApi.yaml` file. Modify this file to make any needed changes.

The `pom.xml` file sets the configuration options that determine how the code will be generated. Configuration options may be found in the `openapi-generator-maven-plugin` at these two places:

   `project.build.plugins.plugin.executions.execution.configuration`
   `project.build.plugins.plugin.executions.execution.configuration.configOptions`
   
No non-generated source code should go in this module. The code generator generates API interfaces in the `api` package, and data transfer objects in the `model` package. The classes that implement the interfaces in the `api` package need to go into the same package, or the swagger-generated documentation won't show up.
