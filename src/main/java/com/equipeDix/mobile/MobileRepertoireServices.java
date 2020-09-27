package com.equipeDix.mobile;

import com.equipeDix.database.AccntMember;
import com.equipeDix.database.AccntPro;
import com.equipeDix.logic.Logic;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.util.Map;
import java.util.Scanner;

public class MobileRepertoireServices {

    private final String fromAccount;
    private final Logic logic;
    public MobileRepertoireServices(Logic logic, String fromAccount){
        this.fromAccount = fromAccount;
        this.logic = logic;

        Button goback = new Button("Retour au compte");
        goback.setPrefSize(100,55);
        goback.setOnAction(e -> {
            if(fromAccount.startsWith("9")){
                MobileViewController.proView((AccntPro) logic.getAccount(fromAccount));
            } else {
                MobileViewController.memberView((AccntMember) logic.getAccount(fromAccount));
            }
        });

        TableView<Map.Entry<String, String>> repertoire = logic.buildTableRepertoireServices(logic);
        repertoire.setMaxHeight(600);
        repertoire.setMinSize(450,600);

        // Double clic pour accéder aux services

        repertoire.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                try {
                    int pos = repertoire.getSelectionModel().getSelectedCells().get(0).getRow();
                    String serviceID = (String) repertoire.getColumns().get(0).getCellData(pos);
                    MobileViewController.serviceView(fromAccount, serviceID);
                } catch (Exception ignored) {
                    //
                }
            }
        });

        MobileApp.root.getChildren().clear();
        MobileApp.root.add(goback,0,0);
        MobileApp.root.add(repertoire, 0, 1,1,3);

        consoleItou();
    }

    public void consoleItou(){
        Runnable console = this::runConsole;

        Thread backgroundThread = new Thread(console);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public void runConsole(){
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Entrez le ID du service que vous désirez consulter.\nEntrez 'retour' pour retourner au menu précédent");
            String serviceIDconsole = scanner.nextLine();
            if(serviceIDconsole.toLowerCase().equals("retour")){
                Thread.sleep(500);
                if(fromAccount.startsWith("9")){
                    Platform.runLater(() -> MobileViewController.proView((AccntPro) logic.getAccount(fromAccount)));
                } else {
                    Platform.runLater(() -> MobileViewController.memberView((AccntMember) logic.getAccount(fromAccount)));
                }
            } else {
                try {
                    serviceIDconsole = String.format("%07d", Integer.parseInt(serviceIDconsole));
                } catch (Exception e) {
                    System.out.println("Entrez un ID valide");
                    consoleItou();
                }
                Thread.sleep(500);
                String finalSeanceIDconsole = serviceIDconsole;
                Platform.runLater(() -> MobileViewController.serviceView(fromAccount, finalSeanceIDconsole));
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
