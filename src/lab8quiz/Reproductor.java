/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lab8quiz;
import javax.swing.ImageIcon;
/**
 *
 * @author laraj
 */
public class Reproductor {
    private String nombre;
    private String artista;
    private String duracion;
    private String generoMusical;
    private ImageIcon imagen;
    private String rutaArchivo;
    private Reproductor siguiente;

    public Reproductor(String nombre, String artista, String duracion, String genero, ImageIcon imagen, String ruta) {
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
        this.generoMusical = genero;
        this.imagen = imagen;
        this.rutaArchivo = ruta;
        this.siguiente = null;
    }

    public String getNombre() { return nombre; }
    public String getArtista() { return artista; }
    public String getDuracion() { return duracion; }
    public String getGeneroMusical() { return generoMusical; }
    public ImageIcon getImagen() { return imagen; }
    public String getRutaArchivo() { return rutaArchivo; }
    public Reproductor getSiguiente() { return siguiente; }
    public void setSiguiente(Reproductor siguiente) { this.siguiente = siguiente; }
}
