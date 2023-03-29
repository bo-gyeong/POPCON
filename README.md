# 공통 프로젝트 D201

![Untitled](https://user-images.githubusercontent.com/72604908/228416497-dadb6206-806a-47c3-8464-d154a0e4727b.png)

# **🔖 프로젝트 소개**

## 🤷‍♀️ 어느새 유효기간을 넘겨버린 기프티콘, 아깝지 않으신가요?

- 쉽고, 편하고, 친절한 **POPCON**과 함께 기프티콘을 관리해보세요
- 갤러리와 문자에서 이미지를 읽어와 자동으로 기프티콘을 등록해줘요
    - 기프티콘 정보를 자동으로 인식해 직접 입력할 필요가 없어요
- 원하는 시간대에 유효기간 만료 전 알림을 받을 수 있어요

## 🤳 간편하게 흔들어서 바로 사용하세요!

- 기기를 흔들면 현위치에서 바로 쓸 수 있는 기프티콘이 팦💥 등장해요
- 지도 탭에서는 내가 가진 기프티콘들을 쓸 수 있는 매장의 정보들을 볼 수 있어요

## 🎁 고마운 사람의 집 앞에 기프티콘을 선물해보세요!

- 지도에서 원하는 위치에, 원하는 기프티콘을 두고갈 수 있어요
- 다른 사람이 두고 간 기프티콘을 주워갈 수 있어요
- 기프티콘을 득템하면 감사메세지는 필수!
- 친구들과의 재밌는 놀이로, 고마운 사람에게 마음을 전할 수단으로 다양하게 활용해보세요
- 집에서 자고있던 워치를 사용할 기회가 될수도,,?✨

---

# **📚 기술스택**

![Untitled 1](https://user-images.githubusercontent.com/72604908/228416677-3b57880f-5c88-44aa-8802-758de76c73c2.png)

| 분야 | 사용기술 |
| --- | --- |
| FrontEnd | Android(Kotlin), MVVM |
| BackEnd | SpringBoot, FCM |
| Database | MariaDB |
| DevOps | AWS EC2, docker, GitLab Runner, Google Cloud Platform |
| Tool | Jira, Notion, IntelliJ, AndroidStudio, GitLab |
| Design | Figma |

---

# 🧬 **아키텍처**

![Untitled 2](https://user-images.githubusercontent.com/72604908/228416567-2af0fccc-3cfe-42e4-9167-fbe8f7d66655.png)

---

# 🔑 ERD

![Untitled 3](https://user-images.githubusercontent.com/72604908/228416726-443f5523-5625-4051-a0d5-486372ffc519.png)

---

# 📝 **API 명세서**

- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) (포스트맨으로 수정할것!)
- Brand controller
    
    
    Get
    /api/v1/brands/orderby_gifticon
    기프티콘 개수 많은 순서대로 정렬된 브랜드 리스트
    
    | Get | /api/v1/brands/orderby_gifticon | 기프티콘 개수 많은 순서대로 정렬된 브랜드 리스트 |
    | --- | --- | --- |
- File controller
    
    
    | POST | /api/v1/files/add_origin | 기프티콘 등록위해 원본 이미지 리스트 업로드 |
    | --- | --- | --- |
    | POST | /api/v1/files/register_gifticon | 등록하기 버튼 누른 후 상품,바코드 이미지 저장 및 db 업데이트 |
    
- Gifticon Controller
    
    
    | PUT | /api/v1/gifticons | 기프티콘 수정 |
    | --- | --- | --- |
    | POST | /api/v1/gifticons | 기프티콘 저장 |
    | DELETE | /api/v1/gifticons | 기프티콘 삭제 |
    | GET | /api/v1/gifticons/{barcode_num} | 기프티콘 조회 |
    | GET | /api/v1/gifticons/{email}/{social} | 유저가 가지고 있는 기프티콘 조회 |
    | GET | /api/v1/gifticons/{email}/{social}/map | 지도에서 띄우는 기프티콘 리스트 |
    | POST | /api/v1/gifticons/brand | 기프티콘 브랜드별 정렬 |
    | GET | /api/v1/gifticons/brandsort/{email}/{social} | 브랜드 기프티콘 순으로 정렬 |
    | GET | /api/v1/gifticons/check | 기프티콘 유호기간 체크 후 상태 변경 / 서버용 APIPOST |
    | POST | /api/v1/gifticons/history | 기프티콘 히스토리 |
- Location Controller
    
    
    | POST | /api/v1/local/search | 현위치 기반 기프티콘 사용가능 한 모든 매장 |
    | --- | --- | --- |
    | POST | /api/v1/local/search/byBrand | 현위치 기반 기프티콘 사용가능 한 지정 브랜드 매장 |
    | POST | /api/v1/local/shake | 흔들었을때 사용가능한 주변 매장 브랜드 |
- Present Controller
    
    
    | POST | /api/v1/presents/get_present | 기부 줍기 |
    | --- | --- | --- |
    | POST | /api/v1/presents/give_present | 기부 버리기 |
    | POST | /api/v1/presents/possible_list | 가까운 선물 리스트, 줍기가능한 선물 리스트 |
- User Controller
    
    
    | GET | /api/v1/user/getlevel | 사용자레벨 |
    | --- | --- | --- |
    | POST | /api/v1/user/login | 로그인 |
    | GET | /api/v1/user/refresh | 토크재발급 |
    | POST | /api/v1/user/update | 사용자정보 변경 |
    | DELETE | /api/v1/user/withdrawal | 사용자정보 삭제 |


- Gifticon controller
  
    PUT
    /api/v1/gifticons
    기프티콘 수정

    POST
    /api/v1/gifticons
    기프티콘 저장

    DELETE
    /api/v1/gifticons
    기프티콘 삭제

    GET
    /api/v1/gifticons/{barcode_num}
    기프티콘 조회

    GET
    /api/v1/gifticons/{email}/{social}
    유저가 가지고 있는 기프티콘 조회

    GET
    /api/v1/gifticons/{email}/{social}/map
    지도에서 띄우는 기프티콘 리스트

    POST
    /api/v1/gifticons/brand
    기프티콘 브랜드별 정렬

    GET
    /api/v1/gifticons/brandsort/{email}/{social}
    브랜드 기프티콘 순으로 정렬

    GET
    /api/v1/gifticons/check
    기프티콘 유호기간 체크 후 상태 변경 / 서버용 API

    POST
    /api/v1/gifticons/history
    기프티콘 히스토리

- Location Controller


    POST
    /api/v1/local/search
    현위치 기반 기프티콘 사용가능 한 모든 매장

    POST
    /api/v1/local/search/byBrand
    현위치 기반 기프티콘 사용가능 한 지정 브랜드 매장

    POST
    /api/v1/local/shake
    흔들었을때 사용가능한 주변 매장 브랜드

- Present Controller
  
    POST
    /api/v1/presents/get_present
    기부 줍기

    POST
    /api/v1/presents/give_present
    기부 버리기

    POST
    /api/v1/presents/possible_list
    가까운 선물 리스트, 줍기가능한 선물 리스트

- User Controller

    GET
    /api/v1/user/getlevel
    사용자레벨

    POST
    /api/v1/user/login
    로그인

    GET
    /api/v1/user/refresh
    토크재발급

    POST
    /api/v1/user/update
    사용자정보 변경

    DELETE
    /api/v1/user/withdrawal
    탈퇴

- Brand controller
  
    Get
    /api/v1/brands/orderby_gifticon
    기프티콘 개수 많은 순서대로 정렬된 브랜드 리스트


- File controller
  
    POST
    /api/v1/files/add_origin
    기프티콘 등록위해 원본 이미지 리스트 업로드

    POST
    /api/v1/files/register_gifticon
    등록하기 버튼 누른 후 상품,바코드 이미지 저장 및 db 업데이트


---

# 👀 기능 엿보기 (GIF)

### 로그인

![%EC%86%8C%EC%85%9C_%EB%A1%9C%EA%B7%B8%EC%9D%B8](https://user-images.githubusercontent.com/72604908/228416969-3f0f1e25-e300-4f54-bce0-6434698dc150.gif)

### 조회, 수정, 삭제

![%EC%88%98%EC%A0%95_%EC%82%AD%EC%A0%9C](https://user-images.githubusercontent.com/72604908/228417149-b55bfc14-af31-4a1a-ba60-93f1d0372c16.gif)

### 사용, 히스토리

![use_history](https://user-images.githubusercontent.com/72604908/228417438-cb49370b-83ba-4736-af06-4e09bdd56dbd.gif)

### 기프티콘 등록

![add](https://user-images.githubusercontent.com/72604908/228417541-98230653-254d-4b2c-89c7-199729a4acf5.gif)

### 갤러리 저장으로 등록

![add_gallery](https://user-images.githubusercontent.com/72604908/228417703-43a9d3b9-66ab-4a0c-909f-9a26adc4e85a.gif)

### MMS 수신으로 등록

![add_mms](https://user-images.githubusercontent.com/72604908/228417813-c1a75399-5047-474e-adc4-7b28f271850f.gif)

### 위치기반 기프티콘 사용

![location_shake](https://user-images.githubusercontent.com/72604908/228417908-cdf86149-0dff-4527-957d-6def2700f914.gif)

### 매장 시연 영상

![ezgif com-resize (2)](https://user-images.githubusercontent.com/72604908/228422564-5e794af2-2c87-4b3d-a215-56b407650516.gif)

### 주변 매장 정보 검색

![ezgif com-resize (1)](https://user-images.githubusercontent.com/72604908/228422135-64cb9ee2-68bf-4f65-9d22-6d378531ec9f.gif)

### 선물 뿌리기

![map_donate](https://user-images.githubusercontent.com/72604908/228418710-ce7a8491-7545-4f24-9344-2896b92747c2.gif)

### 선물 줍기, 감사인사

![map_pick](https://user-images.githubusercontent.com/72604908/228418616-d4dbef6d-8a6a-4825-a648-6df0497a95c8.gif)

### 워치 연동 + 사용

![ezgif com-resize (1)](https://user-images.githubusercontent.com/72604908/228423049-acf78d33-af54-4e79-bc22-16152f94650d.gif)

### 워치 선물 뿌리기

![EC9B8CECB9982](https://user-images.githubusercontent.com/72604908/228420914-d520da93-2342-476f-8ed3-a6b7342d0bf4.gif)

### 설정화면

![settings](https://user-images.githubusercontent.com/72604908/228418842-573703c2-6ae9-4506-af00-c029a1073868.gif)

### 푸시 알림 : 레벨업

![levelup](https://user-images.githubusercontent.com/72604908/228419018-4d0cce49-e37a-4944-bff9-5d1654a196e3.gif)

### 레벨별 프로필 이미지

![%EB%A0%88%EB%B2%A8%EB%B3%84_%EC%9D%B4%EB%AF%B8%EC%A7%80](https://user-images.githubusercontent.com/72604908/228419066-bdda0af9-8390-4769-b1d4-8715f060e963.gif)

### 유효기간 만료전 푸시알림

### 감사메세지 알림

---

# 🎤소감 한마디

## 나연
- 스프링 부트로 백엔드를 구현한 것도, 웹이 아닌 앱을 구현한 것도 처음이어서 더 애착이 가고 열심히 참여했던 프로젝트였다. 밤새 만든 api가 모바일에서 동작하는 걸 확인할 때면 밤샘 피로가 녹아내리듯 뿌듯한 감정이 들었다. 개발 결과물과 사용자들의 피드백이 너무 좋았던 프로젝트였기 때문에 정책적으로 조금만 더 보완하여 꼭 앱스토에 출시하고 싶은 마음이다. 마지막으로 마음이 잘 맞는 팀원들을 만나 참 복받은 프로젝트 기간이었다고 생각한다.❤

## 동현
- 기프티콘 관리라는 재밌는 주제로 진행해서 좋았고, 처음해보는 모바일 프로젝트라 많이 배울 수 있었습니다!

## 유나
- 새로운 시도를 해볼 수 있었던 좋은 기회였고, 일상 생활에 불편함을 해결할 수 있는 서비스를 개발했다는 점에서 뿌듯했습니다.

## 보경
- 짧은 기간동안 다양한 기능들을 구현하면서 성장할 수 있었던 프로젝트였습니다!!

## 재완
- 서버를 구현하면서 만난 오류들을 수정해가는 경험을 통해 많은것을 배울 수있는 프로젝트였습니다 !!!!
