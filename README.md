# boot-1.4.1-ehcache-3-h2-cache-through

simple spring boot application that uses ehcache 3 and in-memory h2 database to demonstate the user of 'cache-through' functionality. importantly, this example also addresses the use of JPA as the mechanism for storing data in the back end. the mechanism by which cache-trough is made possible is the implementation of CacheLoaderWriter. the challenge of cache-though is the creation of a cache key generator for storing (inserting) the cached entities.

this application came about after trying to use ehcache 3 with the default, auto-configuration provided by spring boot & annotations. when the expiry was set to a significant amount of time, when a 'put' was executed, the findAll operation would not contain the record within the expiry interval. this was surprising and undesired. what this indicated that adding a record would insert it into the db but not make it availabe in the cache immediately. once the expiry period elapsed, the findAll method would then query the db & the newly-inserted record would then be available. this (annotation-based caching approach) seemed inadequate. the desired behavior was to have any newly-added objects be present in the cache immediately (of course, as long as expiry hadn't elapsed) in addition to it being persisted in the bd.

... enter cache-through

endpint for adding a single record: localhost:8080/save/

json to insert a new record (i use postman):

```{
	"name":"four",
	"price":4
}```

endpoint for getting a recorord uing the the cache key (NOT Entity#id)

TODO: pic

this follows the [Devoxx 2016 UK video](https://youtu.be/FQfd8x29Ud8)

