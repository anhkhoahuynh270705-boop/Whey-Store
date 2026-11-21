package com.gymstore.gateway.repository;

import com.gymstore.gateway.dto.response.UserAccountDto;
import com.gymstore.gateway.dto.response.UserAccountRow;
import com.gymstore.gateway.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AppUserRepository {

    @Value("${app.datasource.url:jdbc:postgresql://localhost:5432/keycloak_db}")
    private String dbUrl;

    @Value("${app.datasource.username:keycloak_user}")
    private String dbUsername;

    @Value("${app.datasource.password:keycloak_pass}")
    private String dbPassword;

    private final UserMapper userMapper;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    // ── Write

    public void save(UserAccountDto user) {
        final String sql = """
                INSERT INTO app_users (id, username, email, full_name, phone, role, status, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT (username) DO UPDATE
                    SET email      = EXCLUDED.email,
                        full_name  = EXCLUDED.full_name,
                        phone      = COALESCE(EXCLUDED.phone, app_users.phone),
                        role       = EXCLUDED.role,
                        status     = EXCLUDED.status,
                        updated_at = CURRENT_TIMESTAMP
                """;
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getRole() != null ? user.getRole().name() : "CUSTOMER");
            ps.setString(7, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            ps.executeUpdate();
        } catch (Exception e) {
            log.error("save() user '{}': {}", user.getUsername(), e.getMessage(), e);
        }
    }

    public boolean updateStatus(String id, String status) {
        final String sql = "UPDATE app_users SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            log.error("updateStatus() id='{}': {}", id, e.getMessage(), e);
            return false;
        }
    }

    // ── Read 

    public List<UserAccountDto> findAll() {
        final String sql = """
                SELECT id, username, email, full_name, phone, role, status, created_at
                FROM app_users
                ORDER BY created_at DESC
                """;
        List<UserAccountDto> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            log.error("findAll(): {}", e.getMessage(), e);
        }
        return list;
    }

    public Optional<UserAccountDto> findById(String id) {
        final String sql = """
                SELECT id, username, email, full_name, phone, role, status, created_at
                FROM app_users WHERE id = ?
                """;
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            log.error("findById() id='{}': {}", id, e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean existsByUsername(String username) {
        final String sql = "SELECT 1 FROM app_users WHERE username = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            log.error("existsByUsername() '{}': {}", username, e.getMessage(), e);
            return false;
        }
    }

    private UserAccountDto mapRow(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("created_at");
        UserAccountRow row = new UserAccountRow(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("full_name"),
                rs.getString("phone"),
                rs.getString("role"),
                rs.getString("status"),
                timestamp != null ? timestamp.toLocalDateTime() : null);
        return userMapper.fromRow(row);
    }
}
