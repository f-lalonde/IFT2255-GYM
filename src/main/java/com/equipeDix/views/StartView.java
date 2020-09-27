package com.equipeDix.views;

import com.equipeDix.CentreDeDonnees;
import com.equipeDix.database.*;
import com.equipeDix.logic.Logic;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Optional;

public class StartView {

    // Champs à afficher
    private GridPane accountView = new GridPane();
    private GridPane menuView = new GridPane();
    private TableView<?> tableView;
    private final GymButton newAccount = new GymButton("Nouveau Compte");
    private final GymButton weeklyReport = new GymButton("Générer rapport hebdomadaire");
    private final TextField idNumber = new TextField();
    private final TextField telephone = new TextField();
    private final TextField addressCivicNumber = new TextField();
    private final TextField addressStreetName = new TextField();
    private final TextField addressCity = new TextField();
    private final ComboBox<String> addressProvince = new ComboBox<>();
    private final TextField addressPostalCode = new TextField();
    private final TextField lastName = new TextField();
    private final TextField firstName = new TextField();
    private final TextField email = new TextField();

    public StartView(Logic logic) {

        // Texte de label
        Text idText = new Text("# ID :  ");
        Text emailText = new Text("Courriel : ");
        Text telephoneText = new Text("Téléphone :  ");
        Text addressText = new Text("Adresse :  ");
        Text lastNameText = new Text("Nom :  ");
        Text firstNameText = new Text("Prénom :  ");

        addressCivicNumber.setPromptText("Numéro");
        addressStreetName.setPromptText("Rue");
        addressCity.setPromptText("Ville");
        addressPostalCode.setPromptText("Code Postal");
        addressProvince.getItems().addAll(
                "QC",
                "AB",
                "BC",
                "MB",
                "NB",
                "NL",
                "NT",
                "NS",
                "NU",
                "ON",
                "PE",
                "SK",
                "YT");
        addressProvince.getSelectionModel().selectFirst();


        // Désactivation des champs qu'on ne peut pas utiliser pour la recherche de compte
        // todo : permettre la recherche par nom et prénom ?


        addressCivicNumber.setDisable(true);
        addressStreetName.setDisable(true);
        addressCity.setDisable(true);
        addressProvince.setDisable(true);
        addressPostalCode.setDisable(true);
        lastName.setDisable(true);
        firstName.setDisable(true);
        telephone.setDisable(true);
        email.setDisable(true);

        addressCity.setMaxWidth(200);
        addressCivicNumber.setMaxWidth(60);
        addressPostalCode.setMaxWidth(100);

        // Disposition de la vue du menu (par effet de bord)
        menuViewSetUp();

        // Disposition de la vue du compte (par effet de bord)
        accountViewSetUp();

        // Disposition de la vue de la table (vide ici)
        tableView = new TableView<>();
        StackPane placeHolder = new StackPane();
        placeHolder.setStyle("-fx-background-color:linear-gradient(from 50px 0px to 50px 50px , repeat, #e8e8e8 49% , #f7f7f7 12% );");
        tableView.setPlaceholder(placeHolder);
        tableView.setPrefHeight(425);
        tableView.setFocusTraversable(false);

        // Boutons

        newAccount.setOnAction(actionEvent -> Controller.newAccount());
        weeklyReport.setOnAction(e -> logic.generateWeeklyReport(logic));


        // Actions
        idNumber.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
                Account account;
                String accntID = idNumber.getText();

                // Permets d'entrer "1" pour accéder au compte "000000001"

                if(!accntID.startsWith("9") && accntID.length() < 9) {
                    try {
                        String accntIDtry = String.format("%09d", Integer.parseInt(accntID));
                        account = logic.getAccount(accntIDtry);
                        if (account == null) {
                            throw new Exception();
                        } else {
                            loadAccount(account, accntIDtry);
                        }
                    } catch (Exception e) {
                        Confirmation.alertBox(Confirmation.alerts[0]);
                        idNumber.setText("");
                    }

                } else if (accntID.length() == 9) {
                    try {
                        account = logic.getAccount(idNumber.getText());
                        if (account == null) {
                            throw new Exception();
                        } else {
                            loadAccount(account, accntID);
                        }
                    } catch (Exception e) {
                        Confirmation.alertBox(Confirmation.alerts[1]);
                        idNumber.setText("");
                    }
                } else {
                    Confirmation.alertBox(Confirmation.alerts[0]);
                    idNumber.setText("");
                }
            }
        });

        // Arrangement des boutons dans la vue du menu
        menuView.add(newAccount, 0,0);
        menuView.add(weeklyReport, 4, 0);
        Separator separateurMenu = new Separator(Orientation.HORIZONTAL);
        menuView.add(separateurMenu, 0, 1, 7, 1);

        // Sous-vue pour l'adresse
        GridPane address = new GridPane();
        ColumnConstraints addressCol1 = new ColumnConstraints();
        ColumnConstraints addressCol2 = new ColumnConstraints();
        ColumnConstraints addressCol3 = new ColumnConstraints();
        addressCol1.setMaxWidth(80);
        addressCol1.setPrefWidth(80);
        addressCol2.setMaxWidth(140);
        addressCol2.setPrefWidth(130);
        addressCol3.setMaxWidth(95);
        addressCol3.setPrefWidth(90);
        address.getColumnConstraints().addAll(addressCol1, addressCol2, addressCol3);

        address.hgapProperty().setValue(10);
        address.add(addressCivicNumber, 0, 0);
        address.add(addressStreetName, 1, 0, 2,1);
        address.add(addressCity, 0,1,2,1);
        address.add(addressProvince, 2, 1);
        address.add(addressPostalCode, 0,2,2,1);

        // Arrangement des champs dans la vue du compte
        accountView.addColumn(1,idText, firstNameText, lastNameText);
        accountView.addColumn(2,idNumber, firstName, lastName);
        accountView.add(addressText, 4,0);
        accountView.add(telephoneText,4,2);
        accountView.add(emailText, 4,3);
        accountView.add(address, 5,0,1,2);
        accountView.add(telephone, 5,2);
        accountView.add(email, 5,3);

        setAccountView(accountView);

        // débute avec le focus sur le champ d'entrée
        Platform.runLater(idNumber::requestFocus);
    }

    private void loadAccount(Account account, String accountID){
        if(accountID.startsWith("9")){
            Controller.proView((AccntPro) account);
        } else {
            Controller.memberView((AccntMember) account);
        }
    }

    private void menuViewSetUp(){

        menuView.setMinSize(1024, 80);
        menuView.setPrefHeight(90);

        // Paramètre des colonnes
        ColumnConstraints menuCol = new ColumnConstraints();
        menuCol.setPrefWidth(171);
        menuCol.setHalignment(HPos.CENTER);

        // Paramètre de la rangée 0 (boutons)
        RowConstraints menuRow = new RowConstraints();
        menuRow.setPrefHeight(80);
        menuRow.setMinHeight(65);
        menuRow.setValignment(VPos.CENTER);

        // Paramètre de la rangée 2 (séparateur)
        RowConstraints menuRow2 = new RowConstraints();
        menuRow2.setPrefHeight(5);
        menuRow2.setValignment(VPos.BOTTOM);

        // Application des règles
        menuView.getRowConstraints().addAll(menuRow, menuRow2);
        menuView.getColumnConstraints().addAll(menuCol, menuCol, menuCol, menuCol, menuCol, menuCol);
    }

    private void accountViewSetUp(){

        accountView.setMinHeight(250);
        accountView.setPrefHeight(275);
        accountView.setAlignment(Pos.CENTER);

        // Paramètres rangées / colonnes
        // Rangées 0, 1, 2, 3
        RowConstraints gridRow1 = new RowConstraints();
        gridRow1.setPrefHeight(50);
        gridRow1.setMinHeight(40);
        gridRow1.setMaxHeight(50);
        gridRow1.setValignment(VPos.CENTER);

        // Rangée 4
        RowConstraints gridRow2 = new RowConstraints();
        gridRow2.setPrefHeight(50);
        gridRow2.setMaxHeight(80);
        gridRow2.setValignment(VPos.BOTTOM);

        // Colonnes 0, 1, 4, 6 (texte descriptif + padding / espace libre)
        ColumnConstraints gridCol1 = new ColumnConstraints();
        gridCol1.setMinWidth(100);
        gridCol1.setPrefWidth(110);
        gridCol1.setHalignment(HPos.RIGHT);

        // Colonnes 2, 5 (TextField / Area)
        ColumnConstraints gridCol2 = new ColumnConstraints();
        gridCol2.setMinWidth(200);
        gridCol2.setPrefWidth(250);
        gridCol2.setMaxWidth(260);

        // Colonnes 3 (padding)
        ColumnConstraints gridCol3 = new ColumnConstraints();
        gridCol3.setMinWidth(15);
        gridCol3.setPrefWidth(30);
        gridCol3.setMaxWidth(30);

        // Application des paramètres
        accountView.getColumnConstraints().addAll(gridCol1, gridCol1, gridCol2, gridCol3, gridCol1, gridCol2, gridCol1);
        accountView.getRowConstraints().addAll(gridRow1, gridRow1, gridRow1, gridRow1, gridRow2);
    }

    protected GridPane getAccountView() {
        return accountView;
    }

    protected GridPane getMenuView(){
        return menuView;
    }

    private TableView<?> getTableView(){
        return tableView;
    }

    protected void setAccountView(GridPane accountView) {
        this.accountView = accountView;
    }

    protected void setMenuView(GridPane menuView){
        this.menuView = menuView;
    }

    protected void setTableView(TableView<?> tableView){
        this.tableView = tableView;
    }

    public void applyView(){
        CentreDeDonnees.topView.getChildren().clear();
        CentreDeDonnees.topView.getChildren().add(getAccountView());
        CentreDeDonnees.menuBar.getChildren().clear();
        CentreDeDonnees.menuBar.getChildren().add(getMenuView());
        CentreDeDonnees.bottomView.getChildren().clear();
        CentreDeDonnees.bottomView.getChildren().add(getTableView());
    }

    protected void setDisableAllTextFieldsExceptID(boolean value){
        setDisableAddress(value);
        setDisableFirstName(value);
        setDisableLastName(value);
        setDisableTelephone(value);
        setDisableEmail(value);
    }

    protected void setDisableID(boolean value){
        idNumber.setDisable(value);
    }

    protected void setDisableLastName(boolean value){
        lastName.setDisable(value);
    }

    protected void setDisableFirstName(boolean value){
        firstName.setDisable(value);
    }

    protected void setDisableAddress(boolean value){
        addressCivicNumber.setDisable(value);
        addressStreetName.setDisable(value);
        addressCity.setDisable(value);
        addressProvince.setDisable(value);
        addressPostalCode.setDisable(value);
    }

    protected void setDisableTelephone(boolean value){
        telephone.setDisable(value);
    }

    protected void setDisableEmail(boolean value){
        email.setDisable(value);
    }

    protected void setEditableAllTextFieldsExceptID(boolean value){
        setEditableAddress(value);
        setEditableFirstName(value);
        setEditableLastName(value);
        setEditableTelephone(value);
        setEditableEmail(value);
    }

    protected void setEditableID(boolean value){
        idNumber.setEditable(value);
    }

    protected void setEditableLastName(boolean value){
        lastName.setEditable(value);
    }

    protected void setEditableFirstName(boolean value){
        firstName.setEditable(value);
    }

    protected void setEditableAddress(boolean value){
        addressCivicNumber.setEditable(value);
        addressStreetName.setEditable(value);
        addressCity.setEditable(value);
        addressProvince.setEditable(value);
        addressPostalCode.setEditable(value);
    }

    protected void setEditableTelephone(boolean value){
        telephone.setEditable(value);
    }

    protected void setEditableEmail(boolean value){
        email.setEditable(value);
    }

    protected void setTextID(String id){
        this.idNumber.setText(id);
    }

    protected void setTextFirstName(String firstName){
        this.firstName.setText(firstName);
    }

    protected void setTextLastName(String lastName){
        this.lastName.setText(lastName);
    }

    protected void setTextAddress(String[] address){
        addressCivicNumber.setText(address[0]);
        addressStreetName.setText(address[1]);
        addressCity.setText(address[2]);
        addressProvince.getSelectionModel().select(address[3]);
        addressPostalCode.setText(address[4]);
    }

    protected void setTextTelephone(String telephone){
        this.telephone.setText(telephone);
    }

    protected void setTextEmail(String email){
        this.email.setText(email);
    }

    protected String getFirstNameValue(){
        return firstName.getText();
    }

    protected String getLastNameValue(){
        return lastName.getText();
    }

    protected String[] getAddressValue(){
        String[] address = new String[5];
        address[0] = addressCivicNumber.getText();
        address[1] = addressStreetName.getText();
        address[2] = addressCity.getText();
        address[3] = addressProvince.getSelectionModel().getSelectedItem();
        address[4] = addressPostalCode.getText();
        return address;
    }

    protected String getTelephoneValue(){
        return telephone.getText();
    }

    protected String getEmailValue(){
        return email.getText();
    }

    protected String telCheck(){
        String telCheck = getTelephoneValue();
        while(true) {
            if (telCheck.length() == 10) {
                try {
                    Double.parseDouble(telCheck);
                    break;
                } catch (Exception e) {
                    telCheck = getNewTel();
                    if(telCheck == null){
                        break;
                    }
                }
            } else {
                telCheck = getNewTel();
                if(telCheck == null){
                    break;
                }
            }
        }
        return telCheck;
    }

    private String getNewTel() {
        TextInputDialog newTel = new TextInputDialog();
        newTel.setTitle("Téléphone invalide");
        newTel.setHeaderText("Il faut un numéro de téléphone valide pour créer un compte : " +
                "il doit être composé de 10 chiffres.");
        newTel.setContentText("Entrez un # valide:");
        Optional<String> result = newTel.showAndWait();
        return result.orElse(null);
    }

    protected void hideNewAccountButton(boolean value){
        newAccount.setDisable(value);
        newAccount.setVisible(!value);
        weeklyReport.setDisable(value);
        weeklyReport.setVisible(!value);

    }

}
