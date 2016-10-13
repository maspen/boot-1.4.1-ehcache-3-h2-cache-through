# boot-1.4.1-ehcache-3-h2-cache-through

simple spring boot application that uses ehcache 3 and in-memory h2 database to demonstate the user of 'cache-through' functionality. importantly, this example also addresses the use of JPA as the mechanism for storing data in the back end.

TODO: explain a little about what this is doing & how this differs from the auto-configured version using xml

endpint for adding a single record:

TODO: pic

endpoint for getting a recorord uing the the cache key (NOT Entity#id)

TODO: pic

this follows the [Devoxx 2016 UK video](https://youtu.be/FQfd8x29Ud8)

