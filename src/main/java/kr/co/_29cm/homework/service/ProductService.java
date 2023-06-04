package kr.co._29cm.homework.service;

import kr.co._29cm.homework.domain.Product;
import kr.co._29cm.homework.exception.SoldOutException;
import kr.co._29cm.homework.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 재고 감소 (책임)
     * @param id
     * @param quantity
     * @throws SoldOutException
     */
    public void decrease(Long id, Long quantity) throws SoldOutException {
        Product product = productRepository.findById(id).orElseThrow();
        product.decrease(quantity);
        Product updatedProduct = productRepository.saveAndFlush(product);
    }
}
