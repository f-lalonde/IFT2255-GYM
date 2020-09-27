package com.equipeDix.views;

import com.equipeDix.CentreDeDonnees;
import com.equipeDix.database.*;
import com.equipeDix.logic.Logic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ServiceView extends ServiceBasicsView {

    /**
     *
     * @param logic passer la logique du Centre de Données
     * @param serviceID #ID du service dont on veut voir les infos
     * @param fromAccount ID du compte dont provient la requête d'informations
     */
    public ServiceView(Logic logic, String fromAccount, String serviceID) {
        super();
        setFieldEditable(false);

        // Chargement des objets
        Service service = logic.getService(serviceID);
        AccntPro pro = (AccntPro) logic.getAccount(service.getProID());

        GridPane menuView = getMenuView();

        // On peuple les champs
        setTextIdNumber(serviceID);
        setTextProName(pro.getPrenom() + " " +pro.getNom());
        setTextServiceName(service.getServiceName());
        setTextCost(Integer.toString(service.getCost()));
        setTextCapacity(Integer.toString(service.getCapacity()));
        setTextCreatedOn(LocalDate.from(service.getDateCreated()).toString());
        setTextTooltipCreatedOn("à "+ LocalTime.from(service.getDateCreated()).toString().substring(0,8));

        // Boutons
        GymButton goBack = new GymButton("Retour au compte");
        GymButton saveChanges = new GymButton("Modifier les informations");
        GymButton newSeance = new GymButton("Créer une séance Ad Hoc");
        GymButton saveSeance = new GymButton("Enregistrer la séance");
        GymButton setRecurring = new GymButton("Changer la récurrence");
        GymButton saveRecurring = new GymButton("Enregistrer la récurrence");
        GymButton cancel = new GymButton("Annuler");
        GymButton delete = new GymButton("Effacer le service");

        cancel.setDisable(true);
        cancel.setVisible(false);
        // actions des boutons

        goBack.setOnAction(e -> {
            if(fromAccount.startsWith("9")){
                Controller.proView(pro);
            } else {
                Controller.memberView((AccntMember) logic.getAccount(fromAccount));
            }
        });

        final boolean[] modifyMode = {false};

        saveChanges.setOnAction(e -> {
            if(modifyMode[0]) {
                if (saveChanges(logic, service)) {
                    modifyMode[0] = false;
                    saveChanges.setText("Modifier les informations");
                    Confirmation.alertBox("Modifications enregistrées");
                    setFieldEditable(false);
                } else {
                    Confirmation.alertBox("Les modifications n'ont pas pu être enregistrées");
                }
            } else {
                saveChanges.setText("Enregistrer les modifications");
                modifyMode[0] = true;
                setFieldEditable(true);
            }
        });

        NewSeanceView nsv = new NewSeanceView(logic, service);

        newSeance.setOnAction(e -> {
            newSeance.setDisable(true);
            saveChanges.setDisable(true);

            cancel.setDisable(false);
            cancel.setVisible(true);
            delete.setDisable(true);
            delete.setVisible(false);

            setRecurring.setDisable(true);
            setRecurring.setVisible(false);
            saveSeance.setVisible(true);
            saveSeance.setDisable(false);

            CentreDeDonnees.bottomView.getChildren().clear();
            CentreDeDonnees.bottomView.getChildren().add(nsv.getAdHocView());
        });

        // invisible au départ
        saveSeance.setDisable(true);
        saveSeance.setVisible(false);

        saveSeance.setTextAlignment(TextAlignment.CENTER);
        saveSeance.setOnAction(e -> {
            if(logic.createSeance(nsv.getSeanceDateStart(), nsv.getSeanceDateEnd(), service.getCapacity(), service)){
                Confirmation.alertBox("La séance a bien été crée");
                tableSeance(logic, service, fromAccount);
                applyView();

                newSeance.setDisable(false);
                saveChanges.setDisable(false);

                cancel.setVisible(false);
                cancel.setDisable(true);
                delete.setDisable(false);
                delete.setVisible(true);

                setRecurring.setDisable(false);
                setRecurring.setVisible(true);
                saveSeance.setVisible(false);
                saveSeance.setDisable(true);

            } else {
                Confirmation.alertBox("ERREUR : la séance n'a pas pu être crée.");
            }
        });

        setRecurring.setOnAction(e -> {
            setRecurring.setDisable(true);
            saveChanges.setDisable(true);

            cancel.setDisable(false);
            cancel.setVisible(true);
            delete.setDisable(true);
            delete.setVisible(false);
            newSeance.setDisable(true);
            newSeance.setVisible(false);
            saveRecurring.setVisible(true);
            saveRecurring.setDisable(false);
            CentreDeDonnees.bottomView.getChildren().clear();
            CentreDeDonnees.bottomView.getChildren().add(nsv.getRecurView());
        });

        // invisible au départ
        saveRecurring.setDisable(true);
        saveRecurring.setVisible(false);

        saveRecurring.setOnAction(e ->{
            service.setRecurring(nsv.getRecurring());
            if(logic.update(service)){
                Confirmation.alertBox("La récurrence a été mise à jour");
                tableSeance(logic, service, fromAccount);
                applyView();

                setRecurring.setDisable(false);
                saveChanges.setDisable(false);

                cancel.setVisible(false);
                cancel.setDisable(true);
                delete.setDisable(false);
                delete.setVisible(true);
                newSeance.setDisable(false);
                newSeance.setVisible(true);
                saveRecurring.setVisible(false);
                saveRecurring.setDisable(true);
            } else {
                Confirmation.alertBox("Un problème est survenue lors de la mise à jour du service.");
            }
        });

        cancel.setOnAction(e -> {
            tableSeance(logic, service, fromAccount);
            applyView();

            saveChanges.setDisable(false);

            cancel.setVisible(false);
            cancel.setDisable(true);
            delete.setDisable(false);
            delete.setVisible(true);

            newSeance.setDisable(false);
            newSeance.setVisible(true);
            saveSeance.setVisible(false);
            saveSeance.setDisable(true);

            saveRecurring.setVisible(false);
            saveRecurring.setDisable(true);
            setRecurring.setDisable(false);
            setRecurring.setVisible(true);
        });

        delete.setOnAction(e -> {
            if(Confirmation.confirmDelete()){
                if(logic.delete(service)){
                    Confirmation.alertBox("Le service a bien été supprimé");
                    Controller.proView(pro);
                } else {
                    Confirmation.alertBox("ERREUR : le service n'a pas pu être supprimé.");
                }
            }
        });

        // On peuple le menu

        menuView.add(goBack, 0,0);
        if(fromAccount.startsWith("9")){
            menuView.add(saveChanges, 1,0);
            menuView.add(setRecurring, 2,0);
            menuView.add(saveSeance, 2,0);
            menuView.add(newSeance, 3, 0);
            menuView.add(saveRecurring, 3,0);
            menuView.add(delete,5,0);
            menuView.add(cancel, 5,0);
        }

        // Affichage de la récurrence.

        Text recurringText = new Text("Récurrence :");
        GridPane recurringGrid = new GridPane();
        recurringGrid.setPadding(new Insets(0,0,0,30));

        RowConstraints recuRow = new RowConstraints();
        recuRow.setPrefHeight(30);
        recuRow.setMinHeight(30);

        ColumnConstraints recuCol = new ColumnConstraints();
        recuCol.setHalignment(HPos.LEFT);

        recurringGrid.getColumnConstraints().add(recuCol);
        recurringGrid.getRowConstraints().addAll(recuRow,recuRow,recuRow,recuRow,recuRow,recuRow,recuRow,recuRow);

        CheckBox lundi = new CheckBox("Lundi");         Label wrapLundi = new Label();
        CheckBox mardi = new CheckBox("Mardi");         Label wrapMardi = new Label();
        CheckBox mercredi = new CheckBox("Mercredi");   Label wrapMercredi = new Label();
        CheckBox jeudi = new CheckBox("Jeudi");         Label wrapJeudi = new Label();
        CheckBox vendredi = new CheckBox("Vendredi");   Label wrapVendredi = new Label();
        CheckBox samedi = new CheckBox("Samedi");       Label wrapSamedi = new Label();
        CheckBox dimanche = new CheckBox("Dimanche");   Label wrapDimanche = new Label();
        lundi.setDisable(true);
        mardi.setDisable(true);
        mercredi.setDisable(true);
        jeudi.setDisable(true);
        vendredi.setDisable(true);
        samedi.setDisable(true);
        dimanche.setDisable(true);

        Label[] labels = {wrapLundi, wrapMardi, wrapMercredi, wrapJeudi, wrapVendredi, wrapSamedi, wrapDimanche};
        CheckBox[] checkBoxes = {lundi, mardi, mercredi, jeudi, vendredi, samedi, dimanche};
        addRecurTooltips(labels, checkBoxes, service);

        recurringGrid.addColumn(0, recurringText,lundi,mardi,mercredi,jeudi,vendredi,samedi,dimanche);
        recurringGrid.add(wrapLundi, 0,1);
        recurringGrid.add(wrapMardi,0,2);
        recurringGrid.add(wrapMercredi,0,3);
        recurringGrid.add(wrapJeudi,0,4);
        recurringGrid.add(wrapVendredi,0,5);
        recurringGrid.add(wrapSamedi,0,6);
        recurringGrid.add(wrapDimanche,0,7);

        // on ajoute recurringGrid à la vue de service
        GridPane serviceView = getServiceView();

        serviceView.add(recurringGrid,0,0,1,5);

        // on génère et applique la table
        tableSeance(logic, service, fromAccount);

    }

    private void addRecurTooltips(Label[] labels, CheckBox[] checkBoxes, Service service){
        Tooltip[] tooltips = {new Tooltip(), new Tooltip(),new Tooltip(),new Tooltip(),new Tooltip(),new Tooltip(),new Tooltip()};
        HashMap<String, ArrayList<LocalTime[]>> recurring = service.getRecurring();
        String[] keys = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        for(int i = 0; i < keys.length; ++i){
            // on en profite pour donner la taille des wrappers
            labels[i].setPrefSize(80,30);

            ArrayList<LocalTime[]> perDays;

            if(recurring.containsKey(keys[i].toLowerCase())) {

                checkBoxes[i].setSelected(true);
                perDays = recurring.get(keys[i].toLowerCase());
                String tooltip = keys[i] +"\nSeance 1 : " + perDays.get(0)[0].toString() + " à " + perDays.get(0)[1].toString() + "\n";

                for (int j = 1; j < perDays.size(); ++j) {
                    tooltip = tooltip.concat("Seance " + (j + 1) + ": " + perDays.get(j)[0].toString() + " à " + perDays.get(j)[1].toString() + "\n");
                }
                tooltips[i].setText(tooltip);
                tooltips[i].setShowDelay(Duration.ZERO);
                tooltips[i].setStyle("-fx-font-size : 12");
                labels[i].setTooltip(tooltips[i]);
            }
        }
    }

    private void tableSeance(Logic logic, Service service, String fromAccount){
        //todo :
        // Ça fait quelques fonctions que je copy/paste avec seulement quelques modifications mineures.
        // Il y a surement moyen de les généraliser et en faire des classes et augmenter la modularité.
        // Composition over inheritance?
        // Il faudrait éventuellement penser à la gestion des classes, pas juste les organiser dans un tas.
        ArrayList<String> seancesID = service.getSeanceList();

        // Fabriquer une liste des séances
        ObservableList<Seance> listSeances = FXCollections.observableArrayList();
        for (String id : seancesID) {
            listSeances.add(logic.getSeance(id));
        }

        // Construction et paramétrisation de la table pour visualiser les informations

        TableColumn<Seance, String> idColumn = new TableColumn<>("Seance #ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("SeanceID"));

        TableColumn<Seance, String> nomService = new TableColumn<>("Service");
        nomService.setCellValueFactory(new PropertyValueFactory<>("parentServiceName"));

        // Séparation pour date et heure
        TableColumn<Seance, LocalDateTime> tempsDebut = new TableColumn<>("Heure séance");
        tempsDebut.setCellValueFactory(new PropertyValueFactory<>("dateStart"));
        tempsDebut.setCellFactory(heure -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(LocalTime.from(item)));
                }
            }
        });

        TableColumn<Seance, LocalDateTime> jourDebut = new TableColumn<>("Jour séance");
        jourDebut.setCellValueFactory(new PropertyValueFactory<>("dateStart"));
        jourDebut.setCellFactory(heure -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty || item != null) {
                    setText(LocalDate.from(item).toString());
                }
            }
        });
        TableView<Seance> tableView = new TableView<>(listSeances);
        tableView.getColumns().addAll(idColumn, nomService, jourDebut, tempsDebut);

        // Styles
        idColumn.setStyle("-fx-alignment: CENTER;");
        nomService.setStyle("-fx-alignment: CENTER;");
        jourDebut.setStyle("-fx-alignment: CENTER;");
        tempsDebut.setStyle("-fx-alignment: CENTER;");

        // Double clic pour accéder aux séances

        tableView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                try {
                    int pos = tableView.getSelectionModel().getSelectedCells().get(0).getRow();
                    String id = idColumn.getCellData(pos);
                    if (fromAccount.startsWith("9")) {
                        Controller.seanceView(service.getServiceID(), id);
                    } else {
                        Controller.seanceView(fromAccount, id);
                    }
                } catch (Exception ignored) {
                    //
                }
            }
        });

        setTableView(tableView);
    }

    private boolean saveChanges(Logic logic, Service service){
        int capacityInt;
        int costInt;
        try {
            costInt = Integer.parseInt(getTextCost());
        } catch (Exception e) {
            Confirmation.alertBox("N'entrez que des chiffres dans le champ 'Coût' (max 100$).");
            return false;
        }
        try {
            capacityInt = Integer.parseInt(getTextCapacity());
        } catch (Exception e) {
            Confirmation.alertBox("N'entrez que des chiffres dans le champ 'Nombre de participants max'.");
            return false;
        }
        service.setCapacity(capacityInt);
        service.setCost(costInt);
        service.setServiceName(getTextServiceName());

        return logic.update(service);
    }


}
