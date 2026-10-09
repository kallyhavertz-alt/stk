package com.stkapi.stk;

import com.stkapi.stk.UserAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByAccountNumber(String accountNumber);

    // Prevents concurrent balance updates by locking the row until the transaction completes
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserAccount u WHERE u.accountNumber = :accountNumber")
    Optional<UserAccount> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);
}