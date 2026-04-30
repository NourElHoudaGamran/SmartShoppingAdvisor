package model; // Changé de 'service' à 'model' pour correspondre à ton dossier

import model.User;

/**
 * Gestion de la session utilisateur.
 */
public class Session {

    private static User loggedInUser;

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
        if (user != null) {
            // Correction de la syntaxe ici (guillemets et parenthèses)
            System.out.println("✅ Session ouverte pour : " + user.getName() + " (ID: " + user.getId() + ")");
        }
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void logout() {
        if (loggedInUser != null) {
            System.out.println("🚶 Déconnexion de : " + loggedInUser.getName());
        }
        loggedInUser = null;
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }
}