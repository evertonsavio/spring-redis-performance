package dev.evertonsavio.redisperformance.service;

import dev.evertonsavio.redisperformance.entity.Product;
import dev.evertonsavio.redisperformance.service.util.CacheTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV2 {

    @Autowired
    private CacheTemplate<Integer, Product> cacheTemplate;

    public Mono<Product> getProduct(int id){
        return this.cacheTemplate.get(id);
    }

    public Mono<Product> updateProduct(int id, Mono<Product> productMono){
        return productMono.flatMap(product -> this.cacheTemplate.update(id, product));
    }

    public Mono<Void> deleteProduct(int id){
        return this.cacheTemplate.delete(id);
    }
}
