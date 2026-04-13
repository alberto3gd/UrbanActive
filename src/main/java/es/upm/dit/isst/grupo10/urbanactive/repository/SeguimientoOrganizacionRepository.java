package es.upm.dit.isst.grupo10.urbanactive.repository;

import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.SeguimientoOrganizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeguimientoOrganizacionRepository extends JpaRepository<SeguimientoOrganizacion, Long> {

    boolean existsBySeguidorAndOrganizacion(Usuario seguidor, Organizacion organizacion);                             // Verificación de existencia de un seguimiento

    Optional<SeguimientoOrganizacion> findBySeguidorAndOrganizacion(Usuario seguidor, Organizacion organizacion);     // Búsqueda de un seguimiento por su usuario seguidor y organización seguida

    void deleteBySeguidorAndOrganizacion(Usuario seguidor, Organizacion organizacion);                                // Borrado de un seguimiento

    long countByOrganizacion(Organizacion organizacion);                                                              // Conteo de seguidores de una organización

    List<SeguimientoOrganizacion> findBySeguidor(Usuario seguidor);                                                   // Listado de seguimientos realizados por un usuario   

    List<SeguimientoOrganizacion> findByOrganizacion(Organizacion organizacion);                                      // Listado de seguimientos recibidos por una organización   
}