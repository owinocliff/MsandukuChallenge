package com.msanduku.challenge.batch;

import com.msanduku.challenge.lib.ExcelRowMapper;
import com.msanduku.challenge.model.Users;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author Clifford Owino
 */
@Configuration
@EnableBatchProcessing
@EnableAsync
public class BatchConfiguration {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    int CHUNK_COUNT = 60000;

    int LINES_TO_SKIP = 2;

    private static final String QUERY_INSERT_STMT = "INSERT INTO users(PKColumn,EmpID , NamePrefix, FirstName, MiddleInitial, LastName, Gender, EMail, FatherName, MotherName, MotherMaidenName, DateofBirth) "
            + "VALUES ( null,:EmpID , :NamePrefix, :FirstName, :MiddleInitial, :LastName, :Gender, :EMail, :FatherName, :MotherName, :MotherMaidenName, :DateofBirth)";

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSourceLocal = new DriverManagerDataSource();
        dataSourceLocal.setDriverClassName("com.mysql.jdbc.Driver");
        dataSourceLocal.setUrl("jdbc:mysql://localhost/test");
        dataSourceLocal.setUsername("root");
        dataSourceLocal.setPassword("");

        return dataSourceLocal;
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

    @Bean
    public TaskItemWriter fileWriter() {
        return new TaskItemWriter();
    }

    @Bean
    public CompositeItemWriter compositeItemWriter() {
        CompositeItemWriter<Users> compositeItemWriter = new CompositeItemWriter<>();
        List<ItemWriter<? super Users>> delegates = new ArrayList<>();
        delegates.add(dbWriter());
        delegates.add(fileWriter());
        compositeItemWriter.setDelegates(delegates);
        return compositeItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<Users> dbWriter() {
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

        TaskletStep processingStep = stepBuilderFactory.get("step1")
                .<Users, Users>chunk(CHUNK_COUNT)
                .reader(reader())
                .processor(processor())
                .writer(compositeItemWriter())
//               .transactionManager(txManager)
                .build();

        return processingStep;
    }

    @Bean
    public Job importUserJob() {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }

}
