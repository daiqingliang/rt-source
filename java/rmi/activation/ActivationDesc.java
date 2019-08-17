package java.rmi.activation;

import java.io.Serializable;
import java.rmi.MarshalledObject;

public final class ActivationDesc implements Serializable {
  private ActivationGroupID groupID;
  
  private String className;
  
  private String location;
  
  private MarshalledObject<?> data;
  
  private boolean restart;
  
  private static final long serialVersionUID = 7455834104417690957L;
  
  public ActivationDesc(String paramString1, String paramString2, MarshalledObject<?> paramMarshalledObject) throws ActivationException { this(ActivationGroup.internalCurrentGroupID(), paramString1, paramString2, paramMarshalledObject, false); }
  
  public ActivationDesc(String paramString1, String paramString2, MarshalledObject<?> paramMarshalledObject, boolean paramBoolean) throws ActivationException { this(ActivationGroup.internalCurrentGroupID(), paramString1, paramString2, paramMarshalledObject, paramBoolean); }
  
  public ActivationDesc(ActivationGroupID paramActivationGroupID, String paramString1, String paramString2, MarshalledObject<?> paramMarshalledObject) { this(paramActivationGroupID, paramString1, paramString2, paramMarshalledObject, false); }
  
  public ActivationDesc(ActivationGroupID paramActivationGroupID, String paramString1, String paramString2, MarshalledObject<?> paramMarshalledObject, boolean paramBoolean) {
    if (paramActivationGroupID == null)
      throw new IllegalArgumentException("groupID can't be null"); 
    this.groupID = paramActivationGroupID;
    this.className = paramString1;
    this.location = paramString2;
    this.data = paramMarshalledObject;
    this.restart = paramBoolean;
  }
  
  public ActivationGroupID getGroupID() { return this.groupID; }
  
  public String getClassName() { return this.className; }
  
  public String getLocation() { return this.location; }
  
  public MarshalledObject<?> getData() { return this.data; }
  
  public boolean getRestartMode() { return this.restart; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ActivationDesc) {
      ActivationDesc activationDesc = (ActivationDesc)paramObject;
      return (((this.groupID == null) ? (activationDesc.groupID == null) : this.groupID.equals(activationDesc.groupID)) && ((this.className == null) ? (activationDesc.className == null) : this.className.equals(activationDesc.className)) && ((this.location == null) ? (activationDesc.location == null) : this.location.equals(activationDesc.location)) && ((this.data == null) ? (activationDesc.data == null) : this.data.equals(activationDesc.data)) && this.restart == activationDesc.restart);
    } 
    return false;
  }
  
  public int hashCode() { return ((this.location == null) ? 0 : (this.location.hashCode() << 24)) ^ ((this.groupID == null) ? 0 : (this.groupID.hashCode() << 16)) ^ ((this.className == null) ? 0 : (this.className.hashCode() << 9)) ^ ((this.data == null) ? 0 : (this.data.hashCode() << 1)) ^ (this.restart ? 1 : 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\activation\ActivationDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */