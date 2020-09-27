package com.equipeDix.views;

import com.equipeDix.database.*;
import com.equipeDix.logic.Logic;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TabPane;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class NewSeanceView {
    private HashMap<String, ArrayList<LocalTime[]>> recurring;
    private final Logic logic;
    private final TabPane tabPane = new TabPane();
    private LocalDateTime seanceDateStart;
    private LocalDateTime seanceDateEnd;
    private Service service;
    private LocalDate adHocDate;
    private final HashMap<String, TabDay> tabDays = new HashMap<>();

    // Pour les nouveaux services sans #ID
    public NewSeanceView() {
        this.recurring = new HashMap<>();
        this.logic = null;

    }

    // Pour les modifications / ajouts à des services existants
    public NewSeanceView(Logic logic, Service service){
        this.service = service;
        this.logic = logic;
        this.recurring = service.getRecurring();

    }

    private TabPane adHocPane(DayOfWeek day){
        HashMap<DayOfWeek, String> traduction = new HashMap<>();
        traduction.put(DayOfWeek.MONDAY, "lundi");
        traduction.put(DayOfWeek.TUESDAY, "mardi");
        traduction.put(DayOfWeek.WEDNESDAY, "mercredi");
        traduction.put(DayOfWeek.THURSDAY, "jeudi");
        traduction.put(DayOfWeek.FRIDAY, "vendredi");
        traduction.put(DayOfWeek.SATURDAY, "samedi");
        traduction.put(DayOfWeek.SUNDAY, "dimanche");

        String jour = traduction.get(day);
        TabDay tabDay = new TabDay(jour, recurring.get(jour));
        tabDay.getTab().setClosable(false);
        tabPane.getTabs().clear();
        tabPane.getTabs().add(tabDay.getTab());
        tabDays.put(jour, tabDay);

        return tabPane;
    }

    private TabPane recurringPane(){
        // Un tab par jour

        tabDays.put("lundi", new TabDay("lundi", recurring.get("lundi")));
        tabDays.put("mardi", new TabDay("mardi", recurring.get("mardi")));
        tabDays.put("mercredi", new TabDay("mercredi", recurring.get("mercredi")));
        tabDays.put("jeudi", new TabDay("jeudi", recurring.get("jeudi")));
        tabDays.put("vendredi", new TabDay("vendredi", recurring.get("vendredi")));
        tabDays.put("samedi",new TabDay("samedi", recurring.get("samedi")));
        tabDays.put("dimanche", new TabDay("dimanche", recurring.get("dimanche")));

        tabPane.getTabs().addAll(tabDays.get("lundi").getTab(), tabDays.get("mardi").getTab(),
                tabDays.get("mercredi").getTab(), tabDays.get("jeudi").getTab(), tabDays.get("vendredi").getTab(),
                tabDays.get("samedi").getTab(),tabDays.get("dimanche").getTab());

        return tabPane;
    }

    private TilePane buttonPane(TabPane tabPane, boolean fromAdHoc){

        Button startTimeBtn = new Button("Début de la séance");
        Button endTimeBtn = new Button("Fin de la séance");
        Button clearBtn = new Button("Réinitialiser");

        clearBtn.setPrefSize(100,55);
        endTimeBtn.setPrefSize(100,55);
        startTimeBtn.setPrefSize(100,55);

        clearBtn.setWrapText(true);
        endTimeBtn.setWrapText(true);
        startTimeBtn.setWrapText(true);

        TilePane buttonPane = new TilePane();
        buttonPane.setPrefSize(120,410);
        buttonPane.setOrientation(Orientation.VERTICAL);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setVgap(30);
        buttonPane.setPadding(new Insets(30,0,0,0));
        buttonPane.getChildren().addAll(startTimeBtn, endTimeBtn, clearBtn);

        // Action des boutons
        final String[] jour = new String[1];
        LocalTime[] duration = new LocalTime[2];
        final boolean[] timeStartSelected = {false};
        startTimeBtn.setOnAction(event -> {

            try {
                timeStartSelected[0] = true;
                jour[0] = tabPane.getSelectionModel().getSelectedItem().getText();
                TabDay temp = tabDays.get(jour[0]);
                if(!temp.isSelectionActive()){
                    throw new Exception();
                }
                duration[0] = temp.getCurrentSelection();
                if(duration[0] == null){
                    throw new Exception();
                }
                startTimeBtn.setDisable(true);
                endTimeBtn.setDisable(false);
            } catch (Exception e) {
                Confirmation.alertBox("Il faut choisir une date et une heure de début avant de l'enregistrer");
            }
        });

        // Si on a déjà fait une sélection d'un temps de départ, et qu'on change de jour, on réinitialise.
        tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
            if(timeStartSelected[0]){
                duration[0] = null;
                startTimeBtn.setDisable(false);
                endTimeBtn.setDisable(true);
                timeStartSelected[0] = false;
            }

        });

        // EndTimeBtn

        endTimeBtn.setOnAction(e -> {

            if(jour[0].equals(tabPane.getSelectionModel().getSelectedItem().getText())) {
                TabDay temp = tabDays.get(jour[0]);
                duration[1] = temp.getCurrentSelection();
                if (checkEm(duration)) {
                    startTimeBtn.setDisable(false);
                    endTimeBtn.setDisable(true);
                    if (fromAdHoc) {
                        seanceDateStart = adHocDate.atTime(duration[0]);
                        seanceDateEnd = adHocDate.atTime(duration[1]);
                    } else {

                        if (recurring.containsKey(jour[0])) {
                            recurring.get(jour[0]).add(duration);

                        } else {
                            ArrayList<LocalTime[]> tempList = new ArrayList<>();
                            tempList.add(duration);
                            recurring.put(jour[0], tempList);

                        }
                    }
                }
            } else {
                Confirmation.alertBox("L'heure de fin et de début doivent se trouver dans la même journée");
            }
        });

        clearBtn.setOnAction(e -> {
            if(Confirmation.confirmation("Voulez-vous effacer tous les choix et recommencer?")) {
                // On efface le choix déjà fait
                duration[0] = null;
                try {
                    // si le service existe, on restaure la récurrence déjà enregistrée
                    if(logic.getService(service.getServiceID()).getRecurring() == null){
                        throw new Exception();
                    } else{
                        service.setRecurring(logic.getService(service.getServiceID()).getRecurring());
                    }

                } catch (Exception exception) {
                    // Sinon, on le remplace par une liste vide.
                    recurring = new HashMap<>();
                }
                // On réinitialise le tab
                String jours = tabPane.getSelectionModel().getSelectedItem().getText();
                TabDay temp = tabDays.get(jours);
                temp.clearSelection();

                // On remet les boutons à l'état initial
                startTimeBtn.setDisable(false);
                endTimeBtn.setDisable(true);
            }
        });

        return buttonPane;
    }


    private boolean checkEm(LocalTime[] duration) {
        // todo : faire la vérification que l'horaire du professionnel n'est pas en conflit entre ses services

        // Check 1. Temps fin > Temps début
        if (duration[1].isBefore(duration[0])) {
            Confirmation.alertBox("Erreur : L'heure de fin est avant l'heure de début");
            return false;

        // Check 2. Temps fin != Temps début
        } else if(duration[1].equals(duration[0])){
            Confirmation.alertBox("Erreur : L'heure de fin est identique à l'heure de début");
            return false;

        } else {
            // Demande de confirmation
            return Confirmation.confirmation("La séance sera de " + duration[0].toString() + " à "
                    + duration[1].toString() + ". Est-ce exact?");
        }
    }

    public HBox getAdHocView(){
        HBox root = new HBox();

        HBox tab = new HBox();

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate localDate, boolean b) {
                super.updateItem(localDate, b);
                LocalDate today = LocalDate.now();
                setDisable(b || localDate.compareTo(today) < 0);
            }
        });
        datePicker.valueProperty().addListener((observableValue, localDate, t1) -> {
            adHocDate = t1;
            showOnDatePick(tab, t1.getDayOfWeek());
        });

        root.setPrefSize(1024,425);
        tab.setPrefSize(300,410);
        DatePickerSkin dateSkin = new DatePickerSkin(datePicker);
        Node popup = dateSkin.getPopupContent();
        TilePane test = new TilePane();
        test.setMinHeight(410);
        test.setPrefWidth(1024);
        test.setOrientation(Orientation.HORIZONTAL);
        test.getChildren().addAll(buttonPane(tabPane, true),popup,tab);

        root.getChildren().addAll(test);

        return root;
    }

    private void showOnDatePick(HBox tab, DayOfWeek day){
        tab.getChildren().clear();
        tab.getChildren().add(adHocPane(day));
    }

    public HBox getRecurView(){
        HBox root = new HBox();
        HBox btn = new HBox();
        HBox tab = new HBox();

        btn.setPrefSize(150,410);
        tab.setPrefSize(874,410);
        btn.getChildren().add(buttonPane(tabPane, false));
        tab.getChildren().add(recurringPane());
        root.getChildren().addAll(btn, tab);

        return root;
    }

    protected LocalDateTime getSeanceDateStart() {
        return seanceDateStart;
    }

    protected LocalDateTime getSeanceDateEnd() {
        return seanceDateEnd;
    }

    protected HashMap<String, ArrayList<LocalTime[]>> getRecurring() {
        return recurring;
    }



}
