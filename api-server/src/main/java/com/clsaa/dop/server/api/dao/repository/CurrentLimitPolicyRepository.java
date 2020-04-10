package com.clsaa.dop.server.api.dao.repository;

import com.clsaa.dop.server.api.dao.entity.CurrentLimitPolicy;
import com.clsaa.dop.server.api.dao.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CurrentLimitPolicyRepository extends JpaRepository<CurrentLimitPolicy,String> {
    CurrentLimitPolicy findCurrentLimitPolicyById(String id);

    CurrentLimitPolicy findByNameAndService(String name,Service service);

    List<CurrentLimitPolicy> findByService(Service service);

}