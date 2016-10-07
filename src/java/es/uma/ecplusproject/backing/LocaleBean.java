/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

/**
 *
 * @author francis
 */
@Named(value = "localeBean")
@SessionScoped
public class LocaleBean implements Serializable {

    private static final String MENSAJES_BUNDLE = "mensajes";
    
    private Locale locale;
    
    /**
     * Creates a new instance of LocaleBean
     */
    public LocaleBean() {
    }
    
    @PostConstruct
    public void inicializar() {
        locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    }
    
    public String getLocale() {
        return locale.getLanguage();
    }
    
    public ResourceBundle getResourceBundle() {
        if (locale != null) {
            return ResourceBundle.getBundle(MENSAJES_BUNDLE, locale);
        } else {
            return ResourceBundle.getBundle(MENSAJES_BUNDLE);
        }
    }
    
    public synchronized void setLocale(String language) {
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }
    
    public String getDisplayLanguageForLocale(Locale l) {
        return l.getDisplayLanguage(locale);
    }
    
    
}
