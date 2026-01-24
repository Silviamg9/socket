package com.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Hello world!
 *
 */
public class AppServerSocket 
{
	private final static int PORT = 7777;
	private static int numGen;
	private static int intentos = 0; // MEJORA 2: Variable para contar intentos
	
	
    public static void main( String[] args ) {
    	
    	//Generar número
    	numGen = (new Random()).nextInt(10)+1;
    	
    	try {
    		ServerSocket srvSock = new ServerSocket(PORT);
    		System.out.println("ServerSocket en puerto: " + PORT);

    		//Abrimos el socket y escuchamos
    		Socket client = srvSock.accept();
    		mostrarInfoCliente(client);
    		
    		
    		//Para mandar datos al cliente >>>
    		PrintWriter salida = new PrintWriter(client.getOutputStream(), true);
    		//Para recibir datos al cliente <<<
    		BufferedReader entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
    	
    		// --- MEJORA 1: MENSAJE DE BIENVENIDA ---
            // Se envía este mensaje al cliente inmediatamente tras el accept()
            salida.println("<Server> ¡Bienvenido al Juego del Número Secreto! Intenta adivinar del 1 al 10.");
    		
    		String datoRec;
    		
    		//leeremos todos los mensajes recibidos
    		//comprobamos si es el número mágico
    		while ((datoRec = entrada.readLine()) != null) {
                // MEJORA 2: Se procesa el número y se devuelven los intentos
                String respuesta = checkNumero(datoRec);
                salida.println(respuesta);
                
                if (respuesta.contains("GANASTE")) break; 
            }

            client.close();
            srvSock.close();
    		
    	}catch(IOException e) {
    		System.err.println("Problemas en el socket");
    		e.printStackTrace();
    		System.exit(1);
    	}
    }
	private static void mostrarInfoCliente(Socket client) {
			// Obtener la IP del cliente
			InetAddress clientAddress = client.getInetAddress();
			String clientIP = clientAddress.getHostAddress();
			String hostName = clientAddress.getHostName();
		
			System.out.println("IP:" + clientIP + ", HostName: "+ hostName);
	}
	private static String checkNumero(String datoRec) {
		
		intentos++; // Incrementamos el contador en cada recepción
		
		try {
            int numero = Integer.parseInt(datoRec.trim());
            if (numero > numGen) {
                return "<Server> El número es MENOR. (Intentos: " + intentos + ")";
            } else if (numero < numGen) {
                return "<Server> El número es MAYOR. (Intentos: " + intentos + ")";
            } else {
                return "<Server> ¡GANASTE! Lo lograste en " + intentos + " intentos.";
            }
        } catch (NumberFormatException e) {
            return "<Server> Por favor, introduzca un número";
        }
		
	}
}
