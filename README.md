
# CirCuit Breaker

CirCuit Breaker Framework : Spring FeignClient + Hystrix 사용

Reservation -> Payment 와의 Req/Res 연결에서 요청이 과도한 경우 CirCuit Breaker 통한 격리

Hystrix 설정: 요청처리 쓰레드에서 처리시간이 600 밀리가 초과할 경우 CirCuit Breaker Closing 설정 


# application.yml

![image](https://user-images.githubusercontent.com/5582138/106612834-e1159380-65ac-11eb-8e42-33852762480a.png)


요청처리 쓰레드에서 처리시간이 600 밀리가 초과할 경우 CirCuit Breaker Closing 설정 


![image](https://user-images.githubusercontent.com/5582138/106613261-57b29100-65ad-11eb-8120-3f2a877a187e.png)


피호출되는 Payment 의 부하 처리 - 400 밀리에서 + 220 밀리 조정


부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
동시사용자 100명
60초 동안 실시




시스템은 동작 중이며, CirCuit Breaker 에 의해 부하에 따른 open / close가 반복됨.
