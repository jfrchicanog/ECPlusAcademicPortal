/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.ejb;

import es.uma.ecplusproject.entities.Categoria;
import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.ListaSindromes;
import es.uma.ecplusproject.entities.Palabra;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import es.uma.ecplusproject.entities.Sindrome;
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
    public ListaPalabras eliminarPalabra(Palabra palabra) throws ECPlusBusinessException;
    
    public void aniadirCategoria(Categoria categoria) throws ECPlusBusinessException;
    public void eliminarCategoria(Categoria categoria) throws ECPlusBusinessException;
    public void editarCategoria(Categoria categoria) throws ECPlusBusinessException;
    
    public void aniadirListaSindromes(ListaSindromes lista) throws ECPlusBusinessException;
    public void eliminarListaSindromes(ListaSindromes lista) throws ECPlusBusinessException;
    public List<Sindrome> documentosDeLista(ListaSindromes lista);
    public Sindrome editarDocumento(Sindrome documento) throws ECPlusBusinessException;
    public ListaSindromes eliminarDocumento(Sindrome documento) throws ECPlusBusinessException;
    public void aniadirDocumento(Sindrome nuevoDocumento) throws ECPlusBusinessException;

    public List<ListaPalabras> fetchListasPalabras();
    public List<ListaSindromes> fetchListasindromes();
    
}
