package cl.tienda.usuarios.dto;

import cl.tienda.usuarios.model.Usuario;

// Lo que se devuelve a React: igual que Usuario pero SIN la contraseña.
public record UsuarioRespuesta(Long id, String nombre, String correo, String rol) {

    public static UsuarioRespuesta desde(Usuario usuario) {
        return new UsuarioRespuesta(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol());
    }
}
