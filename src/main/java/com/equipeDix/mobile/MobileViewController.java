package com.equipeDix.mobile;

import com.equipeDix.database.AccntMember;
import com.equipeDix.database.AccntPro;
import com.equipeDix.database.Account;
import com.equipeDix.logic.Logic;
import com.equipeDix.views.Controller;
import javafx.geometry.VPos;
import javafx.scene.layout.RowConstraints;

public class MobileViewController {

    public static final Logic logic = Controller.logic;

    public static void startView() {
        rowConstraintsStartView();
        MobileStartView msv = new MobileStartView(logic);
        msv.applyView();
    }

    public static void memberView(AccntMember member) {
        MobileMemberView mv = new MobileMemberView(logic, member);
        mv.applyView();
    }

    public static void proView(AccntPro pro) {
        MobileProView pv = new MobileProView(logic, pro);
        pv.applyView();
    }

    public static void repertoireDesServices(String fromAccount) {
        new MobileRepertoireServices(logic, fromAccount);
    }

    public static void serviceView(String fromAccount, String serviceID){
        MobileServiceView msv = new MobileServiceView(logic, fromAccount, serviceID);
        msv.applyView();
    }

    public static void seanceView(String fromAccount, String serviceID, String seanceID){
        MobileSeanceView msv = new MobileSeanceView(logic, fromAccount, serviceID, seanceID);
        msv.applyView();
    }

    private static void rowConstraintsStartView(){
        RowConstraints alignCenter = new RowConstraints();
        RowConstraints alignBottom = new RowConstraints();
        RowConstraints alignTop = new RowConstraints();
        alignCenter.setValignment(VPos.CENTER);
        alignCenter.setMaxHeight(200);
        alignCenter.setMinHeight(200);
        alignBottom.setValignment(VPos.BOTTOM);
        alignBottom.setMaxHeight(200);
        alignBottom.setMinHeight(200);
        alignTop.setValignment(VPos.TOP);
        alignTop.setMaxHeight(190);
        alignTop.setMinHeight(190);

        MobileApp.root.getRowConstraints().addAll(alignCenter, alignBottom, alignCenter, alignTop);
    }
}
