package com.techelevator.controller;

import com.techelevator.dao.ModerationDao;
import com.techelevator.dao.UserDao;
import com.techelevator.model.Moderation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.module.ModuleReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class ModerationController {

    @Autowired
    ModerationDao moderationDao;
    @Autowired
    UserDao userDao;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/moderation/forum/{id}")
    public List<Moderation> getListOfModeratorOnForum(@PathVariable long id) {
        return moderationDao.getListOfModeratorsOfForum(id);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/moderation/user") //Todo only admin should have access maybe.
    public List<Moderation> getListOfUsersModeration(Principal user) {
        return moderationDao.getModerationsByUsername(user.getName());
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/moderation/forum/{id}/promote")
    public void promoteOrAddUserToMod(@PathVariable long id, @RequestBody String username, Principal user) {
        boolean isMod = false;
        List<Moderation> list = moderationDao.getListOfModeratorsOfForum(id);

        // storing the check if the current user has admin permission to make person moderator
        boolean isAdmin = userDao.getUserByUsername(user.getName()).getAuthorities().contains("ROLE_ADMIN");
        for (Moderation mod : list) {
            if (mod.getUsername() == user.getName()) {
                isMod = true;
                break;
            }
        }
        // to check if they are an admin or moderator of this forum
        if (isAdmin || isMod) {
            List<Moderation> mod = new ArrayList<>();

            // to see if the user is an mod of the forum already or not
            for (Moderation moderator : list) {
                if (moderator.getUsername().equals(username)){
                    mod.add(moderator);
                }
            }
            // confirming previous comment
            if (mod.isEmpty()) {
                moderationDao.makeUserModeratorOfForum(id, username);
            }
            else {
                //throw an exception with message or throw back message or just exception
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a Moderator of forum.");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User doesn't have permission for request.");
        }
    }
}