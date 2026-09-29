package controlador;

import modelo.EmpleadoAdministrativo;
import modelo.EmpleadoBase;
import modelo.EmpleadoComercial;
import modelo.RepositorioEmpleados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class EmpleadoControlador {
    // RETO: un solo array a modificar para que el combo muestre "Comercial"
    public static final String[] TIPOS_EMPLEADO = {"Operativo", "Administrativo", "Comercial"};

    private final RepositorioEmpleados repositorio;
    private final ArrayList<String> historial;

    public EmpleadoControlador() {
        repositorio = new RepositorioEmpleados();
        historial = new ArrayList<>();
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        String[] cedulas = {"1001", "1002", "1003", "1004"};
        String[] nombres = {"Ana Torres", "Luis Gómez", "Marta Ríos", "Pedro Cano"};
        double[] salarios = {1800000, 2500000, 1750000, 3200000};

        for (int i = 0; i < cedulas.length; i++) {
            EmpleadoBase empleado;
            if (i % 2 == 0) {
                empleado = new EmpleadoBase(cedulas[i], nombres[i], salarios[i]);
            } else {
                empleado = new EmpleadoAdministrativo(cedulas[i], nombres[i], salarios[i], 300000);
            }
            repositorio.agregar(empleado);
        }
    }

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
                return false;
            }
        }
        return puntos <= 1;
    }

    private String validar(String cedula, String nombre, String salario,
                           String tipo, String extra) {
        if (cedula.isEmpty() || nombre.isEmpty()) {
            return "La cédula y el nombre son obligatorios.";
        }
        if (!esNumeroValido(salario)) {
            return "El salario debe ser un número positivo (sin puntos de miles).";
        }
        if (tipo.equals("Administrativo") && !esNumeroValido(extra)) {
            return "La bonificación debe ser un número positivo.";
        }
        if (tipo.equals("Comercial")) {
            if (!esNumeroValido(extra)) {
                return "La comisión debe ser un número positivo.";
            }
            if (Double.parseDouble(extra) > 50) {
                return "La comisión no puede ser mayor a 50%.";
            }
        }
        return null;
    }

    private EmpleadoBase construirEmpleado(String cedula, String nombre, String salario,
                                           String tipo, String extra) {
        double salarioBase = Double.parseDouble(salario);
        if (tipo.equals("Administrativo")) {
            return new EmpleadoAdministrativo(cedula, nombre, salarioBase, Double.parseDouble(extra));
        }
        if (tipo.equals("Comercial")) {
            return new EmpleadoComercial(cedula, nombre, salarioBase, Double.parseDouble(extra));
        }
        return new EmpleadoBase(cedula, nombre, salarioBase);
    }

    // ======================= OPERACIONES CRUD =======================
    public String agregarEmpleado(String cedula, String nombre, String salario,
                                  String tipo, String extra) {
        String error = validar(cedula, nombre, salario, tipo, extra);
        if (error != null) {
            return error;
        }
        EmpleadoBase nuevo = construirEmpleado(cedula, nombre, salario, tipo, extra);
        if (repositorio.agregar(nuevo)) {
            historial.add("AGREGADO: " + cedula + " - " + nombre);
            return "Empleado agregado correctamente.";
        }
        return "Ya existe un empleado con la cédula " + cedula + ".";
    }

    public EmpleadoBase buscarEmpleado(String cedula) {
        historial.add("BÚSQUEDA: " + cedula);
        return repositorio.buscar(cedula);
    }

    public String actualizarEmpleado(String cedula, String nombre, String salario,
                                     String tipo, String extra) {
        String error = validar(cedula, nombre, salario, tipo, extra);
        if (error != null) {
            return error;
        }
        EmpleadoBase actualizado = construirEmpleado(cedula, nombre, salario, tipo, extra);
        if (repositorio.actualizar(actualizado)) {
            historial.add("ACTUALIZADO: " + cedula + " - " + nombre);
            return "Empleado actualizado correctamente.";
        }
        return "No existe ningún empleado con la cédula " + cedula + ".";
    }

    public String eliminarEmpleado(String cedula) {
        if (repositorio.eliminar(cedula)) {
            historial.add("ELIMINADO: " + cedula);
            return "Empleado eliminado correctamente.";
        }
        return "No existe ningún empleado con la cédula " + cedula + ".";
    }

    // BONUS: la tabla muestra los empleados ordenados alfabéticamente por nombre
    public ArrayList<EmpleadoBase> obtenerEmpleados() {
        ArrayList<EmpleadoBase> lista = repositorio.listarTodos();
        lista.sort(Comparator.comparing(EmpleadoBase::getNombre, String.CASE_INSENSITIVE_ORDER));
        return lista;
    }

    // BONUS: cuenta cuántos empleados hay de cada tipo (clave = tipo, valor = cantidad)
    public HashMap<String, Integer> contarPorTipo() {
        HashMap<String, Integer> conteo = new HashMap<>();
        for (EmpleadoBase empleado : repositorio.listarTodos()) {
            String tipo = empleado.getTipo();
            conteo.put(tipo, conteo.getOrDefault(tipo, 0) + 1);
        }
        return conteo;
    }

    // Polimorfismo: no cambia con el nuevo tipo de empleado
    public double calcularTotalNomina() {
        double total = 0;
        for (EmpleadoBase empleado : repositorio.listarTodos()) {
            total += empleado.calcularSalarioTotal();
        }
        return total;
    }

    public ArrayList<String> obtenerHistorial() {
        return historial;
    }
}
