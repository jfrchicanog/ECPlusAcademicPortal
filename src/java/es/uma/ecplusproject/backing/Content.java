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
import es.uma.ecplusproject.entities.Resolucion;
import es.uma.ecplusproject.entities.Sindrome;
import es.uma.ecplusproject.entities.TipoDocumento;
import es.uma.ecplusproject.entities.Video;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author francis
 */
@Named(value = "content")
@ViewScoped
public class Content implements Serializable {
    
    @PersistenceContext(unitName = "ECplusProjectRSPU")
    private EntityManager em;
    @Inject
    private LocaleBean localeBean;
    
    private List<Locale> idiomas;
    private Locale idiomaSeleccionado;
    
    private List<Palabra> palabras;
    private Palabra palabraElegida;
    
    private List<Sindrome> sindromes;
    private Sindrome sindromeElegido;
    
    
    public Palabra getPalabraElegida() {
        return palabraElegida;
    }

    public void setPalabraElegida(Palabra palabra) {
        palabraElegida = palabra;
    }

    public Sindrome getSindromeElegido() {
        return sindromeElegido;
    }

    public void setSindromeElegido(Sindrome sindromeElegido) {
        this.sindromeElegido = sindromeElegido;
    }


    public boolean getRenderWordResources() {
        return palabraElegida != null;
    }
            
        
    public Resolucion [] getResoluciones() {
        return Resolucion.values();
    }
    
    public TipoDocumento [] getTiposDocumento() {
        return TipoDocumento.values();
    }
    
    public String getLocalizedStringForResolution(String resolucion) {
        return localeBean.getResourceBundle().getString(resolucion);
    }
    
    public String getLocalizedStringForTipoDocumento(String tipoDocumento) {
        return localeBean.getResourceBundle().getString(tipoDocumento);
    }

    public String getIdiomaSeleccionado() {
        if (idiomaSeleccionado == null) {
            prepareLanguages();
        }
        return idiomaSeleccionado.getLanguage();
    }

    public void setIdiomaSeleccionado(String idioma) {
        this.idiomaSeleccionado = new Locale(idioma);
    }
    
    
    private List<Palabra> fetchWords() {
        TypedQuery<ListaPalabras> consulta = em.createNamedQuery("idioma", ListaPalabras.class);
        consulta.setParameter("idioma", getIdiomaSeleccionado());
        List<ListaPalabras> resultado = consulta.getResultList();
        if (resultado.isEmpty()) {
            return new ArrayList<>();
        } else {
            return resultado.get(0).getPalabras();
        }
    }
    
    private List<Sindrome> fetchSyndromes() {
        TypedQuery<ListaSindromes> consulta = em.createNamedQuery("sindromes-idioma", ListaSindromes.class);
        consulta.setParameter("idioma", getIdiomaSeleccionado());
        List<ListaSindromes> resultado = consulta.getResultList();
        if (resultado.isEmpty()) {
            return new ArrayList<>();
        } else {
            return resultado.get(0).getSindromes();
        }
    }
    
    public List<Locale> getSupportedLanguages() {
        if (idiomas == null) {
            prepareLanguages();
        }
        return idiomas;
    }

    private void prepareLanguages() {
        Set<String> codigosIdiomas = new HashSet<>();
        
        TypedQuery<ListaPalabras> consulta = em.createNamedQuery("todas-listas-palabras", ListaPalabras.class);
        for (ListaPalabras lp : consulta.getResultList()){
            codigosIdiomas.add(lp.getIdioma());
        }
        
        TypedQuery<ListaSindromes> sindromes = em.createNamedQuery("todas-listas-sindromes", ListaSindromes.class);
        for (ListaSindromes ls : sindromes.getResultList()){
            codigosIdiomas.add(ls.getIdioma());
        }

        idiomas = new ArrayList<>();
        for (String codigo: codigosIdiomas) {
            idiomas.add(new Locale(codigo));
        }
        
        idiomaSeleccionado = idiomas.get(0);
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
    
    public void onIdiomaChange() {
        palabras = null;
        sindromes = null;
        palabraElegida = null;
        sindromeElegido = null;
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
    
    public String getCurrentLocale() {
        return FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage();
    }
}
