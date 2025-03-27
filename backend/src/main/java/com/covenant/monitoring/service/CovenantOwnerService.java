package com.covenant.monitoring.service;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.CovenantOwner;
import com.covenant.monitoring.repository.CovenantOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CovenantOwnerService {

    private final CovenantOwnerRepository covenantOwnerRepository;

    @Autowired
    public CovenantOwnerService(CovenantOwnerRepository covenantOwnerRepository) {
        this.covenantOwnerRepository = covenantOwnerRepository;
    }

    public List<CovenantOwner> getAllCovenantOwners() {
        return covenantOwnerRepository.findAll();
    }

    public Optional<CovenantOwner> getCovenantOwnerById(Long id) {
        return covenantOwnerRepository.findById(id);
    }

    public List<CovenantOwner> getCovenantOwnersByCovenant(Covenant covenant) {
        return covenantOwnerRepository.findByCovenant(covenant);
    }

    public List<CovenantOwner> getCovenantOwnersByOwnerName(String ownerName) {
        return covenantOwnerRepository.findByOwnerName(ownerName);
    }

    public List<CovenantOwner> getCovenantOwnersByOwnerUnit(String ownerUnit) {
        return covenantOwnerRepository.findByOwnerUnit(ownerUnit);
    }

    public List<CovenantOwner> getCovenantOwnersByOwnerNameAndUnit(String ownerName, String ownerUnit) {
        return covenantOwnerRepository.findByOwnerNameAndOwnerUnit(ownerName, ownerUnit);
    }

    public CovenantOwner saveCovenantOwner(CovenantOwner covenantOwner) {
        return covenantOwnerRepository.save(covenantOwner);
    }

    public void deleteCovenantOwner(Long id) {
        covenantOwnerRepository.deleteById(id);
    }
}
