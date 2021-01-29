# Code Generator

To generate the code, run `mvn clean install` on this module. Generated code will be placed in the target folder, and will be packaged into a jar file. Modules that need to use the generated code may add a dependency to this module.

Code is generated from the spec in the `OpenApi.yaml` file. Modify this file to make any needed changes.