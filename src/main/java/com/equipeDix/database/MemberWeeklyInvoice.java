package com.equipeDix.database;

import com.equipeDix.logic.Logic;

import java.time.LocalDate;
import java.util.ArrayList;

public class MemberWeeklyInvoice {
    private final String memberName;
    private final String memberID;
    private final String address;
    private final String city;
    private final String province;
    private final String postalCode;
    private final ArrayList<String[]> servicesReceived = new ArrayList<>();

    public MemberWeeklyInvoice(Logic logic, AccntMember member){
        this.memberName = String.format("%.25s", member.getPrenom() +" "+member.getNom());
        this.memberID = member.getId();
        String[] fullAddress = member.getAddress();
        this.address = String.format("%.25s", (fullAddress[0]+" "+fullAddress[1]));
        this.city = String.format("%.14s", fullAddress[2]);
        this.province = fullAddress[3];
        this.postalCode = fullAddress[4];

        for(String seanceID : member.getSeanceInscr()){
            Seance seance = logic.getSeance(seanceID);
            if(!LocalDate.from(seance.getDateStart()).isAfter(LocalDate.now())){

                String[] serviceInfos = new String[3];

                serviceInfos[0] = LocalDate.from(seance.getDateStart()).toString();

                serviceInfos[1] = String.format("%.25s", logic.getAccount(seance.getProID()).getPrenom() +" "+
                        logic.getAccount(seance.getProID()).getNom());

                serviceInfos[2] = String.format("%.20s", seance.getParentServiceName());

                servicesReceived.add(serviceInfos);
            }
        }
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberID() {
        return memberID;
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

    public ArrayList<String[]> getServicesReceived() {
        return servicesReceived;
    }
}
