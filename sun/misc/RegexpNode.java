package sun.misc;

import java.io.PrintStream;

class RegexpNode {
  char c = '#';
  
  RegexpNode firstchild;
  
  RegexpNode nextsibling;
  
  int depth = 0;
  
  boolean exact;
  
  Object result;
  
  String re = null;
  
  RegexpNode() {}
  
  RegexpNode(char paramChar, int paramInt) {}
  
  RegexpNode add(char paramChar) {
    RegexpNode regexpNode = this.firstchild;
    if (regexpNode == null) {
      regexpNode = new RegexpNode(paramChar, this.depth + 1);
    } else {
      while (regexpNode != null) {
        if (regexpNode.c == paramChar)
          return regexpNode; 
        regexpNode = regexpNode.nextsibling;
      } 
      regexpNode = new RegexpNode(paramChar, this.depth + 1);
      regexpNode.nextsibling = this.firstchild;
    } 
    this.firstchild = regexpNode;
    return regexpNode;
  }
  
  RegexpNode find(char paramChar) {
    for (RegexpNode regexpNode = this.firstchild; regexpNode != null; regexpNode = regexpNode.nextsibling) {
      if (regexpNode.c == paramChar)
        return regexpNode; 
    } 
    return null;
  }
  
  void print(PrintStream paramPrintStream) {
    if (this.nextsibling != null) {
      RegexpNode regexpNode = this;
      paramPrintStream.print("(");
      while (regexpNode != null) {
        paramPrintStream.write(regexpNode.c);
        if (regexpNode.firstchild != null)
          regexpNode.firstchild.print(paramPrintStream); 
        regexpNode = regexpNode.nextsibling;
        paramPrintStream.write((regexpNode != null) ? 124 : 41);
      } 
    } else {
      paramPrintStream.write(this.c);
      if (this.firstchild != null)
        this.firstchild.print(paramPrintStream); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\RegexpNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */