package io.github.rscai.mockdb.dao;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import io.github.rscai.mockdb.model.Category;
import io.github.rscai.mockdb.schema.Dev;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DataSourceConfig.class, DaoConfig.class})
@SqlGroup(value = {
    @Sql(scripts = "/io/github/rscai/mockdb/dao/CategoryDaoTest.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)}
)
public class CategoryDaoTest extends OracleDBTestBase {

  @BeforeClass
  public static void beforeClass() throws Exception {
    String dockerHost = System.getProperty("docker.host", "http://ubuntu-vb:4243");
    String databaseHost = System.getProperty("docker.database.host", "ubuntu-vb");
    String image = System.getProperty("docker.image.oracle", "sath89/oracle-xe-11g");
    String urlTemplate = System
        .getProperty("docker.database.url.template", "jdbc:oracle:thin:@%s:%d:xe");
    String databaseUsername = System.getProperty("docker.database.username", "system");
    String databasePassword = System.getProperty("docker.database.password", "oracle");
    String driverClass = System
        .getProperty("docker.database.driverClass", "oracle.jdbc.driver.OracleDriver");

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

    Thread.sleep(90000);
    System.setProperty("docker.database.containerId", creation.id());

    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
        String.format(urlTemplate, databaseHost, port));
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, databaseUsername);
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, databasePassword);
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, driverClass);
  }

  @AfterClass
  public static void afterClass() throws Exception {
    String dockerHost = System.getProperty("docker.host", "http://ubuntu-vb:4243");
    String containerId = System.getProperty("docker.database.containerId");

    DefaultDockerClient docker = DefaultDockerClient.builder().uri(new URI(dockerHost)).build();
    docker.killContainer(containerId);
    docker.removeContainer(containerId);
  }

  @Autowired
  private CategoryDao categoryDao;

  @Override
  protected IDataSet getDataSet() throws DataSetException {
    final DefaultTable category = new DefaultTable(Dev.CATEGORY);
    category.addRow(new Object[]{
        1, "level 1", null
    });
    category.addRow(new Object[]{
        21, "level 2 - 1", 1
    });
    category.addRow(new Object[]{
        22, "level 2 - 2", 1
    });
    category.addRow(new Object[]{
        23, "level 2 - 3", 1
    });
    category.addRow(new Object[]{
        31, "level 3 - 1 - 1", 21
    });
    category.addRow(new Object[]{
        32, "level 3 - 2 - 1", 22
    });
    category.addRow(new Object[]{
        41, "level 4 - 2 - 1 - 1", 32
    });

    return new DefaultDataSet(new ITable[]{category});
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public void testFindById() throws Exception {
    final long id = 1;

    Category actual = categoryDao.findById(id).get();

    assertThat(actual.getId(), is(1L));
    assertThat(actual.getName(), is("level 1"));
    assertThat(actual.getSubCategories(), hasSize(3));
    assertThat(actual.getSubCategories().get(0).getId(), is(21L));
    assertThat(actual.getSubCategories().get(0).getName(), is("level 2 - 1"));
    assertThat(actual.getSubCategories().get(0).getSubCategories(), hasSize(1));

    assertThat(actual.getSubCategories().get(1).getId(), is(22L));
    assertThat(actual.getSubCategories().get(1).getName(), is("level 2 - 2"));
    assertThat(actual.getSubCategories().get(1).getSubCategories(), hasSize(1));
    assertThat(actual.getSubCategories().get(1).getSubCategories().get(0).getName(),
        is("level 3 - 2 - 1"));
    assertThat(actual.getSubCategories().get(1).getSubCategories().get(0).getSubCategories().get(0)
        .getName(), is("level 4 - 2 - 1 - 1"));
  }
}
