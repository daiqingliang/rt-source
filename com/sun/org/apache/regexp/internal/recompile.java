package com.sun.org.apache.regexp.internal;

public class recompile {
  public static void main(String[] paramArrayOfString) {
    RECompiler rECompiler = new RECompiler();
    if (paramArrayOfString.length <= 0 || paramArrayOfString.length % 2 != 0) {
      System.out.println("Usage: recompile <patternname> <pattern>");
      System.exit(0);
    } 
    for (boolean bool = false; bool < paramArrayOfString.length; bool += true) {
      try {
        String str1 = paramArrayOfString[bool];
        String str2 = paramArrayOfString[bool + true];
        String str3 = str1 + "PatternInstructions";
        System.out.print("\n    // Pre-compiled regular expression '" + str2 + "'\n    private static char[] " + str3 + " = \n    {");
        REProgram rEProgram = rECompiler.compile(str2);
        byte b1 = 7;
        char[] arrayOfChar = rEProgram.getInstructions();
        for (byte b2 = 0; b2 < arrayOfChar.length; b2++) {
          if (b2 % b1 == 0)
            System.out.print("\n        "); 
          String str;
          for (str = Integer.toHexString(arrayOfChar[b2]); str.length() < 4; str = "0" + str);
          System.out.print("0x" + str + ", ");
        } 
        System.out.println("\n    };");
        System.out.println("\n    private static RE " + str1 + "Pattern = new RE(new REProgram(" + str3 + "));");
      } catch (RESyntaxException rESyntaxException) {
        System.out.println("Syntax error in expression \"" + paramArrayOfString[bool] + "\": " + rESyntaxException.toString());
      } catch (Exception exception) {
        System.out.println("Unexpected exception: " + exception.toString());
      } catch (Error error) {
        System.out.println("Internal error: " + error.toString());
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\recompile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */