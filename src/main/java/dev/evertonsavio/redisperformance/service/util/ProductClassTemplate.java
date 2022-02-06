package dev.evertonsavio.redisperformance.service.util;

import dev.evertonsavio.redisperformance.entity.Product;
import dev.evertonsavio.redisperformance.entity.ProductRepository;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public class ProductClassTemplate extends CacheTemplate<Integer, Product>{

    @Autowired
    private ProductRepository productRepository;
    private final RMapReactive<Integer, Product> mapReactive;

    public ProductClassTemplate(RedissonReactiveClient client) {
        this.mapReactive = client.getMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class));
    }

    @Override
    protected Mono<Product> getFromSource(Integer integer) {
        return this.productRepository.findById(integer);
    }

    @Override
    protected Mono<Product> getFromCache(Integer integer) {
        return this.mapReactive.get(integer);
    }

    @Override
    protected Mono<Product> updateSource(Integer integer, Product product) {
        return this.productRepository.findById(integer)
                .doOnNext(p -> product.setId(integer))
                .flatMap(p -> this.productRepository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Integer integer, Product product) {
        return this.mapReactive.fastPut(integer, product).thenReturn(product);
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer integer) {
        return this.productRepository.deleteById(integer);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer integer) {
        return this.mapReactive.fastRemove(integer).then();
    }
}
