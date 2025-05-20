package com.topglobales.comportamientoptrn.patronescomportamiento.validator;

import com.topglobales.comportamientoptrn.patronescomportamiento.model.User;

/**
 * Concrete Handler: Validates if the recipient user is blocked.
 */
public class BlockedUserValidator extends AbstractValidationHandler {

    @Override
    protected boolean performValidation(NotificationContext context) {
        User recipient = context.getRecipientUser();

        if (recipient != null) { // This validator applies if there's a specific user in the context
            if (recipient.isBlocked()) {
                context.setIsValid(false);
                context.addValidationMessage(String.format("User [%s] is blocked. Notification suppressed for this user.", recipient.getName()));
                return false; // Validation failed
            }
            context.addValidationMessage(String.format("User [%s] is not blocked: OK", recipient.getName()));
        } else {
            // If no specific user, this validator doesn't apply or passes by default.
            // For clarity, we can log that it's being skipped for non-user contexts if the chain is run globally.
            // context.addValidationMessage("BlockedUserValidator: No specific user in context, check not applicable here.");
            // For now, because is not critical, keep it simple
        }
        return true; // Validation passed (or not applicable for non-user context)
    }

    @Override
    public String toString() {
        return "BlockedUserValidator";
    }
}