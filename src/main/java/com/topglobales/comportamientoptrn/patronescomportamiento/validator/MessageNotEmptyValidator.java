package com.topglobales.comportamientoptrn.patronescomportamiento.validator;

/**
 * Concrete Handler: Validates that the notification message is not empty.
 */
public class MessageNotEmptyValidator extends AbstractValidationHandler {
    @Override
    protected boolean performValidation(NotificationContext context) {
        if (context.getOriginalMessage() == null || context.getOriginalMessage().trim().isEmpty()) {
            context.setIsValid(false);
            context.addValidationMessage("Message cannot be empty.");
            return false;
        }
        context.addValidationMessage("Message is not empty: OK");
        return true;
    }

    @Override
    public String toString() {
        return "MessageNotEmptyValidator";
    }
}