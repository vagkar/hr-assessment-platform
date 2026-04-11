package com.hrplatform.backend.service;

import com.hrplatform.backend.exception.BadRequestException;
import com.hrplatform.backend.model.dto.auth.AuthResponse;
import com.hrplatform.backend.model.dto.auth.LoginRequest;
import com.hrplatform.backend.model.dto.auth.RegisterRequest;
import com.hrplatform.backend.model.entity.Company;
import com.hrplatform.backend.model.entity.User;
import com.hrplatform.backend.repository.CompanyRepository;
import com.hrplatform.backend.repository.UserRepository;
import com.hrplatform.backend.security.JwtTokenProvider;
import com.hrplatform.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (companyRepository.existsByEmail(request.companyEmail())) {
            throw new BadRequestException("Company email already registered: " + request.companyEmail());
        }
        if (userRepository.existsByEmail(request.adminEmail())) {
            throw new BadRequestException("Admin email already registered: " + request.adminEmail());
        }

        Company company = Company.builder()
                .name(request.companyName())
                .email(request.companyEmail())
                .build();
        company = companyRepository.save(company);

        User user = User.builder()
                .company(company)
                .name(request.adminName())
                .email(request.adminEmail())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role("ADMIN")
                .build();
        user = userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(
                user.getId(), company.getId(), user.getEmail(), user.getRole()
        );

        return new AuthResponse(
                jwtTokenProvider.generateToken(principal),
                user.getId(),
                company.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("User not found"));

        UserPrincipal principal = new UserPrincipal(
                user.getId(), user.getCompany().getId(), user.getEmail(), user.getRole()
        );

        return new AuthResponse(
                jwtTokenProvider.generateToken(principal),
                user.getId(),
                user.getCompany().getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}
