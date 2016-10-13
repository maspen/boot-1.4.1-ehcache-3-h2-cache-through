# boot-1.4.1-ehcache-3-h2-cache-through

simple spring boot application that uses ehcache 3 and in-memory h2 database to demonstate the user of 'cache-through' functionality. importantly, this example also addresses the use of JPA as the mechanism for storing data in the back end. the mechanism by which cache-trough is made possible is the implementation of CacheLoaderWriter. the challenge of cache-though is the creation of a cache key generator for storing (inserting) the cached entities.

TODO: explain how a new field had to be added to the entity to accommodate
TODO: mention that the entity.id (id column in db) also has to be set BUT once persisted, the db sets this to the 'correct
 value based on insertion 'scheme'
TODO: explain the challenge (still unresolved) to update the cached entity.id once they are retrieved from the db

this application came about after trying to use ehcache 3 with the default, auto-configuration provided by spring boot & annotations (the spring boot documentation details this). when the expiry was set to a significant amount of time, when a 'put' was executed, the findAll method would not contain the record within the expiry interval. this was surprising and undesired. what this indicated that adding a record would insert it into the db but not make it availabe in the cache immediately. once the expiry period elapsed, the findAll method would then query the db & the newly-inserted record would then be available; this makes sense & behaves as expected. this (annotation-based caching approach) seemed inadequate. the desired behavior was to have any newly-added objects be present in the cache immediately (of course, as long as expiry hadn't elapsed) in addition to it being persisted in the bd.

... enter cache-through

* endpint for adding a single record: [http://localhost:8080/products/save/](http://localhost:8080/products/save/)

json to insert a new record (i use postman):

```{
	"name":"four",
	"price":4
}```

* endpoint for getting a record uing the the cache key (NOT Entity#id) example: http://localhost:8080/products/getByCacheKey/-4689282352029021067

the "5753825177764511602" is the cache key that is auto-generated when save() is called. This can be seen in the console:

```
...
MyCacheLoadWriter#load(key) key: -4689282352029021067
...
```
* endpoint for findAll(): [http://localhost:8080/products/all](http://localhost:8080/products/all)

NOTE: there's a makeSlow() method added in the CacheLoaderWriter implementation MyCacheLoadWriter to simulate lag when the application interacts w/ the db.

**what remains to be solved is the case when there are already records in the db. currently am tring to find a way to insert these into the cache on application startup**

this sample project relises on [Devoxx 2016 UK video](https://youtu.be/FQfd8x29Ud8) by Louis Jacomet & Aurelien Broszniowski

