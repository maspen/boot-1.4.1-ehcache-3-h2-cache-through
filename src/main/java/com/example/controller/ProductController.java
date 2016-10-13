package com.example.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.assist.CustomKeyGenerator;
import com.example.config.Config;
import com.example.entity.Product;
import com.example.repository.ProductRepository;

@RequestMapping("/products")
@RestController
public class ProductController {
	
	private final CacheManager cacheManager;
	private final Cache<Long, Product> productCache;
	private final ProductRepository repository;
	
	@Autowired
	public ProductController(CacheManager cacheManager, ProductRepository repository) {
		this.cacheManager = cacheManager;
		this.productCache = this.cacheManager.getCache(Config.PROUCT_CACHE_NAME, Long.class, Product.class);
		this.repository = repository;
	}
	
	@PostConstruct
	private void initializeCacheFromDB() {
		List<Product> productList = new ArrayList<>();
		repository.findAll().forEach(productList::add);
		
		for (Product product : productList) {
			// update the cache
			productCache.put(product.getCacheKey(), product);
			// update/set the keys
			CustomKeyGenerator.addKey(product.getCacheKey());
		}
		
		// does nothing!!!! ... no entries so keys not associated w/ Product(s)
		//productCache.getAll(CustomKeyGenerator.getKeysSet());
		
		System.out.println("done w/ initializeCacheFromDB()");
	}
	
    @RequestMapping(value = "/getByCacheKey/{key}", method = RequestMethod.GET)
    public Product getProductById(@PathVariable("key") Long key) {
      long start = System.nanoTime();
      Product product = productCache.get(key);
      
      System.out.println(String.format("getProduct(key) took: %s millis", (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))));
      
      return product;
    }
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public List<Product> getAllProducts() {
		long start = System.nanoTime();

		//need a way to get a set of cache keys
		Map<Long, Product> cachedProducMap = productCache.getAll(CustomKeyGenerator.getKeysSet());
		
		System.out.println(String.format("findAll() took: %s millis", (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))));
		
		return cachedProducMap.entrySet().stream()
			.map(x -> x.getValue())
			.collect(Collectors.toList());
	}	
	
    @RequestMapping(value = "/save/", method = RequestMethod.PUT)
    public Product save(@RequestBody Product product) {
    	long start = System.nanoTime();
    	
    	// have to generate cache Key; stored automatically
    	Long newCacheKey = CustomKeyGenerator.getAndStoreRandomKey();
    	// set cache Key on Product
    	product.setCacheKey(newCacheKey);
    	
    	// doesn't matter what id is set as long as its not null
    	//	in db gets set to auto-generated, sequence #
    	product.setId(newCacheKey);
    	
    	// persist in cache
    	Product savedProduct = productCache.putIfAbsent(newCacheKey, product);
    	
    	System.out.println(String.format("save(product) took: %s millis", (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))));
    	
    	return savedProduct;
    }
	
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id) {
    	long start = System.nanoTime();
    	productCache.remove(id);
    	
    	System.out.println(String.format("delete(id) took: %s millis", (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))));
    }
}
