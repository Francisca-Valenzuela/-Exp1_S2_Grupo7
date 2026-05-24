-- ============================================================
-- Script de datos iniciales para el Minimarket
-- Todos los usuarios tienen la contraseña: password
-- ============================================================

-- Insertar roles
INSERT INTO rol (id, nombre) VALUES (1, 'ROLE_CLIENTE');
INSERT INTO rol (id, nombre) VALUES (2, 'ROLE_EMPLEADO');
INSERT INTO rol (id, nombre) VALUES (3, 'ROLE_GERENTE');


-- Insertar usuarios (Contraseña 100% compatible para todos: password)
INSERT INTO usuario (id, username, password) 
VALUES (1, 'cliente1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'); 

INSERT INTO usuario (id, username, password) 
VALUES (2, 'empleado1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');

INSERT INTO usuario (id, username, password) 
VALUES (3, 'gerente1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');

-- Asignar roles a usuarios
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (1, 1); -- cliente1 -> ROLE_CLIENTE
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (2, 2); -- empleado1 -> ROLE_EMPLEADO
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (3, 3); -- gerente1 -> ROLE_GERENTE