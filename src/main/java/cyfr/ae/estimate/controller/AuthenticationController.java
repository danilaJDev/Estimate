package cyfr.ae.estimate.controller;

import cyfr.ae.estimate.dto.SignUpRequest;
import cyfr.ae.estimate.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignUpRequest request) {
        authenticationService.signup(request);
        return "redirect:/login?registration_success";
    }
}