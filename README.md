# server
상품주문프로그램(client+server+ddl)의 server

### Notion URL
* Link

***

### ⚡`TODO`: 개선
> springboot, jpa 공부하기 위함

* 기능
  * 쇼핑몰 기능 구현 측면에서 상품주문`API`(server) 모델링 개선 
    * product, order(상태, 결제타입, PG결제응답결과), orderitem, user 
  * 기능: 로그인(JWT), 상품전시, 결제타입선택, 결제(재고감소처리, 동시성이슈발생부분)
    * 변경된 모델링 기반으로
    * API
    * 뷰단은 최소화
* 기술
  * 동시성 이슈 해결 솔루션 (분산락,..) 
    * 이론, 용어
    * 핵심 코드 정리
    * 테스트코드 정리
    * Solid원칙 및 대응 코드 정리
    * ERD, 클래스다이어그램 (프로젝트 구조 파악)
* 배포
  * github repo
  * AWS
  * 도커로 application 배포
    * > [앞으로 개발 할때]
      > - application(ex: springboot, django) 도커 빌드/클라우드배포/클라우드mysql&redis 연동
      > - DB(mysql, redis) 는 직접 설치해서 쓰고 APP만
