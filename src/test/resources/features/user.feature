Feature: Validation de base de l'API

  Scenario: Vérifier qu'un utilisateur valide est accepté
    Given un nom d'utilisateur "yasmine"
    When le système valide le nom
    Then le résultat doit être vrai