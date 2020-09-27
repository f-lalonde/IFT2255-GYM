package com.equipeDix.views;

import com.equipeDix.CentreDeDonnees;
import com.equipeDix.database.*;
import com.equipeDix.logic.BuildTableViews;
import com.equipeDix.logic.Logic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;

public class ProView extends AccountView  {
    final TabPane tabPane = new TabPane();

    public ProView(Logic logic, AccntPro pro) {
        super(logic, pro);
        setTextID(pro.getId());

        // Boutons et champs exclusifs aux professionnels
        GymButton ajouterService = new GymButton("Ajouter un service");

        ajouterService.setOnAction(e -> Controller.newServiceView(pro.getId()));

        // Mise à jour de la vue
        GridPane accountView = getAccountView();
        GridPane menuView = getMenuView();
        menuView.add(ajouterService,2,0);

        tabPane.getTabs().addAll(tableService(logic,pro), tableSeance(pro));
        setAccountView(accountView);
        setMenuView(menuView);
        //
    }



    private Tab tableService(Logic logic, AccntPro pro) {

        ArrayList<String> servicesID = pro.getServicesIndex();

        TableView<Service> tableView = logic.buildTableViewService(logic, servicesID);

        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setText("Liste des Services");
        tab.setContent(tableView);

        tableView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                try {
                    int pos = tableView.getSelectionModel().getSelectedCells().get(0).getRow();
                    String id = (String) tableView.getColumns().get(0).getCellData(pos);
                    Controller.serviceView(pro.getId(), id);
                } catch (Exception ignored) {
                    //
                }
            }
        });

        return tab;
    }

    private Tab tableSeance(AccntPro pro){

        ArrayList<String> seancesID = pro.getSeancesIndex();

        TableView<Seance> tableView = logic.buildTableViewSeance(logic, seancesID);

        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setText("Liste des Séances");
        tab.setContent(tableView);

        // Double clic pour accéder aux séances

        tableView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                try {
                    int pos = tableView.getSelectionModel().getSelectedCells().get(0).getRow();
                    String id = (String) tableView.getColumns().get(0).getCellData(pos);
                    Controller.seanceView(pro.getId(), id);
                } catch (Exception ignored) {
                    //
                }
            }
        });
        return tab;
    }

    @Override
    public void applyView() {
        super.applyView();
        CentreDeDonnees.bottomView.getChildren().clear();
        CentreDeDonnees.bottomView.getChildren().add(tabPane);
    }
}
