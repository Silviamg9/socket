package es.iescamas.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Servidor TCP que atiende clientes mediante un hilo por conexión.
 * Sirve HTML básico y un favicon desde src/main/resources/favicon.ico
 */
public class HiloPorClienteServidor implements Runnable {

    /** Puerto donde escucha el servidor. */
    protected int serverPort = 9001;

    /** Socket servidor. */
    protected ServerSocket serversocket = null;

    /** Flag de parada. */
    protected boolean isStopped;

    /** Referencia al hilo que ejecuta run(). */
    protected Thread runningThread = null;

    public HiloPorClienteServidor(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }

        openServerSocket();

        while (!isStopped()) {
            try {
                Socket clientSocket = this.serversocket.accept();

                new Thread(() -> {
                    try {
                        processClientRequest(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, "client-" + clientSocket.getPort()).start();

            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server stopped.");
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
        }

        System.out.println("Server Stopped");
    }

    /**
     * Procesa la conexión de un cliente.
     */
    private void processClientRequest(Socket clientSocket) throws IOException {
        try (clientSocket;
             InputStream in = clientSocket.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
             OutputStream out = clientSocket.getOutputStream()) {

            // 1) Leer la primera línea: "GET /ruta HTTP/1.1"
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isBlank()) return;

            String path = "/";
            if (requestLine.startsWith("GET ")) {
                int start = 4;
                int end = requestLine.indexOf(' ', start);
                if (end > start) 
                	path = requestLine.substring(start, end);
            }

            // 2) Favicon: servir el fichero real desde resources y salir
            if ("/favicon.ico".equals(path)) {
                serveFavicon(out);
                return;
            }

            // --- LÓGICA DE MEJORAS 1 Y 2 ---
            String mensajeSaludo = "Panel de Control del Servidor";
            String contenidoExtra = "";

            if (path.startsWith("/nombre/")) {
                // Mejora 1: Ruta dinámica
                mensajeSaludo = "Hola " + path.substring(8);
                contenidoExtra = "<p style='font-size: 1.2em;'>Has accedido a través de una ruta dinámica.</p>"
                               + "<a href='/' style='color:#3498db; text-decoration:none;'>← Volver al inicio</a>";
            } else if (path.equals("/")) {
                // Mejora 2: Página de inicio con navegación (Mejora libre)
                mensajeSaludo = "Bienvenido al Servidor Concurrente";
                contenidoExtra = "<h3>Pruebas de Concurrencia Real:</h3>"
                        + "<p>Haz clic en estos enlaces para abrir hilos distintos:</p>"
                        + "<ul style='list-style: none; padding: 0;'>"
                        + "<li style='margin-bottom:10px;'><a href='/nombre/Ana' style='color:#3498db;'>Saludar a Ana</a></li>"
                        + "<li style='margin-bottom:10px;'><a href='/nombre/Pepe' style='color:#3498db;'>Saludar a Pepe</a></li>"
                        + "<li><a href='/nombre/Lucia' style='color:#3498db;'>Saludar a Lucia</a></li>"
                        + "</ul>";
            }
            
            // 3) Datos del cliente
            String clientIp = clientSocket.getInetAddress().getHostAddress();
            int clientPort = clientSocket.getPort(); // puerto remoto del cliente
            String remote = clientSocket.getRemoteSocketAddress().toString(); // /IP:PUERTO

            long time = System.currentTimeMillis();
            String fecha = new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date(time));

            String body = "<html>"
                    + "<head>"
                    + "<link rel='icon' href='/favicon.ico'>"
                    + "<title>PSP - Práctica 4</title>"
                    + "</head>"
                    + "<body style='background-color: coral; font-family: sans-serif; padding: 20px;'>"
                    + "<h1 style='color:blue;'>" + mensajeSaludo + "</h1>" // Muestra el saludo dinámico
                    + "<div>" + contenidoExtra + "</div>"               // Muestra los enlaces o el texto extra
                    + "<hr>"
                    + "<p><b>Path actual:</b> " + path + "</p>"
                    + "<p><b>Hilo asignado:</b> " + Thread.currentThread().getName() + "</p>"
                    + "<p><b>IP Cliente:</b> " + clientIp + "</p>"
                    + "<p><b>Fecha Server:</b> " + fecha + "</p>"
                    + "</body></html>";

            byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

            String headers =
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + bodyBytes.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

            out.write(headers.getBytes(StandardCharsets.US_ASCII));
            out.write(bodyBytes);
            out.flush();

            // Log: ignorar favicon (ya se devuelve arriba) y registrar petición normal
            System.out.println("[" + Thread.currentThread().getName() + "] " + requestLine);
            System.out.println("[" + Thread.currentThread().getName() + "] Cliente: " + remote);
            System.out.println("[" + Thread.currentThread().getName() + "] Petición procesada: " + fecha);
        }
    }

    /**
     * Sirve el favicon real desde el classpath: src/main/resources/favicon.ico
     */
    private void serveFavicon(OutputStream out) throws IOException {
        try (InputStream iconStream = HiloPorClienteServidor.class.getResourceAsStream("/favicon.ico")) {

            if (iconStream == null) {
                out.write(("HTTP/1.1 404 Not Found\r\nConnection: close\r\n\r\n")
                        .getBytes(StandardCharsets.US_ASCII));
                out.flush();
                return;
            }

            byte[] iconBytes = iconStream.readAllBytes();

            String headers =
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: image/x-icon\r\n" +
                    "Content-Length: " + iconBytes.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

            out.write(headers.getBytes(StandardCharsets.US_ASCII));
            out.write(iconBytes);
            out.flush();
        }
    }

    private synchronized boolean isStopped() {
        return isStopped;
    }

    private void openServerSocket() {
        try {
            this.serversocket = new ServerSocket(this.serverPort);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot open port " + serverPort, ex);
        }
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            if (this.serversocket != null) {
                this.serversocket.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
