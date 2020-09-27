package com.equipeDix.views;

import com.equipeDix.CentreDeDonnees;
import com.equipeDix.database.*;
import com.equipeDix.logic.Logic;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static java.time.temporal.ChronoUnit.MINUTES;

public class SeanceView extends ServiceBasicsView{
    private final Logic logic;
    private TableView<AccntMember> table = new TableView<>();
    private final Text description = new Text();

    public SeanceView(Logic logic, String fromAccount, String seanceID){
        super();
        this.logic = logic;
        AccntMember[] membre = new AccntMember[1];

        // ATTENTION : "fromAccount" peut être un ServiceID.
        // NB :  #compte.length() = 9,   #service.length() = 7

        Seance seance = logic.getSeance(seanceID);
        AccntPro pro = (AccntPro) logic.getAccount(seance.getProID());
        Service service = logic.getService(seance.getParentServiceID());

        description.setText("");
        description.setStyle("-fx-font-size : 12pt;" +
                "-fx-text-alignment : center;");
        separatorPaddingSeance();

        boolean memberValid = true;
        boolean alreadyEnrolled = false;
        boolean fromService = false;
        boolean fromPro = false;
        if(fromAccount.length() < 9){
            fromService = true;

        } else {
            if(fromAccount.startsWith("9")){
                fromPro = true;
            } else {
                membre[0] = (AccntMember) logic.getAccount(fromAccount);
                if(membre[0].getSeanceInscr().contains(seanceID)){
                    alreadyEnrolled = true;
                }
                if(membre[0].getValidUntil().isBefore(LocalDate.now())){
                    memberValid = false;
                }
            }
        }

        // On affiche les membres inscrits (ou qui ont participés si la séance est terminée)
        if(fromService || fromPro){
                enrolledMemberView(seance);
        }

        boolean finalFromService = fromService;

        setFieldEditable(false);

        // on peuple les champs existants
        setTextIdNumber(seance.getSeanceID());
        setTextProName(pro.getPrenom() +" "+pro.getNom());
        setTextCost(Integer.toString(service.getCost()));
        setTextCapacity(Integer.toString(seance.getCapacity()));
        setTextCreatedOn(LocalDate.from(seance.getDateCreated()).toString());
        setTextTooltipCreatedOn("à " + LocalTime.from(seance.getDateCreated()).toString().substring(0,8));
        setTextServiceName(service.getServiceName());

        // nouveaux champs
        Text onDay = new Text();
        Text dispo = new Text();
        Text dispoText = new Text("Places restantes : ");
        Text onDayText = new Text("A lieu le : ");
        Text durationText = new Text();

        int dispoInt = seance.getCapacity() - seance.getEnrolledMembers().size();
        dispo.setText("   " + dispoInt);
        onDay.setText(" "+LocalDate.from(seance.getDateStart()).toString() +  " à " +
                LocalTime.from(seance.getDateStart()).toString());

        long durationTime = LocalTime.from(seance.getDateStart()).until(LocalTime.from(seance.getDateEnd()), MINUTES);
        durationText.setText("Durée : "+ durationTime +" minutes");

        // Boutons du menu

        GymButton goBack = new GymButton("Retour");
        GymButton saveChanges = new GymButton("Modifier des informations");
        GymButton dateChanges = new GymButton("Changer date / heure");
        GymButton enroll = new GymButton("S'inscrire à cette séance");
        GymButton delete = new GymButton("Supprimer séance");

        // action des boutons

        boolean finalFromPro = fromPro;
        goBack.setOnAction(e -> {
            if(finalFromPro){
                Controller.proView(pro);
            } else if(finalFromService){
                Controller.serviceView(pro.getId(), fromAccount);
            } else {
                Controller.serviceView(fromAccount, seance.getParentServiceID());
            }
        });

        final boolean[] modifyMode = {false};

        saveChanges.setOnAction(e -> {
            if(modifyMode[0]) {
                if (saveChanges(logic, seance)) {
                    modifyMode[0] = false;
                    saveChanges.setText("Modifier des informations");
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

        dateChanges.setOnAction(e-> Confirmation.alertBox("Pas implémenté :("));

        enroll.setOnAction(e -> {
            if(logic.memberRegisterToSeance(membre[0], seance)){
                if(CentreDeDonnees.payment()){
                    Controller.memberView(membre[0]);
                }
            } else {
                Confirmation.alertBox("Erreur : problème lors de l'enregistrement.");
            }

        });

        delete.setOnAction(e -> {
            if(Confirmation.confirmDelete()){
                if(logic.delete(seance)){
                    Confirmation.alertBox("La séance a bien été supprimée");
                    Controller.serviceView(service.getProID(), service.getServiceID());
                } else {
                    Confirmation.alertBox("ERREUR : la séance n'a pas pu être supprimée.");
                }
            }
        });

        GridPane view = getServiceView();
        view.add(dispoText, 4, 3);
        view.add(dispo,5,3);
        view.add(onDayText,1,3);
        view.add(onDay,2,3);
        view.add(durationText,0,3);
        view.add(description, 0,4,5,1);

        GridPane menu = getMenuView();
        menu.add(goBack,0,0);

        if(fromPro || finalFromService){
            menu.add(saveChanges, 2,0);
            menu.add(dateChanges, 1,0);
            menu.add(delete,5,0);

        } else {
            menu.add(enroll, 2,0);

        }

        // on bloque le bouton d'inscription dans ces cas :
        final boolean isOver = seance.getDateEnd().isBefore(LocalDateTime.now());

        Label enrollWrapper = new Label();
        enrollWrapper.setPrefSize(100,55);
        if(alreadyEnrolled || !(memberValid) || isOver){
            enroll.setDisable(true);
            menu.add(enrollWrapper,2,0);
            Tooltip enrollToolTip = new Tooltip();
            enrollToolTip.setShowDelay(Duration.ZERO);
            if(alreadyEnrolled) {
                enrollToolTip.setText("Le membre est déjà inscrit à cette séance.");
            } else if(isOver){
                enrollToolTip.setText("La séance est passée.");
            } else {
                enrollToolTip.setText("Le compte n'est pas en règle.");
            }
            enrollWrapper.setTooltip(enrollToolTip);
        }
    }

    private void enrolledMemberView(Seance seance){

        boolean isOver = seance.getDateEnd().isBefore(LocalDateTime.now());
        ArrayList<String> enrolled = seance.getEnrolledMembers();
        ArrayList<String> attended = new ArrayList<>(seance.getAttendedMembers().keySet());

        if(isOver) {
            table = logic.buildTableViewMembers(logic, attended);
            table.getColumns().remove(3);
            description.setText("Liste des membres ayant participé à l'activité");
        } else {
            table = logic.buildTableViewMembers(logic, enrolled);
            description.setText("Liste des membres inscrits à l'activité");
        }

    }

    private boolean saveChanges(Logic logic, Seance seance){
        int capacityInt;

        // todo : veut-on permettre de changer le coût de séances individuelles?
        /*int costInt;
        try {
            costInt = Integer.parseInt(getTextCost());
        } catch (Exception e) {
            CentreDeDonnees.alertBox("N'entrez que des chiffres dans le champ 'Coût' (max 100$).");
            return false;
        }*/
        try {
            capacityInt = Integer.parseInt(getTextCapacity());
        } catch (Exception e) {
            Confirmation.alertBox("N'entrez que des chiffres dans le champ 'Nombre de participants max'.");
            return false;
        }
        seance.setCapacity(capacityInt);
        //seance.setCost(costInt);

        return logic.update(seance);
    }

    @Override
    public void applyView(){
        super.applyView();
        CentreDeDonnees.bottomView.getChildren().clear();
        CentreDeDonnees.bottomView.getChildren().add(table);
    }

}
