package com.topglobales.comportamientoptrn.patronescomportamiento.model;

/**
 * Concrete Subclass (Template Method): Implements formatting for Guest users.
 */
public class GuestUser extends User {
    public GuestUser(String name, String email, String phoneNumber, NotificationStrategy initialStrategy) {
       super(name, email, phoneNumber, initialStrategy);
   }

   @Override
   protected String getHeader() {
       return String.format("Hello %s!", getName());
   }

    @Override
   protected String formatBody(String rawMessage) {
       // Guests might get simpler messages
       return "Notification: " + rawMessage;
   }

   @Override
   protected String getFooter() {
       return "Visit our website for more info.";
   }
}