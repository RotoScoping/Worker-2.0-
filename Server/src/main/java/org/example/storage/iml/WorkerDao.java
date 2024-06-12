package org.example.storage.iml;

import org.example.logger.AsyncLogger;
import org.example.model.Coordinates;
import org.example.model.Organization;
import org.example.model.Worker;
import org.example.storage.AuditableCrud;
import org.example.storage.loader.ILoader;
import org.example.storage.loader.impl.DatabaseLoader;
import org.example.storage.pool.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;


/**
 * Class managing a collection of workers (dao)
 */
public class WorkerDao implements AuditableCrud<Integer, Worker> {

    private static final AsyncLogger logger = AsyncLogger.get("server");
    private static final WorkerDao INSTANCE = new WorkerDao(new DatabaseLoader());

    private final DataSource connections;
    private final Collection<Worker> storage;

    private final LocalDateTime createdAt;

    private final ReentrantLock lock = new ReentrantLock();
    private final ILoader<? extends Collection<Worker>> loader;

    private static final String UPDATE_WORKER_SQL = """
                UPDATE workers
                SET name = ?, creation_date = ?, salary = ?, start_date = ?,
                    position = ?, status = ?, organization_id = ?
                WHERE id = ?;
            """;

    private static final String UPDATE_COORDINATES_SQL = """
                UPDATE coordinates
                SET x = ?, y = ?
                WHERE id = ?;
            """;

    private static final String UPDATE_ORGANIZATION_SQL = """
                UPDATE organizations
                SET full_name = ?, type = ?, zip_code = ?, x = ?, y = ?, z = ?, address = ?
                WHERE full_name = ?;
            """;

    public static final String DELETE_WORKER_SQL = "DELETE FROM workers WHERE id = ?";

    public static final String ADD_WORKER_SQL = "INSERT INTO workers (name, coordinates_id, creation_date, salary, start_date, position, status, organization_id, user_id) "
                                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    /**
     * Instantiates a new Worker repository.
     *
     * @param loader the loader
     */
    private WorkerDao(ILoader<? extends Collection<Worker>> loader) {
        connections = DataSourceFactory.getDataSource();
        this.loader = loader;
        storage = new PriorityQueue<>((w1, w2) -> w2.getId() - w1.getId());
        createdAt = LocalDateTime.now();
        Init(loader);

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static WorkerDao getInstance() {
        return INSTANCE;
    }

    /**
     * A method that initializes a collection of workers using the ILoader<PriorityQueue<Worker>> interface implementation.
     *
     * @param loader the loader
     */
    private void Init(ILoader<? extends Collection<Worker>> loader) {
        Collection<Worker> data = loader.load();
        storage.addAll(data);
        storage.forEach(System.out::println);
    }


    /**
     * Method that add worker to the storage
     *
     * @param worker the worker
     */
    public boolean add(Worker worker) {
        worker.setCreationDate(LocalDate.now());
        try (Connection conn = connections.getConnection()) {
            conn.setAutoCommit(false); // Начало транзакции
            int coordinatesId = addCoordinates(conn, worker.getCoordinates());
            int organizationId = 0;
            if (worker.getOrganization() != null) {
                organizationId = addOrganization(conn, worker.getOrganization());
            }
            try (PreparedStatement pstmt = conn.prepareStatement(ADD_WORKER_SQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, worker.getName());
                pstmt.setInt(2, coordinatesId);
                pstmt.setDate(3, Date.valueOf(worker.getCreationDate()));
                pstmt.setFloat(4, worker.getSalary());
                pstmt.setDate(5, new java.sql.Date(worker.getStartDate()
                        .getTime()));
                pstmt.setObject(6, worker.getPosition()
                        .name(), Types.OTHER);  // Correct way to set ENUM type
                pstmt.setObject(7, worker.getStatus()
                        .name(), Types.OTHER);  // Correct way to set ENUM type
                if (worker.getOrganization() != null) {
                    pstmt.setInt(8, organizationId);
                } else {
                    pstmt.setNull(8, java.sql.Types.INTEGER);
                }
                pstmt.setLong(9, worker.getUserId());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating worker failed, no rows affected.");
                }
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        worker.setId(generatedId);
                        lock.lock();
                        try {
                            storage.add(worker);
                        } finally {
                            lock.unlock();
                        }
                        conn.commit();
                        logger.log(Level.INFO, String.format("Работник успешно добавлен пользователем %s: %s", worker.getUser().getUsername(), worker));
                        return true;
                    } else {
                        conn.rollback();
                        throw new SQLException("Creating worker failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                logger.log(Level.SEVERE, String.format("Error adding worker to the database: %s", e.getMessage()));
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, String.format("Error establishing database connection: %s", e.getMessage()));
            e.printStackTrace();
        }

        return false;
    }

    private int addCoordinates(Connection connection, Coordinates coords) throws SQLException {
        String sql = "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, coords.getX());
            preparedStatement.setDouble(2, coords.getY());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating coordinates failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating coordinates failed, no ID obtained.");
                }
            }
        }
    }

    private int addOrganization(Connection connection, Organization organization) throws SQLException {
        String sql = "INSERT INTO organizations (full_name, type, zip_code, x, y, z, address) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, organization.getFullName());
            preparedStatement.setObject(2, organization.getType()
                    .name(), Types.OTHER);
            preparedStatement.setString(3, organization.getPostalAddress()
                    .getZipCode());
            preparedStatement.setDouble(4, organization.getPostalAddress()
                    .getTown()
                    .getX());
            preparedStatement.setInt(5, organization.getPostalAddress()
                    .getTown()
                    .getY());
            preparedStatement.setDouble(6, organization.getPostalAddress()
                    .getTown()
                    .getZ());
            preparedStatement.setString(7, organization.getPostalAddress()
                    .getTown()
                    .getName());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating organization failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating organization failed, no ID obtained.");
                }
            }
        }
    }


    /**
     * Method that returns all workers
     *
     * @return collection of workers
     */
    @Override
    public Collection<Worker> readAll() {
        lock.lock();
        try {
            return new ArrayList<>(storage);
        } finally {
            lock.unlock();
        }
    }


    /**
     * Method that updates worker in collection.
     * <br> That
     *
     * @param worker the worker
     * @return boolean result of operation
     */
    public boolean update(Worker worker) {
        worker.setCreationDate(LocalDate.now());
        boolean isOrgNull = false;
        for (Worker el : storage) {
            if (el.getId() == worker.getId() && el.getUserId() == worker.getUserId()) {
                try (Connection conn = connections.getConnection()) {
                    conn.setAutoCommit(false);
                    try (PreparedStatement workerSt = conn.prepareStatement(UPDATE_WORKER_SQL)) {
                        if (!el.getCoordinates().equals(worker.getCoordinates())) {
                            try (PreparedStatement psCoordinates = conn.prepareStatement(UPDATE_COORDINATES_SQL)) {
                                Coordinates coordinates = worker.getCoordinates();
                                psCoordinates.setLong(1, coordinates.getX());
                                psCoordinates.setDouble(2, coordinates.getY());
                                psCoordinates.setInt(3, coordinates.getId());
                                psCoordinates.executeUpdate();
                            }
                        }

                        if (worker.getOrganization() != null) {
                            if (!worker.getOrganization().equals(el.getOrganization())) {
                                try (PreparedStatement ps = conn.prepareStatement(UPDATE_ORGANIZATION_SQL)) {
                                    Organization organization = worker.getOrganization();
                                    ps.setString(1, organization.getFullName());
                                    ps.setObject(2, organization.getType().name(), Types.OTHER);
                                    ps.setString(3, organization.getPostalAddress().getZipCode());
                                    ps.setDouble(4, organization.getPostalAddress().getTown().getX());
                                    ps.setInt(5, organization.getPostalAddress().getTown().getY());
                                    ps.setDouble(6, organization.getPostalAddress().getTown().getZ());
                                    ps.setString(7, organization.getPostalAddress().getTown().getName());
                                    ps.setString(8, organization.getFullName());

                                    int updatedRows = ps.executeUpdate();
                                    if (updatedRows == 0) {
                                        conn.rollback();
                                        return false;
                                    }
                                }
                            }
                        } else {
                            if (el.getOrganization() != null) {
                                isOrgNull = true;
                                workerSt.setNull(7, java.sql.Types.INTEGER);
                            }
                        }

                        if (!isOrgNull && el.getOrganization() != null) {
                            workerSt.setInt(7, el.getOrganization().getId());
                        } else {
                            workerSt.setNull(7, java.sql.Types.INTEGER);
                        }
                        workerSt.setString(1, worker.getName());
                        workerSt.setDate(2, Date.valueOf(worker.getCreationDate()));
                        workerSt.setFloat(3, worker.getSalary());
                        workerSt.setDate(4,  new java.sql.Date(worker.getStartDate().getTime()));;
                        workerSt.setObject(5, worker.getPosition().name(), Types.OTHER);
                        workerSt.setObject(6, worker.getStatus().name(), Types.OTHER);
                        workerSt.setLong(8, (long) worker.getId());

                        int rowsUpdated = workerSt.executeUpdate();
                        if (rowsUpdated == 0) {
                            conn.rollback();
                            return false;
                        }

                        conn.commit();

                        lock.lock();
                        try {
                            storage.removeIf(w -> w.getId() == el.getId());
                            storage.add(worker);
                        } finally {
                            lock.unlock();
                        }
                    } catch (SQLException e) {
                        conn.rollback();
                        logger.log(Level.WARNING, String.format("Произошла ошибка при выполнии SQL update: %s", e.getMessage()));
                    } finally {
                        conn.setAutoCommit(true);
                    }
                } catch (SQLException e) {
                    logger.log(Level.WARNING, String.format("Произошла ошибка при выполнии SQL update: %s", e.getMessage()));
                }
                logger.log(Level.INFO, String.format("Работник успешно обновлен пользователем %s: %s", worker.getUser().getUsername(), worker));
                return true;
            }
        }
        return false;
    }


        /**
         * Method that removes worker from collection by id.
         *
         * @param id the id
         * @return boolean result of operation
         */
        public boolean removeById(Integer id) {
            lock.lock();
            try (Connection connection = connections.getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_WORKER_SQL)) {
                    preparedStatement.setLong(1, (long) id);
                    int affectedRows = preparedStatement.executeUpdate();
                    logger.log(Level.INFO, "Удаляем объект с id = " + id);
                    if (affectedRows > 0) {
                        storage.removeIf(worker -> worker.getId() == id);
                        return true;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            return false;
        }


        /**
         * Method that dumps workers to file using implementation the ILoader<PriorityQueue<Worker>> interface implementation
         */
        public boolean dumpToTheFile () {
            return loader.save(storage);
        }

        @Override
        public LocalDateTime getCreatedDate () {
            return createdAt;
        }

        @Override
        public String getCollectionType () {
            return storage.getClass()
                    .getSimpleName();
        }
    }
