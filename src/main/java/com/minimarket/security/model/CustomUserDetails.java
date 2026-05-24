package com.minimarket.security.model;

import com.minimarket.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementación de UserDetails para el Minimarket.
 *
 * Esta clase envuelve la entidad Usuario y adapta sus datos al contrato
 * que Spring Security necesita para el proceso de autenticación.
 *
 * Spring Security usará esta clase para:
 *  1. Verificar credenciales (username/password).
 *  2. Cargar los roles/authorities del usuario autenticado.
 *  3. Determinar el estado de la cuenta (activa, bloqueada, expirada).
 *
 * MITIGACIÓN de Autenticación Rota:
 *  - isAccountNonLocked() permite implementar bloqueo por intentos fallidos.
 *  - isCredentialsNonExpired() permite forzar cambio de contraseña periódico.
 *  - isEnabled() permite deshabilitar cuentas sin eliminarlas.
 */
public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Mapea los roles del Usuario a GrantedAuthority de Spring Security.
     * Los nombres de rol deben incluir el prefijo "ROLE_" para que
     * hasRole('GERENTE') funcione correctamente en las reglas de acceso.
     * Ej: nombre "ROLE_GERENTE" en BD → hasRole("GERENTE") en SecurityConfig.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return usuario.getPassword(); // Contraseña encriptada con BCrypt
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    // La cuenta no expira 
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // La cuenta no está bloqueada
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Las credenciales no expiran (se puede mejorar con campo ultimoCambioPassword)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // La cuenta está activa 
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Método auxiliar para acceder al Usuario original si se necesita
    public Usuario getUsuario() {
        return usuario;
    }
}