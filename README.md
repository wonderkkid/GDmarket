
# CirCuit Breaker

CirCuit Breaker Framework : Spring FeignClient + Hystrix 사용

Reservation -> Payment 와의 Req/Res 연결에서 요청이 과도한 경우 CirCuit Breaker 통한 격리

Hystrix 설정: 요청처리 쓰레드에서 처리시간이 600 밀리가 초과할 경우 CirCuit Breaker Closing 설정 


# application.yml

https://github.com/wonderkkid/GDmarket/blob/main/feign.png?raw=true


