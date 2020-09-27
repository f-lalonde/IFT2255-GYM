package com.equipeDix.mobile;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MobileApp extends Application {

    public static final int WIDTH = 450, HEIGHT = 800;
    private final ScrollPane realRoot = new ScrollPane();
    public static final GridPane root = new GridPane();
    public static Scene mobileScene;
    private static Stage mobile;
    @Override
    public void start(Stage mobile) throws Exception {
        MobileApp.mobile = mobile;
        ColumnConstraints rootCenter = new ColumnConstraints();
        rootCenter.setHalignment(HPos.CENTER);
        root.getColumnConstraints().add(rootCenter);

        mobile.setTitle("Prototype application mobile GYM");
        mobile.setResizable(false);
        realRoot.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        realRoot.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        realRoot.setPrefSize(WIDTH, HEIGHT);
        realRoot.setContent(root);
        root.setMinWidth(WIDTH-10);
        root.setMaxWidth(WIDTH-10);
        mobileScene = new Scene(realRoot, WIDTH, HEIGHT);
        mobile.setScene(mobileScene);
        MobileViewController.startView();
        mobile.show();

    }

    public static void returnToRootScene(){
        mobile.setScene(mobileScene);
    }

    public static void changeScene(Scene scene){
        mobile.setScene(scene);
    }
}