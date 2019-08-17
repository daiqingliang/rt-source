package java.util.regex;

import java.util.HashMap;
import java.util.Locale;

static final abstract enum UnicodeProp {
  ALPHABETIC, LETTER, IDEOGRAPHIC, LOWERCASE, UPPERCASE, TITLECASE, WHITE_SPACE, CONTROL, PUNCTUATION, HEX_DIGIT, ASSIGNED, NONCHARACTER_CODE_POINT, DIGIT, ALNUM, BLANK, GRAPH, PRINT, WORD, JOIN_CONTROL;
  
  private static final HashMap<String, String> posix;
  
  private static final HashMap<String, String> aliases;
  
  public static UnicodeProp forName(String paramString) {
    paramString = paramString.toUpperCase(Locale.ENGLISH);
    String str = (String)aliases.get(paramString);
    if (str != null)
      paramString = str; 
    try {
      return valueOf(paramString);
    } catch (IllegalArgumentException illegalArgumentException) {
      return null;
    } 
  }
  
  public static UnicodeProp forPOSIXName(String paramString) {
    paramString = (String)posix.get(paramString.toUpperCase(Locale.ENGLISH));
    return (paramString == null) ? null : valueOf(paramString);
  }
  
  public abstract boolean is(int paramInt);
  
  static  {
    // Byte code:
    //   0: new java/util/regex/UnicodeProp$1
    //   3: dup
    //   4: ldc 'ALPHABETIC'
    //   6: iconst_0
    //   7: invokespecial <init> : (Ljava/lang/String;I)V
    //   10: putstatic java/util/regex/UnicodeProp.ALPHABETIC : Ljava/util/regex/UnicodeProp;
    //   13: new java/util/regex/UnicodeProp$2
    //   16: dup
    //   17: ldc 'LETTER'
    //   19: iconst_1
    //   20: invokespecial <init> : (Ljava/lang/String;I)V
    //   23: putstatic java/util/regex/UnicodeProp.LETTER : Ljava/util/regex/UnicodeProp;
    //   26: new java/util/regex/UnicodeProp$3
    //   29: dup
    //   30: ldc 'IDEOGRAPHIC'
    //   32: iconst_2
    //   33: invokespecial <init> : (Ljava/lang/String;I)V
    //   36: putstatic java/util/regex/UnicodeProp.IDEOGRAPHIC : Ljava/util/regex/UnicodeProp;
    //   39: new java/util/regex/UnicodeProp$4
    //   42: dup
    //   43: ldc 'LOWERCASE'
    //   45: iconst_3
    //   46: invokespecial <init> : (Ljava/lang/String;I)V
    //   49: putstatic java/util/regex/UnicodeProp.LOWERCASE : Ljava/util/regex/UnicodeProp;
    //   52: new java/util/regex/UnicodeProp$5
    //   55: dup
    //   56: ldc 'UPPERCASE'
    //   58: iconst_4
    //   59: invokespecial <init> : (Ljava/lang/String;I)V
    //   62: putstatic java/util/regex/UnicodeProp.UPPERCASE : Ljava/util/regex/UnicodeProp;
    //   65: new java/util/regex/UnicodeProp$6
    //   68: dup
    //   69: ldc 'TITLECASE'
    //   71: iconst_5
    //   72: invokespecial <init> : (Ljava/lang/String;I)V
    //   75: putstatic java/util/regex/UnicodeProp.TITLECASE : Ljava/util/regex/UnicodeProp;
    //   78: new java/util/regex/UnicodeProp$7
    //   81: dup
    //   82: ldc 'WHITE_SPACE'
    //   84: bipush #6
    //   86: invokespecial <init> : (Ljava/lang/String;I)V
    //   89: putstatic java/util/regex/UnicodeProp.WHITE_SPACE : Ljava/util/regex/UnicodeProp;
    //   92: new java/util/regex/UnicodeProp$8
    //   95: dup
    //   96: ldc 'CONTROL'
    //   98: bipush #7
    //   100: invokespecial <init> : (Ljava/lang/String;I)V
    //   103: putstatic java/util/regex/UnicodeProp.CONTROL : Ljava/util/regex/UnicodeProp;
    //   106: new java/util/regex/UnicodeProp$9
    //   109: dup
    //   110: ldc 'PUNCTUATION'
    //   112: bipush #8
    //   114: invokespecial <init> : (Ljava/lang/String;I)V
    //   117: putstatic java/util/regex/UnicodeProp.PUNCTUATION : Ljava/util/regex/UnicodeProp;
    //   120: new java/util/regex/UnicodeProp$10
    //   123: dup
    //   124: ldc 'HEX_DIGIT'
    //   126: bipush #9
    //   128: invokespecial <init> : (Ljava/lang/String;I)V
    //   131: putstatic java/util/regex/UnicodeProp.HEX_DIGIT : Ljava/util/regex/UnicodeProp;
    //   134: new java/util/regex/UnicodeProp$11
    //   137: dup
    //   138: ldc 'ASSIGNED'
    //   140: bipush #10
    //   142: invokespecial <init> : (Ljava/lang/String;I)V
    //   145: putstatic java/util/regex/UnicodeProp.ASSIGNED : Ljava/util/regex/UnicodeProp;
    //   148: new java/util/regex/UnicodeProp$12
    //   151: dup
    //   152: ldc 'NONCHARACTER_CODE_POINT'
    //   154: bipush #11
    //   156: invokespecial <init> : (Ljava/lang/String;I)V
    //   159: putstatic java/util/regex/UnicodeProp.NONCHARACTER_CODE_POINT : Ljava/util/regex/UnicodeProp;
    //   162: new java/util/regex/UnicodeProp$13
    //   165: dup
    //   166: ldc 'DIGIT'
    //   168: bipush #12
    //   170: invokespecial <init> : (Ljava/lang/String;I)V
    //   173: putstatic java/util/regex/UnicodeProp.DIGIT : Ljava/util/regex/UnicodeProp;
    //   176: new java/util/regex/UnicodeProp$14
    //   179: dup
    //   180: ldc 'ALNUM'
    //   182: bipush #13
    //   184: invokespecial <init> : (Ljava/lang/String;I)V
    //   187: putstatic java/util/regex/UnicodeProp.ALNUM : Ljava/util/regex/UnicodeProp;
    //   190: new java/util/regex/UnicodeProp$15
    //   193: dup
    //   194: ldc 'BLANK'
    //   196: bipush #14
    //   198: invokespecial <init> : (Ljava/lang/String;I)V
    //   201: putstatic java/util/regex/UnicodeProp.BLANK : Ljava/util/regex/UnicodeProp;
    //   204: new java/util/regex/UnicodeProp$16
    //   207: dup
    //   208: ldc 'GRAPH'
    //   210: bipush #15
    //   212: invokespecial <init> : (Ljava/lang/String;I)V
    //   215: putstatic java/util/regex/UnicodeProp.GRAPH : Ljava/util/regex/UnicodeProp;
    //   218: new java/util/regex/UnicodeProp$17
    //   221: dup
    //   222: ldc 'PRINT'
    //   224: bipush #16
    //   226: invokespecial <init> : (Ljava/lang/String;I)V
    //   229: putstatic java/util/regex/UnicodeProp.PRINT : Ljava/util/regex/UnicodeProp;
    //   232: new java/util/regex/UnicodeProp$18
    //   235: dup
    //   236: ldc 'WORD'
    //   238: bipush #17
    //   240: invokespecial <init> : (Ljava/lang/String;I)V
    //   243: putstatic java/util/regex/UnicodeProp.WORD : Ljava/util/regex/UnicodeProp;
    //   246: new java/util/regex/UnicodeProp$19
    //   249: dup
    //   250: ldc 'JOIN_CONTROL'
    //   252: bipush #18
    //   254: invokespecial <init> : (Ljava/lang/String;I)V
    //   257: putstatic java/util/regex/UnicodeProp.JOIN_CONTROL : Ljava/util/regex/UnicodeProp;
    //   260: bipush #19
    //   262: anewarray java/util/regex/UnicodeProp
    //   265: dup
    //   266: iconst_0
    //   267: getstatic java/util/regex/UnicodeProp.ALPHABETIC : Ljava/util/regex/UnicodeProp;
    //   270: aastore
    //   271: dup
    //   272: iconst_1
    //   273: getstatic java/util/regex/UnicodeProp.LETTER : Ljava/util/regex/UnicodeProp;
    //   276: aastore
    //   277: dup
    //   278: iconst_2
    //   279: getstatic java/util/regex/UnicodeProp.IDEOGRAPHIC : Ljava/util/regex/UnicodeProp;
    //   282: aastore
    //   283: dup
    //   284: iconst_3
    //   285: getstatic java/util/regex/UnicodeProp.LOWERCASE : Ljava/util/regex/UnicodeProp;
    //   288: aastore
    //   289: dup
    //   290: iconst_4
    //   291: getstatic java/util/regex/UnicodeProp.UPPERCASE : Ljava/util/regex/UnicodeProp;
    //   294: aastore
    //   295: dup
    //   296: iconst_5
    //   297: getstatic java/util/regex/UnicodeProp.TITLECASE : Ljava/util/regex/UnicodeProp;
    //   300: aastore
    //   301: dup
    //   302: bipush #6
    //   304: getstatic java/util/regex/UnicodeProp.WHITE_SPACE : Ljava/util/regex/UnicodeProp;
    //   307: aastore
    //   308: dup
    //   309: bipush #7
    //   311: getstatic java/util/regex/UnicodeProp.CONTROL : Ljava/util/regex/UnicodeProp;
    //   314: aastore
    //   315: dup
    //   316: bipush #8
    //   318: getstatic java/util/regex/UnicodeProp.PUNCTUATION : Ljava/util/regex/UnicodeProp;
    //   321: aastore
    //   322: dup
    //   323: bipush #9
    //   325: getstatic java/util/regex/UnicodeProp.HEX_DIGIT : Ljava/util/regex/UnicodeProp;
    //   328: aastore
    //   329: dup
    //   330: bipush #10
    //   332: getstatic java/util/regex/UnicodeProp.ASSIGNED : Ljava/util/regex/UnicodeProp;
    //   335: aastore
    //   336: dup
    //   337: bipush #11
    //   339: getstatic java/util/regex/UnicodeProp.NONCHARACTER_CODE_POINT : Ljava/util/regex/UnicodeProp;
    //   342: aastore
    //   343: dup
    //   344: bipush #12
    //   346: getstatic java/util/regex/UnicodeProp.DIGIT : Ljava/util/regex/UnicodeProp;
    //   349: aastore
    //   350: dup
    //   351: bipush #13
    //   353: getstatic java/util/regex/UnicodeProp.ALNUM : Ljava/util/regex/UnicodeProp;
    //   356: aastore
    //   357: dup
    //   358: bipush #14
    //   360: getstatic java/util/regex/UnicodeProp.BLANK : Ljava/util/regex/UnicodeProp;
    //   363: aastore
    //   364: dup
    //   365: bipush #15
    //   367: getstatic java/util/regex/UnicodeProp.GRAPH : Ljava/util/regex/UnicodeProp;
    //   370: aastore
    //   371: dup
    //   372: bipush #16
    //   374: getstatic java/util/regex/UnicodeProp.PRINT : Ljava/util/regex/UnicodeProp;
    //   377: aastore
    //   378: dup
    //   379: bipush #17
    //   381: getstatic java/util/regex/UnicodeProp.WORD : Ljava/util/regex/UnicodeProp;
    //   384: aastore
    //   385: dup
    //   386: bipush #18
    //   388: getstatic java/util/regex/UnicodeProp.JOIN_CONTROL : Ljava/util/regex/UnicodeProp;
    //   391: aastore
    //   392: putstatic java/util/regex/UnicodeProp.$VALUES : [Ljava/util/regex/UnicodeProp;
    //   395: new java/util/HashMap
    //   398: dup
    //   399: invokespecial <init> : ()V
    //   402: putstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   405: new java/util/HashMap
    //   408: dup
    //   409: invokespecial <init> : ()V
    //   412: putstatic java/util/regex/UnicodeProp.aliases : Ljava/util/HashMap;
    //   415: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   418: ldc 'ALPHA'
    //   420: ldc 'ALPHABETIC'
    //   422: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   425: pop
    //   426: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   429: ldc 'LOWER'
    //   431: ldc 'LOWERCASE'
    //   433: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   436: pop
    //   437: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   440: ldc 'UPPER'
    //   442: ldc 'UPPERCASE'
    //   444: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   447: pop
    //   448: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   451: ldc 'SPACE'
    //   453: ldc 'WHITE_SPACE'
    //   455: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   458: pop
    //   459: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   462: ldc 'PUNCT'
    //   464: ldc 'PUNCTUATION'
    //   466: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   469: pop
    //   470: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   473: ldc 'XDIGIT'
    //   475: ldc 'HEX_DIGIT'
    //   477: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   480: pop
    //   481: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   484: ldc 'ALNUM'
    //   486: ldc 'ALNUM'
    //   488: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   491: pop
    //   492: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   495: ldc 'CNTRL'
    //   497: ldc 'CONTROL'
    //   499: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   502: pop
    //   503: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   506: ldc 'DIGIT'
    //   508: ldc 'DIGIT'
    //   510: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   513: pop
    //   514: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   517: ldc 'BLANK'
    //   519: ldc 'BLANK'
    //   521: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   524: pop
    //   525: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   528: ldc 'GRAPH'
    //   530: ldc 'GRAPH'
    //   532: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   535: pop
    //   536: getstatic java/util/regex/UnicodeProp.posix : Ljava/util/HashMap;
    //   539: ldc 'PRINT'
    //   541: ldc 'PRINT'
    //   543: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   546: pop
    //   547: getstatic java/util/regex/UnicodeProp.aliases : Ljava/util/HashMap;
    //   550: ldc 'WHITESPACE'
    //   552: ldc 'WHITE_SPACE'
    //   554: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   557: pop
    //   558: getstatic java/util/regex/UnicodeProp.aliases : Ljava/util/HashMap;
    //   561: ldc 'HEXDIGIT'
    //   563: ldc 'HEX_DIGIT'
    //   565: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   568: pop
    //   569: getstatic java/util/regex/UnicodeProp.aliases : Ljava/util/HashMap;
    //   572: ldc 'NONCHARACTERCODEPOINT'
    //   574: ldc 'NONCHARACTER_CODE_POINT'
    //   576: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   579: pop
    //   580: getstatic java/util/regex/UnicodeProp.aliases : Ljava/util/HashMap;
    //   583: ldc 'JOINCONTROL'
    //   585: ldc 'JOIN_CONTROL'
    //   587: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   590: pop
    //   591: return
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\regex\UnicodeProp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */