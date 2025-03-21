/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8quiz;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import com.mpatric.mp3agic.*;
import javazoom.jl.player.Player;

/**
 *
 * @author laraj
 */
public class Gui extends JFrame {
    private ListaMusica listaMusica = new ListaMusica();
    private Reproductor actual;
    private Player player;
    private Thread thread;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private long pauseLocation;
    private long songTotalLength;
    private String filePath;
    private boolean isPaused = false;
    private boolean isPlaying = false;

    private JLabel lblImagen, lblNombre, lblArtista, lblGenero, lblDuracion;
    private DefaultListModel<String> modeloLista = new DefaultListModel<>();
    private JList<String> listaCanciones = new JList<>(modeloLista);
    private JButton btnPlay, btnPause, btnStop, btnAdd, btnSelect, btnRemove;
    private JFileChooser chooser = new JFileChooser();
    private JButton btnReanudar;

    public Gui() {
        setTitle("Reproductor Musical");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        lblImagen = new JLabel("", JLabel.CENTER);
        lblImagen.setPreferredSize(new Dimension(300, 300));

        lblNombre = crearLabel("Nombre:");
        lblArtista = crearLabel("Artista:");
        lblGenero = crearLabel("Genero:");
        lblDuracion = crearLabel("Duracion:");

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBackground(Color.BLACK);
        infoPanel.add(lblNombre);
        infoPanel.add(lblArtista);
        infoPanel.add(lblGenero);
        infoPanel.add(lblDuracion);

        listaCanciones.setBackground(Color.BLACK);
        listaCanciones.setForeground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(listaCanciones);
        scroll.setPreferredSize(new Dimension(300, 400));
        add(scroll, BorderLayout.EAST);

        btnPlay = new JButton("Play");
        btnPause = new JButton("Pause");
        btnReanudar = new JButton("Reanudar");
        btnStop = new JButton("Stop");
        btnAdd = new JButton("Add");
        btnSelect = new JButton("Select");
        btnRemove = new JButton("Remove");

        JPanel controlPanel = new JPanel(new GridLayout(1, 6));
        controlPanel.add(btnPlay);
        controlPanel.add(btnPause);
        controlPanel.add(btnReanudar);
        controlPanel.add(btnStop);
        controlPanel.add(btnAdd);
        controlPanel.add(btnSelect);
        controlPanel.add(btnRemove);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.BLACK);
        centerPanel.add(lblImagen, BorderLayout.NORTH);
        centerPanel.add(infoPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> agregar());
        btnSelect.addActionListener(e -> seleccionar());
        btnPlay.addActionListener(e -> reproducir());
        btnPause.addActionListener(e -> pausar());
        btnReanudar.addActionListener(e -> reanudar());
        btnStop.addActionListener(e -> detener());
        btnRemove.addActionListener(e -> eliminar());
        
        setVisible(true);
    }

    private JLabel crearLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        return lbl;
    }

    private void agregar() {
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("MP3", "mp3"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                Mp3File mp3 = new Mp3File(file);
                String nombre = file.getName();
                String artista = "Desconocido";
                String genero = "Desconocido";
                String duracion = mp3.getLengthInSeconds() + " s";
                ImageIcon img = new ImageIcon("album_default.png");

                if (mp3.hasId3v2Tag()) {
                    ID3v2 tag = mp3.getId3v2Tag();
                    if (tag.getTitle() != null) nombre = tag.getTitle();
                    if (tag.getArtist() != null) artista = tag.getArtist();
                    if (tag.getGenreDescription() != null) genero = tag.getGenreDescription();
                    if (tag.getAlbumImage() != null) img = new ImageIcon(tag.getAlbumImage());
                }

                Reproductor nueva = new Reproductor(nombre, artista, duracion, genero, img, file.getAbsolutePath());
                listaMusica.agregar(nueva);
                modeloLista.addElement(nombre); 

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar MP3");
            }
        }
    }

    private void seleccionar() {
        int index = listaCanciones.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cancion de la lista porfi");
            return;
        }
        actual = listaMusica.obtener(index);
        if (actual != null) mostrarActual();
    }

    private void mostrarActual() {
        lblNombre.setText("Nombre: " + actual.getNombre());
        lblArtista.setText("Artista: " + actual.getArtista());
        lblGenero.setText("Genero: " + actual.getGeneroMusical());
        lblDuracion.setText("Duracion: " + actual.getDuracion());
        if (actual.getImagen() != null) {
            lblImagen.setIcon(new ImageIcon(actual.getImagen().getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH)));
        } else {
            lblImagen.setIcon(new ImageIcon("album_default.png"));
        }
    }

    private void reproducir() {
    if (actual == null) {
        JOptionPane.showMessageDialog(this, "Selecciona una cancion");
        return;
    }
    detener();
    try {
        fis = new FileInputStream(actual.getRutaArchivo());
        bis = new BufferedInputStream(fis);
        player = new Player(bis);
        songTotalLength = fis.available();
        filePath = actual.getRutaArchivo();

        thread = new Thread(() -> {
            try {
                isPlaying = true;
                isPaused = false;
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void detener() {
        if (player != null) player.close();
        if (thread != null) thread.interrupt();
    }

    private void pausar() {
    if (!isPlaying || isPaused) {
        JOptionPane.showMessageDialog(this, "No se puede pausar. La cancion ya esta pausada o no esta sonando");
        return;
    }
    if (player != null && fis != null) {
        try {
            pauseLocation = fis.available();
            player.close();
            isPaused = true;
            isPlaying = false;
            System.out.println("Pausado en: " + pauseLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

   private void reanudar() {
    if (!isPaused) {
        JOptionPane.showMessageDialog(this, "No hay cancion pausada para reanudar");
        return;
    }
    try {
        fis = new FileInputStream(filePath);
        bis = new BufferedInputStream(fis);
        player = new Player(bis);
        fis.skip(songTotalLength - pauseLocation);

        thread = new Thread(() -> {
            try {
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

        isPaused = false;
        isPlaying = true;

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void eliminar() {
        int index = listaCanciones.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una cancion de la lista");
            return;
        }
        if (listaMusica.eliminarPorIndice(index)) {
            detener();
            actual = null;
            modeloLista.remove(index); 
            JOptionPane.showMessageDialog(this, "Cancion eliminada");
        } else {
            JOptionPane.showMessageDialog(this, "Indice invalido");
        }
    }

    public static void main(String[] args) {
        new Gui();
    }
}
