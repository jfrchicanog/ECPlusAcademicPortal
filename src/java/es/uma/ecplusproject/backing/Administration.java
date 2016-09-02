/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import es.uma.ecplusproject.ejb.EdicionLocal;
import es.uma.ecplusproject.entities.Categoria;
import es.uma.ecplusproject.entities.Foto;
import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.Palabra;
import es.uma.ecplusproject.entities.Pictograma;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import es.uma.ecplusproject.entities.Resolucion;
import es.uma.ecplusproject.entities.Video;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author francis
 */
@Named(value = "administration")
@ViewScoped
public class Administration implements Serializable {

    private static final String MENSAJES_BUNDLE = "mensajes";

    @PersistenceContext(unitName = "ECplusProjectRSPU")
    private EntityManager em;
    @Inject
    private LocaleBean localeBean;
    @Inject
    private EdicionLocal edicion;
    

    private ListaPalabras listaSeleccionada;
    private String codigoIdioma;
    private String cadenaIdioma;
    private Palabra palabraSeleccionada;

    // Cache
    private List<ListaPalabras> listasPalabras;
    private List<Categoria> categorias;
    private List<Palabra> palabras;
    private List<RecursoAudioVisual> recursos;
    
    
    public Administration() {
    }

    public Palabra getPalabraSeleccionada() {
        return palabraSeleccionada;
    }

    public void setPalabraSeleccionada(Palabra palabraSeleccionada) {
        this.palabraSeleccionada = palabraSeleccionada;
    }

    public String getCadenaIdioma() {
        return cadenaIdioma;
    }

    public String getCodigoIdioma() {
        return codigoIdioma;
    }

    public void setCodigoIdioma(String codigoIdioma) {
        this.codigoIdioma = codigoIdioma;
    }
    
    public List<ListaPalabras> getListaPalabras() {
        if (listasPalabras != null) {
            return listasPalabras;
        } else {
            return listasPalabras=fetchListasPalabras();
        }
    }

    private List<ListaPalabras> fetchListasPalabras() {
        TypedQuery<ListaPalabras> query = em.createNamedQuery("todas-listas-palabras", ListaPalabras.class);
        return query.getResultList();
    }

    public String getResoluciones(ListaPalabras lista) {
        Map<Resolucion, String> map = lista.getHashes();
        return map.get(Resolucion.BAJA) + ",\n" + map.get(Resolucion.MEDIA) + ",\n" + map.get(Resolucion.ALTA);
    }

    public void removeLista(ListaPalabras lista) {
        // TODO
    }

    public ListaPalabras getListaSeleccionada() {
        return listaSeleccionada;
    }

    public void setListaSeleccionada(ListaPalabras listaSeleccionada) {
        if (this.listaSeleccionada != null && !this.listaSeleccionada.equals(listaSeleccionada)) {
            palabras=null;
        }
        this.listaSeleccionada = listaSeleccionada;
        
    }

    public void onListaSeleccionada(SelectEvent event) {
        // TODO
        //System.out.println("Seleccionada " + listaSeleccionada.getIdioma());
    }

    public void onCodigoIdiomaChanged() {
        cadenaIdioma = cadenaParaIdioma(codigoIdioma);
    }

    public String cadenaParaIdioma(String codigo) {
        Locale l = new Locale(codigo);
        String nombre = localeBean.getDisplayLanguageForLocale(l);

        if (!codigo.equals(nombre)) {
            return nombre;
        } else {
            return "";
        }
    }
    
    public void ficheroSubido(FileUploadEvent event) {
        // TODO
        
    }

    public List<Categoria> getCategorias() {
        if (categorias!=null) {
            return categorias;
        } else {
            return categorias=createCategorias();
        }
    }

    private List<Categoria> createCategorias() {
        List<Categoria> resultado = new ArrayList<>();
        Categoria c = new Categoria();
        c.setNombre("Objeto");
        c.setId(1L);
        resultado.add(c);
        c = new Categoria();
        c.setNombre("Acción");
        c.setId(2L);
        resultado.add(c);
        return resultado;
    }

    public List<Palabra> getPalabras() {
        // TODO
        if (palabras != null) {
            return palabras;
        } else {
            return palabras = fetchPalabras();
        }

    }
    
    public String getResolucionesPalabra(Palabra palabra) {
        Map<Resolucion, String> map = palabra.getHashes();
        return map.get(Resolucion.BAJA) + ",\n" + map.get(Resolucion.MEDIA) + ",\n" + map.get(Resolucion.ALTA);
    }

    private List<Palabra> fetchPalabras() {
        if (listaSeleccionada != null) {
            return edicion.palabrasDeLista(listaSeleccionada);
        } else {
            return null;
        }
    }

    public String getCadenaIdiomaSeleccionado() {
        if (listaSeleccionada == null) {
            return ResourceBundle.getBundle(MENSAJES_BUNDLE).getString("shouldSelectAList");
        } else {
            return localeBean.getDisplayLanguageForLocale(new Locale(listaSeleccionada.getIdioma()));
        }
    }
    
    public String getCadenaPalabraSeleccionada() {
        if (palabraSeleccionada == null) {
            return ResourceBundle.getBundle(MENSAJES_BUNDLE).getString("shouldSelectAWord");
        } else {
            return palabraSeleccionada.getNombre() + " ("+palabraSeleccionada.getId()+")";
        }
    }
    
    public Set<RecursoAudioVisual> getRecursosAudiovisuales() {
        if (palabraSeleccionada!= null) {
            return palabraSeleccionada.getAudiovisuales();
        } else {
            return null;
        }
    }
    
    public String getCadenaTipoRecurso(RecursoAudioVisual rav) {
        if (rav instanceof Pictograma) {
            return ResourceBundle.getBundle(MENSAJES_BUNDLE).getString("pictogram");
        } else if (rav instanceof Video) {
            return ResourceBundle.getBundle(MENSAJES_BUNDLE).getString("video");
        } else if (rav instanceof Foto) {
            return ResourceBundle.getBundle(MENSAJES_BUNDLE).getString("picture");
        } else {
            return ResourceBundle.getBundle(MENSAJES_BUNDLE).getString("unknownType");
        }
    }
    
    
}
