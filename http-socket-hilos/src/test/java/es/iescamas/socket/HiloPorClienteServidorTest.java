package es.iescamas.socket;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HiloPorClienteServidorTest {

	private HiloPorClienteServidor server;
    private Thread serverThread;
    private int port;

    @BeforeEach
    void startServer() throws Exception {
        // Busca un puerto libre para evitar conflictos
        try (ServerSocket tmp = new ServerSocket(0)) {
            port = tmp.getLocalPort();
        }
        server = new HiloPorClienteServidor(port);
        serverThread = new Thread(server);
        serverThread.start();
        Thread.sleep(300); // Espera a que el servidor esté listo
    }

    @AfterEach
    void stopServer() throws Exception {
        server.stop();
        serverThread.interrupt();
    }

    @Test
    @DisplayName("C1: GET /nombre/Silvia devuelve 200 OK")
    void shouldSayHelloToSilvia() throws Exception {
    	// Simula la petición de un cliente real al servidor
        String response = sendRequest("/nombre/Silvia");
        // Verifica (Assert) que la respuesta del servidor contiene el protocolo de éxito
        assertTrue(response.contains("200 OK"), "Debe devolver 200 OK");
        // Comprueba que el saludo dinámico se ha generado correctamente
        assertTrue(response.contains("Hola Silvia"), "Debe saludar a Silvia");
    }

    @Test
    @DisplayName("C2: Ruta inexistente devuelve 404 Not Found")
    void shouldReturn404() throws Exception {
    	// Lanza una petición a una ruta que no existe en el servidor
        String response = sendRequest("/ruta-que-no-existe");
        // Valida que el servidor ejecuta la MEJORA 3
        assertTrue(response.contains("404"), "La respuesta debe contener el código de error 404");
    }

    @Test
    @DisplayName("C3: Concurrencia - Dos clientes a la vez")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldBeConcurrent() throws Exception {
    	// CompletableFuture lanza las peticiones en hilos separados de forma asíncrona
        CompletableFuture<String> res1 = CompletableFuture.supplyAsync(() -> {
            try { return sendRequest("/nombre/Ana"); } catch (Exception e) { return ""; }
        });
        CompletableFuture<String> res2 = CompletableFuture.supplyAsync(() -> {
            try { return sendRequest("/nombre/Pepe"); } catch (Exception e) { return ""; }
        });

        // Verifica que ambos clientes recibieron su respuesta personalizada sin estorbarse
        assertTrue(res1.get().contains("Hola Ana"));
        assertTrue(res2.get().contains("Hola Pepe"));
    }

    private String sendRequest(String path) throws Exception {
        try (Socket s = new Socket("127.0.0.1", port);
             OutputStream out = s.getOutputStream();
             InputStream in = s.getInputStream()) {
            String req = "GET " + path + " HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
            out.write(req.getBytes(StandardCharsets.UTF_8));
            out.flush();
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
