package sockets.tcp;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClienteSocketStream {

    public static void main(String[] args) {
        try {
            System.out.println("Creando socket cliente");

            Socket clientSocket = new Socket();

            // Configura el cliente para conectar a la IP real de la VM y ese puerto.
            String ipVm = "192.168.0.21";
			int puerto = 6666;
            
			// Añade logs de IP/puerto y del cliente remoto.
			System.out.println("Estableciendo la conexión con " + ipVm + ":" + puerto);

            InetSocketAddress addr = new InetSocketAddress(ipVm, puerto);
            clientSocket.connect(addr);

            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            System.out.println("Enviando mensaje");

            String mensaje = "mensaje desde el cliente";
            os.write(mensaje.getBytes());

            System.out.println("Mensaje enviado");

            System.out.println("Cerrando el socket cliente");
            clientSocket.close();

            System.out.println("Terminado");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}