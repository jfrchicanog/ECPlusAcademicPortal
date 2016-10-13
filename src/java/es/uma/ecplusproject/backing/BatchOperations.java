/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import es.uma.ecplusproject.ejb.ECPlusBusinessException;
import es.uma.ecplusproject.ejb.EdicionLocal;
import es.uma.ecplusproject.entities.Categoria;
import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.Palabra;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author francis
 */
@Named(value = "batchOperations")
@ViewScoped
public class BatchOperations implements Serializable {

    @Inject
    private EdicionLocal edicion;

    private List<ListaPalabras> listaPalabras;
    private ListaPalabras listaSeleccionada;
    private Categoria categoriaSeleccionada;

    private String resultado;
    private String comandos;

    public Categoria getCategoriaSeleccionada() {
        return categoriaSeleccionada;
    }

    public void setCategoriaSeleccionada(Categoria categoriaSeleccionada) {
        if (categoriaSeleccionada != null && listaSeleccionada != null) {
            int index = listaSeleccionada.getCategorias().indexOf(categoriaSeleccionada);
            if (index < 0) {
                this.categoriaSeleccionada = null;
            } else {
                this.categoriaSeleccionada = listaSeleccionada.getCategorias().get(index);
            }
        } else {
            this.categoriaSeleccionada = null;
        }

    }

    public String getListaSeleccionada() {
        if (listaSeleccionada != null) {
            return listaSeleccionada.getIdioma();
        } else {
            return null;
        }
    }

    public void setListaSeleccionada(String listaSeleccionada) {
        if (listaSeleccionada == null) {
            this.listaSeleccionada = null;
        } else {
            listaPalabras.stream()
                    .filter(lp -> lp.getIdioma().equals(listaSeleccionada))
                    .forEach(lp -> this.listaSeleccionada = lp);
        }
    }

    public List<Categoria> getCategorias() {
        if (listaSeleccionada != null) {
            return listaSeleccionada.getCategorias();
        } else {
            return null;
        }
    }

    public List<ListaPalabras> getListaPalabras() {
        if (listaPalabras == null) {
            listaPalabras = edicion.fetchListasPalabras();
            listaSeleccionada = listaPalabras.get(0);
        }
        return listaPalabras;
    }

    private Stream<Palabra> lineToWord(String line) {
        try {
            String[] nombreID = line.split(":");
            String nombre = nombreID[0];
            if (nombreID.length > 1) {
                long id = Long.parseLong(nombreID[1]);
                
                return listaSeleccionada.getPalabras().stream()
                        .filter(p->p.getId()==id && 
                                (nombre.isEmpty() || nombre.equals(p.getNombre())));
            } else {
                return listaSeleccionada.getPalabras().stream()
                        .filter(p->nombre.equals(p.getNombre()));
            }
        } catch (NumberFormatException e) {
            return Stream.empty();
        }
    }

    public void procesarCambioCategoria() {
        if (listaSeleccionada != null && categoriaSeleccionada != null) {
            Stream.of(comandos.split("\\r?\\n"))
                    .flatMap(this::lineToWord)
                    .forEach(palabra->{
                        try {
                            palabra.setCategoria(categoriaSeleccionada);
                            edicion.editarPalabra(palabra);
                        } catch (ECPlusBusinessException e) {
                        }
                    });
            comandos = "";
            resultado = "Palabras procesadas";
        } else {
            Administration.addMessage("Select a word list and a category first");
        }
    }

    public String getResultado() {
        return resultado;
    }

    public String getComandos() {
        return comandos;
    }

    public void setComandos(String comandos) {
        this.comandos = comandos;
    }

}
