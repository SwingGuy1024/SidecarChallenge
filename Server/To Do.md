1) Experiment with WebSecurityConfig. There appear to be two ways to allow the swagger documentation to get past security. One is in `configure(HttpSecurity)`, and the other is in `configure(WebSecurity)`. Are they both needed? Does one work better than the other?

1) Put all controller implementation methods into separate classes in another package.
1) Move Encoder to its own Bean in the same class as UserDetails
1) Figure out User logic and add two addUser methods, one for admin and one for customers. Use email for username. If no email address, ask for username and use mobile phone for contact.