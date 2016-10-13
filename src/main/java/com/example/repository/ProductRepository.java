package com.example.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

	public Product getProductByCacheKey(Long cacheKey);
	
	public Iterable<Product> findAll();
	
	public Product save(Product product);
	
	public void deleteByCacheKey(Long cacheKey);
}
