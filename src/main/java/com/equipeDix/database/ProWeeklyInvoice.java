package com.equipeDix.database;

import com.equipeDix.logic.Logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class ProWeeklyInvoice {
    private final String proName;
    private final String proID;
    private final String address;
    private final String city;
    private final String province;
    private final String postalCode;
    private int totalPayThisWeek;
    private int totalServiceThisWeek;
    private final HashMap<Seance, ArrayList<String[]>> servicesGiven = new HashMap<>();

    public ProWeeklyInvoice(Logic logic, AccntPro pro){
        this.proName = String.format("%.25s", pro.getPrenom() +" "+pro.getNom());
        this.proID = pro.getId();
        String[] fullAddress = pro.getAddress();
        this.address = String.format("%.25s", (fullAddress[0]+" "+fullAddress[1]));
        this.city = String.format("%.14s", fullAddress[2]);
        this.province = fullAddress[3];
        this.postalCode = fullAddress[4];

        totalPayThisWeek = 0;
        totalServiceThisWeek = 0;
        for(String seancesID : pro.getSeancesIndex()){
            Seance seance = logic.getSeance(seancesID);
            totalPayThisWeek = totalPayThisWeek + seance.getPayAfterSeance();
            ArrayList<String[]> seanceInfos = new ArrayList<>();

            if(LocalDateTime.now().isAfter(seance.getDateEnd())){
                totalServiceThisWeek++;
                HashMap<String, LocalDateTime> attended = seance.getAttendedMembers();
                ArrayList<String> attendedMember = new ArrayList<>(attended.keySet());
                for(String memberID : attendedMember){
                    String[] memberInfos = new String[3];
                    AccntMember membre = (AccntMember) logic.getAccount(memberID);
                    memberInfos[0] = memberID;
                    memberInfos[1] = String.format("%.25s", membre.getPrenom() +" "+ membre.getNom());
                    memberInfos[2] = attended.get(memberID).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                    seanceInfos.add(memberInfos);
                } for(String memberID : seance.getEnrolledMembers()){
                    String[] memberInfos = new String[3];
                    AccntMember membre = (AccntMember) logic.getAccount(memberID);
                    memberInfos[0] = memberID;
                    memberInfos[1] = String.format("%.25s", membre.getPrenom() +" "+ membre.getNom());
                    memberInfos[2] = "absent";
                    seanceInfos.add(memberInfos);
                }
                servicesGiven.put(seance, seanceInfos);
            }
        }
    }

    public String getProName() {
        return proName;
    }

    public String getProID() {
        return proID;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public int getTotalPayThisWeek() {
        return totalPayThisWeek;
    }

    public int getTotalServiceThisWeek() {
        return totalServiceThisWeek;
    }

    public HashMap<Seance, ArrayList<String[]>> getServicesGiven() {
        return servicesGiven;
    }
}
