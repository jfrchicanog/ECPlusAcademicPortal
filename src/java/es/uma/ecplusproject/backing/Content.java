/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.Palabra;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author francis
 */
@Named(value = "content")
@ViewScoped
public class Content implements Serializable {
    
    @PersistenceContext(unitName = "ECplusProjectRSPU")
    private EntityManager em;
    
    private String idioma;
    private String palabra;
    private List<Palabra> palabras;
    private Palabra palabraElegida;

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
        palabraElegida = null;
        for (Palabra p: getWords()) {
            if (p.getNombre().equals(palabra)) {
                palabraElegida = p;
                break;
            }
        }
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    
    public List<String> autocompletar(String texto) {
        List<String> resultado = new ArrayList<>();
        for (Palabra palabra: getWords()) {
            if (palabra.getNombre().startsWith(texto)) {
                resultado.add(palabra.getNombre());
            }
        }
        return resultado;
    }
    
    private List<Palabra> fetchWords() {
        TypedQuery<ListaPalabras> consulta = em.createNamedQuery("idioma", ListaPalabras.class);
        consulta.setParameter("idioma", "cat");
        List<ListaPalabras> resultado = consulta.getResultList();
        if (resultado.isEmpty()) {
            return new ArrayList<>();
        } else {
            return resultado.get(0).getPalabras();
        }
    }
    
    public List<RecursoAudioVisual> getRecursoAudioVisualForWord() {
        List<RecursoAudioVisual> resultado = new ArrayList<>();
        if (palabraElegida != null) {
            resultado.addAll(palabraElegida.getAudiovisuales());
        }
        return resultado;
    }
    
    
    public List<Palabra> getWords() {
        if (palabras == null) {
            palabras = fetchWords();
        }
        return palabras;
    }
    
    public String getImageURL(String hash) {
        return "/ecplus/api/v1/resource/"+hash;
    }
    
}
