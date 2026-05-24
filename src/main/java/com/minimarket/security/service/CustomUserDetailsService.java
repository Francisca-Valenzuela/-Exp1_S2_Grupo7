package com.minimarket.security.service;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.security.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio de carga de usuarios para Spring Security.
 *
 * Implementa UserDetailsService, que es la interfaz que Spring Security
 * llama durante el proceso de autenticación para obtener los datos del usuario.
 *
 * Flujo de autenticación:
 *  1. Usuario envía username + password al formulario de login.
 *  2. UsernamePasswordAuthenticationFilter captura las credenciales.
 *  3. AuthenticationManager delega en DaoAuthenticationProvider.
 *  4. DaoAuthenticationProvider llama a loadUserByUsername() de esta clase.
 *  5. Se consulta la BD por el username.
 *  6. Se compara el password ingresado con el BCrypt almacenado.
 *  7. Si coincide → autenticación exitosa → se actualiza SecurityContext.
 *
 * MITIGACIÓN de Inyección SQL:
 *  - Se usa Spring Data JPA con findByUsername(), que usa consultas parametrizadas
 *    internamente (PreparedStatement), previniendo inyección SQL por diseño.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga un usuario desde la base de datos por su nombre de usuario.
     *
     * @param username Nombre de usuario ingresado en el formulario de login.
     * @return UserDetails con la información del usuario para Spring Security.
     * @throws UsernameNotFoundException Si el usuario no existe en la BD.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Consulta parametrizada - PREVIENE INYECCIÓN SQL
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));
        return new CustomUserDetails(usuario);
    }
}