package com.minimarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador con endpoints públicos del Minimarket.
 *
 * Los endpoints bajo /public/** están configurados en SecurityConfig
 * como permitAll(), es decir, accesibles sin autenticación.
 *
 * Propósito: Informar al cliente que la aplicación está disponible
 * y proporcionar un punto de entrada sin requerir credenciales.
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    /**
     * Endpoint público de bienvenida.
     * Accesible sin autenticación: GET /public/bienvenida
     */
    @GetMapping("/bienvenida")
    public String bienvenida() {
        return "¡Bienvenido al Minimarket! Este es un recurso público.";
    }

    /**
     * Endpoint público de estado del sistema.
     * Accesible sin autenticación: GET /public/estado
     */
    @GetMapping("/estado")
    public String estado() {
        return "Sistema Minimarket operativo. Por favor, inicia sesión para acceder a todas las funcionalidades.";
    }
}