package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.*;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.*;
import com.dsc.fptublog.service.interfaces.IAdminService;
import com.dsc.fptublog.util.SHA256Util;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
@RequestScoped
public class ImplAdminService implements IAdminService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IAccountDAO accountDAO;

    @Inject
    private IAccountStatusDAO accountStatusDAO;

    @Inject
    private IAdminDAO adminDAO;

    @Inject
    private IBlogDAO blogDAO;

    @Inject
    private IBlogStatusDAO blogStatusDAO;

    @Inject
    private ILecturerStudentAwardDAO lecturerStudentAwardDAO;

    @Inject
    private IStudentDAO studentDAO;

    @Inject
    private ILecturerDAO lecturerDAO;

    @Inject
    private ILecturerFieldDAO lecturerFieldDAO;

    @Inject
    private IMajorDAO majorDAO;

    @Inject
    private IBlogHistoryDAO blogHistoryDAO;

    @Inject
    private IFieldDAO fieldDAO;

    @Inject
    private ICategoryDAO categoryDAO;

    @Inject
    private IBannedInfoDAO bannedInfoDAO;

    @Override
    public List<AccountEntity> getAllAccounts() throws SQLException {
        List<AccountEntity> accountList;
        try {
            connectionWrapper.beginTransaction();
            accountList = accountDAO.getAllAccounts();
            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }
        return accountList;
    }

    @Override
    public AccountEntity updateAccount(AccountEntity account) throws SQLException {
        try {
            connectionWrapper.beginTransaction();
            boolean resultUpdate = accountDAO.updateByAccount(account);
            if (resultUpdate) {
                connectionWrapper.commit();
                return account;
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return null;
    }

    @Override
    public AccountEntity updateRole(AccountEntity account) throws SQLException {
        AccountEntity result = null;
        try {
            connectionWrapper.beginTransaction();

            // Update to Lecturer
            if ("LECTURER".equals(account.getRole())) {
                // delete all lecturer_student_award of this student
                if (lecturerStudentAwardDAO.deleteByStudentId(account.getId())) {
                    // delete the student
                    if (studentDAO.deleteStudentById(account.getId())) {
                        // create new Lecturer
                        AccountEntity newAccount = AccountEntity.builder()
                                .id(account.getId())
                                .role(account.getRole())
                                .build();
                        if (accountDAO.updateByAccount(newAccount)) {
                            result = lecturerDAO.insertById(account.getId());
                        }
                    }
                }
            }

            // Update to Student
            if ("STUDENT".equals(account.getRole())) {
                // delete all lecturer_student_award of this lecturer
                if (lecturerStudentAwardDAO.deleteByLecturerId(account.getId())) {
                    // delete lecturer fields
                    if (lecturerFieldDAO.deleteByLecturerId(account.getId())) {
                        // convert blogs reviewed by this lecturer to pending approved
                        if (blogDAO.deleteReviewerId(account.getId())) {
                            AccountEntity newAccount = AccountEntity.builder()
                                    .id(account.getId())
                                    .role(account.getRole())
                                    .build();
                            if (lecturerDAO.deleteById(account.getId())) {
                                if (accountDAO.updateByAccount(newAccount)) {
                                    String majorId = majorDAO.getByName("Software Engineering").getId();
                                    result = studentDAO.insertByAccountIdAndMajorIdAndSchoolYear(account.getId(),
                                            majorId, (short) 0);
                                }
                            }
                        }
                    }
                }
            }

            connectionWrapper.commit();
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public void deleteAccount(String accountId) throws SQLException {
        try {
            connectionWrapper.beginTransaction();
            AccountEntity deleteAccount = accountDAO.getById(accountId);
            AccountStatusEntity deleteStatus = accountStatusDAO.getByName("deleted");
            deleteAccount.setStatusId(deleteStatus.getId());
            boolean result = accountDAO.deleteAccount(deleteAccount);
            if (result) {
                connectionWrapper.commit();
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
    }

    @Override
    public boolean getAuthentication(AdminEntity admin) throws SQLException, NoSuchAlgorithmException {
        boolean result = false;
        try {
            connectionWrapper.beginTransaction();

            admin.setPassword(SHA256Util.getEncryptedPassword(admin.getPassword()));
            result = adminDAO.checkAuthentication(admin);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public boolean banAccount(String accountId, String message) throws SQLException {
        boolean result = false;
        try {
            connectionWrapper.beginTransaction();
            // Set ban status Id for student account
            AccountStatusEntity banStatus = accountStatusDAO.getByName("banned");
            AccountEntity bannedStudentAccount = accountDAO.getById(accountId);
            if (bannedStudentAccount == null) {
                return false;
            }
            bannedStudentAccount.setStatusId(banStatus.getId());
            result = accountDAO.updateByAccount(bannedStudentAccount);

            // add Banned message
            result &= bannedInfoDAO.insertByAccountIdAndMessage(accountId, message);

            if (result) {
                connectionWrapper.commit();
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return result;
    }

    @Override
    public List<AccountEntity> getAllBannedAccounts() throws SQLException {
        List<AccountEntity> bannedAccounts;
        try {
            connectionWrapper.beginTransaction();
            bannedAccounts = accountDAO.getAllBannedAccounts();
            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }
        return bannedAccounts;
    }

    @Override
    public boolean deleteBlog(String id) throws SQLException {
        try {
            connectionWrapper.beginTransaction();
            BlogEntity deleteBlog = blogDAO.blogIdIsExistent(id); // check blog is exist
            BlogStatusEntity deleteStatus = blogStatusDAO.getByName("deleted");
            deleteBlog.setStatusId(deleteStatus.getId()); // set delete status id into blog
            boolean result = blogDAO.updateByBlog(deleteBlog); //update Blog's status to deleted
            if (result) {
                connectionWrapper.commit();
                return true;
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return false;
    }

    @Override
    public boolean unbanAccount(String accountId) throws SQLException {
        boolean result = false;
        try {
            connectionWrapper.beginTransaction();
            //set active statusId for account
            AccountStatusEntity activeStatus = accountStatusDAO.getByName("activated");
            AccountEntity account = AccountEntity.builder().id(accountId).build();
            account.setStatusId(activeStatus.getId());
            result = accountDAO.updateByAccount(account);
            if (result) {
                connectionWrapper.commit();
                return result;
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return result;
    }

    @Override
    public BlogEntity createBlog(BlogEntity newBlog) throws SQLException {
        CategoryEntity announcementCategory;
        try {
            connectionWrapper.beginTransaction();
            //create blog history
            long createdDateTime = System.currentTimeMillis();
            AccountEntity adminAccount = accountDAO.getAdminAccount();
            announcementCategory = categoryDAO.getByName("Announcement");
            System.out.println(announcementCategory);
            System.out.println(adminAccount);
            newBlog.setCategoryId(announcementCategory.getId());
            newBlog.setAuthorId(adminAccount.getId());
            BlogHistory blogHistory = blogHistoryDAO.insertByBlogHistory(new BlogHistory(null, newBlog.getAuthorId(), newBlog.getCategoryId(), createdDateTime, 0, 0));
            newBlog.setHistoryId(blogHistory.getId());
            BlogStatusEntity approvedStatus = blogStatusDAO.getByName("approved");
            newBlog.setStatusId(approvedStatus.getId());
            newBlog.setCreatedDateTime(createdDateTime);
            newBlog.setUpdatedDatetime(createdDateTime);
            newBlog.setViews(0);

            newBlog = blogDAO.insertByBlog(newBlog);
            connectionWrapper.commit();
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return newBlog;
    }

    @Override
    public BlogEntity updateBlog(BlogEntity updatedBlog) throws SQLException {
        BlogEntity result = null;
        try {
            connectionWrapper.beginTransaction();
            AccountEntity adminAccount = accountDAO.getAdminAccount();
            updatedBlog.setAuthorId(adminAccount.getId());
            //get olb blog then check authorId
            BlogEntity oldBlog = blogDAO.getById(updatedBlog.getId());
            if (!oldBlog.getAuthorId().equals(updatedBlog.getAuthorId())) {
                return null;
            }
            long currentTime = System.currentTimeMillis();

            oldBlog.setThumbnailUrl(updatedBlog.getThumbnailUrl());
            oldBlog.setTitle(updatedBlog.getTitle());
            oldBlog.setContent(updatedBlog.getContent());
            oldBlog.setDescription(updatedBlog.getDescription());
            oldBlog.setUpdatedDatetime(currentTime);
            oldBlog.setReviewerId(null);
            oldBlog.setReviewDateTime(0);

            //update db
            result = blogDAO.insertByBlog(oldBlog);
            connectionWrapper.commit();
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return result;
    }

    @Override
    public List<BlogEntity> getAllBlogsOfAdmin(int limit, int page) throws SQLException {
        List<BlogEntity> result;
        AccountEntity adminAccount = accountDAO.getAdminAccount();
        try {
            connectionWrapper.beginTransaction();

            int offset = limit * (page - 1);
            result = blogDAO.getByAuthorId(adminAccount.getId(), limit, offset);
            if (result == null) {
                result = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

}
