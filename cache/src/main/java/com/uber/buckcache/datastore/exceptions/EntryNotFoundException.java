package com.uber.buckcache.datastore.exceptions;

/**
 * The specified entry was not found.
 */
public class EntryNotFoundException extends CacheException {
    public EntryNotFoundException(String message) {
        super(message);
    }
}
