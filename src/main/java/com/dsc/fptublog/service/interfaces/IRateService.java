package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.model.BlogRateModel;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IRateService {

    BlogRateModel getRateOfBlog(String blogId) throws SQLException;
}
