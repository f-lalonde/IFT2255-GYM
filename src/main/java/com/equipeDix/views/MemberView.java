package com.equipeDix.views;

import com.equipeDix.logic.BuildTableViews;
import com.equipeDix.CentreDeDonnees;
import com.equipeDix.database.*;
import com.equipeDix.logic.Logic;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.util.*;

public class MemberView extends AccountView {

    private final AccntMember member;
    private final TextField validUntil;

    public MemberView(Logic logic, AccntMember member) {
        super(logic, member);
        this.member = member;
        setTextID(member.getId());

        // Boutons et champs exclusifs aux membres
        Text validUntilText = new Text("Valide jusqu'au :  ");
        validUntilText.setStyle("-fx-font: 12 system;");

        validUntil = new TextField(member.getValidUntil().toString());
        validUntil.setDisable(true);
        validUntil.setMaxWidth(100);
        validUntil.setPrefSize(100, 25);

        setValidColorBorder();
        GymButton repertoireService = new GymButton("Répertoire des services");
        GymButton renew = new GymButton("Renouveller l'abonnement");

        // action des boutons
        repertoireService.setOnAction(event -> repertoireServices(logic,member));

        renew.setOnAction(event -> {
            if(CentreDeDonnees.payment()) {
                Confirmation.alertBox("Le paiement a réussi!");
                member.setValidUntil(member.getValidUntil().plusMonths(1));
                logic.update(member);
                validUntil.setText(member.getValidUntil().toString());
                setValidColorBorder();
            } else {
                Confirmation.alertBox("Le paiement a échoué :(");
            }
        });

        // Mise à jour de la vue
        GridPane accountView = getAccountView();
        GridPane menuView = getMenuView();
        menuView.add(repertoireService,2,0);
        menuView.add(renew, 3,0);
        accountView.add(validUntilText, 1,3);
        accountView.add(validUntil, 2,3);

        tableSeance(logic, member);
        setAccountView(accountView);
        setMenuView(menuView);
        //
    }

    private void repertoireServices(Logic logic, AccntMember membre) {

        TableView<Map.Entry<String,String>> tableView = logic.buildTableRepertoireServices(logic);
        // Double clic pour accéder aux services

        tableView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                try {
                    int pos = tableView.getSelectionModel().getSelectedCells().get(0).getRow();
                    String id = (String) tableView.getColumns().get(0).getCellData(pos);
                    Controller.serviceView(membre.getId(), id);
                } catch (Exception ignored) {
                    //
                }
            }
        });
        setTableView(tableView);
        applyView();
    }

    private void tableSeance(Logic logic, AccntMember member) {

        ArrayList<String> seancesID = member.getSeanceInscr();

        TableView<Seance> tableView = logic.buildTableViewSeance(logic, seancesID);

        // Colonne de confirmation de présence par l'agent //todo à transférer au proview (mobile seulement?)

        TableColumn<Seance, Seance> confirmAttendance = new TableColumn<>("Confirmer la présence");
        confirmAttendance.setCellValueFactory(conf -> new ReadOnlyObjectWrapper<>(conf.getValue()));
        confirmAttendance.setCellFactory(seanceVoidTableColumn -> new TableCell<>() {
            private final Button confirm = new Button("Confirmer");

            @Override
            protected void updateItem(Seance seance, boolean b) {
                super.updateItem(seance, b);

                if(seance != null){
                    if(!LocalDate.from(seance.getDateStart()).equals(LocalDate.now())){
                        confirm.setDisable(true);
                        if(LocalDate.from(seance.getDateStart()).isBefore(LocalDate.now())){
                            confirm.setText("Manquée");
                        } else {
                            confirm.setText("À venir");
                        }

                    }

                    setGraphic(confirm);
                    confirm.setOnAction(e-> {
                        if(logic.confirmMemberAttendance(member,seance.getSeanceID())){
                            confirm.setDisable(true);
                        } else {
                            Confirmation.alertBox("Une erreur est survenue.");
                        }

                    });
                } else {
                    setGraphic(null);
                }
            }
        });

        tableView.getColumns().add(confirmAttendance);

        confirmAttendance.setStyle("-fx-alignment: CENTER;");

        setTableView(tableView);
    }

    private void setValidColorBorder(){
        if(member.getValidUntil().isAfter(LocalDate.now())){
            validUntil.setStyle("-fx-border-color : green;" +
                    "-fx-border-width : 5px");
        } else if(member.getValidUntil().equals(LocalDate.now())){
            validUntil.setStyle("-fx-border-color : orange;" +
                    "-fx-border-width : 5px");
        } else {
            validUntil.setStyle("-fx-border-color : crimson;" +
                    "-fx-border-width : 5px");
        }
    }
}
