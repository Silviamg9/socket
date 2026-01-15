package sockets.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.ServerSocket;

public class ServidorSocketStream {

    public static void main(String[] args) {
    	System.out.println("Creando socket servidor");
		try (ServerSocket serverSocket = new ServerSocket()) {
			
			System.out.println("Realizando el bind");
			// Escuchamos en 0.0.0.0 para aceptar conexiones de cualquier IP
			InetSocketAddress addr = new InetSocketAddress("0.0.0.0", 5555);
			serverSocket.bind(addr);

			System.out.println("Servidor escuchando en IP: 0.0.0.0 Puerto: 5555");

			// Aceptamos la conexión del cliente
			try (Socket newSocket = serverSocket.accept()) {
				System.out.println("Conexión recibida desde: " + newSocket.getRemoteSocketAddress());

				InputStream is = newSocket.getInputStream();
				
				// Definimos un buffer de 25 bytes
				byte[] buffer = new byte[25];
				
				int bytesLeidos = is.read(buffer);

				if (bytesLeidos != -1) {
					String mensajeLimpio = new String(buffer, 0, bytesLeidos);
					System.out.println("Mensaje recibido: " + mensajeLimpio);
				}

				System.out.println("Cerrando el nuevo socket");
			}
			
			System.out.println("Cerrando el socket servidor");
			System.out.println("Terminado");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}