/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import es.uma.ecplusproject.ejb.ECPlusBusinessException;
import es.uma.ecplusproject.ejb.EdicionLocal;
import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author francis
 */
@Named(value = "recomputeHash")
@ViewScoped
public class RecomputeHash implements Serializable {

    @Inject
    private EdicionLocal edicion;
    
    
    /**
     * Creates a new instance of RecomputeHash
     */
    public RecomputeHash() {
    }
    
    public void recomputeHashes() {
        try {
            edicion.recomputeHashes();
        } catch (ECPlusBusinessException e) {
            System.out.println(e.getMessage());
        }
    }

}
