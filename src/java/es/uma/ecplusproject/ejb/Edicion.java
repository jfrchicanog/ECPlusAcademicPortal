/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.ejb;

import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.Palabra;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import es.uma.ecplusproject.entities.Resolucion;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

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
        em.refresh(lista);
        return lista.getPalabras();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public void aniadirListaPalabras(ListaPalabras lista) throws ECPlusBusinessException {
        try {
            TypedQuery<ListaPalabras> query = em.createNamedQuery("idioma", ListaPalabras.class);
            query.setParameter("idioma", lista.getIdioma());

            if (query.getResultList().isEmpty()) {
                recalculaHashes(lista);
                em.persist(lista);
            } else {
                throw new AlreadyExistsException("The list with code " + lista.getIdioma() + " already exists");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new ECPlusBusinessException(e.getMessage());
        }

    }

    private void recalculaHashes(ListaPalabras lp) throws NoSuchAlgorithmException {

        for (Resolucion res : Resolucion.values()) {
            List<String> listaHashes = new ArrayList<>();
            if (lp.getPalabras() != null) {
                for (Palabra palabra : lp.getPalabras()) {
                    listaHashes.add(palabra.getHashes().get(res) + ";");
                }
            }
            Collections.sort(listaHashes);
            StringBuilder buffer = new StringBuilder();
            for (String hash : listaHashes) {
                buffer.append(hash);
            }

            String hash = calculaHash(buffer.toString().getBytes(Charset.forName("UTF-8")));

            lp.updateHash(res, hash);
        }
    }

    private String calculaHash(byte[] contenido) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        String hash = new HexBinaryAdapter().marshal(digest.digest(contenido));
        return hash.toLowerCase();
    }

    @Override
    public void eliminarListaPalabras(ListaPalabras lista) throws ECPlusBusinessException {
        ListaPalabras listaParaEliminar = em.merge(lista);
        if (listaParaEliminar.getPalabras().isEmpty()) {
            em.remove(listaParaEliminar);
        } else {
            throw new ListWithWordsException("The list " + lista.getIdioma() + " contains words");
        }
    }

    @Override
    public Palabra editarPalabra(Palabra palabra) throws ECPlusBusinessException {
        try {
            calculaHashes(palabra);
            Palabra nueva = em.merge(palabra);
            recalculaHashes(nueva.getListaPalabras());
            return nueva;
        } catch (NoSuchAlgorithmException e) {
            throw new ECPlusBusinessException(e.getMessage());
        }
    }

    private void calculaHashes(Palabra palabra) throws NoSuchAlgorithmException {
        Map<Resolucion, String> hashes = palabra.getHashes();
        if (hashes == null) {
            hashes = new HashMap<>();
            palabra.setHashes(hashes);
        }

        for (Resolucion res : Resolucion.values()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(palabra.getNombre()).append(";");
            buffer.append(palabra.getIconoReemplazable()).append(";");
            if (palabra.getIcono()!=null) {
                buffer.append(palabra.getIcono().getFicheros().get(res)).append(";");
            }
            List<String> listaHashes = new ArrayList<>();
            if (palabra.getAudiovisuales() != null) {
                for (RecursoAudioVisual av : palabra.getAudiovisuales()) {
                    listaHashes.add(av.getFicheros().get(res) + ";");
                }
            }
            Collections.sort(listaHashes);
            for (String hash : listaHashes) {
                buffer.append(hash);
            }

            String hash = calculaHash(buffer.toString().getBytes(Charset.forName("UTF-8")));

            hashes.put(res, hash);
        }
    }

    @Override
    public Palabra aniadirPalabra(Palabra palabra) throws ECPlusBusinessException {
        try {
            calculaHashes(palabra);
            em.persist(palabra);
            ListaPalabras lp = palabra.getListaPalabras();
            recalculaHashes(lp);
            em.merge(lp);
            return palabra;
        } catch (NoSuchAlgorithmException e) {
            throw new ECPlusBusinessException(e.getMessage());
        }
    }

}
