package com.equipeDix.database;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ServiceTransactionRecord {

    private final LocalDateTime recordCreatedOn;
    private final LocalDate seanceDate;
    private final String proID;
    private final String memberID;
    private final String seanceID;
    private final String comment;

    public ServiceTransactionRecord(Seance seance, String memberID){
        recordCreatedOn = LocalDateTime.now();
        seanceDate = LocalDate.from(seance.getDateStart());
        proID = seance.getProID();
        this.memberID = memberID;
        seanceID = seance.getSeanceID();
        this.comment = "";
    }

    public ServiceTransactionRecord(Seance seance, String memberID, String comment){
        recordCreatedOn = LocalDateTime.now();
        seanceDate = LocalDate.from(seance.getDateStart());
        proID = seance.getProID();
        this.memberID = memberID;
        seanceID = seance.getSeanceID();
        this.comment = comment;
    }

    public LocalDateTime getRecordCreatedOn() {
        return recordCreatedOn;
    }

    public LocalDate getSeanceDate() {
        return seanceDate;
    }

    public String getProID() {
        return proID;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getSeanceID() {
        return seanceID;
    }

    public String getComment() {
        return comment;
    }


}
