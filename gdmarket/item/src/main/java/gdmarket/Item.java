package gdmarket;

import javax.persistence.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gdmarket.config.kafka.KafkaProcessor;
import org.springframework.beans.BeanUtils;
import java.util.List;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

@Entity
@Table(name="Item_table")
public class Item {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer itemNo;
    private String itemName;
    private String itemStatus;
    private String itemPrice;

    @PostPersist
    public void onPostPersist(){
        ItemRegistered itemRegistered = new ItemRegistered();
        BeanUtils.copyProperties(this, itemRegistered);
        itemRegistered.publishAfterCommit();

        // kafka 메시지 설정
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(itemRegistered);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }

        KafkaProcessor processor = ItemApplication.applicationContext.getBean(KafkaProcessor.class);
        MessageChannel outputChannel = processor.outboundTopic();

        outputChannel.send(MessageBuilder
                .withPayload(json)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }

    @PostUpdate
    public void onPostUpdate(){
        RentedItem rentedItem = new RentedItem();
        BeanUtils.copyProperties(this, rentedItem);
        rentedItem.publishAfterCommit();


        ReturnedItem returnedItem = new ReturnedItem();
        BeanUtils.copyProperties(this, returnedItem);
        returnedItem.publishAfterCommit();
    }

    @PreRemove
    public void onPreRemove(){
        ItemDeleted itemDeleted = new ItemDeleted();
        BeanUtils.copyProperties(this, itemDeleted);
        itemDeleted.publishAfterCommit();


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

    public String getItemStatus() {
        return itemStatus;
    }
    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemPrice() {
        return itemPrice;
    }
    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

}
