package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.entidades.Rol;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.RolRepositorio;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import com.jkmconfecciones.Integrador_app.service.Impl.RegistroServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistroServiceImplTest {

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    @Mock
    private RolRepositorio rolRepositorio;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistroServiceImpl registroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistroExitoso() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Patrick");
        usuario.setCorreo("roche12369874@gmail.com");
        usuario.setContraseña("clave12345");

        Rol rol = new Rol();
        rol.setNombreRol("Usuario");

        when(usuarioRepositorio.findByCorreo(usuario.getCorreo())).thenReturn(Optional.empty());
        when(rolRepositorio.findAll()).thenReturn(List.of(rol));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        String resultado = registroService.registrarUsuario(usuario, "captchaFake", "127.0.0.1");

        assertEquals("OK", resultado);
        verify(usuarioRepositorio, times(1)).save(any(Usuario.class));
    }

    @Test
    void testCorreoInvalido() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Patrick");
        usuario.setCorreo("correo_invalido");
        usuario.setContraseña("clave12345");

        String resultado = registroService.registrarUsuario(usuario, "captchaFake", "127.0.0.1");

        assertEquals("Por favor ingresa un correo electrónico válido.", resultado);
    }

    @Test
    void testCorreoYaRegistrado() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Patrick");
        usuario.setCorreo("patrick@example.com");
        usuario.setContraseña("clave12345");

        when(usuarioRepositorio.findByCorreo(usuario.getCorreo())).thenReturn(Optional.of(new Usuario()));

        String resultado = registroService.registrarUsuario(usuario, "captchaFake", "127.0.0.1");

        assertEquals("El correo ya está registrado.", resultado);
    }
}
