package cz.engeto.radim.secondproject.controller;

import cz.engeto.radim.secondproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import cz.engeto.radim.secondproject.dto.User;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    UserService service;

    @GetMapping("/")
    public String loadHomePage() {
        Resource resource = new ClassPathResource("templates/genesisFrontEnd.html");
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] byteData = FileCopyUtils.copyToByteArray(inputStream);
            String content = new String(byteData, StandardCharsets.UTF_8);
            //LOGGER.info(content);
            return content;
        } catch (IOException e) {
            return "Frontend not accessible";
        }
        //return null;
    }

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
