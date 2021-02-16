package org.openapitools.api;

import java.util.Optional;
import javax.validation.Valid;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.engine.Role;
import org.openapitools.engine.UserEngine;
import org.openapitools.entity.User;
import org.openapitools.framework.ResponseUtility;
import org.openapitools.model.LoginDto;
import org.openapitools.model.UserDto;
import org.openapitools.repositories.UserRepository;
import org.openapitools.server.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
@RequestMapping("${openapi.customerOrders.base-path:}")
public class LoginApiController implements LoginApi {

    private static final Logger log = LoggerFactory.getLogger(LoginApiController.class);
    private final NativeWebRequest request;
    
    private final ObjectMapper objectMapper;
    
    private final UserRepository userRepository;
    
    private final UserEngine userEngine;
    
    private final PasswordEncoder encoder;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
    
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public LoginApiController(
            final NativeWebRequest request,
            final ObjectMapper objectMapper,
            final UserRepository userRepository,
            final PasswordEncoder encoder,
            final UserEngine userEngine
    ) {
        this.request = request;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.userEngine = userEngine;
        doDemoStartup();
        log.info("LoginApiController");
        log.info("${openapi.customerOrders.base-path:}");
        if (log.isDebugEnabled()) {
            log.debug("Expired Token for testing: {}", JwtTokenUtil.getInstance().generateExpiredTokenForTesting("ADMIN", "CUSTOMER"));
        }
    }

    @Override
    public ResponseEntity<String> login(final LoginDto loginDto) {
        return ResponseUtility.serveOK(() -> userEngine.loginUser(loginDto));
    }

    private User makeUser(LoginDto loginDto) {
        return objectMapper.convertValue(loginDto, User.class);
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private void doDemoStartup() {
        long count = userRepository.count();
        log.info("Total users = {}", count);
        if (count == 0) {
            makeUser("User1", Role.CUSTOMER);
            makeUser("User2", Role.CUSTOMER);
            makeUser("User3", Role.CUSTOMER);
            makeUser("Admin1", Role.ADMIN);
            makeUser("Admin2", Role.ADMIN);
        }
        if (log.isTraceEnabled()) {
            for (int i=0; i<5; ++i) {
                log.trace("Encoding password: {}", encoder.encode("password"));
            }
        }
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private void makeUser(String userName, Role role) {
        UserDto userDto = new UserDto();
        userDto.setUsername(userName);
        userDto.setPassword(userName);
        userDto.setEmail(String.format("%s@nobody.com", userName));
        if (log.isDebugEnabled()) {
            log.debug("Creating userDto {} with password {}", userDto.getUsername(), userDto.getPassword());
        }
        userEngine.createUser(userDto, role);
    }

    @Override
    public ResponseEntity<Void> addCustomer(@Valid final org.openapitools.model.UserDto userDto) {
        return ResponseUtility.serveOK(() -> userEngine.createUser(userDto, Role.CUSTOMER));
    }
}
