package com.equipeDix.views;

import com.equipeDix.CentreDeDonnees;
import com.equipeDix.logic.Logic;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class NewAccountView extends StartView {

    private final ToggleGroup typeCompte = new ToggleGroup();
    private final RadioButton choixMembre = new RadioButton("Membre");
    private final RadioButton choixPro = new RadioButton("Professionnel");

    public NewAccountView(Logic logic){
        super(logic);
        GridPane accountView = getAccountView();
        setDisableID(true);
        setDisableAllTextFieldsExceptID(false);


        // Boutons
        GymButton saveInfos = new GymButton("Sauvegarder les informations");

        GymButton cancel = new GymButton("Annuler");

        Text choixType = new Text("Type de compte:  ");

        choixMembre.setToggleGroup(typeCompte);
        choixPro.setToggleGroup(typeCompte);
        typeCompte.selectToggle(choixMembre);
        FlowPane flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER_LEFT);
        Text buffer = new Text("    ");
        flowPane.getChildren().addAll(choixMembre, buffer, choixPro);

        // Actions des boutons
        saveInfos.setOnAction(event -> {
            if(Confirmation.confirmation("Les données sont exactes")) {
                try{
                    String newID = sendInfos(logic);
                    if(newID == null){
                        throw new Exception();
                    }
                    Alert showId = new Alert(Alert.AlertType.INFORMATION);
                    showId.setTitle("Numéro de compte");
                    showId.setHeaderText("Veuiller prendre en note ce numéro :");
                    showId.setContentText(newID);
                    showId.showAndWait();
                    Controller.startView();
                } catch (Exception e) {
                    Confirmation.alertBox("Problème lors de la création du compte");
                }
            }
        });

        cancel.setOnAction(event -> Controller.startView());

        accountView.add(choixType,1,3);
        accountView.add(flowPane, 2,3);

        GridPane menuView = getMenuView();

        menuView.getChildren().removeIf(node -> node != null && node.getId() != null
                && node.getId().equals("newAccountBtn"));
        menuView.add(saveInfos,0,0);
        menuView.add(cancel, 2,0);
        setAccountView(accountView);
        setMenuView(menuView);
    }

    private String sendInfos(Logic logic){
        String telCheck = telCheck();
        String firstName = getFirstNameValue();
        String lastName = getLastNameValue();
        String[] address = getAddressValue();
        String email =  getEmailValue();

        // blank in adr?
        boolean isBlankPresent = false;
        for(String field : address){
            isBlankPresent = field.isBlank() || field.isEmpty();
        }

        if(firstName.isBlank() || lastName.isBlank() || isBlankPresent || email.isBlank()){
            Confirmation.alertBox("Il faut remplir tous les champs");
            return null;
        }
        if(telCheck == null){
            return null;
        } else {
            int choice;
            if(typeCompte.getSelectedToggle().equals(choixMembre)){
                choice = 1;
                if(!CentreDeDonnees.payment()){
                    return null;
                }
            } else if(typeCompte.getSelectedToggle().equals(choixPro)) {
                choice = 2;
            } else {
                return null;
            }

            return logic.createAccount(choice, firstName, lastName, address, telCheck, email);
        }
    }


}
