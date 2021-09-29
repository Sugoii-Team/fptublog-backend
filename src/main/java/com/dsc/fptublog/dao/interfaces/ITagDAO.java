package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.TagEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface ITagDAO {
    public TagEntity getById(String id) throws SQLException;

    public boolean deleteById(String deletedTagId) throws SQLException;

    public boolean updateByTag(TagEntity updatedTag) throws SQLException;

    public TagEntity insertByTag(TagEntity tag) throws SQLException;
}
