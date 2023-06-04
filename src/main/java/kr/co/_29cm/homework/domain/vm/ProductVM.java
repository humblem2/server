package kr.co._29cm.homework.domain.vm;

import kr.co._29cm.homework.domain.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Presentation Layer 용 VO
 * '뷰단으로부터의 요청'과 '뷰단으로의 응답'을 추상화/캡슐화 한 도메인 객체(VM: Value Management)
 * OrderVM ───────association──────> ProductVM ───────generalization/specialization──────▷ Product
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class ProductVM extends Product {
    private Long orderRequestQuantity;                  // 주문 요청 수량

    private String orderStatus;                         // 주문 요청 상태

    private String orderMessage;                        // 주문 요청 결과 메세지
}
