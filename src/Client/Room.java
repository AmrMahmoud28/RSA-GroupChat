package Client;

import animatefx.animation.FadeIn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static Client.Controller.users;

public class Room extends Thread implements Initializable {
    @FXML
    public Label clientName;
    @FXML
    public Pane chat;
    @FXML
    public TextField msgField;
    @FXML
    public TextArea msgRoom;
    @FXML
    public Label fullName;
    @FXML
    public Label publicKey;
    @FXML
    public Label privateKey;
    @FXML
    public Pane profile;
    @FXML
    public Button profileBtn;
    @FXML
    public ImageView proImage;
    @FXML
    public Circle showProPic;
    public TextField keyField;
    public Label clientKey;
    public boolean toggleChat = false, toggleProfile = false;

    BufferedReader reader;
    PrintWriter writer;
    Socket socket;



    public void connectSocket() {
        try {
            socket = new Socket("localhost", 8889);
            System.out.println("Socket is connected with server!");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = reader.readLine();
                System.out.println("msg: " + msg);
                String[] tokens = msg.split(" ");
                String cmd = tokens[0];
                System.out.println("cmd: " + cmd);
                System.out.println(cmd);

                StringBuilder fulmsg = new StringBuilder();
                for(int i = 1; i < tokens.length; i++) {
                    fulmsg.append(tokens[i]).append(" ");
                }
                fulmsg.trimToSize();
                System.out.println("Full msg: " + fulmsg);
                String plaintext;
                if(cmd.contains("_ToEveryone:")){
                    plaintext = String.valueOf(fulmsg);
                }
                else{
                    plaintext = decrypt(String.valueOf(fulmsg), Integer.parseInt(Controller.d), Integer.parseInt(Controller.n));
                }
                System.out.println("Decrypted Full msg: " + plaintext);
                System.out.println(fulmsg);

                if (cmd.equalsIgnoreCase(Controller.fullName.split(" ")[0] + "{" + Controller.e + "," + Controller.n + "}:") || cmd.equalsIgnoreCase(Controller.fullName.split(" ")[0] + "{" + Controller.e + "," + Controller.n + "}_ToEveryone:")) {
                    continue;
                } else if(fulmsg.toString().equalsIgnoreCase("bye")) {
                    break;
                }
                msgRoom.appendText(cmd + " " + plaintext + "\n");
            }
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleProfileBtn(ActionEvent event) {
        if (event.getSource().equals(profileBtn) && !toggleProfile) {
            new FadeIn(profile).play();
            profile.toFront();
            chat.toBack();
            toggleProfile = true;
            toggleChat = false;
            profileBtn.setText("Back");
            setProfile();
        } else if (event.getSource().equals(profileBtn) && toggleProfile) {
            new FadeIn(chat).play();
            chat.toFront();
            toggleProfile = false;
            toggleChat = false;
            profileBtn.setText("Profile");
        }
    }

    public void setProfile() {
        for (User user : users) {
            if (Integer.parseInt(Controller.n) == user.n && Integer.parseInt(Controller.d) == user.privateKey) {
                fullName.setText(user.fullName);
                fullName.setOpacity(1);
                publicKey.setText("{" + user.publicKey + ", " + user.n + "}");
                publicKey.setOpacity(1);
                privateKey.setText("{" + user.privateKey + ", " + user.n + "}");
                privateKey.setOpacity(1);
            }
        }
    }

    public void handleSendEvent(MouseEvent event) {
        send();
        for(User user : users) {
            System.out.println(user.fullName);
        }
    }


    public void send() {
        String msg = msgField.getText();
        String to = keyField.getText();

        if(!msg.isEmpty()){
            System.out.println("Message: " + msg);
            if (!to.isEmpty()){
                int recE = Integer.parseInt(to.replaceAll(" ", "").split(",")[0]);
                int recN = Integer.parseInt(to.replaceAll(" ", "").split(",")[1]);

                int[] plaintextIndexs = fromCharToInt(msg);
                String ciphertext = encrypt(plaintextIndexs, recE, recN);
                writer.println(Controller.fullName.split(" ")[0] + "{" + Controller.e + "," + Controller.n + "}: " + ciphertext);
                msgRoom.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                msgRoom.appendText("Me_To{" + recE + "," + recN + "}: " + msg + "\n");
            }
            else{
                writer.println(Controller.fullName.split(" ")[0] + "{" + Controller.e + "," + Controller.n + "}_ToEveryone: " + msg);
                msgRoom.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                msgRoom.appendText("Me_ToEveryone: " + msg + "\n");
            }


            msgField.setText("");
            keyField.setText("");
            if(msg.equalsIgnoreCase("BYE") || (msg.equalsIgnoreCase("logout"))) {
                System.exit(0);
            }
        }
    }

    private char[] getCharacters() {
        return new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', ' ', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    }

    private int[] fromCharToInt(String m) {
        char[] characters = getCharacters();
        int[] indexes = new int[m.length()];

        for (int i = 0; i < m.length(); i++) {
            for (int j = 0; j < characters.length; j++) {
                if (m.charAt(i) == characters[j]) {
                    indexes[i] = j;
                    break;
                }
            }
        }

        return indexes;
    }

    private String fromIntToChar(int[] plaintextIndexs) {
        char[] characters = getCharacters();
        StringBuilder plaintext = new StringBuilder();
        boolean isFound = false;

        for (int i = 0; i < plaintextIndexs.length; i++) {
            isFound = false;
            for (int j = 0; j < characters.length; j++) {
                if (plaintextIndexs[i] == j) {
                    plaintext.append(characters[j]);
                    isFound = true;
                    break;
                }
            }
            if (!isFound){
                plaintext.append(plaintextIndexs[i]);
            }
        }

        return plaintext.toString();
    }

    private String encrypt(int[] plaintextIndexs, int key, int n) {
        StringBuilder ciphertext = new StringBuilder();
        BigInteger m, k, mod, result;

        for (int i = 0; i < plaintextIndexs.length; i++) {
            m = BigInteger.valueOf(plaintextIndexs[i]);
            k = BigInteger.valueOf(key);
            mod = BigInteger.valueOf(n);
            result = m.modPow(k, mod);

            ciphertext.append(result.intValue()).append(" ");
        }

        return ciphertext.toString();
    }

    private String decrypt(String ciphertext, int key, int n) {
        String[] ciphertextArray = ciphertext.split(" ");
        int[] plaintextIndexes = new int[ciphertextArray.length];
        String plaintext;
        BigInteger m, k, mod, result;

        for (int i = 0; i < ciphertextArray.length; i++) {
            m = BigInteger.valueOf(Integer.parseInt(ciphertextArray[i]));
            k = BigInteger.valueOf(key);
            mod = BigInteger.valueOf(n);
            result = m.modPow(k, mod);

            plaintextIndexes[i] = result.intValue();
        }

        plaintext = fromIntToChar(plaintextIndexes);

        return plaintext;
    }

    public void sendMessageByKey(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            send();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showProPic.setStroke(Color.valueOf("#90a4ae"));
        Image image;
        image = new Image("icons/user.png", false);
        showProPic.setFill(new ImagePattern(image));
        clientName.setText(Controller.fullName.split(" ")[0]);
        clientKey.setText("{" + Controller.e + ", " + Controller.n + "}");
        connectSocket();
    }
}