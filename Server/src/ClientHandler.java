import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private JPanel jPanel;
    private JFrame jFrame;

    int fileId =0;

    public ClientHandler(Socket socket, JPanel jPanel, JFrame jFrame) {
        this.socket = socket;
        this.jPanel = jPanel;
        this.jFrame = jFrame;
    }

    @Override
    public void run() {
        try {
            // Stream to receive data from the client through the socket.
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            // Read the size of the file name so know when to stop reading.
            int fileNameLength = dataInputStream.readInt();

            if (fileNameLength > 0) {
                // Byte array to hold name of file.
                byte[] fileNameBytes = new byte[fileNameLength];

                // Read from the input stream into the byte array.
                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);

                // Create the file name from the byte array.
                String fileName = new String(fileNameBytes);


                // Read how much data to expect for the actual content of the file.
                int fileContentLength = dataInputStream.readInt();

                // If the file exists.
                if (fileContentLength > 0) {
                    // Array to hold the file data.
                    byte[] fileContentBytes = new byte[fileContentLength];

                    // Read from the input stream into the fileContentBytes array.
                    dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);

                    // Add the file to the list of received files.
                    synchronized (Server.myFiles) {
                        Server.myFiles.add(new MyFile(fileId, fileName, fileContentBytes, Server.getFileExtension(fileName)));
                        fileId++;
                    }

                    // Panel to hold the picture and file name.
                    JPanel jpFileRow = new JPanel();
                    jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.X_AXIS));

                    // Set the file name.
                    JLabel jlFileName = new JLabel(fileName);
                    jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                    jlFileName.setBorder(new EmptyBorder(10,0, 10,0));


                    if (Server.getFileExtension(fileName).equalsIgnoreCase("txt")) {


                        jpFileRow.setName(String.valueOf(Server.myFiles.size() - 1));
                        jpFileRow.addMouseListener(getMyMouseListener());


                        jpFileRow.add(jlFileName);
                        jPanel.add(jpFileRow);
                        jFrame.validate();
                    } else {
                        // Create a new file.
                        // Set the name to be the fileId so you can get the correct file from the panel.
                        jpFileRow.setName(String.valueOf(Server.myFiles.size() - 1));

                        // Add a mouse listener so when it is clicked the popup appears.
                        jpFileRow.addMouseListener(getMyMouseListener());

                        // Add the file name and pic type to the panel and then add panel to parent panel.
                        jpFileRow.add(jlFileName);
                        jPanel.add(jpFileRow);

                        jFrame.validate();
                    }
                }
            }

            // Close the socket after receiving the file.
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private MouseListener getMyMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                // Get the source of the click which is the JPanel.
                JPanel jPanel = (JPanel) e.getSource();

                int fileId = Integer.parseInt(jPanel.getName());  // Get the ID of the file


                // Loop through the file storage and see which file is the selected one.
                for (MyFile myFile : Server.myFiles) {
                    if (myFile.getId() == fileId) {
                        JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        };
    }

    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {

        // Frame to hold everything.
        JFrame jFrame = new JFrame(" File Downloader");
        jFrame.setSize(400, 400);

        // Panel to hold everything.
        JPanel jPanel = new JPanel();

        // Make the layout a box layout with child elements stacked on top of each other.
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        // Title above panel.
        JLabel jlTitle = new JLabel("WittCode's File Downloader");
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label title horizontally.
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20,0,10,0));

        // Label to prompt the user if they are sure they want to download the file.
        JLabel jlPrompt = new JLabel("Are you sure you want to download " + fileName + "?");
        jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
        jlPrompt.setBorder(new EmptyBorder(20,0,10,0));
        jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create the yes for accepting the download.
        JButton jbYes = new JButton("Yes");
        jbYes.setPreferredSize(new Dimension(150, 75));
        jbYes.setFont(new Font("Arial", Font.BOLD, 20));

        // No button for rejecting the download.
        JButton jbNo = new JButton("No");
        jbNo.setPreferredSize(new Dimension(150, 75));
        jbNo.setFont(new Font("Arial", Font.BOLD, 20));

        // Label to hold the content of the file whether it be text of images.
        JLabel jlFileContent = new JLabel();
        jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel to hold the yes and no buttons and make the next to each other left and right.
        JPanel jpButtons = new JPanel();
        jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));

        // Add the yes and no buttons.
        jpButtons.add(jbYes);
        jpButtons.add(jbNo);

        // If the file is a text file then display the text.
        if (fileExtension.equalsIgnoreCase("txt")) {
            // Wrap it with <html> so that new lines are made.
            jlFileContent.setText("<html>" + new String(fileData) + "</html>");

        } else {// If the file is not a text file then make it an image.
            jlFileContent.setIcon(new ImageIcon(fileData));
        }

        // Yes so download file.
        jbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create the file with its name.
                File fileToDownload = new File(fileName);
                try {
                    // Create a stream to write data to the file.
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);

                    // Write the actual file data to the file.
                    fileOutputStream.write(fileData);

                    // Close the stream.
                    fileOutputStream.close();
                    // Get rid of the jFrame. after the user clicked yes.
                    jFrame.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        // No so close window.
        jbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // User clicked no so don't download the file but close the jframe.
                jFrame.dispose();
            }
        });

        // Add everything to the panel before adding to the frame.
        jPanel.add(jlTitle);
        jPanel.add(jlPrompt);
        jPanel.add(jlFileContent);
        jPanel.add(jpButtons);

        // Add panel to the frame.
        jFrame.add(jPanel);

        // Return the jFrame
        return jFrame;

    }
}
