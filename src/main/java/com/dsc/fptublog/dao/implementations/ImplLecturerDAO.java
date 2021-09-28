package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.dao.interfaces.ILecturerAwardDAO;
import com.dsc.fptublog.dao.interfaces.ILecturerDAO;
import com.dsc.fptublog.dao.interfaces.ILecturerFieldDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.LecturerAwardEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.entity.LecturerFieldEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequestScoped
public class ImplLecturerDAO implements ILecturerDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IAccountDAO accountDAO;

    @Inject
    private ILecturerFieldDAO lecturerFieldDAO;

    @Inject
    private ILecturerAwardDAO lecturerAwardDAO;

    @Override
    public LecturerEntity getById(String id) {
        return null;
    }

    @Override
    public LecturerEntity getByEmail(String email) {
        return null;
    }

    @Override
    public LecturerEntity getByAccount(AccountEntity account) throws SQLException {
        if (account == null) {
            return null;
        }

        Connection connection = connectionWrapper.getConnection();
        LecturerEntity result = null;
        if (connection != null) {
            String sql = "SELECT id " +
                         "FROM account_lecturer " +
                         "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, account.getId());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                List<LecturerFieldEntity> lecturerFieldList = lecturerFieldDAO.getByLecturerId(account.getId());
                List<LecturerAwardEntity> lecturerAwardList = lecturerAwardDAO.getByLecturerId(account.getId());

                result = new LecturerEntity(account, lecturerFieldList, lecturerAwardList);
            }
        }
        return result;
    }
}
