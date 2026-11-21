package com.gymstore.gateway.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KeycloakConfig {

    /** URL gốc của Keycloak server */
    @Value("${keycloak.server-url:http://localhost:8090}")
    private String serverUrl;

    /** Realm chứa user của ứng dụng */
    @Value("${keycloak.realm:gymstore}")
    private String realm;

    /** Client ID của frontend Angular */
    @Value("${keycloak.client-id:gymstore-frontend}")
    private String clientId;

    /** Realm dùng để lấy admin token */
    @Value("${keycloak.admin-realm:master}")
    private String adminRealm;

    /** Client ID dùng để lấy admin token */
    @Value("${keycloak.admin-client-id:admin-cli}")
    private String adminClientId;

    /** Tên đăng nhập admin Keycloak */
    @Value("${keycloak.admin-username:admin}")
    private String adminUsername;

    /** Mật khẩu admin Keycloak */
    @Value("${keycloak.admin-password:admin}")
    private String adminPassword;

    // Computed URL helpers 

    public String adminTokenUrl() {
        return serverUrl + "/realms/" + adminRealm + "/protocol/openid-connect/token";
    }

    public String usersUrl() {
        return serverUrl + "/admin/realms/" + realm + "/users";
    }

    public String userUrl(String userId) {
        return usersUrl() + "/" + userId;
    }

    public String sendVerifyEmailUrl(String userId) {
        return userUrl(userId) + "/send-verify-email";
    }

    public String userRoleMappingsUrl(String userId) {
        return userUrl(userId) + "/role-mappings/realm";
    }

    public String roleUrl(String roleName) {
        return serverUrl + "/admin/realms/" + realm + "/roles/" + roleName;
    }

    public String revokeUrl() {
        return serverUrl + "/realms/" + realm + "/protocol/openid-connect/revoke";
    }

    public String logoutUrl() {
        return serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
    }

    public String adminTokenForm() {
        return "grant_type=password"
                + "&client_id="  + adminClientId
                + "&username="   + adminUsername
                + "&password="   + adminPassword;
    }
}
