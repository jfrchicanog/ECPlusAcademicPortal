/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import es.uma.ecplusproject.ejb.AlreadyExistsException;
import es.uma.ecplusproject.ejb.ECPlusBusinessException;
import es.uma.ecplusproject.ejb.EdicionLocal;
import es.uma.ecplusproject.ejb.ListWithDocumentsException;
import es.uma.ecplusproject.ejb.ListWithWordsException;
import es.uma.ecplusproject.entities.ListaSindromes;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
@Named(value = "documentos")
@ViewScoped
public class Documentos implements Serializable {

    private static final String MENSAJES_BUNDLE = "mensajes";

    @PersistenceContext(unitName = "ECplusProjectRSPU")
    private EntityManager em;
    @Inject
    private LocaleBean localeBean;
    @Inject
    private EdicionLocal edicion;
    
    
    private ListaSindromes listaSeleccionada;
    private String codigoIdioma;
    
    // Cache
    private List<ListaSindromes> listaSindromes;
    

    public String getCodigoIdioma() {
        return codigoIdioma;
    }

    public void setCodigoIdioma(String codigoIdioma) {
        this.codigoIdioma = codigoIdioma;
    }
    private String cadenaIdioma;

    /**
     * Creates a new instance of Documentos
     */
    public Documentos() {
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

    public String getCadenaIdioma() {
        return cadenaIdioma;
    }
    
    public void aniadirListaDeSindromes() {
        ListaSindromes nuevaLista = new ListaSindromes();
        nuevaLista.setIdioma(codigoIdioma);
        try {
            edicion.aniadirListaSindromes(nuevaLista);
            listaSindromes=null;
        } catch (AlreadyExistsException e) {
            addMessage(e.getMessage());
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    public void removeLista(ListaSindromes lista) {
        try {
            edicion.eliminarListaSindromes(lista);
            listaSindromes = null;
            if (listaSeleccionada.equals(lista)) {
                listaSeleccionada = null;
            }

        } catch (ListWithDocumentsException e) {
            addMessage(e.getMessage());
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
     public ListaSindromes getListaSeleccionada() {
        return listaSeleccionada;
    }

    public void setListaSeleccionada(ListaSindromes listaSeleccionada) {
        this.listaSeleccionada = listaSeleccionada;
    }
    
    
    public List<ListaSindromes> getListaSindromes() {
        if (listaSindromes != null) {
            return listaSindromes;
        } else {
            return listaSindromes = fetchListasindromes();
        }
    }
    
    private List<ListaSindromes> fetchListasindromes() {
        TypedQuery<ListaSindromes> query = em.createNamedQuery("todas-listas-sindromes", ListaSindromes.class);
        return query.getResultList();
    }
    
}
