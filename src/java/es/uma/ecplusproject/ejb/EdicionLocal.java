/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.ejb;

import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.Palabra;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author francis
 */
@Local
public interface EdicionLocal {

    public List<Palabra> palabrasDeLista(ListaPalabras lista);
    
}
