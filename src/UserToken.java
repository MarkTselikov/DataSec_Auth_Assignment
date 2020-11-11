public class UserToken {

    public final String username;
    public final boolean isLogged;
    public final String token;

    public UserToken(String name, String token, boolean log) {
    this.username = name;
    this.isLogged = log;
    this.token =  token;
  }
}