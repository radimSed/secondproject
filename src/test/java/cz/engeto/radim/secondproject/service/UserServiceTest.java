package cz.engeto.radim.secondproject.service;

import cz.engeto.radim.secondproject.controller.ConfigurationManager;
import cz.engeto.radim.secondproject.dto.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

//@TestPropertySource("classpath:application-test.properties")
public class UserServiceTest {

    @Test
    public void getUsers(){
        UserService service = new UserService("personIdsTest.txt");

        List<User> userList = service.getUsers(true);
        int size = userList.size();
        Assertions.assertEquals(3, size);
    }
}
