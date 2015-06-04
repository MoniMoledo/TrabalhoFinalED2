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
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
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
                System.out.println("1 - Adicionar tabelas ao banco de dados");
                System.out.println("2 - Consultar tabelas que existem no banco");
                System.out.println("3 - Adicionar registros a uma tabela");
                System.out.println("5 - Consultar registros de uma tabela");
                System.out.println("6 - Excluir registros de uma tabela");
                System.out.println("7 - Modificar o valor de um registro de uma tabela");
                System.out.println("8 - Consultar Catálogo");
                System.out.println("9 - Fechar programa");
                System.out.println("-------------------------------------");
                Catalogo catalogo = new Catalogo();
                int opcao = 0;
                opcao = teclado.nextInt();
                teclado.nextLine();
                switch (opcao) {
                    case 1:
                        dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(NOME_CATALOGO, true)));
                        do {
                            try {
                                System.out.println("-----------------------------------------------------------------------------------");
                                System.out.println("Informe o nome da tabela que deseja adicionar");
                                tabela = teclado.nextLine();

                                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                                catalogo.verificaCatalogo(dis, tabela);
                                catalogo.addTabela(tabela);

                                catalogo.salva(dos);
                                dis.close();
                                ok = true;

                            } catch (StringInvalidaException e) {
                                System.err.println(e.getMessage());
                                ok = false;
                            }
                        } while (!ok);
                        dos.close();

                        //ATRIBUTOS
                        Atributo atr = new Atributo();

                        boolean has_primary_key = false;
                        boolean has_primary_key_file = false;
                        do {

                            try {

                                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                                ok = true;
                                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tabela + ".dat", true)));
                                //Arquivo vazio (não entendi esse if)
                                if (catalogo.isEmpty(tabela)) {
                                    DataOutputStream dosNull = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tabela + ".dat", true)));
                                    dosNull.writeUTF(" ");
                                    dosNull.close();
                                    has_primary_key_file = false;
                                }
                                boolean erro;
                                do {
                                    System.out.println("-----------------------------------------------------------------------------------");
                                    System.out.println("Informe, sem espaço, o nome dos atributos que deseja adicionar, \ncom seus respectivos tipos, separando-os por vírgulas."
                                            + "\nCaso este seja chave primária, digite 'primary_key'. Não use acentos nem espaço entre os atributos");

                                    erro = false;
                                    resp = teclado.nextLine();
                                    if (!resp.contains("primary_key")) {
                                        erro = true;
                                    }
                                    String[] nomes = resp.split(",");

                                    for (int i = 0; i < nomes.length; i++) {
                                        atr = new Atributo();
                                        catalogo.verificaEspacos(nomes[i]);
                                        String nome = catalogo.verificaEspacos(nomes[i])[0];
                                        catalogo.verificaExistencia(nome, tabela);
                                        if (catalogo.verificaEspacos(nomes[i]).length <= 1) {
                                            throw new StringInvalidaException("Erro de sintaxe. nome tipo primary_key,nome2 tipo2...");
                                        }
                                        String tipo = catalogo.verificaEspacos(nomes[i])[1];
                                        if (!tipo.equalsIgnoreCase("string") && !tipo.equalsIgnoreCase("boolean") && !tipo.equalsIgnoreCase("integer") && !tipo.equalsIgnoreCase("float") && !tipo.equalsIgnoreCase("double")) {
                                            throw new StringInvalidaException("O tipo deve ser: string,integer,float,double ou boolean.");
                                        }

                                        atr.setTabela(tabela);
                                        atr.setNome(nome);
                                        atr.setTipo(tipo);
                                        dis = new DataInputStream(new BufferedInputStream(new FileInputStream(tabela + ".dat")));
                                        has_primary_key_file = catalogo.verificaChaveArquivo(dis);

                                        //Para saber se o atributo é chave primária
                                        if (!erro) {
                                            if (nomes[i].contains("primary_key")) {
                                                //Add o atributo no arraylist
                                                atr.setChave(true);
                                                has_primary_key = true;
                                                catalogo.addAtributo(atr);

                                            } else {
                                                atr.setChave(false);
                                                catalogo.addAtributo(atr);
                                            }

                                        }
                                    }
                                    if (!has_primary_key && !has_primary_key_file) {
                                        System.err.println("A tabela precisa de uma chave primária");
                                    }

                                } while (!has_primary_key && !has_primary_key_file);
                                atr.setTabela(tabela);
                                dis.close();
                            } catch (StringInvalidaException e) {
                                ok = false;
                                System.err.println(e.getMessage());
                            }

                        } while (!ok);

                        catalogo.salvarAtributos(dos);
                        dos.close();

                        break;
                    case 2:
                        do {
                            try {
                                
                                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                                System.out.println("Que tabela deseja consultar?");
                                resp = teclado.nextLine();
                                catalogo.verificaTabela(dis, resp);
                                ok = true;
                                //dis = new DataInputStream(new BufferedInputStream(new FileInputStream(resp + ".dat")));
                                Tabela t = new Tabela(resp);
                                t.le();
                                //catalogo.leTabela(dis);
                                //dis.close();

                                break;
                            } catch (StringInvalidaException e) {
                                ok = false;
                                System.err.println(e.getMessage());
                            }
                        } while (!ok);
                        break;

                    case 3:
                        /* INSERE NA TABELA, AINDA A FAZER
                         *ESTE É APENAS O MÉTODO DE INSERIR EM HASH EXTERIOR
                         *EXTRAIDO DA FUNCAO QUE ENTREGAMOS A PROFESSORA [JON]
                         */

                        do {
                            try {
                                System.out.println("A qual tabela você deseja adicionar um registro?");
                                Scanner in = new Scanner(System.in);
                                String tab = in.nextLine();
                                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                                catalogo.verificaTabela(dis, tab);
                                Tabela t = new Tabela(tab);
                                t.criaHash(7);
                                System.out.println("Insira apenas os valores separados por virgula nessa ordem:");
                                ArrayList<String> nome = t.getNomes();
                                ArrayList<String> tipo = t.getTipos();
                                for (int i = 0; i < t.getNumAtributos(); i++) {
                                    if (i != 1 && i != 2) {
                                        System.out.print(nome.get(i));
                                        System.out.print("(" + tipo.get(i) + ")     ");
                                    }
                                }
                                System.out.println("");
                                String str = in.nextLine();
                                str = str.replace(" ,", ",");
                                str = str.replace(", ", ",");
                                str = str.replaceFirst(",", ",-1,false,");
                                String atributos[] = str.split(",");
                                if (atributos.length != t.getNumAtributos()) {
                                    throw new StringInvalidaException("Você deve informar " + t.getNumAtributos() + " atributos.");
                                }
                                for (int i = 0; i < t.getNumAtributos(); i++) {
                                    if (tipo.get(i).equalsIgnoreCase("string")) {
                                        if (atributos[i].length() < 13) {
                                            int n = atributos[i].length();
                                            for (int j = 0; j < 13 - n; j++) {
                                                atributos[i] = atributos[i].concat(" ");//STRING = CHAR(13)
                                            }
                                        } else {
                                            throw new StringInvalidaException("A string deve ter no maximo 13 characteres.");
                                        }
                                        //DEVEMOS DAR ERRO SE A STRING FOR UM NUMERO??
                                    } else {
                                        if (tipo.get(i).equalsIgnoreCase("integer")) {
                                            try {
                                                Integer.parseInt(atributos[i]);
                                            } catch (Exception e) {
                                                //System.err.println("ERRO DE TIPO");
                                                //break;
                                                throw new StringInvalidaException("Erro de tipo");
                                            }
                                        } else {
                                            if (tipo.get(i).equalsIgnoreCase("double")) {
                                                try {
                                                    Double.parseDouble(atributos[i]);
                                                } catch (Exception e) {
                                                    throw new StringInvalidaException("Erro de tipo");
                                                }
                                            } else {
                                                if (tipo.get(i).equalsIgnoreCase("float")) {
                                                    try {
                                                        Float.parseFloat(atributos[i]);
                                                    } catch (Exception e) {
                                                        throw new StringInvalidaException("Erro de tipo");
                                                    }
                                                } else {
                                                    if (tipo.get(i).equals("boolean")) {
                                                        if (atributos[i].equalsIgnoreCase("true") && atributos[i].equalsIgnoreCase("false")) {
                                                            throw new StringInvalidaException("Erro de tipo");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                

                                //AGORA JA VALIDEI TODOS OS TIPOS
                                //PARA ESCREVER EU SÓ PRECISO DO TIPO E DO ATRIBUTO
                                //NOTA(STRING DEVE TER CHAR(13)
                                if (dos != null) {
                                    dos.close();
                                }
                                t.insere(atributos);

                                break;
                            } catch (StringInvalidaException e) {
                                ok = false;
                                System.err.println(e.getMessage());
                            }
                        } while (!ok);
                        break;

                    case 6:
                        Tabela t = new Tabela("cliente");
                        t.busca(10);
                        /* EXCLUIR NA TABELA, AINDA A FAZER
                         *ESTE É APENAS O MÉTODO DE EXCLUIR EM HASH EXTERIOR
                         *EXTRAIDO DA FUNCAO QUE ENTREGAMOS A PROFESSORA [JON]
                         */
                        do {
                            try {

                                /*          public int exclui(int CodCli, String nomeArquivoHash, String nomeArquivoDados) throws Exception {
           
                                 int pos = busca(CodCli, nomeArquivoHash, nomeArquivoDados);
                                 Cliente cliente = null;
                        
                                 if (pos == -1) {
                                 return -1;
                                 } else {   
                                 RandomAccessFile tabDados = new RandomAccessFile(nomeArquivoDados, "rw");
                                 tabDados.seek(pos * Cliente.tamanhoRegistro);
                                 cliente = Cliente.le(tabDados);
                                 cliente.flag = true;
                                 tabDados.seek(pos * Cliente.tamanhoRegistro);
                                 cliente.salva(tabDados);
                                 return pos;
                                 }
                                 }
                                 */
                                break;
                            } catch (StringInvalidaException e) {
                                ok = false;
                                System.err.println(e.getMessage());
                            }
                        } while (!ok);
                        break;

                    case 8:
                        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                        try {

                            while (true) {
                                System.out.println(dis.readUTF());
                            }
                        } catch (EOFException e) {

                        }
                        dis.close();
                        break;
                    case 9:
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
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Adicionar atributo: nomeAtributo tipo primary_key,nome_atributo tipo");
        }

    }

}
