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
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author francis
 */
@Named(value = "copyResources")
@ViewScoped
public class CopyResources implements Serializable {

    @Inject
    private EdicionLocal edicion;

    private List<ListaPalabras> listaPalabras;
    
    private ListaPalabras listaOrigen;
    private ListaPalabras listaDestino;
    
    private String resultado;
    private String comandos;

    
    public String getListaOrigen() {
        if (listaOrigen != null) {
            return listaOrigen.getIdioma();
        } else {
            return null;
        }
    }
    
    public String getListaDestino() {
        if (listaDestino != null) {
            return listaDestino.getIdioma();
        } else {
            return null;
        }
    }

    public void setListaOrigen(String listaSeleccionada) {
        if (listaSeleccionada == null) {
            this.listaOrigen = null;
        } else {
            listaPalabras.stream()
                    .filter(lp -> lp.getIdioma().equals(listaSeleccionada))
                    .forEach(lp -> this.listaOrigen = lp);
        }
    }
    
    public void setListaDestino(String listaSeleccionada) {
        if (listaSeleccionada == null) {
            this.listaDestino = null;
        } else {
            listaPalabras.stream()
                    .filter(lp -> lp.getIdioma().equals(listaSeleccionada))
                    .forEach(lp -> this.listaDestino = lp);
        }
    }

    
    public List<ListaPalabras> getListaPalabras() {
        if (listaPalabras == null) {
            listaPalabras = edicion.fetchListasPalabras();
            listaOrigen = listaPalabras.get(0);
            listaDestino = listaPalabras.get(0);
        }
        return listaPalabras;
    }

    public void procesar() {
        if (listaOrigen == null || listaDestino == null || comandos == null) {
            resultado = "Seleccione las listas y escriba las palabras";
            return;
        }
        
        if (listaOrigen == listaDestino) {
            resultado = "Las listas origen y destino no pueden ser la misma";
            return;
        }
        
        Stream.of(comandos.split("\\r?\\n")).forEach(line->{
            String [] destOrigin = line.split("\\\t");
            if (destOrigin.length == 2) {
                String destinationWord = destOrigin[0];
                String originWord = destOrigin[1];
                
                processEntry(originWord, destinationWord);
            }
        });
        resultado = "Ejecutado con éxito";
        
    }

    private void processEntry(String origin, String destination) {
        
        Set<RecursoAudioVisual> originResources = WordsBatchSyntax.lineToWord(listaOrigen, origin)
                .flatMap(p->{
                    OptionalLong minId = p.getAudiovisuales().stream()
                            .filter(rav -> (rav instanceof Video))
                            .mapToLong(RecursoAudioVisual::getId).min();
                    if (minId.isPresent()) {
                        return p.getAudiovisuales().stream()
                            .filter(rav -> minId.getAsLong()!=rav.getId());
                    } else {
                        return p.getAudiovisuales().stream();
                    }
                })
                .collect(Collectors.toSet());

        WordsBatchSyntax.lineToWord(listaDestino, destination)
                .forEach(to->{
                    originResources.stream()
                            .filter((ravFrom) -> (to.getAudiovisuales().stream()
                                .map(ravTo->ravTo.getFicheros())
                                .allMatch(files->!files.equals(ravFrom.getFicheros()))))
                            .forEach((ravFrom) -> {
                                to.getAudiovisuales().add(ravFrom);
                            });
                    try {
                        edicion.editarPalabra(to);
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
