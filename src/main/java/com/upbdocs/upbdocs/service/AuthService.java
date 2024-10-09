package com.upbdocs.upbdocs.service;

import com.upbdocs.upbdocs.model.Role;
import com.upbdocs.upbdocs.model.User;
import com.upbdocs.upbdocs.model.ERole;
import com.upbdocs.upbdocs.repository.RoleRepository;
import com.upbdocs.upbdocs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final EmailSenderService emailSenderService;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

     @Autowired
    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService,
                       AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsServiceImpl,
                       EmailSenderService emailSenderService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
         this.emailSenderService = emailSenderService;
     }

    public String registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        if (!isValidEmail(email)) {
            throw new RuntimeException("Error: Invalid email format!");
        }

        if (!isValidPassword(password)) {
            throw new RuntimeException("Error: Password doesn't meet the requirements!");
        }

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        String verificationCode = UUID.randomUUID().toString();

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(userRole)
                .verificationCode(verificationCode)
                .enabled(false)
                .build();
        userRepository.save(user);

        emailSenderService.sendVerificationEmail(user.getEmail(), verificationCode);

        return "User registered successfully! Please check your email to verify your account.";
    }

    public String authenticateUser(String username, String password) {
        try {
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!user.isEnabled()) {
                throw new RuntimeException("Account is not verified. Please check your email for verification instructions.");
            }

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return jwtService.generateToken(userDetails);

        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("User not found", e);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid credentials", e);
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }

     private boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return Pattern.compile(PASSWORD_REGEX).matcher(password).matches();
    }



     public String verifyUser(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new RuntimeException("Invalid verification code"));

        user.setVerificationCode(null);
        user.setEnabled(true);
        userRepository.save(user);

        return "Account verified successfully. You can now log in.";
     }
}

