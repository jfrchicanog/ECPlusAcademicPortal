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
public class ListWithWordsException extends ECPlusBusinessException {
    public ListWithWordsException() {
        super();
    }
    
    public ListWithWordsException(String msg) {
        super(msg);
    }
}
