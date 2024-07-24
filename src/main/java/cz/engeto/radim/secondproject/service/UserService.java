package cz.engeto.radim.secondproject.service;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import cz.engeto.radim.secondproject.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jmx.access.InvocationFailureException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
//import java.util.logging.Logger;
import java.io.InputStream;
import org.slf4j.LoggerFactory;
import cz.engeto.radim.secondproject.dto.User;

@Service
public class UserService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private ArrayList<String> personIDs = new ArrayList<>();
    Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(String file){
//        jdbcTemplate = new JdbcTemplate();
        setPersonIds(file);
    }
    /**
     * reads persond IDs from file into a List this.personIDs
     *
     */
    private void setPersonIds(String file){
        String str = null;

        try(InputStream is = getClass().getClassLoader().getResourceAsStream(file)){
            StringBuilder stringBuilder = new StringBuilder();
            str = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e) {
            System.err.println(e.getMessage());
        }

        String[] parts = str.split("\n");
        for( String line : parts ){
            this.personIDs.add(line);
        }
    }

    public List<User> getUsers(boolean detail) {
        String query, logString;
        List<User> userList;

        if (detail == true){
            query = "select * from users";
            userList = getDetailedUserList(query);
        } else {
            query = "select ID, Name, Surname from users";
            userList = getReducedUserList(query);
        }

        int listSize = userList.size();
        if(listSize == 1){
            logString = "List of 1 user returned.";
        } else {
            logString = "List of " + listSize + " users returned.";
        }
        log.info(logString);

        return userList;
    }

    private List<User> getDetailedUserList(String query){
        RowMapper<User> rowMapper = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getInt("ID"));
                user.setName(rs.getString("Name"));
                user.setSurname(rs.getString("Surname"));
                user.setPersonId(rs.getString("PersonID"));
                user.setUuid(rs.getString("Uuid"));
                return user;
            }
        };

        try {
            return jdbcTemplate.query(query, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            List<User> userList = new ArrayList<>();
            return  userList;
        }
    }

    private List<User> getReducedUserList(String query){
        RowMapper<User> rowMapper = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getInt("ID"));
                user.setName(rs.getString("Name"));
                user.setSurname(rs.getString("Surname"));
                return user;
            }
        };

        try {
            return jdbcTemplate.query(query, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            List<User> userList = new ArrayList<>();
            return  userList;
        }
    }

    public List<User> getUser(int id, boolean detail){
        String query;
        List<User> userList;

        if (detail == true){
            query = "select * from users where ID = " + id;
            userList = getDetailedUserList(query);
        } else {
            query = "select ID, Name, Surname from users where ID = " + id;;
            userList = getReducedUserList(query);
        }

        log.info("User info for ID " + id + " returned");
        return userList;
    }

    public List<User> updateUser(User user){
        String query = "update users set Name = '" + user.getName() + "', Surname = '" + user.getSurname() +
                "' where ID = " + user.getId() + " and PersonID = '" + user.getPersonId() + "' and Uuid = '" + user.getUuid() +
                "'";

        int nmbrOfRowsAffected = jdbcTemplate.update(query);

        String logString = nmbrOfRowsAffected + " rows updated for user ID " + user.getId();
        if (nmbrOfRowsAffected == 1){
            log.info(logString);
        } else {
            log.warn(logString);
        }

        query = "select * from users where ID = " + user.getId()
                + " and Name = '" + user.getName()
                + "' and Surname = '" + user.getSurname()
                + "' and PersonID = '" + user.getPersonId()
                + "' and Uuid = '" + user.getUuid() + "'";

        return getDetailedUserList(query);
    }

    public List<User> createUser(User user){
        List<User> userList = new ArrayList<>();
        String personId, uuidString;

        personId = getAvailablePersonId();
        if (personId == null){
            log.error("No person ID available, user not created");
        } else {
            TimeBasedGenerator uuidV1Generator = Generators.timeBasedGenerator();
            uuidString = uuidV1Generator.generate().toString();

            String query = "insert into users (Name, Surname, PersonID, Uuid) values ('" + user.getName() + "', '" + user.getSurname() + "', '" +
                personId + "', '" + uuidString + "')";

            jdbcTemplate.update(query);

            query = "select * from users where Name = '" + user.getName()
                + "' and Surname = '" + user.getSurname()
                + "' and PersonID = '" + personId
                + "' and Uuid = '" + uuidString + "'";

            userList = getDetailedUserList(query);

            log.info("User created: ID = " + userList.get(0).getId() + ", Name = " + userList.get(0).getName() +
                    ", Surname = " + userList.get(0).getSurname());
        }

        return userList;
    }

    private String getAvailablePersonId(){
        for(String id : this.personIDs){
            if(checkIdAvailibility(id) == true){
                return id;
            }
        }
        return null;
    }

    private boolean checkIdAvailibility(String id){
        String query = "select * from users where PersonID = '" + id + "'";
        List<User> userList = getDetailedUserList(query);

        if (userList.isEmpty()){
            return true;
        } else {
            return false;
        }
    }

    public String deleteUser(int id){
        String returnString;

        String query = "delete from users where ID = " + id;
        int nmbr = jdbcTemplate.update(query);
        returnString = "Deleted " + nmbr + " records from database. Affected user ID = " + id;
        if (nmbr == 1) {
            log.info(returnString);
        } else {
            log.warn(returnString);
        }
        return returnString;
    }
}
