# os1-1_slack_bot
TmaxOS OS1-1 팀의 슬랙봇 프로젝트 입니다.
식사 메뉴 공지 및 일정 Reminder 기능을 제공하기 위해서 시작 하였다.

---

## 사용한 것
#### openjdk11
#### spring v2.4.1
- jsp
  - tomcat jasper
  - jstl
- lombok 
- mongodb
#### gradle v6.7.1
#### mongodb v4.4.4

---
## Prerequisites
* `spring profile` 을 추가해서 구동해야 함
  * application.properties 참고
    

application.properties 파일 내용
```
...

// application-slack.properties, application-mongodb.properties 가 필요!
spring.profiles.include=slack,mongodb 
```

application-mongodb.properties에 아래와 같이 기입 
```
# mongodb configuration
spring.data.mongodb.host=${ip}
spring.data.mongodb.port=${port}
spring.data.mongodb.database=${db}
spring.data.mongodb.username=${user}
spring.data.mongodb.password=${password}
```

application-slack.properties에 아래와 같이 기입
```
slack-post-message-url=https://slack.com/api/chat.postMessage
slack-oauth-token=${oauth token}
menu-path=${menu excel file.. xlsx format}
```

---

## TODO

* 일정 Reminder
* 메뉴 업로드
* Dockerfile

---

## 여담
* 공부중입니다. web 초보