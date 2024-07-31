package cz.engeto.radim.secondproject.service;

import cz.engeto.radim.secondproject.controller.ConfigurationManager;
import cz.engeto.radim.secondproject.controller.NotFoundException;
import cz.engeto.radim.secondproject.dto.User;
import net.bytebuddy.agent.VirtualMachine;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

@TestPropertySource("classpath:application-test.properties")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
//    UserService service = new UserService(ConfigurationManagerTest.fileWithPersonIdsTest());
    UserService service;

    @Test
    public void getUsersTest(){
        List<User> userList = service.getUsers(true);
        int size = userList.size();
        Assertions.assertEquals(3, size);

        userList = service.getUsers(false);
        size = userList.size();
        Assertions.assertEquals(3, size);
    }

    @Test
    public void getUserId1Test1(){
        //should return user ID 1
        int expUserId = 1;
        String expName = "Lisa";
        String expSurname = "Simpson";

        List<User> userList = new ArrayList<>();
        try {
            userList = service.getUser(expUserId, true);
        } catch (NotFoundException e) {
            Assertions.assertTrue(true);
        }

        Assertions.assertEquals(expUserId, userList.get(0).getId());
        Assertions.assertEquals(expName, userList.get(0).getName());
        Assertions.assertEquals(expSurname, userList.get(0).getSurname());
    }

    @Test
    public void getUserId1Test2(){
        //should return NotFoundException
        int expUserId = 10;

        List<User> userList = new ArrayList<>();
        try {
            userList = service.getUser(expUserId, true);
            Assertions.assertTrue(false);
        } catch (NotFoundException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void updateUserTest1(){
        //should update user
        String expName = "Bartolomej";
        String expSurname = "Simpson";

        User user = new User();
        user.setId(2);
        user.setName(expName);
        user.setSurname(expSurname);

        try{
            List<User> userList = service.updateUser(user);
            Assertions.assertEquals(expName, userList.get(0).getName());
            Assertions.assertEquals(expSurname, userList.get(0).getSurname());
        } catch (NotFoundException e){
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void updateUserTest2(){
        //should throw NotFoundException
        String expName = "Bartolomej";
        String expSurname = "Simpson";

        User user = new User();
        user.setId(20);
        user.setName(expName);
        user.setSurname(expSurname);

        try{
            List<User> userList = service.updateUser(user);
            Assertions.assertEquals(expName, userList.get(0).getName());
            Assertions.assertEquals(expSurname, userList.get(0).getSurname());
        } catch (NotFoundException e){
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void createUserTest1(){
        //should fail because personId is not valid
        String expName = "Montgomery";
        String expSurname = "Burns";
        String personId = "1a1a1a1a";

        User user = new User();
        user.setName(expName);
        user.setSurname(expSurname);
        user.setPersonId(personId);

        List<User> userList = new ArrayList<>();
        try {
            userList = service.createUser(user);
            Assertions.assertTrue(false);
        } catch (NotFoundException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void createUserTest2(){
        //should create new user
        String expName = "Montgomery";
        String expSurname = "Burns";
        String personId = "mY6sT1jA3cLz";

        User user = new User();
        user.setName(expName);
        user.setSurname(expSurname);
        user.setPersonId(personId);

        List<User> userList = new ArrayList<>();
        try {
            userList = service.createUser(user);
            Assertions.assertEquals(expName, userList.get(0).getName());
            Assertions.assertEquals(expSurname, userList.get(0).getSurname());
        } catch (NotFoundException e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void deleteUserTest1(){
        //should delete user 3
        String expString = "Deleted 1 record from database. Affected user ID = 3";

        try {
            String s = service.deleteUser(3);
            Assertions.assertEquals(expString, s);
        } catch (NotFoundException e){
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void deleteUserTest2(){
        //should throw NofFoundException for user 30
        String expString = "Deleted 1 record from database. Affected user ID = 3";

        try {
            String s = service.deleteUser(30);
            Assertions.assertEquals(expString, s);
        } catch (NotFoundException e){
            Assertions.assertTrue(true);
        }
    }
}
