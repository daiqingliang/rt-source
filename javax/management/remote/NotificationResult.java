package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class NotificationResult implements Serializable {
  private static final long serialVersionUID = 1191800228721395279L;
  
  private long earliestSequenceNumber;
  
  private long nextSequenceNumber;
  
  private TargetedNotification[] targetedNotifications;
  
  public NotificationResult(long paramLong1, long paramLong2, TargetedNotification[] paramArrayOfTargetedNotification) {
    validate(paramArrayOfTargetedNotification, paramLong1, paramLong2);
    this.earliestSequenceNumber = paramLong1;
    this.nextSequenceNumber = paramLong2;
    this.targetedNotifications = (paramArrayOfTargetedNotification.length == 0) ? paramArrayOfTargetedNotification : (TargetedNotification[])paramArrayOfTargetedNotification.clone();
  }
  
  public long getEarliestSequenceNumber() { return this.earliestSequenceNumber; }
  
  public long getNextSequenceNumber() { return this.nextSequenceNumber; }
  
  public TargetedNotification[] getTargetedNotifications() { return (this.targetedNotifications.length == 0) ? this.targetedNotifications : (TargetedNotification[])this.targetedNotifications.clone(); }
  
  public String toString() { return "NotificationResult: earliest=" + getEarliestSequenceNumber() + "; next=" + getNextSequenceNumber() + "; nnotifs=" + getTargetedNotifications().length; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      validate(this.targetedNotifications, this.earliestSequenceNumber, this.nextSequenceNumber);
      this.targetedNotifications = (this.targetedNotifications.length == 0) ? this.targetedNotifications : (TargetedNotification[])this.targetedNotifications.clone();
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new InvalidObjectException(illegalArgumentException.getMessage());
    } 
  }
  
  private static void validate(TargetedNotification[] paramArrayOfTargetedNotification, long paramLong1, long paramLong2) throws IllegalArgumentException {
    if (paramArrayOfTargetedNotification == null)
      throw new IllegalArgumentException("Notifications null"); 
    if (paramLong1 < 0L || paramLong2 < 0L)
      throw new IllegalArgumentException("Bad sequence numbers"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\NotificationResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */