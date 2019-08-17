package com.sun.jmx.snmp.IPAcl;

import java.io.IOException;

class ParserTokenManager implements ParserConstants {
  static final long[] jjbitVec0 = { 0L, 0L, -1L, -1L };
  
  static final int[] jjnextStates = { 
      18, 19, 21, 28, 29, 39, 23, 24, 26, 27, 
      41, 42, 7, 8, 10, 18, 20, 21, 44, 46, 
      13, 1, 2, 4, 37, 28, 38, 26, 27, 37, 
      28, 38, 15, 16 };
  
  public static final String[] jjstrLiteralImages = { 
      "", null, null, null, null, null, null, "access", "acl", "=", 
      "communities", "enterprise", "hosts", "{", "managers", "-", "}", "read-only", "read-write", "trap", 
      "inform", "trap-community", "inform-community", "trap-num", null, null, null, null, null, null, 
      null, null, null, null, null, null, ",", ".", "!", "/" };
  
  public static final String[] lexStateNames = { "DEFAULT" };
  
  static final long[] jjtoToken = { 1067601362817L };
  
  static final long[] jjtoSkip = { 126L };
  
  private ASCII_CharStream input_stream;
  
  private final int[] jjrounds = new int[47];
  
  private final int[] jjstateSet = new int[94];
  
  protected char curChar;
  
  int curLexState = 0;
  
  int defaultLexState = 0;
  
  int jjnewStateCnt;
  
  int jjround;
  
  int jjmatchedPos;
  
  int jjmatchedKind;
  
  private final int jjStopStringLiteralDfa_0(int paramInt, long paramLong) {
    switch (paramInt) {
      case 0:
        if ((paramLong & 0x8000L) != 0L)
          return 0; 
        if ((paramLong & 0xFE5000L) != 0L) {
          this.jjmatchedKind = 31;
          return 47;
        } 
        if ((paramLong & 0xD80L) != 0L) {
          this.jjmatchedKind = 31;
          return 48;
        } 
        return -1;
      case 1:
        if ((paramLong & 0xFE5C00L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 1;
          return 49;
        } 
        if ((paramLong & 0x180L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 1;
          return 50;
        } 
        return -1;
      case 2:
        if ((paramLong & 0xFE5C00L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 2;
          return 49;
        } 
        if ((paramLong & 0x100L) != 0L)
          return 49; 
        if ((paramLong & 0x80L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 2;
          return 50;
        } 
        return -1;
      case 3:
        if ((paramLong & 0x565C00L) != 0L) {
          if (this.jjmatchedPos != 3) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 3;
          } 
          return 49;
        } 
        if ((paramLong & 0xA80000L) != 0L)
          return 49; 
        if ((paramLong & 0x80L) != 0L) {
          if (this.jjmatchedPos != 3) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 3;
          } 
          return 50;
        } 
        return -1;
      case 4:
        if ((paramLong & 0xA00000L) != 0L)
          return 51; 
        if ((paramLong & 0x60000L) != 0L) {
          if (this.jjmatchedPos < 3) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 3;
          } 
          return 51;
        } 
        if ((paramLong & 0x1000L) != 0L)
          return 49; 
        if ((paramLong & 0x504C80L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 4;
          return 49;
        } 
        return -1;
      case 5:
        if ((paramLong & 0x500080L) != 0L)
          return 49; 
        if ((paramLong & 0x4C00L) != 0L) {
          if (this.jjmatchedPos != 5) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 5;
          } 
          return 49;
        } 
        if ((paramLong & 0xA60000L) != 0L) {
          if (this.jjmatchedPos != 5) {
            this.jjmatchedKind = 31;
            this.jjmatchedPos = 5;
          } 
          return 51;
        } 
        return -1;
      case 6:
        if ((paramLong & 0x400000L) != 0L)
          return 51; 
        if ((paramLong & 0x4C00L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 6;
          return 49;
        } 
        if ((paramLong & 0xA60000L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 6;
          return 51;
        } 
        return -1;
      case 7:
        if ((paramLong & 0x660000L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 7;
          return 51;
        } 
        if ((paramLong & 0x800000L) != 0L)
          return 51; 
        if ((paramLong & 0x4000L) != 0L)
          return 49; 
        if ((paramLong & 0xC00L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 7;
          return 49;
        } 
        return -1;
      case 8:
        if ((paramLong & 0x20000L) != 0L)
          return 51; 
        if ((paramLong & 0xC00L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 8;
          return 49;
        } 
        if ((paramLong & 0x640000L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 8;
          return 51;
        } 
        return -1;
      case 9:
        if ((paramLong & 0x40000L) != 0L)
          return 51; 
        if ((paramLong & 0x800L) != 0L)
          return 49; 
        if ((paramLong & 0x600000L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 9;
          return 51;
        } 
        if ((paramLong & 0x400L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 9;
          return 49;
        } 
        return -1;
      case 10:
        if ((paramLong & 0x600000L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 10;
          return 51;
        } 
        return ((paramLong & 0x400L) != 0L) ? 49 : -1;
      case 11:
        if ((paramLong & 0x600000L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 11;
          return 51;
        } 
        return -1;
      case 12:
        if ((paramLong & 0x600000L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 12;
          return 51;
        } 
        return -1;
      case 13:
        if ((paramLong & 0x400000L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 13;
          return 51;
        } 
        return ((paramLong & 0x200000L) != 0L) ? 51 : -1;
      case 14:
        if ((paramLong & 0x400000L) != 0L) {
          this.jjmatchedKind = 31;
          this.jjmatchedPos = 14;
          return 51;
        } 
        return -1;
    } 
    return -1;
  }
  
  private final int jjStartNfa_0(int paramInt, long paramLong) { return jjMoveNfa_0(jjStopStringLiteralDfa_0(paramInt, paramLong), paramInt + 1); }
  
  private final int jjStopAtPos(int paramInt1, int paramInt2) {
    this.jjmatchedKind = paramInt2;
    this.jjmatchedPos = paramInt1;
    return paramInt1 + 1;
  }
  
  private final int jjStartNfaWithStates_0(int paramInt1, int paramInt2, int paramInt3) {
    this.jjmatchedKind = paramInt2;
    this.jjmatchedPos = paramInt1;
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      return paramInt1 + 1;
    } 
    return jjMoveNfa_0(paramInt3, paramInt1 + 1);
  }
  
  private final int jjMoveStringLiteralDfa0_0() {
    switch (this.curChar) {
      case '!':
        return jjStopAtPos(0, 38);
      case ',':
        return jjStopAtPos(0, 36);
      case '-':
        return jjStartNfaWithStates_0(0, 15, 0);
      case '.':
        return jjStopAtPos(0, 37);
      case '/':
        return jjStopAtPos(0, 39);
      case '=':
        return jjStopAtPos(0, 9);
      case 'a':
        return jjMoveStringLiteralDfa1_0(384L);
      case 'c':
        return jjMoveStringLiteralDfa1_0(1024L);
      case 'e':
        return jjMoveStringLiteralDfa1_0(2048L);
      case 'h':
        return jjMoveStringLiteralDfa1_0(4096L);
      case 'i':
        return jjMoveStringLiteralDfa1_0(5242880L);
      case 'm':
        return jjMoveStringLiteralDfa1_0(16384L);
      case 'r':
        return jjMoveStringLiteralDfa1_0(393216L);
      case 't':
        return jjMoveStringLiteralDfa1_0(11010048L);
      case '{':
        return jjStopAtPos(0, 13);
      case '}':
        return jjStopAtPos(0, 16);
    } 
    return jjMoveNfa_0(5, 0);
  }
  
  private final int jjMoveStringLiteralDfa1_0(long paramLong) {
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(0, paramLong);
      return 1;
    } 
    switch (this.curChar) {
      case 'a':
        return jjMoveStringLiteralDfa2_0(paramLong, 16384L);
      case 'c':
        return jjMoveStringLiteralDfa2_0(paramLong, 384L);
      case 'e':
        return jjMoveStringLiteralDfa2_0(paramLong, 393216L);
      case 'n':
        return jjMoveStringLiteralDfa2_0(paramLong, 5244928L);
      case 'o':
        return jjMoveStringLiteralDfa2_0(paramLong, 5120L);
      case 'r':
        return jjMoveStringLiteralDfa2_0(paramLong, 11010048L);
    } 
    return jjStartNfa_0(0, paramLong);
  }
  
  private final int jjMoveStringLiteralDfa2_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(0, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(1, paramLong2);
      return 2;
    } 
    switch (this.curChar) {
      case 'a':
        return jjMoveStringLiteralDfa3_0(paramLong2, 11403264L);
      case 'c':
        return jjMoveStringLiteralDfa3_0(paramLong2, 128L);
      case 'f':
        return jjMoveStringLiteralDfa3_0(paramLong2, 5242880L);
      case 'l':
        if ((paramLong2 & 0x100L) != 0L)
          return jjStartNfaWithStates_0(2, 8, 49); 
        break;
      case 'm':
        return jjMoveStringLiteralDfa3_0(paramLong2, 1024L);
      case 'n':
        return jjMoveStringLiteralDfa3_0(paramLong2, 16384L);
      case 's':
        return jjMoveStringLiteralDfa3_0(paramLong2, 4096L);
      case 't':
        return jjMoveStringLiteralDfa3_0(paramLong2, 2048L);
    } 
    return jjStartNfa_0(1, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa3_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(1, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(2, paramLong2);
      return 3;
    } 
    switch (this.curChar) {
      case 'a':
        return jjMoveStringLiteralDfa4_0(paramLong2, 16384L);
      case 'd':
        return jjMoveStringLiteralDfa4_0(paramLong2, 393216L);
      case 'e':
        return jjMoveStringLiteralDfa4_0(paramLong2, 2176L);
      case 'm':
        return jjMoveStringLiteralDfa4_0(paramLong2, 1024L);
      case 'o':
        return jjMoveStringLiteralDfa4_0(paramLong2, 5242880L);
      case 'p':
        if ((paramLong2 & 0x80000L) != 0L) {
          this.jjmatchedKind = 19;
          this.jjmatchedPos = 3;
        } 
        return jjMoveStringLiteralDfa4_0(paramLong2, 10485760L);
      case 't':
        return jjMoveStringLiteralDfa4_0(paramLong2, 4096L);
    } 
    return jjStartNfa_0(2, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa4_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(2, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(3, paramLong2);
      return 4;
    } 
    switch (this.curChar) {
      case '-':
        return jjMoveStringLiteralDfa5_0(paramLong2, 10878976L);
      case 'g':
        return jjMoveStringLiteralDfa5_0(paramLong2, 16384L);
      case 'r':
        return jjMoveStringLiteralDfa5_0(paramLong2, 5244928L);
      case 's':
        return ((paramLong2 & 0x1000L) != 0L) ? jjStartNfaWithStates_0(4, 12, 49) : jjMoveStringLiteralDfa5_0(paramLong2, 128L);
      case 'u':
        return jjMoveStringLiteralDfa5_0(paramLong2, 1024L);
    } 
    return jjStartNfa_0(3, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa5_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(3, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(4, paramLong2);
      return 5;
    } 
    switch (this.curChar) {
      case 'c':
        return jjMoveStringLiteralDfa6_0(paramLong2, 2097152L);
      case 'e':
        return jjMoveStringLiteralDfa6_0(paramLong2, 16384L);
      case 'm':
        if ((paramLong2 & 0x100000L) != 0L) {
          this.jjmatchedKind = 20;
          this.jjmatchedPos = 5;
        } 
        return jjMoveStringLiteralDfa6_0(paramLong2, 4194304L);
      case 'n':
        return jjMoveStringLiteralDfa6_0(paramLong2, 8389632L);
      case 'o':
        return jjMoveStringLiteralDfa6_0(paramLong2, 131072L);
      case 'p':
        return jjMoveStringLiteralDfa6_0(paramLong2, 2048L);
      case 's':
        if ((paramLong2 & 0x80L) != 0L)
          return jjStartNfaWithStates_0(5, 7, 49); 
        break;
      case 'w':
        return jjMoveStringLiteralDfa6_0(paramLong2, 262144L);
    } 
    return jjStartNfa_0(4, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa6_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(4, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(5, paramLong2);
      return 6;
    } 
    switch (this.curChar) {
      case '-':
        return jjMoveStringLiteralDfa7_0(paramLong2, 4194304L);
      case 'i':
        return jjMoveStringLiteralDfa7_0(paramLong2, 1024L);
      case 'n':
        return jjMoveStringLiteralDfa7_0(paramLong2, 131072L);
      case 'o':
        return jjMoveStringLiteralDfa7_0(paramLong2, 2097152L);
      case 'r':
        return jjMoveStringLiteralDfa7_0(paramLong2, 280576L);
      case 'u':
        return jjMoveStringLiteralDfa7_0(paramLong2, 8388608L);
    } 
    return jjStartNfa_0(5, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa7_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(5, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(6, paramLong2);
      return 7;
    } 
    switch (this.curChar) {
      case 'c':
        return jjMoveStringLiteralDfa8_0(paramLong2, 4194304L);
      case 'i':
        return jjMoveStringLiteralDfa8_0(paramLong2, 264192L);
      case 'l':
        return jjMoveStringLiteralDfa8_0(paramLong2, 131072L);
      case 'm':
        return ((paramLong2 & 0x800000L) != 0L) ? jjStartNfaWithStates_0(7, 23, 51) : jjMoveStringLiteralDfa8_0(paramLong2, 2097152L);
      case 's':
        if ((paramLong2 & 0x4000L) != 0L)
          return jjStartNfaWithStates_0(7, 14, 49); 
        break;
      case 't':
        return jjMoveStringLiteralDfa8_0(paramLong2, 1024L);
    } 
    return jjStartNfa_0(6, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa8_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(6, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(7, paramLong2);
      return 8;
    } 
    switch (this.curChar) {
      case 'i':
        return jjMoveStringLiteralDfa9_0(paramLong2, 1024L);
      case 'm':
        return jjMoveStringLiteralDfa9_0(paramLong2, 2097152L);
      case 'o':
        return jjMoveStringLiteralDfa9_0(paramLong2, 4194304L);
      case 's':
        return jjMoveStringLiteralDfa9_0(paramLong2, 2048L);
      case 't':
        return jjMoveStringLiteralDfa9_0(paramLong2, 262144L);
      case 'y':
        if ((paramLong2 & 0x20000L) != 0L)
          return jjStartNfaWithStates_0(8, 17, 51); 
        break;
    } 
    return jjStartNfa_0(7, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa9_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(7, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(8, paramLong2);
      return 9;
    } 
    switch (this.curChar) {
      case 'e':
        return ((paramLong2 & 0x800L) != 0L) ? jjStartNfaWithStates_0(9, 11, 49) : (((paramLong2 & 0x40000L) != 0L) ? jjStartNfaWithStates_0(9, 18, 51) : jjMoveStringLiteralDfa10_0(paramLong2, 1024L));
      case 'm':
        return jjMoveStringLiteralDfa10_0(paramLong2, 4194304L);
      case 'u':
        return jjMoveStringLiteralDfa10_0(paramLong2, 2097152L);
    } 
    return jjStartNfa_0(8, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa10_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(8, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(9, paramLong2);
      return 10;
    } 
    switch (this.curChar) {
      case 'm':
        return jjMoveStringLiteralDfa11_0(paramLong2, 4194304L);
      case 'n':
        return jjMoveStringLiteralDfa11_0(paramLong2, 2097152L);
      case 's':
        if ((paramLong2 & 0x400L) != 0L)
          return jjStartNfaWithStates_0(10, 10, 49); 
        break;
    } 
    return jjStartNfa_0(9, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa11_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(9, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(10, paramLong2);
      return 11;
    } 
    switch (this.curChar) {
      case 'i':
        return jjMoveStringLiteralDfa12_0(paramLong2, 2097152L);
      case 'u':
        return jjMoveStringLiteralDfa12_0(paramLong2, 4194304L);
    } 
    return jjStartNfa_0(10, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa12_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(10, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(11, paramLong2);
      return 12;
    } 
    switch (this.curChar) {
      case 'n':
        return jjMoveStringLiteralDfa13_0(paramLong2, 4194304L);
      case 't':
        return jjMoveStringLiteralDfa13_0(paramLong2, 2097152L);
    } 
    return jjStartNfa_0(11, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa13_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(11, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(12, paramLong2);
      return 13;
    } 
    switch (this.curChar) {
      case 'i':
        return jjMoveStringLiteralDfa14_0(paramLong2, 4194304L);
      case 'y':
        if ((paramLong2 & 0x200000L) != 0L)
          return jjStartNfaWithStates_0(13, 21, 51); 
        break;
    } 
    return jjStartNfa_0(12, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa14_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(12, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(13, paramLong2);
      return 14;
    } 
    switch (this.curChar) {
      case 't':
        return jjMoveStringLiteralDfa15_0(paramLong2, 4194304L);
    } 
    return jjStartNfa_0(13, paramLong2);
  }
  
  private final int jjMoveStringLiteralDfa15_0(long paramLong1, long paramLong2) {
    if ((paramLong2 &= paramLong1) == 0L)
      return jjStartNfa_0(13, paramLong1); 
    try {
      this.curChar = this.input_stream.readChar();
    } catch (IOException iOException) {
      jjStopStringLiteralDfa_0(14, paramLong2);
      return 15;
    } 
    switch (this.curChar) {
      case 'y':
        if ((paramLong2 & 0x400000L) != 0L)
          return jjStartNfaWithStates_0(15, 22, 51); 
        break;
    } 
    return jjStartNfa_0(14, paramLong2);
  }
  
  private final void jjCheckNAdd(int paramInt) {
    if (this.jjrounds[paramInt] != this.jjround) {
      this.jjstateSet[this.jjnewStateCnt++] = paramInt;
      this.jjrounds[paramInt] = this.jjround;
    } 
  }
  
  private final void jjAddStates(int paramInt1, int paramInt2) {
    do {
      this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[paramInt1];
    } while (paramInt1++ != paramInt2);
  }
  
  private final void jjCheckNAddTwoStates(int paramInt1, int paramInt2) {
    jjCheckNAdd(paramInt1);
    jjCheckNAdd(paramInt2);
  }
  
  private final void jjCheckNAddStates(int paramInt1, int paramInt2) {
    do {
      jjCheckNAdd(jjnextStates[paramInt1]);
    } while (paramInt1++ != paramInt2);
  }
  
  private final void jjCheckNAddStates(int paramInt) {
    jjCheckNAdd(jjnextStates[paramInt]);
    jjCheckNAdd(jjnextStates[paramInt + 1]);
  }
  
  private final int jjMoveNfa_0(int paramInt1, int paramInt2) {
    int i = 0;
    this.jjnewStateCnt = 47;
    int j = 1;
    this.jjstateSet[0] = paramInt1;
    int k = Integer.MAX_VALUE;
    while (true) {
      if (++this.jjround == Integer.MAX_VALUE)
        ReInitRounds(); 
      if (this.curChar < '@') {
        long l = 1L << this.curChar;
        do {
          switch (this.jjstateSet[--j]) {
            case 49:
              if ((0x3FF200000000000L & l) != 0L)
                jjCheckNAddTwoStates(18, 19); 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAddStates(0, 2);
              } 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(20);
              } 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(19);
              } 
              break;
            case 48:
              if ((0x3FF200000000000L & l) != 0L) {
                jjCheckNAddTwoStates(18, 19);
              } else if (this.curChar == ':') {
                jjCheckNAddStates(3, 5);
              } 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAddStates(0, 2);
              } else if (this.curChar == ':') {
                jjCheckNAddTwoStates(23, 25);
              } 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(20);
              } 
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddTwoStates(26, 27); 
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddTwoStates(23, 24); 
              break;
            case 47:
              if ((0x3FF200000000000L & l) != 0L)
                jjCheckNAddTwoStates(18, 19); 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAddStates(0, 2);
              } 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(20);
              } 
              break;
            case 50:
              if ((0x3FF200000000000L & l) != 0L) {
                jjCheckNAddTwoStates(18, 19);
              } else if (this.curChar == ':') {
                jjCheckNAddStates(3, 5);
              } 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAddStates(0, 2);
              } else if (this.curChar == ':') {
                jjCheckNAddTwoStates(23, 25);
              } 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(20);
              } 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(19);
              } 
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddTwoStates(26, 27); 
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddTwoStates(23, 24); 
              break;
            case 5:
              if ((0x3FF000000000000L & l) != 0L) {
                jjCheckNAddStates(6, 9);
              } else if (this.curChar == ':') {
                jjAddStates(10, 11);
              } else if (this.curChar == '"') {
                jjCheckNAddTwoStates(15, 16);
              } else if (this.curChar == '#') {
                jjCheckNAddStates(12, 14);
              } else if (this.curChar == '-') {
                this.jjstateSet[this.jjnewStateCnt++] = 0;
              } 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAddStates(15, 17);
              } 
              if ((0x3FE000000000000L & l) != 0L) {
                if (k > 24)
                  k = 24; 
                jjCheckNAddTwoStates(12, 13);
                break;
              } 
              if (this.curChar == '0') {
                if (k > 24)
                  k = 24; 
                jjCheckNAddStates(18, 20);
              } 
              break;
            case 51:
              if ((0x3FF200000000000L & l) != 0L)
                jjCheckNAddTwoStates(18, 19); 
              if ((0x3FF000000000000L & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(19);
              } 
              break;
            case 0:
              if (this.curChar == '-')
                jjCheckNAddStates(21, 23); 
              break;
            case 1:
              if ((0xFFFFFFFFFFFFDBFFL & l) != 0L)
                jjCheckNAddStates(21, 23); 
              break;
            case 2:
              if ((0x2400L & l) != 0L && k > 5)
                k = 5; 
              break;
            case 3:
              if (this.curChar == '\n' && k > 5)
                k = 5; 
              break;
            case 4:
              if (this.curChar == '\r')
                this.jjstateSet[this.jjnewStateCnt++] = 3; 
              break;
            case 6:
              if (this.curChar == '#')
                jjCheckNAddStates(12, 14); 
              break;
            case 7:
              if ((0xFFFFFFFFFFFFDBFFL & l) != 0L)
                jjCheckNAddStates(12, 14); 
              break;
            case 8:
              if ((0x2400L & l) != 0L && k > 6)
                k = 6; 
              break;
            case 9:
              if (this.curChar == '\n' && k > 6)
                k = 6; 
              break;
            case 10:
              if (this.curChar == '\r')
                this.jjstateSet[this.jjnewStateCnt++] = 9; 
              break;
            case 11:
              if ((0x3FE000000000000L & l) == 0L)
                break; 
              if (k > 24)
                k = 24; 
              jjCheckNAddTwoStates(12, 13);
              break;
            case 12:
              if ((0x3FF000000000000L & l) == 0L)
                break; 
              if (k > 24)
                k = 24; 
              jjCheckNAddTwoStates(12, 13);
              break;
            case 14:
              if (this.curChar == '"')
                jjCheckNAddTwoStates(15, 16); 
              break;
            case 15:
              if ((0xFFFFFFFBFFFFFFFFL & l) != 0L)
                jjCheckNAddTwoStates(15, 16); 
              break;
            case 16:
              if (this.curChar == '"' && k > 35)
                k = 35; 
              break;
            case 17:
              if ((0x3FF000000000000L & l) == 0L)
                break; 
              if (k > 31)
                k = 31; 
              jjCheckNAddStates(15, 17);
              break;
            case 18:
              if ((0x3FF200000000000L & l) != 0L)
                jjCheckNAddTwoStates(18, 19); 
              break;
            case 19:
              if ((0x3FF000000000000L & l) == 0L)
                break; 
              if (k > 31)
                k = 31; 
              jjCheckNAdd(19);
              break;
            case 20:
              if ((0x3FF000000000000L & l) == 0L)
                break; 
              if (k > 31)
                k = 31; 
              jjCheckNAdd(20);
              break;
            case 21:
              if ((0x3FF000000000000L & l) == 0L)
                break; 
              if (k > 31)
                k = 31; 
              jjCheckNAddStates(0, 2);
              break;
            case 22:
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddStates(6, 9); 
              break;
            case 23:
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddTwoStates(23, 24); 
              break;
            case 24:
              if (this.curChar == ':')
                jjCheckNAddTwoStates(23, 25); 
              break;
            case 25:
            case 41:
              if (this.curChar == ':' && k > 28)
                k = 28; 
              break;
            case 26:
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddTwoStates(26, 27); 
              break;
            case 27:
              if (this.curChar == ':')
                jjCheckNAddStates(3, 5); 
              break;
            case 28:
            case 42:
              if (this.curChar == ':')
                jjCheckNAddTwoStates(29, 36); 
              break;
            case 29:
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddTwoStates(29, 30); 
              break;
            case 30:
              if (this.curChar == '.')
                jjCheckNAdd(31); 
              break;
            case 31:
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddTwoStates(31, 32); 
              break;
            case 32:
              if (this.curChar == '.')
                jjCheckNAdd(33); 
              break;
            case 33:
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddTwoStates(33, 34); 
              break;
            case 34:
              if (this.curChar == '.')
                jjCheckNAdd(35); 
              break;
            case 35:
              if ((0x3FF000000000000L & l) == 0L)
                break; 
              if (k > 28)
                k = 28; 
              jjCheckNAdd(35);
              break;
            case 36:
              if ((0x3FF000000000000L & l) == 0L)
                break; 
              if (k > 28)
                k = 28; 
              jjCheckNAddStates(24, 26);
              break;
            case 37:
              if ((0x3FF000000000000L & l) != 0L)
                jjCheckNAddTwoStates(37, 28); 
              break;
            case 38:
              if ((0x3FF000000000000L & l) == 0L)
                break; 
              if (k > 28)
                k = 28; 
              jjCheckNAdd(38);
              break;
            case 39:
              if ((0x3FF000000000000L & l) == 0L)
                break; 
              if (k > 28)
                k = 28; 
              jjCheckNAddStates(27, 31);
              break;
            case 40:
              if (this.curChar == ':')
                jjAddStates(10, 11); 
              break;
            case 43:
              if (this.curChar != '0')
                break; 
              if (k > 24)
                k = 24; 
              jjCheckNAddStates(18, 20);
              break;
            case 45:
              if ((0x3FF000000000000L & l) == 0L)
                break; 
              if (k > 24)
                k = 24; 
              jjCheckNAddTwoStates(45, 13);
              break;
            case 46:
              if ((0xFF000000000000L & l) == 0L)
                break; 
              if (k > 24)
                k = 24; 
              jjCheckNAddTwoStates(46, 13);
              break;
          } 
        } while (j != i);
      } else if (this.curChar < '') {
        long l = 1L << (this.curChar & 0x3F);
        do {
          switch (this.jjstateSet[--j]) {
            case 49:
              if ((0x7FFFFFE87FFFFFEL & l) != 0L)
                jjCheckNAddTwoStates(18, 19); 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAddStates(0, 2);
              } 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(20);
              } 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(19);
              } 
              break;
            case 48:
              if ((0x7FFFFFE87FFFFFEL & l) != 0L)
                jjCheckNAddTwoStates(18, 19); 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAddStates(0, 2);
              } 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(20);
              } 
              if ((0x7E0000007EL & l) != 0L)
                jjCheckNAddTwoStates(26, 27); 
              if ((0x7E0000007EL & l) != 0L)
                jjCheckNAddTwoStates(23, 24); 
              break;
            case 47:
              if ((0x7FFFFFE87FFFFFEL & l) != 0L)
                jjCheckNAddTwoStates(18, 19); 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAddStates(0, 2);
              } 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(20);
              } 
              break;
            case 50:
              if ((0x7FFFFFE87FFFFFEL & l) != 0L)
                jjCheckNAddTwoStates(18, 19); 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAddStates(0, 2);
              } 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(20);
              } 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(19);
              } 
              if ((0x7E0000007EL & l) != 0L)
                jjCheckNAddTwoStates(26, 27); 
              if ((0x7E0000007EL & l) != 0L)
                jjCheckNAddTwoStates(23, 24); 
              break;
            case 5:
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAddStates(15, 17);
              } 
              if ((0x7E0000007EL & l) != 0L)
                jjCheckNAddStates(6, 9); 
              break;
            case 51:
              if ((0x7FFFFFE87FFFFFEL & l) != 0L)
                jjCheckNAddTwoStates(18, 19); 
              if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                if (k > 31)
                  k = 31; 
                jjCheckNAdd(19);
              } 
              break;
            case 1:
              jjAddStates(21, 23);
              break;
            case 7:
              jjAddStates(12, 14);
              break;
            case 13:
              if ((0x100000001000L & l) != 0L && k > 24)
                k = 24; 
              break;
            case 15:
              jjAddStates(32, 33);
              break;
            case 17:
              if ((0x7FFFFFE07FFFFFEL & l) == 0L)
                break; 
              if (k > 31)
                k = 31; 
              jjCheckNAddStates(15, 17);
              break;
            case 18:
              if ((0x7FFFFFE87FFFFFEL & l) != 0L)
                jjCheckNAddTwoStates(18, 19); 
              break;
            case 19:
              if ((0x7FFFFFE07FFFFFEL & l) == 0L)
                break; 
              if (k > 31)
                k = 31; 
              jjCheckNAdd(19);
              break;
            case 20:
              if ((0x7FFFFFE07FFFFFEL & l) == 0L)
                break; 
              if (k > 31)
                k = 31; 
              jjCheckNAdd(20);
              break;
            case 21:
              if ((0x7FFFFFE07FFFFFEL & l) == 0L)
                break; 
              if (k > 31)
                k = 31; 
              jjCheckNAddStates(0, 2);
              break;
            case 22:
              if ((0x7E0000007EL & l) != 0L)
                jjCheckNAddStates(6, 9); 
              break;
            case 23:
              if ((0x7E0000007EL & l) != 0L)
                jjCheckNAddTwoStates(23, 24); 
              break;
            case 26:
              if ((0x7E0000007EL & l) != 0L)
                jjCheckNAddTwoStates(26, 27); 
              break;
            case 36:
              if ((0x7E0000007EL & l) == 0L)
                break; 
              if (k > 28)
                k = 28; 
              jjCheckNAddStates(24, 26);
              break;
            case 37:
              if ((0x7E0000007EL & l) != 0L)
                jjCheckNAddTwoStates(37, 28); 
              break;
            case 38:
              if ((0x7E0000007EL & l) == 0L)
                break; 
              if (k > 28)
                k = 28; 
              jjCheckNAdd(38);
              break;
            case 39:
              if ((0x7E0000007EL & l) == 0L)
                break; 
              if (k > 28)
                k = 28; 
              jjCheckNAddStates(27, 31);
              break;
            case 44:
              if ((0x100000001000000L & l) != 0L)
                jjCheckNAdd(45); 
              break;
            case 45:
              if ((0x7E0000007EL & l) == 0L)
                break; 
              if (k > 24)
                k = 24; 
              jjCheckNAddTwoStates(45, 13);
              break;
          } 
        } while (j != i);
      } else {
        char c = (this.curChar & 0xFF) >> '\006';
        long l = 1L << (this.curChar & 0x3F);
        do {
          switch (this.jjstateSet[--j]) {
            case 1:
              if ((jjbitVec0[c] & l) != 0L)
                jjAddStates(21, 23); 
              break;
            case 7:
              if ((jjbitVec0[c] & l) != 0L)
                jjAddStates(12, 14); 
              break;
            case 15:
              if ((jjbitVec0[c] & l) != 0L)
                jjAddStates(32, 33); 
              break;
          } 
        } while (j != i);
      } 
      if (k != Integer.MAX_VALUE) {
        this.jjmatchedKind = k;
        this.jjmatchedPos = paramInt2;
        k = Integer.MAX_VALUE;
      } 
      paramInt2++;
      if ((j = this.jjnewStateCnt) == (i = 47 - (this.jjnewStateCnt = i)))
        return paramInt2; 
      try {
        this.curChar = this.input_stream.readChar();
      } catch (IOException iOException) {
        return paramInt2;
      } 
    } 
  }
  
  public ParserTokenManager(ASCII_CharStream paramASCII_CharStream) { this.input_stream = paramASCII_CharStream; }
  
  public ParserTokenManager(ASCII_CharStream paramASCII_CharStream, int paramInt) {
    this(paramASCII_CharStream);
    SwitchTo(paramInt);
  }
  
  public void ReInit(ASCII_CharStream paramASCII_CharStream) {
    this.jjmatchedPos = this.jjnewStateCnt = 0;
    this.curLexState = this.defaultLexState;
    this.input_stream = paramASCII_CharStream;
    ReInitRounds();
  }
  
  private final void ReInitRounds() {
    this.jjround = -2147483647;
    byte b = 47;
    while (b-- > 0)
      this.jjrounds[b] = Integer.MIN_VALUE; 
  }
  
  public void ReInit(ASCII_CharStream paramASCII_CharStream, int paramInt) {
    ReInit(paramASCII_CharStream);
    SwitchTo(paramInt);
  }
  
  public void SwitchTo(int paramInt) {
    if (paramInt >= 1 || paramInt < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + paramInt + ". State unchanged.", 2); 
    this.curLexState = paramInt;
  }
  
  private final Token jjFillToken() {
    Token token = Token.newToken(this.jjmatchedKind);
    token.kind = this.jjmatchedKind;
    String str = jjstrLiteralImages[this.jjmatchedKind];
    token.image = (str == null) ? this.input_stream.GetImage() : str;
    token.beginLine = this.input_stream.getBeginLine();
    token.beginColumn = this.input_stream.getBeginColumn();
    token.endLine = this.input_stream.getEndLine();
    token.endColumn = this.input_stream.getEndColumn();
    return token;
  }
  
  public final Token getNextToken() {
    Object object = null;
    int i = 0;
    while (true) {
      try {
        this.curChar = this.input_stream.BeginToken();
      } catch (IOException iOException) {
        this.jjmatchedKind = 0;
        return jjFillToken();
      } 
      try {
        this.input_stream.backup(0);
        while (this.curChar <= ' ' && (0x100002600L & 1L << this.curChar) != 0L)
          this.curChar = this.input_stream.BeginToken(); 
      } catch (IOException iOException) {
        continue;
      } 
      this.jjmatchedKind = Integer.MAX_VALUE;
      this.jjmatchedPos = 0;
      i = jjMoveStringLiteralDfa0_0();
      if (this.jjmatchedKind != Integer.MAX_VALUE) {
        if (this.jjmatchedPos + 1 < i)
          this.input_stream.backup(i - this.jjmatchedPos - 1); 
        if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L)
          return jjFillToken(); 
        continue;
      } 
      break;
    } 
    int j = this.input_stream.getEndLine();
    int k = this.input_stream.getEndColumn();
    String str = null;
    boolean bool = false;
    try {
      this.input_stream.readChar();
      this.input_stream.backup(1);
    } catch (IOException iOException) {
      bool = true;
      str = (i <= 1) ? "" : this.input_stream.GetImage();
      if (this.curChar == '\n' || this.curChar == '\r') {
        j++;
        k = 0;
      } else {
        k++;
      } 
    } 
    if (!bool) {
      this.input_stream.backup(1);
      str = (i <= 1) ? "" : this.input_stream.GetImage();
    } 
    throw new TokenMgrError(bool, this.curLexState, j, k, str, this.curChar, 0);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\ParserTokenManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */