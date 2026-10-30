package com.ucc.iaproxy.store;

import com.ucc.iaproxy.model.UserAccount;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store of user accounts.
 * Unknown users are created automatically on the FREE plan.
 */
@Component
public class UserStore {

    private final Map<String, UserAccount> accounts = new ConcurrentHashMap<>();

    public UserAccount get(String userId) {
        return accounts.computeIfAbsent(userId, UserAccount::new);
    }

    public Collection<UserAccount> all() {
        return accounts.values();
    }
}
