package kr.co._29cm.homework.domain.vm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Presentation Layer 용 VO
 * '뷰단으로의 응답'을 추상화/캡슐화 한 도메인 객체(VM: Value Management)
 * OrderVM ───────association──────> ProductVM ───────generalization/specialization──────▷ Product
 */
public class OrderVM {
    private List<ProductVM> orderList;                  // 주문 요청 내역

    private String orderPrice;                          // 주문 금액

    private String deliveryPrice;                       // 배송비

    private String paymentPrice;                        // 결제 금액 = 주문 금액 + [배송비]

    private boolean isFreeDeliveryPrice;                // 배송비 무료 여부

    public OrderVM() {
    }

    public OrderVM(List<ProductVM> orderList, String orderPrice, String deliveryPrice, String paymentPrice, boolean isFreeDeliveryPrice) {
        this.orderList = orderList;
        this.orderPrice = orderPrice;
        this.deliveryPrice = deliveryPrice;
        this.paymentPrice = paymentPrice;
        this.isFreeDeliveryPrice = isFreeDeliveryPrice;
    }

    public List<ProductVM> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<ProductVM> orderList) {
        this.orderList = orderList;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getPaymentPrice() {
        return paymentPrice;
    }

    public void setPaymentPrice(String paymentPrice) {
        this.paymentPrice = paymentPrice;
    }

    public boolean isFreeDeliveryPrice() {
        return isFreeDeliveryPrice;
    }

    public void setFreeDeliveryPrice(boolean freeDeliveryPrice) {
        isFreeDeliveryPrice = freeDeliveryPrice;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return super.toString();
    }
}
