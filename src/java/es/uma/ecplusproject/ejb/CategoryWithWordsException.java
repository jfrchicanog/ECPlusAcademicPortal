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
public class CategoryWithWordsException extends ECPlusBusinessException{
    public CategoryWithWordsException() {
        super();
    }
    
    public CategoryWithWordsException(String msg) {
        super(msg);
    }
    
}
