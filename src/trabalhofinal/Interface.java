/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhofinal;

import excecoes.StringInvalidaException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 *  * @author Carlos Daniel Ogg, Fernando da Rós, João Manoel, Jonatha Nunes,
 * Monique Moledo
 */
public class Interface {

    public static final String NOME_CATALOGO = "catalogo.dat";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String resposta = null;

        try {

            Scanner teclado = new Scanner(System.in);
            String resp;
            String tabela = null;
            DataOutputStream dos = null;
            boolean ok = true;
            do {
                System.out.println("Escolha uma das opções abaixo: ");
                System.out.println("-------------------------------------");
                System.out.println("1 - Adicionar uma tabela ao banco de dados");
                System.out.println("2 - Adicionar atributos a uma tabela");
                System.out.println("3 - Consultar tabelas que existem no banco");
                System.out.println("4 - Adicionar registros a uma tabela");
                System.out.println("5 - Consultar registros de uma tabela");
                System.out.println("6 - Excluir registros de uma tabela");
                System.out.println("7 - Modificar o valor de um registro de uma tabela");
                System.out.println("8 - Fechar programa");
                System.out.println("-------------------------------------");
                Catalogo catalogo = new Catalogo();
                int opcao = 0;
                opcao = teclado.nextInt();
                teclado.nextLine();
                switch (opcao) {
                    //DONE
                    case 1:
                        dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(NOME_CATALOGO, true)));
                        do {
                            try {
                                System.out.println("-----------------------------------------------------------------------------------");
                                System.out.println("Informe o nome das tabelas que deseja adicionar, separando-os por vírgulas e sem acentos");
                                tabela = teclado.nextLine();
                                String[] nomes = null;
                                if (tabela.contains(" ")) {
                                    for (int i = 0; i < tabela.split(", ").length; i++) {
                                        nomes = tabela.split(", ");
                                    }
                                } else {
                                    for (int i = 0; i < tabela.split(",").length; i++) {
                                        nomes = tabela.split(",");
                                    }
                                }

                                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                                for (int i = 0; i < nomes.length; i++) {
                                    catalogo.verificaCatalogo(dis, nomes[i]);
                                    catalogo.addTabela(nomes[i]);
                                }

                                catalogo.salva(dos);
                                dis.close();
                                ok = true;

                            } catch (StringInvalidaException e) {
                                System.err.println(e.getMessage());
                                ok = false;
                            }
                        } while (!ok);
                        dos.close();
                        break;

                    //DONE
                    case 2:
                        Atributo atr = new Atributo();
                        boolean has_primary_key = false;
                        do {

                            try {
                                System.out.println("-----------------------------------------------------------------------------------");
                                System.out.println("A que tabela deseja adicionar um Atributo?");
                                tabela = teclado.nextLine();
                                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                                catalogo.verificaTabela(dis, tabela);
                                ok = true;
                                dis.close();
                                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tabela + ".dat", true)));
                                do {
                                    System.out.println("-----------------------------------------------------------------------------------");
                                    System.out.println("Informe o nome dos atributos que deseja adicionar, \ncom seus respectivos tipos, separando-os por vírgulas."
                                            + "\nCaso seja chave primária, digite 'primary_key'. Não use acentos nem espaço entre os atributos");

                                    resp = teclado.nextLine();
                                    String[] nomes = null;

                                    nomes = resp.split(",");

                                    for (int i = 0; i < nomes.length; i++) {
                                        catalogo.verificaEspacos(nomes[i]);
                                        String nome = catalogo.verificaEspacos(nomes[i])[0];
                                        String tipo = catalogo.verificaEspacos(nomes[i])[1];
                                        atr.setNome(nome);
                                        atr.setTipo(tipo);
                                        //Para saber se o atributo é chave primária
                                        if (nomes[i].contains("primary_key")) {
                                            atr.setChave(true);
                                            has_primary_key = true;
                                            catalogo.addAtributo(tabela, nome, tipo, true);

                                        } else {
                                            atr.setChave(false);
                                            catalogo.addAtributo(tabela, nome, tipo, true);
                                        }

                                    }
                                    if (!has_primary_key) {
                                        System.err.println("A tabela precisa de uma chave primária");
                                    }

                                } while (!has_primary_key);
                                atr.setTabela(tabela);

                            } catch (StringInvalidaException e) {
                                ok = false;
                                System.err.println(e.getMessage());
                            }
                        } while (!ok);
                        catalogo.salvarAtributos(dos);
                        dos.close();
                        break;
                    case 3: 
                        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                        catalogo.le(dis);
                    case 8:
                        System.exit(0);
                        break;
                    default:
                        System.err.println("Opção inválida");

                }
                System.out.println("-----------------------------------------------------------------------------------");
                System.out.println("Deseja fazer mais alguma coisa S/N?");
                resposta = teclado.nextLine();
               
            } while (resposta.toLowerCase().equals("s"));
            
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

}
