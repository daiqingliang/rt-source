package javax.activation;

import java.beans.Beans;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class CommandInfo {
  private String verb;
  
  private String className;
  
  public CommandInfo(String paramString1, String paramString2) {
    this.verb = paramString1;
    this.className = paramString2;
  }
  
  public String getCommandName() { return this.verb; }
  
  public String getCommandClass() { return this.className; }
  
  public Object getCommandObject(DataHandler paramDataHandler, ClassLoader paramClassLoader) throws IOException, ClassNotFoundException {
    Object object = null;
    object = Beans.instantiate(paramClassLoader, this.className);
    if (object != null)
      if (object instanceof CommandObject) {
        ((CommandObject)object).setCommandContext(this.verb, paramDataHandler);
      } else if (object instanceof Externalizable && paramDataHandler != null) {
        InputStream inputStream = paramDataHandler.getInputStream();
        if (inputStream != null)
          ((Externalizable)object).readExternal(new ObjectInputStream(inputStream)); 
      }  
    return object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\CommandInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */