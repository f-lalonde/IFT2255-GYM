package com.equipeDix.logic;

import com.equipeDix.database.*;
import com.equipeDix.views.Confirmation;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Logic {
    final Database database = new Database();

    public Logic() {

    }

    public boolean delete(Account account){
        database.removeEmailToID(account.getEmail(), account.getId());
        return database.removeAccnt(account.getId());
    }

    public boolean delete(Service service){
        ArrayList<String> seanceList = service.getSeanceList();
        AccntPro pro = (AccntPro) database.getAccnt(service.getProID());

        for(String seanceID : seanceList){
            Seance seance = database.getSeance(seanceID);
            pro.removeSeance(seanceID);
            delete(seance);
        }

        pro.removeService(service.getServiceID());
        database.updateAcct(pro.getId(), pro);
        return database.removeService(service.getServiceID());
    }

    public boolean delete(Seance seance){
        ArrayList<String> temp = seance.getEnrolledMembers();
        // on enlève la séance des séances auxquels les membres sont inscrit
        // todo : il faudrait les notifier d'une façon ou d'une autre
        for(String memberID : temp){
            AccntMember member = (AccntMember) database.getAccnt(memberID);
            member.removeSeance(seance.getSeanceID());
            database.updateAcct(memberID, member);
        }

        // on enlève la séance du compte du pro
        AccntPro pro = (AccntPro) getAccount(seance.getProID());
        pro.removeSeance(seance.getSeanceID());
        database.updateAcct(pro.getId(), pro);

        // on enlève la séance de la liste des séances du service parent
        Service service = getService(seance.getParentServiceID());
        service.removeSeance(seance.getSeanceID());
        database.updateService(service.getServiceID(), service);

        return database.removeSeance(seance.getSeanceID());
    }

    public boolean update(Account account){
        String oldEmail = database.getAccnt(account.getId()).getEmail();
        database.updateEmailToID(oldEmail, account.getEmail(), account.getId());
        return database.updateAcct(account.getId(), account);
    }

    public boolean update(Service service){
        return database.updateService(service.getServiceID(), service);
    }

    public boolean update(Seance seance){
        return database.updateSeance(seance.getSeanceID(), seance);
    }

    /**
     *
     * @param choix int : fournir 1 si compte membre, 2 si compte pro
     * @return numéro du membre sous forme de string
     */
    public String createAccount(int choix, String firstName, String lastName, String[] address, String telephone, String email){
        switch (choix){
            case 1 :
                String memberID = String.format("%09d", database.getLastID("membre") + 1);
                AccntMember memberAccnt = new AccntMember(firstName, lastName, address, telephone, email, memberID);
                if(database.addAccnt(memberID, memberAccnt)) {
                    database.incLastID("membre");
                    database.addEmail(email, memberID);

                    return memberID;
                } else {
                    return null;
                }

            case 2 :
                String proID = Integer.toString(database.getLastID("pro") + 1);
                AccntPro proAccnt = new AccntPro(firstName, lastName, address, telephone, email, proID);
                if(database.addAccnt(proID, proAccnt)) {
                    database.incLastID("pro");
                    database.addEmail(email, proID);
                    return proID;
                } else {
                    return null;
                }

            default:
                return null;
        }

    }

    public boolean isEmailAlreadyUsed(String email){
        return database.isEmailInList(email);
    }

    public Service createService(String[] infos, AccntPro pro, LocalDate start, HashMap<String, ArrayList<LocalTime[]>> recurring){

        int capacity = Integer.parseInt(infos[1]);
        int cost = Integer.parseInt(infos[2]);
        if(capacity > 30){
            Confirmation.alertBox("Maximum 30 participants / séance");
            return null;
        }
        if(cost > 100){
            Confirmation.alertBox("Maximum 100$ / séance");
            return null;
        }
        String serviceID = String.format("%07d",database.getLastID("service") + 1);
        Service service = new Service(infos[0], start, recurring, capacity, pro.getId(), serviceID, cost);
        boolean check = database.addService(serviceID, service);

        if(check){
            database.updateServiceNameList();
            database.incLastID("service");
            pro.addService(serviceID);
            return service;

        } else {
            return null;
        }
    }

    public boolean createSeance(LocalDateTime dateStart, LocalDateTime dateEnd, int capacity, Service parent){

        //todo : voir database, i.e. il faut prévoir quoi faire quand on va dépasser 100 séances, par exemple.
        int parentPart = Integer.parseInt(parent.getServiceID()) * 10000;
        int proPart = Integer.parseInt(parent.getProID().substring(7,9));
        int seancePart = (database.getLastID(parent.getServiceID()) + 1) * 100;

        String seanceID = String.format("%07d", (parentPart + proPart + seancePart));
        AccntPro pro = (AccntPro) database.getAccnt(parent.getProID());
        Seance seance = new Seance(seanceID, dateStart, dateEnd, capacity, parent);
        parent.addSeance(seanceID);
        pro.addSeance(seanceID);
        database.updateAcct(pro.getId(), pro);
        database.updateService(parent.getServiceID(), parent);
        database.incLastID(parent.getServiceID());
        return database.addSeance(seanceID, seance);
    }

    public Account getAccount(String id){
        return database.getAccnt(id);
    }

    public Service getService(String serviceID){
        return database.getService(serviceID);
    }

    public Seance getSeance(String seanceID){
        return database.getSeance(seanceID);
    }

    public HashMap<String, String> getServiceNameList(){
        return database.getServiceNameList();
    }

    public Account getAccountFromEmail(String email){
        return database.getAccnt(database.getIDfromEmail(email));
    }

    public boolean memberRegisterToSeance(AccntMember membre, Seance seance){
        seance.addEnrolledMember(membre.getId());
        boolean check = database.updateSeance(seance.getSeanceID(), seance);
        if(check){
            membre.addSeance(seance.getSeanceID());
            return database.updateAcct(membre.getId(), membre);
        } else {
            return false;
        }
    }

    public boolean confirmMemberAttendance(AccntMember membre, String seanceID){
        Seance seance = database.getSeance(seanceID);
        LocalDate checkDate = LocalDate.from(seance.getDateStart());

        // On ne peut être confirmé que la journée même de l'activité.
        if(checkDate.isEqual(LocalDate.now())){
            if(seance.getEnrolledMembers().contains(membre.getId()) &&
            membre.getSeanceInscr().contains(seanceID)){
                database.createSTR(seance, membre.getId());
                seance.addAttendedMember(membre.getId());
                seance.removeEnrolledMember(membre.getId());
                membre.getSeanceInscr().remove(seanceID);
            } else {
                return false;
            }

        } else {
            return false;
        }
        database.updateAcct(membre.getId(), membre);

        return database.updateSeance(seanceID, seance);
    }

    public TableView<Seance> buildTableViewSeance(Logic logic, ArrayList<String> seancesID){
        return BuildTableViews.buildTableViewSeance(logic, seancesID);
    }

    public TableView<Service> buildTableViewService(Logic logic, ArrayList<String> servicesID) {
        return BuildTableViews.buildTableViewService(logic, servicesID);
    }

    public TableView<AccntMember> buildTableViewMembers(Logic logic, ArrayList<String> memberID) {
        return BuildTableViews.buildTableViewMembers(logic, memberID);
    }

    public TableView<Map.Entry<String, String>> buildTableRepertoireServices(Logic logic){
        return BuildTableViews.buildTableRepertoireServices(logic);
    }

    public void requestStoreData(){
        database.storeData();

    }

    public void generateMemberInvoice(Logic logic){
        HashMap<String,AccntMember> memberList = database.getMemberList();
        ArrayList<String> memberIDsList = new ArrayList<>(memberList.keySet());
        for(String memberID : memberIDsList){
            AccntMember member = memberList.get(memberID);
            String invoice = BuildReports.buildMemberInvoice(new MemberWeeklyInvoice(logic, member));
            String nameFile = member.getNom()+"_"+member.getPrenom();
            database.exportReport(invoice, memberID, nameFile, "invoice");
        }
    }

    public void generateProInvoice(Logic logic){
        HashMap<String,AccntPro> proList = database.getProList();
        ArrayList<String> proIDsList = new ArrayList<>(proList.keySet());
        for(String proID : proIDsList){
            AccntPro pro = proList.get(proID);
            ProWeeklyInvoice invoice = new ProWeeklyInvoice(logic, pro);
            String invoiceText = BuildReports.buildProInvoice(invoice);
            String nameFile = pro.getNom() + "_" + pro.getPrenom();
            database.exportReport(invoiceText, proID, nameFile, "invoice");

            generateTEF(pro.getPrenom(), pro.getNom(), proID, invoice.getTotalPayThisWeek());
        }
    }

    public void generateWeeklyReport(Logic logic){
        String report = BuildReports.buildWeeklyReport(logic, database.getProList());
        database.exportReport(report, "Management", "Rapport_Hebdomadaire");

    }

    public void generateTEF(String firstName, String lastName, String proID, int amountToTransfer){
        FileTEF tef = new FileTEF(firstName,lastName,proID,amountToTransfer);
        database.generateTEF(tef);
    }

    public void procedureComptableHebdomadaire(Logic logic){
        generateMemberInvoice(logic);
        generateProInvoice(logic);
        generateWeeklyReport(logic);
    }

}