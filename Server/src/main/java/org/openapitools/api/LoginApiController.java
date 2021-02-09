package org.openapitools.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.framework.ResponseUtility;
import org.openapitools.model.UserDto;
import org.openapitools.repositories.UserRepository;
import org.openapitools.server.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
import org.openapitools.entity.User;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-01-08T23:22:44.934923-08:00[America/Los_Angeles]")
@Controller
@RequestMapping("${openapi.customerOrders.base-path:}")
public class LoginApiController implements LoginApi {

    private static final String USER_PASSWORD_COMBINATION_NOT_FOUND = "User/password Combination not found";

    private static final Logger log = LoggerFactory.getLogger(LoginApiController.class);
    private final NativeWebRequest request;
    
    private final ObjectMapper objectMapper;
    
    private final UserRepository userRepository;
    
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Autowired
    public LoginApiController(final NativeWebRequest request, final ObjectMapper objectMapper, final UserRepository userRepository) {
        this.request = request;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        doDemoStartup();
        log.info("LoginApiController");
        log.info("${openapi.customerOrders.base-path:}");
        if (log.isDebugEnabled()) {
            log.debug("Expired Token for testing: {}", JwtTokenUtil.getInstance().generateExpiredTokenForTesting("ADMIN", "CUSTOMER"));
        }
    }

    @Override
    public ResponseEntity<String> login(final UserDto userDto) {
        return ResponseUtility.serveOK(() -> loginUser(userDto));
    }

    private String loginUser(UserDto userDto) {
        log.trace("LoginApiController.loginUser with {}", userDto);
        log.trace("user DTO: {}", userDto.getUsername());
        User user = makeUser(userDto);
        log.info("user: {} = {}", userDto.getUsername(), user.getUsername());
        User storedUser = userRepository.findByUsername(user.getUsername());
        log.info("Stored User: {}", storedUser);
        if (storedUser == null) {
            throw new AuthorizationServiceException(USER_PASSWORD_COMBINATION_NOT_FOUND);
        }
        if (!encoder.matches(userDto.getPassword(), storedUser.getPassword())) {
            throw new AuthorizationServiceException(USER_PASSWORD_COMBINATION_NOT_FOUND);
        }
        final String token = JwtTokenUtil.getInstance().generateToken(user.getUsername(), storedUser.getRole().toString());
        log.info("token = {}", token);
        return token;
    }

    private User makeUser(UserDto userDto) {
        return objectMapper.convertValue(userDto, User.class);
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private void doDemoStartup() {
        long count = userRepository.count();
        log.info("Total users = {}", count);
        boolean create = count == 0;
        makeUser("User1", UserDto.RoleEnum.CUSTOMER, create);
        makeUser("User2", UserDto.RoleEnum.CUSTOMER, create);
        makeUser("User3", UserDto.RoleEnum.CUSTOMER, create);
        makeUser("Admin1", UserDto.RoleEnum.ADMIN, create);
        makeUser("Admin2", UserDto.RoleEnum.ADMIN, create);
        if (log.isTraceEnabled()) {
            for (int i=0; i<5; ++i) {
                log.trace("Encoding password: {}", encoder.encode("password"));
            }
        }
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private void makeUser(String userName, UserDto.RoleEnum role, boolean create) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(encoder.encode(userName));
        user.setRole(role);
        user.setEmail(String.format("%s@nobody.com", userName));
        if (log.isDebugEnabled()) {
            log.debug("Creating user {} with password {}", user.getUsername(), user.getPassword());
        }
        if (create) {
            userRepository.save(user);
        }
    }

}
