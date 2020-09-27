package com.equipeDix.views;

import javafx.application.Platform;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import java.time.LocalTime;
import java.util.ArrayList;

public class TabDay {
    private final Tab tab;
    private LocalTime currentSelection;
    private final ListView<LocalTime> am = new ListView<>();
    private final ListView<LocalTime> pm = new ListView<>();
    private boolean selectionActive = false;

    public TabDay(String jour, ArrayList<LocalTime[]> recurring){
        tab = new Tab(jour);

        // Disposition des tabs

        HBox root = new HBox();
        HBox amBox = new HBox();
        HBox pmBox = new HBox();

        amBox.setPrefSize(310, 380);
        pmBox.setPrefSize(310,380);

        // Liste des heures

        for(int i = 7; i < 12; ++i){
            am.getItems().add(LocalTime.of(i,0));
            am.getItems().add(LocalTime.of(i,30));
        }
        for(int i = 12; i<20; ++i){
            pm.getItems().add(LocalTime.of(i,0));
            pm.getItems().add(LocalTime.of(i,30));
        }

        // on ne peut pas sélectioner des périodes de temps déjà allouées à d'autres activités
        if(recurring != null){
            disableUnavailable(am, recurring);
            disableUnavailable(pm, recurring);
        }

        am.setEditable(false);
        pm.setEditable(false);

        // on s'assure qu'un seul élément peut être sélectionné au travers des deux listes.

        final boolean[] listenerIsActive = {true}; // prévient le listener de s'activer sur le clearSelection().

        am.getSelectionModel().selectedItemProperty().addListener((observableValue, localTime, t1) -> {
            if(listenerIsActive[0]){
                selectionActive = t1 != null;
                listenerIsActive[0] = false;
                Platform.runLater(() -> {
                    pm.getSelectionModel().clearSelection();
                    setCurrentSelection(t1);
                    listenerIsActive[0] = true;

                });
            }

        });

        pm.getSelectionModel().selectedItemProperty().addListener((observableValue, localTime, t1) -> {
            if(listenerIsActive[0]){
                selectionActive = t1 != null;
                listenerIsActive[0] = false;
                Platform.runLater(() -> {
                    am.getSelectionModel().clearSelection();
                    setCurrentSelection(t1);
                    listenerIsActive[0] = true;


                });
            }
        });

        // On place les objets dans les boîtes
        amBox.getChildren().add(am);
        pmBox.getChildren().add(pm);
        root.getChildren().addAll(amBox, pmBox);
        tab.setContent(root);
        tab.setClosable(false);

    }

    public Tab getTab() {

        return tab;
    }

    public LocalTime getCurrentSelection() {
        return currentSelection;
    }

    private void disableUnavailable(ListView<LocalTime> listView, ArrayList<LocalTime[]> recurring){
        //todo : éventuellement, appliquer ça à tous les moments où une séance existe pour un professionnel donné
        // (lors de la création de séance ad hoc, pas lors de la modification de la récurrence)
        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<LocalTime> call(ListView<LocalTime> localTimeListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(LocalTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item != null && !empty){
                            setText(item.toString());

                            for (LocalTime[] localTimes : recurring) {
                                if(item.isBefore(localTimes[1]) && item.isAfter(localTimes[0]) ||
                                        item.equals(localTimes[0]) || item.equals(localTimes[1])){
                                    setDisable(true);
                                    setStyle("-fx-background-color : lightgray; " +
                                            "-fx-text-fill : firebrick; " +
                                            "-fx-font-weight : bold");
                                }
                            }
                        }
                    }
                };
            }
        });
    }

    public void clearSelection(){
        am.getSelectionModel().clearSelection();
        pm.getSelectionModel().clearSelection();
    }

    private void setCurrentSelection(LocalTime currentSelection) {
        this.currentSelection = currentSelection;
    }

    public boolean isSelectionActive() {
        return selectionActive;
    }

}

