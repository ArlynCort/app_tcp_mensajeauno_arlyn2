package app_tcp_mensajeauno_arlyn2;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class PrincipalSrv2 extends javax.swing.JFrame {
    private final int PORT = 12345;
    private ServerSocket serverSocket;
    private final Map<String, PrintWriter> clientsMap = new HashMap<>();
    private DefaultListModel<String> userListModel;
    private JList<String> userList;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JLabel userListTitle;
    private JTextArea mensajesTxt;
    private JButton bIniciar;

    public PrincipalSrv2() {
        initComponents();
    }

    private void initComponents() {
        this.setTitle("Servidor ...");
        this.setSize(510, 500);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(null);

        bIniciar = new javax.swing.JButton();
        JLabel jLabel1 = new javax.swing.JLabel();
        mensajesTxt = new JTextArea();
        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        progressBar = new JProgressBar();
        progressLabel = new JLabel();
        userListTitle = new JLabel();

        bIniciar.setFont(new java.awt.Font("Segoe UI", 0, 18));
        bIniciar.setText("INICIAR SERVIDOR");
        bIniciar.addActionListener(evt -> {
            bIniciarActionPerformed(evt);
            bIniciar.setEnabled(false);
        });
        getContentPane().add(bIniciar);
        bIniciar.setBounds(100, 150, 250, 40);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel1.setForeground(new java.awt.Color(204, 0, 0));
        jLabel1.setText("SERVIDOR TCP");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(170, 40, 160, 17);

        mensajesTxt.setColumns(25);
        mensajesTxt.setRows(5);
        jScrollPane1.setViewportView(mensajesTxt);
        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(50, 200, 350, 70);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBounds(50, 300, 350, 100);

        userListTitle.setText("Usuarios Conectados:");
        userListTitle.setFont(new java.awt.Font("Verdana", 1, 14));
        userListTitle.setBounds(50, 270, 200, 25);

        getContentPane().add(userListTitle);
        getContentPane().add(userScrollPane);

        // Barra de progreso
        progressBar.setBounds(50, 80, 300, 25);
        progressBar.setIndeterminate(true);
        progressLabel.setBounds(220, 120, 200, 25);
        progressLabel.setFont(new java.awt.Font("Verdana", 0, 14));

        getContentPane().add(progressBar);
        getContentPane().add(progressLabel);

        // Inicialmente oculta el progreso
        progressBar.setVisible(false);
        progressLabel.setVisible(false);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new PrincipalSrv2().setVisible(true));
    }

    private void bIniciarActionPerformed(java.awt.event.ActionEvent evt) {
        iniciarServidor();
    }

    private void iniciarServidor() {
        // Muestra la barra de progreso
        progressBar.setVisible(true);
        progressLabel.setVisible(true);

        new Thread(() -> {
            try {
                InetAddress addr = InetAddress.getLocalHost();
                serverSocket = new ServerSocket(PORT);
                mensajesTxt.append("Servidor TCP en ejecución: " + addr + " ,Puerto " + serverSocket.getLocalPort() + "\n");

                // Simulación de progreso 
                for (int i = 0; i <= 100; i += 10) {
                    final int progress = i;
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(progress);
                        progressLabel.setText("Conectando... " + progress + "%");
                    });
                    try {
                        Thread.sleep(200); // Espera para simular el progreso
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                progressLabel.setVisible(false);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    
                    // Recibir el nombre de usuario del cliente
                    String nombreUsuario = in.readLine();
                    synchronized (clientsMap) {
                        clientsMap.put(nombreUsuario, out);
                        userListModel.addElement(nombreUsuario);
                    }

               
                    broadcastMessage("USUARIO:" + nombreUsuario);

                    // Enviar lista de usuarios conectados al nuevo usuario
                    for (String usuario : clientsMap.keySet()) {
                        if (!usuario.equals(nombreUsuario)) {
                            out.println("USUARIO:" + usuario);
                        }
                    }

                    mensajesTxt.append(nombreUsuario + " se ha conectado.\n");

                    
                    new Thread(new ClientHandler(clientSocket, nombreUsuario, out, in)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void broadcastMessage(String message) {
        synchronized (clientsMap) {
            for (PrintWriter clientOut : clientsMap.values()) {
                clientOut.println(message);
            }
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final String nombreUsuario;
        private final PrintWriter out;
        private final BufferedReader in;

        public ClientHandler(Socket clientSocket, String nombreUsuario, PrintWriter out, BufferedReader in) {
            this.clientSocket = clientSocket;
            this.nombreUsuario = nombreUsuario;
            this.out = out;
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    // Enviar mensaje al destinatario específico
                    String[] parts = mensaje.split(":", 2);
                    if (parts.length == 2) {
                        String destinatario = parts[0];
                        String mensajeEnviar = nombreUsuario + ": " + parts[1];

                        synchronized (clientsMap) {
                            PrintWriter destinatarioOut = clientsMap.get(destinatario);
                            if (destinatarioOut != null) {
                                destinatarioOut.println(mensajeEnviar);
                            } else {
                                out.println("El usuario " + destinatario + " no está conectado.");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                synchronized (clientsMap) {
                    clientsMap.remove(nombreUsuario);
                    userListModel.removeElement(nombreUsuario);
                    broadcastMessage("USUARIODESCONECTADO:" + nombreUsuario);
                }

                mensajesTxt.append(nombreUsuario + " se ha desconectado.\n");
            }
        }
    }
}
