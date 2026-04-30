package App;

import javafx.application.Application;

/**
 * Cette classe sert de point d'entrée principal pour contourner les
 * restrictions de modules de JavaFX 11+ et lancer l'interface graphique.
 */
public class AppLauncher {

    public static void main(String[] args) {
        // Cette commande appelle la classe MainApp qui contient la fenêtre
        Application.launch(MainApp.class, args);
    }
}