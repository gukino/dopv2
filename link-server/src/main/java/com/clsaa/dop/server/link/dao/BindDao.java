package com.clsaa.dop.server.link.dao;

import com.clsaa.dop.server.link.model.po.Bind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BindDao extends JpaRepository<Bind, Long> {

    List<Bind> findByCuser(long cuser);

    @Modifying
    @Query(value = "update Bind b set b.state='STOP' where b.bid=?1")
    int stopBind(long bid);

    @Modifying
    @Query(value = "update Bind b set b.state='RUNNING' where b.bid=?2")
    int startBind(long bid);
}