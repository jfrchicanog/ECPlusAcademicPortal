/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.ejb;

/**
 *
 * @author francis
 */
public class UnallowedFormatException extends ECPlusBusinessException {
    public UnallowedFormatException () {
        super();
    }
    
    public UnallowedFormatException(String msg) {
        super(msg);
    }
    
}
