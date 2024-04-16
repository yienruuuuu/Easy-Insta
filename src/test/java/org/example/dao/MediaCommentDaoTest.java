package org.example.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Eric.Lee
 * Date:2024/4/16
 */
@SpringBootTest
class MediaCommentDaoTest {
    @Autowired
    private MediaCommentDao mediaCommentDao;

    @Test
    void testFindAllDistinctIgUsers() {
        List<String> igUsers = mediaCommentDao.findDistinctUserNames();
        assertNotNull(igUsers);
        assertFalse(igUsers.isEmpty());
        System.out.println("User : " + igUsers);
    }
}