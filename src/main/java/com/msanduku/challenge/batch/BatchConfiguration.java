package com.msanduku.challenge.batch;

import com.msanduku.challenge.lib.ExcelRowMapper;
import com.msanduku.challenge.model.Users;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author Clifford Owino
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    int CHUNK_COUNT = 3;

    int LINES_TO_SKIP = 2;

    private static final String QUERY_INSERT_STMT = "INSERT INTO users(PKColumn,EmpID , NamePrefix, FirstName, MiddleInitial, LastName, Gender, EMail, FatherName, MotherName, MotherMaidenName, DateofBirth) "
            + "VALUES ( null,:EmpID , :NamePrefix, :FirstName, :MiddleInitial, :LastName, :Gender, :EMail, :FatherName, :MotherName, :MotherMaidenName, :DateofBirth)";

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/test");
        dataSource.setUsername("root");
        dataSource.setPassword("");

        return dataSource;
    }

    @Bean
    ItemReader<Users> reader() {

        PoiItemReader<Users> reader = new PoiItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource("100000RecordsFull.xlsx"));
        reader.setRowMapper(excelRowMapper());
        return reader;
    }

    private RowMapper<Users> excelRowMapper() {
        return new ExcelRowMapper();
    }

    @Bean
    public TaskItemProcessor processor() {
        return new TaskItemProcessor();
    }

//https://www.petrikainulainen.net/programming/spring-framework/spring-batch-tutorial-writing-information-to-a-database-with-jdbc/
    @Bean
    public JdbcBatchItemWriter<Users> writer() {
        JdbcBatchItemWriter<Users> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setDataSource(dataSource);
        writer.setSql(QUERY_INSERT_STMT);

        ItemSqlParameterSourceProvider<Users> paramProvider = new BeanPropertyItemSqlParameterSourceProvider<>();

        writer.setItemSqlParameterSourceProvider(paramProvider);

        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Users, Users>chunk(CHUNK_COUNT)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job importUserJob() {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

}
