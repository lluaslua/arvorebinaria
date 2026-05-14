package org.example.entidades;

import org.example.interfaces.InterfaceArvore;

import java.util.ArrayList;
import java.util.List;

public class ArvoreAVL implements InterfaceArvore {

        private static final List<String> logs = new ArrayList<>();

        public static List<String> getLogs() {
            return new ArrayList<>(logs);
        }

        public static void limparLogs() {
            logs.clear();
        }
        private Folha folha;
        private ArvoreAVL esquerda;
        private ArvoreAVL direita;
        private int altura;

        public ArvoreAVL(Folha folha) {
            this.folha = folha;
            this.esquerda = null;
            this.direita = null;
            this.altura = 1;
        }

        @Override
        public Folha getFolha() {
            return folha;
        }

        @Override
        public InterfaceArvore getEsquerda() {
            return esquerda;
        }

        @Override
        public InterfaceArvore getDireita() {
            return direita;
        }


        private int altura(ArvoreAVL no) {
            return (no == null) ? 0 : no.altura;
        }


        private int fatorBalanceamento(ArvoreAVL no) {
            return (no == null) ? 0 : altura(no.esquerda) - altura(no.direita);
        }


        private ArvoreAVL rotacaoDireita(ArvoreAVL y) {
            ArvoreAVL x = y.esquerda;
            ArvoreAVL T2 = x.direita;

            x.direita = y;
            y.esquerda = T2;

            y.altura = Math.max(altura(y.esquerda), altura(y.direita)) + 1;
            x.altura = Math.max(altura(x.esquerda), altura(x.direita)) + 1;

            return x;
        }


        private ArvoreAVL rotacaoEsquerda(ArvoreAVL x) {
            ArvoreAVL y = x.direita;
            ArvoreAVL T2 = y.esquerda;

            y.esquerda = x;
            x.direita = T2;

            x.altura = Math.max(altura(x.esquerda), altura(x.direita)) + 1;
            y.altura = Math.max(altura(y.esquerda), altura(y.direita)) + 1;

            return y;
        }

        private ArvoreAVL rotacaoDuplaEsquerdaDireita(ArvoreAVL no) {
            no.esquerda = rotacaoEsquerda(no.esquerda);
            return rotacaoDireita(no);
        }

        private ArvoreAVL rotacaoDuplaDireitaEsquerda(ArvoreAVL no) {
            no.direita = rotacaoDireita(no.direita);
            return rotacaoEsquerda(no);
        }

        private void imprimirRotacao(String tipo, int noDesbalanceado) {
            String msg = "Rotação " + tipo + " no nó " + noDesbalanceado;
            System.out.println("[AVL] " + msg);
            logs.add(msg);
        }

        @Override
        public InterfaceArvore inserir(Folha novo) {

            if (novo.getValor() < this.folha.getValor()) {
                if (this.esquerda == null) {
                    this.esquerda = new ArvoreAVL(novo);
                } else {
                    this.esquerda = (ArvoreAVL) this.esquerda.inserir(novo);
                }
            } else if (novo.getValor() > this.folha.getValor()) {
                if (this.direita == null) {
                    this.direita = new ArvoreAVL(novo);
                } else {
                    this.direita = (ArvoreAVL) this.direita.inserir(novo);
                }
            } else {
                return this;
            }


            this.altura = 1 + Math.max(altura(this.esquerda), altura(this.direita));


            int balance = fatorBalanceamento(this);


            if (balance > 1 && novo.getValor() < this.esquerda.folha.getValor()) {
                imprimirRotacao("Simples Direita (LL)", this.folha.getValor());
                return rotacaoDireita(this);
            }


            if (balance < -1 && novo.getValor() > this.direita.folha.getValor()) {
                imprimirRotacao("Simples Esquerda (RR)", this.folha.getValor());
                return rotacaoEsquerda(this);
            }


            if (balance > 1 && novo.getValor() > this.esquerda.folha.getValor()) {
                imprimirRotacao("Dupla Esquerda-Direita (LR)", this.folha.getValor());
                return rotacaoDuplaEsquerdaDireita(this);
            }


            if (balance < -1 && novo.getValor() < this.direita.folha.getValor()) {
                imprimirRotacao("Dupla Direita-Esquerda (RL)", this.folha.getValor());
                return rotacaoDuplaDireitaEsquerda(this);
            }

            return this;
        }
    }
