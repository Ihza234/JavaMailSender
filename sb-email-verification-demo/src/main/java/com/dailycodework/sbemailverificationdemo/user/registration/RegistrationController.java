package com.dailycodework.sbemailverificationdemo.user.registration;

import com.dailycodework.sbemailverificationdemo.event.RegistrationCompleteEvent;
import com.dailycodework.sbemailverificationdemo.user.User;
import com.dailycodework.sbemailverificationdemo.user.UserService;
import com.dailycodework.sbemailverificationdemo.user.registration.token.VerificationToken;
import com.dailycodework.sbemailverificationdemo.user.registration.token.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;

    @PostMapping
    public String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request){
        User user = userService.registerUser(registrationRequest);
        // publish registration event
        publisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl(request)));

        return "Succes! Please Check Your Email To Complete Your Registration";
    }
    @GetMapping("/verifyEmail")
    public  String verifyEmail(@RequestParam("token") String token) {
        VerificationToken theToken = tokenRepository.findByToken(token);
        if (theToken.getUser().isEnabled()){
            return "this account has already been verified, please, login.";
        }
        String verificationResult = userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")){
            return "Email verified succesfully. Now you can login to your account";
        }
        return "invalid verification token";

    }


    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+";"+request.getServerPort()+request.getContextPath();
    }
}
