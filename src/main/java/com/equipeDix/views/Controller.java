package com.equipeDix.views;

import com.equipeDix.database.AccntMember;
import com.equipeDix.database.AccntPro;
import com.equipeDix.logic.Logic;

public class Controller {
    public static final Logic logic = new Logic();

    // todo : QUESTIONNEMENT :
    //  est-ce mieux d'initialiser toutes les vues et de simplement switcher entre elles avec applyView()
    //  ou bien de recréer les instances à neufs chaque fois est la meilleure façon de faire?
    public static void newAccount() {
        NewAccountView na = new NewAccountView(logic);
        na.applyView();
    }

    public static void startView() {
        StartView sv = new StartView(logic);
        sv.applyView();
    }

    public static void memberView(AccntMember member) {
        MemberView mv = new MemberView(logic, member);
        mv.applyView();
    }

    public static void proView(AccntPro pro) {
        ProView pv = new ProView(logic, pro);
        pv.applyView();
    }

    public static void serviceView(String fromAccountID, String serviceID) {
        ServiceView sv = new ServiceView(logic, fromAccountID, serviceID);
        sv.applyView();
    }

    public static void newServiceView(String proID) {
        NewServiceView nsv = new NewServiceView(logic, proID);
        nsv.applyView();
    }

    public static void seanceView(String fromAccount, String seanceID) {
        SeanceView sv = new SeanceView(logic, fromAccount, seanceID);
        sv.applyView();
    }
}
