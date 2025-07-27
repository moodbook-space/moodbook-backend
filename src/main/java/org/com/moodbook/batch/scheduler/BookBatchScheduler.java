package org.com.moodbook.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job bookImportJob;

    // 매일 자정마다 실행 (초, 분, 시, 일, 월, 요일)
    // 현재는 매일 자정마다 실행하는 것으로 세팅
    // 테스트하고 싶으면 @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 * * * *")
    public void runBookBatchJob() {
        try {
            JobExecution jobExecution = jobLauncher.run(bookImportJob, new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis()) // JobInstance 구분
                .toJobParameters());
            log.info("도서 배치 작업 실행 완료 - 상태: {}", jobExecution.getStatus());
        } catch (Exception e) {
            log.error("도서 배치 작업 실행 실패", e);
        }
    }

}
