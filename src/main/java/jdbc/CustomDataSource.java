package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private static final Object mutex= new Object();

    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.password = password;
        this.name = name;
    }

    public static CustomDataSource getInstance() {
        CustomDataSource result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    Properties properties = getProperties();
                    instance = result = new CustomDataSource(
                            properties.getProperty("postgres.driver"),
                            properties.getProperty("postgres.url"),
                            properties.getProperty("postgres.password"),
                            properties.getProperty("postgres.name")
                    );
                }
            }
        }
        return result;
    }

    @SneakyThrows
    private static Properties getProperties() {
        ClassLoader classLoader = CustomDataSource.class.getClassLoader();
        Properties properties = new Properties();
        properties.load(classLoader.getResourceAsStream("app.properties"));
        return properties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new CustomConnector().getConnection(url);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new CustomConnector().getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        //ignore
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        //ignore
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
