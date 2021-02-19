1) Experiment with WebSecurityConfig. There appear to be two ways to allow the swagger documentation to get past security. One is in `configure(HttpSecurity)`, and the other is in `configure(WebSecurity)`. Are they both needed? Does one work better than the other?

1) Add a UserDetailsManager implementation to replace the UserDetailsService. Use JdbcUserDetailsManager.

