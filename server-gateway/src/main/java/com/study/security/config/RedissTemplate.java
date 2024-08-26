package com.study.security.config;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedissTemplate {

    private final RedisOperations<String, Object> redisOperations;

    /**
     * Set key and value into a hash key
     *
     * @param key     key value - must not be null.
     * @param hashKey hash key value -  must not be null.
     * @param val     Object value
     * @return Mono of object
     */
    public void set(String key, String hashKey, Object val) {
        redisOperations.opsForHash().put(key, hashKey, val);
    }

    /**
     * @param key key value - must not be null.
     * @return Flux of Object
     */
    public List<Object> get(@NotNull String key) {
        return redisOperations.opsForHash().values(key);
    }

    /**
     * Get value for given hashKey from hash at key.
     *
     * @param key     key value - must not be null.
     * @param hashKey hash key value -  must not be null.
     * @return Object
     */
    public Object get(String key, Object hashKey) {
        return redisOperations.opsForHash().get(key, hashKey);
    }

    /**
     * Delete a key that contained in a hash key.
     *
     * @param key     key value - must not be null.
     * @param hashKey hash key value -  must not be null.
     * @return 1 Success or 0 Error
     */
    public Long remove(String key, Object hashKey) {
        return redisOperations.opsForHash().delete(key, hashKey);
    }
}