package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AdminEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IAdminDAO {

    boolean checkAuthentication(AdminEntity admin) throws SQLException;
}
