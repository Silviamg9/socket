package sockets.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.ServerSocket;

public class ServidorSocketStream {

    public static void main(String[] args) {
        try {
            System.out.println("Creando socket servidor");

            ServerSocket serverSocket = new ServerSocket();

            System.out.println("Realizando el bind");

            // Configura el servidor para escuchar en IP válida y puerto 5555
            InetSocketAddress addr = new InetSocketAddress("0.0.0.0", 5555);
            serverSocket.bind(addr);

            // Añade logs de IP/puerto donde escucha el servidor.
            System.out.println("Servidor escuchando en IP: " + addr.getHostName() + " Puerto: " + addr.getPort());

			Socket newSocket = serverSocket.accept();

			System.out.println("Conexión recibida desde: " + newSocket.getRemoteSocketAddress());

			InputStream is = newSocket.getInputStream();
			OutputStream os = newSocket.getOutputStream();

			byte[] mensaje = new byte[100];
			is.read(mensaje);

			System.out.println("Mensaje recibido: " + new String(mensaje));

			System.out.println("Cerrando el nuevo socket");
			newSocket.close();

			System.out.println("Cerrando el socket servidor");
			serverSocket.close();

			System.out.println("Terminado");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}