package gdmarket;

import javax.persistence.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gdmarket.config.kafka.KafkaProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer reservationNo;
    private String customerName;
    private Integer customerId;
    private Integer itemNo;
    private String itemName;
    private Integer itemPrice;
//    private String rentalStatus;
    private String paymentStatus;

    @PostPersist
    public void onPostPersist(){
        Reserved reserved = new Reserved();
        reserved.setReservationNo(this.getReservationNo());
        reserved.setCustomerName(this.getCustomerName());
        reserved.setCustomerId(this.getCustomerId());
        reserved.setItemStatus("NotRentable");
        reserved.setItemNo(this.getItemNo());
        reserved.setItemName(this.getItemName());
        reserved.setItemPrice(this.getItemPrice());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(reserved);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }
        KafkaProcessor processor = ReservationApplication.applicationContext.getBean(KafkaProcessor.class);
        MessageChannel outputChannel = processor.outboundTopic();
        outputChannel.send(MessageBuilder
                .withPayload(json)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
        System.out.println("@@@@@@@ reserved to Json @@@@@@@");
        System.out.println(reserved.toJson());
    }


    @PreUpdate
    public void onPreUpdate(){

        if("NotPaid".equals(this.getPaymentStatus())) {
            PaymentRequested paymentRequested = new PaymentRequested();
            BeanUtils.copyProperties(this, paymentRequested);
            paymentRequested.publishAfterCommit();

            //Following code causes dependency to external APIs
            // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

            gdmarket.external.Payment payment = new gdmarket.external.Payment();
            // mappings goes here
            ReservationApplication.applicationContext.getBean(gdmarket.external.PaymentService.class)
                    .approvePayment(payment);

        }
        if("Paid".equals(this.getPaymentStatus())) {
            PaymentCancellationRequested paymentCancellationRequested = new PaymentCancellationRequested();
            BeanUtils.copyProperties(this, paymentCancellationRequested);
            paymentCancellationRequested.publishAfterCommit();

            //Following code causes dependency to external APIs
            // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

            gdmarket.external.Payment payment = new gdmarket.external.Payment();
            // mappings goes here
            ReservationApplication.applicationContext.getBean(gdmarket.external.PaymentService.class)
                    .cancelPayment(payment);
        }
    }


    @PreRemove
    public void onPreRemove(){
        ReservationCancelled reservationCancelled = new ReservationCancelled();
        reservationCancelled.setReservationNo(this.getReservationNo());
        reservationCancelled.setCustomerName(this.getCustomerName());
        reservationCancelled.setCustomerId(this.getCustomerId());
        reservationCancelled.setItemStatus("Rentable");
        reservationCancelled.setItemNo(this.getItemNo());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(reservationCancelled);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }
        KafkaProcessor processor = ReservationApplication.applicationContext.getBean(KafkaProcessor.class);
        MessageChannel outputChannel = processor.outboundTopic();
        outputChannel.send(MessageBuilder
                .withPayload(json)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
        System.out.println("@@@@@@@ reservationCancelled to Json @@@@@@@");
        System.out.println(reservationCancelled.toJson());


    }

    public Integer getReservationNo() {
        return reservationNo;
    }
    public void setReservationNo(Integer reservationNo) {
        this.reservationNo = reservationNo;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getItemNo() {
        return itemNo;
    }
    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

//    public String getRentalStatus() {
//        return rentalStatus;
//    }
//    public void setRentalStatus(String rentalStatus) {
//        this.rentalStatus = rentalStatus;
//    }

    public Integer getItemPrice() {
        return itemPrice;
    }
    public void setItemPrice(Integer itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

}
