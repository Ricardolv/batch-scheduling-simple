package com.richard.config.batch;

import com.richard.model.Voltage;
import com.richard.tasks.MyTaskOne;
import com.richard.tasks.MyTaskTwo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    public final JobBuilderFactory jobBuilderFactoryVoltage;
    public final StepBuilderFactory stepBuilderFactoryVoltage;
    public final JobBuilderFactory jobBuilderFactoryTask;
    public final StepBuilderFactory stepBuilderFactoryTask;

    public BatchConfig(JobBuilderFactory jobBuilderFactoryVoltage, StepBuilderFactory stepBuilderFactoryVoltage, JobBuilderFactory jobBuilderFactoryTask, StepBuilderFactory stepBuilderFactoryTask) {
        this.jobBuilderFactoryVoltage = jobBuilderFactoryVoltage;
        this.stepBuilderFactoryVoltage = stepBuilderFactoryVoltage;
        this.jobBuilderFactoryTask = jobBuilderFactoryTask;
        this.stepBuilderFactoryTask = stepBuilderFactoryTask;
    }


    @Bean
    public FlatFileItemReader<Voltage> reader() {
        return new FlatFileItemReaderBuilder<Voltage>()
            .name("voltItemReader")
            .resource(new ClassPathResource("Volts.csv"))
            .delimited()
            .names(new String[]{"volt", "time"})
            .lineMapper(lineMapper())
            .fieldSetMapper(new BeanWrapperFieldSetMapper<Voltage>() {{
                setTargetType(Voltage.class);
            }})
            .build();
    }

    @Bean
    public LineMapper<Voltage> lineMapper() {

        final DefaultLineMapper<Voltage> defaultLineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(";");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[] { "volt", "time" });

        final VoltageFieldSetMapper fieldSetMapper = new VoltageFieldSetMapper();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }

    @Bean
    public VoltageProcessor processor() {
        return new VoltageProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Voltage> writer(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Voltage>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO voltage (volt, time) VALUES (:volt, :time)")
            .dataSource(dataSource)
            .build();
    }

    @Bean
    @Primary
    public Job importVoltageJob(NotificationListener listener, Step step1) {
        return jobBuilderFactoryVoltage.get("importVoltageJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Voltage> writer) {
        return stepBuilderFactoryVoltage.get("step1")
            .<Voltage, Voltage> chunk(10)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build();
    }

    @Bean
    public Step stepOne(){
        return stepBuilderFactoryTask.get("stepOne")
            .tasklet(new MyTaskOne())
            .build();
    }

    @Bean
    public Step stepTwo(){
        return stepBuilderFactoryTask.get("stepTwo")
            .tasklet(new MyTaskTwo())
            .build();
    }

    @Bean
    public Job demoJob(){
        return jobBuilderFactoryTask.get("demoJob")
            .incrementer(new RunIdIncrementer())
            .start(stepOne())
            .next(stepTwo())
            .build();
    }

}
