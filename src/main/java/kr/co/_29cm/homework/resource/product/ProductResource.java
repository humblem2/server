package kr.co._29cm.homework.resource.product;

import kr.co._29cm.homework.annotation.LogExecutionTime;
import kr.co._29cm.homework.common.Constant;
import kr.co._29cm.homework.common.Utility;
import kr.co._29cm.homework.domain.Product;
import kr.co._29cm.homework.domain.vm.OrderVM;
import kr.co._29cm.homework.domain.vm.ProductVM;
import kr.co._29cm.homework.exception.SoldOutException;
import kr.co._29cm.homework.facade.RedissonLockProductFacade;
import kr.co._29cm.homework.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1")
public class ProductResource {
    private final ProductRepository productRepository;

    private RedissonLockProductFacade productService; // 인터페이스 기반 코딩

    public ProductResource(ProductRepository productRepository, RedissonLockProductFacade productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    /**
     * 상품 목록 조회
     * @return List<Product> 상품 목록
     */
    @GetMapping("/products")
    @LogExecutionTime
    public List<Product> products() {
        List<Product> products = productRepository.findAll();
        System.out.println("\n[Product] \n" + products + "\n");
        return products;
    }

    /**
     * 상품 주문
     * 클라이언트로부터 JSON 배열로 이루어진 상품 목록을 받아서 재고수량 체크하여 재고감소 후 결제를 가정하고 주문
     * @return OrderVM 주문내역, 주문금액, 결제금액(배송비 포함 될 수 있음)
     */
    @PostMapping("/orders")
    @LogExecutionTime
    public OrderVM orders(@RequestBody List<ProductVM> userRequestOrders) {
        /* 재고를 체크하여 뷰단으로 응답 할 주문 요청 내역 */
        List<ProductVM> orders = new ArrayList<>();

        /* 재고가 있어서 주문이 가능한 상품들의 주문금액 합산 */
        Long purchaseAllowedOrderPriceSum = 0L;

        /* 재고 체크 */
        for (ProductVM userRequestOrder : userRequestOrders) {
            Product product = productRepository
                                .findByProductId(userRequestOrder.getProductId())
                                .orElseThrow(() -> {
                                    String errorMessage = "No product found with productId: " + userRequestOrder.getProductId();
                                    return new NoSuchElementException(errorMessage);
                                });
            ModelMapper modelMapper = new ModelMapper();
            ProductVM productVM = modelMapper.map(product, ProductVM.class); // 바인딩
            productVM.setOrderRequestQuantity(userRequestOrder.getOrderRequestQuantity());

            System.out.println("\n[ProductVM] \n" + productVM + "\n");

            try {
                /* 재고 체크 시도 */
                productService.decrease(productVM.getId(), productVM.getOrderRequestQuantity());

                /* 재고가 있어서 주문&결제 가능한 경우 */
                productVM.setOrderStatus(Constant.ORDER_SUCCESS_CODE); // 주문상태코드
                productVM.setOrderMessage(Constant.ORDER_SUCCESS_MESSAGE); // 주문상태메세지
                orders.add(productVM);

                /* 상품들의 주문 금액 합산 */
                Long _price = Utility.convertPriceToNumber(productVM.getProductPrice()); // 단가
                Long _quantity = productVM.getOrderRequestQuantity(); // 구매수량
                purchaseAllowedOrderPriceSum += (_price * _quantity); // 단가 * 구매수량 = "20,000원" -> 20000L
            } catch (SoldOutException e) {
                /* SoldOutException 발생 */
                System.out.println(e.getMessage());

                /* 재고가 부족해 주문&결제 불가한 경우 */
                productVM.setOrderStatus(Constant.ORDER_EXCEPTION_CODE);
                productVM.setOrderMessage(Constant.ORDER_EXCEPTION_MESSAGE);
                orders.add(productVM);
            }
        }

        /* 최종 응답 */
        OrderVM orderVM = new OrderVM();
        orderVM.setOrderList(orders); // [주문상품과 주문결과]의 목록

        /* 주문 금액이 5만원 미만인 경우 배송료 2,500원이 추가 */
        boolean isFreeDeliveryPrice = false;
        Long deliveryPrice = 0L;
        if (purchaseAllowedOrderPriceSum < 50000L) {
            deliveryPrice = 2500L; // 주문 금액이 5만원 미만인 경우 배송료 2,500원이 추가
        } else {
            deliveryPrice = 0L;
            isFreeDeliveryPrice = true;
        }
        
        /* 주문 금액, 결제 금액 (배송비 포함)의 원화, 컴마 및 문자열 처리 */
        orderVM.setDeliveryPrice(Utility.convertNumberToPrice(deliveryPrice));
        orderVM.setOrderPrice(Utility.convertNumberToPrice(purchaseAllowedOrderPriceSum));
        orderVM.setPaymentPrice(Utility.convertNumberToPrice(purchaseAllowedOrderPriceSum + deliveryPrice));
        orderVM.setFreeDeliveryPrice(isFreeDeliveryPrice);

        System.out.println("\n[OrderVM] \n" + orderVM + "\n");

        return orderVM;
    }
}
