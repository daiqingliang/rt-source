package java.rmi.activation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.util.Arrays;
import java.util.Properties;

public final class ActivationGroupDesc implements Serializable {
  private String className;
  
  private String location;
  
  private MarshalledObject<?> data;
  
  private CommandEnvironment env;
  
  private Properties props;
  
  private static final long serialVersionUID = -4936225423168276595L;
  
  public ActivationGroupDesc(Properties paramProperties, CommandEnvironment paramCommandEnvironment) { this(null, null, null, paramProperties, paramCommandEnvironment); }
  
  public ActivationGroupDesc(String paramString1, String paramString2, MarshalledObject<?> paramMarshalledObject, Properties paramProperties, CommandEnvironment paramCommandEnvironment) {
    this.props = paramProperties;
    this.env = paramCommandEnvironment;
    this.data = paramMarshalledObject;
    this.location = paramString2;
    this.className = paramString1;
  }
  
  public String getClassName() { return this.className; }
  
  public String getLocation() { return this.location; }
  
  public MarshalledObject<?> getData() { return this.data; }
  
  public Properties getPropertyOverrides() { return (this.props != null) ? (Properties)this.props.clone() : null; }
  
  public CommandEnvironment getCommandEnvironment() { return this.env; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ActivationGroupDesc) {
      ActivationGroupDesc activationGroupDesc = (ActivationGroupDesc)paramObject;
      return (((this.className == null) ? (activationGroupDesc.className == null) : this.className.equals(activationGroupDesc.className)) && ((this.location == null) ? (activationGroupDesc.location == null) : this.location.equals(activationGroupDesc.location)) && ((this.data == null) ? (activationGroupDesc.data == null) : this.data.equals(activationGroupDesc.data)) && ((this.env == null) ? (activationGroupDesc.env == null) : this.env.equals(activationGroupDesc.env)) && ((this.props == null) ? (activationGroupDesc.props == null) : this.props.equals(activationGroupDesc.props)));
    } 
    return false;
  }
  
  public int hashCode() { return ((this.location == null) ? 0 : (this.location.hashCode() << 24)) ^ ((this.env == null) ? 0 : (this.env.hashCode() << 16)) ^ ((this.className == null) ? 0 : (this.className.hashCode() << 8)) ^ ((this.data == null) ? 0 : this.data.hashCode()); }
  
  public static class CommandEnvironment implements Serializable {
    private static final long serialVersionUID = 6165754737887770191L;
    
    private String command;
    
    private String[] options;
    
    public CommandEnvironment(String param1String, String[] param1ArrayOfString) {
      this.command = param1String;
      if (param1ArrayOfString == null) {
        this.options = new String[0];
      } else {
        this.options = new String[param1ArrayOfString.length];
        System.arraycopy(param1ArrayOfString, 0, this.options, 0, param1ArrayOfString.length);
      } 
    }
    
    public String getCommandPath() { return this.command; }
    
    public String[] getCommandOptions() { return (String[])this.options.clone(); }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof CommandEnvironment) {
        CommandEnvironment commandEnvironment = (CommandEnvironment)param1Object;
        return (((this.command == null) ? (commandEnvironment.command == null) : this.command.equals(commandEnvironment.command)) && Arrays.equals(this.options, commandEnvironment.options));
      } 
      return false;
    }
    
    public int hashCode() { return (this.command == null) ? 0 : this.command.hashCode(); }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws IOException, ClassNotFoundException {
      param1ObjectInputStream.defaultReadObject();
      if (this.options == null)
        this.options = new String[0]; 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\activation\ActivationGroupDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */