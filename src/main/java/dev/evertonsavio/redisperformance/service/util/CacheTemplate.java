package dev.evertonsavio.redisperformance.service.util;

import reactor.core.publisher.Mono;

public abstract class CacheTemplate<K, T> {

    public Mono<T> get(K k){
        return getFromCache(k)
                .switchIfEmpty(getFromSource(k)
                        .flatMap(e -> updateCache(k, e))
                );
    }

    public Mono<T> update(K k, T t){
        return updateSource(k, t)
                .flatMap(e -> deleteFromCache(k).thenReturn(e));
    }

    public Mono<Void> delete(K k){
        return deleteFromSource(k)
                .then(deleteFromCache(k));
    }

    abstract protected Mono<T> getFromSource(K k);
    abstract protected Mono<T> getFromCache(K k);
    abstract protected Mono<T> updateSource(K k, T t);
    abstract protected Mono<T> updateCache(K k, T t);
    abstract protected Mono<Void> deleteFromSource(K k);
    abstract protected Mono<Void> deleteFromCache(K k);

}
