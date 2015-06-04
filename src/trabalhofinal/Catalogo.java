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
    //USAR HASHMAP!!!!!!!!!!

    public void addTabela(String nome) { //DESNECESSARIO POIS SÓ ADICIONAMOS 1 TABELA POR VEZ
        nomeTabela.add(nome);
    }

    public void addAtributo(Atributo atr) {
        if (atr.isChave()) {
            atributos.add(0, atr);//SEMPRE SALVA A PK NA PRIMEIRA POSICAO
        } else {
            atributos.add(atr);
        }
    }

    public void salva(DataOutputStream catalogo) throws Exception {
        DataOutputStream catalogTabela = null;
        try {
            //for para guardar no catalogo principal nome da tabela e nome do arquivo que serão guardadas as info dessa tabela("tabela"+j)
            for (int j = 0; j < nomeTabela.size(); j++) {
                catalogo.writeUTF(nomeTabela.get(j));

            }
        } finally {
            if (catalogTabela != null) {
                catalogTabela.close();
            }
        }
    }

    public void salvarAtributos(DataOutputStream dos) throws IOException {
        Atributo prox = new Atributo();
        prox.setChave(false);
        prox.setNome("proximo");
        prox.setTabela(atributos.get(0).getTabela());
        prox.setTipo("integer");
        
        Atributo flag = new Atributo();
        flag.setChave(false);
        flag.setNome("flag");
        flag.setTabela(atributos.get(0).getTabela());
        flag.setTipo("boolean");
        
        atributos.add(1,prox);
        atributos.add(2,flag);
        
        dos.writeInt(atributos.size());
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

    public String[] verificaEspacos(String str) {
        String[] result = str.split(" ");
        int i = 0;
        if (str.contains(" ")) {
            StringTokenizer st = new StringTokenizer(str);
            while (st.hasMoreTokens()) {
                result[i] = st.nextToken();
                i++;
            }
            if ((str.contains("primary_key") && i > 3) || (!str.contains("primary_key") && i > 2)) {
                throw new StringInvalidaException("Não use espaço no nome dos atributos");
            }
        }
        return result;
    }

//    public void verificaChave(DataInputStream dis) throws IOException {
//        Atributo aux = new Atributo();
//        try {
//            while (true) {
//                aux.setNome(dis.readUTF());
//                aux.setTipo(dis.readUTF());
//                aux.setChave(dis.readBoolean());
//                if (aux.isChave()) {
//                    throw new StringInvalidaException("Já existe uma chave primária");
//                }
//            }
//        }
//        catch(EOFException e){
//            
//        }
//    }
    public String[] verificaConsistenciaTabela(String tabela, String[] nomes) {
        if (tabela.contains(" ")) {
            for (int i = 0; i < tabela.split(", ").length; i++) {
                nomes = tabela.split(", ");
            }
        } else if (!tabela.contains(" ")) {
            for (int i = 0; i < tabela.split(",").length; i++) {
                nomes = tabela.split(",");
            }
        } else {
            throw new StringInvalidaException("Não pode haver espaço no nome das tabelas e deve-se usar vírgula"
                    + "\nExemplo: Tabela1,Tabela_2,TabelaTres");
        }
        for (int i = 0; i < nomes.length; i++) {
            if (nomes[i].contains(" ")) {
                throw new StringInvalidaException("Não pode haver espaço no nome das tabelas e deve-se usar vírgula"
                        + "\nExemplo: Tabela1,Tabela_2,TabelaTres");
            }
        }
        return nomes;
    }

    public boolean verificaChaveArquivo(DataInputStream dis) throws IOException {
        Atributo aux = new Atributo();
        dis.readUTF();
        try {
            if (dis == null) {
                return false;
            }
            while (true) {
                aux.setNome(dis.readUTF());
                aux.setTipo(dis.readUTF());
                aux.setChave(dis.readBoolean());
                if (aux.isChave()) {
                    return true;
                }
            }
        } catch (EOFException e) {

        }
        return false;
    }

    public boolean isEmpty(String tabela) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(tabela + ".dat")));
        return dis.available() == 0;
    }

    public boolean isNull(DataInputStream dis) throws IOException {
        return dis.readUTF() == " ";
    }

    public void verificaExistencia(String nome, String tabela) throws FileNotFoundException, IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(tabela + ".dat")));
        dis.readUTF();//dis.readInt();
        try {
            while (true) {
                if (dis.readUTF().toLowerCase().equals(nome.toLowerCase())) {
                    throw new StringInvalidaException("Este atributo já existe");
                }
                dis.readUTF();
                dis.readBoolean();
            }
        } catch (EOFException e) {

        }
        dis.close();
    }

    public void leTabela(DataInputStream dis) throws IOException {
        dis.readUTF();
        dis.readInt();
        try {
            while (true) {//if nomeAtributo !=flag ou proximo..
                System.out.println("Nome Atributo: " + dis.readUTF());
                System.out.println("Tipo Atributo: " + dis.readUTF());
                System.out.println("Chave Primaria: " + dis.readBoolean());
                System.out.println("-------------------------");
            }
        } catch (EOFException e) {

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
                System.out.println(nomeTabela + "\n");
                System.out.println("Atributo ---------- Tipo --------- Chave Primária\n");
                arqAt = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeArqTabela)));
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
