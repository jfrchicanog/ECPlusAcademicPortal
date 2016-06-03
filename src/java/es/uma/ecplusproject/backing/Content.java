/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import es.uma.ecplusproject.entities.Foto;
import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.ListaSindromes;
import es.uma.ecplusproject.entities.Palabra;
import es.uma.ecplusproject.entities.Pictograma;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import es.uma.ecplusproject.entities.Sindrome;
import es.uma.ecplusproject.entities.Video;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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
    
    private List<Sindrome> sindromes;
    private Sindrome sindromeElegido;

    public Sindrome getSindromeElegido() {
        return sindromeElegido;
    }

    public void setSindromeElegido(Sindrome sindromeElegido) {
        this.sindromeElegido = sindromeElegido;
    }

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
    
    private List<Sindrome> fetchSyndromes() {
        TypedQuery<ListaSindromes> consulta = em.createNamedQuery("sindromes-idioma", ListaSindromes.class);
        consulta.setParameter("idioma", "cat");
        List<ListaSindromes> resultado = consulta.getResultList();
        if (resultado.isEmpty()) {
            return new ArrayList<>();
        } else {
            return resultado.get(0).getSindromes();
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
    
    public List<Sindrome> getSyndromes() {
        if (sindromes == null) {
            sindromes = fetchSyndromes();
        }
        return sindromes;
    }
    
    public String getDetalleSindrome() {
        if (sindromeElegido != null) {
            return new String(sindromeElegido.getContenido(), Charset.forName("UTF-8"));
        }
        else {
            return "No se ha elegido síndrome";
        }
    }
    

    public String getImageURL(String hash) {
        return "/ecplus/api/v1/foto/"+hash;
    }
    
    public String getVideoURL(String hash) {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        System.out.println(contextPath);
        return contextPath+"/ecplus/api/v1/video/"+hash;
    }
    
    public String getPictogramaURL(String hash) {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        System.out.println(contextPath);
        return contextPath+"/ecplus/api/v1/pictograma/"+hash;
    }
    
    public String getSindromeURL() {
        if (sindromeElegido != null) {
            String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
            System.out.println(contextPath);
            return contextPath+"/ecplus/api/v1/sindrome/"+sindromeElegido.getId();
        }
        else {
            return "";
        }
    }
    
    public void loginButton(ActionEvent actionEvent) {
        addMessage("Login no disponible");
    }
    
    public boolean esVideo(RecursoAudioVisual rav) {
        return rav instanceof Video;
    }
    
    public boolean esPictograma(RecursoAudioVisual rav) {
        return rav instanceof Pictograma;
    }
    
    public boolean esFoto(RecursoAudioVisual rav) {
        return rav instanceof Foto;
    }
     
    private void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
}
