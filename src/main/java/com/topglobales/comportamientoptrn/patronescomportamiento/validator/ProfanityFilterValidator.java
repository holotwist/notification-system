package com.topglobales.comportamientoptrn.patronescomportamiento.validator;

/**
 * Concrete Handler: Simulates a profanity filter.
 */
public class ProfanityFilterValidator extends AbstractValidationHandler {
    @Override
    protected boolean performValidation(NotificationContext context) {
        // This is a mock filter.
        // Maybe change the single validation for a set of bad words
        if (context.getOriginalMessage() != null && context.getOriginalMessage().toLowerCase().contains("fernely")) {
            context.setIsValid(false);
            context.addValidationMessage("Message contains prohibited words (e.g., 'fernely').");
            return false;
        }
        context.addValidationMessage("Profanity check: OK");
        return true;
    }

    @Override
    public String toString() {
        return "ProfanityFilterValidator";
    }
}