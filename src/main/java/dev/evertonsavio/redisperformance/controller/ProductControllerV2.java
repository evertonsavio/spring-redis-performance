package dev.evertonsavio.redisperformance.controller;

import dev.evertonsavio.redisperformance.entity.Product;
import dev.evertonsavio.redisperformance.service.ProductServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("product/v2")
public class ProductControllerV2 {

    @Autowired
    private ProductServiceV2 productServiceV2;

    @GetMapping("{id}")
    public Mono<Product> getProdut(@PathVariable int id){
        return this.productServiceV2.getProduct(id);
    }

    @PutMapping("{id}")
    public Mono<Product> updateProduct(@PathVariable int id, @RequestBody Mono<Product> productMono){
        return this.productServiceV2.updateProduct(id, productMono);
    }
    @DeleteMapping("{id}")
    public Mono<Void> deleteProduct(@PathVariable int id){
        return this.productServiceV2.deleteProduct(id);
    }

}
