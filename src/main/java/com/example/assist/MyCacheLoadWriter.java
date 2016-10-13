package com.example.assist;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.ehcache.spi.loaderwriter.BulkCacheLoadingException;
import org.ehcache.spi.loaderwriter.BulkCacheWritingException;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Product;
import com.example.repository.ProductRepository;

@Service
public class MyCacheLoadWriter implements CacheLoaderWriter<Long, Product> {

	private final ProductRepository repository;
	
	@Autowired
	public MyCacheLoadWriter(ProductRepository repository) {		
		this.repository = repository;
	}
	
	@PostConstruct
	void verifyInit() {
		System.out.println("MyCacheLoadWriter initialized");
		
		try {
			loadAll(null);
		} catch (Exception e) {
			System.err.println("error loadAll() on PostConstruct " + e.getMessage());
		}
	}
	
	// read-through:
	@Override
	public Product load(Long key) throws Exception {
		System.out.println("MyCacheLoadWriter#load(key) key: " + key.toString() );
		
		makeSlow();
		
		// NOTE: this is product.cacheKey NOT product.id
		return repository.getProductByCacheKey(key);
	}
	
	@Override
	public Map<Long, Product> loadAll(Iterable<? extends Long> keys) throws BulkCacheLoadingException, Exception {
		System.out.println("MyCacheLoadWriter#loadAll(Iterable<? extends Long> keys) keys");	
		
		// this is for startup, so data can be loaded ... when cache has nothing it it INCLUDING the keys
		if(null == keys || !keys.iterator().hasNext()) {
			System.out.println("MyCacheLoadWriter#loadAll(Iterable<? extends Long> keys) 'keys' is null or empty" );
		}
		
		makeSlow();
		Iterable<Product> productList = repository.findAll();
		Map<Long, Product> productMap = new HashMap<>();
		for (Product product : productList) {
			productMap.put(product.getCacheKey(), product);
		}
		
		return productMap;
	}
	
	// write-though
	@Override
	public void write(Long key, Product value) throws Exception {
		System.out.println("MyCacheLoadWriter#write(key, Product) key: " + key.toString() + " value: " + value.toString());
		
		makeSlow();
		Product savedProduct = repository.save(value);
		// TODO: now, savedProduct has product.id ... need a way to update cache & replace cached
		// 		value w/ this one 
	}

	// NOTE: for 'batching' this method is being called
	@Override
	public void writeAll(Iterable<? extends Entry<? extends Long, ? extends Product>> entries)
			throws BulkCacheWritingException, Exception {
		
		StringBuilder builder = new StringBuilder();
		
		for (Entry<? extends Long, ? extends Product> entry : entries) {
			// for sysout
			builder.append(entry.getKey().toString() + " " + entry.getValue().toString() + "|");
			// for repo
			write(entry.getKey(), entry.getValue());
		}
		
		System.out.println("MyCacheLoadWriter#writeAll(entries) entries: " + builder.toString());	
	}

	@Override
	public void delete(Long key) throws Exception {
		System.out.println("MyCacheLoadWriter#delete(key) key: " + key.toString());
		
		makeSlow();
		repository.deleteByCacheKey(key);
	}

	// NOTE: for 'batching' this method is being called
	@Override
	public void deleteAll(Iterable<? extends Long> keys) throws BulkCacheWritingException, Exception {
		StringBuilder builder = new StringBuilder();
		for (Long key : keys) {
			// for sysout
			builder.append(key.toString() + "|");
			// for repo
			delete(key);
		}
		
		System.out.println("MyCacheLoadWriter#deleteAll(keys) keys: " + builder.toString());
	}
	
	private void makeSlow() {
    	try {
            // Emulated repository call to simulate load
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            System.out.println("Error");
        }
    }
}
