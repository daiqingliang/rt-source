package javax.sql.rowset.serial;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class SerialDatalink implements Serializable, Cloneable {
  private URL url;
  
  private int baseType;
  
  private String baseTypeName;
  
  static final long serialVersionUID = 2826907821828733626L;
  
  public SerialDatalink(URL paramURL) throws SerialException {
    if (paramURL == null)
      throw new SerialException("Cannot serialize empty URL instance"); 
    this.url = paramURL;
  }
  
  public URL getDatalink() throws SerialException {
    URL uRL = null;
    try {
      uRL = new URL(this.url.toString());
    } catch (MalformedURLException malformedURLException) {
      throw new SerialException("MalformedURLException: " + malformedURLException.getMessage());
    } 
    return uRL;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof SerialDatalink) {
      SerialDatalink serialDatalink = (SerialDatalink)paramObject;
      return this.url.equals(serialDatalink.url);
    } 
    return false;
  }
  
  public int hashCode() { return 31 + this.url.hashCode(); }
  
  public Object clone() {
    try {
      return (SerialDatalink)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\serial\SerialDatalink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */