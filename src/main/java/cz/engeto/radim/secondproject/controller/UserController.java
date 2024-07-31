package cz.engeto.radim.secondproject.controller;

import cz.engeto.radim.secondproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("api/v1/user")
    @ResponseBody
    public ResponseEntity<List<User>> createUser(@RequestBody User user) {
        return new ResponseEntity<>(service.createUser(user), HttpStatus.CREATED);
    }

    @GetMapping("api/v1/user/{id}")
    @ResponseBody
    public ResponseEntity<List<User>> getUser(
            @PathVariable int id,
            @RequestParam(value = "detail") boolean detail
            ){
        return new ResponseEntity<>(service.getUser(id, detail), HttpStatus.OK);
    }

    @GetMapping("api/v1/users")
    @ResponseBody
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(value = "detail") boolean detail
    ){
        return new ResponseEntity<>(service.getUsers(detail), HttpStatus.OK);
    }

    @PutMapping("api/v1/user")
    @ResponseBody
    public ResponseEntity<List<User>> updateUser(
            @RequestBody User user
    ){
        return new ResponseEntity<>(service.updateUser(user), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("api/v1/user/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteUser(
            @PathVariable int id
            ){
        return ResponseEntity.ok(service.deleteUser(id));
    }
}
