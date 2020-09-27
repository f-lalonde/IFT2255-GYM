package com.equipeDix.logic;

import com.equipeDix.database.AccntPro;
import com.equipeDix.database.MemberWeeklyInvoice;
import com.equipeDix.database.ProWeeklyInvoice;
import com.equipeDix.database.Seance;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class BuildReports {

    private BuildReports(){

    }

    public static String buildProInvoice(ProWeeklyInvoice invoice){
        String toExport = invoice.getProName() + " ;\n" +
                invoice.getProID() + " ;\n" +
                invoice.getAddress() + " ;\n" +
                invoice.getCity() + " ;\n" +
                invoice.getProvince() + " ;\n" +
                invoice.getPostalCode() + " ;\n";

        ArrayList<Seance> seances = new ArrayList<>(invoice.getServicesGiven().keySet());

        if(seances.isEmpty()){
            toExport = toExport.concat("Pas de séances données cette semaine \n;"
                    + "\nMontant total pour la semaine : " + invoice.getTotalPayThisWeek() + " $\n");
        } else {
            toExport = toExport.concat(invoice.getTotalServiceThisWeek() + " séances données cette semaine \n;"
                    + "\nMontant total pour la semaine : " + invoice.getTotalPayThisWeek() + " $\n");
            for (Seance seance : seances) {

                toExport = toExport.concat(
                        "\n\tSeance #" + seance.getSeanceID() + " :\n" +
                                seance.getDateStart().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " ;\n" +
                                seance.getCost() + " $ par inscription ;\n" +
                                "total pour la séance : " + seance.getPayAfterSeance() + " $\n");

                toExport = toExport.concat("\nListe des inscriptions :\n");
                for (String[] infos : invoice.getServicesGiven().get(seance)) {

                    toExport = toExport.concat(
                            String.format("%-11s", infos[0]) + "\n" +
                                    String.format("%-27s", infos[1]) + "\n" +
                                    infos[2] + "\n");
                }
            }
        }
        return toExport;
    }

    public static String buildMemberInvoice(MemberWeeklyInvoice invoice){
        String toExport = invoice.getMemberName() + " ;\n" +
                invoice.getMemberID() + " ;\n" +
                invoice.getAddress() + " ;\n" +
                invoice.getCity() + " ;\n" +
                invoice.getProvince() + " ;\n" +
                invoice.getPostalCode() + " ;\n";
        int count = 0;
        for(String[] serviceInfo : invoice.getServicesReceived()){
            toExport = toExport.concat("\tService "+(++count)+ " :\n");
            toExport = toExport.concat(serviceInfo[0] + " ;\n" +serviceInfo[1] + " ;\n" +serviceInfo[2] + " ;\n\n");
        }
        return toExport;
    }

    public static String buildWeeklyReport(Logic logic, HashMap<String, AccntPro> proList){
        ArrayList<String> proIDs = new ArrayList<>(proList.keySet());
        String report = "";
        int totalProWithServicesThisWeek = 0;
        int totalServicesThisWeek = 0;
        int totalToPayThisWeek = 0;
        for(String proID : proIDs){
            ProWeeklyInvoice proInvoice = new ProWeeklyInvoice(logic, proList.get(proID));
            if(proInvoice.getTotalPayThisWeek() > 0){
                totalProWithServicesThisWeek++;
                totalServicesThisWeek = totalServicesThisWeek + proInvoice.getTotalServiceThisWeek();
                totalToPayThisWeek = totalToPayThisWeek + proInvoice.getTotalPayThisWeek();
                report = report.concat(buildProInvoice(proInvoice)+"\n\n================\n\n");
            };
        }
        report = report.concat("Nombre de professionnel ayant donné des services dans les 7 derniers jours : " +
                        totalProWithServicesThisWeek + "\n" +
                        "Nombre total de services donnés dans les 7 derniers jours : " + totalServicesThisWeek + "\n" +
                        "Montant total des paies pour les 7 derniers jours : " + totalToPayThisWeek + "\n\n" +
                "===== FIN DU RAPPORT =====");
        return report;
    }

}
