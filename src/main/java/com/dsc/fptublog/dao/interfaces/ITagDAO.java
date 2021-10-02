package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.TagEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ITagDAO {
    TagEntity getById(String id) throws SQLException;

    List<TagEntity> getByIdList(List<String> idList) throws SQLException;

    List<TagEntity> getAll() throws SQLException;

    boolean deleteById(String deletedTagId) throws SQLException;

    boolean updateByTag(TagEntity updatedTag) throws SQLException;

    TagEntity insertByTag(TagEntity tag) throws SQLException;

    List<TagEntity> insertIfNotExistedByTagList(List<TagEntity> tagList) throws SQLException;
}
