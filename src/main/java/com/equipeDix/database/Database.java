package com.equipeDix.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class Database {
    private final String[] fileName =
            {"memberList","proList", "serviceList", "serviceNameList", "seanceList", "lastID", "emailList"};
    private final String dataDir = "data/";
    private final ArrayList<String> nomListeCategories = new ArrayList<>();
    private final HashMap<String,AccntMember> memberList;
    private final HashMap<String,AccntPro> proList;
    private final HashMap<String,Service> serviceList;
    private final HashMap<String,String> serviceNameList;
    private final HashMap<String,Seance> seanceList;
    private final HashMap<String,Integer> lastID;
    private final HashMap<String,String> emailToID;
    private FileIO fileIO;
    public Database() {
        nomListeCategories.add("membre");
        nomListeCategories.add("pro");
        nomListeCategories.add("service");
        // on tente de charger un fichier existant. Sinon, on crée un nouvel objet.
        // Les avertissements sont normaux. On cast sans filet.

        fileIO = new FileIO(fileName[0],dataDir);
        HashMap<String,AccntMember> importMember = (HashMap<String,AccntMember>) fileIO.getImported();
        this.memberList = Objects.requireNonNullElseGet(importMember, HashMap::new);

        fileIO = new FileIO(fileName[1],dataDir);
        HashMap<String,AccntPro> importPro = (HashMap<String,AccntPro>) fileIO.getImported();
        this.proList = Objects.requireNonNullElseGet(importPro, HashMap::new);

        fileIO = new FileIO(fileName[2],dataDir);
        HashMap<String,Service> importService = (HashMap<String,Service>) fileIO.getImported();
        this.serviceList = Objects.requireNonNullElseGet(importService, HashMap::new);

        fileIO = new FileIO(fileName[3],dataDir);
        HashMap<String,String> importServiceName = (HashMap<String,String>) fileIO.getImported();
        this.serviceNameList = Objects.requireNonNullElseGet(importServiceName, HashMap::new);

        fileIO = new FileIO(fileName[4],dataDir);
        HashMap<String,Seance> importSeance = (HashMap<String,Seance>) fileIO.getImported();
        this.seanceList = Objects.requireNonNullElseGet(importSeance, HashMap::new);

        fileIO = new FileIO(fileName[5],dataDir);
        HashMap<String,Integer> importLastID = (HashMap<String,Integer>) fileIO.getImported();
        if(importLastID == null){
            /*Les trois premiers chiffres correspondent au code du service, les deux prochains chiffres correspondent
            au numéro de la séance et les deux derniers chiffres correspondent aux deux derniers chiffres du numéro du
            professionnel.*/
            // todo prévoir ce qui arrive lorsqu'on a utilisé tous les chiffres. Ne pas faire comme IPv4!
            this.lastID = new HashMap<>();
            lastID.put("membre", 0);
            lastID.put("pro", 900000000);
            lastID.put("service", 100);
        } else {
            this.lastID = importLastID;
        }

        fileIO = new FileIO(fileName[6],dataDir);
        HashMap<String,String> importEmailToID = (HashMap<String,String>) fileIO.getImported();
        this.emailToID = Objects.requireNonNullElseGet(importEmailToID, HashMap::new);
    }

    /**
     * Méthode pour ajouter un compte Membre ou professionnel au centre
     * @param id le numéro unique du membre ou professionnel
     * @param account le nouveau compte
     * @return true si on a ajouté le compte
     */
    public boolean addAccnt(String id, Account account){

        if(!(id.startsWith("9"))){
            if(memberList.containsKey(id)){
                System.out.println("#"+id+" déjà dans la liste des membres.");
                return false;
            }
            memberList.put(id, (AccntMember) account);
            System.out.println("#"+id+" ajouté à la liste des membres.");
        } else {
            if(proList.containsKey(id)){
                System.out.println("#"+id+" déjà dans la liste des professionnels.");
                return false;
            }
            proList.put(id, (AccntPro) account);
            System.out.println("#"+id+" ajouté à la liste des professionnels.");
        }
        return true;
    }

    /**
     * vérifi si c'est un account professionel ou membre et le returne
     * @param id le numero unique
     * @return l'account du menbre ou professionnel
     */
    public Account getAccnt(String id){
        if(id.startsWith("9")){
            return proList.getOrDefault(id, null);
        } else {
            return memberList.getOrDefault(id, null);
        }
    }

    /**
     * Pour mettre a jour l'acount
     * @param id le numero unique
     * @param updAcc la mise a jour
     * @return true
     */
    public boolean updateAcct(String id, Account updAcc){
        if(id.startsWith("9")){
            proList.put(id, (AccntPro) updAcc);
            System.out.println("#"+id+" a été mis à jour dans la liste des professionnels.");

        } else {
            memberList.put(id, (AccntMember) updAcc);
            System.out.println("#"+id+" a été mis à jour dans la liste des membres.");
        }
        return true;
    }

    /**
     * Pour enlever l'account du centre
     * @param id unero unique
     * @return true
     */
    public boolean removeAccnt(String id){
        if(!(id.startsWith("9"))){
            memberList.remove(id);
            System.out.println("#"+id+" retiré de la liste des membres.");
        } else {
            proList.remove(id);
            System.out.println("#"+id+" retiré de la liste des professionnels.");
        }
        return true;
    }

    public HashMap<String, String> getServiceNameList() {
        return serviceNameList;
    }

    /**
     * met a jour la liste de nom de service avec les key de service list
     */
    public void updateServiceNameList(){
        for(String key: serviceList.keySet()){
            serviceNameList.put(key, serviceList.get(key).getServiceName());
        }
    }

    public Service getService(String id){
        return serviceList.get(id);
    }

    /**
     * ajoute un service a la liste de service
     * @param id numero unique
     * @param service le service a ajouter
     * @return vrai si ajouté, faux sinon
     */
    public boolean addService(String id,Service service){
        if (this.serviceList.containsKey(id)){
            return false;
        } else {
            this.serviceList.put(id,service);
            lastID.put(id, 0);
            System.out.println("#"+id+" ajouté à la liste des services.");
            return true;
        }
    }

    /**
     * Enleve un service de la liste de service
     * @param id numero unique
     * @return true sir la service est dans la liste
     */
    public boolean removeService(String id){
        if(this.serviceList.containsKey(id)){
            this.serviceList.remove(id);
            lastID.remove(id);
            System.out.println("#"+id+" retiré de la liste des services.");
            return true;
        } else {
            return false;
        }
    }

    /**
     * met a jour un service avec les nouveau information
     * @param id numero unique
     * @param service le nouveau service qui va remplacer l'encien
     * @return true si le service est dans la liste
     */
    public boolean updateService(String id,Service service){
        if (this.serviceList.containsKey(id)){
            this.serviceList.put(id,service);
            System.out.println("#"+id+" a été mis à jour dans la liste des services.");
            return true;
        } else {
            return false;
        }
    }

    public Seance getSeance(String id){
        return this.seanceList.get(id);
    }

    /**
     * ajoute une seance a liste de seance
     * @param id numero unique
     * @param seance la seance a ajouter
     * @return true si la seance n'est pas déja dans la liste
     */
    public boolean addSeance(String id,Seance seance){
        if (this.seanceList.containsKey(id)){
            return false;
        } else {
            this.seanceList.put(id,seance);
            System.out.println("#"+id+" ajouté à la liste des séances.");
            return true;
        }
    }

    /**
     * pour mettre a jour les information d'une seance
     * @param id numero unique de la seance
     * @param seance la nouvelle seance
     * @return true si la séance fut trouvée et mise à jour
     */
    public boolean updateSeance(String id,Seance seance){
        if (this.seanceList.containsKey(id)){
            this.seanceList.put(id,seance);
            System.out.println("#"+id+" a été mis à jour dans la liste des séances.");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Pour enlever une seance de la liste
     * @param id numero unique de la seance
     * @return true si la séance est dans la liste
     */
    public boolean removeSeance(String id){
        if (this.seanceList.containsKey(id)){
            this.seanceList.remove(id);
            System.out.println("#"+id+" retiré de la liste des séances.");
            return true;
        } else {
            return false;
        }
    }

    public int getLastID(String nomListe) {
        return this.lastID.get(nomListe);
    }

    /**
     * Pour encrémenter les numeros uniques
     * @param nomListe String décrivant la liste duquel fait parti le membre ajouté. Les options sont
     *                 "membre", "pro", "service", ou les trois premiers chiffres du serviceID.
     */
    public void incLastID(String nomListe) {
        int temp = this.lastID.get(nomListe);
        temp++;
        if(!nomListeCategories.contains(nomListe) && temp == 100){
            temp = 0;
        }
        this.lastID.put(nomListe, temp);

    }

    public void addEmail(String email, String id){
        emailToID.put(email, id);
    }

    public boolean isEmailInList(String email){
        return emailToID.containsKey(email);
    }

    public String getIDfromEmail(String email){
        System.out.println(email);
        System.out.println(emailToID.keySet().toString());
        return emailToID.getOrDefault(email, null);
    }

    public void updateEmailToID(String oldEmail, String newEmail, String id){
        emailToID.remove(oldEmail, id);
        emailToID.put(newEmail, id);
    }

    public void removeEmailToID(String email, String id){
        emailToID.remove(email, id);
    }

    public void storeData() {
        fileIO = new FileIO(memberList, fileName[0],dataDir);
        fileIO = new FileIO(proList, fileName[1],dataDir);
        fileIO = new FileIO(serviceList, fileName[2],dataDir);
        fileIO = new FileIO(serviceNameList, fileName[3],dataDir);
        fileIO = new FileIO(seanceList, fileName[4],dataDir);
        fileIO = new FileIO(lastID, fileName[5],dataDir);
        fileIO = new FileIO(emailToID, fileName[6],dataDir);
    }

    public void createSTR(Seance seance, String memberID) {
        ServiceTransactionRecord strec = new ServiceTransactionRecord(seance, memberID);
        fileIO = new FileIO(strec, memberID, dataDir+"TransactionRecords/"+seance.getParentServiceID()+"/"+seance.getSeanceID());
    }

    public void generateTEF(FileTEF tef){
        FileIO.exportTEFtoFile(tef);
    }

    public void exportReport(String toExport, String accountID, String nom){
        FileIO.exportInvoice(toExport, accountID, nom);
    }

    public void exportReport(String toExport, String accountID, String nom, String type){
        FileIO.exportInvoice(toExport, accountID, nom, type);
    }

    public HashMap<String, AccntMember> getMemberList() {
        return memberList;
    }

    public HashMap<String, AccntPro> getProList() {
        return proList;
    }
}
