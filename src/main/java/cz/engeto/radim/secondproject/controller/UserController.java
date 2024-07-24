package cz.engeto.radim.secondproject.controller;

import cz.engeto.radim.secondproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import cz.engeto.radim.secondproject.dto.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@ResponseBody
public class UserController {

//    private static String FILE_WITH_PERSONIDs = "personIds.txt";

    @Autowired
    UserService service = new UserService(ConfigurationManager.fileWithPersonIds());

    @PostMapping("api/v1/user")
    public List<User> createUser(@RequestBody User user){
        return service.createUser(user);
    }

    @GetMapping("api/v1/user/{id}")
    public List<User> getUser(
            @PathVariable int id,
            @RequestParam(value = "detail") boolean detail
            ){
        return service.getUser(id, detail);
    }

    @GetMapping("api/v1/users")
    @ResponseStatus(code = HttpStatus.OK)
    public List<User> getUsers(
            @RequestParam(value = "detail") boolean detail
    ){
        return service.getUsers(detail);
    }

    @PutMapping("api/v1/user")
    public List<User> updateUser(
            @RequestBody User user
    ){
        return service.updateUser(user);
    }

    @DeleteMapping("api/v1/user/{id}")
    public String deleteUser(
            @PathVariable int id
            ){
        return service.deleteUser(id);
    }



}
