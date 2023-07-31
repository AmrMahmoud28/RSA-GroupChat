package Client;

import Client.Controller.*;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
//import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static Client.Controller.loggedInUser;
import static Client.Controller.users;

public class Room extends Thread implements Initializable {
    @FXML
    public Label clientName;
    @FXML
    public Button chatBtn;
    @FXML
    public Pane chat;
    @FXML
    public TextField msgField;
    @FXML
    public TextArea msgRoom;
    @FXML
    public Label online;
    @FXML
    public Label fullName;
    @FXML
    public Label publicKey;
    @FXML
    public Label privateKey;
    @FXML
    public Label gender;
    @FXML
    public Pane profile;
    @FXML
    public Button profileBtn;
    @FXML
    public TextField fileChoosePath;
    @FXML
    public ImageView proImage;
    @FXML
    public Circle showProPic;
    public TextField keyField;
    private FileChooser fileChooser;
    private File filePath;
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
                fulmsg.append("(Decrypted)");
                System.out.println("Decrypted Full msg: " + fulmsg);
                System.out.println(fulmsg);
                if (cmd.equalsIgnoreCase(Controller.fullName.split(" ")[0] + "{" + Controller.e + "," + Controller.n + "}:")) {
                    continue;
                } else if(fulmsg.toString().equalsIgnoreCase("bye")) {
                    break;
                }
                msgRoom.appendText(cmd + " " + fulmsg + "\n");
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
        if(!msg.isEmpty()){
            System.out.println("Message: " + msg);
            writer.println(Controller.fullName.split(" ")[0] + "{" + Controller.e + "," + Controller.n + "}: " + msg + " (Encrypted)");
            msgRoom.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            msgRoom.appendText("Me: " + msg + "\n");
            msgField.setText("");
            keyField.setText("");
            if(msg.equalsIgnoreCase("BYE") || (msg.equalsIgnoreCase("logout"))) {
                System.exit(0);
            }
        }
    }

    // Changing profile pic

    public boolean saveControl = false;

    public void chooseImageButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        this.filePath = fileChooser.showOpenDialog(stage);
        fileChoosePath.setText(filePath.getPath());
        saveControl = true;
    }

    public void sendMessageByKey(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            send();
        }
    }

    public void saveImage() {
        if (saveControl) {
            try {
                BufferedImage bufferedImage = ImageIO.read(filePath);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                proImage.setImage(image);
                showProPic.setFill(new ImagePattern(image));
                saveControl = false;
                fileChoosePath.setText("");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showProPic.setStroke(Color.valueOf("#90a4ae"));
        Image image;
        image = new Image("icons/user.png", false);
//        if(Controller.gender.equalsIgnoreCase("Male")) {
//            image = new Image("icons/user.png", false);
//        } else {
//            image = new Image("icons/female.png", false);
//            proImage.setImage(image);
//        }
        showProPic.setFill(new ImagePattern(image));
        clientName.setText(Controller.fullName.split(" ")[0]);
        connectSocket();
    }
}