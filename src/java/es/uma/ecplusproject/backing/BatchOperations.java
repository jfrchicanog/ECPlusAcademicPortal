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
    
    private ListaPalabras listaAvanzadaSeleccionada;
    private boolean avanzada;
    private String resultadoAvanzada;
    private String comandosAvanzada;

    public String getListaAvanzadaSeleccionada() {
        if (listaAvanzadaSeleccionada != null) {
            return listaAvanzadaSeleccionada.getIdioma();
        } else {
            return null;
        }
    }

    public void setListaAvanzadaSeleccionada(String listaAvanzadaSeleccionada) {
        if (listaAvanzadaSeleccionada == null) {
            this.listaAvanzadaSeleccionada = null;
        } else {
            listaPalabras.stream()
                    .filter(lp -> lp.getIdioma().equals(listaAvanzadaSeleccionada))
                    .forEach(lp -> this.listaAvanzadaSeleccionada = lp);
        }
    }

    public boolean isAvanzada() {
        return avanzada;
    }

    public void setAvanzada(boolean avanzada) {
        this.avanzada = avanzada;
    }

    public String getResultadoAvanzada() {
        return resultadoAvanzada;
    }

    public String getComandosAvanzada() {
        return comandosAvanzada;
    }

    public void setComandosAvanzada(String comandosAvanzada) {
        this.comandosAvanzada = comandosAvanzada;
    }
    
    
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
            listaAvanzadaSeleccionada = listaPalabras.get(0);
        }
        return listaPalabras;
    }

    public void procesarCambioAvanzada() {
        System.out.println(avanzada);
        if (listaAvanzadaSeleccionada != null && comandosAvanzada!= null) {
            readWords(comandosAvanzada, listaAvanzadaSeleccionada)
                    .forEach(palabra->{
                    try {
                        palabra.setAvanzada(avanzada);
                        edicion.editarPalabra(palabra);
                    } catch (ECPlusBusinessException e) {
                    }
                });
            comandosAvanzada = "";
            resultadoAvanzada = "Palabras procesadas";
        } else {
            Administration.addMessage("Select a word list and a category first");
        }
    }
    
    public void procesarCambioCategoria() {
        if (listaSeleccionada != null && comandos!= null && categoriaSeleccionada != null) {
            readWords(comandos, listaSeleccionada)
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
    
    public static Stream<Palabra> lineToWord(ListaPalabras lista, String line) {
        try {
            String[] nombreID = line.split(":");
            String nombre = nombreID[0];
            if (nombreID.length > 1) {
                long id = Long.parseLong(nombreID[1]);
                
                return lista.getPalabras().stream()
                        .filter(p->p.getId()==id && 
                                (nombre.isEmpty() || nombre.equals(p.getNombre())));
            } else {
                return lista.getPalabras().stream()
                        .filter(p->nombre.equals(p.getNombre()));
            }
        } catch (NumberFormatException e) {
            return Stream.empty();
        }
    }

    public static Stream<Palabra> readWords(String string, ListaPalabras lista) {
        return Stream.of(string.split("\\r?\\n"))
                .flatMap(line->lineToWord(lista, line));
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
