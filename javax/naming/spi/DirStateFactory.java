package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public interface DirStateFactory extends StateFactory {
  Result getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes) throws NamingException;
  
  public static class Result {
    private Object obj;
    
    private Attributes attrs;
    
    public Result(Object param1Object, Attributes param1Attributes) {
      this.obj = param1Object;
      this.attrs = param1Attributes;
    }
    
    public Object getObject() { return this.obj; }
    
    public Attributes getAttributes() { return this.attrs; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\DirStateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */