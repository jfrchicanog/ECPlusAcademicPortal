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
import es.uma.ecplusproject.entities.ListaSindromes;
import es.uma.ecplusproject.entities.Sindrome;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author francis
 */
@Named(value = "documentos")
@ViewScoped
public class Documentos implements Serializable {

    private static final String MENSAJES_BUNDLE = "mensajes";

    @Inject
    private LocaleBean localeBean;
    @Inject
    private EdicionLocal edicion;

    private ListaSindromes listaSeleccionada;
    private String codigoIdioma;
    private String cadenaIdioma;
    private Sindrome documentoSeleccionado;

    // Cache
    private List<ListaSindromes> listaSindromes;
    private Sindrome nuevoDocumento;

    public String getContenidoSeleccionado() {
        if (documentoSeleccionado != null && documentoSeleccionado.getContenido()!=null) {
            return new String(documentoSeleccionado.getContenido(), Charset.forName("UTF-8"));
        }
        return "";
    }

    public void setContenidoSeleccionado(String texto) {
        if (documentoSeleccionado != null) {
            documentoSeleccionado.setContenido(texto.getBytes(Charset.forName("UTF-8")));
        }
    }
    
    public void editarSeleccionado() {
        try {
            Sindrome nuevo = edicion.editarDocumento(documentoSeleccionado);
            documentoSeleccionado.setHash(nuevo.getHash());
            listaSeleccionada.setHash(nuevo.getListaSindromes().getHash());
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }
    }

    public Sindrome getDocumentoSeleccionado() {
        return documentoSeleccionado;
    }

    public void setDocumentoSeleccionado(Sindrome documentoSeleccionado) {
        this.documentoSeleccionado = documentoSeleccionado;
        //System.out.println(documentoSeleccionado.getTipo());
    }

    public void edicionDocumento(RowEditEvent event) {
        Sindrome documento = (Sindrome) event.getObject();
        try {
            Sindrome nuevo = edicion.editarDocumento(documento);
            documento.setHash(nuevo.getHash());
            listaSeleccionada.setHash(nuevo.getListaSindromes().getHash());
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }
    }

    public void eliminarDocumento(Sindrome documento) {
        try {
            ListaSindromes ls = edicion.eliminarDocumento(documento);
            listaSeleccionada.setHash(ls.getHash());
            listaSeleccionada.removeSindrome(documento);
        } catch (ECPlusBusinessException ex) {
            Logger.getLogger(Documentos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Sindrome getNuevoDocumento() {
        if (nuevoDocumento == null) {
            nuevoDocumento = new Sindrome();
        }
        return nuevoDocumento;
    }

    public String getCodigoIdioma() {
        return codigoIdioma;
    }

    public void setCodigoIdioma(String codigoIdioma) {
        this.codigoIdioma = codigoIdioma;
    }

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
            listaSindromes.add(nuevaLista);
            //listaSindromes = null;
        } catch (AlreadyExistsException e) {
            addMessage(e.getMessage());
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }

    }

    public void aniadirDocumento() {
        try {
            nuevoDocumento.setListaSindromes(listaSeleccionada);
            edicion.aniadirDocumento(nuevoDocumento);
            nuevoDocumento = new Sindrome();
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Sindrome> getSindromes() {
        if (listaSeleccionada != null) {
            return listaSeleccionada.getSindromes();
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

    public void removeLista(ListaSindromes lista) {
        try {
            edicion.eliminarListaSindromes(lista);
            listaSindromes.remove(lista);
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
        if (this.listaSeleccionada == null || !this.listaSeleccionada.equals(listaSeleccionada) ) {
            documentoSeleccionado = null;
        }
        this.listaSeleccionada = listaSeleccionada;
    }

    public List<ListaSindromes> getListaSindromes() {
        if (listaSindromes != null) {
            return listaSindromes;
        } else {
            return listaSindromes = edicion.fetchListasindromes();
        }
    }

    

}
