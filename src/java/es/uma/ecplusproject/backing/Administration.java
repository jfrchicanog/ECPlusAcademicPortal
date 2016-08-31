/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.Resolucion;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

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
    
    private ListaPalabras listaSeleccionada;
    private String codigoIdioma;
    private String cadenaIdioma;

    

    

    public Administration() {
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
        TypedQuery<ListaPalabras> query = em.createNamedQuery("todas-listas-palabras", ListaPalabras.class);
        return query.getResultList();
    }
    
    public String getResoluciones(ListaPalabras lista) {
        Map<Resolucion, String> map = lista.getHashes();
        return map.get(Resolucion.BAJA)+", "+map.get(Resolucion.MEDIA)+", "+map.get(Resolucion.ALTA);
    }
    
    public void removeLista(ListaPalabras lista) {
        // TODO
    }

    public ListaPalabras getListaSeleccionada() {
        return listaSeleccionada;
    }

    public void setListaSeleccionada(ListaPalabras listaSeleccionada) {
        this.listaSeleccionada = listaSeleccionada;
    }
    
    public void onListaSeleccionada(SelectEvent event) {
        // TODO
        System.out.println("Seleccionada "+listaSeleccionada.getIdioma());
    }
    
    public void onListaDeseleccionada(UnselectEvent event) {
        // TODO
        System.out.println("Deleccionada "+listaSeleccionada.getIdioma());
    }
    
    public void onCodigoIdiomaChanged() {
        Locale l = new Locale(codigoIdioma);
        String nombre = localeBean.getDisplayLanguageForLocale(l);
        
        if (!codigoIdioma.equals(nombre)) {
            cadenaIdioma = nombre;
        } else {
            cadenaIdioma = "";
        }
    }
}
