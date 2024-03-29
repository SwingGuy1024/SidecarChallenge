package com.neptunedreams.api;

import java.util.Optional;
import javax.validation.Valid;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neptunedreams.auth.JwtTokenUtil;
import com.neptunedreams.userservice.Role;
import com.neptunedreams.userservice.UserService;
import com.neptunedreams.framework.ResponseUtility;
import com.neptunedreams.model.LoginDto;
import com.neptunedreams.model.UserDto;
import com.neptunedreams.repository.UserRepository;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
@RequestMapping("${openapi.customerOrders.base-path:}")
public class LoginApiController implements LoginApi {

    private static final @NonNls Logger log = LoggerFactory.getLogger(LoginApiController.class);
    private final NativeWebRequest request;
    
    private final ObjectMapper objectMapper;
    
    private final UserRepository userRepository;
    
    private final UserService userService;
    
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
            final UserService userService
    ) {
        this.request = request;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.userService = userService;
        log.trace("LoginApiController");
        if (log.isDebugEnabled()) {
            log.debug("Expired Token for testing: {}", JwtTokenUtil.instance.generateExpiredTokenForTesting("ADMIN", "ADMIN"));
            doDemoStartup();
        }
    }

    @Override
    public ResponseEntity<String> login(final LoginDto loginDto) {
        return ResponseUtility.serveOK(() -> userService.loginUser(loginDto));
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private void doDemoStartup() {
        long count = userRepository.count();
        log.debug("Total users = {}", count);
        if (count == 0) {
            makeUser("User1", Role.CUSTOMER);
            makeUser("User2", Role.CUSTOMER);
            makeUser("User3", Role.CUSTOMER);
            makeUser("Admin1", Role.ADMIN);
            makeUser("Admin2", Role.ADMIN);
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
        userService.createUser(userDto, role);
    }

    @Override
    public ResponseEntity<Void> addCustomer(@Valid final UserDto userDto) {
        return ResponseUtility.serveOK(() -> userService.createUser(userDto, Role.CUSTOMER));
    }
}
