# TwoDo-li


<img src="https://i.imgur.com/e0c30l9.jpg" title="ppt_title.jpg"/>

# 목차



1. [프로젝트 소개](##_1._🎊_프로젝트_소개)
2. [설계](#2._🛠_설계)
3. [주요기능](#3._🎊_주요_기능)
4. [트러블슈팅](#4._🧨_트러블슈팅)
5. [멤버소개](#5._🤲_멤버_소개)

# 1. 🎊 프로젝트 소개

## TwoDo-li는?

`친구와 함께 Todo를 달성하고 캐릭터와 내가 함께 성장해 나가는 재미를 느낄 수 있도록 하기 위해 기획한 서비스 입니다.`

## 기획 배경

>기획하게 된 배경은 바쁜 현대 사회 속에서 자기 개발에 관심을 가지고 좋은 습관을 들이기 위해 노력하는 사람들이 많아지고 있습니다.<br>
>그 수단으로 Todo 리스트를 작성하지만, 혼자 할 때 미루거나 실행하지 못한 경험을 가지고 있습니다.<br>
>그렇기에 `Todo를 작성하고 실행할 때 친구와 같이하고, 그 과정에서 자신의 캐릭터를 성장시킬 수 있다면 더 재밌게 좋은 습관을 유지할 수 있지 않을까?` 라는 아이디어로 기획하게 되었습니다.

# 2. 🛠 설계



## ✨ 아키텍쳐

<br>
<img src="https://i.imgur.com/Tnj7P0J.png" title="image (4).png"/>

## 💻 ERD

<br>
<img src="https://i.imgur.com/kEYAy3z.png" title="ErdCloud.PNG"/>

## 📚 백엔드 기술 스텍

<img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">
<img src="https://img.shields.io/badge/Springboot-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=AmazonAWS&logoColor=white">
<img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">
<img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">
<img src="https://img.shields.io/badge/codedeploy-6DB33F?style=for-the-badge&logo=codedeploy&logoColor=white">
<img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white">
<img src="https://img.shields.io/badge/Apache JMeter-D22128?style=for-the-badge&logo=Apache JMeter&logoColor=white">
<img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white">


## 🛒 커밋 종류

> 수정한 종류에 따라 커밋 메시지를 선택

|메시지명|설명|
|----|---|
| feat |새로운 기능 추가 관련|
| fix |버그 수정|
| test |테스트 코드, 리팩토링 테스트 코드 추가|
| refactor |코드 리팩토링(기능향상)|
| chore |빌드 업무 수정, 패키지 매니저 수정|
| docs |문서 수정(md, git관련 파일, 이미지파일 수정)|
| style |코드 formatting, 세미콜론(;) 누락, 코드 변경이 없는 경우|

# 3. 🎊 주요 기능



- 검색을 통한 친구찾기와 매칭 기능을 통해 친구와 함께하는 투두
- 투두 달성률을 보여주는 일간, 주간, 월간 투두 통계
- 투두 달성을 통한 캐릭터 육성
- 투두 달성을 통해 얻은 재화를 이용한 나만의 캐릭터 꾸미기
- 사진 등록을 통한 투두 인증 및 완료 시스템 구현

# 4. 🧨 트러블슈팅



### ‼CICD 적용 중 .properties 파일 인식 이슈

> **도입 이유** : 릴리스 속도를 단축하고, 개발의 효율성을 극대화 하기 위해 CI/CD 도입

> **문제 발생** : Github Action으로 빌드시에, .gitignore로 설정한 .properties파일을 불러오지 못하는 문제가 발생했다. 그로 인해 AWS S3 및 RDS에 접근하지 못하는 문제가
> 발생했다.

> **의사 결정** : .properties파일을 /.github/workflows/에 위치한 yml파일에 환경변수로 추가하고, 그 환경변수를 Github에서 시크릿으로 직접 넣어주었다.
> 그리고 AWS Codedeploy에 전달하여 해결하였다.

<img src='https://ifh.cc/g/GXCsyT.png' border='0'><br>
**deploy.yml**

<img src='https://ifh.cc/g/552pza.png' border='0'><br>
**SecretKey 등록**

----

### ‼예외처리

> **도입 이유** : 예외처리를 전역으로 처리하여 유지보수를 편하게 하기 위해서 도입

> **문제 발생** : Jwt 토큰에 대한 예외처리는 ExceptionHandler가 처리하지 못해서 다른 방식으로 처리를 해야할 것으로 보여졌다.

> **의사 결정** : JwtFilter는 Dispatcher Servlet보다 앞 단에 있고, 전역예외처리 한 것은 뒷 단에 있기 때문에
> JwtFilter에서 보낸 예외는 ExceptionHandler로 처리하지 못한다.
> 그래서 Jwt토큰에 대한 예외처리로는 AuthenticationEntryPoint에서 403 에러코드를 보내도록 결정하였다.

---

### ‼통계 API

> **요구사항 및 문제** : 일간 통계의 경우 이전 7일동안 각 날짜에 Todo의 달성 개수를 출력해 줘야했다.
> 이때 Querydsl을 사용하여 데이터를 가져오려고 결정했다.
> 그런데 해당 날짜에 데이터가 없는 경우 0으로 나오는 것을 기대했으나, 데이터 0이 출력이 되지 않았다.

> **대안** : Map자료구조, CTE

> **의사 결정** : HashMap자료 구조를 사용하여 날짜(Key)에 원하는 값(Value)을 대입하여 넣기 적합하다고 생각했다.
> 그리고 LinkedHashMap으로 입력된 순서를 정해주면 날짜 순으로 데이터를 출력하기 적합하다고 생각하여 도입을 결정했다.
 
---

### ‼JMeter를 활용한 성능 개선

> **문제 발생** : 비 정상적인 쿼리 생성 및 데이터 처리량

> **의사결정** : 비즈니스 로직 일부 수정 후 Querydsl적용하였다.

<img src="https://i.imgur.com/ayzJnYG.png" title="image (5).png"/><br>
**JMeter 테스트 중**

## 5. 🤲 멤버 소개


| 이름      | 포지션        | 개인 깃허브 or 이메일          |
|---------|------------|------------------------------|
| **함형준** | `BackEnd`  | https://github.com/hyeongjun-Ham |
| **백종석** | `BackEnd`  | https://github.com/devjjongs |
| **전성영** | `BackEnd`  | https://github.com/junsj119 |
| **김환희** | `FrontEnd` |  |
| **윤가람** | `Designer` |  |

