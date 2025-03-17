# 🎓 대학교 동아리 커뮤니티 앱

## 📌 Problem & Solution

- **문제점**: 검증된 대학교 동아리 목록이 정리된 곳이 없다.  
  → ✅ **해결**: 검증된 동아리 목록 제공
- **문제점**: 학기마다 공지 단톡방을 개설하고 동아리원들을 관리해야 한다.  
  → ✅ **해결**: 동아리 가입 및 공지사항 작성 기능 제공

---

## 🏛️ Architecture

### 🔹 Infrastructure
![Infrastructure](./docs/system_architecture_v1.png)

### 🔹 Software Architecture
![Software Architecture](./docs/Software_Architecture.png)

> **Note**: 각 도메인의 의존성을 줄이기 위해 다른 도메인의 Repository 직접 접근 불가능  
> **DataService**를 통해 허용된 메서드만 사용 가능

---

## Directory Structure

```
📂 circleon 
├── 📂 authentication # JWT 및 이메일 인증 관련 모듈 
├── 📂 common # 공통 유틸리티 및 예외 처리 
├── 📂 config # 애플리케이션 설정
└── 📂 domain # 주요 비즈니스 로직 
    ├── 📂 admin # 관리자 기능 
    ├── 📂 circle # 동아리 관련 로직 
    ├── 📂 post # 게시글 관련 로직 
    ├── 📂 schedule # 일정 관리 기능 
    └── 📂 user # 사용자 관련 기능
```

---

## 🛠 Tech Stack

### 🎯 Backend
<img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white" alt="Java" height="20">  
<img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" height="20">  
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Data JPA" height="20">  
<img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security" height="20">  
<img src="https://img.shields.io/badge/QueryDSL-005F80?style=for-the-badge&logo=apachekafka&logoColor=white" alt="QueryDSL" height="20">
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" height="20">  

### 🗄️ Database & Cache
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" height="20"> <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" height="20">

### ☁️ Cloud & DevOps
<img src="https://img.shields.io/badge/AWS-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white" alt="AWS" height="20"> <img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white" alt="AWS EC2" height="20"> <img src="https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white" alt="AWS RDS" height="20"> <img src="https://img.shields.io/badge/AWS%20ElastiCache-527FFF?style=for-the-badge&logo=amazonaws&logoColor=white" alt="AWS ElastiCache" height="20"> <img src="https://img.shields.io/badge/AWS%20ALB-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white" alt="AWS ALB" height="20">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" height="20"> <img src="https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white" alt="GitHub Actions" height="20">

### 🖼️ Image Processing
<img src="https://img.shields.io/badge/Thumbnailator-0078D7?style=for-the-badge&logo=java&logoColor=white" alt="Thumbnailator" height="20">

---

## 👨‍💻 Contributor

| 주명하 |
|:---:|
| <a href="https://github.com/myeongha" target="_blank"><img src="https://github.com/myeongha.png" width="100" height="100" style="border-radius: 5%;"></a> <br/> <a href="https://github.com/myeongha" target="_blank"><img src="https://img.shields.io/badge/myeongha-181717?style=for-the-social&logo=github&logoColor=white"/></a> |
