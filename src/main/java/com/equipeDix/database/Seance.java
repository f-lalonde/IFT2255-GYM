package com.equipeDix.database;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Seance implements Serializable {
    private final LocalDateTime dateCreated;
    private final LocalDateTime dateStart;
    private final LocalDateTime dateEnd;
    private final ArrayList<String> enrolledMembers;
    private int capacity;
    private int cost;
    private final String seanceID;
    private final String parentServiceName;
    private final String parentServiceID;
    private final String proID;
    private final HashMap<String, LocalDateTime> attendedMembers;
    private String comments;
    private int payAfterSeance = 0;

    public Seance(String seanceID, LocalDateTime dateStart, LocalDateTime dateEnd, int capacity, Service parentService) {
        this.dateCreated = LocalDateTime.now();
        this.attendedMembers = new HashMap<>();
        this.enrolledMembers = new ArrayList<>();
        this.seanceID = seanceID;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.capacity = capacity;
        this.parentServiceID = parentService.getServiceID();
        this.proID = parentService.getProID();
        this.parentServiceName = parentService.getServiceName();
        this.cost = parentService.getCost();
    }

    public Seance(String seanceID, LocalDateTime dateStart, LocalDateTime dateEnd, int capacity, Service parentService, String comments) {
        this.dateCreated = LocalDateTime.now();
        this.attendedMembers = new HashMap<>();
        this.enrolledMembers = new ArrayList<>();
        this.seanceID = seanceID;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.capacity = capacity;
        this.parentServiceID = parentService.getServiceID();
        this.proID = parentService.getProID();
        this.parentServiceName = parentService.getServiceName();
        this.comments = comments;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public LocalDateTime getDateEnd() {
        return dateEnd;
    }

    public String getSeanceID() {
        return seanceID;
    }

    public String getParentServiceID() {
        return parentServiceID;
    }

    public String getProID() {
        return proID;
    }

    public ArrayList<String> getEnrolledMembers() {
        return enrolledMembers;
    }

    public void addEnrolledMember(String memberID) {
        this.enrolledMembers.add(memberID);
    }

    public void removeEnrolledMember(String memberID) {
        this.enrolledMembers.remove(memberID);
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

    public HashMap<String, LocalDateTime> getAttendedMembers() {
        return attendedMembers;
    }

    public void addAttendedMember(String memberID) {
        this.attendedMembers.put(memberID, LocalDateTime.now());
    }

    public void removeAttendedMember(String memberID) {
        this.attendedMembers.remove(memberID);
    }

    public String getComments() {
        return comments;
    }

    public String getParentServiceName() {
        // don't touch this même si le IDE te dit que c'est inutile! Il est utilisé par
        // PropertyValueFactory!

        return parentServiceName;
    }

    public int getPayAfterSeance() {
        return payAfterSeance;
    }

    private void setPayAfterSeance(int payAfterSeance) {
        this.payAfterSeance = payAfterSeance;
    }

    private void calculatePayAfterSeance(){
        int totalEnrolled = (enrolledMembers.size() + attendedMembers.size());
        setPayAfterSeance(cost * totalEnrolled);
    }
}
