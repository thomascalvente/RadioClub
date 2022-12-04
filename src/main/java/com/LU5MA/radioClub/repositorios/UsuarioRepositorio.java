package com.LU5MA.radioClub.repositorios;

import com.LU5MA.radioClub.entidades.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String>{
    
    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
     public Usuario encontrarUsuarioPorId(@Param ("id") String id );
    
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Usuario buscarPorEmail(@Param("email")String email);
    
    @Query("SELECT u FROM Usuario u WHERE u.licencia = :licencia OR u.email = :email")
    public Usuario buscarPorLicenciaYEmail(@Param("licencia")String licencia, @Param("email")String email);
    
    @Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:frase% OR u.email LIKE %:frase% OR u.licencia LIKE %:frase% ORDER BY u.nombre")
    public List<Usuario> buscarUsuarios(@Param("frase") String frase);
       
}
