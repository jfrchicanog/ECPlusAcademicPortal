/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;

/**
 *
 * @author francis
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class RecursoAudioVisual implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    @CollectionTable(name = "Ficheros")
    @Column(name="hash")
    @MapKeyColumn(name="resolucion")
    private Map<Resolucion, String> ficheros;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<Resolucion, String> getFicheros() {
        return ficheros;
    }

    public void setFicheros(Map<Resolucion, String> ficheros) {
        this.ficheros = ficheros;
    }

    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    public void addFichero(Resolucion res, String hash) {
        if (getFicheros() == null) {
            setFicheros(new HashMap<>());
        }
        getFicheros().put(res, hash);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecursoAudioVisual)) {
            return false;
        }
        RecursoAudioVisual other = (RecursoAudioVisual) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.uma.ecplusproject.ecplusprojectjpa.RecursoAudioVisual[ id=" + id + " ]";
    }
    
}
