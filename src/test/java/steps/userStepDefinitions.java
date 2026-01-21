package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class userStepDefinitions {

    private String username;
    private boolean isValid;

    @Given("un nom d'utilisateur {string}")
    public void un_nom_d_utilisateur(String name) {
        this.username = name;
    }

    @When("le système valide le nom")
    public void le_systeme_valide_le_nom() {
        // Implémentation simple : un nom est valide s'il n'est pas vide et a ≥ 3 caractères
        this.isValid = username != null && username.length() >= 3;
    }

    @Then("le résultat doit être vrai")
    public void le_resultat_doit_etre_vrai() {
        assertTrue(isValid, "Le nom d'utilisateur devrait être valide");
    }
}