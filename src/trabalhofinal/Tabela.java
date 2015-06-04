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
import static trabalhofinal.Interface.NOME_CATALOGO;

/**
 *
 * @author fernandodr
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
        } catch (Exception e) {
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
                nomes.add(dis.readUTF());
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
                        tamanho = tamanho + Integer.SIZE/8;//4 float = 4 tbm
                    } else {
                        if (tipo.equalsIgnoreCase("boolean")) {
                            tamanho = tamanho + 1;//bollean = 1 byte
                        } else {
                            if (tipo.equalsIgnoreCase("double")) {
                                tamanho = tamanho + Double.SIZE/8;//8
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

    public int busca(int cod) throws FileNotFoundException, IOException {
        
        //parametro --> String[] atributos
        //int cod = Integer.parseInt(atributos[0]);
        int resto = cod % 7;//PRIMEIRO ATRIBUTO É SEMPRE A PRIMARY KEY, HASH MOD 7
        int tamanho = tamanhoRegistro();
        RandomAccessFile tabHash = new RandomAccessFile(this.nomeTab + "Hash.dat", "rw");
        RandomAccessFile tabDados = new RandomAccessFile(this.nomeTab + "Dados.dat", "rw");
        int codAtual = -1;
        int codProx;
        int anterior = -1;
        boolean flag = false;

        tabHash.seek(resto * Integer.SIZE/8);
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
                codAtual = tabDados.readInt();
                codProx = tabDados.readInt();
                flag = tabDados.readBoolean();
                if (cod == codAtual && !flag) {
                    return prox;
                }
                anterior = prox;
                prox = codProx;
            }
            if (cod == codAtual && !flag) {
                return anterior;
            }
            return -1;
        }
    }

    public void insere(String[] atributos) throws IOException {
        if (busca(Integer.parseInt(atributos[0])) != -1) {
            //ja existe
        } else {
            ArrayList<String> tipo = getTipos();
            int cod = Integer.parseInt(atributos[0]);
            int resto = cod % 7;//PRIMEIRO ATRIBUTO É SEMPRE A PRIMARY KEY, HASH MOD 7
            int tamanho = tamanhoRegistro();
            RandomAccessFile tabHash = new RandomAccessFile(this.nomeTab + "Hash.dat", "rw");
            RandomAccessFile tabDados = new RandomAccessFile(this.nomeTab + "Dados.dat", "rw");
            tabHash.seek(resto * Integer.SIZE/8);
            int a = tabHash.readInt();
            int codPK = -1;
            int codProx;
            int backup = -1;
            boolean flag;
            int numRegistros = (int) tabDados.length() / tamanhoRegistro();
            if (a == -1) {
                tabHash.seek(resto * Integer.SIZE/8);
                tabHash.writeInt(numRegistros);
                tabDados.seek(numRegistros * tamanhoRegistro());
                escreve(atributos,tabDados);
            } else {
               
                int prox = a;
            while (prox != -1) {
                tabDados.seek(prox * tamanhoRegistro());
                codPK = tabDados.readInt();
                codProx = tabDados.readInt();
                flag = tabDados.readBoolean();
                
                if (flag) {
                    tabDados.seek(prox * tamanhoRegistro());
                    codPK = tabDados.readInt();
                    codProx = tabDados.readInt();
                    escreve(atributos,tabDados);
                    tabDados.seek(prox * tamanhoRegistro());
                    codPK = tabDados.readInt();
                    tabDados.writeInt(codProx);//sobreescrevendo --> cliente.prox = cAux.prox;
                    //return prox;
                }
                tabDados.seek(prox * tamanhoRegistro());
                backup = prox;
                prox = codProx;
            }
            tabDados.seek(backup * tamanhoRegistro());
            codPK = tabDados.readInt();
            tabDados.writeInt(numRegistros);//sobreescrevendo --> cliente.prox = cAux.prox;
            tabDados.seek(numRegistros * tamanhoRegistro());
            escreve(atributos,tabDados);
            //return numRegistros;
            }

        }
    }
    
    public void le() throws FileNotFoundException, IOException{
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeTab+".dat")));
        dis.readUTF();
        int numAtributos = dis.readInt();
        ArrayList<String> nome = new ArrayList<>();
        ArrayList<String> tipo = new ArrayList<>();
        for (int i = 0; i < numAtributos; i++) {
            nome.add(dis.readUTF());
            tipo.add(dis.readUTF());
            dis.readBoolean();
        }
        RandomAccessFile tabDados = new RandomAccessFile(this.nomeTab + "Dados.dat", "rw");
        int numRegistros = (int) tabDados.length() / tamanhoRegistro();
        System.out.println("TABELA "+nomeTab+":");
        for (int i = 0; i < numRegistros; i++) {
            for (int j = 0; j < numAtributos; j++) {
                System.out.print(nome.get(j)+": ");
                if (tipo.get(j).equalsIgnoreCase("string")) {
                        System.out.println(tabDados.readUTF());
                    } else {
                        if (tipo.get(j).equalsIgnoreCase("integer")) {
                            System.out.println(tabDados.readInt());
                        } else {
                            if (tipo.get(j).equalsIgnoreCase("double")) {
                                System.out.println(tabDados.readDouble());
                            } else {
                                if (tipo.get(j).equalsIgnoreCase("float")) {
                                    System.out.println(tabDados.readFloat());
                                } else {
                                    if (tipo.get(j).equals("boolean")) {
                                        System.out.println(tabDados.readBoolean());
                                    }
                                }
                            }
                        }
                    }
            }
            System.out.println("------------------");
        }
        

    }
    
    private void escreve(String[] atributos,RandomAccessFile tabDados) throws IOException{
        ArrayList<String> tipo = getTipos();
        for (int i = 0; i < atributos.length; i++) {
                    if (tipo.get(i).equalsIgnoreCase("string")) {
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
}
