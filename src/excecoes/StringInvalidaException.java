/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excecoes;

/**
 * @author Carlos Daniel Ogg, Fernando da Rós, João Manoel, 
 * Jonatha Nunes, Monique Moledo
 */

public class StringInvalidaException extends RuntimeException{
    public StringInvalidaException(String message){
        super(message);
    }
}
