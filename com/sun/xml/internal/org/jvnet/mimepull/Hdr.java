package com.sun.xml.internal.org.jvnet.mimepull;

class Hdr implements Header {
  String name;
  
  String line;
  
  Hdr(String paramString) {
    int i = paramString.indexOf(':');
    if (i < 0) {
      this.name = paramString.trim();
    } else {
      this.name = paramString.substring(0, i).trim();
    } 
    this.line = paramString;
  }
  
  Hdr(String paramString1, String paramString2) {
    this.name = paramString1;
    this.line = paramString1 + ": " + paramString2;
  }
  
  public String getName() { return this.name; }
  
  public String getValue() {
    int j;
    int i = this.line.indexOf(':');
    if (i < 0)
      return this.line; 
    if (this.name.equalsIgnoreCase("Content-Description")) {
      for (j = i + 1; j < this.line.length(); j++) {
        char c = this.line.charAt(j);
        if (c != '\t' && c != '\r' && c != '\n')
          break; 
      } 
    } else {
      for (j = i + 1; j < this.line.length(); j++) {
        char c = this.line.charAt(j);
        if (c != ' ' && c != '\t' && c != '\r' && c != '\n')
          break; 
      } 
    } 
    return this.line.substring(j);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\Hdr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */