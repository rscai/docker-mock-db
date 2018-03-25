package io.github.rscai.mockdb.dao;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import java.io.Closeable;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DockerizedDataSource implements DataSource, Closeable {

  private static final Log LOGGER = LogFactory.getLog(DockerizedDataSource.class);

  private BasicDataSource delegate;

  private String dockerHost;

  private String databaseHost;

  private String image;

  private String urlTemplate;

  private String databaseUsername;

  private String databasePassword;

  private String driverClass;

  private String containerId;

  public void setDockerHost(String dockerHost) {
    this.dockerHost = dockerHost;
  }

  public void setDatabaseHost(String databaseHost) {
    this.databaseHost = databaseHost;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setUrlTemplate(String urlTemplate) {
    this.urlTemplate = urlTemplate;
  }

  public void setDatabaseUsername(String databaseUsername) {
    this.databaseUsername = databaseUsername;
  }

  public void setDatabasePassword(String databasePassword) {
    this.databasePassword = databasePassword;
  }

  public void setDriverClass(String driverClass) {
    this.driverClass = driverClass;
  }

  @PostConstruct
  public void init() {
    ImmutablePair<String, Integer> idAndPort = createContainer()
        .orElseThrow(() -> new RuntimeException("Create container fail"));
    containerId = idAndPort.left;
    constructDataSource(String.format(urlTemplate, databaseHost, idAndPort.right));
  }
  
  @Override
  public void close() {
    try {
      delegate.close();
      destroyContainer();
    } catch (SQLException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    return delegate.getConnection();
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return delegate.getConnection(username, password);
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return delegate.getParentLogger();
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return delegate.getLoginTimeout();
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    delegate.setLoginTimeout(seconds);
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    delegate.setLogWriter(out);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return delegate.getLogWriter();
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return delegate.isWrapperFor(iface);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return delegate.unwrap(iface);
  }

  private Optional<ImmutablePair<String, Integer>> createContainer() {
    try {
      DefaultDockerClient docker = DefaultDockerClient.builder().uri(new URI(dockerHost)).build();
      Map<String, List<PortBinding>> portBindings = new TreeMap<>();
      portBindings.put("1521/tcp", Arrays.asList(PortBinding.randomPort("0.0.0.0")));
      ContainerConfig config = ContainerConfig.builder().image(image)
          .hostConfig(HostConfig.builder().portBindings(portBindings).build())
          .env("WEB_CONSOLE=false")
          .build();
      ContainerCreation creation = docker.createContainer(config);
      docker.startContainer(creation.id());
      ContainerInfo info = docker.inspectContainer(creation.id());
      final int port = Integer
          .valueOf(info.networkSettings().ports().get("1521/tcp").get(0).hostPort());

      Thread.sleep(60000);
      return Optional.of(ImmutablePair.of(creation.id(), port));
    } catch (URISyntaxException | DockerException | InterruptedException ex) {
      LOGGER.error(ex.getMessage(), ex);
      return Optional.empty();
    }
  }

  private void destroyContainer() {
    try {
      DefaultDockerClient docker = DefaultDockerClient.builder().uri(new URI(dockerHost)).build();
      docker.killContainer(containerId);

    } catch (URISyntaxException | DockerException | InterruptedException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }

  private void constructDataSource(final String url) {
    delegate = new BasicDataSource();
    delegate.setUrl(url);
    delegate.setUsername(databaseUsername);
    delegate.setPassword(databasePassword);
    delegate.setDriverClassName(driverClass);
  }
}
