//package santaOps.santaLog.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.client.RestTemplate;
//import santaOps.santaLog.config.jwt.TokenProvider;
//
//@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity
//public class WebSecurityConfig {
//    private final TokenProvider tokenProvider;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/articles/**").authenticated()
//                        .requestMatchers("/articles/**").permitAll()
//                        .anyRequest().permitAll()
//                );
//
//        return http.build();
//    }
//
//    @Bean
//    public TokenAuthenticationFilter tokenAuthenticationFilter() {
//        return new TokenAuthenticationFilter(tokenProvider);
//    }
//
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//
//}