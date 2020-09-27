package com.equipeDix.views;

import com.equipeDix.database.*;
import com.equipeDix.logic.Logic;
import javafx.scene.layout.GridPane;

public abstract class AccountView extends StartView{

    final Logic logic;

    public AccountView(Logic logic, Account account) {
        super(logic);
        this.logic = logic;
        // propriété des champs
        setStartTextFieldProperties();

        // Boutons
        GymButton saveChanges = new GymButton("Modifier des informations");
        GymButton newSearch = new GymButton("Nouvelle recherche");
        GymButton delete = new GymButton("Effacer le compte");

        // action des boutons

        final boolean[] modifyMode = {false};
        saveChanges.setOnAction(e -> {
            if(modifyMode[0]) {
                if (saveChanges(account)) {
                    modifyMode[0] = false;
                    saveChanges.setText("Modifier des informations");
                    Confirmation.alertBox("Modifications enregistrées");
                    setEditableAllTextFieldsExceptID(false);
                } else {
                    Confirmation.alertBox("Les modifications n'ont pas pu être enregistrées");
                }
            } else {
                saveChanges.setText("Enregistrer les modifications");
                modifyMode[0] = true;
                setEditableAllTextFieldsExceptID(true);
            }
        });

        newSearch.setOnAction(e -> Controller.startView());

        delete.setOnAction(e -> {
            if (Confirmation.confirmDelete()) {
                if (logic.delete(account)) {
                    Confirmation.alertBox("Le compte a bien été supprimé");
                    Controller.startView();
                } else {
                    Confirmation.alertBox("ERREUR : le compte n'a pas pu être supprimé.");
                }
            }
        });

        // Peupler les champs
        setTextTelephone(account.getTelephone());
        setTextAddress(account.getAddress());
        setTextLastName(account.getNom());
        setTextFirstName(account.getPrenom());
        setTextEmail(account.getEmail());

        // Ajout des boutons dans la vue
        GridPane menuView = getMenuView();
        menuView.add(saveChanges,1,0);
        menuView.add(newSearch, 0,0);
        menuView.add(delete, 5,0);
        hideNewAccountButton(true);

        setMenuView(menuView);
    }

    protected boolean saveChanges(Account account){

        try {
            String last = getLastNameValue();
            String first = getFirstNameValue();
            String[] address = getAddressValue();
            String email = getEmailValue();

            // blank in adr?
            boolean isBlankPresent = false;
            for(String field : address){
                isBlankPresent = field.isBlank() || field.isEmpty();
            }

            if(last.isBlank() || first.isBlank() || email.isBlank() || isBlankPresent){
                Confirmation.alertBox("Veuillez remplir tous les champs");
                throw new Exception();
            }
            account.setNom(last);
            account.setPrenom(first);
            account.setAddress(address);
            String telCheck = telCheck();
            if(telCheck != null){
                account.setTelephone(getTelephoneValue());
            }

            if(!email.equals(account.getEmail())){
                if(logic.isEmailAlreadyUsed(email)){
                    Confirmation.alertBox("Cette adresse courriel est déjà utilisée par un autre utilisateur");
                    throw new Exception();
                }
            }

            return logic.update(account);
        } catch (Exception e) {
            return false;
        }
    }

    private void setStartTextFieldProperties(){
        setDisableAllTextFieldsExceptID(false);
        setEditableAllTextFieldsExceptID(false);

        setEditableID(false);
    }

}
