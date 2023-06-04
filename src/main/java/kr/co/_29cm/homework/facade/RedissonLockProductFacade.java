package kr.co._29cm.homework.facade;

import kr.co._29cm.homework.service.ProductService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * ProductService.java의 [락획득]과 [재고감소] 책임 분리 위한 Facade Class - Single Responsibility Principle (SRP)
 */
@Component
public class RedissonLockProductFacade {
    private RedissonClient redissonClient;

    private ProductService stockService;

    public RedissonLockProductFacade(RedissonClient redissonClient, ProductService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    /**
     *  Redis를 이용한 분산락 기반 재고 감소
     *  목적: 단순 재고 감소시 발생하는 동시성 이슈 해결
     * @param key 상품시퀀스번호
     * @param quantity 주문요청수량
     */
    public void decrease(Long key, Long quantity) {
        /* 락 객체 생성 */
        RLock lock = redissonClient.getLock(key.toString());

        try{
            /* 락 획득 시도 - (최대 10초 동안, 락 획득 시도 1번) */
            boolean available = lock.tryLock(10,1, TimeUnit.SECONDS); // FIXME: Config 모듈화 주입

            /* 락 획득 실패 */
            if (!available) {
                System.out.println(key + "번의 상품 락 획득 실패");
                return;
            }

            /* 락 획득 성공 -> 재고 감소 */
            stockService.decrease(key, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            /* 락 해제 */
            lock.unlock();
        }
    }
}
