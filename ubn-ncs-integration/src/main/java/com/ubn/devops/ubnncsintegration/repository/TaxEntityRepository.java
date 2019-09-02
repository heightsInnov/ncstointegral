package com.ubn.devops.ubnncsintegration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubn.devops.ubnncsintegration.model.TaxEntity;

@Repository
public interface TaxEntityRepository extends JpaRepository<TaxEntity,Long> {
     
}
