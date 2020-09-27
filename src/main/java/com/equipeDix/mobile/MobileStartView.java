package com.equipeDix.mobile;

import com.equipeDix.database.AccntMember;
import com.equipeDix.database.AccntPro;
import com.equipeDix.database.Account;
import com.equipeDix.logic.Logic;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class MobileStartView {

    private final TextField email = new TextField();
    private final TextField password = new TextField();
    private final ImageView startTop = new ImageView();
    private final Label passwordWrapper = new Label();
    private final Button submit;

    public MobileStartView(Logic logic){

        email.setMaxSize(320, 35);
        email.setPrefSize(320,35);
        email.setAlignment(Pos.CENTER);
        email.setPromptText("Adresse courriel");

        password.setMaxSize(320,35);
        password.setPrefSize(320,35);
        password.setAlignment(Pos.CENTER);
        password.setPromptText("Mot de passe");
        password.setDisable(true);

        passwordWrapper.setPrefSize(320,35);
        Tooltip pwInfo = new Tooltip("Non implémenté dans la version prototype");
        pwInfo.setShowDelay(Duration.seconds(0));
        passwordWrapper.setTooltip(pwInfo);

        submit = new Button("Se connecter");
        submit.setPrefSize(150,60);
        submit.setOnAction(event -> {
            if(email.getText().isBlank() || email.getText().isEmpty()){ // || regles à suivre (@, ., etc)
                emailWrong();
            } else {
                try {
                    Account account = logic.getAccountFromEmail(email.getText());
                    if (account.getId().startsWith("9")) {
                        MobileViewController.proView((AccntPro) account);
                    } else {

                        MobileViewController.memberView((AccntMember) account);
                    }
                } catch (Exception e){
                    emailWrong();
                }
            }
        });

        URL imagePath = this.getClass().getResource("/gymMobile.png");
        Image startGym = null;
        startGym = new Image(imagePath.toString());
        startTop.setImage(startGym);
        applyView();

        // Partie console
        consoleItou();

    }

    private void emailWrong(){
        System.out.println("Veuillez entrer un e-mail valide");
        email.setPromptText("Veuillez entrer un e-mail valide");
        email.setStyle("-fx-border-color : red;" +
                "-fx-border-width : 2px;" +
                "-fx-prompt-text-fill : red;");
        email.setText("");
        consoleItou();
    }

    public void applyView(){
        MobileApp.root.getChildren().clear();
        MobileApp.root.add(startTop, 0,0);
        MobileApp.root.add(email, 0,1);
        MobileApp.root.add(password, 0,2);
        MobileApp.root.add(passwordWrapper, 0,2);
        MobileApp.root.add(submit, 0,3);
    }

    public void consoleItou(){
        Runnable console = () -> runConsole();

        Thread backgroundThread = new Thread(console);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public void runConsole(){
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Entrez le email d'un membre ou un d'un professionnel :");
            String emailConsole = scanner.nextLine();
            Thread.sleep(500);

            Platform.runLater(() -> {
                email.setText(emailConsole);
                submit.fire();
            });



        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
