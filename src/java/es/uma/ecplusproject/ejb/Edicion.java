/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.ejb;

import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.Palabra;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author francis
 */
@Stateless
public class Edicion implements EdicionLocal {
    @PersistenceContext(unitName = "ECplusProjectRSPU")
    private EntityManager em;
    
    @Override
    public List<Palabra> palabrasDeLista(ListaPalabras listaSeleccionada) {
        ListaPalabras lista = em.merge(listaSeleccionada);
        return lista.getPalabras();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
