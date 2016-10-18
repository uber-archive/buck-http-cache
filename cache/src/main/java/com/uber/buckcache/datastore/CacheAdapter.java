package com.uber.buckcache.datastore;

import javax.annotation.Nonnull;

import com.uber.buckcache.datastore.exceptions.DatastoreUnavailableException;
import com.uber.buckcache.datastore.exceptions.EntryNotFoundException;

/**
 * An adapter to the underlying cache.
 */
public interface CacheAdapter {
    @Nonnull
    CacheEntry get(String key) throws DatastoreUnavailableException, EntryNotFoundException;

    void put(@Nonnull String[] keys, @Nonnull CacheEntry cacheEntry) throws DatastoreUnavailableException;

    int cachedArtifactCount() throws DatastoreUnavailableException;
    int keysCount() throws DatastoreUnavailableException;
}
