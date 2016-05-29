/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uma.ecplusproject.rs;


import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.ListaSindromes;
import es.uma.ecplusproject.entities.Palabra;
import es.uma.ecplusproject.entities.RecursoAudioVisual;
import es.uma.ecplusproject.entities.Resolucion;
import es.uma.ecplusproject.entities.Sindrome;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                                  @QueryParam("resolution") String resolution)
    {
        TypedQuery<ListaPalabras> query = em.createQuery("Select lp FROM ListaPalabras lp WHERE lp.idioma = :language", ListaPalabras.class);
        query.setParameter("language",language);
        Resolucion res = getResolution(resolution);
        try
        {
            List<ListaPalabras> words = query.getResultList();
            if(words.isEmpty())
                return Response.status(Response.Status.BAD_REQUEST).build();
            Map<Resolucion, String> hashes = words.get(0).getHashes();
            
            if(hashes == null || !hashes.containsKey(res)) 
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            return Response.ok(new Hash(hashes.get(res))).build();
            
        }catch(NoResultException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
        
    @GET
    @Path("words/{language}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getWordList(@PathParam("language") String language,
                                @QueryParam("resolution") String resolution)
    {
        TypedQuery<ListaPalabras> query = em.createQuery("select p from ListaPalabras p where p.idioma=:idioma", ListaPalabras.class);
        query.setParameter("idioma", language);
        List<ListaPalabras> words = query.getResultList();
        Resolucion res = getResolution(resolution);
        try
        {
            
            if(words.isEmpty())
                return Response.status(Response.Status.BAD_REQUEST).build();

            List<Palabra> listp = words.get(0).getPalabras();
            List<PalabraRes> lpr = new ArrayList<>();
            for(Palabra p: listp){
                PalabraRes pr = new PalabraRes();
                pr.setNombre(p.getNombre());
                pr.setId(p.getId());
                pr.setIconoReemplazable(p.getIconoReemplazable());
                if(!p.getHashes().isEmpty() && p.getHashes().containsKey(res))
                    pr.setHash(p.getHashes().get(res));
                if(p.getIcono() != null && 
                   !p.getIcono().getFicheros().isEmpty() && !p.getIcono().getFicheros().containsKey(res))
                    pr.setHash(p.getIcono().getFicheros().get(res));
                if(p.getIconoReemplazado()!= null && 
                   !p.getIconoReemplazado().getFicheros().isEmpty() && 
                   !p.getIconoReemplazado().getFicheros().containsKey(res))
                    pr.setHash(p.getIconoReemplazado().getFicheros().get(res));
                
                Set<String> resources = new HashSet<>();
                for(RecursoAudioVisual ra : p.getAudiovisuales()){
                    if(ra.getFicheros().containsKey(res)){
                        resources.add(ra.getFicheros().get(res));
                    }  
                }
                pr.setAudiovisuales(resources);
                lpr.add(pr);
            }
      
            GenericEntity<List<PalabraRes>> lp = 
                    new GenericEntity<List<PalabraRes>>(lpr) {};
            return Response.ok(lp).build();

            
        }catch(NoResultException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }        
    }

    @GET
    @Path("sindromes/{language}/hash")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSindromeListHash(@PathParam("language") String language)
    {
        Query query = em.createQuery("Select ls.hash FROM ListaSindromes ls WHERE ls.idioma = :language");
        query.setParameter("language",language);
        try
        {
            String hash = (String) query.getSingleResult();
            
            if(hash == null) 
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            return Response.ok(new Hash(hash)).build();
            
        }catch(NoResultException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("sindromes/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSindromesList(@PathParam("language") String language)
    {
        TypedQuery<ListaSindromes> query = em.createQuery("Select ls FROM ListaSindromes ls WHERE ls.idioma = :language",ListaSindromes.class);
        query.setParameter("language",language);
        try
        {
            List<ListaSindromes> sindromes = query.getResultList();
            
            if(sindromes.isEmpty()) 
                return Response.status(Response.Status.BAD_REQUEST).build();
            
            List<SindromeClass> ls = new ArrayList<>();
            for(Sindrome s:sindromes.get(0).getSindromes()){
                SindromeClass sin = new SindromeClass();
                sin.setId(s.getId());
                sin.setHash(s.getHash());
                sin.setContenido(s.getContenido());
                sin.setNombre(s.getNombre());
                ls.add(sin);
            }
            
            GenericEntity<List<SindromeClass>> sinds = 
                    new GenericEntity<List<SindromeClass>>(ls) {};
            return Response.ok(sinds).build();
            
        }catch(NoResultException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    @GET
    @Path("resource/{hash}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getResource(@PathParam("hash") String name)
    {
        File file = new File(context.getInitParameter("ecplus.resources.FILES_DIR"), name);
        //file = new File("/Users/francis/Documents/investigacion/Proyectos/ECPlusProject/Web/fotos/foto.JPG");
        if(file.isDirectory() || !file.exists())
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
            
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM).build();
    }
    
    
    private Resolucion getResolution(String resolution) {
        if(resolution == null)
            return Resolucion.BAJA;
        
        if(resolution.equals("high"))
            return Resolucion.ALTA;
        
        if(resolution.equals("medium"))
            return Resolucion.MEDIA; 
        
        return Resolucion.BAJA;
    }
    
}
