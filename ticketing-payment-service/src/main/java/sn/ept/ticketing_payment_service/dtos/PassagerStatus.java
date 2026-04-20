package sn.ept.ticketing_payment_service.dtos;

/**
 * Statuts possibles d'un passager dans le système BRT.
 */
public enum PassagerStatus {

    /** Compte actif, peut utiliser le service */
    ACTIVE,

    /** Compte désactivé volontairement */
    INACTIVE,

    /** Compte suspendu (ex: fraude) */
    SUSPENDED,

    /** En attente de vérification (inscription initiale) */
    PENDING_VERIFICATION
}
