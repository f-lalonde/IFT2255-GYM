package com.equipeDix.views;

import com.equipeDix.CentreDeDonnees;
import com.equipeDix.database.*;
import com.equipeDix.logic.Logic;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class NewServiceView extends ServiceBasicsView {

    private HashMap<String, ArrayList<LocalTime[]>> recurring;
    private LocalDateTime seanceDateStart;
    private LocalDateTime seanceDateEnd;
    private NewSeanceView nsvAH;
    private NewSeanceView nsvRC;

    public NewServiceView(Logic logic, String proID) {
        super();

        AccntPro pro = (AccntPro) logic.getAccount(proID);
        seanceViewAH();
        // Boutons
        CheckBox recurCheck = new CheckBox("Séances récurrentes?");

        GymButton saveInfos = new GymButton("Sauvegarder les informations");
        GymButton cancel = new GymButton("Annuler");
        GymButton recurBtn = new GymButton("Passer au choix des récurrences");

        // action des boutons

        saveInfos.setOnAction(e -> {
            if(Confirmation.confirmation("Les données sont exactes")) {
                if(recurCheck.isSelected()){
                    recurring = nsvRC.getRecurring();

                } else {
                    recurring = new HashMap<>();
                    seanceDateEnd = nsvAH.getSeanceDateEnd();
                    seanceDateStart = nsvAH.getSeanceDateStart();
                }

                Service newService = createService(logic, pro, LocalDate.from(seanceDateStart), recurring);

                if(newService != null){
                    if(createFirstSeance(logic, newService)){
                        Confirmation.alertBox("Service crée avec succès");
                        Controller.proView(pro);
                    } else {
                        Confirmation.alertBox("Problème lors de la création de la première séance");
                    }
                } else {
                    Confirmation.alertBox("La séance n'a pas pu être créer. Vérifier les informations fournies");
                }
            }
        });


        cancel.setOnAction(e -> Controller.proView(pro));

        recurBtn.setDisable(true);
        recurBtn.setOnAction(e -> {
            if(nsvAH.getSeanceDateEnd() == null){
                Confirmation.alertBox("Veuillez d'abord faire le choix de la date de la première séance");
            } else {
                seanceDateStart = nsvAH.getSeanceDateStart();
                seanceDateEnd = nsvAH.getSeanceDateEnd();
                seanceViewRC();
            }
        });

        GridPane menuView = getMenuView();
        // On peuple le menu et on l'applique
        menuView.add(cancel, 0,0);
        menuView.add(saveInfos, 3,0);

        // On ajoute le choix de la récurrence à la vue du service

        GridPane serviceView = getServiceView();

        Text recurExplained = new Text("Il faut d'abord choisir la date et heure de la première séance. " +
                "Ensuite, vous pourrez choisir les récurrences.");

        recurCheck.setOnAction(e -> {
            if(recurCheck.isSelected()){
                serviceView.add(recurExplained,2,4);
                recurBtn.setDisable(false);
            } else {
                serviceView.getChildren().remove(recurExplained);
                recurBtn.setDisable(true);
                if(seanceDateEnd != null){
                    CentreDeDonnees.bottomView.getChildren().clear();
                    CentreDeDonnees.bottomView.getChildren().add(nsvAH.getAdHocView());
                }
            }
        });
        serviceView.add(recurBtn, 2,3);
        serviceView.add(recurCheck,5,3);
        newServiceDisableFields(true);

    }

    private Service createService(Logic logic, AccntPro pro, LocalDate start, HashMap<String, ArrayList<LocalTime[]>> recurring){
        try {
            String[] infos = {getTextServiceName(), getTextCapacity(), getTextCost()};
            return logic.createService(infos, pro, start, recurring);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean createFirstSeance(Logic logic, Service service){
        try{
            int cap = Integer.parseInt(getTextCapacity());
            return logic.createSeance(seanceDateStart, seanceDateEnd, cap, service);
        } catch (Exception e) {
            return false;
        }
    }

    private void seanceViewAH(){
        nsvAH = new NewSeanceView();
        CentreDeDonnees.bottomView.getChildren().clear();
        CentreDeDonnees.bottomView.getChildren().add(nsvAH.getAdHocView());

    }

    private void seanceViewRC(){
        nsvRC = new NewSeanceView();
        CentreDeDonnees.bottomView.getChildren().clear();
        CentreDeDonnees.bottomView.getChildren().add(nsvRC.getRecurView());

    }

    @Override
    public void applyView() {
        CentreDeDonnees.menuBar.getChildren().clear();
        CentreDeDonnees.menuBar.getChildren().add(getMenuView());
        CentreDeDonnees.topView.getChildren().clear();
        CentreDeDonnees.topView.getChildren().add(getServiceView());
    }
}
