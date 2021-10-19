package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.RateEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IRateDAO {

    RateEntity getByName(String star) throws SQLException;
}
