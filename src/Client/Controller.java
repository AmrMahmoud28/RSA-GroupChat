package Client;

import Server.ClientHandler;
import Server.Server;
import animatefx.animation.FadeIn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;


public class Controller {
    @FXML
    public Pane pnSignIn;
    @FXML
    public Pane pnSignUp;
    @FXML
    public Button btnSignUp;
    @FXML
    public Button getStarted;
    @FXML
    public ImageView btnBack;
//    @FXML
//    public TextField regName;
//    @FXML
//    public TextField regPass;
//    @FXML
//    public TextField regEmail;
    @FXML
    public TextField regFirstName;
//    @FXML
//    public TextField regPhoneNo;
//    @FXML
//    public RadioButton male;
//    @FXML
//    public RadioButton female;
    @FXML
    public Label controlRegLabel;
    @FXML
    public Label success;
    @FXML
    public Label goBack;
//    @FXML
//    public TextField userName;
    @FXML
    public TextField privateKey;
    @FXML
    public Label loginNotifier;
//    @FXML
//    public Label nameExists;
//    @FXML
//    public Label checkEmail;
    public static String n, d, e, fullName;
    public static ArrayList<User> loggedInUser = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<User>();
    User testUser = new User();
    User test1User = new User();

    public void registration() {
        if (!regFirstName.getText().equalsIgnoreCase("")) {
            User newUser = new User();
            newUser.fullName = regFirstName.getText();

            users.add(newUser);
            goBack.setOpacity(1);
            success.setOpacity(1);
            makeDefault();
            if (controlRegLabel.getOpacity() == 1) {
                controlRegLabel.setOpacity(0);
            }
        } else {
            controlRegLabel.setOpacity(1);
            setOpacity(success, goBack);
        }
    }

    private void setOpacity(Label a, Label b) {
        if(a.getOpacity() == 1 || b.getOpacity() == 1) {
            a.setOpacity(0);
            b.setOpacity(0);
        }
    }


    private void setOpacity(Label controlRegLabel) {
        controlRegLabel.setOpacity(0);
    }

    private void makeDefault() {
        regFirstName.setText("");
        setOpacity(controlRegLabel);
    }


    public void login() {
        testUser.fullName = "Amr Mahmoud";
        testUser.n = 77;
        testUser.privateKey = 43;
        testUser.publicKey = 7;
        users.add(testUser);

        test1User.fullName = "Ahmed Bass";
        test1User.n = 33;
        test1User.privateKey = 7;
        test1User.publicKey = 3;
        users.add(test1User);

        d = privateKey.getText().replaceAll(" ", "").split(",")[0];
        n = privateKey.getText().replaceAll(" ", "").split(",")[1];
        boolean login = false;
        for (User x : users) {
            if (x.n == Integer.parseInt(n) && x.privateKey == Integer.parseInt(d)) {
                fullName = x.fullName;
                e = "" + x.publicKey;
                login = true;
                loggedInUser.add(x);
                System.out.println(x.fullName);
                break;
            }
        }
        if (login) {
            changeWindow();
        } else {
            loginNotifier.setOpacity(1);
        }
    }

    public void changeWindow() {
        try {
            Stage stage = (Stage) regFirstName.getScene().getWindow();
            Parent root = FXMLLoader.load(this.getClass().getResource("Room.fxml"));
            stage.setScene(new Scene(root, 330, 560));
            stage.setTitle(fullName.split(" ")[0]);
            stage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnSignUp)) {
            new FadeIn(pnSignUp).play();
            pnSignUp.toFront();
        }
        if (event.getSource().equals(getStarted)) {
            new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        loginNotifier.setOpacity(0);
        privateKey.setText("");
    }

    @FXML
    private void handleMouseEvent(MouseEvent event) {
        if (event.getSource() == btnBack) {
            new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        regFirstName.setText("");
    }
}