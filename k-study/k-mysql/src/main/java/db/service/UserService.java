package db.service;

import db.dao.UserDao;
import db.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HK
 * @date 2021-03-17 17:49
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    public List<User> allUser() {
        return userDao.findAll();
    }

}