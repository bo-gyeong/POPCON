# README.md

![Untitled](/uploads/fe806a353506265ac7ecd3fffdc0cd0d/Untitled.png)

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

![Untitled_1](/uploads/31a7bef1fae000b0ee56d76cc87bf9d1/Untitled_1.png)

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

![Untitled_2](/uploads/2cdf776cdbc3e8a593f84b83166c9b89/Untitled_2.png)

---

# 🔑 ERD

![Untitled_3](/uploads/e58669389a57b9fd7d7cfd6d489df7b2/Untitled_3.png)

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

POST
/api/v1/files/register_gifticon
등록하기 버튼 누른 후 상품,바코드 이미지 저장 및 db 업데이트

Gifticon controller
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

Location Controller

- 

POST
/api/v1/local/search
현위치 기반 기프티콘 사용가능 한 모든 매장

POST
/api/v1/local/search/byBrand
현위치 기반 기프티콘 사용가능 한 지정 브랜드 매장

POST
/api/v1/local/shake
흔들었을때 사용가능한 주변 매장 브랜드

Present Controller
POST
/api/v1/presents/get_present
기부 줍기

POST
/api/v1/presents/give_present
기부 버리기

POST
/api/v1/presents/possible_list
가까운 선물 리스트, 줍기가능한 선물 리스트

User Controller

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

Brand controller
Get
/api/v1/brands/orderby_gifticon
기프티콘 개수 많은 순서대로 정렬된 브랜드 리스트

### 

File controller
POST
/api/v1/files/add_origin
기프티콘 등록위해 원본 이미지 리스트 업로드

POST
/api/v1/files/register_gifticon
등록하기 버튼 누른 후 상품,바코드 이미지 저장 및 db 업데이트

Gifticon controller
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

Location Controller

POST
/api/v1/local/search
현위치 기반 기프티콘 사용가능 한 모든 매장

POST
/api/v1/local/search/byBrand
현위치 기반 기프티콘 사용가능 한 지정 브랜드 매장

POST
/api/v1/local/shake
흔들었을때 사용가능한 주변 매장 브랜드

Present Controller
POST
/api/v1/presents/get_present
기부 줍기

POST
/api/v1/presents/give_present
기부 버리기

POST
/api/v1/presents/possible_list
가까운 선물 리스트, 줍기가능한 선물 리스트

User Controller

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

---

# 👀 기능 엿보기 (GIF)

### 로그인

![_EC_86_8C_EC_85_9C__EB_A1_9C_EA_B7_B8_EC_9D_B8](/uploads/bcb22336f7e3dc386c066120a2a59319/_EC_86_8C_EC_85_9C__EB_A1_9C_EA_B7_B8_EC_9D_B8.gif)

### 조회, 수정, 삭제

![_EC_88_98_EC_A0_95__EC_82_AD_EC_A0_9C](/uploads/5af68f5b6fa82732233094b73a72702b/_EC_88_98_EC_A0_95__EC_82_AD_EC_A0_9C.gif)

### 사용, 히스토리

![_EC_82_AC_EC_9A_A9__ED_9E_88_EC_8A_A4_ED_86_A0_EB_A6_AC](/uploads/09645e74f86de77fb7ed8cea6d11c2db/_EC_82_AC_EC_9A_A9__ED_9E_88_EC_8A_A4_ED_86_A0_EB_A6_AC.gif)

### 기프티콘 등록

![_EB_93_B1_EB_A1_9D](/uploads/5b0aefd7f719b5f2ffa111385beea2d6/_EB_93_B1_EB_A1_9D.gif)

### 갤러리 저장으로 등록

![_EA_B0_A4_EB_9F_AC_EB_A6_AC_EC_A0_80_EC_9E_A5](/uploads/78d57fa02707cb0b193f53eb593e8741/_EA_B0_A4_EB_9F_AC_EB_A6_AC_EC_A0_80_EC_9E_A5.gif)

### MMS 수신으로 등록

![MMS_EC_A0_80_EC_9E_A5](/uploads/389196e5897ae7a2c4c2a7d2a0a4a0d5/MMS_EC_A0_80_EC_9E_A5.gif)

### 위치기반 기프티콘 사용

![_EC_89_90_EC_9D_B4_ED_81_AC](/uploads/4d25e1c695e29413e90059048242bcc8/_EC_89_90_EC_9D_B4_ED_81_AC.gif)

### 매장 시연 영상

![_EB_A7_A4_EC_9E_A5__EC_8B_9C_EC_97_B0](/uploads/87c1324574a59d94af4ea35d3e271c2b/_EB_A7_A4_EC_9E_A5__EC_8B_9C_EC_97_B0.gif)

### 주변 매장 정보 검색

![_EB_A7_A4_EC_9E_A5_EA_B2_80_EC_83_89](/uploads/e7e6a3009e9870e09fc78a60483e6ee7/_EB_A7_A4_EC_9E_A5_EA_B2_80_EC_83_89.gif)

### 선물 뿌리기

![_EC_84_A0_EB_AC_BC](/uploads/03608386987978bdb0e9bcf72c0e8fc7/_EC_84_A0_EB_AC_BC.gif)

### 선물 줍기, 감사인사

![_EC_A4_8D_EA_B8_B0](/uploads/84343cdb686813fbc995d48b99a98c11/_EC_A4_8D_EA_B8_B0.gif)

### 워치 연동 + 사용

![_EC_9B_8C_EC_B9_98](/uploads/e1522cf85241073fa5c37ac0cd57fb7f/_EC_9B_8C_EC_B9_98.gif)

### 워치 선물 뿌리기

![_EC_9B_8C_EC_B9_982](/uploads/449af4015091057c75c02da7e7177542/_EC_9B_8C_EC_B9_982.gif)

### 설정화면

![_EC_95_8C_EB_A6_BC__EC_84_A4_EC_A0_95](/uploads/e5930f4c952b3d2afb8a2739850ad761/_EC_95_8C_EB_A6_BC__EC_84_A4_EC_A0_95.gif)

### 푸시 알림 : 레벨업

![_EB_A0_88_EB_B2_A8_EC_97_85](/uploads/4997911c2d53c09be05b384737f82b8e/_EB_A0_88_EB_B2_A8_EC_97_85.gif)

### 레벨별 프로필 이미지

![_EB_A0_88_EB_B2_A8_EB_B3_84__EC_9D_B4_EB_AF_B8_EC_A7_80](/uploads/2dc689fe3ec21d3fd968fd581c3be397/_EB_A0_88_EB_B2_A8_EB_B3_84__EC_9D_B4_EB_AF_B8_EC_A7_80.gif)

유효기간 만료전 푸시알림

감사메세지 알림

---