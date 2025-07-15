package org.com.moodbook.batch.config;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.batch.dto.BatchBookResponse;
import org.com.moodbook.batch.job.BookApiReader;
import org.com.moodbook.batch.job.BookItemProcessor;
import org.com.moodbook.batch.job.BookItemWriter;
import org.com.moodbook.book.entity.Book;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final BookItemProcessor processor;
    private final BookItemWriter writer;
    private final BookApiReader reader;

    @Bean
    public Job bookImportJob() {
        return new JobBuilder("bookImportJob", jobRepository)
            .start(bookImportStep())
            .build();
    }

    @Bean
    public Step bookImportStep() {
        return new StepBuilder("bookImportStep", jobRepository)
            .<BatchBookResponse, Book>chunk(100, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }

}
