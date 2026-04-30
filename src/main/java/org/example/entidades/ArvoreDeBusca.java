package org.example.entidades;


import org.example.interfaces.InterfaceArvore;

public class ArvoreDeBusca implements InterfaceArvore {
    private Folha folha;
    private ArvoreDeBusca esquerda;
    private ArvoreDeBusca direita;


    public ArvoreDeBusca(Folha folha) {
        this.folha = folha;
        this.esquerda = null;
        this.direita = null;
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


    @Override
    public InterfaceArvore inserir(Folha novo) {
        if (novo.getValor() < this.folha.getValor()) {
            if (this.esquerda == null) {
                this.esquerda = new ArvoreDeBusca(novo);
            } else {
                this.esquerda = (ArvoreDeBusca) this.esquerda.inserir(novo);
            }
        } else if (novo.getValor() > this.folha.getValor()) {
            if (this.direita == null) {
                this.direita = new ArvoreDeBusca(novo);
            } else {
                this.direita = (ArvoreDeBusca) this.direita.inserir(novo);
            }
        }
        return this;
    }
}