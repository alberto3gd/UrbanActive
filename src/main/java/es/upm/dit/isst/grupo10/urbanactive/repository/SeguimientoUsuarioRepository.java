package es.upm.dit.isst.grupo10.urbanactive.repository;

import es.upm.dit.isst.grupo10.urbanactive.model.SeguimientoUsuario;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeguimientoUsuarioRepository extends JpaRepository<SeguimientoUsuario, Long> {

    boolean existsBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);                           // Verificación de existencia de un seguimiento

    Optional<SeguimientoUsuario> findBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);        // Búsqueda de un seguimiento por sus dos usuarios

    void deleteBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);                              // Borrado de un seguimiento

    long countBySeguido(Usuario seguido);                                                            // Conteo de seguidores de un usuario

    List<SeguimientoUsuario> findBySeguidor(Usuario seguidor);                                       // Listado de seguimientos realizados por un usuario

    List<SeguimientoUsuario> findBySeguido(Usuario seguido);                                         // Listado de seguimientos recibidos por un usuario
}