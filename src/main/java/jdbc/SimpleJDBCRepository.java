package jdbc;


import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Getter
@Setter
public class SimpleJDBCRepository {

    private static final String CREATE_USER_SQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE myusers SET firstname = ?, lastname = ?, age =? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM myusers WHERE id = ?";
    private static final String FIND_USER_BY_ID_SQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String FIND_USER_BY_NAME_SQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String FIND_ALL_USER_SQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        try (
                Connection c = CustomDataSource.getInstance().getConnection();
                PreparedStatement preparedStatement =
                        c.prepareStatement(CREATE_USER_SQL, RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setInt(3, user.getAge());
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            resultSet.next();
            return resultSet.getLong(1);

        } catch (SQLException e) {
            throw new SolutionException(e);
        }
    }

    public User findUserById(Long userId) {
        try (
                Connection c = CustomDataSource.getInstance().getConnection();
                PreparedStatement preparedStatement =
                        c.prepareStatement(FIND_USER_BY_ID_SQL)
        ) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return userFromResultSet(resultSet);

        } catch (SQLException e) {
            throw new SolutionException(e);
        }
    }

    public User findUserByName(String userName) {
        try (
                Connection c = CustomDataSource.getInstance().getConnection();
                PreparedStatement preparedStatement =
                        c.prepareStatement(FIND_USER_BY_NAME_SQL)
        ) {
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return userFromResultSet(resultSet);

        } catch (SQLException e) {
            throw new SolutionException(e);
        }
    }

    public List<User> findAllUser() {
        try (
                Connection c = CustomDataSource.getInstance().getConnection();
                Statement statement = c.createStatement()
        ) {
            ResultSet resultSet = statement.executeQuery(FIND_ALL_USER_SQL);
            List<User> userList = new ArrayList<>();
            while (resultSet.next()) {
                userList.add(userFromResultSet(resultSet));
            }
            return userList;

        } catch (SQLException e) {
            throw new SolutionException(e);
        }
    }

    public User updateUser(User user) {
        try (
                Connection c = CustomDataSource.getInstance().getConnection();
                PreparedStatement preparedStatement =
                        c.prepareStatement(UPDATE_USER_SQL)
        ) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setInt(3, user.getAge());
            preparedStatement.setLong(4, user.getId());

            int countUpdatedUsers = preparedStatement.executeUpdate();
            if (countUpdatedUsers != 1) {
                throw new SQLException("can't update user id=" + user.getId());
            }
            return findUserById(user.getId());

        } catch (SQLException e) {
            throw new SolutionException(e);
        }
    }

    public void deleteUser(Long userId) {
        try (
                Connection c = CustomDataSource.getInstance().getConnection();
                PreparedStatement preparedStatement =
                        c.prepareStatement(DELETE_USER)
        ) {
            preparedStatement.setLong(1, userId);

            int countUpdatedUsers = preparedStatement.executeUpdate();
            if (countUpdatedUsers != 1) {
                throw new SQLException("can't delete user id=" + userId);
            }

        } catch (SQLException e) {
            throw new SolutionException(e);
        }
    }

    private User userFromResultSet(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .firstName(resultSet.getString("firstname"))
                .lastName(resultSet.getString("lastname"))
                .age(resultSet.getInt("age"))
                .build();
    }
}
