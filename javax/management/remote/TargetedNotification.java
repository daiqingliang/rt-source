package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.management.Notification;

public class TargetedNotification implements Serializable {
  private static final long serialVersionUID = 7676132089779300926L;
  
  private Notification notif;
  
  private Integer id;
  
  public TargetedNotification(Notification paramNotification, Integer paramInteger) {
    validate(paramNotification, paramInteger);
    this.notif = paramNotification;
    this.id = paramInteger;
  }
  
  public Notification getNotification() { return this.notif; }
  
  public Integer getListenerID() { return this.id; }
  
  public String toString() { return "{" + this.notif + ", " + this.id + "}"; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      validate(this.notif, this.id);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new InvalidObjectException(illegalArgumentException.getMessage());
    } 
  }
  
  private static void validate(Notification paramNotification, Integer paramInteger) {
    if (paramNotification == null)
      throw new IllegalArgumentException("Invalid notification: null"); 
    if (paramInteger == null)
      throw new IllegalArgumentException("Invalid listener ID: null"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\TargetedNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */