package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IVoteDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.VoteEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequestScoped
public class ImplVoteDAO implements IVoteDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public VoteEntity getByAccountIdAndBlogId(String accountId, String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        VoteEntity result = null;

        String sql = "SELECT id, rate_id " +
                "FROM vote " +
                "WHERE account_id = ? AND blog_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, accountId);
            stm.setString(2, blogId);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String rateId = resultSet.getString("rate_id");

                result = new VoteEntity(id, accountId, blogId, rateId);
            }
        }

        return result;
    }

    @Override
    public boolean deleteByAccountIdAndBlogId(String accountId, String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "DELETE FROM vote " +
                "WHERE account_id = ? AND blog_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, accountId);
            stm.setString(2, blogId);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean insertByVoteEntity(VoteEntity vote) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "INSERT INTO vote (account_id, blog_id, rate_id) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, vote.getAccountId());
            stm.setString(2, vote.getBlogId());
            stm.setString(3, vote.getRateId());

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }

        }

        return false;
    }
}
