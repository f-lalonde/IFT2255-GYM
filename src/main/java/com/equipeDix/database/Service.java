package com.equipeDix.database;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Service implements Serializable {

    private String serviceName;
    private final LocalDateTime dateCreated;
    private final LocalDate dateStart;
    // private LocalDate dateEnd; // inutilisé pour l'instant, ne voit pas l'utilité ici. commented out, il faudra demander au client.
    private HashMap<String, ArrayList<LocalTime[]>> recurring;
    private int capacity;
    private final String proID;
    private final String serviceID;
    private int cost;
    private final ArrayList<String> seanceList;
    private final ArrayList<String> comments;

    public Service(String name, LocalDate start, HashMap<String, ArrayList<LocalTime[]>> recurring, int capacity, String proID, String serviceID, int cost){
        this.dateCreated = LocalDateTime.now().withNano(0);
        this.serviceName = name;
        this.dateStart = start;
        this.recurring = recurring;
        this.capacity = capacity;
        this.proID = proID;
        this.serviceID = serviceID;
        this.cost = cost;
        this.comments = new ArrayList<>();
        this.seanceList = new ArrayList<>();
    }

    public Service(String name, LocalDate start, HashMap<String, ArrayList<LocalTime[]>> recurring, int capacity, String proID, String serviceID, int cost, String comments){
        this.dateCreated = LocalDateTime.now().withNano(0);
        this.serviceName = name;
        this.dateStart = start;
        this.recurring = recurring;
        this.capacity = capacity;
        this.proID = proID;
        this.serviceID = serviceID;
        this.cost = cost;
        this.comments = new ArrayList<>();
        this.seanceList = new ArrayList<>();
        this.comments.add(comments);
    }

    public void addComment(String comment){
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String[] createdOn = now.toString().split("T");
        String commentFull = createdOn[0] + ", " + createdOn[1] + ": " + comment;
        this.comments.add(commentFull);
    }

    public void removeComment(int pos){
        this.comments.remove(pos);
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    /*public LocalDate getDateEnd() {
        return dateEnd;
    }*/

    public HashMap<String, ArrayList<LocalTime[]>> getRecurring() {
        return recurring;
    }

    public void setRecurring(HashMap<String, ArrayList<LocalTime[]>> recurring) {
        this.recurring = recurring;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getProID() {
        return proID;
    }

    public String getServiceID() {
        return serviceID;
    }

    public ArrayList<String> getSeanceList() {
        return seanceList;
    }

    public boolean addSeance(String seanceID){
        return this.seanceList.add(seanceID);
    }

    public void removeSeance(String seanceID){
        this.seanceList.remove(seanceID);
    }
}

