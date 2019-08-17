package com.sun.org.apache.regexp.internal;

import java.io.StringBufferInputStream;
import java.io.StringReader;

final class RETestCase {
  private final StringBuffer log = new StringBuffer();
  
  private final int number;
  
  private final String tag;
  
  private final String pattern;
  
  private final String toMatch;
  
  private final boolean badPattern;
  
  private final boolean shouldMatch;
  
  private final String[] parens;
  
  private final RETest test;
  
  private RE regexp;
  
  public RETestCase(RETest paramRETest, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString) {
    this.number = ++paramRETest.testCount;
    this.test = paramRETest;
    this.tag = paramString1;
    this.pattern = paramString2;
    this.toMatch = paramString3;
    this.badPattern = paramBoolean1;
    this.shouldMatch = paramBoolean2;
    if (paramArrayOfString != null) {
      this.parens = new String[paramArrayOfString.length];
      for (byte b = 0; b < paramArrayOfString.length; b++)
        this.parens[b] = paramArrayOfString[b]; 
    } else {
      this.parens = null;
    } 
  }
  
  public void runTest() {
    this.test.say(this.tag + "(" + this.number + "): " + this.pattern);
    if (testCreation())
      testMatch(); 
  }
  
  boolean testCreation() {
    try {
      this.regexp = new RE();
      this.regexp.setProgram(this.test.compiler.compile(this.pattern));
      if (this.badPattern) {
        this.test.fail(this.log, "Was expected to be an error, but wasn't.");
        return false;
      } 
      return true;
    } catch (Exception exception) {
      if (this.badPattern) {
        this.log.append("   Match: ERR\n");
        success("Produces an error (" + exception.toString() + "), as expected.");
        return false;
      } 
      String str = (exception.getMessage() == null) ? exception.toString() : exception.getMessage();
      this.test.fail(this.log, "Produces an unexpected exception \"" + str + "\"");
      exception.printStackTrace();
    } catch (Error error) {
      this.test.fail(this.log, "Compiler threw fatal error \"" + error.getMessage() + "\"");
      error.printStackTrace();
    } 
    return false;
  }
  
  private void testMatch() {
    this.log.append("   Match against: '" + this.toMatch + "'\n");
    try {
      boolean bool = this.regexp.match(this.toMatch);
      this.log.append("   Matched: " + (bool ? "YES" : "NO") + "\n");
      if (checkResult(bool) && (!this.shouldMatch || checkParens())) {
        this.log.append("   Match using StringCharacterIterator\n");
        if (!tryMatchUsingCI(new StringCharacterIterator(this.toMatch)))
          return; 
        this.log.append("   Match using CharacterArrayCharacterIterator\n");
        if (!tryMatchUsingCI(new CharacterArrayCharacterIterator(this.toMatch.toCharArray(), 0, this.toMatch.length())))
          return; 
        this.log.append("   Match using StreamCharacterIterator\n");
        if (!tryMatchUsingCI(new StreamCharacterIterator(new StringBufferInputStream(this.toMatch))))
          return; 
        this.log.append("   Match using ReaderCharacterIterator\n");
        if (!tryMatchUsingCI(new ReaderCharacterIterator(new StringReader(this.toMatch))))
          return; 
      } 
    } catch (Exception exception) {
      this.test.fail(this.log, "Matcher threw exception: " + exception.toString());
      exception.printStackTrace();
    } catch (Error error) {
      this.test.fail(this.log, "Matcher threw fatal error \"" + error.getMessage() + "\"");
      error.printStackTrace();
    } 
  }
  
  private boolean checkResult(boolean paramBoolean) {
    if (paramBoolean == this.shouldMatch) {
      success((this.shouldMatch ? "Matched" : "Did not match") + " \"" + this.toMatch + "\", as expected:");
      return true;
    } 
    if (this.shouldMatch) {
      this.test.fail(this.log, "Did not match \"" + this.toMatch + "\", when expected to.");
    } else {
      this.test.fail(this.log, "Matched \"" + this.toMatch + "\", when not expected to.");
    } 
    return false;
  }
  
  private boolean checkParens() {
    this.log.append("   Paren count: " + this.regexp.getParenCount() + "\n");
    if (!assertEquals(this.log, "Wrong number of parens", this.parens.length, this.regexp.getParenCount()))
      return false; 
    for (byte b = 0; b < this.regexp.getParenCount(); b++) {
      this.log.append("   Paren " + b + ": " + this.regexp.getParen(b) + "\n");
      if ((!"null".equals(this.parens[b]) || this.regexp.getParen(b) != null) && !assertEquals(this.log, "Wrong register " + b, this.parens[b], this.regexp.getParen(b)))
        return false; 
    } 
    return true;
  }
  
  boolean tryMatchUsingCI(CharacterIterator paramCharacterIterator) {
    try {
      boolean bool = this.regexp.match(paramCharacterIterator, 0);
      this.log.append("   Match: " + (bool ? "YES" : "NO") + "\n");
      return (checkResult(bool) && (!this.shouldMatch || checkParens()));
    } catch (Exception exception) {
      this.test.fail(this.log, "Matcher threw exception: " + exception.toString());
      exception.printStackTrace();
    } catch (Error error) {
      this.test.fail(this.log, "Matcher threw fatal error \"" + error.getMessage() + "\"");
      error.printStackTrace();
    } 
    return false;
  }
  
  public boolean assertEquals(StringBuffer paramStringBuffer, String paramString1, String paramString2, String paramString3) {
    if ((paramString2 != null && !paramString2.equals(paramString3)) || (paramString3 != null && !paramString3.equals(paramString2))) {
      this.test.fail(paramStringBuffer, paramString1 + " (expected \"" + paramString2 + "\", actual \"" + paramString3 + "\")");
      return false;
    } 
    return true;
  }
  
  public boolean assertEquals(StringBuffer paramStringBuffer, String paramString, int paramInt1, int paramInt2) {
    if (paramInt1 != paramInt2) {
      this.test.fail(paramStringBuffer, paramString + " (expected \"" + paramInt1 + "\", actual \"" + paramInt2 + "\")");
      return false;
    } 
    return true;
  }
  
  void success(String paramString) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\RETestCase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */