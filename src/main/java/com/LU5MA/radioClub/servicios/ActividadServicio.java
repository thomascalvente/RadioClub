package com.LU5MA.radioClub.servicios;

import com.LU5MA.radioClub.entidades.Actividad;
import com.LU5MA.radioClub.excepciones.MiException;
import com.LU5MA.radioClub.repositorios.ActividadRepositorio;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import net.iharder.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ActividadServicio {

    @Autowired
    private ActividadRepositorio actividadRepositorio;

    @Transactional
    public void crearActividad(String titulo, String cuerpo, MultipartFile imagen, Integer cantidadEstaciones, Integer cantidadEstacionesObligatorias, MultipartFile bases, MultipartFile certificado, MultipartFile tarjeta, String estacionesObligatorias, Integer cantidadEstacionesTarjetas) throws MiException, IOException {
        validar(titulo, cuerpo, cantidadEstaciones, cantidadEstacionesObligatorias);

        Actividad actividad = new Actividad();

        if (!imagen.isEmpty()) {
            try {
                actividad.setImagen(Base64.encodeBytes(imagen.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!bases.isEmpty()) {
            byte[] bytes = bases.getBytes();
            Path path = Paths.get(".//src//main//resources//static//docs//" + bases.getOriginalFilename());
            Files.write(path, bytes);
            actividad.setBases(bases.getOriginalFilename());

        }

        if (!certificado.isEmpty()) {
            byte[] bytes = certificado.getBytes();
            Path path = Paths.get(".//src//main//resources//static//certificadosAndTarjetas//" + certificado.getOriginalFilename());
            Files.write(path, bytes);
            actividad.setCertificado(certificado.getOriginalFilename());
            /*try {
                actividad.setCertificado(Base64.encodeBytes(certificado.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        if (!tarjeta.isEmpty()) {
            byte[] bytes = tarjeta.getBytes();
            Path path = Paths.get(".//src//main//resources//static//certificadosAndTarjetas//" + tarjeta.getOriginalFilename());
            Files.write(path, bytes);
            actividad.setTarjeta(tarjeta.getOriginalFilename());
            /*try {
                actividad.setTarjeta(Base64.encodeBytes(tarjeta.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        actividad.setTitulo(titulo);
        actividad.setCuerpo(cuerpo);
        actividad.setCantidadEstaciones(cantidadEstaciones);
        actividad.setCantidadEstacionesObligatorias(cantidadEstacionesObligatorias);
        actividad.setEstacionesObligatorias(estacionesObligatorias.toUpperCase());
        actividad.setCantidadEstacionesTarjetas(cantidadEstacionesTarjetas);
        actividad.setFecha(LocalDate.now());

        actividadRepositorio.save(actividad);
    }

    public List<Actividad> listarActividades() {
        return actividadRepositorio.listarActividades();
    }

    public Optional<Actividad> findById(Long id) {
        return actividadRepositorio.findById(id);
    }

    @Transactional
    public void actualizar(Long id, String titulo, String cuerpo, MultipartFile imagen, Integer cantidadEstaciones, Integer cantidadEstacionesObligatorias, MultipartFile bases, MultipartFile certificado, MultipartFile tarjeta, String estacionesObligatorias, Integer cantidadEstacionesTarjetas, LocalDate fecha) throws MiException, IOException {

        validar(titulo, cuerpo, cantidadEstaciones, cantidadEstacionesObligatorias);

        Optional<Actividad> respuesta = actividadRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Actividad actividad = respuesta.get();

            actividad.setTitulo(titulo);
            actividad.setCuerpo(cuerpo);
            actividad.setCantidadEstaciones(cantidadEstaciones);
            actividad.setCantidadEstacionesObligatorias(cantidadEstacionesObligatorias);
            actividad.setEstacionesObligatorias(estacionesObligatorias);
            actividad.setCantidadEstacionesTarjetas(cantidadEstacionesTarjetas);

            if (!imagen.isEmpty()) {
                try {
                    actividad.setImagen(Base64.encodeBytes(imagen.getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!bases.isEmpty()) {
                byte[] bytes = bases.getBytes();
                Path path = Paths.get(".//src//main//resources//static//docs//" + bases.getOriginalFilename());
                Files.write(path, bytes);
                actividad.setBases(bases.getOriginalFilename());

            }

            if (!certificado.isEmpty()) {
                byte[] bytes = certificado.getBytes();
                Path path = Paths.get(".//src//main//resources//static//certificadosAndTarjetas//" + certificado.getOriginalFilename());
                Files.write(path, bytes);
                actividad.setCertificado(certificado.getOriginalFilename());
                /*try {
                    actividad.setCertificado(Base64.encodeBytes(certificado.getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            if (!tarjeta.isEmpty()) {
                byte[] bytes = tarjeta.getBytes();
                Path path = Paths.get(".//src//main//resources//static//certificadosAndTarjetas//" + tarjeta.getOriginalFilename());
                Files.write(path, bytes);
                actividad.setTarjeta(tarjeta.getOriginalFilename());
                /*try {
                    actividad.setTarjeta(Base64.encodeBytes(tarjeta.getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            actividad.setFecha(fecha.now());

            actividadRepositorio.save(actividad);
        }
    }

    /*@Transactional
    public void borrar(Long id, LocalDate fecha) {
        Optional<Actividad> respuesta = actividadRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Actividad actividad = respuesta.get();

            actividad.setFecha(fecha.now());
            actividad.setBorrado(true);
            actividadRepositorio.save(actividad);
        }
    }*/
    
    @Transactional
    public void borrar(Long id) {
        actividadRepositorio.deleteById(id);
    }
    
    public Page<Actividad> getAll(Pageable pageable){
        
        return actividadRepositorio.findAll(pageable);
    }
    
    private void validar(String titulo, String cuerpo, Integer cantidadEstaciones, Integer cantidadEstacionesObligatorias) throws MiException {

        if (cantidadEstaciones == null) {
            throw new MiException("Debe ingresar un número en \"Cantidad de estaciones\"");
        }

        if (cantidadEstacionesObligatorias == null) {
            throw new MiException("Debe ingresar un número en \"Cantidad de estaciones Obligatorias\"");
        }

        if (titulo.isEmpty() || titulo == null) {
            throw new MiException("El título no puede estar vacío");
        }

        if (cuerpo.isEmpty() || cuerpo == null) {
            throw new MiException("El cuerpo de la actividad no puede estar vacío");
        }

    }
}
