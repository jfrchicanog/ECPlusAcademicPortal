/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.ejb;

import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.Palabra;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import java.io.File;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author francis
 */
@Local
public interface EdicionLocal {

    public List<Palabra> palabrasDeLista(ListaPalabras lista);
    public void aniadirListaPalabras(ListaPalabras lista) throws ECPlusBusinessException;
    public void eliminarListaPalabras(ListaPalabras lista) throws ECPlusBusinessException;
    public Palabra editarPalabra(Palabra palabra) throws ECPlusBusinessException;
    public Palabra aniadirPalabra(Palabra palabra) throws ECPlusBusinessException;
    public Palabra aniadirRecursoAPalabra(Palabra palabra, String nombreOriginal, File fichero) throws ECPlusBusinessException;
    public Palabra eliminarRecursoDePalabra(Palabra palabra, RecursoAudioVisual rav) throws ECPlusBusinessException;
    
}
