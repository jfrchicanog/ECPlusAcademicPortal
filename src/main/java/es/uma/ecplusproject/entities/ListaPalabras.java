/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 *
 * @author francis
 */
@Entity
@NamedQueries({@NamedQuery(name = "idioma", query = "select l from ListaPalabras l where l.idioma=:idioma"),
                @NamedQuery(name="todas-listas-palabras", query="select l from ListaPalabras l")})
public class ListaPalabras implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String idioma;
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated(EnumType.STRING)
    @CollectionTable(name = "HashesListaPalabras")
    @Column(name="hash")
    @MapKeyColumn(name="resolucion")
    private Map<Resolucion, String> hashes;
    @OneToMany(cascade={CascadeType.MERGE}, fetch = FetchType.EAGER, mappedBy = "listaPalabras")
    @OrderBy("nombre ASC")
    private List<Palabra> palabras;
    @OneToMany(fetch=FetchType.EAGER, mappedBy = "listaPalabras")
    private List<Categoria> categorias;

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public Long getId() {
        return id;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Map<Resolucion, String> getHashes() {
        return hashes;
    }

    public void setHashes(Map<Resolucion, String> hashes) {
        this.hashes = hashes;
    }

    public List<Palabra> getPalabras() {
        return palabras;
    }

    public void setPalabras(List<Palabra> palabras) {
        this.palabras = palabras;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void addPalabra(Palabra p) {
        if (palabras==null) {
            palabras = new ArrayList<>();
        }
        palabras.add(p);
    }
    
    public void removePalabra(Palabra p) {
        if (palabras != null) {
            palabras.remove(p);
        }
    }
    
    public void addCategoria(Categoria cat) {
        if (categorias == null) {
            categorias = new ArrayList<>();
        }
        categorias.add(cat);
    }
    
    public void removeCategoria(Categoria cat) {
        if (categorias != null) {
            categorias.remove(cat);
        }
    }
    
     public void updateHash(Resolucion res, String hash) {
        if (hashes == null) {
            hashes = new HashMap<>();
        }
        hashes.put(res, hash);
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
        if (!(object instanceof ListaPalabras)) {
            return false;
        }
        ListaPalabras other = (ListaPalabras) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.uma.ecplusproject.ecplusprojectjpa.ListaPalabras[ id=" + id + " ]";
    }
    
}
