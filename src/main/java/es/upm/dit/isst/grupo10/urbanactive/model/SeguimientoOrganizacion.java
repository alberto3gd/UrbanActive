package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "seguimiento_organizacion",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seguidor_id", "organizacion_id"})
    }
)
public class SeguimientoOrganizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seguidor_id", nullable = false)
    private Usuario seguidor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    public SeguimientoOrganizacion() {
    }

    public SeguimientoOrganizacion(Usuario seguidor, Organizacion organizacion) {
        this.seguidor = seguidor;
        this.organizacion = organizacion;
    }

    public boolean esValido() {
        return seguidor != null && organizacion != null;
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

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }
}