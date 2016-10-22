package com.uber.buckcache.datastore.exceptions;

/**
 * The datastore is offline.
 */
public class DatastoreUnavailableException extends CacheException {
    public DatastoreUnavailableException(String message) {
        super(message);
    }
}
