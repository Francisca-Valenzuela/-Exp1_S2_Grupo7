package com.minimarket.security.config;

import com.minimarket.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;


import static org.springframework.security.config.Customizer.withDefaults;

/**
 * PASO 3: Configuración principal de Spring Security para el Minimarket.
 *
 * Esta clase define:
 *  - Qué endpoints son públicos y cuáles requieren autenticación.
 *  - La estrategia de autenticación: Username y Password (formLogin).
 *  - El codificador de contraseñas: BCrypt.
 *  - Protecciones contra amenazas: CSRF, gestión de sesiones, headers de seguridad.
 *
 * Estrategia seleccionada: Autenticación con nombre de usuario y contraseña.
 * Justificación: Es la estrategia más adecuada para una implementación inicial
 * de un sistema de minimarket con usuarios internos (clientes, empleados, gerentes).
 * No requiere infraestructura externa (LDAP, OAuth2) y se integra directamente
 * con la base de datos existente a través de UserDetailsService.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize a nivel de método
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Define la cadena de filtros de seguridad (SecurityFilterChain).
     *
     * Reglas de autorización por tipo de usuario:
     *  - Público (sin autenticación): consola H2, endpoints de salud.
     *  - ROLE_CLIENTE: puede ver productos y gestionar su propio carrito.
     *  - ROLE_EMPLEADO: puede gestionar inventario y ventas.
     *  - ROLE_GERENTE: acceso total a todos los endpoints administrativos.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // -------------------------------------------------------
            // MITIGACIÓN CSRF:
            // Se deshabilita para APIs REST stateless. En aplicaciones
            // con formularios HTML se debe mantener habilitado.
            // Mitigación CSRF alternativa: uso de tokens CSRF en forms.
            // -------------------------------------------------------
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")  // H2 requiere CSRF deshabilitado
                .disable()  // Deshabilitado para REST API stateless
            )

            // -------------------------------------------------------
            // HEADERS DE SEGURIDAD:
            // Protegen contra Clickjacking y otros ataques de navegador.
            // Se relaja frameOptions solo para H2 console (desarrollo).
            // -------------------------------------------------------
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'"))
                    .xssProtection(xss -> xss
                        .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
            )     


            // -------------------------------------------------------
            // AUTORIZACIÓN DE REQUESTS:
            // Define qué endpoints requieren qué roles.
            // -------------------------------------------------------
            .authorizeHttpRequests(auth -> auth

                // Recursos públicos - sin autenticación
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Endpoints de productos: visibles para todos los autenticados
                .requestMatchers(HttpMethod.GET, "/api/productos/**").authenticated()

                // Endpoints de gestión de productos/inventario: solo EMPLEADO y GERENTE
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasAnyRole("EMPLEADO", "GERENTE")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("EMPLEADO", "GERENTE")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("GERENTE")

                // Gestión de inventario: EMPLEADO y GERENTE
                .requestMatchers("/api/inventario/**").hasAnyRole("EMPLEADO", "GERENTE")

                // Gestión de ventas: EMPLEADO y GERENTE
                .requestMatchers("/api/ventas/**").hasAnyRole("EMPLEADO", "GERENTE")

                // Carrito: acceso de CLIENTE y superiores
                .requestMatchers("/api/carrito/**").hasAnyRole("CLIENTE", "EMPLEADO", "GERENTE")

                // Gestión de usuarios: solo GERENTE
                .requestMatchers("/api/usuarios/**").hasRole("GERENTE")

                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            )

            // -------------------------------------------------------
            // ESTRATEGIA DE AUTENTICACIÓN: Username y Password
            // formLogin habilita el formulario de login proporcionado
            // por Spring Security con configuración predeterminada.
            // -------------------------------------------------------
            .formLogin(form -> form
                .defaultSuccessUrl("/public/bienvenida", true)
                .permitAll()
            )

            // Configuración de logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/public/bienvenida")
                .invalidateHttpSession(true)    // Invalida sesión al salir
                .deleteCookies("JSESSIONID")    // Elimina cookie de sesión
                .permitAll()
            );

        return http.build();
    }

    /**
     * PASO 4: AuthenticationManager.
     * Gestiona el proceso central de autenticación delegando
     * en el CustomUserDetailsService configurado.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * PASO 4: Codificador de contraseñas BCrypt.
     *
     * BCrypt es el estándar de la industria para almacenar contraseñas:
     * - Aplica sal aleatoria automáticamente (previene ataques de rainbow table).
     * - Resistente a ataques de fuerza bruta por su factor de coste configurable.
     * - MITIGACIÓN: previene exposición de datos sensibles (contraseñas en texto plano).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}