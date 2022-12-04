package com.LU5MA.radioClub.repositorios;

import com.LU5MA.radioClub.entidades.Actividad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ActividadRepositorio extends JpaRepository<Actividad, Long>{
    
    @Query("SELECT a FROM Actividad a WHERE a.titulo = :titulo")
    public Actividad buscarPorTitulo(@Param("titulo") String titulo);
    
    @Query("SELECT a FROM Actividad a WHERE a.borrado!=true ORDER BY a.fecha desc")
    public List<Actividad> listarActividades();
    
}
