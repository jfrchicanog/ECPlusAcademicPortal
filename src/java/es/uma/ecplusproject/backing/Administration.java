/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import es.uma.ecplusproject.ejb.AlreadyExistsException;
import es.uma.ecplusproject.ejb.ECPlusBusinessException;
import es.uma.ecplusproject.ejb.EdicionLocal;
import es.uma.ecplusproject.ejb.ListWithWordsException;
import es.uma.ecplusproject.entities.Categoria;
import es.uma.ecplusproject.entities.Foto;
import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.Palabra;
import es.uma.ecplusproject.entities.Pictograma;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import es.uma.ecplusproject.entities.Resolucion;
import es.uma.ecplusproject.entities.Video;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
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
    private RecursoAudioVisual recursoElegido;

    private Palabra nuevaPalabra;

    // Cache
    private List<ListaPalabras> listasPalabras;
    private List<Categoria> categorias;
    private List<Palabra> palabras;

    public Administration() {
    }

    public RecursoAudioVisual getRecursoElegido() {
        return recursoElegido;
    }

    public void setRecursoElegido(RecursoAudioVisual recursoElegido) {
        this.recursoElegido = recursoElegido;
    }

    public Palabra getNuevaPalabra() {
        if (nuevaPalabra == null) {
            nuevaPalabra = new Palabra();
        }
        return nuevaPalabra;
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
            return listasPalabras = fetchListasPalabras();
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
        try {
            edicion.eliminarListaPalabras(lista);
            listasPalabras = null;

            if (listaSeleccionada.equals(lista)) {
                listaSeleccionada = null;
                palabraSeleccionada = null;
                palabras = null;
                categorias = null;
            }

        } catch (ListWithWordsException e) {
            addMessage(e.getMessage());
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }

    }

    public ListaPalabras getListaSeleccionada() {
        return listaSeleccionada;
    }

    public void setListaSeleccionada(ListaPalabras listaSeleccionada) {
        if (this.listaSeleccionada != null && !this.listaSeleccionada.equals(listaSeleccionada)) {
            palabras = null;
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
        try {
            File file = File.createTempFile("uploaded", ".temp");
            file.delete();
            event.getFile().write(file.getAbsolutePath());

            Palabra modificada = edicion.aniadirRecursoAPalabra(palabraSeleccionada, event.getFile().getFileName(), file);

            file.delete();

            palabraSeleccionada.setHashes(modificada.getHashes());
            palabraSeleccionada.setAudiovisuales(modificada.getAudiovisuales());
            listaSeleccionada.setHashes(modificada.getListaPalabras().getHashes());

        } catch (IOException e) {
            Logger.getLogger(Administration.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception ex) {
            Logger.getLogger(Administration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Categoria> getCategorias() {
        if (categorias != null) {
            return categorias;
        } else {
            return categorias = createCategorias();
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
            return palabraSeleccionada.getNombre() + " (" + palabraSeleccionada.getId() + ")";
        }
    }

    public Set<RecursoAudioVisual> getRecursosAudiovisuales() {
        if (palabraSeleccionada != null) {
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

    public void aniadirListaDePalabras() {
        ListaPalabras nuevaLista = new ListaPalabras();
        nuevaLista.setIdioma(codigoIdioma);
        try {
            edicion.aniadirListaPalabras(nuevaLista);
            listasPalabras = null;
        } catch (AlreadyExistsException e) {
            addMessage(e.getMessage());
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void edicionPalabra(RowEditEvent event) {
        Palabra palabra = (Palabra) event.getObject();
        try {
            Palabra nueva = edicion.editarPalabra(palabra);
            palabra.setHashes(nueva.getHashes());
            listaSeleccionada.setHashes(nueva.getListaPalabras().getHashes());
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }
    }

    public void aniadirPalabra() {
        try {
            nuevaPalabra.setListaPalabras(listaSeleccionada);
            Palabra insertada = edicion.aniadirPalabra(nuevaPalabra);
            listaSeleccionada.setHashes(insertada.getListaPalabras().getHashes());

            palabras = null;
            nuevaPalabra = new Palabra();
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }
    }

    public void eliminarRecursoAudioVisual(RecursoAudioVisual rav) {
        try {
            Palabra modificada = edicion.eliminarRecursoDePalabra(palabraSeleccionada, rav);

            palabraSeleccionada.setHashes(modificada.getHashes());
            palabraSeleccionada.setAudiovisuales(modificada.getAudiovisuales());
            listaSeleccionada.setHashes(modificada.getListaPalabras().getHashes());

        } catch (ECPlusBusinessException e) {
            Logger.getLogger(Administration.class.getName()).log(Level.SEVERE, e.getMessage());
        }
    }

    public void eliminarPalabra(Palabra p) {
        try {
            ListaPalabras lp = edicion.eliminarPalabra(p);
            listaSeleccionada.setHashes(lp.getHashes());
            palabras.remove(p);
            if (p == palabraSeleccionada) {
                palabraSeleccionada = null;
            }
        } catch (ECPlusBusinessException e) {
            Logger.getLogger(Administration.class.getName()).log(Level.SEVERE, e.getMessage());
        }
    }
    
}
