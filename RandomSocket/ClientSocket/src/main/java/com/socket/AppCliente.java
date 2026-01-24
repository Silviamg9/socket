package com.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.UnknownHostException;

/**
 * Hello world!
 *
 */
public class AppCliente {
	
	static final String IP_SERVER = "192.168.0.21";
	static final int  PORT = 7777;
	
    public static void main( String[] args )
    {
    	
        try {
        	//conectamos con el servidor
        	Socket socket = new Socket(IP_SERVER,PORT);
        	
        	//Para enviar datos al server
        	PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
        	
        	//Para recibir respuestas del servidor
        	BufferedReader entradaSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        	//Leer los datos introducidos por consola
        	BufferedReader entradaConsola = new BufferedReader(new InputStreamReader(System.in));
        	
        	String datoRec;
        	while((datoRec = entradaSocket.readLine()) != null){
        		//Mostrar el dato recibido por consola
        		System.out.println(datoRec);
        		
        		System.out.print("<Cliente> Introduce un número: ");
                String numeroParaEnviar = entradaConsola.readLine();
                salida.println(numeroParaEnviar);
            }
        	
        } catch (IOException e) {
			
			e.printStackTrace();
		}
    }
}
