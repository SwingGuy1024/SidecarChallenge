# Building

The project is divided into five modules, Gen, Common, Auth, Business, and Server. You can do `mvn clean install` in the main directory to build all packages. Or you can build each module separately. If you build separately, you should build them in three stages:

Stage 1: Gen & Common

Stage 2: Auth & Business

Stage 3. Server.

Either way, this will produce an executable jar file at `Server/target/Server-0.0.1-SNAPSHOT.jar`.

## Notes on module structure:

1. This project is small enough to need only two modules, one for generated code, the other for application code. But eventually the project will presumably benefit from breaking it up into separate modules. The current structure is an experiment on how that might be done. In particular, it was done this way to separate the RESTful services, in the Server module, from the business logic in the Business module. However, this is not meant to be the final word on how the project should be structured. I did it this way mainly to see what kind of issues arise from the separation of server from business logic. I did not expect, for example, that I would need to break the main class into two separate classes, OpenAPI2SpringBoot and ServerMaster, in order to make one of them accessible to unit tests while the other is the main class for launching the server.
2. The Auth module is broken out into a separate module on the assumption that its code could be shared by multiple modules, all of which need to share authentication code.
# Starting up

Start Redis:

    brew services start redis

Create a mysql database called pizza
Create a user called pizza, with password pizza
Grant the user all rights on the pizza database

    create database pizza;

    use pizza;
    create user pizza identified by 'pizza';
    grant all on pizza.* to pizza;

If using MySQL version 8+, the second line should be

    create user pizza identified WITH mysql_native_password by 'pizza';

Launch the application and start by adding some menu items.
This was developed under Java 8 and has been tested running under Java 8 and 12. The application jar file can be found in Server/target. To run using Maven, type

    > cd Server
    > mvn spring-boot:run

Or you can launch it directly from the jar file:

    > java -jar Server/Target/Server-0.0.1-SNAPSHOT.jar

# Notes:

**Assignment Part 1: Design Assignment**

This design is in the same folder as this README.md file, called **Part 1 — Pizza Design.pdf**.

## 1. Implementation

This doesn't exactly implement the Design I produced in the Part 1 of the assignment. The biggest change is the menu structure. The design calls for a tree structure of Menu Items. This implements MenuItems with separate MenuItemOptions, in a one-to-many relationship.

### Known Design Defects
This project  was created quickly in response to a time-limited coding challenge, so I didn't have the time to implement the database structure properly. Here are two chief changes I would make in a professional product:
   
1. Menu Items: The current menu structure uses a one-to-one relationship between nodes and subnodes, and treats menu items and menu item options as separate entities. In a real system, there would be no difference between them, and menu items would have a many to many relationship with their parent menu item. This would allow menu options, such as dressing on salad or pizza toppings, to be shared by multiple different salads or pizzas.

1. Orders. The order structure would also be a tree structure, with each order instance wrapping a menu item instance. The order tree would be completely separate from the menu item tree, to allow it to be a subset of the menu item. This is because a menu item contains every possible option, but an order item contains only the ones the customer has chosen. Consequently, parents and children do not need to have a many-to-many relationship. But in an order, every selected item would have the same order number. This order number is how the system distinguishes one order from the next.

### API Design:
#### General
The APIs are implemented with a call to a serve method, which takes a lambda expression that delivers the requested data. This is packed into a ResponseEntity object and returned. I did this to separate the server-related classes from the implementation, so the implementation code does not need to know it's running on a server, unless it has to throw an Exception. I did this partly because past servers I've worked on have been very inconsistent in how they log errors, return results, and return errors. The `serve()` method, and some convenience methods that delgate to it, is in the `ResponseUtility` class, and other useful utilities are in the PojoUtilities class (See the [**Service Implementations**](#Service Implementations) section below for more details.) At some point, I'd like to write generators for OpenAPI to encourage this design in all APIs.

#### Developer Rules for error responses:
1. Return an error response only by throwing an annotated Exception. Exceptions are annotated with the `@HttpStatus` annotation, which specifies the status to return. All of these exceptions extend ResponseException. If there's no Exception for the error response you want to send, add one. Be sure to extend `ResponseException` and annotate it with `@HttpStatus`.
1. Never catch a RuntimeException. If the code generates a RuntimeException, let it pass through. It will generate a 500 error response (Internal Server Error), which tells us we need to find the bug and fix it. If you need to say `catch (Exception e)`, first catch any RuntimeExceptions and rethrow them.
1. Don't worry about logging Exceptions, unless you catch them and don't rethrow. The UncaughtExceptionHandler will log all Exceptions it sees.
1. The ResponseException subclasses will only get thrown in response to a known exceptional situation that's not due to a bug, so their stack traces don't appear in the log. Never throw one in response to a bug.

## 2. Implemented Technologies

1. RESTful services
1. JPA Crud operations will write to an underlying MySql database, using Spring Data.
1. Unit tests use an in-memory h2 database.
1. Spring Security defines three security levels with two roles. The levels are ADMIN and CUSTOMER. The third level is no security, and is read-only. Endpoints are mapped like this:

    1. `/menuItem/**` Menus, which are read-only, and require no authentication 

    1. `/order/**` CUSTOMER role required. For placing orders (Not implemented)

    1. `/admin/**` ADMIN role required. Administrative: APIs let you modify the menus and add options

    1. `/login/**` No authorization required.
   
1. Authorization is done using JWT.
1. Redis  Caching. The `menuItem` API, which returns the entire menu, is held in a Redis cache, backed by the MySql database. The cache gets cleared when the menu is updated.
1. Mockito. One unit test ('JwtRequestFilterTest`) uses Mockito create mocks for testing.
1. Actuator. A simple health check method is implemented.
1. Logging. Lots of logging is done at the debug and trace levels. A few methods are logged at the info level, such as login information.

## 3. Logging In Users

There is an API for creating new users, but you don't need to use it. The first time you launch the server, it will check to see if any users exist in the database. If there are none, it will create five users. They are User1, User2, User3, Admin1, and Admin2. Each uses its username as its password. The three Users have the CUSTOMER role, and the other two have the ADMIN role. You may use the APIs to create more users. All users, including guests, may view the menus. Users with the CUSTOMER role may place orders (not implemented). Users with the ADMIN role may modify the menu items, create new ones, and delete old ones.

## 4. Notes:

To monitor the Redis cache, and verify that it's working, use the Redis CLI:

    $> redis-cli MONITOR
Then run the application. Anytime you use the cache, the monitor will print information about the operation.

## 5. APIs

(The most important part of this demo is described in the [**Service Implementations**](#Service Implementations) section, below.)

This uses maven to build. It has been tested using Maven v3.6.3 and Java 1.8.0_212. Maven uses Java 9.0.4

### REST API Documentation

You can view the api documentation in swagger-ui by launching the server, then go to `http:localhost:8080`, which will redirect to
`http://localhost:8080/swagger-ui.html`

You may change the default port value in application.properties

### Service Implementations

To ensure consistency in how the services are written, and to reduce the amount of boilerplate code, all the services use a variant of
the `ResponseUtility.serve()` method. This allows the service to focus solely on the task of generating the service data, and not worry
about creating the ResponseEntity or generating an error response. In case of an error, the service need only throw one of the subclasses of ResponseException,which all include an HttpStatus value. The `PojoUtilities` class has several convenience methods to
simplify this, all of which throw a ResponseException. By convention, all these methods begin with the word "confirm." For example, if a
service requests an Entity with a specific ID, the service should call 
`PojoUtility.confirmFound(entity, id);` If the entity doesn't exist, the `confirmFound()` method will throw a ResponseException with a 
NOT_FOUND status, and include the id in the error message.

So a service method that needs to return an instance of `MenuItemDto` would look something like this:

```
1   public class MenuItemApiController {
2
3   private final DataEngine dataEngine;
4
5   // ...  
6   @Override
7   public ResponseEntity<MenuItemDto> getMenuItem(final Integer id) {
8     return serveOK(() -> dataEngine.getMenuItemDto(id));
9   }
```

So, on line 8, we specify an OK status if the method returns successfully. We also create the lambda expression that delegates the work to
the `DataEngine` class:

```
1  public class DataEngine {
2
3    MenuItem getMenuItemFromId(int id) {
4      MenuItem menuItem = confirmFound(menuItemRepository, id);  // throws NotFound404Exception extends ResponseException
5      return menuItem;
6   });
7 }
```

On line 6, we test for null, using the `confirmFound()` method. If no `menuItem` exists with the specified id, it will throw `ResponseException` with
an `HttpStatus` of `NOT_FOUND`. We don't need to catch it, because it's annotated with `@ResponseStatus(HttpStatus.NOT_FOUND)`, so the
server will use that status code in its response. But the `serveOK()` method, called in the previous method, catches it for logging purposes, then rethrows it.

The call to the `serve()` method takes care of five boilerplate details:

1. It adds the return value (an instance of MenuItemDto) to the `ResponseEntity` on successful completion.
1. It sets the specified HttpStatus, which in this example is `HttpStatus.OK`.
1. It generates the proper error response, with an error status code taken from the `ResponseException` thrown by the lambda expression. In
   this case, this is a `NotFound404Exception` thrown by the`confirmFound()` method. The `NotFound404Exception` method extends `ResponseException`
   , as do all the others.
1. It logs the error message and exception.
1. It catches any RuntimeExceptions and returns a response of Internal Server Error.

Also, by using ResponseExceptions to send failure information back to the `serve()` method, it discourages the use of common Exception
anti-patterns, like catch/log/return-null. Instead, developres are encouraged to wrap a checked exception in a ResponseException and rethrow
it, and to ignore all RuntimeExceptions, letting them propogate up to the `serve()` method, which can then generate an INTERNAL SERVER ERROR
response.

The lambda expression creates a `Supplier<T>`

The `serve()` method has this signature:

`  public static <T> ResponseEntity<T> serve(HttpStatus successStatus, Supplier<T> method)`

The only boilerplate code in the example is the `@RequestMapping` annotation and the method signature, both of which are generated by
Swagger.

### Sample `confirmXxx()` methods.

All of these may throw a `ResponseException`. I've adopted the convention that all methods that may throw `ResponseException` start with the
word *confirm.*

* `<E, ID> E confirmFound(JpaRepository<E, ID> repository, ID id) throws ResponseException` Confirms the returned entity with specified id is not null. This also retrieves and returns the entity.
* `<T> T confirmNeverNull(T object) throws ResponseException` Used for values that are not entities.
* `void confirmNull(Object object) throws ResponseException` This is useful to ensure a new resource doesn't already exist.
* `<T> void confirmEqual(T expected, T actual) throws ResponseException`
* `Long confirmAndDecodeLong(final String id) throws ResponseException`
* `Integer confirmAndDecodeInteger(final String id) throws ResponseException` These two parse the String into an Integer or Long. A better name might be
  just `decodeInteger()`, but it starts with `confirm` to keep with the convention.

People have asked why I didn't use the word *validate,* since it's pretty standard. I decided not to use it to be clear that these methods
are not a part of any third-party validation framework.

I should also stress that these are just convenience methods. If any developers have cases not handled by one of these, and can't write a
simple convenience method to do what they need, they are free to throw a ResponseException directly. Any RuntimeExceptions need not be
caught. They will get logged and an INTERNAL_SERVER_ERROR response will be returned.

## Data Model

### Assumptions

A Menu item consists of options. Each menu item has a price, as does each option. (Option prices may be zero.) An order consists of a menu
item and a list of options.

An order may calculate a price based on the Menu Item's base cost and the options chosen.

When an order is opened, the time is recorded. (I have no idea if that's useful, but it may help in searching.) At this point, the order may
be either canceled or completed. If it's canceled, it's removed from the database. If it's completed, it is marked complete and kept in the
database.

Orders may be searched by ID

I'm not sure if my API is most useful for a UI developer. I prefer to ask the UI developers what they need, then build the API around their
needs. That said, I have APIs to define menu items, and add options to them. I have APIs to create an order, to add options to either an
order or a MenuItem, and to search for completed or open orders in a given date range.

### JPA Entities

#### 1. MenuItem

A MenuItem consists of a name, price, and list of MenuItemOptions (below). The list consists of all possible options for this menu item.
MenuItem has a One-to-Many relationship with MenuItemOption. It also includes a price.

#### 2. MenuItemOption

A MenuItemOption adds an option to aMenuItem (below). It also has a delta price, which is the amount the price changes if the guest chooses
this option.

#### 3. CustomerOrder

A Food order is an actual order. It has a final price, a boolean to record when it has been completed and delivered, and an order date and
completion date, and a list of MenuItemOptions. Unlike the MenuItem, the list of options is all the chosen options, rather than the
available options. Also, unlike MenuItem, the CustomerOrder has a Many-To-Many relationship with MenuItemOption.

## Testing

The testing application properties specify an in-memory database, so changes get wiped out from test to test. This greatly facilitates
testing.

The Controller classes have public method which are called by the server, and package-level methods that are only for testing. All of these
package methods are named `xxxXxxxTestOnly` to discourage their use even if somebody puts a class in the same package.

## Code Generation

Generated using Swagger's OpenAPI Specification OAS 3.0, using the Spring Server generator, with the following options:

* interfaceOnly: True
* bigDecimalAsString: True
* dateLibrary: Java 8
* developer name: Miguel Muñoz
* title: Pizza Orders
* generatorName: spring
* library: spring-boot

### Code Generator Bugs (All are minor)

#### Spurious Optional

When Java 8 is set, it adds a NativeWebRequest member. It also creates default getter for that property and the ObjectMapper property. This default getter wraps the values in an `Optional`.
Both properties are final and autowired, so they can't possibly have null values, so the Optional wrapper returned by the getters is
unnecessary.

#### Spurious default methods

When Java 8 is set, it turns on the defaultInterfaces option, which I would rather be left off. This generates stubs as default methods for
each api method. There are two consequences. First, failure to implement a recently added interface doesn't prevent compilation. Second, the
stubs return a 510 Not implemented. I would rather they throw an Error. (It also takes too much code to return the 501)

#### Date option

When the date library is set to one of the three java 8 values, it turns on Java 8, which is fine, but this activates the two java 8 bugs above.
