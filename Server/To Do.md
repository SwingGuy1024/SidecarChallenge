1) Experiment with WebSecurityConfig. There appear to be two ways to allow the swagger documentation to get past security. One is in `configure(HttpSecurity)`, and the other is in `configure(WebSecurity)`. Are they both needed? Does one work better than the other?

1) Add a UserDetailsManager implementation to replace the UserDetailsService. Use JdbcUserDetailsManager.

1) MenuItem.java: Figure out how to handle collection safely.

1) Add a (this) parameter to all ResponseUtility.serve() methods. All API implementations should implement RequestContainer (below). This lets us pass the api to the serve method. This way, if an exception is thrown, the serve method can retrieve the endpoint and include it in the log file.


    public interface RequestContainer {
      Optional<NativeWebRequest> getRequest();
    }
