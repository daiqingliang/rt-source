package com.sun.jmx.snmp.IPAcl;

class Token {
  public int kind;
  
  public int beginLine;
  
  public int beginColumn;
  
  public int endLine;
  
  public int endColumn;
  
  public String image;
  
  public Token next;
  
  public Token specialToken;
  
  public final String toString() { return this.image; }
  
  public static final Token newToken(int paramInt) {
    switch (paramInt) {
    
    } 
    return new Token();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\Token.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */