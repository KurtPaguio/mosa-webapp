package com.example.mosawebapp.account.domain;

import javax.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, String> {
  Status findByStatus(String status);
  Status findByStatusIgnoreCase(String status);

}
