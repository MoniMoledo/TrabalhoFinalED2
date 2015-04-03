/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhofinal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

/**
 *
 *  * @author Carlos Daniel Ogg, Fernando da Rós, João Manoel, Jonatha Nunes,
 * Monique Moledo
 */
public class Interface {
    
    public static final String NOME_CATALOGO = "catalogo";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        
        Scanner teclado = new Scanner(System.in);
        String resp;
        String tabela;
        
        System.out.println("Deseja adicionar uma nova tabela?");
        resp = teclado.nextLine();
        if (resp.equals("sim")) {
            Catalogo catalogo = new Catalogo();
            do {
                System.out.println("Digite o nome da sua tabela(sem espaços ou acentos):");
                tabela = teclado.nextLine();
                catalogo.addTabela(tabela);
                
                do {
                    System.out.println("Digite o nome do atributo:");
                    resp = teclado.nextLine();
                    Atributo atr = new Atributo();
                    atr.setTabela(tabela);
                    atr.setNome(resp);
                    
                    System.out.println("Digite o tipo do atributo:");
                    resp = teclado.nextLine();
                    atr.setTipo(resp);
                    
                    System.out.println("Esse atributo é a chave primária?");
                    resp = teclado.nextLine();
                    if (resp.equals("sim")) {
                        atr.setChave(true);
                    } else {
                        atr.setChave(false);
                    }
                    catalogo.addAtributo(atr.getTabela(), atr.getNome(),atr.getTipo(),atr.isChave());
                    System.out.println("Caso queira gravar outro atributo digite 'outro'");
                    resp = teclado.nextLine();
                    
                } while (resp.equals("outro"));
                System.out.println("Deseja gravar outra tabela?");
                resp = teclado.nextLine();
            } while (resp.equals("sim"));
            DataOutputStream saida = null;
            DataInputStream entrada = null;
            try{
                
                saida = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(NOME_CATALOGO, true)));
                catalogo.salva(saida);
                
                
            }
            finally{
                if(saida!=null)
                    saida.close();
                entrada = new DataInputStream( new BufferedInputStream( new FileInputStream(NOME_CATALOGO)));
                catalogo.le(entrada);
                if(entrada!=null)
                    entrada.close();
            }
        }
        
    }
    
}


