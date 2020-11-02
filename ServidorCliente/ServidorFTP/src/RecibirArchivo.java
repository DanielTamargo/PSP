import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecibirArchivo {

    private ServerSocket servidor = null;
    public RecibirArchivo( ) throws IOException, IOException {
        // Creamos socket servidor escuchando en el mismo puerto donde se comunicael cliente
        // en este caso el puerto es el 4400
        servidor = new ServerSocket( 21 );
        System.out.println( "Esperando recepcion de archivos..." );
    }
    public void iniciarServidor(){
        while( true ){
            try (
                //Creamos el socket que atendera el servidor
                Socket cliente = servidor.accept()) {
                // Creamos flujo de entrada para leer los datos que envia el cliente
                DataInputStream dis = new DataInputStream( cliente.getInputStream() );
                // Obtenemos el nombre del archivo
                String nombreArchivo = dis.readUTF().toString();
                //Obtenemos el tamaÃ±o del archivo
                int tam = dis.readInt();
                System.out.println( "Recibiendo archivo "+nombreArchivo);
                // Creamos flujo de salida, este flujo nos sirve para
                // indicar donde guardaremos el archivo
                FileOutputStream fos = new FileOutputStream("F:\\Copia//"+nombreArchivo);
                //Ruta donde se va a guardar el archivo
                BufferedOutputStream out = new BufferedOutputStream(fos);
                BufferedInputStream in = new BufferedInputStream( cliente.getInputStream() );
                // Creamos el array de bytes para leer los datos del archivo
                byte[] buffer = new byte[ tam ];
                // Obtenemos el archivo mediante la lectura de bytes enviados
                for( int i = 0; i < buffer.length; i++ ){
                    buffer[i] = (byte)in.read();
                }
                // Escribimos el archivo
                out.write(buffer);
                // Cerramos flujos
                out.flush();
                in.close();
                out.close();
                System.out.println( "Archivo Recibido "+nombreArchivo);
            } catch (IOException ex) {
                Logger.getLogger(RecibirArchivo.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    // Lanzamos el servidor para la recepciÃ³n de archivos
    public static void main( String args[] ) throws IOException{
        new RecibirArchivo().iniciarServidor();
    }
}


