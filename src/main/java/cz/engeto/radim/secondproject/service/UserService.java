package cz.engeto.radim.secondproject.service;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import cz.engeto.radim.secondproject.controller.PersonIdUsedException;
import cz.engeto.radim.secondproject.controller.NotFoundException;
import cz.engeto.radim.secondproject.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;

@Service
public class UserService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private ArrayList<String> personIDs = new ArrayList<>();
    Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(String file){
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

        if(listSize == 0){
            throw new NotFoundException("No users found in database.");
        }
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

        if (userList.size() != 1){
            throw new NotFoundException("User Id " + id + " not found");
        } else {

            log.info("User info for ID " + id + " returned");
            return userList;
        }
    }

    public List<User> updateUser(User user){
        String query = "update users set Name = '" + user.getName() + "', Surname = '" + user.getSurname() +
                "' where ID = " + user.getId();

        int nmbrOfRowsAffected = jdbcTemplate.update(query);

        String logString;
        if (nmbrOfRowsAffected != 1){
            logString = "User Id " + user.getId() + " not found in database. User not updated";
            log.warn(logString);
            throw new NotFoundException(logString);
        } else {
            logString = nmbrOfRowsAffected + " rows updated for user ID " + user.getId();
            log.info(logString);
        }

        query = "select * from users where ID = " + user.getId();
        return getDetailedUserList(query);
    }

    public List<User> createUser(User user) {
        List<User> userList = new ArrayList<>();
        String uuidString;

        boolean isValid = checkPersonId(user.getPersonId());
        if (!isValid){
            String errorMessage = "PersonId " + user.getPersonId() + " is not valid, user not created";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        boolean isAvailable;
        try {
            isAvailable= checkPersonIdAvailibility(user.getPersonId());
        } catch (SQLException e){
            throw new NotFoundException(e.getMessage());
        }
        if (!isAvailable){
            String errorMessage = "PersonId " + user.getPersonId() + " is already used, user not created";
            log.error(errorMessage);
            throw new PersonIdUsedException(errorMessage);
        }

        TimeBasedGenerator uuidV1Generator = Generators.timeBasedGenerator();
        uuidString = uuidV1Generator.generate().toString();

        String query = "insert into users (Name, Surname, PersonID, Uuid) values ('" + user.getName() + "', '" + user.getSurname() + "', '" +
                user.getPersonId() + "', '" + uuidString + "')";

            jdbcTemplate.update(query);

            query = "select * from users where Name = '" + user.getName()
                + "' and Surname = '" + user.getSurname()
                + "' and PersonID = '" + user.getPersonId()
                + "' and Uuid = '" + uuidString + "'";

            userList = getDetailedUserList(query);

            log.info("User created: ID = " + userList.get(0).getId() + ", Name = " + userList.get(0).getName() +
                    ", Surname = " + userList.get(0).getSurname());

        return userList;
    }

    private boolean checkPersonIdAvailibility(String personId) throws SQLException{
        String query = "select * from users where PersonID = '" + personId + "'";
        List<User> userlist = getReducedUserList(query);
        if (userlist.size() != 0){
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPersonId(String personId) {
        int index = this.personIDs.indexOf(personId);
        if(index == -1){
            return false;
        } else {
            return true;
        }
    }

    public String deleteUser(int id){
        String logString;

        String query = "delete from users where ID = " + id;
        int nmbr = jdbcTemplate.update(query);
        if (nmbr == 1) {
            logString = "Deleted 1 record from database. Affected user ID = " + id;
            log.info(logString);
        } else {
            logString = "User " + id + " not found in the database!";
            log.warn(logString);
            throw new NotFoundException(logString);
        }
        return logString;
    }

    public void clearPersonIds(){
        this.personIDs.clear();
    }

    public void addPersonId(String id){
        this.personIDs.add(id);
    }

}
