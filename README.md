
# CirCuit Breaker

CirCuit Breaker Framework : Spring FeignClient + Hystrix 사용

Reservation -> Payment 와의 Req/Res 연결에서 요청이 과도한 경우 CirCuit Breaker 통한 격리

Hystrix 설정: 요청처리 쓰레드에서 처리시간이 600 밀리가 초과할 경우 CirCuit Breaker Closing 설정 


# application.yml

![image](https://user-images.githubusercontent.com/5582138/106612834-e1159380-65ac-11eb-8e42-33852762480a.png)


요청처리 쓰레드에서 처리시간이 600 밀리가 초과할 경우 CirCuit Breaker Closing 설정 



      java
        if ("Paid".equals(paymentStatus) ) {
            System.out.println("=============결제 승인 처리중=============");
            PaymentApproved paymentCompleted = new PaymentApproved();

            paymentCompleted.setPaymentStatus("Paid");
            paymentCompleted.setReservationNo(reservationNo);
            paymentCompleted.setItemNo(itemNo);
            paymentCompleted.setItemPrice(itemPrice);

            BeanUtils.copyProperties(this, paymentCompleted);
            paymentCompleted.publishAfterCommit();

            try {
                Thread.currentThread().sleep((long) (400 + Math.random() * 220));
                System.out.println("=============결제 승인 완료=============");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
'''

![image](https://user-images.githubusercontent.com/5582138/106613261-57b29100-65ad-11eb-8120-3f2a877a187e.png)

![KakaoTalk_20210203_130452776](https://user-images.githubusercontent.com/5582138/106697123-810d0480-6621-11eb-9792-e0eb79b1182c.png)

피호출되는 Payment 의 부하 처리 - 400 밀리에서 + 220 밀리 조정

![KakaoTalk_20210203_130503647](https://user-images.githubusercontent.com/5582138/106697125-8407f500-6621-11eb-86fd-d80d56910bd1.png)

부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
동시사용자 100명
60초 동안 실시




시스템은 동작 중이며, CirCuit Breaker 에 의해 부하에 따른 open / close가 반복됨.
