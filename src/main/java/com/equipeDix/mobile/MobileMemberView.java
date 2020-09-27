package com.equipeDix.mobile;

import com.equipeDix.database.AccntMember;
import com.equipeDix.database.Seance;
import com.equipeDix.logic.Logic;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class MobileMemberView {

    private final GridPane validUntilPane = new GridPane();
    private final GridPane infoPane = new GridPane();
    private final ImageView qrView = new ImageView();
    private final TableView<Seance> weeklySeanceView;
    private final AccntMember member;
    private final Button repertoireServices = new Button("Accéder au répertoire des services");
    private boolean isMemberValid = true;

    public MobileMemberView(Logic logic, AccntMember member){
        this.member = member;

        repertoireServices.setWrapText(true);
        repertoireServices.setPrefSize(150,60);
        repertoireServices.setOnAction(e -> MobileViewController.repertoireDesServices(member.getId()));

        validPane();
        infoPane();

        //qrView.setImage(qrCode);

        LocalDate today = LocalDate.now();

        ArrayList<String> seancesSoon = new ArrayList<>();
        for(String seanceID : member.getSeanceInscr()){
            LocalDate temp = LocalDate.from(logic.getSeance(seanceID).getDateStart());
            if(temp.isBefore(today.plusWeeks(1))){
                seancesSoon.add(seanceID);
            }
        }

        weeklySeanceView = logic.buildTableViewSeance(logic, seancesSoon);
        weeklySeanceView.setMaxSize(440,400);
        weeklySeanceView.setMinSize(440, Region.USE_COMPUTED_SIZE);

        consoleItou();
    }

    private void validPane(){
        validUntilPane.setMinWidth(440);

        Text validUntilPre = new Text("Votre abonnement est valide jusqu'au : ");
        validUntilPre.setTextAlignment(TextAlignment.CENTER);

        Text validUntil = new Text(member.getValidUntil().toString());
        validUntil.setWrappingWidth(320);
        validUntil.setTextAlignment(TextAlignment.CENTER);

        Text validUntilPost = new Text("");
        validUntilPost.setWrappingWidth(320);
        validUntilPost.setTextAlignment(TextAlignment.CENTER);

        if(member.getValidUntil().isAfter(LocalDate.now())){
            validUntil.setStyle("-fx-border-color : green;" +
                    "-fx-border-width : 5px");
        } else if(member.getValidUntil().isBefore(LocalDate.now().plusWeeks(1))){
            validUntil.setStyle("-fx-border-color : orange;" +
                    "-fx-border-width : 5px");
            validUntilPost.setText("Avis : Votre abonnement se termine dans moins d'une semaine.");
        } else {
            validUntil.setStyle("-fx-border-color : crimson;" +
                    "-fx-border-width : 5px");
            validUntilPost.setText("Avis : votre abonnement est acutuellement inactif");
            isMemberValid = false;
        }

        ColumnConstraints validColumnConstraint = new ColumnConstraints();
        validColumnConstraint.setHalignment(HPos.CENTER);
        validColumnConstraint.setHgrow(Priority.ALWAYS);
        validUntilPane.getColumnConstraints().add(validColumnConstraint);

        RowConstraints validRow1and2 = new RowConstraints();
        RowConstraints validRow3 = new RowConstraints();
        RowConstraints validRow4 = new RowConstraints();

        validRow1and2.setMaxHeight(50);
        validRow1and2.setMinHeight(50);
        validRow1and2.setValignment(VPos.BOTTOM);

        validRow3.setValignment(VPos.CENTER);
        validRow3.setMaxHeight(60);
        validRow3.setMinHeight(60);

        validRow4.setValignment(VPos.TOP);
        validRow4.setMaxHeight(40);
        validRow4.setMinHeight(40);

        validUntilPane.getRowConstraints().addAll(validRow1and2, validRow1and2, validRow3, validRow4);

        validUntilPane.add(validUntilPre,0,1);
        validUntilPane.add(validUntil,0,2);
        validUntilPane.add(validUntilPost,0,3);
    }

    private void infoPane(){
        infoPane.setMinWidth(440);

        Text accountDescription = new Text("Informations du compte :");
        Text memberName = new Text("Nom du membre : \n" + member.getPrenom() + " " + member.getNom());
        Text memberID = new Text("Numéro du compte :\n" + member.getId());

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
        infoPane.addRow(1,memberName,memberID);
    }

    public void applyView(){

        MobileApp.root.getChildren().clear();
        MobileApp.root.add(validUntilPane,0,0);
        MobileApp.root.add(infoPane, 0,1);
        if(isMemberValid){
            MobileApp.root.add(qrView,0,2);
        } else {
            Text plzRenew = new Text("Veuillez vous présenter à la réception du centre #GYM " +
                    "si vous désirez reprendre un abonnement");
            plzRenew.setWrappingWidth(320);
            plzRenew.setTextAlignment(TextAlignment.CENTER);
            MobileApp.root.add(plzRenew,0,2);
        }
        MobileApp.root.add(repertoireServices,0,3);
        MobileApp.root.add(weeklySeanceView,0,4,1,2);
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
            System.out.println("Faites une entrée quelconque pour accéder au répertoire des services");
            String emailConsole = scanner.nextLine();
            Thread.sleep(500);

            Platform.runLater(repertoireServices::fire);

        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
