/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.ejb;

import es.uma.ecplusproject.entities.Audio;
import es.uma.ecplusproject.entities.Categoria;
import es.uma.ecplusproject.entities.Foto;
import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.ListaSindromes;
import es.uma.ecplusproject.entities.Palabra;
import es.uma.ecplusproject.entities.Pictograma;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import es.uma.ecplusproject.entities.Resolucion;
import es.uma.ecplusproject.entities.Sindrome;
import es.uma.ecplusproject.entities.Video;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
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

    @Resource(name = "ecplus.resources.FILES_DIR")
    private String filesDir;
    @PersistenceContext(unitName = "ECplusProjectRSPU")
    private EntityManager em;

    @Override
    public List<Palabra> palabrasDeLista(ListaPalabras listaSeleccionada) {
        return listaSeleccionada.getPalabras();
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
    public void eliminarListaSindromes(ListaSindromes lista) throws ECPlusBusinessException {
        ListaSindromes listaParaEliminar = em.merge(lista); // error aquí cuando se elimina una lista que se supone vacía
        if (listaParaEliminar.getSindromes().isEmpty()) {
            em.remove(listaParaEliminar);
        } else {
            throw new ListWithDocumentsException("The documents list " + lista.getIdioma() + " contains documents");
        }
    }

    @Override
    public Palabra editarPalabra(Palabra palabra) throws ECPlusBusinessException {
        try {
            calculaHashes(palabra);
            Palabra nueva = em.merge(palabra);
            System.out.println("Nuevo nombre: " + nueva.getNombre());
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
            buffer.append(palabra.getAvanzada()).append(";");
            
            if (palabra.getCategoria() != null) {
                buffer.append(palabra.getCategoria().getId());
            }
            buffer.append(";");
            
            if (palabra.getContraria() != null) {
                buffer.append(palabra.getContraria().getId());
            }
            buffer.append(";");
                    
            if (palabra.getIcono() != null) {
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

    private RecursoAudioVisual procesaFichero(String arg, File fichero) throws Exception {
        String[] nombreExtension = arg.split("\\.(?=[^\\.]+$)");
        if (nombreExtension.length != 2) {
            throw new IllegalArgumentException("File without extension " + arg);
        }
        String extension = nombreExtension[1];

        RecursoAudioVisual av = recursoAudioVisualPorExtension(extension);
        String hash = createSha1(fichero);
        for (Resolucion res : Resolucion.values()) {
            av.addFichero(res, hash);
        }

        File destino = new File(filesDir, hash.toLowerCase());
        if (!destino.exists()) {
            Files.copy(fichero.toPath(), destino.toPath());
        }

        System.out.println("hash:" + hash);
        return av;
    }

    @Override
    public Palabra aniadirRecursoAPalabra(Palabra palabra, String nombreOriginal, File fichero) throws ECPlusBusinessException {
        try {
            RecursoAudioVisual av = procesaFichero(nombreOriginal, fichero);
            em.persist(av);

            Palabra editar = em.merge(palabra);
            editar.addRecursoAudioVisual(av);
            calculaHashes(editar);

            ListaPalabras lp = editar.getListaPalabras();
            recalculaHashes(lp);
            em.merge(lp);

            return editar;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ECPlusBusinessException(e.getMessage());
        }
    }

    private RecursoAudioVisual recursoAudioVisualPorExtension(String extension) throws UnallowedFormatException {
        switch (extension.toLowerCase()) {
            case "svg":
                return new Pictograma();
            case "mp4":
                return new Video();
            case "jpg":
            case "jpeg":
                return new Foto();
            case "mp3":
                return new Audio();
            default:
                throw new UnallowedFormatException("Format " + extension + " not allowed for resources");
        }

    }

    public String createSha1(File file) throws Exception {
        byte[] contenido = Files.readAllBytes(Paths.get(file.toURI()));
        return calculaHash(contenido);
    }

    @Override
    public Palabra eliminarRecursoDePalabra(Palabra palabra, RecursoAudioVisual rav) throws ECPlusBusinessException {
        try {
            Palabra editar = em.merge(palabra);
            editar.removeRecursoAudioVisual(rav);

            calculaHashes(editar);
            ListaPalabras lp = editar.getListaPalabras();
            recalculaHashes(lp);
            em.merge(lp);

            return editar;
        } catch (NoSuchAlgorithmException e) {
            throw new ECPlusBusinessException(e.getMessage());
        }
    }

    @Override
    public ListaPalabras eliminarPalabra(Palabra palabra) throws ECPlusBusinessException {
        try {
            Palabra eliminar = em.merge(palabra);
            ListaPalabras lp = eliminar.getListaPalabras();

            eliminar.setListaPalabras(null);
            lp.removePalabra(eliminar);
            em.remove(eliminar);
            recalculaHashes(lp);
            return lp;
        } catch (NoSuchAlgorithmException e) {
            throw new ECPlusBusinessException(e.getMessage());
        }
    }

    @Override
    public void aniadirListaSindromes(ListaSindromes lista) throws ECPlusBusinessException {
        try {
            TypedQuery<ListaSindromes> query = em.createNamedQuery("sindromes-idioma", ListaSindromes.class);
            query.setParameter("idioma", lista.getIdioma());

            if (query.getResultList().isEmpty()) {
                recalculaHash(lista);
                em.persist(lista);
            } else {
                throw new AlreadyExistsException("The list with code " + lista.getIdioma() + " already exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ECPlusBusinessException(e.getMessage());
        }
    }

    private void calculaHash(Sindrome sindrome) throws NoSuchAlgorithmException {
        String hashContenido;
        if (sindrome.getContenido() != null) {
            hashContenido = calculaHash(sindrome.getContenido());
        } else {
            hashContenido = "";
        }

        String hash = calculaHash((sindrome.getNombre() + ";" + sindrome.getTipo() + ";" + hashContenido).getBytes());

        sindrome.setHash(hash);
    }

    private void recalculaHash(ListaSindromes ls) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        StringBuilder buffer = new StringBuilder();
        if (ls.getSindromes() == null) {
            ls.setSindromes(new ArrayList<>());
        }

        List<String> listaHashes = new ArrayList<>();
        for (Sindrome sindrome : ls.getSindromes()) {
            listaHashes.add(sindrome.getHash());
        }
        Collections.sort(listaHashes);
        for (String hash : listaHashes) {
            buffer.append(hash + ";");
        }

        String hash = calculaHash(buffer.toString().getBytes("UTF-8"));
        ls.setHash(hash);
    }

    @Override
    public List<Sindrome> documentosDeLista(ListaSindromes lista) {
        return lista.getSindromes();
    }

    @Override
    public Sindrome editarDocumento(Sindrome documento) throws ECPlusBusinessException {
        try {
            calculaHash(documento);
            Sindrome nuevo = em.merge(documento);
            recalculaHash(nuevo.getListaSindromes());
            return nuevo;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new ECPlusBusinessException(e.getMessage());
        }
    }

    @Override
    public ListaSindromes eliminarDocumento(Sindrome documento) throws ECPlusBusinessException {
        try {
            Sindrome eliminar = em.merge(documento);
            ListaSindromes ld = eliminar.getListaSindromes();
            eliminar.setListaSindromes(null);
            ld.removeSindrome(eliminar);
            em.remove(eliminar);
            recalculaHash(ld);
            return ld;

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new ECPlusBusinessException(e.getMessage());
        }
    }

    @Override
    public void aniadirDocumento(Sindrome nuevoDocumento) throws ECPlusBusinessException {
        try {
            calculaHash(nuevoDocumento);
            em.persist(nuevoDocumento);
            ListaSindromes ls = nuevoDocumento.getListaSindromes();
            if (!ls.getSindromes().contains(nuevoDocumento)) {
                ls.addSindrome(nuevoDocumento);
            }

            recalculaHash(ls);
            em.merge(ls);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new ECPlusBusinessException(e.getMessage());
        }
    }

    @Override
    public void aniadirCategoria(Categoria categoria) throws ECPlusBusinessException {
        em.persist(categoria);
        ListaPalabras lp = categoria.getListaPalabras();
        if (!lp.getCategorias().contains(categoria)) {
            lp.addCategoria(categoria);
        }

    }

    @Override
    public void eliminarCategoria(Categoria categoria) throws ECPlusBusinessException {

        Categoria eliminar = em.merge(categoria);
        TypedQuery<Long> query = em.createQuery("select count(*) from Palabra p where p.categoria=:categoria", Long.class);
        query.setParameter("categoria", categoria);
        if (query.getSingleResult() > 0) {
            throw new CategoryWithWordsException("There exist words with this category: " + categoria.getNombre());
        } else {
            ListaPalabras lp = eliminar.getListaPalabras();
            eliminar.setListaPalabras(null);
            lp.removeCategoria(eliminar);
            em.remove(eliminar);
        }
    }

    @Override
    public void editarCategoria(Categoria categoria) throws ECPlusBusinessException {
        em.merge(categoria);
    }

    public List<ListaPalabras> fetchListasPalabras() {
        TypedQuery<ListaPalabras> query = em.createNamedQuery("todas-listas-palabras", ListaPalabras.class);
        return query.getResultList();
    }
    
    public List<ListaSindromes> fetchListasindromes() {
        TypedQuery<ListaSindromes> query = em.createNamedQuery("todas-listas-sindromes", ListaSindromes.class);
        return query.getResultList();
    }

}
