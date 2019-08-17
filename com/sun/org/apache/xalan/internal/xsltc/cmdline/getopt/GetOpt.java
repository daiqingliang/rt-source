package com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class GetOpt {
  private Option theCurrentOption = null;
  
  private ListIterator theOptionsIterator;
  
  private List theOptions = null;
  
  private List theCmdArgs = null;
  
  private OptionMatcher theOptionMatcher = null;
  
  public GetOpt(String[] paramArrayOfString, String paramString) {
    this.theOptions = new ArrayList();
    byte b1 = 0;
    this.theCmdArgs = new ArrayList();
    this.theOptionMatcher = new OptionMatcher(paramString);
    byte b2;
    for (b2 = 0; b2 < paramArrayOfString.length; b2++) {
      String str = paramArrayOfString[b2];
      int i = str.length();
      if (str.equals("--")) {
        b1 = b2 + true;
        break;
      } 
      if (str.startsWith("-") && i == 2) {
        this.theOptions.add(new Option(str.charAt(1)));
      } else if (str.startsWith("-") && i > 2) {
        for (byte b = 1; b < i; b++)
          this.theOptions.add(new Option(str.charAt(b))); 
      } else if (!str.startsWith("-")) {
        if (this.theOptions.size() == 0) {
          b1 = b2;
          break;
        } 
        int j = 0;
        j = this.theOptions.size() - 1;
        Option option = (Option)this.theOptions.get(j);
        char c = option.getArgLetter();
        if (!option.hasArg() && this.theOptionMatcher.hasArg(c)) {
          option.setArg(str);
        } else {
          b1 = b2;
          break;
        } 
      } 
    } 
    this.theOptionsIterator = this.theOptions.listIterator();
    for (b2 = b1; b2 < paramArrayOfString.length; b2++) {
      String str = paramArrayOfString[b2];
      this.theCmdArgs.add(str);
    } 
  }
  
  public void printOptions() {
    ListIterator listIterator = this.theOptions.listIterator();
    while (listIterator.hasNext()) {
      Option option = (Option)listIterator.next();
      System.out.print("OPT =" + option.getArgLetter());
      String str = option.getArgument();
      if (str != null)
        System.out.print(" " + str); 
      System.out.println();
    } 
  }
  
  public int getNextOption() throws IllegalArgumentException, MissingOptArgException {
    char c = '￿';
    if (this.theOptionsIterator.hasNext()) {
      this.theCurrentOption = (Option)this.theOptionsIterator.next();
      char c1 = this.theCurrentOption.getArgLetter();
      boolean bool = this.theOptionMatcher.hasArg(c1);
      String str = this.theCurrentOption.getArgument();
      if (!this.theOptionMatcher.match(c1)) {
        ErrorMsg errorMsg = new ErrorMsg("ILLEGAL_CMDLINE_OPTION_ERR", new Character(c1));
        throw new IllegalArgumentException(errorMsg.toString());
      } 
      if (bool && str == null) {
        ErrorMsg errorMsg = new ErrorMsg("CMDLINE_OPT_MISSING_ARG_ERR", new Character(c1));
        throw new MissingOptArgException(errorMsg.toString());
      } 
      c = c1;
    } 
    return c;
  }
  
  public String getOptionArg() {
    String str1 = null;
    String str2 = this.theCurrentOption.getArgument();
    char c = this.theCurrentOption.getArgLetter();
    if (this.theOptionMatcher.hasArg(c))
      str1 = str2; 
    return str1;
  }
  
  public String[] getCmdArgs() {
    String[] arrayOfString = new String[this.theCmdArgs.size()];
    byte b = 0;
    ListIterator listIterator = this.theCmdArgs.listIterator();
    while (listIterator.hasNext())
      arrayOfString[b++] = (String)listIterator.next(); 
    return arrayOfString;
  }
  
  class Option {
    private char theArgLetter;
    
    private String theArgument = null;
    
    public Option(char param1Char) { this.theArgLetter = param1Char; }
    
    public void setArg(String param1String) { this.theArgument = param1String; }
    
    public boolean hasArg() { return (this.theArgument != null); }
    
    public char getArgLetter() { return this.theArgLetter; }
    
    public String getArgument() { return this.theArgument; }
  }
  
  class OptionMatcher {
    private String theOptString = null;
    
    public OptionMatcher(String param1String) { this.theOptString = param1String; }
    
    public boolean match(char param1Char) {
      boolean bool = false;
      if (this.theOptString.indexOf(param1Char) != -1)
        bool = true; 
      return bool;
    }
    
    public boolean hasArg(char param1Char) {
      boolean bool = false;
      int i = this.theOptString.indexOf(param1Char) + 1;
      if (i == this.theOptString.length()) {
        bool = false;
      } else if (this.theOptString.charAt(i) == ':') {
        bool = true;
      } 
      return bool;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\cmdline\getopt\GetOpt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */