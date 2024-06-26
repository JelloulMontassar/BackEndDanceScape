package com.dance.mo.auth.Controller;

import com.dance.mo.Config.JwtService;
import com.dance.mo.Entities.User;
import com.dance.mo.Exceptions.UserException;
import com.dance.mo.Services.UserService;
import com.dance.mo.auth.DTO.*;
import com.dance.mo.auth.Service.AuthenticationService;
import com.dance.mo.auth.Service.EmailRegistrationService;
import com.dance.mo.auth.Service.RedisService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.security.Principal;
import java.util.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthenticationController {
    @Value("${jwt.secret}")
    private  String SECRET_KEY;
    private static final long DEFAULT_EXPIRATION_TIME_MILLIS = 604800000;
    private final AuthenticationService service;
    private final EmailRegistrationService emailservice;
    private final UserService userService;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    public static Set<String> onlineUsers = new HashSet<>();
    private static final String CONFIRMATION_URL = "http://localhost:4200/forgot-password/%s";
    ///  endpoint : authenticate an existing user


    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        onlineUsers.remove(email);
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    @GetMapping("/forgot-password/{email}")
    public ResponseEntity<ForgotPasswordResponse> forgetPassword(@PathVariable String email) {
        User user = service.resetUserByEmail(email);
        ForgotPasswordResponse response = new ForgotPasswordResponse(user.getEmail());
        long resetToken = (long) (Math.random()*Math.pow(10,10));
        System.out.println(resetToken);
        user.setResetToken(resetToken);
        String jwtToken = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + DEFAULT_EXPIRATION_TIME_MILLIS))
                .claim("resetToken", resetToken)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        userService.updateUser(user.getUserId(),user);
        try {
            emailservice.send(
                    user.getEmail(),
                    user.getFirstName(),
                    "resetpwd",
                    String.format(CONFIRMATION_URL, jwtToken)
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(response);
    }
    @PostMapping("/forgot-password/")
    public ResponseEntity<ForgotPasswordResponse> CforgetPassword(@RequestBody CforgotPasswordRequest CRequest) {
        User user = service.resetUserByEmail(CRequest.getEmail());
        long cReset  = Long.parseLong(CRequest.getResetToken());
        if (Objects.equals(user.getResetToken() ,cReset)&&user.getEmail().equals(CRequest.getEmail())){
            String newPassword = "newPassword";
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedPassword);
            service.updateUser(user);
            return  ResponseEntity.ok(new ForgotPasswordResponse(CRequest.getEmail()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ForgotPasswordResponse.builder()
                        .messageResponse("An error occurred during password reset")
                        .build());
    }
    @GetMapping("/onlineUsers")
    public Set<String> getOnlineUsers() {
        return onlineUsers;
    }
    @PostMapping("/auth")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = service.authenticate(request);
            onlineUsers.add(response.getEmail());
            return ResponseEntity.ok(response);
        }
        catch (UserException e) {
            if (e.getMessage().equals("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        AuthenticationResponse.builder()
                                .messageResponse("User not found")
                                .build());
            } else if (e.getMessage().equals("User account is not active. Please confirm your email.")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        AuthenticationResponse.builder()
                                .messageResponse("User account is not active. Please confirm your email.")
                                .build());
            } else {
                // Handle any other UserException
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        AuthenticationResponse.builder()
                                .messageResponse("An error occurred during authentication")
                                .build());
            }
        }
    }
}



