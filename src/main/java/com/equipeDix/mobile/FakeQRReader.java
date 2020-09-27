package com.equipeDix.mobile;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Optional;

public class FakeQRReader {

    private FakeQRReader(){

    }

    public static String verifyMember(){
        GridPane tempRoot = new GridPane();
        Text description = new Text("Imaginez que votre cellulaire est en mode photo " +
                "et recherche activement un code QR, entrez un numéro de membre puis pesez sur 'Enter'");
        description.setWrappingWidth(320);
        description.setTextAlignment(TextAlignment.CENTER);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setResizable(false);
        dialog.setTitle("Totally a QRreader");
        dialog.setHeaderText("Vraiment, ceci est un lecteur de code QR.");
        dialog.setContentText("Entrez le code que le code QR contient");
        dialog.getEditor().setPromptText("000000000");

        ColumnConstraints rootCenter = new ColumnConstraints();
        rootCenter.setHalignment(HPos.CENTER);
        tempRoot.getColumnConstraints().add(rootCenter);
        tempRoot.setMinWidth(MobileApp.WIDTH);
        tempRoot.setMaxWidth(MobileApp.WIDTH);

        RowConstraints rows = new RowConstraints();
        rows.setValignment(VPos.CENTER);
        rows.setMaxHeight(266);
        rows.vgrowProperty().setValue(Priority.ALWAYS);
        tempRoot.getRowConstraints().addAll(rows,rows,rows);

        tempRoot.addColumn(0, description);
        Scene tempScene = new Scene(tempRoot, MobileApp.WIDTH, MobileApp.HEIGHT);
        MobileApp.changeScene(tempScene);

        Optional<String> result = dialog.showAndWait();
        final String[] id = new String[1];
        id[0] = null;
        result.ifPresent(res -> id[0] = res);
        MobileApp.returnToRootScene();
        return id[0];
    }
}
