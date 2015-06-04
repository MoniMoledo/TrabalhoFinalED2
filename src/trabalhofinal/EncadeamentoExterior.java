package trabalhofinal;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;


public class EncadeamentoExterior {

    /**
     * Cria uma tabela hash vazia de tamanho tam, e salva no arquivo
     * nomeArquivoHash Compartimento que não tem lista encadeada associada deve
     * ter valor igual a -1
     *
     * @param nomeArquivoHash nome do arquivo hash a ser criado
     * @param tam tamanho da tabela hash a ser criada
     */
    public void criaHash(String nomeArquivoHash, int tam) throws FileNotFoundException, IOException {
        RandomAccessFile raf = new RandomAccessFile(nomeArquivoHash, "rw");
        for (int i = 0; i < tam; i++) {
            raf.writeInt(-1);
        }
        raf.close();
    }

    /**
     * Executa busca em Arquivos por Encadeamento Exterior (Hash) Assumir que
     * ponteiro para próximo nó é igual a -1 quando não houver próximo nó
     *
     * @param codCli: chave do cliente que está sendo buscado
     * @param nomeArquivoHash nome do arquivo que contém a tabela Hash
     * @param nomeArquivoDados nome do arquivo onde os dados estão armazenados
     * @return o endereco onde o cliente foi encontrado, ou -1 se não for
     * encontrado
     */
  /*  public int busca(int codCli, String nomeArquivoHash, String nomeArquivoDados) throws Exception {
        int resto = codCli % 7;
        int anterior = 0;
        RandomAccessFile tabHash = new RandomAccessFile(nomeArquivoHash, "rw");
        RandomAccessFile tabDados = new RandomAccessFile(nomeArquivoDados, "rw");
        tabHash.seek(resto * Integer.BYTES);
        Cliente cAux = null;
        int a = tabHash.readInt();
        if (a == -1) {
            return -1;
        } else {
            int prox = a;
            while (prox != -1) {
                tabDados.seek(prox * Cliente.tamanhoRegistro);
                cAux = Cliente.le(tabDados);
                if (cAux.codCliente == codCli && !cAux.flag) {
                    return prox;
                }
                anterior = prox;
                prox = cAux.prox;
            }
            if (codCli == cAux.codCliente && !cAux.flag) {
                return anterior;
            }
            return -1;
        }
    }

   
    public int insere(int codCli, String nomeCli, String nomeArquivoHash, String nomeArquivoDados, int numRegistros) throws Exception {
        int resto = codCli % 7;//  7 ->>tamanho da tabela hash 
        RandomAccessFile tabHash = new RandomAccessFile(nomeArquivoHash, "rw");
        RandomAccessFile tabDados = new RandomAccessFile(nomeArquivoDados, "rw");
        Cliente cliente = new Cliente(codCli, nomeCli, -1, false);
        Cliente cAux = null;
        tabHash.seek(resto * Integer.SIZE / 8);
        int a = tabHash.readInt(); // referencia a tabela de dados
        if (a == -1) {
            tabHash.seek(resto * Integer.SIZE / 8);
            tabHash.writeInt(numRegistros);
            tabDados.seek(numRegistros * Cliente.tamanhoRegistro);
            cliente.salva(tabDados);
            return numRegistros;
        } else {

            int prox = a;
            while (prox != -1) {
                tabDados.seek(prox * Cliente.tamanhoRegistro);
                cAux = Cliente.le(tabDados);
                if (cAux.codCliente == cliente.codCliente) {
                    return -1;
                }

                if (cAux.flag) {
                    tabDados.seek(prox * Cliente.tamanhoRegistro);
                    cliente.prox = cAux.prox;
                    cliente.salva(tabDados);
                    return prox;
                }
                tabDados.seek(prox * Cliente.tamanhoRegistro);
                prox = cAux.prox;
            }
            cAux.prox = numRegistros;
            cAux.salva(tabDados);
            tabDados.seek(numRegistros * Cliente.tamanhoRegistro);
            cliente.salva(tabDados);
            return numRegistros;
        }
    }

    
    public int exclui(int CodCli, String nomeArquivoHash, String nomeArquivoDados) throws Exception {
        //TODO: Inserir aqui o código do algoritmo de remoção
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
    }*/
}
