public class App {

    /**
     * Méthode principale (point d'entrée du programme)
     */
    public static void main(String[] args) {
        System.out.println("API démarrée – prêt pour l'intégration continue !");
    }

    /**
     * Méthode utilitaire simple à tester
     */
    public static int add(int a, int b) {
        return a + b;
    }

    /**
     * Méthode de simulation de traitement
     */
    public static boolean isValidUser(String username) {
        return username != null && username.length() >= 3;
    }
}