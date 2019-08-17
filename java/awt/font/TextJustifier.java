package java.awt.font;

class TextJustifier {
  private GlyphJustificationInfo[] info;
  
  private int start;
  
  private int limit;
  
  static boolean DEBUG = false;
  
  public static final int MAX_PRIORITY = 3;
  
  TextJustifier(GlyphJustificationInfo[] paramArrayOfGlyphJustificationInfo, int paramInt1, int paramInt2) {
    this.info = paramArrayOfGlyphJustificationInfo;
    this.start = paramInt1;
    this.limit = paramInt2;
    if (DEBUG) {
      System.out.println("start: " + paramInt1 + ", limit: " + paramInt2);
      for (int i = paramInt1; i < paramInt2; i++) {
        GlyphJustificationInfo glyphJustificationInfo = paramArrayOfGlyphJustificationInfo[i];
        System.out.println("w: " + glyphJustificationInfo.weight + ", gp: " + glyphJustificationInfo.growPriority + ", gll: " + glyphJustificationInfo.growLeftLimit + ", grl: " + glyphJustificationInfo.growRightLimit);
      } 
    } 
  }
  
  public float[] justify(float paramFloat) { // Byte code:
    //   0: aload_0
    //   1: getfield info : [Ljava/awt/font/GlyphJustificationInfo;
    //   4: arraylength
    //   5: iconst_2
    //   6: imul
    //   7: newarray float
    //   9: astore_2
    //   10: fload_1
    //   11: fconst_0
    //   12: fcmpl
    //   13: ifle -> 20
    //   16: iconst_1
    //   17: goto -> 21
    //   20: iconst_0
    //   21: istore_3
    //   22: getstatic java/awt/font/TextJustifier.DEBUG : Z
    //   25: ifeq -> 53
    //   28: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   31: new java/lang/StringBuilder
    //   34: dup
    //   35: invokespecial <init> : ()V
    //   38: ldc 'delta: '
    //   40: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   43: fload_1
    //   44: invokevirtual append : (F)Ljava/lang/StringBuilder;
    //   47: invokevirtual toString : ()Ljava/lang/String;
    //   50: invokevirtual println : (Ljava/lang/String;)V
    //   53: iconst_m1
    //   54: istore #4
    //   56: iconst_0
    //   57: istore #5
    //   59: fload_1
    //   60: fconst_0
    //   61: fcmpl
    //   62: ifeq -> 791
    //   65: iload #5
    //   67: iconst_3
    //   68: if_icmple -> 75
    //   71: iconst_1
    //   72: goto -> 76
    //   75: iconst_0
    //   76: istore #6
    //   78: iload #6
    //   80: ifeq -> 87
    //   83: iload #4
    //   85: istore #5
    //   87: fconst_0
    //   88: fstore #7
    //   90: fconst_0
    //   91: fstore #8
    //   93: fconst_0
    //   94: fstore #9
    //   96: aload_0
    //   97: getfield start : I
    //   100: istore #10
    //   102: iload #10
    //   104: aload_0
    //   105: getfield limit : I
    //   108: if_icmpge -> 324
    //   111: aload_0
    //   112: getfield info : [Ljava/awt/font/GlyphJustificationInfo;
    //   115: iload #10
    //   117: aaload
    //   118: astore #11
    //   120: iload_3
    //   121: ifeq -> 132
    //   124: aload #11
    //   126: getfield growPriority : I
    //   129: goto -> 137
    //   132: aload #11
    //   134: getfield shrinkPriority : I
    //   137: iload #5
    //   139: if_icmpne -> 318
    //   142: iload #4
    //   144: iconst_m1
    //   145: if_icmpne -> 152
    //   148: iload #5
    //   150: istore #4
    //   152: iload #10
    //   154: aload_0
    //   155: getfield start : I
    //   158: if_icmpeq -> 234
    //   161: fload #7
    //   163: aload #11
    //   165: getfield weight : F
    //   168: fadd
    //   169: fstore #7
    //   171: iload_3
    //   172: ifeq -> 206
    //   175: fload #8
    //   177: aload #11
    //   179: getfield growLeftLimit : F
    //   182: fadd
    //   183: fstore #8
    //   185: aload #11
    //   187: getfield growAbsorb : Z
    //   190: ifeq -> 234
    //   193: fload #9
    //   195: aload #11
    //   197: getfield weight : F
    //   200: fadd
    //   201: fstore #9
    //   203: goto -> 234
    //   206: fload #8
    //   208: aload #11
    //   210: getfield shrinkLeftLimit : F
    //   213: fadd
    //   214: fstore #8
    //   216: aload #11
    //   218: getfield shrinkAbsorb : Z
    //   221: ifeq -> 234
    //   224: fload #9
    //   226: aload #11
    //   228: getfield weight : F
    //   231: fadd
    //   232: fstore #9
    //   234: iload #10
    //   236: iconst_1
    //   237: iadd
    //   238: aload_0
    //   239: getfield limit : I
    //   242: if_icmpeq -> 318
    //   245: fload #7
    //   247: aload #11
    //   249: getfield weight : F
    //   252: fadd
    //   253: fstore #7
    //   255: iload_3
    //   256: ifeq -> 290
    //   259: fload #8
    //   261: aload #11
    //   263: getfield growRightLimit : F
    //   266: fadd
    //   267: fstore #8
    //   269: aload #11
    //   271: getfield growAbsorb : Z
    //   274: ifeq -> 318
    //   277: fload #9
    //   279: aload #11
    //   281: getfield weight : F
    //   284: fadd
    //   285: fstore #9
    //   287: goto -> 318
    //   290: fload #8
    //   292: aload #11
    //   294: getfield shrinkRightLimit : F
    //   297: fadd
    //   298: fstore #8
    //   300: aload #11
    //   302: getfield shrinkAbsorb : Z
    //   305: ifeq -> 318
    //   308: fload #9
    //   310: aload #11
    //   312: getfield weight : F
    //   315: fadd
    //   316: fstore #9
    //   318: iinc #10, 1
    //   321: goto -> 102
    //   324: iload_3
    //   325: ifne -> 333
    //   328: fload #8
    //   330: fneg
    //   331: fstore #8
    //   333: fload #7
    //   335: fconst_0
    //   336: fcmpl
    //   337: ifeq -> 371
    //   340: iload #6
    //   342: ifne -> 375
    //   345: fload_1
    //   346: fconst_0
    //   347: fcmpg
    //   348: ifge -> 355
    //   351: iconst_1
    //   352: goto -> 356
    //   355: iconst_0
    //   356: fload_1
    //   357: fload #8
    //   359: fcmpg
    //   360: ifge -> 367
    //   363: iconst_1
    //   364: goto -> 368
    //   367: iconst_0
    //   368: if_icmpne -> 375
    //   371: iconst_1
    //   372: goto -> 376
    //   375: iconst_0
    //   376: istore #10
    //   378: iload #10
    //   380: ifeq -> 394
    //   383: fload #9
    //   385: fconst_0
    //   386: fcmpl
    //   387: ifle -> 394
    //   390: iconst_1
    //   391: goto -> 395
    //   394: iconst_0
    //   395: istore #11
    //   397: fload_1
    //   398: fload #7
    //   400: fdiv
    //   401: fstore #12
    //   403: fconst_0
    //   404: fstore #13
    //   406: iload #10
    //   408: ifeq -> 427
    //   411: fload #9
    //   413: fconst_0
    //   414: fcmpl
    //   415: ifle -> 427
    //   418: fload_1
    //   419: fload #8
    //   421: fsub
    //   422: fload #9
    //   424: fdiv
    //   425: fstore #13
    //   427: getstatic java/awt/font/TextJustifier.DEBUG : Z
    //   430: ifeq -> 538
    //   433: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   436: new java/lang/StringBuilder
    //   439: dup
    //   440: invokespecial <init> : ()V
    //   443: ldc 'pass: '
    //   445: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   448: iload #5
    //   450: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   453: ldc ', d: '
    //   455: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   458: fload_1
    //   459: invokevirtual append : (F)Ljava/lang/StringBuilder;
    //   462: ldc ', l: '
    //   464: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   467: fload #8
    //   469: invokevirtual append : (F)Ljava/lang/StringBuilder;
    //   472: ldc ', w: '
    //   474: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   477: fload #7
    //   479: invokevirtual append : (F)Ljava/lang/StringBuilder;
    //   482: ldc ', aw: '
    //   484: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   487: fload #9
    //   489: invokevirtual append : (F)Ljava/lang/StringBuilder;
    //   492: ldc ', wd: '
    //   494: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   497: fload #12
    //   499: invokevirtual append : (F)Ljava/lang/StringBuilder;
    //   502: ldc ', wa: '
    //   504: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   507: fload #13
    //   509: invokevirtual append : (F)Ljava/lang/StringBuilder;
    //   512: ldc ', hit: '
    //   514: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   517: iload #10
    //   519: ifeq -> 527
    //   522: ldc 'y'
    //   524: goto -> 529
    //   527: ldc 'n'
    //   529: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   532: invokevirtual toString : ()Ljava/lang/String;
    //   535: invokevirtual println : (Ljava/lang/String;)V
    //   538: aload_0
    //   539: getfield start : I
    //   542: iconst_2
    //   543: imul
    //   544: istore #14
    //   546: aload_0
    //   547: getfield start : I
    //   550: istore #15
    //   552: iload #15
    //   554: aload_0
    //   555: getfield limit : I
    //   558: if_icmpge -> 760
    //   561: aload_0
    //   562: getfield info : [Ljava/awt/font/GlyphJustificationInfo;
    //   565: iload #15
    //   567: aaload
    //   568: astore #16
    //   570: iload_3
    //   571: ifeq -> 582
    //   574: aload #16
    //   576: getfield growPriority : I
    //   579: goto -> 587
    //   582: aload #16
    //   584: getfield shrinkPriority : I
    //   587: iload #5
    //   589: if_icmpne -> 751
    //   592: iload #15
    //   594: aload_0
    //   595: getfield start : I
    //   598: if_icmpeq -> 666
    //   601: iload #10
    //   603: ifeq -> 647
    //   606: iload_3
    //   607: ifeq -> 618
    //   610: aload #16
    //   612: getfield growLeftLimit : F
    //   615: goto -> 624
    //   618: aload #16
    //   620: getfield shrinkLeftLimit : F
    //   623: fneg
    //   624: fstore #17
    //   626: iload #11
    //   628: ifeq -> 657
    //   631: fload #17
    //   633: aload #16
    //   635: getfield weight : F
    //   638: fload #13
    //   640: fmul
    //   641: fadd
    //   642: fstore #17
    //   644: goto -> 657
    //   647: aload #16
    //   649: getfield weight : F
    //   652: fload #12
    //   654: fmul
    //   655: fstore #17
    //   657: aload_2
    //   658: iload #14
    //   660: dup2
    //   661: faload
    //   662: fload #17
    //   664: fadd
    //   665: fastore
    //   666: iinc #14, 1
    //   669: iload #15
    //   671: iconst_1
    //   672: iadd
    //   673: aload_0
    //   674: getfield limit : I
    //   677: if_icmpeq -> 745
    //   680: iload #10
    //   682: ifeq -> 726
    //   685: iload_3
    //   686: ifeq -> 697
    //   689: aload #16
    //   691: getfield growRightLimit : F
    //   694: goto -> 703
    //   697: aload #16
    //   699: getfield shrinkRightLimit : F
    //   702: fneg
    //   703: fstore #17
    //   705: iload #11
    //   707: ifeq -> 736
    //   710: fload #17
    //   712: aload #16
    //   714: getfield weight : F
    //   717: fload #13
    //   719: fmul
    //   720: fadd
    //   721: fstore #17
    //   723: goto -> 736
    //   726: aload #16
    //   728: getfield weight : F
    //   731: fload #12
    //   733: fmul
    //   734: fstore #17
    //   736: aload_2
    //   737: iload #14
    //   739: dup2
    //   740: faload
    //   741: fload #17
    //   743: fadd
    //   744: fastore
    //   745: iinc #14, 1
    //   748: goto -> 754
    //   751: iinc #14, 2
    //   754: iinc #15, 1
    //   757: goto -> 552
    //   760: iload #6
    //   762: ifne -> 783
    //   765: iload #10
    //   767: ifeq -> 783
    //   770: iload #11
    //   772: ifne -> 783
    //   775: fload_1
    //   776: fload #8
    //   778: fsub
    //   779: fstore_1
    //   780: goto -> 785
    //   783: fconst_0
    //   784: fstore_1
    //   785: iinc #5, 1
    //   788: goto -> 59
    //   791: getstatic java/awt/font/TextJustifier.DEBUG : Z
    //   794: ifeq -> 901
    //   797: fconst_0
    //   798: fstore #5
    //   800: iconst_0
    //   801: istore #6
    //   803: iload #6
    //   805: aload_2
    //   806: arraylength
    //   807: if_icmpge -> 869
    //   810: fload #5
    //   812: aload_2
    //   813: iload #6
    //   815: faload
    //   816: fadd
    //   817: fstore #5
    //   819: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   822: new java/lang/StringBuilder
    //   825: dup
    //   826: invokespecial <init> : ()V
    //   829: aload_2
    //   830: iload #6
    //   832: faload
    //   833: invokevirtual append : (F)Ljava/lang/StringBuilder;
    //   836: ldc ', '
    //   838: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   841: invokevirtual toString : ()Ljava/lang/String;
    //   844: invokevirtual print : (Ljava/lang/String;)V
    //   847: iload #6
    //   849: bipush #20
    //   851: irem
    //   852: bipush #9
    //   854: if_icmpne -> 863
    //   857: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   860: invokevirtual println : ()V
    //   863: iinc #6, 1
    //   866: goto -> 803
    //   869: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   872: new java/lang/StringBuilder
    //   875: dup
    //   876: invokespecial <init> : ()V
    //   879: ldc '\\ntotal: '
    //   881: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   884: fload #5
    //   886: invokevirtual append : (F)Ljava/lang/StringBuilder;
    //   889: invokevirtual toString : ()Ljava/lang/String;
    //   892: invokevirtual println : (Ljava/lang/String;)V
    //   895: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   898: invokevirtual println : ()V
    //   901: aload_2
    //   902: areturn }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\TextJustifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */