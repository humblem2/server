package kr.co._29cm.homework.domain;

import kr.co._29cm.homework.exception.SoldOutException;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 재고수량 속성을 갖고있는 '상품'을 추상화/캡슐화 한 Entity 객체
 * 재고수량 속성을 감소시키는 행위 구현
 */
@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                    // 시퀀스번호

    private Long productId;                             // 상품번호

    private String productName;                         // 상품명

    private String productPrice;                        // 판매가격

    private Long productQuantity;                       // 재고수량

    public Product() {
    }

    public Product(Long productId, Long productQuantity) {
        this.productId = productId;
        this.productQuantity = productQuantity;
    }

    /**
     * 단위테스트에서 사용하는 재고수량 필드 getter
     */
    public Long getProductQuantity() {
        return productQuantity;
    }

    /**
     * 재고수량 감소
     *
     * @param productQuantity 결제 요청 시도한 수량
     */
    public void decrease(Long productQuantity) {
        boolean outOfStock = this.productQuantity - productQuantity < 0;
        if(outOfStock){
            System.out.println("잔여수량: " + this.productQuantity + "개\n" + "결제시도: " + productQuantity + "개");
            throw new SoldOutException("상품번호 [" + this.productId + "] 의 재고가 부족합니다");
        }

        this.productQuantity = this.productQuantity - productQuantity;
    }
}
