/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8quiz;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;


/**
 *
 * @author laraj
 */

import com.mpatric.mp3agic.*;
import javazoom.jl.player.Player;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Gui extends JFrame {
    private ListaMusica lista = new ListaMusica();
    private Reproductor actual;
    private Player player;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private int reproduciendoIndex = -1;

    private JLabel lblInfo, lblImagen;
    private JTextArea txtLista;
    private JButton btnPlay, btnStop, btnAdd, btnSelect, btnRemove;

    public Gui() {
        setTitle("Reproductor MP3");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        lblInfo = new JLabel("Información de la canción", JLabel.CENTER);
        lblImagen = new JLabel("Imagen", JLabel.CENTER);
        txtLista = new JTextArea(10, 30);
        txtLista.setEditable(false);

        JPanel panelBotones = new JPanel(new GridLayout(1, 5));
        btnPlay = new JButton("Play");
        btnStop = new JButton("Stop");
        btnAdd = new JButton("Add");
        btnSelect = new JButton("Select");
        btnRemove = new JButton("Remove");
        panelBotones.add(btnPlay);
        panelBotones.add(btnStop);
        panelBotones.add(btnAdd);
        panelBotones.add(btnSelect);
        panelBotones.add(btnRemove);

        add(lblInfo, BorderLayout.NORTH);
        add(lblImagen, BorderLayout.CENTER);
        add(new JScrollPane(txtLista), BorderLayout.EAST);
        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnAdd.addActionListener(e -> agregarCancion());
        btnSelect.addActionListener(e -> seleccionarCancion());
        btnPlay.addActionListener(e -> reproducir());
        btnStop.addActionListener(e -> detener());
        btnRemove.addActionListener(e -> eliminarCancion());

        setVisible(true);
    }

    private void agregarCancion() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("MP3", "mp3"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = fc.getSelectedFile();
                Mp3File mp3 = new Mp3File(archivo);
                String nombre = archivo.getName();
                String artista = "Desconocido";
                String duracion = "00:00";
                String genero = "Desconocido";
                ImageIcon imagen = null;

                if (mp3.hasId3v2Tag()) {
                    ID3v2 tag = mp3.getId3v2Tag();
                    nombre = tag.getTitle() != null ? tag.getTitle() : nombre;
                    artista = tag.getArtist() != null ? tag.getArtist() : "Desconocido";
                    genero = tag.getGenreDescription() != null ? tag.getGenreDescription() : "Desconocido";
                    duracion = convertirDuracion(mp3.getLengthInSeconds());
                    byte[] imgData = tag.getAlbumImage();
                    if (imgData != null) imagen = new ImageIcon(imgData);
                }

                Reproductor r = new Reproductor(nombre, artista, duracion, genero, imagen, archivo.getPath());
                lista.agregar(r);
                actualizarLista();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar la canción.");
            }
        }
    }

    private String convertirDuracion(long seg) {
        long min = seg / 60;
        long s = seg % 60;
        return String.format("%02d:%02d", min, s);
    }

    private void actualizarLista() {
        txtLista.setText("");
        Reproductor r = lista.getCabeza();
        int i = 0;
        while (r != null) {
            txtLista.append(i + ": " + r.getNombre() + " - " + r.getArtista() + "\n");
            r = r.getSiguiente();
            i++;
        }
    }

    private void seleccionarCancion() {
        String input = JOptionPane.showInputDialog(this, "Escribe el índice de la canción:");
        try {
            int idx = Integer.parseInt(input);
            Reproductor r = lista.obtener(idx);
            if (r != null) {
                actual = r;
                reproduciendoIndex = idx;
                mostrarInfo();
            } else {
                JOptionPane.showMessageDialog(this, "Índice inválido");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número inválido");
        }
    }

    private void mostrarInfo() {
        lblInfo.setText("<html>Nombre: " + actual.getNombre() + "<br>Artista: " + actual.getArtista() + "<br>Duración: " + actual.getDuracion() + "<br>Género: " + actual.getGeneroMusical() + "</html>");
        if (actual.getImagen() != null) {
            Image img = actual.getImagen().getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(img));
        } else {
            lblImagen.setIcon(null);
            lblImagen.setText("Sin imagen");
        }
    }

    private void reproducir() {
        try {
            if (actual != null) {
                detener(); // Detener antes de reproducir nueva
                fis = new FileInputStream(actual.getRutaArchivo());
                bis = new BufferedInputStream(fis);
                player = new Player(bis);
                new Thread(() -> {
                    try {
                        player.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una canción");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void detener() {
        try {
            if (player != null) player.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarCancion() {
        String input = JOptionPane.showInputDialog(this, "Escribe el índice a eliminar:");
        try {
            int idx = Integer.parseInt(input);
            if (idx == reproduciendoIndex) {
                detener();
                actual = null;
                reproduciendoIndex = -1;
            }
            if (lista.eliminarPorIndice(idx)) {
                JOptionPane.showMessageDialog(this, "Canción eliminada");
                actualizarLista();
            } else {
                JOptionPane.showMessageDialog(this, "Índice inválido");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número inválido");
        }
    }

    public static void main(String[] args) {
        new Gui();
    }
}
