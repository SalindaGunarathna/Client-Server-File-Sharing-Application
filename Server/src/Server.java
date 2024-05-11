import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


// this is the main server class
public class Server {

    // Array list to hold information about the files received.
    static ArrayList<MyFile> myFiles = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        // Create a new frame.
        JFrame jFrame = new JFrame("Server");// Main container, set the name.
        jFrame.setSize(400, 400);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// When closing the frame also close the program.

        // Panel that will hold the title label and the other jpanels.
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        // Make it scrollable.
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Title above panel.
        JLabel jlTitle = new JLabel(" File Receiver");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20,0,10,0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add everything to the main GUI.
        jFrame.add(jlTitle);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);

        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(1234);

        // This while loop will run forever so the server will never stop unless the application is closed.
        while (true) {
            try {
                // Wait for a client to connect and when they do create a socket to communicate with them.
                Socket socket = serverSocket.accept();

                // Create a new thread to handle the client connection.
                Thread clientThread = new Thread(new ClientHandler(socket, jPanel, jFrame));
                clientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFileExtension(String fileName) {
        // Get the file type by using the last occurence of . (for example aboutMe.txt returns txt).
        // Will have issues with files like myFile.tar.gz.
        int i = fileName.lastIndexOf('.');
        // If there is an extension.
        if (i > 0) {
            // Set the extension to the extension of the filename.
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
        }
    }
}
