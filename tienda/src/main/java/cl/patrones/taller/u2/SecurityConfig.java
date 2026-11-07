package cl.patrones.taller.u2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import cl.patrones.taller.u2.tienda.adapter.UsuarioAnonimo;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.authorizeHttpRequests(authz -> authz.anyRequest().permitAll() )
			.formLogin(form -> form.loginPage("/ingresar").loginProcessingUrl("/ingresar").permitAll())
			.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")).permitAll())
			.anonymous(anonymous -> anonymous.principal( new UsuarioAnonimo()))
		;
		return httpSecurity.build();
	}

    @Bean
    PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
}
