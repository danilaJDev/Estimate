package cyfr.ae.estimate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "admin/dashboard";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ESTIMATOR"))) {
            return "estimator/dashboard";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"))) {
            return "client/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/admin/work-directory")
    @PreAuthorize("hasRole('ADMIN')")
    public String workDirectory() {
        return "admin/work-directory";
    }

    @GetMapping("/estimates/new")
    @PreAuthorize("hasRole('ESTIMATOR')")
    public String newEstimateForm() {
        return "estimator/form";
    }
}