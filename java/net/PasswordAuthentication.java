package java.net;

public final class PasswordAuthentication {
  private String userName;
  
  private char[] password;
  
  public PasswordAuthentication(String paramString, char[] paramArrayOfChar) {
    this.userName = paramString;
    this.password = (char[])paramArrayOfChar.clone();
  }
  
  public String getUserName() { return this.userName; }
  
  public char[] getPassword() { return this.password; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\PasswordAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */