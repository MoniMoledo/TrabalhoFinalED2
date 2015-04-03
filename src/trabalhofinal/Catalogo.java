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
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

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
        at.setTabela(tipo);
        at.setNome(nome);
        at.setTipo(tipo);
        at.setChave(ehChave);
        atributos.add(at);
    }

    public void salva(DataOutputStream catalogo) throws Exception {
        DataOutputStream catalogTabela = null;
        try {
            //for para guardar no catalogo principal nome da tabela e nome do arquivo que serão guardadas as info dessa tabela("tabela"+j)
            for (int j = 0; j < nomeTabela.size(); j++) {
                catalogo.writeUTF(nomeTabela.get(j));
                catalogo.writeUTF("tabela" + j);
                catalogTabela = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("tabela" + j, true)));

                //for para guardar no arquivo "tabela+j" os nomes dos atributos daquela tabela, tipo e se é chave primária.
                for (int i = 0; i < atributos.size(); i++) {
                    if (atributos.get(i).getTabela().equals(nomeTabela.get(j))) {
                        catalogTabela.writeUTF(atributos.get(i).getNome());
                        catalogTabela.writeUTF(atributos.get(i).getTipo());
                        catalogTabela.writeBoolean(atributos.get(i).isChave());
                    }
                }
                catalogTabela.writeUTF("fim_do_arquivo");
            }
        } finally {
            if (catalogTabela != null) {
                catalogTabela.close();
            }
        }
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
                nomeArqTabela = entrada.readUTF();
                System.out.println(nomeTabela + "/n");
                System.out.println("Atributo ---------------- Tipo ----------------Chave Primária/n");
                arqAt = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeArqTabela)));
                do {
                    nomeAtributo = arqAt.readUTF();
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
