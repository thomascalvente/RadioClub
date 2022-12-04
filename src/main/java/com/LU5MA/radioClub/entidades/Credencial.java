package com.LU5MA.radioClub.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;


@Entity
@Data
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date fecha;

    private Long id_actividad;
    
    
    
    private String estacionLlamante;
    private String licencia;
    private String modo;
    private String banda;
    private String freq;
    private String date;
    private String time;
    private String rstSent;
    private String rstRcvd;
    private String operador;

    public Credencial() {
    }

    public Credencial(Date fecha, Long id_actividad, String estacionLlamante, String licencia, String modo, String banda, String freq, String date, String time, String rstSent, String rstRcvd, String operador) {
        this.fecha = fecha;
        this.id_actividad = id_actividad;
        this.estacionLlamante = estacionLlamante;
        this.licencia = licencia;
        this.modo = modo;
        this.banda = banda;
        this.freq = freq;
        this.date = date;
        this.time = time;
        this.rstSent = rstSent;
        this.rstRcvd = rstRcvd;
        this.operador = operador;
    }
    
    
    
    
    

}
