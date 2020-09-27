package com.equipeDix.views;

import com.equipeDix.CentreDeDonnees;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public abstract class ServiceBasicsView {
    private final GridPane serviceView = new GridPane();
    private final GridPane menuView = new GridPane();
    private final TextField idNumber = new TextField();
    private final TextField proName = new TextField();
    private final TextField serviceName = new TextField();
    private final TextField cost = new TextField();
    private final TextField capacity = new TextField();
    private final TextField createdOn = new TextField();
    private final TextField[] editSwitchable = {serviceName, cost, capacity};
    private TableView<?> tableView = new TableView<>();
    private final Separator separateurService = new Separator(Orientation.HORIZONTAL);

    public ServiceBasicsView(){
        // Mise en page du GridPane
        serviceViewSetUp();

        // Mise en page du menu
        menuViewSetUp();

        // Texte de label
        Text idText = new Text("# ID :  ");
        Text serviceNameText = new Text("Description :  ");
        Text proNameText = new Text("Fourni par :  ");
        Text costText = new Text("Coût par séance :  ");
        Text capacityText = new Text("Max participants  \n    par séance :  ");
        Text createdText = new Text("Créé le :  ");

        // prompts
        cost.setPromptText("Maximum 100$");
        capacity.setPromptText("Maximum 30");

        // Désactivation des champs qu'on ne peut pas modifier / remplir
        idNumber.setEditable(false);
        proName.setEditable(false);
        createdOn.setEditable(false);

        // On peuple le GridPane serviceView
        serviceView.add(idText,1,0);
        serviceView.add(serviceNameText,1,1);
        serviceView.add(createdText,1,2);

        serviceView.add(idNumber,2,0);
        serviceView.add(serviceName,2,1);
        serviceView.add(createdOn, 2, 2);

        serviceView.add(proNameText,4,0);
        serviceView.add(capacityText,4,1);
        serviceView.add(costText, 4, 2);

        serviceView.add(proName,5,0);
        serviceView.add(capacity,5,1);
        serviceView.add(cost, 5, 2);

    }

    private void serviceViewSetUp(){

        serviceView.setPrefHeight(275);
        serviceView.setAlignment(Pos.CENTER);

        // Paramètres rangées / colonnes
        // Rangées 0, 1, 2, 3, 4
        RowConstraints gridRow1 = new RowConstraints();
        gridRow1.setPrefHeight(50);
        gridRow1.setMinHeight(40);
        gridRow1.setMaxHeight(50);
        gridRow1.setValignment(VPos.CENTER);

        // Rangée 5
        RowConstraints gridRow2 = new RowConstraints();
        gridRow2.setPrefHeight(10);
        gridRow2.setMaxHeight(30);
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
        serviceView.getColumnConstraints().addAll(gridCol1, gridCol1, gridCol2, gridCol3, gridCol1, gridCol2, gridCol1);
        serviceView.getRowConstraints().addAll(gridRow1, gridRow1, gridRow1, gridRow1, gridRow1, gridRow2);

        // Séparateur

        serviceView.add(separateurService,0,5,7,1);
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

        // Séparateur
        Separator separateurMenu = new Separator(Orientation.HORIZONTAL);
        menuView.add(separateurMenu, 0, 1, 7, 1);
    }

    public void applyView(){
        CentreDeDonnees.menuBar.getChildren().clear();
        CentreDeDonnees.menuBar.getChildren().add(menuView);
        CentreDeDonnees.topView.getChildren().clear();
        CentreDeDonnees.topView.getChildren().add(serviceView);
        CentreDeDonnees.bottomView.getChildren().clear();
        CentreDeDonnees.bottomView.getChildren().add(tableView);
    }

    protected void setTextIdNumber(String value) {
        idNumber.setText(value);
    }

    protected void setTextProName(String value) {
        proName.setText(value);
    }

    protected void setTextServiceName(String value) {
        serviceName.setText(value);
    }

    protected void setTextCost(String value) {
        cost.setText(value);
    }

    protected void setTextCapacity(String value) {
        capacity.setText(value);
    }

    protected void setTextCreatedOn(String value) {
        createdOn.setText(value);
    }

    protected String getTextServiceName() {
        return serviceName.getText();
    }

    protected String getTextCost() {
        return cost.getText();
    }

    protected String getTextCapacity() {
        return capacity.getText();
    }

    protected void setTextTooltipCreatedOn(String value){
        createdOn.setTooltip(new Tooltip((value)));
    }

    protected GridPane getMenuView() {
        return menuView;
    }

    protected void setTableView(TableView<?> tableView) {
        this.tableView = tableView;
    }

    protected GridPane getServiceView() {
        return serviceView;
    }

    protected void setFieldEditable(boolean value){
        for(TextField tf : editSwitchable){
            tf.setEditable(value);
        }
    }

// --Commented out by Inspection START (2020-08-03 15:22):
//    protected void seanceFieldEditable(boolean value){
//        cost.setEditable(value);
//        serviceName.setEditable(value);
//    }
// --Commented out by Inspection STOP (2020-08-03 15:22)

    protected void newServiceDisableFields(boolean value){
        idNumber.setDisable(value);
        proName.setDisable(value);
        createdOn.setDisable(value);
    }

    protected void separatorPaddingSeance(){
        separateurService.setStyle("-fx-padding : 0 0 50 0;");
    }
}
