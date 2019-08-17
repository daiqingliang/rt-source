package javax.naming.spi;

import javax.naming.directory.DirContext;

class DirContextStringPair {
  DirContext ctx;
  
  String str;
  
  DirContextStringPair(DirContext paramDirContext, String paramString) {
    this.ctx = paramDirContext;
    this.str = paramString;
  }
  
  DirContext getDirContext() { return this.ctx; }
  
  String getString() { return this.str; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\DirContextStringPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */