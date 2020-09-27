package com.equipeDix;

// todo : Choses à réfléchir pour les prochains programmes développés :
//  - Utiliser plus de référence que de données brutes : par exemple, si un pro change / corrige son nom,
//    ça ne se change pas automatiquement dans les champs des séances et des services. Il en va de même avec
//    presque toutes (voire toutes) nos données.
//  -

import com.equipeDix.mobile.MobileApp;
import com.equipeDix.views.*;
import com.rnb.PaymentModule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CentreDeDonnees extends Application {

    public static final int WIDTH = 1024, HEIGHT = 800;
    private static Stage primaryStage;
    private static final VBox root = new VBox();
    public static final VBox menuBar = new VBox();
    public static final VBox topView = new VBox();
    public static final VBox bottomView = new VBox();

    private static boolean gotPaid;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        CentreDeDonnees.primaryStage = primaryStage;

        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/gym.png")));
        primaryStage.setTitle("Centre de Données #GYM");
        primaryStage.setResizable(false);

        root.setPrefSize(WIDTH, HEIGHT);

        menuBar.setPrefHeight(80);
        menuBar.setMinHeight(80);
        menuBar.setMaxHeight(100);

        topView.setPadding(new Insets(10, 0, 0, 0));
        topView.setPrefHeight(275);
        topView.setMinHeight(250);
        topView.setMaxHeight(275);

        bottomView.setPrefHeight(425);
        bottomView.setMinHeight(350);
        bottomView.setMaxHeight(425);

        Controller.startView();
        root.getChildren().addAll(menuBar, topView, bottomView);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.show();
        testMobileApp();

        primaryStage.setOnCloseRequest(e -> byebye());

    }

    public static void testMobileApp(){
        MobileApp mobileApp = new MobileApp();
        Stage mobile = new Stage();
        try {
            mobileApp.start(mobile);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static boolean payment(){
        PaymentModule pay = new PaymentModule();
        Stage paymentStage = new Stage();
        try{
            pay.start(paymentStage);
        } catch (Exception e){
            e.printStackTrace();
        }
        return (getGotPaid());
    }

    public static void setGotPaid(boolean value){
        gotPaid = value;
    }

    public static boolean getGotPaid(){
        return gotPaid;
    }

    private void byebye() {
        //todo: pour l'instant, les données sont enregistrées seulement lorsqu'on quitte l'application.
        // il faudrait soit :
        //   a) un auto-save (attention à la synchronisation lors de l'accès aux fichiers)
        //   b) sauvegarder les objets du database chaque fois qu'ils sont modifiés (ça n'arrive pas si souvent en théorie...)
        Controller.logic.requestStoreData();
        primaryStage.close();
        Platform.exit();
        System.exit(0);
    }
}
