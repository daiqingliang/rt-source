package com.sun.org.glassfish.external.amx;

import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

@Taxonomy(stability = Stability.UNCOMMITTED)
public class MBeanListener<T extends MBeanListener.Callback> extends Object implements NotificationListener {
  private final String mJMXDomain;
  
  private final String mType;
  
  private final String mName;
  
  private final ObjectName mObjectName;
  
  private final MBeanServerConnection mMBeanServer;
  
  private final T mCallback;
  
  private static void debug(Object paramObject) { System.out.println("" + paramObject); }
  
  public String toString() { return "MBeanListener: ObjectName=" + this.mObjectName + ", type=" + this.mType + ", name=" + this.mName; }
  
  public String getType() { return this.mType; }
  
  public String getName() { return this.mName; }
  
  public MBeanServerConnection getMBeanServer() { return this.mMBeanServer; }
  
  public T getCallback() { return (T)this.mCallback; }
  
  public MBeanListener(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, T paramT) {
    this.mMBeanServer = paramMBeanServerConnection;
    this.mObjectName = paramObjectName;
    this.mJMXDomain = null;
    this.mType = null;
    this.mName = null;
    this.mCallback = paramT;
  }
  
  public MBeanListener(MBeanServerConnection paramMBeanServerConnection, String paramString1, String paramString2, T paramT) { this(paramMBeanServerConnection, paramString1, paramString2, null, paramT); }
  
  public MBeanListener(MBeanServerConnection paramMBeanServerConnection, String paramString1, String paramString2, String paramString3, T paramT) {
    this.mMBeanServer = paramMBeanServerConnection;
    this.mJMXDomain = paramString1;
    this.mType = paramString2;
    this.mName = paramString3;
    this.mObjectName = null;
    this.mCallback = paramT;
  }
  
  private boolean isRegistered(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName) {
    try {
      return paramMBeanServerConnection.isRegistered(paramObjectName);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  public void startListening() {
    try {
      this.mMBeanServer.addNotificationListener(AMXUtil.getMBeanServerDelegateObjectName(), this, null, this);
    } catch (Exception exception) {
      throw new RuntimeException("Can't add NotificationListener", exception);
    } 
    if (this.mObjectName != null) {
      if (isRegistered(this.mMBeanServer, this.mObjectName))
        this.mCallback.mbeanRegistered(this.mObjectName, this); 
    } else {
      String str = "type=" + this.mType;
      if (this.mName != null)
        str = str + "," + "name" + this.mName; 
      ObjectName objectName = AMXUtil.newObjectName(this.mJMXDomain + ":" + str);
      try {
        Set set = this.mMBeanServer.queryNames(objectName, null);
        for (ObjectName objectName1 : set)
          this.mCallback.mbeanRegistered(objectName1, this); 
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      } 
    } 
  }
  
  public void stopListening() {
    try {
      this.mMBeanServer.removeNotificationListener(AMXUtil.getMBeanServerDelegateObjectName(), this);
    } catch (Exception exception) {
      throw new RuntimeException("Can't remove NotificationListener " + this, exception);
    } 
  }
  
  public void handleNotification(Notification paramNotification, Object paramObject) {
    if (paramNotification instanceof MBeanServerNotification) {
      MBeanServerNotification mBeanServerNotification = (MBeanServerNotification)paramNotification;
      ObjectName objectName = mBeanServerNotification.getMBeanName();
      boolean bool = false;
      if (this.mObjectName != null && this.mObjectName.equals(objectName)) {
        bool = true;
      } else if (objectName.getDomain().equals(this.mJMXDomain) && this.mType != null && this.mType.equals(objectName.getKeyProperty("type"))) {
        String str = objectName.getKeyProperty("name");
        if (this.mName != null && this.mName.equals(str))
          bool = true; 
      } 
      if (bool) {
        String str = mBeanServerNotification.getType();
        if ("JMX.mbean.registered".equals(str)) {
          this.mCallback.mbeanRegistered(objectName, this);
        } else if ("JMX.mbean.unregistered".equals(str)) {
          this.mCallback.mbeanUnregistered(objectName, this);
        } 
      } 
    } 
  }
  
  public static interface Callback {
    void mbeanRegistered(ObjectName param1ObjectName, MBeanListener param1MBeanListener);
    
    void mbeanUnregistered(ObjectName param1ObjectName, MBeanListener param1MBeanListener);
  }
  
  public static class CallbackImpl implements Callback {
    private final boolean mStopAtFirst;
    
    protected final CountDownLatch mLatch = new CountDownLatch(1);
    
    public CallbackImpl() { this(true); }
    
    public CallbackImpl(boolean param1Boolean) { this.mStopAtFirst = param1Boolean; }
    
    public ObjectName getRegistered() { return this.mRegistered; }
    
    public ObjectName getUnregistered() { return this.mUnregistered; }
    
    public void await() {
      try {
        this.mLatch.await();
      } catch (InterruptedException interruptedException) {
        throw new RuntimeException(interruptedException);
      } 
    }
    
    public void mbeanRegistered(ObjectName param1ObjectName, MBeanListener param1MBeanListener) {
      this.mRegistered = param1ObjectName;
      if (this.mStopAtFirst)
        param1MBeanListener.stopListening(); 
    }
    
    public void mbeanUnregistered(ObjectName param1ObjectName, MBeanListener param1MBeanListener) {
      this.mUnregistered = param1ObjectName;
      if (this.mStopAtFirst)
        param1MBeanListener.stopListening(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\amx\MBeanListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */