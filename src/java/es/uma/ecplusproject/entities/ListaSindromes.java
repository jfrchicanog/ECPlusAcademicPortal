/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 *
 * @author francis
 */
@Entity
@NamedQueries({@NamedQuery(name="sindromes-idioma", query="select l from ListaSindromes l where l.idioma=:idioma"),
    @NamedQuery(name="todas-listas-sindromes", query="select l from ListaSindromes l")})
public class ListaSindromes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String idioma;
    private String hash;
    @OneToMany (fetch = FetchType.EAGER, mappedBy = "listaSindromes")
    private List<Sindrome> sindromes;

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<Sindrome> getSindromes() {
        return sindromes;
    }
    
    public void addSindrome(Sindrome sindrome) {
        if (sindromes == null) {
            sindromes = new ArrayList<>();
        }
        sindromes.add(sindrome);
    }
    
    public void removeSindrome(Sindrome sindrome) {
        if (sindromes != null) {
            sindromes.remove(sindrome);
        }
    }

    public void setSindromes(List<Sindrome> sindromes) {
        this.sindromes = sindromes;
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
        if (!(object instanceof ListaSindromes)) {
            return false;
        }
        ListaSindromes other = (ListaSindromes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.uma.ecplusproject.ecplusprojectjpa.ListaSindromes[ id=" + id + " ]";
    }
    
}
