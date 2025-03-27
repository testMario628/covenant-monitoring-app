package com.covenant.monitoring.repository;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.CovenantOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CovenantOwnerRepository extends JpaRepository<CovenantOwner, Long> {
    
    List<CovenantOwner> findByCovenant(Covenant covenant);
    
    List<CovenantOwner> findByOwnerName(String ownerName);
    
    List<CovenantOwner> findByOwnerUnit(String ownerUnit);
    
    List<CovenantOwner> findByOwnerNameAndOwnerUnit(String ownerName, String ownerUnit);
}
