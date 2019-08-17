package javax.naming.spi;

import javax.naming.Name;
import javax.naming.directory.DirContext;

class DirContextNamePair {
  DirContext ctx;
  
  Name name;
  
  DirContextNamePair(DirContext paramDirContext, Name paramName) {
    this.ctx = paramDirContext;
    this.name = paramName;
  }
  
  DirContext getDirContext() { return this.ctx; }
  
  Name getName() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\DirContextNamePair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */