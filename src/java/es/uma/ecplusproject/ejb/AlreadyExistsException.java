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
public class AlreadyExistsException extends ECPlusBusinessException {
    
    public AlreadyExistsException(String message) {
        super(message);
    }
    
    public AlreadyExistsException() {
        super();
    }
    
}
