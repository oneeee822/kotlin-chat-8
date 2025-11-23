# kotlin-chat-8 👾 픽셀 익명톡 (Pixel Chat)
> **우아한테크코스 웹 백엔드 8기 프리코스 4주차 - 낯선 도구 해커톤**
> Kotlin & Ktor를 활용한 실시간 레트로 익명 채팅 서비스

<br>

### 자료 조사
**Kotlin과 Ktor선택 이유**
* **낯선 도구 도전:** 평소 익숙한 Java/Spring이 아닌, 완전히 새로운 언어인 Kotlin과 Ktor를 학습 목표로 선정
* **기술적 적합성:** 채팅 서비스의 핵심인 실시간성을 구현하기에 Ktor의 구조와 코루틴의 비동기 처리가 적합하다고 판단
* **HTML DSL:** 별도의 프론트엔드 프레임워크 없이 Kotlin 코드만으로 HTML을 렌더링할 수 있다는 점에 주목

### 기획
**단순한 채팅을 넘어선 게임 같은 경험**
* **컨셉:** 90년대 레트로 게임기 감성의 픽셀 아트UI 디자인
* **핵심 기능:**
    * **익명성:** 별도 회원가입 없이 '픽셀용사', '레트로마법사' 등 랜덤 닉네임 부여
    * **시한부 채팅:** 5분의 제한 시간을 두어 대화의 긴장감 부여
    * **협동 요소:** 대화를 더 하고 싶으면 `[ LEVEL UP ]` 버튼을 눌러 시간을 연장하는 요소 도입
* **구조 설계:**
    * MVC 패턴 적용 (Service 계층 분리)
    * WebSocket을 이용한 양방향 통신


### 실행 및 구현
* **Tech Stack:** Kotlin, Ktor(Netty), WebSocket, HTML/CSS
* **주요 구현 내용:**
    * Application.kt 진입점과 ChatRoutes, ChatService로 역할 분리 
    * AtomicInteger, AtomicLong을 활용한 Thread-Safe한 상태 관리
    * CSS3와 Google Fonts를 활용한 레트로 CRT 모니터 효과 및 픽셀 디자인 구현
    * WebSocket broadcast 로직을 통해 접속자 간 실시간 메시지 동기화
 * **배포 및 테스트:**
    * 개발 단계에서 ngrok 터널링을 활용하여 외부 접속 환경을 구축
    * 팀원 및 지인들과 실시간으로 링크를 공유하여 다중 접속 환경에서의 채팅 동기화 및 타이머 로직을 검증함

<br>

## 실행 화면
| 채팅 진행 화면 |
|:---:|
| <img width="690" height="796" alt="스크린샷 2025-11-24 오전 1 49 02" src="https://github.com/user-attachments/assets/95abed8d-3792-482e-a6aa-8e981cd5914b" />|
