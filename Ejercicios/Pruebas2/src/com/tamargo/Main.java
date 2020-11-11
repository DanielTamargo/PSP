package com.tamargo;

import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        String dni = "72831820C";
        Pattern dniPattern = Pattern.compile("[0-9]{8}[a-zA-Z]");
        if (dniPattern.matcher(dni).find())
            System.out.println("DNI Válido");
        else
            System.out.println("DNI Erróneo");


    }

    

}
