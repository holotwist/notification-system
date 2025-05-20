package com.topglobales.comportamientoptrn.patronescomportamiento.validator;

/**
 * Abstract base class for ValidationHandlers.
 * Provides default chaining behavior.
 */
public abstract class AbstractValidationHandler implements ValidationHandler {
    private ValidationHandler next;

    @Override
    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next; // Return next for fluent API
    }

    /**
     * Performs the specific validation check.
     * @param context The notification context.
     * @return true if this specific validation passes, false otherwise.
     */
    protected abstract boolean performValidation(NotificationContext context);

    @Override
    public boolean validate(NotificationContext context) {
        if (!performValidation(context)) {
            // If this handler invalidates, mark context and stop chain for this validation type.
            // The context.isValid remains false for subsequent checks by EventManager.
            return false; // Indicate failure at this point.
        }
        // If this handler passes and there's a next handler, pass it on.
        if (next != null) {
            return next.validate(context);
        }
        // If this handler passes, and it's the end of the chain, validation is successful so far.
        return true;
    }
}