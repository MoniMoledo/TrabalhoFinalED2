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
import java.util.Scanner;

/**
 * @author Carlos Daniel Ogg, Fernando Da Rós, João Manoel, Jonatha Nunes,
 * Monique Moledo
 */
public class Interface {

    public static final String NOME_CATALOGO = "catalogo.dat";

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        try {
            Scanner teclado = new Scanner(System.in);
            String resposta = null;
            do {
                int opcao = escolheOpcoes();

                switch (opcao) {
                    case 1:
                        adicionaTabela();
                        break;
                    case 2:
                        exibirTodosRegistros();
                        break;
                    case 3:
                        adicionaRegistro();
                        break;
                    case 4:
                        consultaRegistro();
                        break;
                    case 5:
                        excluiRegistro();
                        break;
                    case 6:
                        atualizaRegistro();
                        break;
                    case 7:
                        leCatalogo();
                        break;
                    case 8:
                        System.exit(0);
                        break;
                    default:
                        System.err.println("Opção inválida");

                }
                System.out.println("-----------------------------------------------------");
                System.out.println("Deseja fazer mais alguma coisa S/N?");

                do {
                    resposta = teclado.nextLine();
                    if (!(resposta.toUpperCase().equals("S")) && !(resposta.toUpperCase().equals("N"))) {
                        System.err.println("Opção inválida. Digite S ou N");
                    }
                } while (!(resposta.toUpperCase().equals("S")) && !(resposta.toUpperCase().equals("N")));
            } while (resposta.toLowerCase().equals("s"));
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Adicionar atributo: nomeAtributo tipo primary_key,nome_atributo tipo");
        }
    }

    private static int escolheOpcoes() {
        int opcao = 0;
        boolean erro;
        Scanner teclado;
        do {
            erro = false;
            teclado = new Scanner(System.in);
            System.out.println("Escolha uma das opções abaixo: ");
            System.out.println("-----------------------------------------------------");
            System.out.println("1 - Adicionar tabelas ao banco de dados");
            System.out.println("2 - Exibir todos os registros de uma tabela do banco");
            System.out.println("3 - Adicionar registro a uma tabela");
            System.out.println("4 - Consultar registros de uma tabela");
            System.out.println("5 - Excluir registros de uma tabela");
            System.out.println("6 - Modificar o valor de registros de uma tabela");
            System.out.println("7 - Consultar Catálogo");
            System.out.println("8 - Fechar programa");
            System.out.println("-----------------------------------------------------");

            try {
                opcao = teclado.nextInt();
            } catch (Exception e) {
                System.err.println("Opção inválida");
                erro = true;
            }
        } while (erro);
        return opcao;
    }

    private static void leCatalogo() throws FileNotFoundException, IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
        try {

            while (true) {
                System.out.println(dis.readUTF());
            }
        } catch (EOFException e) {

        }
        dis.close();
    }

    private static void atualizaRegistro() throws Exception {
        boolean ok = true;
        Catalogo catalogo = new Catalogo();
        do {
            try {
                System.out.println("A qual tabela você deseja modificar registros?");
                Scanner in = new Scanner(System.in);
                String tab = in.nextLine();
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                catalogo.verificaTabela(dis, tab);
                Tabela t = new Tabela(tab);

                System.out.println("Digite a condicao de busca de registros a serem atualizados como no exemplo:");
                System.out.println("Idade >= 20 AND NOME != MONIQUE");
                String condicao = in.nextLine().toLowerCase();
                String[] c = condicao.split(" "); //ATRIBUTO OPERADO NUMERO CONCATENACAO ATRIBUTO OPERADO NUMERO
                //IDADE >= 20 AND NOME != MONIQUE
                ArrayList<Long> posicoes = new ArrayList<Long>();
                int tam = t.getNumAtributos();

                posicoes = t.concatenaBusca(c).pos;
                if (posicoes == null) {
                    System.out.println("Nenhum registro atendeu à condição");
                    break;

                } else {
                    System.out.println("Digite os novos valores de atributos como no exemplo:");
                    System.out.println("Idade = 20 NOME = MONIQUE");
                    in = new Scanner(System.in);
                    String values = in.nextLine();
                    String[] novo = values.split(" ");
                    if (novo.length % 3 != 0) {
                        System.err.println("Verificar sintaxe");
                        break;
                    }
                    tam = novo.length / 3;
                    String[] newAtr = new String[tam];
                    String[] newValues = new String[tam];
                    for (int i = 0; i < newAtr.length; i++) {
                        newAtr[i] = novo[i * 3];
                        newValues[i] = novo[i * 3 + 2];
                    }
                    t.atualiza(posicoes, newAtr, newValues);
                }
                //}

                break;
            } catch (StringInvalidaException e) {
                ok = false;
                System.err.println(e.getMessage());
            }
        } while (!ok);
    }

    private static void excluiRegistro() throws Exception {
        boolean ok = true;
        Catalogo catalogo = new Catalogo();
        do {
            try {
                System.out.println("A qual tabela você deseja excluir um registro?");
                Scanner in = new Scanner(System.in);
                String tab = in.nextLine();
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                catalogo.verificaTabela(dis, tab);
                Tabela t = new Tabela(tab);

                System.out.println("Digite a condicao como no exemplo: Idade >= 20 AND NOME != MONIQUE");
                String condicao = in.nextLine().toLowerCase();
                String[] c = condicao.split(" "); //ATRIBUTO OPERADO NUMERO CONCATENACAO ATRIBUTO OPERADO NUMERO
                //IDADE >= 20 AND NOME != MONIQUE
                ArrayList<Long> posicoes = new ArrayList<Long>();
                posicoes = t.concatenaBusca(c).pos;
                if (posicoes == null) {
                    System.out.println("Nenhum registro atendeu à condição");

                } else {
                    t.excluiCondicao(posicoes);
                    System.out.println(posicoes.size() + " registro(s) excluído(s) com sucesso");
                }
                //}

                break;
            } catch (StringInvalidaException e) {
                ok = false;
                System.err.println(e.getMessage());
            }
        } while (!ok);
    }

    private static void consultaRegistro() throws Exception {
        boolean ok = true;
        Catalogo catalogo = new Catalogo();
        do {
            DataInputStream dis = null;
            try {
                System.out.println("A qual tabela você deseja buscar registros?");
                Scanner in = new Scanner(System.in);
                String tab = in.nextLine();
                dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                catalogo.verificaTabela(dis, tab);
                Tabela t = new Tabela(tab);

                System.out.println("Digite a busca conforme o exemplo: Idade >= 20 AND Nome != Monique");
                String condicao = in.nextLine().toLowerCase();
                String[] c = condicao.split(" ");

                ArrayList<String[]> registros = new ArrayList<>();
                registros = t.concatenaBusca(c).registros;
                if (registros == null) {
                    System.out.println("Nenhum registro foi encontrado");
                } else {
                    for (String[] registro : registros) {
                        t.leRegistro(registro);
                    }
                    System.out.println(registros.size() + " registro(s) encontrado(s)");
                }
                break;

            } catch (StringInvalidaException e) {
                ok = false;
                System.err.println(e.getMessage());
            } finally {
                if (dis != null) {
                    dis.close();
                }
            }
        } while (!ok);
    }

    private static void adicionaTabela() throws FileNotFoundException, Exception {
        Scanner teclado = new Scanner(System.in);
        DataOutputStream dos;
        String tabela = null;
        Catalogo catalogo = new Catalogo();
        boolean ok;
        boolean erro;
        String resp;
        dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(NOME_CATALOGO, true)));
        do {
            try {
                System.out.println("-----------------------------------------------------");
                System.out.println("Informe o nome da tabela que deseja adicionar");
                tabela = teclado.nextLine();

                try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)))) {
                    catalogo.verificaCatalogo(dis, tabela);
                    catalogo.addTabela(tabela);

                    catalogo.salva(dos);
                }
                ok = true;

            } catch (StringInvalidaException e) {
                System.err.println(e.getMessage());
                ok = false;
            }
        } while (!ok);
        dos.close();

        //Atributos
        Atributo atr = new Atributo();

        boolean has_primary_key = false;
        boolean has_primary_key_file = false;
        do {

            try {

                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                ok = true;
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tabela + ".dat", true)));

                if (catalogo.isEmpty(tabela)) {
                    try (DataOutputStream dosNull = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tabela + ".dat", true)))) {
                        dosNull.writeUTF(" ");
                    }
                    has_primary_key_file = false;
                }
                do {
                    System.out.println("-----------------------------------------------------");
                    System.out.println("Informe os atributos no seguinte padrão:");
                    System.out.println("   _______________________________________________");
                    System.out.println("    nome tipo primary_key,nome2 tipo2,nome3 tipo3 ");
                    System.out.println("   _______________________________________________");

                    erro = false;
                    resp = teclado.nextLine();
                    if (!resp.contains("primary_key")) {
                        erro = true;
                    }
                    String[] nomes = resp.split(",");

                    for (String nome1 : nomes) {
                        atr = new Atributo();
                        catalogo.verificaEspacos(nome1);
                        String nome = catalogo.verificaEspacos(nome1)[0];
                        catalogo.verificaExistencia(nome, tabela);
                        if (catalogo.verificaEspacos(nome1).length <= 1) {
                            catalogo.atributos.clear();//caso a exceção seja levantada limpar todos os atributos que ja foram adicionados
                            throw new StringInvalidaException("Erro de sintaxe. nome tipo primary_key,nome2 tipo2...");
                        }
                        String tipo = catalogo.verificaEspacos(nome1)[1];
                        if (!tipo.equalsIgnoreCase("string") && !tipo.equalsIgnoreCase("boolean") && !tipo.equalsIgnoreCase("integer") && !tipo.equalsIgnoreCase("float") && !tipo.equalsIgnoreCase("double")) {
                            catalogo.atributos.clear();
                            throw new StringInvalidaException("O tipo deve ser: string,integer,float,double ou boolean.");
                        }
                        atr.setTabela(tabela);
                        atr.setNome(nome);
                        atr.setTipo(tipo);
                        dis = new DataInputStream(new BufferedInputStream(new FileInputStream(tabela + ".dat")));
                        has_primary_key_file = catalogo.verificaChaveArquivo(dis);
                        //Teste se o atributo é chave primária
                        if (!erro) {
                            if (nome1.contains("primary_key")) {
                                //Adiciona o atributo no arraylist
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
    }

    private static void adicionaRegistro() throws Exception {
        DataOutputStream dos = null;
        boolean ok = true;
        Catalogo catalogo = new Catalogo();
        do {
            try {
                System.out.println("A qual tabela você deseja adicionar um registro?");
                Scanner in = new Scanner(System.in);
                String tab = in.nextLine();
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(NOME_CATALOGO)));
                catalogo.verificaTabela(dis, tab);
                Tabela t = new Tabela(tab);
                RandomAccessFile tabDados = new RandomAccessFile(t.nomeTab + "Dados.dat", "rw");
                if (tabDados.length() == 0) {
                    //nao existe nenhum registro. Cria hash
                    t.criaHash(7);
                }
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
                if (atributos.length > 1 && atributos.length != t.getNumAtributos()) {
                    int n = t.getNumAtributos() - 2;
                    throw new StringInvalidaException("Você deve informar " + n + " atributos.");
                }
                for (int i = 0; i < t.getNumAtributos(); i++) {
                    if (tipo.get(i).equalsIgnoreCase("string") || tipo.get(i).equalsIgnoreCase("data")) {
                        if (atributos[i].length() < 13) {
                            int n = atributos[i].length();
                            for (int j = 0; j < 13 - n; j++) {
                                atributos[i] = atributos[i].concat(" ");    //Sring = char(13)
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
                //Nota: String deverá ser char(13)
                if (dos != null) {
                    dos.close();
                }
                if (t.busca(atributos[0]) != -1) {
                    System.err.print("Registro ja cadastrado!");
                    throw new StringInvalidaException("");
                } else {
                    t.insere(atributos);
                }

                break;
            } catch (StringInvalidaException e) {
                ok = false;
                System.err.println(e.getMessage());
            }
        } while (!ok);
    }

    private static void exibirTodosRegistros() throws IOException {
        Catalogo catalogo = new Catalogo();
        Scanner teclado = new Scanner(System.in);
        String resp;
        boolean ok = true;
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
    }
}
