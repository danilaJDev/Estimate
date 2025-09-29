package cyfr.ae.estimate.controller;

import cyfr.ae.estimate.dto.SignInRequest;
import cyfr.ae.estimate.dto.SignUpRequest;
import cyfr.ae.estimate.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public String signup(@ModelAttribute SignUpRequest request, HttpServletResponse response) {
        String jwt = authenticationService.signup(request);
        addJwtCookie(response, jwt);
        return "redirect:/dashboard";
    }

    @PostMapping("/signin")
    public String signin(@ModelAttribute SignInRequest request, HttpServletResponse response) {
        try {
            String jwt = authenticationService.signin(request);
            addJwtCookie(response, jwt);
            return "redirect:/dashboard";
        } catch (Exception e) {
            return "redirect:/login?error";
        }
    }

    private void addJwtCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        response.addCookie(cookie);
    }
}