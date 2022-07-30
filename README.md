# TwoDo-li

![](https://twodo-li.s3.ap-northeast-2.amazonaws.com/ppt_title.jpg)

## 목차

1. [프로젝트 소개](#1._프로젝트_소개)
2. [설계](#2._설계)
    - 협업툴
    - GitHub
    - 아키텍처
    - ERD
    - 기술스택
    - coomit rule
3. 주요기능
    - 이미지?
    - 영상
4. 트러블슈팅
5. 멤버소개

## 1. 프로젝트 소개

### TwoDo-li는?
`친구와 함께 Todo를 달성하고 캐릭터와 내가 함께 성장해 나가는 재미를 느낄 수 있도록 하기 위해 기획한 서비스 입니다.`
    

## ✨ 아키텍쳐
<br>

<a href='https://ifh.cc/v-M592Oz' target='_blank'><img src='https://ifh.cc/g/M592Oz.png' border='0'></a>


## 💻 ERD
<a href='https://ifh.cc/v-ta1yHw' target='_blank'><img src='https://ifh.cc/g/ta1yHw.jpg' border='0'></a>
**Backend Tech Stack**

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

| 메시지명     |설명|
|----------|---|
| feat     |새로운 기능 추가 관련|
| fix      |버그 수정|
| test     |테스트 코드, 리팩토링 테스트 코드 추가|
| refactor |코드 리팩토링(기능향상)|
| chore    |빌드 업무 수정, 패키지 매니저 수정|
| docs     |문서 수정(md, git관련 파일, 이미지파일 수정)|
| style    |코드 formatting, 세미콜론(;) 누락, 코드 변경이 없는 경우|




## 🧨Trouble Shooting

```
1. CICD 적용 중 .properties 파일 인식 이슈
```

>**도입 이유** : 릴리스 속도를 단축하고, 개발의 효율성을 극대화 하기 위해 CI/CD 도입

>**문제 발생** : Github Action으로 빌드시에, .gitignore로 설정한 .properties파일을 불러오지 못하는 문제가 발생했습니다. 그로 인해 AWS S3 및 RDS에 접근하지 못하는 문제가 발생했습니다.

>**의사 결정** : .properties파일을 /.github/workflows/에 위치한 yml파일에 환경변수로 추가하고, 그 환경변수를 Github에서 시크릿으로 직접 넣어주었습니다.
그리고 AWS Codedeploy에 전달하여 해결하였습니다.

---

<a href='https://ifh.cc/v-GXCsyT' target='_blank'><img src='https://ifh.cc/g/GXCsyT.png' border='0'></a>
<br>
**deploy.yml**

<a href='https://ifh.cc/v-552pza' target='_blank'><img src='https://ifh.cc/g/552pza.png' border='0'></a>
<br>
**SecretKey 등록**
