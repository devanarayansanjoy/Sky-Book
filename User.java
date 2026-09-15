public class User {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private boolean isAdmin;

    public User(String id, String username, String password, String fullName, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.isAdmin = isAdmin;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public boolean isAdmin() { return isAdmin; }
}
