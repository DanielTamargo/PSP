package com.tamargo;

import java.io.IOException;

public class LanzarCliente extends Thread {

    private String[] args;

    public LanzarCliente(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        try {
            Cliente.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
