package com.example.apiprodutosmaven.controllers;

import com.example.apiprodutosmaven.dtos.ProductRecordDto;
import com.example.apiprodutosmaven.models.ProductModel;
import com.example.apiprodutosmaven.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<ProductModel> save(@RequestBody @Valid ProductRecordDto productRecordDto) {

        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel); /*Faz a conversao de DTO para Model*/

        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping
    public ResponseEntity<List<ProductModel>> getAll() {

        List<ProductModel> productsList = productRepository.findAll();

        /*HATEOAS para adicionar link do produto*/
        if (!productsList.isEmpty()) {
            for (ProductModel product : productsList) {
                UUID id = product.getId();
                product.add(linkTo(methodOn(ProductController.class).getOne(id)).withSelfRel());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOne(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> obj = productRepository.findById(id);
        obj.get().add(linkTo(methodOn(ProductController.class).getAll()).withSelfRel());
        return !obj.isEmpty() ? ResponseEntity.status(HttpStatus.OK).body(obj.get()) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductRecordDto productRecordDto) {

        Optional<ProductModel> obj = productRepository.findById(id);

        if (obj.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");

        var productModel = obj.get();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") UUID id) {

        Optional<ProductModel> obj = productRepository.findById(id);

        if (obj.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");

        productRepository.delete(obj.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
    }
}
