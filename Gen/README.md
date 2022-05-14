# Code Generator

To generate the code, run `mvn clean install` on this module. Generated code will be placed in the target folder, and will be packaged into a jar file. Modules that need to use the generated code may add a dependency to this module.

Code is generated from the spec in the `OpenApi.yaml` file. Modify this file to make any needed changes.

The `pom.xml` file sets the configuration options that determine how the code will be generated. Configuration options may be found in the `openapi-generator-maven-plugin` at these two places:

   `project.build.plugins.plugin.executions.execution.configuration`
   `project.build.plugins.plugin.executions.execution.configuration.configOptions`
   
No non-generated source code should go in this module. The code generator generates API interfaces in the `api` package, and data transfer objects in the `model` package. The classes that implement the interfaces in the `api` package need to go into the same package, or the swagger-generated documentation won't show up.

## Generation Notes:

### How to use the 'type' specifier

In the `components:schemas` section, when defining object properties, the `type` and `format` specifiers determine what kind of object will be generated. Here is a list of the various combinations and what they produce. When the `format` is empty, this means no `format` is specified. There is a third specifier, `description`, which specifies the javadocs of the field's getter method.

      type    format    Java Type
      ----    ------    ---------
      boolean           Boolean
      integer int32     Integer
      integer int64     Long
      number  double    Double
      number  float     Float
      number            BigDecimal - Use this one
      number  int32     BigDecimal
      number  int64     BigDecimal
      number  number    BigDecimal
      string            String
      string  byte      byte[] (base64 encoded characters)
      string  binary    File
      string  date      LocalDate
      string  date-time OffsetDateTime
      string  password  String
      array             List<>
    Undocumented:
      integer byte      Integer
      integer binary    Integer
      integer date      Integer
      integer decimal   Integer
      string  int32     String
      string  int64     String
    I don't know how to get BigInteger.

### Specifying the API

There are many keywords that can be used, and it's not obvious what they do. They may be used to generate code in a number of languages, so it's not obvious what they mean for Java. Here's a quick rundown:

    summary:      The opening line of the javadocs, starting with the REST comand. (See note 1)
                  (The REST command is inserted automatically.)
    description:  A paragraph in the javadocs, giving a more detailed explanation.
    operationId:  (required) The name of the generated method.
    

#### Notes
1. It's best to end the summary with a period, since the generated code fails to produce a line break between this line an the next one.
     