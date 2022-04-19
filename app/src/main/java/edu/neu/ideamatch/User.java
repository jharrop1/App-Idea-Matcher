package edu.neu.ideamatch;

import java.util.ArrayList;

public class User {
    private String userName, email;
    private ArrayList<IdeaDetails> userIdeas;
    private ArrayList<String> likedProjects;

    public User() {
    }

    public User(String name, String email) {
        this.userName = name;
        this.email = email;
        this.userIdeas = new ArrayList<IdeaDetails>();
        this.likedProjects = new ArrayList<String>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<IdeaDetails> getUserIdeas() {
        return userIdeas;
    }

    public void setUserIdeas(ArrayList<IdeaDetails> userIdeas) {
        this.userIdeas = userIdeas;
    }

    public ArrayList<String> getLikedProjects() {
        return likedProjects;
    }

    public void setLikedProjects(ArrayList<String> likedProjects) {
        this.likedProjects = likedProjects;
    }
}
