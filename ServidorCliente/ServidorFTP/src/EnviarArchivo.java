import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class EnviarArchivo {


    static String Archivo = "ejemplo.txt";

    public EnviarArchivo(String nombreArchivo){
        this.Archivo=Archivo;
    }

    public void enviarArchivo() throws IOException {
        // Creamos la direccion IP de la maquina que recibira el archivo
        InetAddress direccion = InetAddress.getByName("localhost");
        // Creamos el Socket con la direccion y elpuerto de comunicacion
        Socket socket = new Socket( direccion, 21 );
        socket.setSoTimeout( 2000 );//tiempo de espera para el accept
      
        // Creamos el archivo que vamos a enviar
        File archivo = new File(Archivo);// Obtenemos el tamaño del archivo
        int tamArchivo = ( int )archivo.length();
        // Creamos el flujo de salida, este tipo de flujo nos permite
        // hacer la escritura de diferentes tipos de datos tales como
        // Strings, boolean, caracteres y la familia de enteros,etc.
        DataOutputStream dos = new DataOutputStream( socket.getOutputStream() );
        System.out.println( "Enviando Archivo: "+archivo.getName() );
        // Enviamos el nombre del archivo
        dos.writeUTF( archivo.getName() );
        // Enviamos el tamaño del archivo
        dos.writeInt( tamArchivo );
        // Creamos flujo de entrada para realizar la lectura del archivo en bytes
        FileInputStream fis = new FileInputStream(Archivo);
        BufferedInputStream bis = new BufferedInputStream( fis );
        // Creamos el flujo de salida para enviar los datos del archivo en
        BufferedOutputStream bos = new BufferedOutputStream( socket.getOutputStream());
        // Creamos un array de tipo byte con el tamaÃƒÂ±o del archivo
        byte[] buffer = new byte[ tamArchivo ];
        // Leemos el archivo y lo introducimos en el array de bytes
        bis.read( buffer );
        // Realizamos el envio de los bytes que conforman el archivo
        for( int i = 0; i < buffer.length; i++ ){
            bos.write( buffer[ i ] );
        }
        System.out.println( "Archivo Enviado: "+archivo.getName() );
        // Cerramos socket y flujos
        bis.close();bos.close();
        socket.close();

    }
    public static void main( String args[] ) throws IOException{
        EnviarArchivo ea=new EnviarArchivo(Archivo);
        ea.enviarArchivo();

    }
}


