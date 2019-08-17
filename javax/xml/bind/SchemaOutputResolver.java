package javax.xml.bind;

import java.io.IOException;
import javax.xml.transform.Result;

public abstract class SchemaOutputResolver {
  public abstract Result createOutput(String paramString1, String paramString2) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\SchemaOutputResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */