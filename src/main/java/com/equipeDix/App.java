package com.equipeDix;

// Note : on a enlevé "dateEnd" de service. Je crois que ça ne servait à rien.

//todo :
// 1. Ajouter la section "commentaires" pour :
//      - Compte membre x (Directement dans "Account", ça va gérer les deux d'un coups)
//      - Compte pro x
//      - Service
// 2. Ajouter une façon de défaire des récurrence existantes
// 4. Il faut enlever les séances passées du répertoire des services, mais pas immédiatement du compte professionnel
//    pour les rapports (ou les mettre dans une autre structure gardée jusqu'à l'exécution hebdomadaire du rapport)
// 5. Permettre à un membre de se désinscrire d'une séance
// 6. S'assurer que la base de donnée est synchronisée entre l'application mobile / application desktop



/**
 * JavaFX App. On fait juste passer la puck. Vestige dû au fait que c'est mon premier projet avec Maven.
 */
public class App {

    public static void main(String[] args) {

        CentreDeDonnees.main(args);
    }

}