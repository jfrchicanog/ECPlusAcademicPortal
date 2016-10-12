/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Transient;

/**
 *
 * @author francis
 */
@Entity
public class Palabra implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nombre;
    private Boolean iconoReemplazable;
    @ElementCollection (fetch = FetchType.EAGER)
    @MapKeyEnumerated(EnumType.STRING)
    @CollectionTable(name = "HashesPalabra")
    @Column(name="hash")
    @MapKeyColumn(name="resolucion")
    private Map<Resolucion,String> hashes;
    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<RecursoAudioVisual> audiovisuales;
    @ManyToOne(cascade={CascadeType.PERSIST})
    private RecursoAudioVisual icono;
    @ManyToOne
    @JoinColumn(name="listapalabras")
    private ListaPalabras listaPalabras;
    @ManyToOne
    @JoinColumn(name="categoria")
    private Categoria categoria;
    private Boolean avanzada;

    public Boolean getAvanzada() {
        return avanzada;
    }

    public void setAvanzada(Boolean avanzada) {
        this.avanzada = avanzada;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public ListaPalabras getListaPalabras() {
        return listaPalabras;
    }

    public void setListaPalabras(ListaPalabras listaPalabras) {
        this.listaPalabras = listaPalabras;
    }
    
    @Transient
    private RecursoAudioVisual iconoReemplazado;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getIconoReemplazable() {
        return iconoReemplazable;
    }

    public void setIconoReemplazable(Boolean iconoReemplazable) {
        this.iconoReemplazable = iconoReemplazable;
    }

    public Map<Resolucion, String> getHashes() {
        return hashes;
    }

    public void setHashes(Map<Resolucion, String> hashes) {
        this.hashes = hashes;
    }

    public Set<RecursoAudioVisual> getAudiovisuales() {
        return audiovisuales;
    }

    public void setAudiovisuales(Set<RecursoAudioVisual> audiovisuales) {
        this.audiovisuales = audiovisuales;
    }

    public RecursoAudioVisual getIcono() {
        return icono;
    }

    public void setIcono(RecursoAudioVisual icono) {
        this.icono = icono;
    }

    public RecursoAudioVisual getIconoReemplazado() {
        return iconoReemplazado;
    }

    public void setIconoReemplazado(RecursoAudioVisual iconoReemplazado) {
        this.iconoReemplazado = iconoReemplazado;
    }
    
    public void addRecursoAudioVisual(RecursoAudioVisual av) {
        if (audiovisuales == null) {
            setAudiovisuales(new HashSet<>());
        }
        audiovisuales.add(av);
    }
    
    public void removeRecursoAudioVisual(RecursoAudioVisual av) {
        if (audiovisuales != null) {
            audiovisuales.remove(av);
        }
    }
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Palabra)) {
            return false;
        }
        Palabra other = (Palabra) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.uma.ecplusproject.ecplusprojectjpa.Palabra[ id=" + id + " ]";
    }
    
}
