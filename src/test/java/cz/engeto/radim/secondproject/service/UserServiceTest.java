package cz.engeto.radim.secondproject.service;

import cz.engeto.radim.secondproject.controller.ConfigurationManager;
import cz.engeto.radim.secondproject.dto.User;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

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
    public void getUserId1Test(){
        String expName = "Lisa";
        String expSurname = "Simpson";

        List<User> userlist = service.getUser(1, true);

        Assertions.assertEquals(expName, userlist.get(0).getName());
        Assertions.assertEquals(expSurname, userlist.get(0).getSurname());

        userlist = service.getUser(10, true);
        Assertions.assertEquals(0, userlist.size());

    }

    @Test
    public void updateUserTest(){
        String expName = "Bartolomej";

        User user = new User();
        user.setId(2);
        user.setName(expName);
        user.setSurname("Simpson");
//        user.setPersonId("333");
//        user.setUuid("444");

        service.updateUser(user);

        User resUser = service.getUser(2, true).get(0);

        Assertions.assertEquals(expName, resUser.getName());
        Assertions.assertEquals(2, resUser.getId());
        Assertions.assertEquals("Simpson", resUser.getSurname());
    }

    @Test
    public void createUserTest(){
        //first clear person Ids read from file and replace them with some predictable value
        String id = "1a1a1a1a";

        service.clearPersonIds();
        service.addPersonId(id);

        String expName = "Montgomery";
        String expSurname = "Burns";

        User user = new User();
        user.setName(expName);
        user.setSurname(expSurname);

        service.createUser(user);

        User resUser = service.getUser(4, true).get(0);

        Assertions.assertEquals(expName, resUser.getName());
        Assertions.assertEquals(expSurname, resUser.getSurname());
        Assertions.assertEquals(id, resUser.getPersonId());

        //try to create another user which should not be possible because no person Id is available
        //currently there is only one in the stack and it is already used

        User user2 = new User();
        user2.setName("Marge");
        user2.setSurname("Simpson");

        List<User> userList = service.createUser(user2);

        Assertions.assertEquals(0, userList.size());
    }

    @Test
    public void deleteUserTest(){
        String expString = "Deleted 1 record from database. Affected user ID = 3";
        String expString2 = "Deleted 0 records from database. Affected user ID = 3";
        String s  = service.deleteUser(3);
        Assertions.assertEquals(expString, s);
        s  = service.deleteUser(3);
        Assertions.assertEquals(expString2, s);
    }
}
