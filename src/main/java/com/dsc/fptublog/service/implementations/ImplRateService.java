package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogRateDAO;
import com.dsc.fptublog.dao.interfaces.IRateDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogRateEntity;
import com.dsc.fptublog.model.BlogRateModel;
import com.dsc.fptublog.service.interfaces.IRateService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

@Service
@RequestScoped
public class ImplRateService implements IRateService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IBlogRateDAO blogRateDAO;

    @Inject
    private IRateDAO rateDAO;

    @Override
    public BlogRateModel getRateOfBlog(String blogId) throws SQLException {
        BlogRateModel blogRateModel = new BlogRateModel(0, 0, 0, 0, 0);

        try {
            connectionWrapper.beginTransaction();

            String oneStarId = rateDAO.getByName("1").getId();
            String twoStarId = rateDAO.getByName("2").getId();
            String threeStarId = rateDAO.getByName("3").getId();
            String fourStarId = rateDAO.getByName("4").getId();
            String fiveStarId = rateDAO.getByName("5").getId();

            List<BlogRateEntity> blogRateList = blogRateDAO.getBlogRateByBlogId(blogId);
            if (blogRateList != null) {
                for (var blogRate : blogRateList) {
                    if (oneStarId.equals(blogRate.getRateId())) {
                        blogRateModel.setOneStar(blogRate.getAmount());
                    }
                    if (twoStarId.equals(blogRate.getRateId())) {
                        blogRateModel.setTwoStar(blogRate.getAmount());
                    }
                    if (threeStarId.equals(blogRate.getRateId())) {
                        blogRateModel.setThreeStar(blogRate.getAmount());
                    }
                    if (fourStarId.equals(blogRate.getRateId())) {
                        blogRateModel.setFourStar(blogRate.getAmount());
                    }
                    if (fiveStarId.equals(blogRate.getRateId())) {
                        blogRateModel.setFiveStar(blogRate.getAmount());
                    }
                }
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return blogRateModel;
    }
}
