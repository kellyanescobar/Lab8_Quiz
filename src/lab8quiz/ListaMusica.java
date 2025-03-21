/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8quiz;

/**
 *
 * @author laraj
 */
public class ListaMusica {
    private Reproductor cabeza;

    public void agregar(Reproductor nueva) {
        if (cabeza == null) {
            cabeza = nueva;
        } else {
            Reproductor actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nueva);
        }
    }

    public boolean eliminarPorIndice(int index) {
        if (cabeza == null) return false;
        if (index == 0) {
            cabeza = cabeza.getSiguiente();
            return true;
        }
        Reproductor actual = cabeza;
        int contador = 0;
        while (actual.getSiguiente() != null && contador < index - 1) {
            actual = actual.getSiguiente();
            contador++;
        }
        if (actual.getSiguiente() != null) {
            actual.setSiguiente(actual.getSiguiente().getSiguiente());
            return true;
        }
        return false;
    }

    public Reproductor obtener(int index) {
        Reproductor actual = cabeza;
        int contador = 0;
        while (actual != null) {
            if (contador == index) return actual;
            actual = actual.getSiguiente();
            contador++;
        }
        return null;
    }

    public Reproductor getCabeza() { return cabeza; }

    public int size() {
        int count = 0;
        Reproductor actual = cabeza;
        while (actual != null) {
            count++;
            actual = actual.getSiguiente();
        }
        return count;
    }
}
