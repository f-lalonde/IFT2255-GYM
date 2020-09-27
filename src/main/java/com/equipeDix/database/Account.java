package com.equipeDix.database;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class Account implements Serializable {
    private String nom;
    private String prenom;
    private String[] address;
    private String telephone;
    private String email;
    private final String id;
    private final ArrayList<String> comments;

    public Account(String prenom, String nom, String[] address, String telephone, String email, String id) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.comments = new ArrayList<>();
    }

    public Account(String prenom, String nom, String[] address, String telephone, String email, String id, String comment) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.comments = new ArrayList<>();
        this.comments.add(comment);
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String[] getAddress() {
        return address;
    }

    public void setAddress(String[] address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    /**
     * method pour Ajouter un commentaire a L'account
     * @param comment Texte du commentaire
     */
    public void addComment(String comment){
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String[] createdOn = now.toString().split("T");
        String commentFull = createdOn[0] + ", " + createdOn[1] + ": " + comment;
        this.comments.add(commentFull);
    }

    /**
     * Method pour enlever un commentaire de l'account
     * @param pos position du commentaire dans le tableau des commentaires
     */
    public void removeComment(int pos){
        this.comments.remove(pos);
    }
}
