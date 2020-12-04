package com.tamargo.datos;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GuardarLogs {

    public static final Logger logger = inicializarLog();
    private static final int numLogsMaximos = 12;

    public static synchronized Logger inicializarLog() {
        Logger logger = Logger.getLogger("ProyectoUD5");
        FileHandler fh;
        try {
            comprobarCarpetaLogs();

            // Cogemos la fecha y la formateamos para añadirsela al nombre del log
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();

            String rutaLog = "./logs/log" + dtf.format(now) + ".log";
            fh = new FileHandler(rutaLog, true);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            logger.addHandler(fh);

            borrarLogsSobrantes();
        } catch (IOException e) {
            System.out.println("Error con el log");
        }
        return logger;
    }

    public static synchronized void borrarLogsSobrantes() {
        try {
            File path = new File("./logs");
            ArrayList<File> logs = new ArrayList<>();
            ArrayList<File> logsLCK = new ArrayList<>();
            for (File f : Objects.requireNonNull(path.listFiles())) {
                if (!f.getName().contains("lck"))
                    logs.add(f);
                else
                    logsLCK.add(f);
            }

            if (logs.size() > numLogsMaximos) {
                File f = logs.get(0);
                boolean borrado = false;
                borrado = f.delete();

                if (borrado) {
                    System.out.println("[Log] Para evitar un excesivo número de logs se ha eliminado el log más antiguo: " + f.getName());
                    borrarLogsSobrantes();
                } else
                    System.out.println("[Log] Se ha intentado eliminar sin éxito");
            }

            if (logsLCK.size() > 1) {
                while (logsLCK.size() > 1) {
                    logsLCK.get(0).delete();
                }
            }

        } catch (Exception ignored) { }
    }

    public static synchronized void comprobarCarpetaLogs() {
        File f = new File("./logs");
        if (!f.exists()) {
            System.out.println("[Log] La carpeta logs no existe");
            System.out.println("[Log] Creando la carpeta logs...");
            boolean crearCarpeta = f.mkdirs();
            if (crearCarpeta)
                System.out.println("[Log] Carpeta logs creada");
            else
                System.out.println("[Log] Error al crear la carpeta logs");
        }
    }

}
