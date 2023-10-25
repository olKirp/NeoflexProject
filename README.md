# NeoflexProject
Учебный проект "Кредитный конвейер", созданный на курсах от компании «Neoflex». 

[![Build and Test Conveyor Application](https://github.com/olKirp/NeoflexProject/actions/workflows/github-actions-neoflex.yml/badge.svg?branch=ci)](https://github.com/olKirp/NeoflexProject/actions/workflows/github-actions-neoflex.yml)

[![codecov](https://codecov.io/gh/olKirp/NeoflexProject/graph/badge.svg?token=3K23QAU7AR)](https://codecov.io/gh/olKirp/NeoflexProject)

### Используемые технологии
-	Spring Boot
-	Maven
-	Hibernate
-	PostgreSQL
-	Kafka
-	Docker
-   Swagger/OpenAPI
   
### О приложении
&nbsp;&nbsp;  Это backend-приложение для банка, позволяющее клиентам просматривать кредитные предложения, оставлять заявки на получение займа и заключать с банком договор. 

&nbsp;&nbsp; Состоит из нескольких микросервисов. Коммуникация между ними осуществляется с помощью Spring Cloud OpenFeign и Kafka. Также настроена отправка писем на электронную почту клиентам. 

![app schema](https://github.com/olKirp/NeoflexProject/blob/main/images/neoflex%20app.png "Application schema")

&nbsp; &nbsp; Для хранения данных используется база данных PostgreSQL. 
![database schema](https://github.com/olKirp/NeoflexProject/blob/main/images/database.png "Database schema")

&nbsp; &nbsp; В docker-compose настроено взаимодействие всех компонентов приложения. 
CI настроен с помощью GitHub Actions. 
