package com.equipeDix.database;

import java.time.LocalDate;
import java.util.ArrayList;

public class AccntMember extends Account {
    private final ArrayList<String> seanceInscr;
    private LocalDate validUntil;

    public AccntMember(String prenom, String nom, String[] address, String telephone, String email, String id) {
        super(prenom, nom, address, telephone, email, id);
        seanceInscr = new ArrayList<>();
        // todo : changer éventuellement. Mis de le passé pour tester.
        this.validUntil = LocalDate.of(2020, 7, 22);
    }

    public AccntMember(String prenom, String nom, String[] address, String telephone, String email, String id, String comments) {
        super(prenom, nom, address, telephone, email, id, comments);
        seanceInscr = new ArrayList<>();
        // todo : idem
        this.validUntil = LocalDate.of(2020, 7, 22);
    }

    public ArrayList<String> getSeanceInscr() {
        return seanceInscr;
    }

    /**
     * méthode pour ajouter une séance au compte du membre
     * @param seanceID numéro d'identification de la séance
     * @return true si exécuté sans problème
     */
    public boolean addSeance(String seanceID){
        return this.seanceInscr.add(seanceID);
    }

    /**
     * methode pour enlever une séance du compte du membre
     * @param seanceID numéro d'identification de la séance
     */
    public void removeSeance(String seanceID){
        this.seanceInscr.remove(seanceID);
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }
}
