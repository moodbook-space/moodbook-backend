# 🎲 Moodbook

## 🕰️ 진행 기간
♠︎ **2025. 7. 3 ~ 2025. 7. 28** ♠︎

<br/>

## 📖 프로젝트 기획

- 기존 도서 플랫폼은 키워드 중심 검색에 의존
- 사용자의 감정 상태나 정서적 맥락을 반영하지 못함
- **'감정 선택 -> 도서 추천 -> 검색어 시각화 -> 히스토리 기록'** 의 흐름을 통해 사용자의 도서 검색 효율 향상

<img width="1000" height="700" alt="image" src="https://github.com/user-attachments/assets/a8b30a6b-eb63-4cc4-85e4-73e06f499c8b" />

<br/>

## 🧐 새로운 추천 흐름

- 감정 태그 클릭 (최대 5개까지 가능)
- 사용자 감정에 기반한 도서를 추천 받음
- 사용자는 감정에 맞는 도서를 추천받음으로써 감정 흐름을 따라가는 몰입형 독서 경험을 설계할 수 있음

<img width="1000" height="700" alt="image" src="https://github.com/user-attachments/assets/0b10b725-6a49-411d-b2e9-80eefe1ea1df" />

<img width="1000" height="700" alt="image" src="https://github.com/user-attachments/assets/e8822cf2-b174-4a31-92da-3a2a29b758be" />

<br/>

## 👨‍💻 문제 인식 및 해결 방안

- 사용자가 추천한 도서를 통해 독후감 작성 및 독서모임에 참여할 수 있음
- 단순한 도서 서비스를 제공받는 것이 아닌 서로가 공감하며 책의 내용을 공유할 수 있는 환경 제공

<img width="1000" height="700" alt="image" src="https://github.com/user-attachments/assets/24112e87-8a2e-4338-85a0-9d3cc1edbca0" />

<img width="1000" height="700" alt="image" src="https://github.com/user-attachments/assets/d9006939-aa4a-41ad-9aa9-09bf4e101b7b" />

<img width="1000" height="700" alt="image" src="https://github.com/user-attachments/assets/170456e1-cc87-474c-a56d-a5b2bc85032f" />

<br/>

## 🧭 ERD

<img width="1000" height="700" alt="image" src="https://github.com/user-attachments/assets/5c8e9503-ff00-48d4-a044-6fc4a12163b0" />

<br/>

## 📜 API 명세서

- 링크: https://documenter.getpostman.com/view/31353886/2sB34mhxma#8b0aed3b-14db-4dac-a541-a902374c5d2c

<img width="1000" height="700" alt="image" src="https://github.com/user-attachments/assets/45372fdf-fc6d-418c-a3e6-20413e5f1aed" />

<br/>

## 🏗️ 시스템 아키텍처

### 전반적인 흐름 구성도

```mermaid
flowchart TB
  subgraph Clients
    FE[Frontend/SPA]:::c
    BOT[Webhook/LLM]:::c
  end

  subgraph API Layer
    MemberCtrl[Member/Auth]
    BookCtrl[Book]
    ReviewCtrl[Review]
    ChatCtrl[WebSocket/Chat]
    NotiCtrl[SSE/Notification]
    SearchCtrl[AISearch]
    EmotionCtrl[Emotion]
  end

  subgraph Services
    MemberSvc
    BookSvc
    ReviewSvc
    ChatSvc
    NotiSvc
    SearchSvc
    EmotionSvc
    BatchJob[Spring Batch]
  end

  subgraph Data
    RDB[(RDB: JPA/QueryDSL)]
    MONGO[(MongoDB: Chat logs)]
    REDIS[(Redis: Token/Cache/PubSub)]
    ES[(Elasticsearch)]
  end

  subgraph External
    Aladin[Aladin OpenAPI]
  end

  FE --> MemberCtrl & BookCtrl & ReviewCtrl & ChatCtrl & NotiCtrl & SearchCtrl & EmotionCtrl
  BOT --> SearchCtrl

  MemberCtrl --> MemberSvc --> RDB & REDIS
  BookCtrl --> BookSvc --> RDB
  ReviewCtrl --> ReviewSvc --> RDB
  ChatCtrl --> ChatSvc --> REDIS & MONGO & RDB
  NotiCtrl --> NotiSvc --> REDIS & RDB
  SearchCtrl --> SearchSvc --> ES & RDB
  EmotionCtrl --> EmotionSvc --> RDB

  BatchJob --> Aladin --> BatchJob --> RDB
  BookSvc -. "BookCreatedEvent" .-> SearchSvc
  SearchSvc --> ES

  classDef c fill:#eef,stroke:#88a,stroke-width:1px;

```

### CI/CD 구성도

<img width="1000" height="700" alt="image" src="https://github.com/user-attachments/assets/e1b0238b-f3e5-46a8-aa52-33bdfea68973" />

<br/>

## 🛠️ 기술 스택

| 구분               | 기술/도구                                                             |
| ---------------- | ----------------------------------------------------------------- |
| **Frontend** | React, TypeScript, Styled-components, React-router |
| **Backend** | Java 17 (LTS), Spring Boot 3.5.3, Spring Security, JPA, QueryDSL, H2        |
| **DataBase**       | MySQL, MongoDB 7.0, Redis, ElasticSearch |
| **Main Tech Stack**       | WebSocket, STOMP, Redis Pub/Sub                                |
| **DevOps**        | Docker, Github Actions, Jenkins, Grafana, Prometheus, Discord, ELK(ElasticSearch + Logstash + Kibana), AWS(S3, RDS, EC2), ElasticCache, Nginx  |

<br/>

## 📁 프로젝트 구조

```
moodbook-backend/
 ├── src/main/java/com/moodbook
 │    ├── domain
 │    │    ├── member
 │    │    ├── book
 │    │    ├── review
 │    │    ├── bookmark
 │    │    ├── chat
 │    │    └── notification
 │    ├── global
 │    │    ├── config
 │    │    ├── exception
 │    │    └── security
 │    └── MoodbookApplication.java
 │
 ├── src/main/resources
 │    ├── application.yml
 │    └── static / templates
 │
 ├── build.gradle
 └── docker-compose.yml
```


