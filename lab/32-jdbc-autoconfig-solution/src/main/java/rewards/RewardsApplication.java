package rewards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

// TODO-07 : Switch to imperative `DataSource` configuration

// TODO-08 : Switch to imperative `DataSource` configuration

// TODO-10 : Disable the `DataSource` autoconfiguration exclusion annotation.

@SpringBootApplication
@ComponentScan("config")
//@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class RewardsApplication {
    private final Logger logger
            = LoggerFactory.getLogger(RewardsApplication.class);
    
    private static final String SQL = "SELECT count(*) FROM T_ACCOUNT";

    public static void main(String[] args) {
        SpringApplication.run(RewardsApplication.class,args);
    }

    @Component
    public final class QueryAccountCountRunner
            implements CommandLineRunner {

        private final JdbcTemplate jdbcTemplate;

        public QueryAccountCountRunner(DataSource dataSource) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }

        @Override
        public void run(String... args) throws Exception {
            long accountCount
                    = this.jdbcTemplate.queryForObject(SQL, Long.class);
            logger.info("Number of accounts:{}", accountCount);
        }
    }
}
