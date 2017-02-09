/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import es.uma.ecplusproject.backing.util.WordsBatchSyntax;
import es.uma.ecplusproject.ejb.ECPlusBusinessException;
import es.uma.ecplusproject.ejb.EdicionLocal;
import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import es.uma.ecplusproject.entities.Video;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author francis
 */
@Named(value = "deleteResources")
@ViewScoped
public class DeleteResources implements Serializable {

    @Inject
    private EdicionLocal edicion;

    private List<ListaPalabras> listaPalabras;
    
    private ListaPalabras listaSeleccionada;
    
    private String resultado;
    private String comandos;

    
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

    public List<ListaPalabras> getListaPalabras() {
        if (listaPalabras == null) {
            listaPalabras = edicion.fetchListasPalabras();
            listaSeleccionada = listaPalabras.get(0);
        }
        return listaPalabras;
    }

    public void procesar() {
        if (listaSeleccionada == null || comandos == null) {
            resultado = "Seleccione la lista y escriba las palabras";
            return;
        }
        
        Stream.of(comandos.split("\\r?\\n")).forEach(line->{
            processEntry(line);
        });
        resultado = "Ejecutado con éxito";
        
    }

    private void processEntry(String origin) {
        WordsBatchSyntax.lineToWord(listaSeleccionada, origin)
                .forEach(p->{
                    Iterator<RecursoAudioVisual> it = p.getAudiovisuales().iterator();
                    while (it.hasNext()) {
                        RecursoAudioVisual rav = it.next();
                        if (!(rav instanceof Video)) {
                            it.remove();
                        }
                    }
                    try {
                        edicion.editarPalabra(p);
                    } catch (ECPlusBusinessException e) {
                        System.out.println(e.getMessage());
                    }
                });
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
