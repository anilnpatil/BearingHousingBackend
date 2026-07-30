package TVS_SFL.BearingHousingBackend.auth.dto;


import TVS_SFL.BearingHousingBackend.auth.entities.Role;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
   private String username;
    private String password;
    private Role role; 
}
  

