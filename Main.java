import java.util.Scanner;


class Servidor {
    // Atributos de instancia 
    String nombre;          
    String origen;          
    String critico;         
    int intentosTotales;    
    int intentosFallidos;   

    //VALIDAR
    boolean datosValidos() {
        // Vigilar que haya algo escrito
        if (nombre == null || origen == null || critico == null) {
            return false;
        }

        // Ignoramos espacios  (.trim()) y mayúsculas (.toLowerCase())
        String nomLimpio = nombre.trim();
        String origLimpio = origen.trim().toLowerCase();
        String critLimpio = critico.trim().toLowerCase();

        //El nombre contiene al menos un carácter que no sea espacio
        boolean nombreValido = nomLimpio.length() > 0;

        //El origen es "interno" o "externo"
        boolean origenValido = origLimpio.equals("interno") || origLimpio.equals("externo");

        //La criticidad es "si" o "no"
        boolean criticoValido = critLimpio.equals("si") || critLimpio.equals("no");

        //Total > 0 y fallidos entre 0 y el total
        boolean intentosValidos = (intentosTotales > 0) 
                               && (intentosFallidos >= 0) 
                               && (intentosFallidos <= intentosTotales);

        // miramos que todas se cumplan para asi true, sino sera false
        return nombreValido && origenValido && criticoValido && intentosValidos;
    }

    //PRIORIDAD
    String evaluarEstado() {

        if (!datosValidos()) {
            return "DATOS INVALIDOS";
        }

        String origLimpio = origen.trim().toLowerCase();
        String critLimpio = critico.trim().toLowerCase();

        boolean esExterno = origLimpio.equals("externo");
        boolean esCritico = critLimpio.equals("si");

        //BLOQUEADO
        if (intentosFallidos >= 5 || (esCritico && esExterno && intentosFallidos >= 3)) {
            return "BLOQUEADO";
        } 
        //ALERTA
        else if ((intentosFallidos == 3 || intentosFallidos == 4) || (esExterno && intentosFallidos >= 2)) {
            return "ALERTA";
        } 
        //NORMAL
        else {
            return "NORMAL";
        }
    }

    //PORCENTAJE FALLOS
    double porcentajeFallos() {
        // Se multiplica por 100.0 (double) para evitar la división entera truncada
        return (intentosFallidos * 100.0) / intentosTotales;
    }

    // RESUMEN SERVIDOR
    void mostrarResumen() {
        String nombreLimpio = nombre.trim();
        String estadoActual = evaluarEstado();

        if (datosValidos()) {
            double porcentaje = porcentajeFallos();
            // Redondeo a 1 decimaL
            double porcentajeUnDecimal = Math.round(porcentaje * 10.0) / 10.0;
            System.out.println(nombreLimpio + " | " + intentosFallidos + "/" + intentosTotales 
                               + " | " + porcentajeUnDecimal + "% | " + estadoActual);
        } else {
            // Con datos inválidos el porcentaje se muestra como N/D
            System.out.println(nombreLimpio + " | " + intentosFallidos + "/" + intentosTotales 
                               + " | N/D | " + estadoActual);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        // Creamos dos objetos de clase Servidor
        Servidor servidor1 = new Servidor();
        Servidor servidor2 = new Servidor();

        //Lectura primer server
        System.out.println("--- Datos del Servidor 1 ---");
        System.out.print("Nombre del servidor: ");
        servidor1.nombre = teclado.nextLine();
        System.out.print("Origen (interno/externo): ");
        servidor1.origen = teclado.nextLine();
        System.out.print("Critico (si/no): ");
        servidor1.critico = teclado.nextLine();
        System.out.print("Intentos totales: ");
        servidor1.intentosTotales = teclado.nextInt();
        System.out.print("Intentos fallidos: ");
        servidor1.intentosFallidos = teclado.nextInt();
        teclado.nextLine(); // Limpiamos el salto de línea pendiente tras nextInt()

        //Lectura segundo server
        System.out.println("\n--- Datos del Servidor 2 ---");
        System.out.print("Nombre del servidor: ");
        servidor2.nombre = teclado.nextLine();
        System.out.print("Origen (interno/externo): ");
        servidor2.origen = teclado.nextLine();
        System.out.print("Critico (si/no): ");
        servidor2.critico = teclado.nextLine();
        System.out.print("Intentos totales: ");
        servidor2.intentosTotales = teclado.nextInt();
        System.out.print("Intentos fallidos: ");
        servidor2.intentosFallidos = teclado.nextInt();

        // Mostrar resumen
        System.out.println("\n========== REPORTE DE SEGURIDAD ==========");
        servidor1.mostrarResumen();
        servidor2.mostrarResumen();

        // comparar los 2 servers
        String estado1 = servidor1.evaluarEstado();
        String estado2 = servidor2.evaluarEstado();

        if (estado1.equals("DATOS INVALIDOS") || estado2.equals("DATOS INVALIDOS")) {
            System.out.println("COMPARACION NO DISPONIBLE");
        } 
        else if (estado1.equals(estado2)) {
            System.out.println("IGUAL PRIORIDAD");
        } 
        // Comprobamos en qué casos el servidor 1 tiene mayor prioridad que el servidor 2:
        //1.servidor1 está BLOQUEADO
        //2.servidor1 está en ALERTA y servidor2 está en NORMAL
        else if (estado1.equals("BLOQUEADO") || (estado1.equals("ALERTA") && estado2.equals("NORMAL"))) {
            System.out.println("ATENCION PRIORITARIA: " + servidor1.nombre.trim());
        } 
        // En cualquier otro caso restante, el servidor 2 tiene mayor prioridad
        else {
            System.out.println("ATENCION PRIORITARIA: " + servidor2.nombre.trim());
        }

        teclado.close();
    }
}

/*
 *1. ¿Por qué dos objetos de la misma clase pueden almacenar valores diferentes en sus atributos?
 *Porque la clase (Servidor) es únicamente una plantilla, al hacer new servidor nos llevamos una copia de esa plantilla a memoria, pero tambien tiene su propia zona de memoria independiente donde puede poner atributos los cuales no comparten.

 *2. ¿Qué diferencia existe entre "Servidor b = a;" y "Servidor b = new Servidor();"?
 *Con "Servidor b = new Servidor();" se crea un segundo servidor totalmente nuevo e independiente en memoria y se guarda en la variable "b".
 *Con "Servidor b = a;" NO se crea un objeto nuevo, sino que b se copia la referencia de a, por lo que ambos apuntan al mismo objeto en memoria

 *3. ¿Por qué es importante comprobar datosValidos() antes de calcular el porcentaje?
 *Si intentos totales es 0, la division dara error
 *Si se dan mal los datos, como 5 fallos y 4 intentos totales, el porcentaje de fallos no tendra entido.
 */