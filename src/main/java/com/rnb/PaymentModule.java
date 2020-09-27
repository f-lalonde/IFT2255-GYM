package com.rnb;

import com.equipeDix.CentreDeDonnees;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

// placeholder pour les services financiers RnB

public class PaymentModule extends Application {
    static Stage paymentStage;
    @Override
    public void start(Stage paymentStage) throws Exception {
        PaymentModule.paymentStage = paymentStage;
        URL url = this.getClass().getResource("/rnb.html");
        WebView root = new WebView();
        WebEngine webEngine = root.getEngine();

        // listener pour javascript
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {

                // set an interface object named 'javaConnector' in the web engine's page
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("app",  new JavaConnector());

            }

        });

        paymentStage.setTitle("Services de paiement RnB");
        paymentStage.setScene(new Scene(root, 400,400));
        
        webEngine.load(url.toString());

        paymentStage.showAndWait();

    }

    public static void closeWindow(){
        paymentStage.close();
    }

    public static class JavaConnector {

        @SuppressWarnings("unused")
        public void payment(String value){
            if (null != value) {
                CentreDeDonnees.setGotPaid(value.equals("accept"));
            } else {
                CentreDeDonnees.setGotPaid(false);
            }
            PaymentModule.closeWindow();
        }
    }
}




