package dev.evertonsavio.redisperformance.service.util;

import dev.evertonsavio.redisperformance.entity.Product;
import dev.evertonsavio.redisperformance.entity.ProductRepository;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductLocalCacheTemplate extends CacheTemplate<Integer, Product>{

    @Autowired
    private ProductRepository productRepository;
    private final RLocalCachedMap<Integer, Product> redisMap;

    public ProductLocalCacheTemplate(RedissonClient client) {
        LocalCachedMapOptions<Integer, Product> mapOptions = LocalCachedMapOptions.<Integer, Product>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);

        this.redisMap = client.getLocalCachedMap("product", mapOptions);
    }

    @Override
    protected Mono<Product> getFromSource(Integer integer) {
        return this.productRepository.findById(integer);
    }

    @Override
    protected Mono<Product> getFromCache(Integer integer) {
        return Mono.justOrEmpty(this.redisMap.get(integer));
    }

    @Override
    protected Mono<Product> updateSource(Integer integer, Product product) {
        return this.productRepository.findById(integer)
                .doOnNext(p -> product.setId(integer))
                .flatMap(p -> this.productRepository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Integer integer, Product product) {
        return Mono.create(sink ->
                this.redisMap.fastPutAsync(integer, product) //completable future
                .thenAccept(b -> sink.success(product))
                .exceptionally(ex -> {
                    sink.error(ex);
                    return null;
                }));
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer integer) {
        return this.productRepository.deleteById(integer);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer integer) {
        return Mono.create(sink ->
            this.redisMap.fastRemoveAsync(integer)
                    .thenAccept(b -> sink.success())
                    .exceptionally(ex -> {
                        sink.error(ex);
                        return null;
                    }));
    }
}
