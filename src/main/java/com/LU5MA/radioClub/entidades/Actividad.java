package com.LU5MA.radioClub.entidades;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;


@Entity
@Data
public class Actividad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String titulo;
    
    @Column(length = 500)
    private String cuerpo;
    
    private boolean borrado = false;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    private String imagen;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    private String bases;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    private String certificado;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String tarjeta;
    
    
    private LocalDate fecha;
    
    private Integer cantidadEstaciones;
    
    private Integer cantidadEstacionesObligatorias;
    
    private String estacionesObligatorias;
    
    private Integer cantidadEstacionesTarjetas;
    
    
    
}
