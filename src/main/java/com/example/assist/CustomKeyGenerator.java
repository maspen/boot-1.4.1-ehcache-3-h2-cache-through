package com.example.assist;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gerneates cache keys.
 * Looked at {@link KeyGenerator} but that applies to key generation based on annotation; method
 * takes many params. This is a simple key generator.
 * 
 * @author maspen
 *
 */
public class CustomKeyGenerator {

	// need to maintain keys so that when a new key is generated, no duplicates are
	// generated (ie. for existing Value(s)).
	private static Set<Long> keysSet = new HashSet<>();
	
	public static Long getAndStoreRandomKey() {
		// generate random
		Long nextRandomLong = Long.valueOf(ThreadLocalRandom.current().nextLong());
		// see if does not exist in keysSet, add it to keysSet & return
		if(!keysSet.contains(nextRandomLong)) {
			keysSet.add(nextRandomLong);
			
			return nextRandomLong;
		} else {
			// otherwise, call recursively until unique key is returned
			return getAndStoreRandomKey();
		}
	}
	
	public static Set<Long> getKeysSet() {
		return keysSet;
	}
	
	// TODO: need to 'expose' exception
	public static void addKey(Long newKey) {
		if(!CustomKeyGenerator.containsKey(newKey)) {
			System.out.println("adding key: " + newKey);
		} else {
			System.err.println("duplicate key found: " + newKey);
			System.out.println("  keys: ");
			for (Long key : keysSet) {
				System.out.println("    " + key.longValue());
			}
			throw new IllegalArgumentException("duplicate key: " + newKey);
		}
	}
	
	public static void setKeys(List<Long> keys) {
		for (Long key : keys) {
			keysSet.add(key);
		}
	}
	
	public static boolean containsKey(Long key) {
		return keysSet.contains(key);
	}
}
