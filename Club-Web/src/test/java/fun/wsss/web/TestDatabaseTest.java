package fun.wsss.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试环境自检：跑测试不应该依赖本机的 MySQL。
 *
 * <p>如果这里变了，说明 src/test/resources/application.yml 没生效，
 * 那 CI 上的 mvn test 会因为连不上数据库而失败。
 */
@SpringBootTest
class TestDatabaseTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testsUseInMemoryDatabase() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            assertThat(url).startsWith("jdbc:h2:");
        }
    }
}
