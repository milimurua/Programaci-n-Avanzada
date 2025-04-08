package session;

public class SessionManager {
    private static SessionManager singleInstance;
    private User currentUser;
    private User lastUser;

    // Private constructor
    private SessionManager() {
        System.out.println("Session manager created.");
    }

    //Singleton aplication
    public static SessionManager getInstance() {
        if (singleInstance == null) {
            singleInstance = new SessionManager();
        }
        return singleInstance;
    }

    public void login(User user) {
        this.currentUser = user;
        this.lastUser = user;
        System.out.println("Session started for: " + user.getName());
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("Session closed for: " + currentUser.getName());
            currentUser = null;
        } else {
            System.out.println("No active session.");
        }
    }

    public void reopenLastSession() {
        if (lastUser != null) {
            currentUser = lastUser;
            System.out.println("Session reopened for: " + currentUser.getName());
        } else {
            System.out.println("No previous session to reopen.");
        }
    }

    public void showSession() {
        if (currentUser != null) {
            System.out.println("Logged-in user: " + currentUser);
        } else {
            System.out.println("No active session.");
        }
    }
}
