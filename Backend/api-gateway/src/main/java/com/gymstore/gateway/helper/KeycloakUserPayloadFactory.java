package com.gymstore.gateway.helper;

public final class KeycloakUserPayloadFactory {

    private KeycloakUserPayloadFactory() {}

    public static String toCreateUserJson(String username, String email,
                                          String firstName, String lastName,
                                          String phone, String password) {
        String phoneAttr = (phone != null && !phone.isBlank())
                ? String.format("\"attributes\": { \"phone\": [\"%s\"] },", phone)
                : "";
        return String.format("""
                {
                  "username": "%s",
                  "email": "%s",
                  "firstName": "%s",
                  "lastName": "%s",
                  "enabled": true,
                  "emailVerified": true,
                  %s
                  "credentials": [
                    {
                      "type": "password",
                      "value": "%s",
                      "temporary": false
                    }
                  ]
                }
                """, username, email, firstName, lastName, phoneAttr, password);
    }
}
