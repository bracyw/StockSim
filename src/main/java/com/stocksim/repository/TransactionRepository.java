package com.stocksim.repository;

import com.stocksim.entity.Transaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByPortfolioIdAndDateLessThanEqualOrderByDateAscIdAsc(
            Long portfolioId, LocalDate date);

    List<Transaction> findByPortfolioIdOrderByDateAscIdAsc(Long portfolioId);
}
