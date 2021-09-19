package com.learn.bookstore.serviceimpl;

import com.learn.bookstore.dao.UserDao;
import com.learn.bookstore.entity.User;
import com.learn.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.List;

@Service
@Scope("session")
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;


    @Override
    public User getUser(String account, String password) {
        return userDao.getUser(account,password);
    }

    @Override
    public List<User> getAllUser() { return userDao.getAllUsers(); }

    @Override
    public User banUser(User user){
        return userDao.saveUser(user);
    }//

    @Override
    public User saveUser(User user) {
       final String account = user.getAccount();
        User checkUser = userDao.getUserResponsitory().findOne(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {

                Path<Object> path = root.get("account");
                Predicate predicate = criteriaBuilder.equal(path.as(String.class),account);
                return predicate;

            }
        }).orElse(null);

        if(checkUser==null){
            return userDao.saveUser(user) ;
        }else {
            return null;
        }

    }
}

