package com.LU5MA.radioClub.repositorios;

import com.LU5MA.radioClub.entidades.Credencial;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CredencialRepositorio extends JpaRepository<Credencial, Long>{
    
    @Query("SELECT c.estacionLlamante FROM Credencial c WHERE c.estacionLlamante != null AND c.licencia = :licencia AND c.id_actividad = :id")
    public List<String> buscarPorEstacionLlamante(@Param("licencia") String licencia, @Param("id") Long id);
    
    @Query("SELECT c FROM Credencial c WHERE c.licencia = :licencia AND c.id_actividad = :id")
    public List<Credencial> buscarPorLicencia(@Param("licencia") String licencia, @Param("id") Long id);
    
    @Query("SELECT c FROM Credencial c WHERE c.licencia = :licencia AND c.id_actividad = :id AND c.id = :idCredencial")
    public List<Credencial> buscarPorLicenciaYId(@Param("licencia") String licencia, @Param("id") Long id, @Param("idCredencial") Long idCredencial);
    
    @Query("SELECT licencia FROM Credencial c WHERE c.id_actividad = :id AND c.id = :idCredencial")
    public String traerLicenciaPorId(@Param("id") Long id, @Param("idCredencial") Long idCredencial);
    
    @Query("SELECT c FROM Credencial c WHERE c.id_actividad = :id")
    public List<Credencial> buscarPorId(@Param("id") Long id);
    
    @Modifying
    @Query("delete from Credencial c where c.id_actividad = :id")
    void borrarCredenciales(@Param("id") Long id);
    
    @Query(value = "SELECT c FROM Credencial c WHERE c.id_actividad = :id") 
    public Page<Credencial> traerCredencialesPorId(@Param("id") Long id, Pageable pageable);
}
