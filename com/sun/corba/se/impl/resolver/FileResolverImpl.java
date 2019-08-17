package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.resolver.Resolver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.omg.CORBA.Object;

public class FileResolverImpl implements Resolver {
  private ORB orb;
  
  private File file;
  
  private Properties savedProps;
  
  private long fileModified = 0L;
  
  public FileResolverImpl(ORB paramORB, File paramFile) {
    this.orb = paramORB;
    this.file = paramFile;
    this.savedProps = new Properties();
  }
  
  public Object resolve(String paramString) {
    check();
    String str = this.savedProps.getProperty(paramString);
    return (str == null) ? null : this.orb.string_to_object(str);
  }
  
  public Set list() {
    check();
    HashSet hashSet = new HashSet();
    Enumeration enumeration = this.savedProps.propertyNames();
    while (enumeration.hasMoreElements())
      hashSet.add(enumeration.nextElement()); 
    return hashSet;
  }
  
  private void check() {
    if (this.file == null)
      return; 
    long l = this.file.lastModified();
    if (l > this.fileModified)
      try {
        FileInputStream fileInputStream = new FileInputStream(this.file);
        this.savedProps.clear();
        this.savedProps.load(fileInputStream);
        fileInputStream.close();
        this.fileModified = l;
      } catch (FileNotFoundException fileNotFoundException) {
        System.err.println(CorbaResourceUtil.getText("bootstrap.filenotfound", this.file.getAbsolutePath()));
      } catch (IOException iOException) {
        System.err.println(CorbaResourceUtil.getText("bootstrap.exception", this.file.getAbsolutePath(), iOException.toString()));
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\resolver\FileResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */