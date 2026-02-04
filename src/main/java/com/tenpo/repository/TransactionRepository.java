package com.tenpo.repository;

import com.tenpo.model.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @EntityGraph(attributePaths = { "employee" })
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.employee.client.id = :clientId")
    long countByClientId(@Param("clientId") Integer clientId);

    @EntityGraph(attributePaths = { "employee" })
    @Query("SELECT t FROM Transaction t WHERE t.employee.client.id = :clientId")
    List<Transaction> findByClientId(@Param("clientId") Integer clientId);

    @EntityGraph(attributePaths = { "employee" })
    @Query("SELECT t FROM Transaction t WHERE t.employee.id = :employeeId")
    List<Transaction> findByEmployeeId(@Param("employeeId") Integer employeeId);
}
