package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(
    name = "seguimiento_usuario",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seguidor_id", "seguido_id"})
    }
)
public class SeguimientoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seguidor_id", nullable = false)
    private Usuario seguidor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seguido_id", nullable = false)
    private Usuario seguido;

    public SeguimientoUsuario() {
    }

    public SeguimientoUsuario(Usuario seguidor, Usuario seguido) {
        this.seguidor = seguidor;
        this.seguido = seguido;
    }

    public boolean esValido() {
        return seguidor != null
                && seguido != null
                && !Objects.equals(seguidor, seguido);
    }

    public Long getId() {
        return id;
    }

    public Usuario getSeguidor() {
        return seguidor;
    }

    public void setSeguidor(Usuario seguidor) {
        this.seguidor = seguidor;
    }

    public Usuario getSeguido() {
        return seguido;
    }

    public void setSeguido(Usuario seguido) {
        this.seguido = seguido;
    }
}