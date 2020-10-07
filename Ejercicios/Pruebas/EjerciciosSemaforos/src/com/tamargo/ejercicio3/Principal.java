package com.tamargo.ejercicio3;

import java.util.concurrent.Semaphore;

public class Principal {

    final int CAPACIDADMANGAS = 20;
    final int CAPACIDADCUERPOS = 8;
    static volatile boolean fin;
    Semaphore cestaMangasCapacidad, cestaMangasCantidad;
    Semaphore cestaCuerposCapacidad, cestaCuerposCantidad;
    FabricanteMangas fabricanteMangas;
    FabricanteCuerpos fabricanteCuerpos;
    MontadoraJerseys montadoraJerseys;

    public static void main(String[] args) {




    }

}

