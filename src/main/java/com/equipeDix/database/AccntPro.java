package com.equipeDix.database;

import java.util.ArrayList;

public class AccntPro extends Account {

    private final ArrayList<String> servicesIndex;
    private final ArrayList<String> seancesIndex;

    public AccntPro(String nom, String prenom, String[] address, String telephone, String email, String id) {
        super(nom, prenom, address, telephone, email, id);

        servicesIndex = new ArrayList<>();
        seancesIndex = new ArrayList<>();
    }

    public AccntPro(String nom, String prenom, String[] address, String telephone, String email, String id, String comments) {
        super(nom, prenom, address, telephone, email, id, comments);

        servicesIndex = new ArrayList<>();
        seancesIndex = new ArrayList<>();
    }

    public ArrayList<String> getServicesIndex() {
        return servicesIndex;
    }

    /**
     * method pour ajouter un service a l'account du professionnel
     * @param serviceID numéro d'identification du service
     * @return false si il na pas de service
     */
    public boolean addService(String serviceID) {
        return this.servicesIndex.add(serviceID);
    }

    /**
     * method pour retirer un service de l'account du professionnel
     * @param serviceID numéro d'identification du service
     */
    public void removeService(String serviceID) { this.servicesIndex.remove(serviceID);}

    public ArrayList<String> getSeancesIndex() {
        return seancesIndex;
    }

    /**
     * Ajouter une seance a l'account du professionnel
     * @param seanceID numéro d'identification de la séance
     * @return vrai si l'ajout a bien effectué, faux sinon
     */
    public boolean addSeance(String seanceID) {
        return this.seancesIndex.add(seanceID);
    }

    /**
     * retirer un seance de l'account du professionnell
     * @param seanceID numéro d'identification de la séance
     */
    public void removeSeance(String seanceID) { this.seancesIndex.remove(seanceID);}
}
