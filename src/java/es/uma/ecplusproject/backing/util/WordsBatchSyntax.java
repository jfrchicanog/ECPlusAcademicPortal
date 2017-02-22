/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.backing.util;

import es.uma.ecplusproject.entities.ListaPalabras;
import es.uma.ecplusproject.entities.Palabra;
import java.util.stream.Stream;

/**
 *
 * @author francis
 */
public class WordsBatchSyntax {

    public static Stream<Palabra> lineToWord(ListaPalabras lista, String line) {
        try {
            String[] nombreID = line.split(":");
            String nombre = nombreID[0];
            if (nombreID.length > 1) {
                long id = Long.parseLong(nombreID[1]);
                return lista.getPalabras().stream().filter((Palabra p) -> p.getId() == id && (nombre.isEmpty() || nombre.equals(p.getNombre())));
            } else {
                return lista.getPalabras().stream().filter((Palabra p) -> nombre.equals(p.getNombre()));
            }
        } catch (NumberFormatException e) {
            return Stream.empty();
        }
    }

    public static Stream<Palabra> readWords(String string, ListaPalabras lista) {
        return Stream.of(string.split("\\r?\\n")).flatMap((String line) -> lineToWord(lista, line));
    }
   
    
}
