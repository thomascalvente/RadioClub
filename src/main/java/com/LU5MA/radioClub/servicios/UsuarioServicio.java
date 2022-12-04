package com.LU5MA.radioClub.servicios;

import com.LU5MA.radioClub.entidades.Usuario;
import com.LU5MA.radioClub.enumeraciones.Rol;
import com.LU5MA.radioClub.excepciones.MiException;
import com.LU5MA.radioClub.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public void registrar(String licencia, String nombre, String email, String password, String password2) throws MiException {

        validar(licencia, nombre, email, password, password2);

        Usuario usuario = new Usuario();

        usuario.setLicencia(licencia);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));

        usuario.setRol(Rol.USER);

        usuarioRepositorio.save(usuario);

    }

    public List<Usuario> listar() {
        return usuarioRepositorio.findAll();
    }

    @Transactional
    public void cambiarRol(String id, Rol rol) {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setRol(rol);

            usuarioRepositorio.save(usuario);
        }
    }

    @Transactional
    public void eliminar(String id) {
        usuarioRepositorio.deleteById(id);
    }

    public List<Usuario> buscarUsuarios(String frase) {
        return usuarioRepositorio.buscarUsuarios(frase);
    }
    
    public Page<Usuario> getAll(Pageable pageable){
        
        return usuarioRepositorio.findAll(pageable);
    }

    public void validar(String licencia, String nombre, String email, String password, String password2) throws MiException {
        
        Usuario usuario = usuarioRepositorio.buscarPorLicenciaYEmail(licencia, email);

        if (licencia.isEmpty() || licencia == null) {
            throw new MiException("La licencia no puede estar vacía");
        }
        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("El nombre no puede estar vacío");
        }
        if (email.isEmpty() || email == null) {
            throw new MiException("El email no puede estar vacío");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MiException("La contraseña no puede estar vacía, y debe tener mas de 5 dígitos");
        }
        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }
        
        if (usuario != null && usuario.getLicencia().equals(licencia)) {
            throw new MiException("La licencia que intenta registrar ya se encuentra registrada, intenta otra");
        }else if(usuario != null && usuario.getEmail().equals(email)){
            throw new MiException("El usuario que intenta registrar ya se encuentra registrado, intenta otro");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());

            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession sesion = attr.getRequest().getSession(true);

            sesion.setAttribute("usuariosession", usuario);

            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            return null;
        }

    }
}
