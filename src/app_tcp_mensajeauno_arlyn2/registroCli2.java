package app_tcp_mensajeauno_arlyn2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class registroCli2 extends JFrame {

    private JTextField nombreTxt;
    private JButton bConectar;

    public registroCli2() {
        initComponents();
    }

    private void initComponents() {

        this.setTitle("Conexión al Servidor");
        this.setSize(400, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(null);

        // Configuración del ícono de la imagen
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("cliente.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setBounds(270, 40, scaledIcon.getIconWidth(), scaledIcon.getIconHeight());
        this.getContentPane().add(imageLabel);

        JLabel jLabelNombre = new JLabel("Ingrese su nombre:");
        jLabelNombre.setFont(new Font("Verdana", 0, 14));
        jLabelNombre.setBounds(40, 30, 200, 30);
        this.getContentPane().add(jLabelNombre);

        nombreTxt = new JTextField();
        nombreTxt.setFont(new Font("Verdana", 0, 14));
        nombreTxt.setBounds(40, 70, 200, 30);
        this.getContentPane().add(nombreTxt);

        bConectar = new JButton("CONECTAR CON SERVIDOR");
        bConectar.setFont(new Font("Segoe UI", 0, 14));
        bConectar.setBounds(40, 110, 200, 40);
        bConectar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                conectarActionPerformed(evt);
            }
        });
        this.getContentPane().add(bConectar);
    }

    private void conectarActionPerformed(ActionEvent evt) {
        String nombreUsuario = nombreTxt.getText().trim();
        if (nombreUsuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre.");
            return;
        }
        // Aquí puedes intentar conectar al servidor, si la conexión es exitosa:
        this.setVisible(false);
        new PrincipalCli2(nombreUsuario).setVisible(true);
        this.dispose(); // Cierra la ventana de conexión
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new registroCli2().setVisible(true);
            }
        });
    }
}
