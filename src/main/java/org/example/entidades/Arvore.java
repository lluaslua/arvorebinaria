package org.example.entidades;

public class Arvore {
    private Folha folha;
    private Arvore esquerda;
    private Arvore direita;

    public Arvore() {
        this.folha = null;
        this.esquerda = null;
        this.direita = null;
    }

    public Arvore(Folha folha) {
        this.folha = folha;
        this.esquerda = null;
        this.direita = null;
    }

    public boolean isEmpty() {
        return this.folha == null;
    }

    public Folha getFolha() {
        return folha;
    }

    public Arvore getEsquerda() {
        return esquerda;
    }

    public Arvore getDireita() {
        return direita;
    }

    public void inserir(Folha novo) {
        if (isEmpty()) {
            this.folha = novo;
        } else {
            if (novo.getValor() < this.folha.getValor()) {
                if (this.esquerda == null) {
                    this.esquerda = new Arvore(novo);
                } else {
                    this.esquerda.inserir(novo);
                }
            } else {
                if (this.direita == null) {
                    this.direita = new Arvore(novo);
                } else {
                    this.direita.inserir(novo);
                }
            }
        }
    }

    public void inverterNos() {
        if (isEmpty()) {
            return;
        }
        Arvore temp = this.esquerda;
        this.esquerda = this.direita;
        this.direita = temp;

        if (this.esquerda != null) {
            this.esquerda.inverterNos();
        }
        if (this.direita != null) {
            this.direita.inverterNos();
        }
    }

    public String imprimirArvore() {
        if (isEmpty()) {
            return "(vazia)";
        }
        return imprimirArvore(this);
    }

    private String imprimirArvore(Arvore no) {
        if (no == null || no.isEmpty()) {
            return "-";
        }
        String esquerdaStr = imprimirArvore(no.esquerda);
        String direitaStr = imprimirArvore(no.direita);
        return no.folha.getValor() + "(" + esquerdaStr + ", " + direitaStr + ")";
    }
}