package com.sun.org.apache.regexp.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class RETest {
  static final boolean showSuccesses = false;
  
  static final String NEW_LINE = System.getProperty("line.separator");
  
  REDebugCompiler compiler = new REDebugCompiler();
  
  int testCount = 0;
  
  int failures = 0;
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (!test(paramArrayOfString))
        System.exit(1); 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.exit(1);
    } 
  }
  
  public static boolean test(String[] paramArrayOfString) throws Exception {
    RETest rETest = new RETest();
    if (paramArrayOfString.length == 2) {
      rETest.runInteractiveTests(paramArrayOfString[1]);
    } else if (paramArrayOfString.length == 1) {
      rETest.runAutomatedTests(paramArrayOfString[0]);
    } else {
      System.out.println("Usage: RETest ([-i] [regex]) ([/path/to/testfile.txt])");
      System.out.println("By Default will run automated tests from file 'docs/RETest.txt' ...");
      System.out.println();
      rETest.runAutomatedTests("docs/RETest.txt");
    } 
    return (rETest.failures == 0);
  }
  
  void runInteractiveTests(String paramString) {
    RE rE = new RE();
    try {
      rE.setProgram(this.compiler.compile(paramString));
      say("" + NEW_LINE + "" + paramString + "" + NEW_LINE + "");
      PrintWriter printWriter = new PrintWriter(System.out);
      this.compiler.dumpProgram(printWriter);
      printWriter.flush();
      boolean bool = true;
      while (bool) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        System.out.flush();
        String str = bufferedReader.readLine();
        if (str != null) {
          if (rE.match(str)) {
            say("Match successful.");
          } else {
            say("Match failed.");
          } 
          showParens(rE);
          continue;
        } 
        bool = false;
        System.out.println();
      } 
    } catch (Exception exception) {
      say("Error: " + exception.toString());
      exception.printStackTrace();
    } 
  }
  
  void die(String paramString) {
    say("FATAL ERROR: " + paramString);
    System.exit(-1);
  }
  
  void fail(StringBuffer paramStringBuffer, String paramString) {
    System.out.print(paramStringBuffer.toString());
    fail(paramString);
  }
  
  void fail(String paramString) {
    this.failures++;
    say("" + NEW_LINE + "");
    say("*******************************************************");
    say("*********************  FAILURE!  **********************");
    say("*******************************************************");
    say("" + NEW_LINE + "");
    say(paramString);
    say("");
    if (this.compiler != null) {
      PrintWriter printWriter = new PrintWriter(System.out);
      this.compiler.dumpProgram(printWriter);
      printWriter.flush();
      say("" + NEW_LINE + "");
    } 
  }
  
  void say(String paramString) { System.out.println(paramString); }
  
  void showParens(RE paramRE) {
    for (byte b = 0; b < paramRE.getParenCount(); b++)
      say("$" + b + " = " + paramRE.getParen(b)); 
  }
  
  void runAutomatedTests(String paramString) {
    long l = System.currentTimeMillis();
    testPrecompiledRE();
    testSplitAndGrep();
    testSubst();
    testOther();
    File file = new File(paramString);
    if (!file.exists())
      throw new Exception("Could not find: " + paramString); 
    bufferedReader = new BufferedReader(new FileReader(file));
    try {
      while (bufferedReader.ready()) {
        RETestCase rETestCase = getNextTestCase(bufferedReader);
        if (rETestCase != null)
          rETestCase.runTest(); 
      } 
    } finally {
      bufferedReader.close();
    } 
    say(NEW_LINE + NEW_LINE + "Match time = " + (System.currentTimeMillis() - l) + " ms.");
    if (this.failures > 0)
      say("*************** THERE ARE FAILURES! *******************"); 
    say("Tests complete.  " + this.testCount + " tests, " + this.failures + " failure(s).");
  }
  
  void testOther() {
    RE rE = new RE("(a*)b");
    say("Serialized/deserialized (a*)b");
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(128);
    (new ObjectOutputStream(byteArrayOutputStream)).writeObject(rE);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    rE = (RE)(new ObjectInputStream(byteArrayInputStream)).readObject();
    if (!rE.match("aaab")) {
      fail("Did not match 'aaab' with deserialized RE.");
    } else {
      say("aaaab = true");
      showParens(rE);
    } 
    byteArrayOutputStream.reset();
    say("Deserialized (a*)b");
    (new ObjectOutputStream(byteArrayOutputStream)).writeObject(rE);
    byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    rE = (RE)(new ObjectInputStream(byteArrayInputStream)).readObject();
    if (rE.getParenCount() != 0)
      fail("Has parens after deserialization."); 
    if (!rE.match("aaab")) {
      fail("Did not match 'aaab' with deserialized RE.");
    } else {
      say("aaaab = true");
      showParens(rE);
    } 
    rE = new RE("abc(\\w*)");
    say("MATCH_CASEINDEPENDENT abc(\\w*)");
    rE.setMatchFlags(1);
    say("abc(d*)");
    if (!rE.match("abcddd")) {
      fail("Did not match 'abcddd'.");
    } else {
      say("abcddd = true");
      showParens(rE);
    } 
    if (!rE.match("aBcDDdd")) {
      fail("Did not match 'aBcDDdd'.");
    } else {
      say("aBcDDdd = true");
      showParens(rE);
    } 
    if (!rE.match("ABCDDDDD")) {
      fail("Did not match 'ABCDDDDD'.");
    } else {
      say("ABCDDDDD = true");
      showParens(rE);
    } 
    rE = new RE("(A*)b\\1");
    rE.setMatchFlags(1);
    if (!rE.match("AaAaaaBAAAAAA")) {
      fail("Did not match 'AaAaaaBAAAAAA'.");
    } else {
      say("AaAaaaBAAAAAA = true");
      showParens(rE);
    } 
    rE = new RE("[A-Z]*");
    rE.setMatchFlags(1);
    if (!rE.match("CaBgDe12")) {
      fail("Did not match 'CaBgDe12'.");
    } else {
      say("CaBgDe12 = true");
      showParens(rE);
    } 
    rE = new RE("^abc$", 2);
    if (!rE.match("\nabc"))
      fail("\"\\nabc\" doesn't match \"^abc$\""); 
    if (!rE.match("\rabc"))
      fail("\"\\rabc\" doesn't match \"^abc$\""); 
    if (!rE.match("\r\nabc"))
      fail("\"\\r\\nabc\" doesn't match \"^abc$\""); 
    if (!rE.match("abc"))
      fail("\"\\u0085abc\" doesn't match \"^abc$\""); 
    if (!rE.match(" abc"))
      fail("\"\\u2028abc\" doesn't match \"^abc$\""); 
    if (!rE.match(" abc"))
      fail("\"\\u2029abc\" doesn't match \"^abc$\""); 
    rE = new RE("^a.*b$", 2);
    if (rE.match("a\nb"))
      fail("\"a\\nb\" matches \"^a.*b$\""); 
    if (rE.match("a\rb"))
      fail("\"a\\rb\" matches \"^a.*b$\""); 
    if (rE.match("a\r\nb"))
      fail("\"a\\r\\nb\" matches \"^a.*b$\""); 
    if (rE.match("ab"))
      fail("\"a\\u0085b\" matches \"^a.*b$\""); 
    if (rE.match("a b"))
      fail("\"a\\u2028b\" matches \"^a.*b$\""); 
    if (rE.match("a b"))
      fail("\"a\\u2029b\" matches \"^a.*b$\""); 
  }
  
  private void testPrecompiledRE() {
    char[] arrayOfChar = { 
        '|', Character.MIN_VALUE, '\032', '|', Character.MIN_VALUE, '\r', 'A', '\001', '\004', 'a', 
        '|', Character.MIN_VALUE, '\003', 'G', Character.MIN_VALUE, '￶', '|', Character.MIN_VALUE, '\003', 'N', 
        Character.MIN_VALUE, '\003', 'A', '\001', '\004', 'b', 'E', Character.MIN_VALUE, Character.MIN_VALUE };
    REProgram rEProgram = new REProgram(arrayOfChar);
    RE rE = new RE(rEProgram);
    say("a*b");
    boolean bool = rE.match("aaab");
    say("aaab = " + bool);
    showParens(rE);
    if (!bool)
      fail("\"aaab\" doesn't match to precompiled \"a*b\""); 
    bool = rE.match("b");
    say("b = " + bool);
    showParens(rE);
    if (!bool)
      fail("\"b\" doesn't match to precompiled \"a*b\""); 
    bool = rE.match("c");
    say("c = " + bool);
    showParens(rE);
    if (bool)
      fail("\"c\" matches to precompiled \"a*b\""); 
    bool = rE.match("ccccaaaaab");
    say("ccccaaaaab = " + bool);
    showParens(rE);
    if (!bool)
      fail("\"ccccaaaaab\" doesn't match to precompiled \"a*b\""); 
  }
  
  private void testSplitAndGrep() {
    String[] arrayOfString1 = { "xxxx", "xxxx", "yyyy", "zzz" };
    RE rE = new RE("a*b");
    String[] arrayOfString2 = rE.split("xxxxaabxxxxbyyyyaaabzzz");
    byte b;
    for (b = 0; b < arrayOfString1.length && b < arrayOfString2.length; b++)
      assertEquals("Wrong splitted part", arrayOfString1[b], arrayOfString2[b]); 
    assertEquals("Wrong number of splitted parts", arrayOfString1.length, arrayOfString2.length);
    rE = new RE("x+");
    arrayOfString1 = new String[] { "xxxx", "xxxx" };
    arrayOfString2 = rE.grep(arrayOfString2);
    for (b = 0; b < arrayOfString2.length; b++) {
      say("s[" + b + "] = " + arrayOfString2[b]);
      assertEquals("Grep fails", arrayOfString1[b], arrayOfString2[b]);
    } 
    assertEquals("Wrong number of string found by grep", arrayOfString1.length, arrayOfString2.length);
  }
  
  private void testSubst() {
    RE rE = new RE("a*b");
    String str1 = "-foo-garply-wacky-";
    String str2 = rE.subst("aaaabfooaaabgarplyaaabwackyb", "-");
    assertEquals("Wrong result of substitution in \"a*b\"", str1, str2);
    rE = new RE("http://[\\.\\w\\-\\?/~_@&=%]+");
    str2 = rE.subst("visit us: http://www.apache.org!", "1234<a href=\"$0\">$0</a>", 2);
    assertEquals("Wrong subst() result", "visit us: 1234<a href=\"http://www.apache.org\">http://www.apache.org</a>!", str2);
    rE = new RE("(.*?)=(.*)");
    str2 = rE.subst("variable=value", "$1_test_$212", 2);
    assertEquals("Wrong subst() result", "variable_test_value12", str2);
    rE = new RE("^a$");
    str2 = rE.subst("a", "b", 2);
    assertEquals("Wrong subst() result", "b", str2);
    rE = new RE("^a$", 2);
    str2 = rE.subst("\r\na\r\n", "b", 2);
    assertEquals("Wrong subst() result", "\r\nb\r\n", str2);
  }
  
  public void assertEquals(String paramString1, String paramString2, String paramString3) {
    if ((paramString2 != null && !paramString2.equals(paramString3)) || (paramString3 != null && !paramString3.equals(paramString2)))
      fail(paramString1 + " (expected \"" + paramString2 + "\", actual \"" + paramString3 + "\")"); 
  }
  
  public void assertEquals(String paramString, int paramInt1, int paramInt2) {
    if (paramInt1 != paramInt2)
      fail(paramString + " (expected \"" + paramInt1 + "\", actual \"" + paramInt2 + "\")"); 
  }
  
  private boolean getExpectedResult(String paramString) {
    if ("NO".equals(paramString))
      return false; 
    if ("YES".equals(paramString))
      return true; 
    die("Test script error!");
    return false;
  }
  
  private String findNextTest(BufferedReader paramBufferedReader) throws IOException {
    String str = "";
    while (paramBufferedReader.ready()) {
      str = paramBufferedReader.readLine();
      if (str == null)
        break; 
      str = str.trim();
      if (str.startsWith("#"))
        break; 
      if (!str.equals("")) {
        say("Script error.  Line = " + str);
        System.exit(-1);
      } 
    } 
    return str;
  }
  
  private RETestCase getNextTestCase(BufferedReader paramBufferedReader) throws IOException {
    String str1 = findNextTest(paramBufferedReader);
    if (!paramBufferedReader.ready())
      return null; 
    String str2 = paramBufferedReader.readLine();
    String str3 = paramBufferedReader.readLine();
    boolean bool1 = "ERR".equals(str3);
    boolean bool2 = false;
    int i = 0;
    String[] arrayOfString = null;
    if (!bool1) {
      bool2 = getExpectedResult(paramBufferedReader.readLine().trim());
      if (bool2) {
        i = Integer.parseInt(paramBufferedReader.readLine().trim());
        arrayOfString = new String[i];
        for (byte b = 0; b < i; b++)
          arrayOfString[b] = paramBufferedReader.readLine(); 
      } 
    } 
    return new RETestCase(this, str1, str2, str3, bool1, bool2, arrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\RETest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */