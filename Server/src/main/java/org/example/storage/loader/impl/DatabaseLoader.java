package org.example.storage.loader.impl;

import org.example.logger.AsyncLogger;
import org.example.model.*;
import org.example.storage.loader.ILoader;
import org.example.storage.pool.DataSourceFactory;
import org.example.util.ConnectionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.logging.Level;

public class DatabaseLoader implements ILoader<Collection<Worker>> {
    private static final AsyncLogger logger = AsyncLogger.get("server");

    private static final String LOAD_SQL = """
                     SELECT w.id, w.name, w.creation_date, w.salary, w.start_date, w.position, w.status,
                     c.x AS c_x, c.y AS c_y, c.id as coords_id,
                     o.id as org_id, o.full_name AS o_full_name, o.type AS o_type,
                     o.zip_code AS a_zip_code,
                     o.x AS l_x, o.y AS l_y, o.z AS l_z, o.address AS l_name,
                     u.id as user_id, u.username AS username, u.password_hash as password
                     FROM workers w
                     INNER JOIN coordinates c ON w.coordinates_id = c.id
                     INNER JOIN users u ON w.user_id = u.id
                     LEFT JOIN organizations o ON w.organization_id = o.id
            """;
    private final DataSource connections;

    public DatabaseLoader() {
        connections = DataSourceFactory.getDataSource();
    }

    @Override
    public boolean save(Collection<Worker> col) {
        return false;
    }

    public Collection<Worker> load() {
        try (Connection conn = connections.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(LOAD_SQL)) {

            Collection<Worker> collInit = new PriorityQueue<>();
            while (rs.next()) {
                Worker.Builder workerBuilder = Worker.builder();
                workerBuilder.id(rs.getInt("id"));
                workerBuilder.name(rs.getString("name"));

                Coordinates coordinates = Coordinates.builder()
                        .x(rs.getLong("c_x"))
                        .y(rs.getDouble("c_y"))
                        .id(rs.getInt("coords_id"))
                        .build();

                workerBuilder.coordinates(coordinates);

                workerBuilder.creationDate(rs.getDate("creation_date")
                        .toLocalDate());
                workerBuilder.salary(rs.getFloat("salary"));
                workerBuilder.startDate(rs.getDate("start_date"));
                workerBuilder.position(Position.valueOf(rs.getString("position")));
                workerBuilder.status(Status.valueOf(rs.getString("status")));
                workerBuilder.user(User.builder()
                        .id(rs.getLong("user_id"))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .build());
                if (rs.getString("o_full_name") != null) {
                    Location location = Location.builder()
                            .x(rs.getDouble("l_x"))
                            .y(rs.getInt("l_y"))
                            .z(rs.getDouble("l_z"))
                            .name(rs.getString("l_name"))
                            .build();

                    Address address = Address.builder()
                            .zipCode(rs.getString("a_zip_code"))
                            .town(location)
                            .build();

                    Organization organization = Organization.builder().id(rs.getInt("org_id"))
                            .fullName(rs.getString("o_full_name"))
                            .type(OrganizationType.valueOf(rs.getString("o_type")))
                            .postalAddress(address)
                            .build();
                    workerBuilder.organization(organization);

                }

                Worker build = workerBuilder.build();
                System.out.println(build);
                System.out.println(build.getUserId());

                collInit.add(workerBuilder.build());
            }

            return collInit;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, String.format("Error loading workers from the database: %s", e.getMessage()));
            return new PriorityQueue<>();
        }
    }
}
