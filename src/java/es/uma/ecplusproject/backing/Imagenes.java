/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author francis
 */
@Named(value = "imagenes")
@RequestScoped
public class Imagenes implements Serializable {
    
    /**
     * Creates a new instance of Imagenes
     */
    public Imagenes() {
    }
    
    public StreamedContent getImagen() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        }
        
        try {
            
            String nombreImagen = context.getExternalContext().getRequestParameterMap().get("nombreImagen");
            //BufferedImage bufferedImg = new BufferedImage(100, 25, BufferedImage.TYPE_INT_RGB);
            //Graphics2D g2 = bufferedImg.createGraphics();
            //g2.drawString("This is a text", 0, 10);
            //ByteArrayOutputStream os = new ByteArrayOutputStream();
            //ImageIO.write(bufferedImg, "png", os);
            //DefaultStreamedContent graphicText = new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/png"); 
 
            System.out.println(nombreImagen);
            
            File foto = new File("/Users/francis/Documents/investigacion/Proyectos/ECPlusProject/Web/fotos/foto.JPG");
            //System.out.println(foto);
            DefaultStreamedContent defaultStreamedContent = new DefaultStreamedContent(new FileInputStream(foto), "image/jpeg");
            return defaultStreamedContent;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Content.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } 
        return null;
        
        
        
    }

    
}
