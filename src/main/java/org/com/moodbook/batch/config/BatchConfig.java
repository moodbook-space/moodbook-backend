package org.com.moodbook.batch.config;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.HttpServerErrorException;

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
            .faultTolerant()

            // skip: 아이템 단위로 문제가 생겨도 다음 아이템 처리 계속
            .skip(DataIntegrityViolationException.class)
            .skip(DuplicateKeyException.class)
            .skip(JsonSyntaxException.class)
            .skip(IllegalArgumentException.class)
            .skipLimit(1_000)

            // retry: 일시적 오류(네트워크/HTTP 5xx 등)인 경우 재시도
            .retry(IOException.class)
            .retry(HttpServerErrorException.class)
            .retryLimit(3)

            .build();
    }

}
