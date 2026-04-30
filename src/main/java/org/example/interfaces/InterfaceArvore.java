package org.example.interfaces;

import org.example.entidades.Folha;

public interface InterfaceArvore {
    Folha getFolha();
    InterfaceArvore getEsquerda();
    InterfaceArvore getDireita();
    InterfaceArvore inserir(Folha novo);
}