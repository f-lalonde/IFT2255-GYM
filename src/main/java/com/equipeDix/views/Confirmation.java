package com.equipeDix.views;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class Confirmation {

    public static final String[] alerts = {"idFormat", "noSuchAccount"};

    public static boolean confirmDelete(){
        TextInputDialog confirm = new TextInputDialog();
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Cette opération est IRRÉVERSIBLE. Êtes-vous certain de vouloir procéder à la suppression?");
        confirm.setContentText("si oui, tapez « oui » (sans les guillements). Toute autre réponse annulera l'opération");
        Optional<String> result = confirm.showAndWait();
        return result.map(s -> s.toLowerCase().equals("oui")).orElse(false);

    }

    public static boolean confirmation(String op) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Dialogue de confirmation");
        confirmation.setHeaderText("Opération : " + op);
        confirmation.setContentText("Confirmez-vous vouloir effectuer cette opération?");

        Optional<ButtonType> result = confirmation.showAndWait();
        return ButtonType.OK.equals(result.orElse(null));
    }

    //todo : spécialiser les alertes? (Ou mieux les généraliser!)
    public static void alertBox(String desc) {
        String title;
        String header;
        String content;
        switch (desc) {
            case "idFormat":
                title = "Mauvais format : ID";
                header = "Le numéro d'identification entré n'est pas correctement formatté";
                content = "Un numéro d'identification contient 9 chiffres." +
                        "\nLe numéro d'identification des professionnels débute par un 9.";
                break;

            case "noSuchAccount":
                title = "Compte inexistant";
                header = "Le numéro d'identification entré ne se trouve pas dans la base de donnée";
                content = "Veuillez réessayer ou créer un nouveau compte";
                break;

            default:
                title = "Notification";
                header = desc;
                content = "Prenez note du message ci-haut";
                break;
        }
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
