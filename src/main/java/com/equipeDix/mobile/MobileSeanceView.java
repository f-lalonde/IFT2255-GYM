package com.equipeDix.mobile;

import com.equipeDix.CentreDeDonnees;
import com.equipeDix.database.AccntMember;
import com.equipeDix.database.Seance;
import com.equipeDix.logic.Logic;
import com.equipeDix.views.Confirmation;
import com.equipeDix.views.Controller;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

import static java.time.temporal.ChronoUnit.MINUTES;

public class MobileSeanceView extends MobileServiceView{
    private final Logic logic;
    private TableView<AccntMember> membersTable;
    private final Seance seance;
    private final GridPane seanceInfos;
    private final Button enroll;
    private final String fromAccount;
    private final Button goBack;
    private final Button verify;
    public MobileSeanceView(Logic logic, String fromAccount, String serviceID, String seanceID){
        super(logic, fromAccount, serviceID);
        this.logic = logic;
        this.fromAccount = fromAccount;
        seance = logic.getSeance(seanceID);

        // Modification de l'action du bouton goBack
        goBack = getGoBack();
        goBack.setOnAction( e -> MobileViewController.serviceView(fromAccount, serviceID));
        setGoBack(goBack);

        // Bouton d'inscription pour les membres

        enroll = new Button("Je m'inscris à cette séance!");
        enroll.setPrefSize(130,50);

        // Bouton de prise de présence / vérification d'inscription (Pro)
        verify = new Button("Confirmer inscription");
        verify.setPrefSize(130,33);
        verify.setOnAction(e -> {
            String idRead = FakeQRReader.verifyMember();
            if(idRead == null){
                new Alert(Alert.AlertType.ERROR, "code QR invalide", ButtonType.CLOSE).showAndWait();
            } else {
                AccntMember member = (AccntMember) logic.getAccount(idRead);
                if(logic.confirmMemberAttendance(member, seanceID)){
                    new Alert(Alert.AlertType.CONFIRMATION, "Membre Validé. Bienvenu à " + member.getPrenom() +
                            " " +member.getNom()+"!", ButtonType.CLOSE).showAndWait();
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "Membre non valide ou non inscrit",ButtonType.CLOSE).showAndWait();
                }
            }
        });

        // Description des champs

        Text dispoText = new Text("Places restantes : ");
        Text onDayText = new Text("A lieu le : ");
        Text durationText = new Text();

        // Remplissage des champs

        Text onDay = new Text();
        Text dispo = new Text();
        long durationTime = LocalTime.from(seance.getDateStart()).until(LocalTime.from(seance.getDateEnd()), MINUTES);
        durationText.setText("Durée : "+ durationTime +" minutes");

        int dispoInt = seance.getCapacity() - seance.getEnrolledMembers().size();
        dispo.setText("   " + dispoInt);
        onDay.setText(" "+ LocalDate.from(seance.getDateStart()).toString() +  " à " +
                LocalTime.from(seance.getDateStart()).toString());

        seanceInfos = getServiceInfos();

        RowConstraints rows = new RowConstraints();
        rows.setValignment(VPos.CENTER);
        rows.setMaxHeight(66);
        rows.vgrowProperty().setValue(Priority.ALWAYS);
        seanceInfos.getRowConstraints().addAll(rows,rows);

        if(fromAccount.startsWith("9")) {
            goBack.setTextAlignment(TextAlignment.CENTER);
            seanceInfos.getChildren().remove(goBack);
            seanceInfos.add(goBack, 0, 0,1,1);
            seanceInfos.add(verify, 1, 0);
        } else {
            // vérification pour l'activation du bouton "enroll"
            AccntMember membre = (AccntMember) logic.getAccount(fromAccount);

            boolean memberValid = true;
            boolean alreadyEnrolled = false;
            boolean isOver = seance.getDateEnd().isBefore(LocalDateTime.now());
            if(membre.getSeanceInscr().contains(seanceID)){
                alreadyEnrolled = true;
            }
            if(membre.getValidUntil().isBefore(LocalDate.now())){
                memberValid = false;
            }

            if(alreadyEnrolled || !(memberValid) || isOver){
                enroll.setDisable(true);
                if(alreadyEnrolled) {
                    enroll.setText("Vous êtes déjà inscrit à cette séance");
                } else if(isOver){
                    enroll.setText("La séance est passée.");
                } else {
                    enroll.setText("Veuillez renouveller votre abonnement.");
                }
            }


            enroll.setOnAction( e -> {
                if(logic.memberRegisterToSeance(membre, seance)){
                    if(CentreDeDonnees.payment()){
                        Controller.memberView(membre);
                    }
                } else {
                    Confirmation.alertBox("Erreur : problème lors de l'enregistrement.");
                }
            });
        }
        seanceInfos.addColumn(0, dispoText, onDayText);
        seanceInfos.addColumn(1, dispo, onDay, durationText);

    }

    @Override
    protected void fetchTable() {
        // Table des membres
        if(LocalDate.from(seance.getDateStart()).isBefore(LocalDate.now())){
            ArrayList<String> attendedMemberArrayList = new ArrayList<>(seance.getAttendedMembers().keySet());
            membersTable = logic.buildTableViewMembers(logic, attendedMemberArrayList);
        } else {
            membersTable = logic.buildTableViewMembers(logic, seance.getEnrolledMembers());
        }
    }

    @Override
    public void applyView() {
        fetchTable();

        MobileApp.root.getChildren().clear();
        MobileApp.root.add(seanceInfos,0,0,1,3);
        if(fromAccount.startsWith("9")){
            MobileApp.root.add(membersTable,0,3);
        } else {
            enroll.setTextAlignment(TextAlignment.CENTER);
            MobileApp.root.add(enroll, 0,2, 1,2);
        }

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
            System.out.println("Tapez 'Inscription' pour vous inscrire à cette séance.\nEntrez 'retour' pour retourner au menu précédent");
            if(fromAccount.startsWith("9")){
                System.out.println("Entrez 'confirmer' pour faire la confirmation d'un membre");
            }
            String inscription = scanner.nextLine();
            if(inscription.toLowerCase().equals("retour")){
                Thread.sleep(500);

                Platform.runLater(goBack::fire);

            } else if(inscription.toLowerCase().equals("inscription")){
                Thread.sleep(500);
                Platform.runLater(enroll::fire);
            } else if(inscription.toLowerCase().equals("confirmer") && fromAccount.startsWith("9")) {
                System.out.println("Entrez le numéro du membre que vous souhaitez vérifier");
                String idRead = scanner.nextLine();
                if(idRead == null){
                    System.out.println("code QR invalide");
                } else {
                    AccntMember member = (AccntMember) logic.getAccount(idRead);
                    if(logic.confirmMemberAttendance(member, seance.getSeanceID())){
                        System.out.println("Membre Validé. Bienvenu à " + member.getPrenom() +
                                " " +member.getNom()+"!");
                    } else {
                        System.out.println("Membre non valide ou non inscrit, ou bien ce n'est pas la journée de la séance");
                    }
                }
                consoleItou();
            } else {
                System.out.println("Entrée invalide");
                consoleItou();
            }

        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
