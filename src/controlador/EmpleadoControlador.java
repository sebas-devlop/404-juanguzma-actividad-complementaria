package controlador;
import modelo.EmpleadoAdministrativo;
import modelo.EmpleadoBase;
import modelo.RepositorioEmpleados;
import java.util.ArrayList;
/**
 * El "cerebro" del sistema: recibe lo que el usuario escribe en la ventana,
 * lo valida y decide qué hacer con los datos.
 */
public class EmpleadoControlador {
    // Array: lista FIJA de tipos de empleado (no cambia mientras corre el programa)
    public static final String[] TIPOS_EMPLEADO = {"Operativo", "Administrativo"};
    private final RepositorioEmpleados repositorio;
    private final ArrayList<String> historial; // ArrayList: crece con cada operación
    public EmpleadoControlador() {
        repositorio = new RepositorioEmpleados();
        historial = new ArrayList<>();
        cargarDatosDePrueba();
    }
    // Carga 4 empleados de ejemplo usando arrays paralelos y un ciclo for
    private void cargarDatosDePrueba() {
        String[] cedulas = {"1001", "1002", "1003", "1004"};
        String[] nombres = {"Ana Torres", "Luis Gómez", "Marta Ríos", "Pedro Cano"};
        double[] salarios = {1800000, 2500000, 1750000, 3200000};


        for (int i = 0; i < cedulas.length; i++) {
            EmpleadoBase empleado;
            if (i % 2 == 0) {
                empleado = new EmpleadoBase(cedulas[i], nombres[i], salarios[i]);
            } else {
                empleado = new EmpleadoAdministrativo(cedulas[i], nombres[i],
                        salarios[i], 300000);

            }
            repositorio.agregar(empleado);
        }
    }
    // Revisa carácter por carácter que el texto sea un número positivo válido
    private boolean esNumeroValido(String texto) {
        if (texto.isEmpty() || texto.equals(".")) {
            return false;
        }
        int puntos = 0;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '.') {
                puntos++;
            } else if (!Character.isDigit(c)) {
                return false; // letra, signo, espacio... no es válido
            }
        }
        return puntos <= 1; // máximo un punto decimal
    }
    // Devuelve un mensaje de error, o null si todo está correcto
    private String validar(String cedula, String nombre, String salario,
                           String tipo, String bonificacion) {

        if (cedula.isEmpty() || nombre.isEmpty()) {
            return "La cédula y el nombre son obligatorios.";
        }
        if (!esNumeroValido(salario)) {
            return "El salario debe ser un número positivo (sin puntos de miles).";
        }
        if (tipo.equals("Administrativo") && !esNumeroValido(bonificacion)) {
            return "La bonificación debe ser un número positivo.";
        }
        return null;
    }
    // Fábrica de empleados: decide qué clase instanciar según el tipo
    private EmpleadoBase construirEmpleado(String cedula, String nombre, String salario,
                                           String tipo, String bonificacion) {

        double salarioBase = Double.parseDouble(salario);
        if (tipo.equals("Administrativo")) {
            double bono = Double.parseDouble(bonificacion);
            return new EmpleadoAdministrativo(cedula, nombre, salarioBase, bono);
        }
        return new EmpleadoBase(cedula, nombre, salarioBase);
    }
}
