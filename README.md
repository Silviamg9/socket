**📝 Ejercicio 2: Análisis de la solución (Problema / Causa / Solución)**

- En este ejercicio hemos corregido la lectura del flujo de datos para evitar caracteres vacíos o "basura" al final de los mensajes recibidos.
 
|                  Problema                 |                                                    Causa                                                |                                                                    Solución                                                                       |
| :---------------------------------------- | :------------------------------------------------------------------------------------------------------ | :------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Mensaje con espacios extra ("basura")** | Se imprime el array de bytes completo (25 bytes) aunque el mensaje recibido sea más corto (ej. "Hola"). | Almacenar el retorno de `is.read(buffer)` en una variable `leidos` y usar el constructor `new String(buffer, 0, leidos)`.                         |
| **Pérdida de datos / Mensaje incompleto** | Si el mensaje enviado es mayor que el tamaño del buffer definido (25 bytes), el texto se corta.         | Implementar un bucle `while` que siga leyendo del `InputStream` mientras haya bytes disponibles o usar clases de alto nivel como `BufferedReader`.|
