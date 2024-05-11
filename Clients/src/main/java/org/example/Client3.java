package org.example;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Client3 {
    public Client3() {
    }

    public static void main(String[] args) {
        final File[] fileToSend = new File[1];
        JFrame jFrame = new JFrame(" Client");
        jFrame.setSize(450, 450);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), 1));
        jFrame.setDefaultCloseOperation(3);

        //create the components
        JLabel jlTitle = new JLabel("File Sender");
        jlTitle.setFont(new Font("Arial", 1, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(0.5F);

        //set file name
        final JLabel jlFileName = new JLabel("Choose a file to send.");
        jlFileName.setFont(new Font("Arial", 1, 20));
        jlFileName.setBorder(new EmptyBorder(50, 0, 0, 0));
        jlFileName.setAlignmentX(0.5F);

        //set button
        JPanel jpButton = new JPanel();
        jpButton.setBorder(new EmptyBorder(75, 0, 10, 0));

        //create send button
        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setPreferredSize(new Dimension(150, 75));
        jbSendFile.setFont(new Font("Arial", 1, 20));

        //create choose file button
        JButton jbChooseFile = new JButton("Choose File");
        jbChooseFile.setPreferredSize(new Dimension(150, 75));
        jbChooseFile.setFont(new Font("Arial", 1, 20));

        //add components
        jpButton.add(jbSendFile);
        jpButton.add(jbChooseFile);

        //add action listener to choose file button
        jbChooseFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose a file to send.");
                if (jFileChooser.showOpenDialog((Component)null) == 0) {
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFileName.setText("The file you want to send is: " + fileToSend[0].getName());
                }

            }
        });

        //add action listener to send file button
        jbSendFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fileToSend[0] == null) {
                    // if file is not selected
                    jlFileName.setText("Please choose a file to send first!");
                } else {
                    try {
                        //set fileInputStream
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());

                        //set host and port
                        Socket socket = new Socket("localhost", 1234);

                        //set dataOutputStream
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        //set file name
                        String fileName = fileToSend[0].getName();
                        byte[] fileNameBytes = fileName.getBytes();// convert file name to byte array
                        byte[] fileBytes = new byte[(int)fileToSend[0].length()];// convert file to byte array

                        fileInputStream.read(fileBytes);// read file
                        dataOutputStream.writeInt(fileNameBytes.length);// write file name length
                        dataOutputStream.write(fileNameBytes);// write file name
                        dataOutputStream.writeInt(fileBytes.length);// write fileBytes length

                        dataOutputStream.write(fileBytes);
                    } catch (IOException var8) {
                        var8.printStackTrace();
                    }
                }

            }
        });

        // add components
        jFrame.add(jlTitle);
        jFrame.add(jlFileName);
        jFrame.add(jpButton);
        jFrame.setVisible(true);
    }
}
