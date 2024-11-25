package vn.btec.campus.models;

public class User {
    private String id;
    
    private String username;
    private String email;
    private String profilePicPath;

    public User(String id, String username, String email, String profilePicPath) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePicPath = profilePicPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }
}
