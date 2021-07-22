package space.nov29.cataria.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import space.nov29.cataria.dto.AuthenticationResponse;
import space.nov29.cataria.dto.LoginRequest;
import space.nov29.cataria.dto.RegisterRequest;
import space.nov29.cataria.model.User;
import space.nov29.cataria.repository.UserRespository;

@Service
@Slf4j
public class AuthService {

    private final UserRespository userRespository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthService(UserRespository userRespository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.userRespository = userRespository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        String password = encodePassword(registerRequest.getPassword());
        user.setPassword(password);
        userRespository.save(user);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword());

        Authentication authenticate = authenticationManager.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String accessToken = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(accessToken);
    }
}
