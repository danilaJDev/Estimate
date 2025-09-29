package cyfr.ae.estimate.dto;

import cyfr.ae.estimate.model.Role;
import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
}