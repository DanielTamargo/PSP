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
    private static final int numLogsMaximos = 8;

    /**
     * Inicializa el log, le genera un FileHandler y configura los niveles que aceptará
     * Este método llamará a comprobarCarpetaLogs() y borrarLogsSobrantes()
     */
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

    /**
     * Borra los logs "sobrantes"
     * La variable numLogsMaximos determinará cuantos logs puede almacenar, una vez se exceda el número, se eliminarán
     * los logs más antiguos
     */
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
                System.out.println(f.getName());
            }

            if (logs.size() > numLogsMaximos) {
                while (logs.size() > numLogsMaximos) {
                    File f = logs.get(0);
                    boolean borrado = false;
                    borrado = f.delete();

                    if (borrado) {
                        System.out.println("[Log] Para evitar un excesivo número de logs se ha eliminado el log más antiguo: " + f.getName());
                    } else {
                        System.out.println("[Log] Se ha intentado eliminar sin éxito");
                    }

                    logs.remove(0);
                }
            }

            if (logsLCK.size() > 1) {
                while (logsLCK.size() > 1) {
                    logsLCK.get(0).delete();
                    logsLCK.remove(0);
                }
            }

        } catch (Exception ignored) { }
    }

    /**
     * Comprueba que la carpeta logs existe, si no existe, la crea para poder generar dentro los logs
     */
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
