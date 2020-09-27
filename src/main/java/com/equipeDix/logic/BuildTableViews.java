package com.equipeDix.logic;

import com.equipeDix.database.AccntMember;
import com.equipeDix.database.AccntPro;
import com.equipeDix.database.Seance;
import com.equipeDix.database.Service;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuildTableViews {

    private BuildTableViews() {

    }

    // todo : BUG ÉTRANGE. Si je change certaines methodes dans la classe Seance(i.e. notamment corriger l'erreur
    //  de demander "int memberID" dans la méthode removeEnrolledMember, ou bien d'enlever des retour de booléens
    //  inutiles), alors buildTableViewSeance() provoque un NullPointerException. À investiguer. Pour l'instant,
    //  on passe deux Parse tout à fait inutile afin de faire fonctionner la fonction removeEnrolledMember.

    private static TableCell<Seance, LocalDateTime> callDay(TableColumn<Seance, LocalDateTime> jour) {
        return new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(String.valueOf(LocalDate.from(item)));
                }
            }
        };
    }

    private static TableCell<Seance, LocalDateTime> callTime(TableColumn<Seance, LocalDateTime> heure) {
        return new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText("");
                } else {
                    setText(String.valueOf(LocalTime.from(item)));
                }
            }
        };
    }

    public static TableView<Seance> buildTableViewSeance(Logic logic, ArrayList<String> seancesID) {

        // Fabriquer une liste des séances
        ObservableList<Seance> listSeances = FXCollections.observableArrayList();
        for (String s : seancesID) {
            listSeances.add(logic.getSeance(s));
        }

        // Construction et paramétrisation de la table pour visualiser les informations

        TableColumn<Seance, String> idColumn = new TableColumn<>("Seance #ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("seanceID"));

        TableColumn<Seance, String> nomService = new TableColumn<>("Service");
        nomService.setCellValueFactory(new PropertyValueFactory<>("parentServiceName"));

        // Séparation pour date et heure
        TableColumn<Seance, LocalDateTime> tempsDebut = new TableColumn<>("Heure séance");
        tempsDebut.setCellValueFactory(new PropertyValueFactory<>("dateStart"));
        tempsDebut.setCellFactory(BuildTableViews::callTime);

        TableColumn<Seance, LocalDateTime> jourDebut = new TableColumn<>("Jour séance");
        jourDebut.setCellValueFactory(new PropertyValueFactory<>("dateEnd"));
        jourDebut.setCellFactory(BuildTableViews::callDay);

        TableView<Seance> tableView = new TableView<>(listSeances);
        tableView.getColumns().addAll(idColumn, nomService, tempsDebut, jourDebut);

        // Styles
        idColumn.setStyle("-fx-alignment: CENTER;");
        nomService.setStyle("-fx-alignment: CENTER;");
        tempsDebut.setStyle("-fx-alignment: CENTER;");
        jourDebut.setStyle("-fx-alignment: CENTER;");

        return tableView;
    }

    public static TableView<Service> buildTableViewService(Logic logic, ArrayList<String> servicesID) {
        // Fabriquer une liste des séances
        ObservableList<Service> listServices = FXCollections.observableArrayList();
        for (String id : servicesID) {
            listServices.add(logic.getService(id));
        }

        // Construction et paramétrisation de la table pour visualiser les informations

        TableColumn<Service, String> idColumn = new TableColumn<>("Service #ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ServiceID"));

        TableColumn<Service, String> nomService = new TableColumn<>("Service");
        nomService.setCellValueFactory(new PropertyValueFactory<>("ServiceName"));

        //Styles
        idColumn.setStyle("-fx-alignment: CENTER;");
        nomService.setStyle("-fx-alignment: CENTER;");

        TableView<Service> tableView = new TableView<>(listServices);
        tableView.getColumns().addAll(idColumn, nomService);


        return tableView;
    }

    public static TableView<AccntMember> buildTableViewMembers(Logic logic, ArrayList<String> memberID) {
        ObservableList<AccntMember> listMembers = FXCollections.observableArrayList();
        for (String id : memberID) {
            listMembers.add((AccntMember) logic.getAccount(id));
        }

        TableColumn<AccntMember, String> firstNameColumn = new TableColumn<>("Prénom");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableColumn<AccntMember, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<AccntMember, String> telColumn = new TableColumn<>("Téléphone");
        telColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        TableColumn<AccntMember, String> validAccount = new TableColumn<>("Valide?");
        validAccount.setCellValueFactory(entry -> {

            if (entry.getValue().getValidUntil().isBefore(LocalDate.now())) {
                return new SimpleStringProperty("Non");
            } else {
                return new SimpleStringProperty("Oui");
            }

        });

        // styles
        firstNameColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setStyle("-fx-alignment: CENTER;");
        telColumn.setStyle("-fx-alignment: CENTER;");
        validAccount.setStyle("-fx-alignment: CENTER;");

        TableView<AccntMember> tableView = new TableView<>(listMembers);
        tableView.getColumns().addAll(firstNameColumn, nameColumn, telColumn, validAccount);

        // On surligne les membres non valides en rouge.
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(AccntMember accntMember, boolean b) {
                super.updateItem(accntMember, b);
                if (accntMember == null) {
                    setStyle("");
                } else if (validAccount.getCellObservableValue(accntMember).getValue().equals("Non")) {
                    setStyle("-fx-background-color : firebrick;" +
                            "-fx-text-fill : #C7C7C7;");
                }
            }
        });

        return tableView;
    }

    public static TableView<Map.Entry<String, String>> buildTableRepertoireServices(Logic logic) {
        HashMap<String, String> repertoire = logic.getServiceNameList();
        ObservableList<Map.Entry<String, String>> listServices = FXCollections.observableArrayList();

        listServices.addAll(repertoire.entrySet());

        TableColumn<Map.Entry<String, String>, String> listID = new TableColumn<>("ID");
        listID.setCellValueFactory(entry -> new SimpleStringProperty(entry.getValue().getKey()));

        TableColumn<Map.Entry<String, String>, String> nomService = new TableColumn<>("Service");
        nomService.setCellValueFactory(entry -> new SimpleStringProperty(entry.getValue().getValue()));

        TableColumn<Map.Entry<String, String>, String> nomPro = new TableColumn<>("Professionnel");
        nomPro.setCellValueFactory(entry -> {
            String serviceId = entry.getValue().getKey();
            Service service = logic.getService(serviceId);
            String proId = service.getProID();
            AccntPro pro = (AccntPro) logic.getAccount(proId);
            return new SimpleStringProperty(pro.getPrenom() + " " + pro.getNom());
        });

        TableView<Map.Entry<String, String>> tableView = new TableView<>(listServices);
        tableView.getColumns().addAll(listID, nomService, nomPro);
        return tableView;
    }

}
