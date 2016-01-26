/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhofinal;

import excecoes.StringInvalidaException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * @author Carlos Daniel Ogg, Fernando da Rós, João Manoel, Jonatha Nunes,
 * Monique Moledo
 */
public class Tabela {

    String nomeTab;

    Tabela(String nomeTabela) throws FileNotFoundException {
        this.nomeTab = nomeTabela;
    }

    public String getNomeTab() {
        return this.nomeTab;
    }

    public int getNumAtributos() throws IOException {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(this.nomeTab + ".dat")));
            dis.readUTF();
            int resp = dis.readInt();
            dis.close();
            return resp;
        } catch (IOException e) {
        }
        return -1;
    }

    public ArrayList<String> getNomes() throws IOException {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(this.nomeTab + ".dat")));
            dis.readUTF();
            int tam = dis.readInt();
            ArrayList<String> nomes = new ArrayList<>();
            for (int i = 0; i < tam; i++) {
                nomes.add(dis.readUTF().toLowerCase());
                dis.readUTF();
                dis.readBoolean();
            }
            dis.close();
            return nomes;
        } catch (Exception e) {
        }
        return null;
    }

    public ArrayList<String> getTipos() throws IOException {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(this.nomeTab + ".dat")));
            dis.readUTF();
            int tam = dis.readInt();
            ArrayList<String> tipos = new ArrayList<>();
            for (int i = 0; i < tam; i++) {
                dis.readUTF();
                tipos.add(dis.readUTF());
                dis.readBoolean();
            }
            dis.close();
            return tipos;
        } catch (Exception e) {
        }
        return null;
    }

    public int tamanhoRegistro() throws IOException {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(this.nomeTab + ".dat")));
            dis.readUTF();
            int nAtributos = dis.readInt();
            int tamanho = 0;
            String tipo;
            for (int i = 0; i < nAtributos; i++) {
                dis.readUTF();
                tipo = dis.readUTF();
                dis.readBoolean();
                if (tipo.equalsIgnoreCase("string")) {
                    tamanho = tamanho + 15;//2bytes + 1 por caractere, são 13 caracteres
                } else {
                    if (tipo.equalsIgnoreCase("integer") || tipo.equalsIgnoreCase("float")) {
                        tamanho = tamanho + Integer.SIZE / 8;//4 float = 4 tbm
                    } else {
                        if (tipo.equalsIgnoreCase("boolean")) {
                            tamanho = tamanho + 1;//bollean = 1 byte
                        } else {
                            if (tipo.equalsIgnoreCase("double")) {
                                tamanho = tamanho + Double.SIZE / 8;//8
                            }
                        }
                    }
                }
            }
            return tamanho;
        } catch (Exception e) {
        }
        return -1;
    }

    public void criaHash(int tamanho) throws FileNotFoundException, IOException {
        RandomAccessFile raf = new RandomAccessFile(this.nomeTab + "Hash.dat", "rw");
        for (int i = 0; i < tamanho; i++) {
            raf.writeInt(-1);
        }
        raf.close();
    }

    private int getHashCode(String chave) throws Exception {

        ArrayList<String> tipo = new ArrayList<>();
        tipo = this.getTipos();
        int cod = -1; // se nao for dos tipos abaixos 

        if (tipo.get(0).equalsIgnoreCase("string")) {
            if (chave.length() < 13) {
                int n = chave.length();
                for (int j = 0; j < 13 - n; j++) {
                    chave = chave.concat(" ");//STRING = CHAR(
                }
            }
            cod = chave.hashCode();
        } else {
            if (tipo.get(0).equalsIgnoreCase("integer") || tipo.get(0).equalsIgnoreCase("int")) {
                cod = Integer.parseInt(chave);
            } else {
                if (tipo.get(0).equalsIgnoreCase("double")) {
                    cod = (int) Double.parseDouble(chave);
                } else {
                    if (tipo.get(0).equalsIgnoreCase("float")) {
                        cod = (int) Float.parseFloat(chave);
                    }
                } //pk nao pode seer boolean
            }
        }

        return cod;
    }

    public int busca(String chave) throws Exception {

        //parametro --> String[] atributos
        //int cod = Integer.parseInt(atributos[0]);
        //busca por pk de qualquer tipo.
        ArrayList<String> tipo = new ArrayList<>();
        tipo = this.getTipos();

        int cod = getHashCode(chave);
        int resto = cod % 7;//PRIMEIRO ATRIBUTO É SEMPRE A PRIMARY KEY, HASH MOD 7
        int tamanho = tamanhoRegistro();
        RandomAccessFile tabHash = new RandomAccessFile(this.nomeTab + "Hash.dat", "rw");
        RandomAccessFile tabDados = new RandomAccessFile(this.nomeTab + "Dados.dat", "rw");
        int codAtual = -1;
        int pontProx;
        int anterior = -1;
        boolean flag = false;

        tabHash.seek(resto * Integer.SIZE / 8);
        //Cliente cAux = null;
        int a = tabHash.readInt();
        if (a == -1) {
            return -1;
        } else {
            int prox = a;
            int pos;
            while (prox != -1) {
                pos = prox * tamanho;//posição
                tabDados.seek(pos);

                getTabDados(tipo.get(0), tabDados);

                tabDados.readInt();//lendo ponteiro
                flag = tabDados.readBoolean();
                tabDados.seek(pos);

                if (tipo.get(0).equalsIgnoreCase("string")) {
                    if (chave.length() < 13) {
                        int n = chave.length();
                        for (int j = 0; j < 13 - n; j++) {
                            chave = chave.concat(" ");//STRING = CHAR(
                        }
                    }
                    String lido = tabDados.readUTF();
                    if (chave.equals(lido) && !flag) {
                        return prox;
                    }
                } else {
                    if (tipo.get(0).equalsIgnoreCase("integer")) {
                        int lido = tabDados.readInt();
                        if (Integer.parseInt(chave) == lido && !flag) {
                            return prox;
                        }
                    } else {
                        if (tipo.get(0).equalsIgnoreCase("double")) {
                            if (Double.parseDouble(chave) == tabDados.readDouble() && !flag) {
                                return prox;
                            }
                        } else {
                            if (tipo.get(0).equalsIgnoreCase("float")) {
                                if (Float.parseFloat(chave) == (tabDados.readFloat()) && !flag) {
                                    return prox;
                                }
                            }
                        }
                    }
                }

                pontProx = tabDados.readInt();

                prox = pontProx;
            }
            return -1;
        }
    }

    public void insere(String[] atributos) throws Exception {
        if (busca(atributos[0]) != -1) {

            System.err.println("Registro ja cadastrado!");
        } else {
            ArrayList<String> tipo = getTipos();
            int cod = getHashCode(atributos[0]);
            int resto = cod % 7;//PRIMEIRO ATRIBUTO É SEMPRE A PRIMARY KEY, HASH MOD 7
            int tamanho = tamanhoRegistro();
            RandomAccessFile tabHash = new RandomAccessFile(this.nomeTab + "Hash.dat", "rw");
            RandomAccessFile tabDados = new RandomAccessFile(this.nomeTab + "Dados.dat", "rw");
            tabHash.seek(resto * Integer.SIZE / 8);
            int a = tabHash.readInt();
            int codPK = -1;
            int codProx;
            int backup = -1;
            boolean flag;
            boolean done = false;
            int numRegistros = (int) tabDados.length() / tamanhoRegistro();
            if (a == -1) {
                tabHash.seek(resto * Integer.SIZE / 8);
                tabHash.writeInt(numRegistros);
                tabDados.seek(numRegistros * tamanhoRegistro());
                escreve(atributos, tabDados);
            } else {

                int prox = a;
                while (prox != -1) {
                    tabDados.seek(prox * tamanhoRegistro());
                    getTabDados(tipo.get(0), tabDados);

                    codProx = tabDados.readInt();
                    flag = tabDados.readBoolean();

                    if (flag) {
                        tabDados.seek(prox * tamanhoRegistro());
                        escreve(atributos, tabDados); // escrevi o prox = -1 mas tenho q manter o q tava antes prox = cod prox
                        tabDados.seek(prox * tamanhoRegistro());

                        getTabDados(tipo.get(0), tabDados);

                        tabDados.writeInt(codProx);
                        done = true;
                        break;
                    }
                    //tabDados.seek(prox * tamanhoRegistro());
                    backup = prox;
                    prox = codProx;
                }
                if (!done) { //se nao encontrou nenhum registro excluído pelo caminho, escrever no final do arquivo
                    tabDados.seek(backup * tamanhoRegistro());

                    getTabDados(tipo.get(0), tabDados);

                    //codPK = tabDados.readInt();
                    tabDados.writeInt(numRegistros);//sobreescrevendo --> cliente.prox = cAux.prox;
                    tabDados.seek(numRegistros * tamanhoRegistro());
                    escreve(atributos, tabDados);
                    //return numRegistros;
                }

            }

        }
    }

    public void le() throws FileNotFoundException, IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeTab + ".dat")));
        dis.readUTF();
        int numAtributos = dis.readInt();
        ArrayList<String> nome = this.getNomes();
        ArrayList<String> tipo = this.getTipos();

        RandomAccessFile tabDados = new RandomAccessFile(this.nomeTab + "Dados.dat", "rw");
        int numRegistros = (int) tabDados.length() / tamanhoRegistro();
        System.out.println("-----------------------------------------------------");
        System.out.println("TABELA " + nomeTab + ":");
        System.out.println("-----------------------------------------------------");
        boolean flag;
        int pos = 0;
//        int teste = 0;
        for (int i = 0; i < numRegistros; i++) {
            tabDados.seek(pos);
            getTabDados(tipo.get(0), tabDados);
            
            tabDados.readInt();//PROX
            flag = tabDados.readBoolean();
            tabDados.seek(pos);

            if (!flag) {
                for (int j = 0; j < numAtributos; j++) {
                    if (nome.get(j).equalsIgnoreCase("proximo")) {
                        tabDados.readInt();
                    } else {
                        if (nome.get(j).equalsIgnoreCase("flag")) {
                            tabDados.readBoolean();
                        } else {
                            System.out.println(nome.get(j) + ": " + getTabDados(tipo.get(j), tabDados));
                            
                        }
                    }
                }
            }
            pos = pos + tamanhoRegistro();
            if (!flag) {
                System.out.println("-----------------------------------------------------");
            }
        }

    }

    public String[] leRegistro1(RandomAccessFile raf) throws FileNotFoundException, IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeTab + ".dat")));
        dis.readUTF();
        int numAtributos = dis.readInt();
        String[] result = new String[numAtributos];
        ArrayList<String> nome = this.getNomes();
        ArrayList<String> tipo = this.getTipos();

        RandomAccessFile tabDados = raf;

        for (int j = 0; j < numAtributos; j++) {
            result[j] = getTabDados(tipo.get(j), tabDados);
            
        }
        return result;
    }

    public String getTabDados(String pTipo, RandomAccessFile pTabDados) throws IOException {
        if (pTipo.equalsIgnoreCase("string")) {
            return String.valueOf(pTabDados.readUTF());
        } else {
            if (pTipo.equalsIgnoreCase("integer")) {
                return String.valueOf(pTabDados.readInt());
            } else {
                if (pTipo.equalsIgnoreCase("double")) {
                    return String.valueOf(pTabDados.readDouble());
                } else {
                    if (pTipo.equalsIgnoreCase("float")) {
                        return String.valueOf(pTabDados.readFloat());
                    } else {
                        if (pTipo.equals("boolean")) {
                            return String.valueOf(pTabDados.readBoolean());
                        }
                    }
                }
            }
        }
        return null;
    }

    //@Override
    public void leRegistro(String[] registro) throws FileNotFoundException, IOException {
        ArrayList<String> nome = this.getNomes();
        ArrayList<String> tipo = this.getTipos();

        System.out.println("-----------------------------------------------------");
        for (int j = 0; j < registro.length; j++) {
            if (!nome.get(j).equalsIgnoreCase("proximo")) {

                if (!nome.get(j).equalsIgnoreCase("flag")) {

                    System.out.print(nome.get(j) + ": ");
                    
                    System.out.println(registro[j]);
                }
            }
        }
    }

    private void escreve(String[] atributos, RandomAccessFile tabDados) throws IOException {
        ArrayList<String> tipo = getTipos();
        for (int i = 0; i < atributos.length; i++) {
            if (tipo.get(i).equalsIgnoreCase("string")) {
                int n = atributos[i].length();
                for (int j = 0; j < 13 - n; j++) {
                    atributos[i] = atributos[i].concat(" ");
                }
                tabDados.writeUTF(atributos[i]);
            } else {
                if (tipo.get(i).equalsIgnoreCase("integer")) {
                    tabDados.writeInt(Integer.parseInt(atributos[i]));
                } else {
                    if (tipo.get(i).equalsIgnoreCase("double")) {
                        tabDados.writeDouble(Double.parseDouble(atributos[i]));
                    } else {
                        if (tipo.get(i).equalsIgnoreCase("float")) {
                            tabDados.writeFloat(Float.parseFloat(atributos[i]));
                        } else {
                            if (tipo.get(i).equals("boolean")) {
                                if (atributos[i].equalsIgnoreCase("true")) {
                                    tabDados.writeBoolean(true);
                                } else {
                                    tabDados.writeBoolean(false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    int exclui(String pk) throws Exception {

        int pos = busca(pk);

        if (pos == -1) {
            //Não achou
            return -1;
        } else {
            RandomAccessFile tabDados = new RandomAccessFile(this.nomeTab + "Dados.dat", "rw");
            tabDados.seek(pos * this.tamanhoRegistro());
            int numAtributos = this.getNumAtributos();
            ArrayList<String> tipo = this.getTipos();
            String[] atributos = new String[numAtributos];

            for (int i = 0; i < numAtributos; i++) {
                atributos[i] = getTabDados(tipo.get(i), tabDados);
                
            }
            atributos[2] = String.valueOf(true);
            tabDados.seek(pos * this.tamanhoRegistro());
            this.escreve(atributos, tabDados);
            return pos;
        }
    }

    public Resultado buscaCondicao(String[] c) throws FileNotFoundException, IOException, Exception {

        ArrayList<String> nomes = this.getNomes();
        ArrayList<String> tipos = this.getTipos();
        ArrayList<String[]> registros = new ArrayList<>();
        ArrayList<Long> posicoes = new ArrayList<>();
        long pos;

        RandomAccessFile tabDados = new RandomAccessFile(nomeTab + "Dados.dat", "rw");
        Resultado result = new Resultado();

        for (int i = 0; i < c.length; i += 4) { //VERIFICANDO SE O ATRIBUTO EXISTE
            if (!nomes.contains(c[i].toLowerCase())) {
                System.err.println("O atributo " + c[i] + " não existe na tabela " + this.nomeTab);
                return result;
            }
        }
        //verificando se é pk para chamar busca com hash
        if (c[0].equals(nomes.get(0)) && c[1].equals("=")) {
            int i = this.busca(c[2]);
            if (i == -1) {
                return result;
            }
            tabDados.seek(i * this.tamanhoRegistro());
            posicoes.add(tabDados.getFilePointer());
            registros.add(this.leRegistro1(tabDados));
            result.registros = registros;
            result.pos = posicoes;
            return result;
        }

        for (int i = 2; i < c.length; i += 4) { //PASSANDO AS STRINGS PARA CHAR(13)
            int a = nomes.indexOf(c[i - 2]);
            if (tipos.get(nomes.indexOf(c[i - 2])).equals("string")) {
                int n = c[i].length();
                for (int j = 0; j < 13 - n; j++) {
                    c[i] = c[i].concat(" ");
                }
            }
        }

        int numRegistros = (int) tabDados.length() / tamanhoRegistro();
        String[] atributos;
        tabDados.seek(0);
        for (int i = 0; i < numRegistros; i++) {
            pos = i * tamanhoRegistro();
            tabDados.seek(i * tamanhoRegistro());
            int t = tamanhoRegistro();
            atributos = new String[this.getNumAtributos()];
            for (int j = 0; j < this.getNumAtributos(); j++) {
                atributos[j] = getTabDados(tipos.get(j), tabDados);
                
            }
            int n = nomes.indexOf(c[0]);
            this.selecionaCondiconal(atributos, tipos, n, registros, c, posicoes, pos); //adiciona um registro(atributos) ao (registros) se a condicao for satisfeita
        }
        result.pos = posicoes;
        result.registros = registros;
        return result;
    }

    private void selecionaCondiconal(String[] atributos, ArrayList<String> tipos, int n, ArrayList<String[]> registros, String[] c, ArrayList<Long> posicoes, long pos) {
        if (atributos[2].equalsIgnoreCase("false")) {
            if (tipos.get(n).equals("string")) {
                if (c[1].equals("=") && atributos[n].compareToIgnoreCase(c[2]) == 0) {
                    registros.add(atributos);
                    posicoes.add(pos);
                } else {
                    if (c[1].equals("<") && atributos[n].compareToIgnoreCase(c[2]) < 0) {
                        registros.add(atributos);
                        posicoes.add(pos);
                    } else {
                        if (c[1].equals(">") && atributos[n].compareToIgnoreCase(c[2]) > 0) {
                            registros.add(atributos);
                            posicoes.add(pos);
                        } else {
                            if (c[1].equals("!=") && atributos[n].compareToIgnoreCase(c[2]) != 0) {
                                registros.add(atributos);
                                posicoes.add(pos);
                            } else {
                                if (c[1].equals(">=") && atributos[n].compareToIgnoreCase(c[2]) >= 0) {
                                    registros.add(atributos);
                                    posicoes.add(pos);
                                } else {
                                    if (c[1].equals("<=") && atributos[n].compareToIgnoreCase(c[2]) <= 0) {
                                        registros.add(atributos);
                                        posicoes.add(pos);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (tipos.get(n).equals("integer")) {
                    if (c[1].equals("=") && Integer.parseInt(atributos[n]) == Integer.parseInt(c[2])) {
                        registros.add(atributos);
                        posicoes.add(pos);
                    } else {
                        if (c[1].equals("<") && Integer.parseInt(atributos[n]) < Integer.parseInt(c[2])) {
                            registros.add(atributos);
                            posicoes.add(pos);
                        } else {
                            if (c[1].equals(">") && Integer.parseInt(atributos[n]) > Integer.parseInt(c[2])) {
                                registros.add(atributos);
                                posicoes.add(pos);
                            } else {
                                if (c[1].equals("!=") && Integer.parseInt(atributos[n]) != Integer.parseInt(c[2])) {
                                    registros.add(atributos);
                                    posicoes.add(pos);
                                } else {
                                    if (c[1].equals(">=") && Integer.parseInt(atributos[n]) >= Integer.parseInt(c[2])) {
                                        registros.add(atributos);
                                        posicoes.add(pos);
                                    } else {
                                        if (c[1].equals("<=") && Integer.parseInt(atributos[n]) <= Integer.parseInt(c[2])) {
                                            registros.add(atributos);
                                            posicoes.add(pos);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (tipos.get(n).equals("double")) {
                        if (c[1].equals("=") && Double.parseDouble(atributos[n]) == Double.parseDouble(c[2])) {
                            registros.add(atributos);
                            posicoes.add(pos);
                        } else {
                            if (c[1].equals("<") && Double.parseDouble(atributos[n]) < Double.parseDouble(c[2])) {
                                registros.add(atributos);
                                posicoes.add(pos);
                            } else {
                                if (c[1].equals(">") && Double.parseDouble(atributos[n]) > Double.parseDouble(c[2])) {
                                    registros.add(atributos);
                                    posicoes.add(pos);
                                } else {
                                    if (c[1].equals("!=") && Double.parseDouble(atributos[n]) != Double.parseDouble(c[2])) {
                                        registros.add(atributos);
                                        posicoes.add(pos);
                                    } else {
                                        if (c[1].equals(">=") && Double.parseDouble(atributos[n]) >= Double.parseDouble(c[2])) {
                                            registros.add(atributos);
                                            posicoes.add(pos);
                                        } else {
                                            if (c[1].equals("<=") && Double.parseDouble(atributos[n]) <= Double.parseDouble(c[2])) {
                                                registros.add(atributos);
                                                posicoes.add(pos);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (tipos.get(n).equals("float")) {
                            if (c[1].equals("=") && Float.parseFloat(atributos[n]) == Float.parseFloat(c[2])) {
                                registros.add(atributos);
                                posicoes.add(pos);
                            } else {
                                if (c[1].equals("<") && Float.parseFloat(atributos[n]) < Float.parseFloat(c[2])) {
                                    registros.add(atributos);
                                    posicoes.add(pos);
                                } else {
                                    if (c[1].equals(">") && Float.parseFloat(atributos[n]) > Float.parseFloat(c[2])) {
                                        registros.add(atributos);
                                        posicoes.add(pos);
                                    } else {
                                        if (c[1].equals("!=") && Float.parseFloat(atributos[n]) != Float.parseFloat(c[2])) {
                                            registros.add(atributos);
                                            posicoes.add(pos);
                                        } else {
                                            if (c[1].equals(">=") && Float.parseFloat(atributos[n]) >= Float.parseFloat(c[2])) {
                                                registros.add(atributos);
                                                posicoes.add(pos);
                                            } else {
                                                if (c[1].equals("<=") && Float.parseFloat(atributos[n]) <= Float.parseFloat(c[2])) {
                                                    registros.add(atributos);
                                                    posicoes.add(pos);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (c[1].equals("=") && atributos[n].compareToIgnoreCase(c[2]) == 0) {
                                registros.add(atributos);
                                posicoes.add(pos);
                            } else {
                                if (c[1].equals("!=") && atributos[n].compareToIgnoreCase(c[2]) != 0) {
                                    registros.add(atributos);
                                    posicoes.add(pos);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    void excluiCondicao(ArrayList<Long> posicoes) throws Exception {

        RandomAccessFile tabDados = new RandomAccessFile(this.nomeTab + "Dados.dat", "rw");
        for (int k = 0; k < posicoes.size(); k++) {
            tabDados.seek(posicoes.get(k));
            int numAtributos = this.getNumAtributos();
            ArrayList<String> tipo = this.getTipos();
            String[] atributos = new String[numAtributos];

            for (int i = 0; i < numAtributos; i++) {
                atributos[i] = getTabDados(tipo.get(i), tabDados);
                
            }
            //atributos [0] == pk;atributos [1] == ponteiroprox; atributos [2] == flag;
            atributos[2] = String.valueOf(true);
            tabDados.seek(posicoes.get(k));
            this.escreve(atributos, tabDados);
        }
    }

    public void atualiza(ArrayList<Long> posicoes, String newAtr[], String[] newValores) throws Exception {
        // ArrayList<Long> posicoes = this.buscaCondicao(c).pos;
        ArrayList<String> nome = this.getNomes();
        ArrayList<String> tipo = this.getTipos();
        String[] oldValores = new String[this.getNumAtributos()];
        Boolean done = false;
        for (int i = 0; i < newAtr.length; i++) {
            if (nome.indexOf(newAtr[i]) == 0) { //entao o atributo eh pk
                System.err.println("A chave primária não pode ser alterada");
                done = true;
            }
        }

        if (!done) {
            if (posicoes != null) {
                RandomAccessFile tabDados = new RandomAccessFile(this.nomeTab + "Dados.dat", "rw");
                for (int i = 0; i < posicoes.size(); i++) {
                    //Ler registro atual
                    tabDados.seek(posicoes.get(i));
                    for (int j = 0; j < this.getNumAtributos(); j++) {
                        oldValores[j] = getTabDados(tipo.get(j), tabDados);
                        
                    }
                    //para todos os atributos que mudaram - atualizar o valor de oldValores
                    int index;
                    for (int j = 0; j < newValores.length; j++) {
                        index = nome.indexOf(newAtr[j]);
                        if (index == -1) {
                            throw new StringInvalidaException("O atributo informado não existe");
                        } else {
                            oldValores[index] = newValores[j];
                        }
                    }
                    tabDados.seek(posicoes.get(i));
                    this.escreve(oldValores, tabDados);
                }
                System.out.println(posicoes.size() + " registro(s) atualizado(s) com sucesso");

            } else {
                System.out.println("Nenhum registro foi atualizado.");
            }
        }
    }

    public Resultado concatenaBusca(String[] c) throws Exception {
        Resultado resultado = new Resultado();
        Resultado resultado1 = new Resultado();
        if (c.length >= 3) {
            resultado1 = this.buscaCondicao(c);

            for (int i = 0; i < resultado1.registros.size(); i++) {
                resultado.registros.add(resultado1.registros.get(i));
                resultado.pos.add(resultado1.pos.get(i));;
            }
            if (c.length > 4) {
                String[] aux = c;
                String[] aux1 = c;
                for (int i = 3; i < c.length; i += 4) {
                    String[] condicao2 = new String[aux.length - 4];
                    for (int l = 0; l < aux.length - 4; l++) {
                        condicao2[l] = aux[l + 4];
                    }
                    aux1 = new String[aux.length - 3];
                    for (int l = 0; l < aux.length - 3; l++) {
                        aux1[l] = aux[l + 3];
                    }

                    Resultado resultado2 = new Resultado();

                    resultado2 = this.buscaCondicao(condicao2);
                    //if (condicao2.length > 4) {
                    if (aux1[0].equalsIgnoreCase("AND")) {
                        //faz interseção
                        resultado.registros.clear();
                        resultado.pos.clear();
                        for (int j = 0; j < resultado1.registros.size(); j++) {
                            for (int k = 0; k < resultado2.registros.size(); k++) {
                                if (resultado1.registros.get(j)[0].equals(resultado2.registros.get(k)[0])) {
                                    resultado.registros.add(resultado1.registros.get(j));
                                    resultado.pos.add(resultado1.pos.get(j));
                                }

                            }
                        }
                    } else if (aux1[0].equalsIgnoreCase("OR")) {
                        //faz uniao
                        //removendo a interseção
                        for (int j = 0; j < resultado.registros.size(); j++) {
                            for (int k = 0; k < resultado2.registros.size(); k++) {
                                if (resultado.registros.get(j)[0].equals(resultado2.registros.get(k)[0])) {
                                    resultado.registros.remove(j);
                                    resultado.pos.remove(j);
                                    j -= 1;
                                }

                            }
                        }
                        //uniao
                        for (int m = 0; m < resultado2.registros.size(); m++) {
                            resultado.registros.add(resultado2.registros.get(m));
                            resultado.pos.add(resultado2.pos.get(m));
                        }

                    } else {
                        // throw exception?

                        System.err.println("Operador inválido");
                        break;
                    }
                    resultado1.registros.clear();
                    resultado1.pos.clear();
                    for (int k = 0; k < resultado.registros.size(); k++) {
                        resultado1.registros.add(resultado.registros.get(k));
                        resultado1.pos.add(resultado.pos.get(k));
                    }

                    aux = condicao2;
                }
            }
        }
        return resultado;

    }
}
