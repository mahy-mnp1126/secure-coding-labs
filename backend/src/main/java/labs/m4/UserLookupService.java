package labs.m4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

/**
 * Module 4, Task 2: this is the SAFE version from Section 1.3.
 * In a branch of your fork, replace findUser with the VULNERABLE version from
 * Section 1.3, open a pull request, and watch CodeQL block it.
 */
@Service
public class UserLookupService {
    private final DataSource dataSource;

    UserLookupService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // SAFE: the query text is fixed; the value is bound as a parameter.
    public Optional<UserSummary> findUser(String username) throws SQLException {
        String sql = "SELECT id, username, role FROM users WHERE username = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapUser(rs)) : Optional.empty();
            }
        }
    }

    private UserSummary mapUser(ResultSet rs) throws SQLException {
        return new UserSummary(rs.getLong("id"), rs.getString("username"), rs.getString("role"));
    }
}
