package kr.co._29cm.homework.facade;

import kr.co._29cm.homework.domain.Product;
import kr.co._29cm.homework.exception.SoldOutException;
import kr.co._29cm.homework.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RedissonLockProductFacadeTest {
    private static final Logger LOGGER = Logger.getLogger(RedissonLockProductFacadeTest.class.getName());

    @Autowired
    private RedissonLockProductFacade productService;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Multi-thread 요청으로 동시에 여러개 요청하여 동시성 이슈 해결된 환경에서 [SoldOutException]이 정상 동작하는지 검증
     * @throws InterruptedException
     */
    @Test
    public void testSoldOutExceptionInConcurrencyFixedEnvironment() throws InterruptedException {
        long startTime = System.nanoTime();

        /* 테스트 상품 시퀀스번호 */
        Long testId = 2L; // 예시) 2번 상품(재고수량: 89개)의 시퀀스 번호

        /* 테스트 상품 조회 */
        Product testProduct = productRepository.findById(testId).orElseThrow();
        long longPrimitive = testProduct.getProductQuantity();
        int testProductQuantity = (int) longPrimitive; // 테스트상품의 재고수량

        /* 엔드유저 수 지정 - 최소한 테스트상품의 재고수량 보다 크게 지정해야 함 */
        int threadCount = testProductQuantity + 100;

        /* 각 엔드유저는 1개씩 주문 */
        Long quantityCountByUser = 1L;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger exceptionCounter = new AtomicInteger(0); // [SoldOutException] 예외 발생 횟수를 저장

        /* 비동기로 실행할 작업 */
        for (int i = 0; i< threadCount; i++) {
            executorService.submit(()->{
                try {
                    /* 재고 감소 (작업) */
                    productService.decrease(testId,quantityCountByUser);
                }catch (SoldOutException e) {
                    exceptionCounter.incrementAndGet();
                    LOGGER.severe("SoldOutException occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        /* 모든 thread 작업이 완료될 때 까지 대기 */
        latch.await();

        /* 테스트상품 결과 조회 */
        Product product = productRepository.findById(testId).orElseThrow();

        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;  // 단위: 초

        /* 결과 리포트 */
        LOGGER.severe("예외발생: " + exceptionCounter.get() + " 번");
        LOGGER.severe("접속유저: " + threadCount + " 명");
        LOGGER.severe("최초재고: " + testProductQuantity + " 개");
        LOGGER.severe("최종재고: " + product.getProductQuantity() + " 개");
        LOGGER.severe("수행시간: " + durationInSeconds + " 초");

        /* 시나리오1: 테스트상품 재고수량 0개가 되야 성공 */
        assertEquals(0L, product.getProductQuantity());

        /* 시나리오2: [SoldOutException] 예외가 최소 1번 이상 발생해야 성공 */
        assertEquals(true,exceptionCounter.get() > 0);
    }
}