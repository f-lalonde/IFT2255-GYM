package com.equipeDix.mobile;

import com.equipeDix.database.AccntPro;
import com.equipeDix.database.Seance;
import com.equipeDix.logic.BuildTableViews;
import com.equipeDix.logic.Logic;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class MobileProView {

    private final GridPane infoPane = new GridPane();
    private final TableView<Seance> weeklySeanceView;
    private final AccntPro pro;
    private final Button repertoireServices = new Button("Accéder au répertoire des services");
    private final Logic logic;
    public MobileProView(Logic logic, AccntPro pro){
        this.logic = logic;
        this.pro = pro;

        repertoireServices.setWrapText(true);
        repertoireServices.setPrefSize(150,60);
        repertoireServices.setOnAction(e -> MobileViewController.repertoireDesServices(pro.getId()));

        LocalDate today = LocalDate.now();
        ArrayList<String> seancesSoon = new ArrayList<>();
        for(String seanceID : pro.getSeancesIndex()){
            LocalDate temp = LocalDate.from(logic.getSeance(seanceID).getDateStart());
            if(temp.isBefore(today.plusWeeks(1))){
                seancesSoon.add(seanceID);
            }
        }
        weeklySeanceView = logic.buildTableViewSeance(logic, seancesSoon);
        weeklySeanceView.setMaxSize(440,400);
        weeklySeanceView.setMinSize(440, Region.USE_COMPUTED_SIZE);
        // Double clic pour accéder aux séances

        weeklySeanceView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                try {
                    int pos = weeklySeanceView.getSelectionModel().getSelectedCells().get(0).getRow();
                    String seanceID = (String) weeklySeanceView.getColumns().get(0).getCellData(pos);
                    String serviceID = logic.getSeance(seanceID).getParentServiceID();
                    MobileViewController.seanceView(pro.getId(), serviceID, seanceID);

                } catch (Exception e) {
                    System.out.println("PROBLEM");
                }
            }
        });
        infoPane();
        consoleItou();
    }

    private void infoPane(){
        infoPane.setMinWidth(440);

        Text accountDescription = new Text("Informations du compte :");
        Text proName = new Text("Nom du professionnel : \n" + pro.getPrenom() + " " + pro.getNom());
        Text proID = new Text("Numéro du compte :\n" + pro.getId());

        accountDescription.setWrappingWidth(320);
        accountDescription.setTextAlignment(TextAlignment.CENTER);
        accountDescription.setStyle("-fx-font-weight : bold;");

        ColumnConstraints infoCol = new ColumnConstraints();
        infoCol.setHalignment(HPos.CENTER);
        infoCol.setHgrow(Priority.ALWAYS);
        infoPane.getColumnConstraints().addAll(infoCol, infoCol);

        RowConstraints infoRow = new RowConstraints();
        infoRow.setValignment(VPos.CENTER);
        infoPane.getRowConstraints().addAll(infoRow, infoRow, infoRow);

        infoPane.add(accountDescription,0,0,2,1);
        infoPane.addRow(1,proName,proID);
    }

    public void applyView(){

        MobileApp.root.getChildren().clear();
        MobileApp.root.add(infoPane, 0,1);
        MobileApp.root.add(repertoireServices,0,2);
        MobileApp.root.add(weeklySeanceView,0,3,1,2);
    }

    public void consoleItou(){
        Runnable console = new Runnable() {
            @Override
            public void run() {
                runConsole();
            }
        };

        Thread backgroundThread = new Thread(console);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public void runConsole() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Entrez 'repertoire' pour accéder au répertoire des services\n" +
                    "Entrez l'ID d'une séance pour y accéder");
            String seanceIDconsole = scanner.nextLine();
            if (seanceIDconsole.toLowerCase().equals("repertoire")) {
                Thread.sleep(500);

                Platform.runLater(repertoireServices::fire);

            } else {
                try {
                    seanceIDconsole = String.format("%07d", Integer.parseInt(seanceIDconsole));
                } catch (Exception e) {
                    System.out.println("Entrez un ID valide");
                    consoleItou();
                }
                Thread.sleep(500);
                String finalSeanceIDconsole = seanceIDconsole;
                final Seance[] seanceConsole = new Seance[1];
                Platform.runLater(() -> seanceConsole[0] = logic.getSeance(finalSeanceIDconsole));
                Thread.sleep(500);
                String[] parentID = new String[1];
                Platform.runLater(() -> parentID[0] = seanceConsole[0].getParentServiceID());
                Thread.sleep(500);
                Platform.runLater(() -> MobileViewController.seanceView(pro.getId(), parentID[0], finalSeanceIDconsole));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
