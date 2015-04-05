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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Carlos Daniel Ogg, Fernando da Rós, João Manoel, Jonatha Nunes,
 * Monique Moledo
 */
public class Catalogo {

    private List<String> nomeTabela = new ArrayList<>();

    private List<Atributo> atributos = new ArrayList<>();

    public void addTabela(String nome) {
        nomeTabela.add(nome);
    }

    public void addAtributo(String tabela, String nome, String tipo, boolean ehChave) {
        Atributo at = new Atributo();
        at.setTabela(tabela);
        at.setNome(nome);
        at.setTipo(tipo);
        at.setChave(ehChave);
        atributos.add(at);
    }

    public void salva(DataOutputStream catalogo) throws Exception {
        DataOutputStream catalogTabela = null;
        try {
            //for para guardar no catalogo principal nome da tabela
            for (int j = 0; j < nomeTabela.size(); j++) {
                catalogo.writeUTF(nomeTabela.get(j));
                
            }
        } finally {
            if (catalogTabela != null) {
                catalogTabela.close();
            }
        }
    }
    
    public void salvarAtributos(DataOutputStream dos) throws IOException{
        for (int i = 0; i < atributos.size(); i++) {
            dos.writeUTF(atributos.get(i).getNome());
            dos.writeUTF(atributos.get(i).getTipo());
            dos.writeBoolean(atributos.get(i).isChave());
        }
    }

    public void verificaCatalogo(DataInputStream dis, String tabela) throws IOException, StringInvalidaException {
        try {
            while (true) {
                if (dis.readUTF().toLowerCase().equals(tabela.toLowerCase())) {
                    throw new StringInvalidaException("A tabela " + tabela + " já existe. Tente de novo");
                }

            }
        } catch (EOFException e) {

        }

    }

    public void verificaTabela(DataInputStream dis, String info) throws IOException {
        boolean existe = false;
        try {
            while (true) {
                if (dis.readUTF().toLowerCase().equals(info.toLowerCase())) {
                    existe = true;
                }

            }

        } catch (EOFException e) {
            if (!existe) {
                throw new StringInvalidaException("A tabela não existe. Verifique se o nome está escrito corretamente");
            }
        }
    }
    
    public String[] verificaEspacos(String str){
        String [] result = str.split(" ");
        int i = 0;
        if(str.contains(" ")){
            StringTokenizer st = new StringTokenizer(str);
            while(st.hasMoreTokens()){
                result[i] = st.nextToken();
                i++;
            }
        }
        return result;
    }

    public void le(DataInputStream entrada) throws Exception {
        String nomeArqTabela = "";
        String nomeTabela = "";
        String nomeAtributo = "";
        String tipoAtributo = "";
        Boolean ehChave = null;
        String chave = "não";
        DataInputStream arqAt = null;

        try {
            while (true) {
                nomeTabela = entrada.readUTF();
                System.out.println(nomeTabela + "\n");
                System.out.println("Atributo ---------- Tipo --------- Chave Primária\n");
                arqAt = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeTabela)));
                do {
                    nomeAtributo = arqAt.readUTF();
                    if (nomeAtributo.equals("fim_do_arquivo")) {
                        break;
                    }
                    tipoAtributo = arqAt.readUTF();
                    ehChave = arqAt.readBoolean();
                    if (ehChave) {
                        chave = "sim";
                    }

                    System.out.println(nomeAtributo + "            " + tipoAtributo + "             " + chave);
                } while (!nomeAtributo.equals("fim_do_arquivo"));
                System.out.println("_____________________________________________________________________________________________");
            }
        } catch (EOFException e) {
            //arquivo terminou
        } finally {
            if (arqAt != null) {
                arqAt.close();
            }
        }
    }

}
