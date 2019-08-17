package com.sun.org.apache.regexp.internal;

import java.io.PrintWriter;
import java.util.Hashtable;

public class REDebugCompiler extends RECompiler {
  static Hashtable hashOpcode = new Hashtable();
  
  String opcodeToString(char paramChar) {
    String str = (String)hashOpcode.get(new Integer(paramChar));
    if (str == null)
      str = "OP_????"; 
    return str;
  }
  
  String charToString(char paramChar) { return (paramChar < ' ' || paramChar > '') ? ("\\" + paramChar) : String.valueOf(paramChar); }
  
  String nodeToString(int paramInt) {
    char c1 = this.instruction[paramInt + 0];
    char c2 = this.instruction[paramInt + 1];
    return opcodeToString(c1) + ", opdata = " + c2;
  }
  
  public void dumpProgram(PrintWriter paramPrintWriter) {
    short s = 0;
    while (s < this.lenInstruction) {
      char c1 = this.instruction[s + false];
      char c2 = this.instruction[s + true];
      short s1 = (short)this.instruction[s + 2];
      paramPrintWriter.print(s + ". " + nodeToString(s) + ", next = ");
      if (s1 == 0) {
        paramPrintWriter.print("none");
      } else {
        paramPrintWriter.print(s + s1);
      } 
      s += 3;
      if (c1 == '[') {
        paramPrintWriter.print(", [");
        char c = c2;
        for (byte b = 0; b < c; b++) {
          char c3 = this.instruction[s++];
          char c4 = this.instruction[s++];
          if (c3 == c4) {
            paramPrintWriter.print(charToString(c3));
          } else {
            paramPrintWriter.print(charToString(c3) + "-" + charToString(c4));
          } 
        } 
        paramPrintWriter.print("]");
      } 
      if (c1 == 'A') {
        paramPrintWriter.print(", \"");
        char c = c2;
        while (c-- != '\000')
          paramPrintWriter.print(charToString(this.instruction[s++])); 
        paramPrintWriter.print("\"");
      } 
      paramPrintWriter.println("");
    } 
  }
  
  static  {
    hashOpcode.put(new Integer(56), "OP_RELUCTANTSTAR");
    hashOpcode.put(new Integer(61), "OP_RELUCTANTPLUS");
    hashOpcode.put(new Integer(47), "OP_RELUCTANTMAYBE");
    hashOpcode.put(new Integer(69), "OP_END");
    hashOpcode.put(new Integer(94), "OP_BOL");
    hashOpcode.put(new Integer(36), "OP_EOL");
    hashOpcode.put(new Integer(46), "OP_ANY");
    hashOpcode.put(new Integer(91), "OP_ANYOF");
    hashOpcode.put(new Integer(124), "OP_BRANCH");
    hashOpcode.put(new Integer(65), "OP_ATOM");
    hashOpcode.put(new Integer(42), "OP_STAR");
    hashOpcode.put(new Integer(43), "OP_PLUS");
    hashOpcode.put(new Integer(63), "OP_MAYBE");
    hashOpcode.put(new Integer(78), "OP_NOTHING");
    hashOpcode.put(new Integer(71), "OP_GOTO");
    hashOpcode.put(new Integer(92), "OP_ESCAPE");
    hashOpcode.put(new Integer(40), "OP_OPEN");
    hashOpcode.put(new Integer(41), "OP_CLOSE");
    hashOpcode.put(new Integer(35), "OP_BACKREF");
    hashOpcode.put(new Integer(80), "OP_POSIXCLASS");
    hashOpcode.put(new Integer(60), "OP_OPEN_CLUSTER");
    hashOpcode.put(new Integer(62), "OP_CLOSE_CLUSTER");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\REDebugCompiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */