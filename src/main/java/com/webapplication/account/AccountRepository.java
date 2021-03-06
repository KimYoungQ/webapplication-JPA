package com.webapplication.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByName(String name);

    Account findByName(String name);
}
