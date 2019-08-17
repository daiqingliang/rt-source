package com.sun.media.sound;

public final class SoftEnvelopeGenerator implements SoftProcess {
  public static final int EG_OFF = 0;
  
  public static final int EG_DELAY = 1;
  
  public static final int EG_ATTACK = 2;
  
  public static final int EG_HOLD = 3;
  
  public static final int EG_DECAY = 4;
  
  public static final int EG_SUSTAIN = 5;
  
  public static final int EG_RELEASE = 6;
  
  public static final int EG_SHUTDOWN = 7;
  
  public static final int EG_END = 8;
  
  int max_count = 10;
  
  int used_count = 0;
  
  private final int[] stage = new int[this.max_count];
  
  private final int[] stage_ix = new int[this.max_count];
  
  private final double[] stage_v = new double[this.max_count];
  
  private final int[] stage_count = new int[this.max_count];
  
  private final double[][] on = new double[this.max_count][1];
  
  private final double[][] active = new double[this.max_count][1];
  
  private final double[][] out = new double[this.max_count][1];
  
  private final double[][] delay = new double[this.max_count][1];
  
  private final double[][] attack = new double[this.max_count][1];
  
  private final double[][] hold = new double[this.max_count][1];
  
  private final double[][] decay = new double[this.max_count][1];
  
  private final double[][] sustain = new double[this.max_count][1];
  
  private final double[][] release = new double[this.max_count][1];
  
  private final double[][] shutdown = new double[this.max_count][1];
  
  private final double[][] release2 = new double[this.max_count][1];
  
  private final double[][] attack2 = new double[this.max_count][1];
  
  private final double[][] decay2 = new double[this.max_count][1];
  
  private double control_time = 0.0D;
  
  public void reset() {
    for (byte b = 0; b < this.used_count; b++) {
      this.stage[b] = 0;
      this.on[b][0] = 0.0D;
      this.out[b][0] = 0.0D;
      this.delay[b][0] = 0.0D;
      this.attack[b][0] = 0.0D;
      this.hold[b][0] = 0.0D;
      this.decay[b][0] = 0.0D;
      this.sustain[b][0] = 0.0D;
      this.release[b][0] = 0.0D;
      this.shutdown[b][0] = 0.0D;
      this.attack2[b][0] = 0.0D;
      this.decay2[b][0] = 0.0D;
      this.release2[b][0] = 0.0D;
    } 
    this.used_count = 0;
  }
  
  public void init(SoftSynthesizer paramSoftSynthesizer) {
    this.control_time = 1.0D / paramSoftSynthesizer.getControlRate();
    processControlLogic();
  }
  
  public double[] get(int paramInt, String paramString) {
    if (paramInt >= this.used_count)
      this.used_count = paramInt + 1; 
    return (paramString == null) ? this.out[paramInt] : (paramString.equals("on") ? this.on[paramInt] : (paramString.equals("active") ? this.active[paramInt] : (paramString.equals("delay") ? this.delay[paramInt] : (paramString.equals("attack") ? this.attack[paramInt] : (paramString.equals("hold") ? this.hold[paramInt] : (paramString.equals("decay") ? this.decay[paramInt] : (paramString.equals("sustain") ? this.sustain[paramInt] : (paramString.equals("release") ? this.release[paramInt] : (paramString.equals("shutdown") ? this.shutdown[paramInt] : (paramString.equals("attack2") ? this.attack2[paramInt] : (paramString.equals("decay2") ? this.decay2[paramInt] : (paramString.equals("release2") ? this.release2[paramInt] : null))))))))))));
  }
  
  public void processControlLogic() { // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: iload_1
    //   3: aload_0
    //   4: getfield used_count : I
    //   7: if_icmpge -> 1452
    //   10: aload_0
    //   11: getfield stage : [I
    //   14: iload_1
    //   15: iaload
    //   16: bipush #8
    //   18: if_icmpne -> 24
    //   21: goto -> 1446
    //   24: aload_0
    //   25: getfield stage : [I
    //   28: iload_1
    //   29: iaload
    //   30: ifle -> 338
    //   33: aload_0
    //   34: getfield stage : [I
    //   37: iload_1
    //   38: iaload
    //   39: bipush #6
    //   41: if_icmpge -> 338
    //   44: aload_0
    //   45: getfield on : [[D
    //   48: iload_1
    //   49: aaload
    //   50: iconst_0
    //   51: daload
    //   52: ldc2_w 0.5
    //   55: dcmpg
    //   56: ifge -> 338
    //   59: aload_0
    //   60: getfield on : [[D
    //   63: iload_1
    //   64: aaload
    //   65: iconst_0
    //   66: daload
    //   67: ldc2_w -0.5
    //   70: dcmpg
    //   71: ifge -> 152
    //   74: aload_0
    //   75: getfield stage_count : [I
    //   78: iload_1
    //   79: ldc2_w 2.0
    //   82: aload_0
    //   83: getfield shutdown : [[D
    //   86: iload_1
    //   87: aaload
    //   88: iconst_0
    //   89: daload
    //   90: ldc2_w 1200.0
    //   93: ddiv
    //   94: invokestatic pow : (DD)D
    //   97: aload_0
    //   98: getfield control_time : D
    //   101: ddiv
    //   102: d2i
    //   103: iastore
    //   104: aload_0
    //   105: getfield stage_count : [I
    //   108: iload_1
    //   109: iaload
    //   110: ifge -> 120
    //   113: aload_0
    //   114: getfield stage_count : [I
    //   117: iload_1
    //   118: iconst_0
    //   119: iastore
    //   120: aload_0
    //   121: getfield stage_v : [D
    //   124: iload_1
    //   125: aload_0
    //   126: getfield out : [[D
    //   129: iload_1
    //   130: aaload
    //   131: iconst_0
    //   132: daload
    //   133: dastore
    //   134: aload_0
    //   135: getfield stage_ix : [I
    //   138: iload_1
    //   139: iconst_0
    //   140: iastore
    //   141: aload_0
    //   142: getfield stage : [I
    //   145: iload_1
    //   146: bipush #7
    //   148: iastore
    //   149: goto -> 338
    //   152: aload_0
    //   153: getfield release2 : [[D
    //   156: iload_1
    //   157: aaload
    //   158: iconst_0
    //   159: daload
    //   160: ldc2_w 1.0E-6
    //   163: dcmpg
    //   164: ifge -> 223
    //   167: aload_0
    //   168: getfield release : [[D
    //   171: iload_1
    //   172: aaload
    //   173: iconst_0
    //   174: daload
    //   175: dconst_0
    //   176: dcmpg
    //   177: ifge -> 223
    //   180: aload_0
    //   181: getfield release : [[D
    //   184: iload_1
    //   185: aaload
    //   186: iconst_0
    //   187: daload
    //   188: invokestatic isInfinite : (D)Z
    //   191: ifeq -> 223
    //   194: aload_0
    //   195: getfield out : [[D
    //   198: iload_1
    //   199: aaload
    //   200: iconst_0
    //   201: dconst_0
    //   202: dastore
    //   203: aload_0
    //   204: getfield active : [[D
    //   207: iload_1
    //   208: aaload
    //   209: iconst_0
    //   210: dconst_0
    //   211: dastore
    //   212: aload_0
    //   213: getfield stage : [I
    //   216: iload_1
    //   217: bipush #8
    //   219: iastore
    //   220: goto -> 1446
    //   223: aload_0
    //   224: getfield stage_count : [I
    //   227: iload_1
    //   228: ldc2_w 2.0
    //   231: aload_0
    //   232: getfield release : [[D
    //   235: iload_1
    //   236: aaload
    //   237: iconst_0
    //   238: daload
    //   239: ldc2_w 1200.0
    //   242: ddiv
    //   243: invokestatic pow : (DD)D
    //   246: aload_0
    //   247: getfield control_time : D
    //   250: ddiv
    //   251: d2i
    //   252: iastore
    //   253: aload_0
    //   254: getfield stage_count : [I
    //   257: iload_1
    //   258: dup2
    //   259: iaload
    //   260: aload_0
    //   261: getfield release2 : [[D
    //   264: iload_1
    //   265: aaload
    //   266: iconst_0
    //   267: daload
    //   268: aload_0
    //   269: getfield control_time : D
    //   272: ldc2_w 1000.0
    //   275: dmul
    //   276: ddiv
    //   277: d2i
    //   278: iadd
    //   279: iastore
    //   280: aload_0
    //   281: getfield stage_count : [I
    //   284: iload_1
    //   285: iaload
    //   286: ifge -> 296
    //   289: aload_0
    //   290: getfield stage_count : [I
    //   293: iload_1
    //   294: iconst_0
    //   295: iastore
    //   296: aload_0
    //   297: getfield stage_ix : [I
    //   300: iload_1
    //   301: iconst_0
    //   302: iastore
    //   303: dconst_1
    //   304: aload_0
    //   305: getfield out : [[D
    //   308: iload_1
    //   309: aaload
    //   310: iconst_0
    //   311: daload
    //   312: dsub
    //   313: dstore_2
    //   314: aload_0
    //   315: getfield stage_ix : [I
    //   318: iload_1
    //   319: aload_0
    //   320: getfield stage_count : [I
    //   323: iload_1
    //   324: iaload
    //   325: i2d
    //   326: dload_2
    //   327: dmul
    //   328: d2i
    //   329: iastore
    //   330: aload_0
    //   331: getfield stage : [I
    //   334: iload_1
    //   335: bipush #6
    //   337: iastore
    //   338: aload_0
    //   339: getfield stage : [I
    //   342: iload_1
    //   343: iaload
    //   344: tableswitch default -> 1446, 0 -> 392, 1 -> 472, 2 -> 668, 3 -> 777, 4 -> 892, 5 -> 1020, 6 -> 1023, 7 -> 1353
    //   392: aload_0
    //   393: getfield active : [[D
    //   396: iload_1
    //   397: aaload
    //   398: iconst_0
    //   399: dconst_1
    //   400: dastore
    //   401: aload_0
    //   402: getfield on : [[D
    //   405: iload_1
    //   406: aaload
    //   407: iconst_0
    //   408: daload
    //   409: ldc2_w 0.5
    //   412: dcmpg
    //   413: ifge -> 419
    //   416: goto -> 1446
    //   419: aload_0
    //   420: getfield stage : [I
    //   423: iload_1
    //   424: iconst_1
    //   425: iastore
    //   426: aload_0
    //   427: getfield stage_ix : [I
    //   430: iload_1
    //   431: ldc2_w 2.0
    //   434: aload_0
    //   435: getfield delay : [[D
    //   438: iload_1
    //   439: aaload
    //   440: iconst_0
    //   441: daload
    //   442: ldc2_w 1200.0
    //   445: ddiv
    //   446: invokestatic pow : (DD)D
    //   449: aload_0
    //   450: getfield control_time : D
    //   453: ddiv
    //   454: d2i
    //   455: iastore
    //   456: aload_0
    //   457: getfield stage_ix : [I
    //   460: iload_1
    //   461: iaload
    //   462: ifge -> 472
    //   465: aload_0
    //   466: getfield stage_ix : [I
    //   469: iload_1
    //   470: iconst_0
    //   471: iastore
    //   472: aload_0
    //   473: getfield stage_ix : [I
    //   476: iload_1
    //   477: iaload
    //   478: ifne -> 655
    //   481: aload_0
    //   482: getfield attack : [[D
    //   485: iload_1
    //   486: aaload
    //   487: iconst_0
    //   488: daload
    //   489: dstore_2
    //   490: aload_0
    //   491: getfield attack2 : [[D
    //   494: iload_1
    //   495: aaload
    //   496: iconst_0
    //   497: daload
    //   498: dstore #4
    //   500: dload #4
    //   502: ldc2_w 1.0E-6
    //   505: dcmpg
    //   506: ifge -> 578
    //   509: dload_2
    //   510: dconst_0
    //   511: dcmpg
    //   512: ifge -> 578
    //   515: dload_2
    //   516: invokestatic isInfinite : (D)Z
    //   519: ifeq -> 578
    //   522: aload_0
    //   523: getfield out : [[D
    //   526: iload_1
    //   527: aaload
    //   528: iconst_0
    //   529: dconst_1
    //   530: dastore
    //   531: aload_0
    //   532: getfield stage : [I
    //   535: iload_1
    //   536: iconst_3
    //   537: iastore
    //   538: aload_0
    //   539: getfield stage_count : [I
    //   542: iload_1
    //   543: ldc2_w 2.0
    //   546: aload_0
    //   547: getfield hold : [[D
    //   550: iload_1
    //   551: aaload
    //   552: iconst_0
    //   553: daload
    //   554: ldc2_w 1200.0
    //   557: ddiv
    //   558: invokestatic pow : (DD)D
    //   561: aload_0
    //   562: getfield control_time : D
    //   565: ddiv
    //   566: d2i
    //   567: iastore
    //   568: aload_0
    //   569: getfield stage_ix : [I
    //   572: iload_1
    //   573: iconst_0
    //   574: iastore
    //   575: goto -> 652
    //   578: aload_0
    //   579: getfield stage : [I
    //   582: iload_1
    //   583: iconst_2
    //   584: iastore
    //   585: aload_0
    //   586: getfield stage_count : [I
    //   589: iload_1
    //   590: ldc2_w 2.0
    //   593: dload_2
    //   594: ldc2_w 1200.0
    //   597: ddiv
    //   598: invokestatic pow : (DD)D
    //   601: aload_0
    //   602: getfield control_time : D
    //   605: ddiv
    //   606: d2i
    //   607: iastore
    //   608: aload_0
    //   609: getfield stage_count : [I
    //   612: iload_1
    //   613: dup2
    //   614: iaload
    //   615: dload #4
    //   617: aload_0
    //   618: getfield control_time : D
    //   621: ldc2_w 1000.0
    //   624: dmul
    //   625: ddiv
    //   626: d2i
    //   627: iadd
    //   628: iastore
    //   629: aload_0
    //   630: getfield stage_count : [I
    //   633: iload_1
    //   634: iaload
    //   635: ifge -> 645
    //   638: aload_0
    //   639: getfield stage_count : [I
    //   642: iload_1
    //   643: iconst_0
    //   644: iastore
    //   645: aload_0
    //   646: getfield stage_ix : [I
    //   649: iload_1
    //   650: iconst_0
    //   651: iastore
    //   652: goto -> 1446
    //   655: aload_0
    //   656: getfield stage_ix : [I
    //   659: iload_1
    //   660: dup2
    //   661: iaload
    //   662: iconst_1
    //   663: isub
    //   664: iastore
    //   665: goto -> 1446
    //   668: aload_0
    //   669: getfield stage_ix : [I
    //   672: iload_1
    //   673: dup2
    //   674: iaload
    //   675: iconst_1
    //   676: iadd
    //   677: iastore
    //   678: aload_0
    //   679: getfield stage_ix : [I
    //   682: iload_1
    //   683: iaload
    //   684: aload_0
    //   685: getfield stage_count : [I
    //   688: iload_1
    //   689: iaload
    //   690: if_icmplt -> 712
    //   693: aload_0
    //   694: getfield out : [[D
    //   697: iload_1
    //   698: aaload
    //   699: iconst_0
    //   700: dconst_1
    //   701: dastore
    //   702: aload_0
    //   703: getfield stage : [I
    //   706: iload_1
    //   707: iconst_3
    //   708: iastore
    //   709: goto -> 1446
    //   712: aload_0
    //   713: getfield stage_ix : [I
    //   716: iload_1
    //   717: iaload
    //   718: i2d
    //   719: aload_0
    //   720: getfield stage_count : [I
    //   723: iload_1
    //   724: iaload
    //   725: i2d
    //   726: ddiv
    //   727: dstore_2
    //   728: dconst_1
    //   729: ldc2_w 0.4166666666666667
    //   732: ldc2_w 10.0
    //   735: invokestatic log : (D)D
    //   738: ddiv
    //   739: dload_2
    //   740: invokestatic log : (D)D
    //   743: dmul
    //   744: dadd
    //   745: dstore_2
    //   746: dload_2
    //   747: dconst_0
    //   748: dcmpg
    //   749: ifge -> 757
    //   752: dconst_0
    //   753: dstore_2
    //   754: goto -> 765
    //   757: dload_2
    //   758: dconst_1
    //   759: dcmpl
    //   760: ifle -> 765
    //   763: dconst_1
    //   764: dstore_2
    //   765: aload_0
    //   766: getfield out : [[D
    //   769: iload_1
    //   770: aaload
    //   771: iconst_0
    //   772: dload_2
    //   773: dastore
    //   774: goto -> 1446
    //   777: aload_0
    //   778: getfield stage_ix : [I
    //   781: iload_1
    //   782: dup2
    //   783: iaload
    //   784: iconst_1
    //   785: iadd
    //   786: iastore
    //   787: aload_0
    //   788: getfield stage_ix : [I
    //   791: iload_1
    //   792: iaload
    //   793: aload_0
    //   794: getfield stage_count : [I
    //   797: iload_1
    //   798: iaload
    //   799: if_icmplt -> 1446
    //   802: aload_0
    //   803: getfield stage : [I
    //   806: iload_1
    //   807: iconst_4
    //   808: iastore
    //   809: aload_0
    //   810: getfield stage_count : [I
    //   813: iload_1
    //   814: ldc2_w 2.0
    //   817: aload_0
    //   818: getfield decay : [[D
    //   821: iload_1
    //   822: aaload
    //   823: iconst_0
    //   824: daload
    //   825: ldc2_w 1200.0
    //   828: ddiv
    //   829: invokestatic pow : (DD)D
    //   832: aload_0
    //   833: getfield control_time : D
    //   836: ddiv
    //   837: d2i
    //   838: iastore
    //   839: aload_0
    //   840: getfield stage_count : [I
    //   843: iload_1
    //   844: dup2
    //   845: iaload
    //   846: aload_0
    //   847: getfield decay2 : [[D
    //   850: iload_1
    //   851: aaload
    //   852: iconst_0
    //   853: daload
    //   854: aload_0
    //   855: getfield control_time : D
    //   858: ldc2_w 1000.0
    //   861: dmul
    //   862: ddiv
    //   863: d2i
    //   864: iadd
    //   865: iastore
    //   866: aload_0
    //   867: getfield stage_count : [I
    //   870: iload_1
    //   871: iaload
    //   872: ifge -> 882
    //   875: aload_0
    //   876: getfield stage_count : [I
    //   879: iload_1
    //   880: iconst_0
    //   881: iastore
    //   882: aload_0
    //   883: getfield stage_ix : [I
    //   886: iload_1
    //   887: iconst_0
    //   888: iastore
    //   889: goto -> 1446
    //   892: aload_0
    //   893: getfield stage_ix : [I
    //   896: iload_1
    //   897: dup2
    //   898: iaload
    //   899: iconst_1
    //   900: iadd
    //   901: iastore
    //   902: aload_0
    //   903: getfield sustain : [[D
    //   906: iload_1
    //   907: aaload
    //   908: iconst_0
    //   909: daload
    //   910: ldc2_w 0.001
    //   913: dmul
    //   914: dstore_2
    //   915: aload_0
    //   916: getfield stage_ix : [I
    //   919: iload_1
    //   920: iaload
    //   921: aload_0
    //   922: getfield stage_count : [I
    //   925: iload_1
    //   926: iaload
    //   927: if_icmplt -> 983
    //   930: aload_0
    //   931: getfield out : [[D
    //   934: iload_1
    //   935: aaload
    //   936: iconst_0
    //   937: dload_2
    //   938: dastore
    //   939: aload_0
    //   940: getfield stage : [I
    //   943: iload_1
    //   944: iconst_5
    //   945: iastore
    //   946: dload_2
    //   947: ldc2_w 0.001
    //   950: dcmpg
    //   951: ifge -> 1446
    //   954: aload_0
    //   955: getfield out : [[D
    //   958: iload_1
    //   959: aaload
    //   960: iconst_0
    //   961: dconst_0
    //   962: dastore
    //   963: aload_0
    //   964: getfield active : [[D
    //   967: iload_1
    //   968: aaload
    //   969: iconst_0
    //   970: dconst_0
    //   971: dastore
    //   972: aload_0
    //   973: getfield stage : [I
    //   976: iload_1
    //   977: bipush #8
    //   979: iastore
    //   980: goto -> 1446
    //   983: aload_0
    //   984: getfield stage_ix : [I
    //   987: iload_1
    //   988: iaload
    //   989: i2d
    //   990: aload_0
    //   991: getfield stage_count : [I
    //   994: iload_1
    //   995: iaload
    //   996: i2d
    //   997: ddiv
    //   998: dstore #4
    //   1000: aload_0
    //   1001: getfield out : [[D
    //   1004: iload_1
    //   1005: aaload
    //   1006: iconst_0
    //   1007: dconst_1
    //   1008: dload #4
    //   1010: dsub
    //   1011: dload_2
    //   1012: dload #4
    //   1014: dmul
    //   1015: dadd
    //   1016: dastore
    //   1017: goto -> 1446
    //   1020: goto -> 1446
    //   1023: aload_0
    //   1024: getfield stage_ix : [I
    //   1027: iload_1
    //   1028: dup2
    //   1029: iaload
    //   1030: iconst_1
    //   1031: iadd
    //   1032: iastore
    //   1033: aload_0
    //   1034: getfield stage_ix : [I
    //   1037: iload_1
    //   1038: iaload
    //   1039: aload_0
    //   1040: getfield stage_count : [I
    //   1043: iload_1
    //   1044: iaload
    //   1045: if_icmplt -> 1077
    //   1048: aload_0
    //   1049: getfield out : [[D
    //   1052: iload_1
    //   1053: aaload
    //   1054: iconst_0
    //   1055: dconst_0
    //   1056: dastore
    //   1057: aload_0
    //   1058: getfield active : [[D
    //   1061: iload_1
    //   1062: aaload
    //   1063: iconst_0
    //   1064: dconst_0
    //   1065: dastore
    //   1066: aload_0
    //   1067: getfield stage : [I
    //   1070: iload_1
    //   1071: bipush #8
    //   1073: iastore
    //   1074: goto -> 1446
    //   1077: aload_0
    //   1078: getfield stage_ix : [I
    //   1081: iload_1
    //   1082: iaload
    //   1083: i2d
    //   1084: aload_0
    //   1085: getfield stage_count : [I
    //   1088: iload_1
    //   1089: iaload
    //   1090: i2d
    //   1091: ddiv
    //   1092: dstore #4
    //   1094: aload_0
    //   1095: getfield out : [[D
    //   1098: iload_1
    //   1099: aaload
    //   1100: iconst_0
    //   1101: dconst_1
    //   1102: dload #4
    //   1104: dsub
    //   1105: dastore
    //   1106: aload_0
    //   1107: getfield on : [[D
    //   1110: iload_1
    //   1111: aaload
    //   1112: iconst_0
    //   1113: daload
    //   1114: ldc2_w -0.5
    //   1117: dcmpg
    //   1118: ifge -> 1196
    //   1121: aload_0
    //   1122: getfield stage_count : [I
    //   1125: iload_1
    //   1126: ldc2_w 2.0
    //   1129: aload_0
    //   1130: getfield shutdown : [[D
    //   1133: iload_1
    //   1134: aaload
    //   1135: iconst_0
    //   1136: daload
    //   1137: ldc2_w 1200.0
    //   1140: ddiv
    //   1141: invokestatic pow : (DD)D
    //   1144: aload_0
    //   1145: getfield control_time : D
    //   1148: ddiv
    //   1149: d2i
    //   1150: iastore
    //   1151: aload_0
    //   1152: getfield stage_count : [I
    //   1155: iload_1
    //   1156: iaload
    //   1157: ifge -> 1167
    //   1160: aload_0
    //   1161: getfield stage_count : [I
    //   1164: iload_1
    //   1165: iconst_0
    //   1166: iastore
    //   1167: aload_0
    //   1168: getfield stage_v : [D
    //   1171: iload_1
    //   1172: aload_0
    //   1173: getfield out : [[D
    //   1176: iload_1
    //   1177: aaload
    //   1178: iconst_0
    //   1179: daload
    //   1180: dastore
    //   1181: aload_0
    //   1182: getfield stage_ix : [I
    //   1185: iload_1
    //   1186: iconst_0
    //   1187: iastore
    //   1188: aload_0
    //   1189: getfield stage : [I
    //   1192: iload_1
    //   1193: bipush #7
    //   1195: iastore
    //   1196: aload_0
    //   1197: getfield on : [[D
    //   1200: iload_1
    //   1201: aaload
    //   1202: iconst_0
    //   1203: daload
    //   1204: ldc2_w 0.5
    //   1207: dcmpl
    //   1208: ifle -> 1350
    //   1211: aload_0
    //   1212: getfield sustain : [[D
    //   1215: iload_1
    //   1216: aaload
    //   1217: iconst_0
    //   1218: daload
    //   1219: ldc2_w 0.001
    //   1222: dmul
    //   1223: dstore_2
    //   1224: aload_0
    //   1225: getfield out : [[D
    //   1228: iload_1
    //   1229: aaload
    //   1230: iconst_0
    //   1231: daload
    //   1232: dload_2
    //   1233: dcmpl
    //   1234: ifle -> 1350
    //   1237: aload_0
    //   1238: getfield stage : [I
    //   1241: iload_1
    //   1242: iconst_4
    //   1243: iastore
    //   1244: aload_0
    //   1245: getfield stage_count : [I
    //   1248: iload_1
    //   1249: ldc2_w 2.0
    //   1252: aload_0
    //   1253: getfield decay : [[D
    //   1256: iload_1
    //   1257: aaload
    //   1258: iconst_0
    //   1259: daload
    //   1260: ldc2_w 1200.0
    //   1263: ddiv
    //   1264: invokestatic pow : (DD)D
    //   1267: aload_0
    //   1268: getfield control_time : D
    //   1271: ddiv
    //   1272: d2i
    //   1273: iastore
    //   1274: aload_0
    //   1275: getfield stage_count : [I
    //   1278: iload_1
    //   1279: dup2
    //   1280: iaload
    //   1281: aload_0
    //   1282: getfield decay2 : [[D
    //   1285: iload_1
    //   1286: aaload
    //   1287: iconst_0
    //   1288: daload
    //   1289: aload_0
    //   1290: getfield control_time : D
    //   1293: ldc2_w 1000.0
    //   1296: dmul
    //   1297: ddiv
    //   1298: d2i
    //   1299: iadd
    //   1300: iastore
    //   1301: aload_0
    //   1302: getfield stage_count : [I
    //   1305: iload_1
    //   1306: iaload
    //   1307: ifge -> 1317
    //   1310: aload_0
    //   1311: getfield stage_count : [I
    //   1314: iload_1
    //   1315: iconst_0
    //   1316: iastore
    //   1317: aload_0
    //   1318: getfield out : [[D
    //   1321: iload_1
    //   1322: aaload
    //   1323: iconst_0
    //   1324: daload
    //   1325: dconst_1
    //   1326: dsub
    //   1327: dload_2
    //   1328: dconst_1
    //   1329: dsub
    //   1330: ddiv
    //   1331: dstore #4
    //   1333: aload_0
    //   1334: getfield stage_ix : [I
    //   1337: iload_1
    //   1338: aload_0
    //   1339: getfield stage_count : [I
    //   1342: iload_1
    //   1343: iaload
    //   1344: i2d
    //   1345: dload #4
    //   1347: dmul
    //   1348: d2i
    //   1349: iastore
    //   1350: goto -> 1446
    //   1353: aload_0
    //   1354: getfield stage_ix : [I
    //   1357: iload_1
    //   1358: dup2
    //   1359: iaload
    //   1360: iconst_1
    //   1361: iadd
    //   1362: iastore
    //   1363: aload_0
    //   1364: getfield stage_ix : [I
    //   1367: iload_1
    //   1368: iaload
    //   1369: aload_0
    //   1370: getfield stage_count : [I
    //   1373: iload_1
    //   1374: iaload
    //   1375: if_icmplt -> 1407
    //   1378: aload_0
    //   1379: getfield out : [[D
    //   1382: iload_1
    //   1383: aaload
    //   1384: iconst_0
    //   1385: dconst_0
    //   1386: dastore
    //   1387: aload_0
    //   1388: getfield active : [[D
    //   1391: iload_1
    //   1392: aaload
    //   1393: iconst_0
    //   1394: dconst_0
    //   1395: dastore
    //   1396: aload_0
    //   1397: getfield stage : [I
    //   1400: iload_1
    //   1401: bipush #8
    //   1403: iastore
    //   1404: goto -> 1446
    //   1407: aload_0
    //   1408: getfield stage_ix : [I
    //   1411: iload_1
    //   1412: iaload
    //   1413: i2d
    //   1414: aload_0
    //   1415: getfield stage_count : [I
    //   1418: iload_1
    //   1419: iaload
    //   1420: i2d
    //   1421: ddiv
    //   1422: dstore #4
    //   1424: aload_0
    //   1425: getfield out : [[D
    //   1428: iload_1
    //   1429: aaload
    //   1430: iconst_0
    //   1431: dconst_1
    //   1432: dload #4
    //   1434: dsub
    //   1435: aload_0
    //   1436: getfield stage_v : [D
    //   1439: iload_1
    //   1440: daload
    //   1441: dmul
    //   1442: dastore
    //   1443: goto -> 1446
    //   1446: iinc #1, 1
    //   1449: goto -> 2
    //   1452: return }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftEnvelopeGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */