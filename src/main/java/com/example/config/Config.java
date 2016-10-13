package com.example.config;

import java.util.concurrent.TimeUnit;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.assist.MyCacheLoadWriter;
import com.example.entity.Product;

@Configuration
public class Config {
	public static final String PROUCT_CACHE_NAME = "product-cache-programmatic";
	private final long cacheExpiryDuration = 1l;
	private final TimeUnit cacheExpiryUnits = TimeUnit.MINUTES;
	
	@Autowired
	MyCacheLoadWriter myCacheLoadWriter;
	
	@Bean
	public CacheManager cacheManager() {
		CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache(PROUCT_CACHE_NAME, singularCacheConfigBuilderWithWriteBehind())
				.build(true);
		
		return cacheManager;
	}
	
	CacheConfigurationBuilder<Long, Product> singularCacheConfigBuilderWithWriteBehind() {
		CacheConfigurationBuilder<Long, Product> singularCacheConfigBuilder = 
				CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Product.class, ResourcePoolsBuilder.heap(200))
				.withExpiry(Expirations.timeToLiveExpiration(Duration.of(cacheExpiryDuration, cacheExpiryUnits)))
				.withLoaderWriter(myCacheLoadWriter)
				// NOTE: batching will create batches
//				.add(WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(3, TimeUnit.SECONDS, 3));
		//																			(long maxDelay, TimeUnit maxDelayUnit, int batchSize)
		//																			batchSize - will wait until this many for maxDelay until write behind
				// NOTE: un-batching will write as soon as something is there on the queue
				.add(WriteBehindConfigurationBuilder.newUnBatchedWriteBehindConfiguration());
		
		return singularCacheConfigBuilder;
	}
}
