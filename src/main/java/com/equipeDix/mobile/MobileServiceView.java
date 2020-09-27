package com.equipeDix.mobile;

import com.equipeDix.database.AccntPro;
import com.equipeDix.database.Seance;
import com.equipeDix.database.Service;
import com.equipeDix.logic.Logic;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

import java.util.Scanner;

public class MobileServiceView {

    private TableView<Seance> seanceTable;
    private final Service service;
    private final String fromAccount;
    private final GridPane serviceInfos = new GridPane();
    private final String serviceID;
    private Button goBack;
    private final Logic logic;
    public MobileServiceView(Logic logic, String fromAccount, String serviceID){
        this.logic = logic;
        this.serviceID = serviceID;
        this.fromAccount = fromAccount;

        service = logic.getService(serviceID);

        AccntPro pro = (AccntPro) logic.getAccount(service.getProID());

        goBack = new Button("Retour");
        goBack.setPrefSize(130,33);
        goBack.setOnAction( e -> MobileViewController.repertoireDesServices(fromAccount));

        // Description des champs
        Text idText = new Text("# ID :  ");
        Text serviceNameText = new Text("Description :  ");
        Text proNameText = new Text("Fourni par :  ");
        Text costText = new Text("Coût par séance :  ");
        Text capacityText = new Text("Max participants par séance :  ");
        capacityText.setWrappingWidth(180);

        // Remplissage des champs

        Text idNumber = new Text(serviceID);
        Text serviceName = new Text(service.getServiceName());
        Text proName = new Text(pro.getPrenom() +" " +pro.getNom());
        Text cost = new Text(service.getCost()+" $");
        Text capacity = new Text(service.getCapacity() +"");

        // Contraintes
        ColumnConstraints col = new ColumnConstraints();
        col.setHalignment(HPos.CENTER);
        col.setHgrow(Priority.ALWAYS);

        serviceInfos.getColumnConstraints().addAll(col, col);

        RowConstraints rows = new RowConstraints();
        rows.setValignment(VPos.CENTER);
        rows.setMaxHeight(66);
        rows.vgrowProperty().setValue(Priority.ALWAYS);
        serviceInfos.getRowConstraints().addAll(rows,rows,rows,rows,rows,rows);
        serviceInfos.setMinWidth(440);
        // Ajout des items au GridPane
        serviceInfos.add(goBack,0,0,2,1);
        serviceInfos.addColumn(0, idText, serviceNameText, proNameText, costText, capacityText);
        serviceInfos.addColumn(1, idNumber, serviceName, proName, cost, capacity);
        consoleItou();
    }

    protected void fetchTable(){
        // Table des services
        seanceTable = logic.buildTableViewSeance(logic, service.getSeanceList());

        // Double clic pour accéder aux séances

        seanceTable.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                try {
                    int pos = seanceTable.getSelectionModel().getSelectedCells().get(0).getRow();
                    String seanceID = (String) seanceTable.getColumns().get(0).getCellData(pos);
                    MobileViewController.seanceView(fromAccount, serviceID, seanceID);

                } catch (Exception e) {
                    System.out.println("PROBLEM");
                }
            }
        });
    }

    public Button getGoBack() {
        return goBack;
    }

    public void setGoBack(Button goBack) {
        this.goBack = goBack;
    }

    public GridPane getServiceInfos() {
        return serviceInfos;
    }

    public void applyView(){
        fetchTable();

        MobileApp.root.getChildren().clear();
        MobileApp.root.add(serviceInfos,0,0,1,2);
        MobileApp.root.add(seanceTable,0,2,1,2);
    }

    public void consoleItou(){
        Runnable console = this::runConsole;

        Thread backgroundThread = new Thread(console);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public void runConsole(){
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Entrez le ID de la séance que vous désirez consulter.\nEntrez 'retour' pour retourner au menu précédent");
            String seanceIDconsole = scanner.nextLine();
            if(seanceIDconsole.toLowerCase().equals("retour")){
                Thread.sleep(500);

                Platform.runLater(goBack::fire);

            } else {
                try {
                    seanceIDconsole = String.format("%07d", Integer.parseInt(seanceIDconsole));
                } catch (Exception e) {
                    System.out.println("Entrez un ID valide");
                    consoleItou();
                }
                Thread.sleep(500);
                String finalSeanceIDconsole = seanceIDconsole;
                Platform.runLater(() -> MobileViewController.seanceView(fromAccount, serviceID, finalSeanceIDconsole));
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
