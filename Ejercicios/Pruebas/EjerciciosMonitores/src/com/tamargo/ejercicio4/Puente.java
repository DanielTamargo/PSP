package com.tamargo.ejercicio4;

public class Puente {

    private final int numMaxVehiculos = 5;
    private final int pesoMax = 2600;

    private int numVehiculos = 0;
    private int pesoTotal = 0;

    public Puente() {
    }

    public synchronized void salirVehiculo(int pesoCoche) {

        if (numVehiculos <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        numVehiculos--;
        pesoTotal -= pesoCoche;
        System.out.println("\tHa salido un vehículo. Vehículos: " + numVehiculos + ", Peso total: " + pesoTotal);
        notifyAll();
    }

    public synchronized void accederVehiculo(int pesoCoche) {

        while (numVehiculos >= numMaxVehiculos || (pesoTotal + pesoCoche >= pesoMax)) {
            try{
                if (numVehiculos >= 5)
                    System.out.println("Ya hay 5 coches, esperando...");
                else
                    System.out.println("No se puede exceder el límite de peso, esperando...");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        numVehiculos++;
        pesoTotal += pesoCoche;
        System.out.println("Ha entrado un vehículo. Vehículos: " + numVehiculos + ", Peso total: " + pesoTotal);
        notifyAll();


    }

}
