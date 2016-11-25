/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.rs;

import es.uma.ecplusproject.entities.Audio;
import es.uma.ecplusproject.entities.Foto;
import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.ListaSindromes;
import es.uma.ecplusproject.entities.Palabra;
import es.uma.ecplusproject.entities.Pictograma;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import es.uma.ecplusproject.entities.Resolucion;
import es.uma.ecplusproject.entities.Sindrome;
import es.uma.ecplusproject.entities.Video;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author gabriel
 */
@Path("api/v1")
@Stateless
public class ecplusRS {

    @Context
    private ServletContext context;
    @PersistenceContext(unitName = "ECplusProjectRSPU")
    private EntityManager em;

    /**
     * Creates a new instance of ecplusRS
     */
    public ecplusRS() {
    }

    @GET
    @Path("words/{language}/hash")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWordListHash(@PathParam("language") String language,
            @QueryParam("resolution") String resolution) {
        TypedQuery<ListaPalabras> query = em.createQuery("Select lp FROM ListaPalabras lp WHERE lp.idioma = :language", ListaPalabras.class);
        query.setParameter("language", language);
        Resolucion res = getResolution(resolution);
        try {
            List<ListaPalabras> words = query.getResultList();
            if (words.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            Map<Resolucion, String> hashes = words.get(0).getHashes();

            if (hashes == null || !hashes.containsKey(res)) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            return Response.ok(new Hash(hashes.get(res))).build();

        } catch (NoResultException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("words/{language}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getWordList(@PathParam("language") String language,
            @QueryParam("resolution") String resolution) {
        TypedQuery<ListaPalabras> query = em.createQuery("select p from ListaPalabras p where p.idioma=:idioma", ListaPalabras.class);
        query.setParameter("idioma", language);
        List<ListaPalabras> words = query.getResultList();
        Resolucion res = getResolution(resolution);
        try {

            if (words.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            List<Palabra> listp = words.get(0).getPalabras();
            List<PalabraRes> lpr = new ArrayList<>();
            for (Palabra p : listp) {
                PalabraRes pr = new PalabraRes();
                pr.setNombre(p.getNombre());
                pr.setId(p.getId());
                pr.setIconoReemplazable(p.getIconoReemplazable());
                if (!p.getHashes().isEmpty() && p.getHashes().containsKey(res)) {
                    pr.setHash(p.getHashes().get(res));
                }
                if (p.getIcono() != null) {
                    pr.setIcono(p.getIcono().getId());
                } 

                Set<es.uma.ecplusproject.rs.RecursoAudioVisual> resources = new HashSet<>();
                for (RecursoAudioVisual ra : p.getAudiovisuales()) {
                    es.uma.ecplusproject.rs.RecursoAudioVisual restRav = new es.uma.ecplusproject.rs.RecursoAudioVisual();
                    restRav.setId(ra.getId());
                    restRav.setHash(ra.getFichero(res));
                    restRav.setType(getRESTType(ra));
                    resources.add(restRav);
                }
                pr.setAudiovisuales(resources);
                lpr.add(pr);
            }

            GenericEntity<List<PalabraRes>> lp
                    = new GenericEntity<List<PalabraRes>>(lpr) {
            };
            return Response.ok(lp).build();

        } catch (NoResultException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("sindromes/{language}/hash")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSindromeListHash(@PathParam("language") String language) {
        Query query = em.createQuery("Select ls.hash FROM ListaSindromes ls WHERE ls.idioma = :language");
        query.setParameter("language", language);
        try {
            String hash = (String) query.getSingleResult();

            if (hash == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            return Response.ok(new Hash(hash)).build();

        } catch (NoResultException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("sindrome/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response getSindrome(@PathParam("id") Long id) {
        Sindrome sindrome = em.find(Sindrome.class, id);
        if (sindrome == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(new String(sindrome.getContenido(), Charset.forName("UTF-8"))).build();
    }

    @GET
    @Path("sindromes/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSindromesList(@PathParam("language") String language) {
        TypedQuery<ListaSindromes> query = em.createQuery("Select ls FROM ListaSindromes ls WHERE ls.idioma = :language", ListaSindromes.class);
        query.setParameter("language", language);
        try {
            List<ListaSindromes> sindromes = query.getResultList();

            if (sindromes.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            List<SindromeClass> ls = new ArrayList<>();
            for (Sindrome s : sindromes.get(0).getSindromes()) {
                SindromeClass sin = new SindromeClass();
                sin.setId(s.getId());
                sin.setHash(s.getHash());
                sin.setContenido(s.getContenido());
                sin.setNombre(s.getNombre());
                ls.add(sin);
            }

            GenericEntity<List<SindromeClass>> sinds
                    = new GenericEntity<List<SindromeClass>>(ls) {
            };
            return Response.ok(sinds).build();

        } catch (NoResultException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("resource/{hash}")
    public Response getResource(@PathParam("hash") String name) {
            File file = new File(context.getInitParameter("ecplus.resources.FILES_DIR"), name.toLowerCase());
            System.out.println(file.getAbsolutePath());
            //String mimeType = Files.probeContentType(file.toPath());
            String mimeType = probeContentType(file);
            System.out.println(mimeType);
            if (mimeType == null) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM;
            }
            //file = new File("/Users/francis/Documents/investigacion/Proyectos/ECPlusProject/Web/fotos/foto.JPG");
            if (file.isDirectory() || !file.exists()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            return Response.ok(file, MediaType.valueOf(mimeType)).build();

    }

    @GET
    @Path("video/{hash}")
    public Response getVideo(@PathParam("hash") String name) {
            File file = new File(context.getInitParameter("ecplus.resources.FILES_DIR"), name.toLowerCase());
            if (file.isDirectory() || !file.exists()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            return Response.ok(file, MediaType.valueOf("video/mp4")).build();
    }
    
    @GET
    @Path("foto/{hash}")
    public Response getFoto(@PathParam("hash") String name) {
            File file = new File(context.getInitParameter("ecplus.resources.FILES_DIR"), name.toLowerCase());
            if (file.isDirectory() || !file.exists()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            return Response.ok(file, MediaType.valueOf("image/jpeg")).build();
    }
    
    @GET
    @Path("pictograma/{hash}")
    public Response getPictograma(@PathParam("hash") String name) {
            File file = new File(context.getInitParameter("ecplus.resources.FILES_DIR"), name.toLowerCase());
            if (file.isDirectory() || !file.exists()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            return Response.ok(file, MediaType.valueOf("image/svg+xml")).build();
    }
    
    @GET
    @Path("audio/{hash}")
    public Response getAudio(@PathParam("hash") String name) {
            File file = new File(context.getInitParameter("ecplus.resources.FILES_DIR"), name.toLowerCase());
            if (file.isDirectory() || !file.exists()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            return Response.ok(file, MediaType.valueOf("audio/mpeg3")).build();
    }

    
    private String probeContentType(File file) {
        InputStream is;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            String mimeType = URLConnection.guessContentTypeFromStream(is);
            is.close();
            return mimeType;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ecplusRS.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (IOException ex) {
            Logger.getLogger(ecplusRS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }

    private Resolucion getResolution(String resolution) {
        if (resolution == null) {
            return Resolucion.BAJA;
        }

        if (resolution.equals("high")) {
            return Resolucion.ALTA;
        }

        if (resolution.equals("medium")) {
            return Resolucion.MEDIA;
        }

        return Resolucion.BAJA;
    }

    private String getRESTType(RecursoAudioVisual ra) {
        if (ra instanceof Foto) {
            return "Fotografia";
        } else if (ra instanceof Video) {
            return "Video";
        } else if (ra instanceof Pictograma) {
            return "Pictograma";
        } else if (ra instanceof Audio) {
            return "Audio";
        } else {
            throw new RuntimeException("Unknown DTYPE "+ra.getClass());
        }
    }

}
