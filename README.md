_**Ejercicio 1: VM (Servidor) ↔ Host (Cliente)**_

**📝 Descripción**

  - En este ejercicio se ha configurado una comunicación TCP entre una **Máquina Virtual (Linux)**
    actuando como servidor y el **Host (PC Real)** actuando como cliente. Se ha sustituido el uso de
    localhost por una configuración de red real mediante un **Adaptador Puente (Bridged).**

**🌐 Configuración de Red**

  - **IP de la VM (Servidor):** 192.168.0.21
  - **Puerto:** 5555
  - **Interfaz de escucha del Servidor:** 0.0.0.0

  **Cómo se obtuvo la IP de la VM** 
  
  - Para obtener la dirección IP interna de la máquina Linux, se ejecutó el siguiente comando en la terminal: **hostname -I**.
    Esto devolvió la **IP 192.168.0.21**, que es la dirección que el cliente utiliza para establecer la conexión.

**🚀 Ejecución y Pruebas**

  1. **En la VM:** Se ejecutó ServidorSocketStream.java. El servidor queda bloqueado en el método accept() esperando una conexión.
  2. **En el Host:** Se ejecutó ClienteSocketStream.java apuntando a la IP de la VM.
  3. **Resultado:** El cliente conectó correctamente, envió el mensaje y el servidor lo imprimió por pantalla.

**📸 Evidencias**

  1. **Prueba de conectividad (Ping)**

     ![Ping](ping.png)
     
  2. **Ejecución con éxito**

    ![Ejecución](ejecucion.png)

     
