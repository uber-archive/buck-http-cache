package com.uber.buckcache.datastore.exceptions;

import javax.annotation.Nonnull;

/**
 * Generic exception class for cache.
 */
class CacheException extends Exception {
    @Nonnull private final String message;

    CacheException(@Nonnull String message) {
        this.message = message;
    }


    @Override
    @Nonnull
    public String getMessage() {
        return message;
    }
}
