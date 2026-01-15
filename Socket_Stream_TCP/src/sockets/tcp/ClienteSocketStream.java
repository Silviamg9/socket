package sockets.tcp;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClienteSocketStream {

    public static void main(String[] args) {
    	System.out.println("Creando socket cliente");
		try (Socket clientSocket = new Socket()) {
			
			System.out.println("Estableciendo la conexión");
			// IP de tu VM Linux
			InetSocketAddress addr = new InetSocketAddress("192.168.0.21", 5555);
			clientSocket.connect(addr);

			OutputStream os = clientSocket.getOutputStream();
			
			System.out.println("Enviando mensaje");
			String mensaje = "mensaje desde el cliente ejercicio 2";
			os.write(mensaje.getBytes());

			System.out.println("Mensaje enviado");
			System.out.println("Cerrando el socket cliente");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Terminado");
    }
}