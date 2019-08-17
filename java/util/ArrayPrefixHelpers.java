package java.util;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;

class ArrayPrefixHelpers {
  static final int CUMULATE = 1;
  
  static final int SUMMED = 2;
  
  static final int FINISHED = 4;
  
  static final int MIN_PARTITION = 16;
  
  static final class CumulateTask<T> extends CountedCompleter<Void> {
    final T[] array;
    
    final BinaryOperator<T> function;
    
    CumulateTask<T> left;
    
    CumulateTask<T> right;
    
    T in;
    
    T out;
    
    final int lo;
    
    final int hi;
    
    final int origin;
    
    final int fence;
    
    final int threshold;
    
    public CumulateTask(CumulateTask<T> param1CumulateTask, BinaryOperator<T> param1BinaryOperator, T[] param1ArrayOfT, int param1Int1, int param1Int2) {
      super(param1CumulateTask);
      this.function = param1BinaryOperator;
      this.array = param1ArrayOfT;
      this.lo = this.origin = param1Int1;
      this.hi = this.fence = param1Int2;
      int i;
      this.threshold = ((i = (param1Int2 - param1Int1) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16) ? 16 : i;
    }
    
    CumulateTask(CumulateTask<T> param1CumulateTask, BinaryOperator<T> param1BinaryOperator, T[] param1ArrayOfT, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      super(param1CumulateTask);
      this.function = param1BinaryOperator;
      this.array = param1ArrayOfT;
      this.origin = param1Int1;
      this.fence = param1Int2;
      this.threshold = param1Int3;
      this.lo = param1Int4;
      this.hi = param1Int5;
    }
    
    public final void compute() {
      BinaryOperator binaryOperator;
      Object[] arrayOfObject;
      if ((binaryOperator = this.function) == null || (arrayOfObject = this.array) == null)
        throw new NullPointerException(); 
      int i = this.threshold;
      int j = this.origin;
      int k = this.fence;
      CumulateTask cumulateTask = this;
      int m;
      int n;
      while ((m = cumulateTask.lo) >= 0 && (n = cumulateTask.hi) <= arrayOfObject.length) {
        if (n - m > i) {
          CumulateTask cumulateTask3;
          CumulateTask cumulateTask1 = cumulateTask.left;
          CumulateTask cumulateTask2 = cumulateTask.right;
          if (cumulateTask1 == null) {
            int i2 = m + n >>> 1;
            cumulateTask3 = cumulateTask2 = cumulateTask.right = new CumulateTask(cumulateTask, binaryOperator, arrayOfObject, j, k, i, i2, n);
            cumulateTask = cumulateTask1 = cumulateTask.left = new CumulateTask(cumulateTask, binaryOperator, arrayOfObject, j, k, i, m, i2);
          } else {
            Object object = cumulateTask.in;
            cumulateTask1.in = object;
            cumulateTask3 = cumulateTask = null;
            if (cumulateTask2 != null) {
              Object object1 = cumulateTask1.out;
              cumulateTask2.in = (m == j) ? object1 : binaryOperator.apply(object, object1);
              int i3;
              while (((i3 = cumulateTask2.getPendingCount()) & true) == 0) {
                if (cumulateTask2.compareAndSetPendingCount(i3, i3 | true)) {
                  cumulateTask = cumulateTask2;
                  break;
                } 
              } 
            } 
            int i2;
            while (((i2 = cumulateTask1.getPendingCount()) & true) == 0) {
              if (cumulateTask1.compareAndSetPendingCount(i2, i2 | true)) {
                if (cumulateTask != null)
                  cumulateTask3 = cumulateTask; 
                cumulateTask = cumulateTask1;
                break;
              } 
            } 
            if (cumulateTask == null)
              break; 
          } 
          if (cumulateTask3 != null)
            cumulateTask3.fork(); 
          continue;
        } 
        int i1;
        while (((i1 = cumulateTask.getPendingCount()) & 0x4) == 0) {
          int i2 = ((i1 & true) != 0) ? 4 : ((m > j) ? 2 : 6);
          if (cumulateTask.compareAndSetPendingCount(i1, i1 | i2)) {
            Object object;
            if (i2 != 2) {
              int i3;
              if (m == j) {
                object = arrayOfObject[j];
                i3 = j + 1;
              } else {
                object = cumulateTask.in;
                i3 = m;
              } 
              for (int i4 = i3; i4 < n; i4++)
                arrayOfObject[i4] = object = binaryOperator.apply(object, arrayOfObject[i4]); 
            } else if (n < k) {
              object = arrayOfObject[m];
              for (int i3 = m + 1; i3 < n; i3++)
                object = binaryOperator.apply(object, arrayOfObject[i3]); 
            } else {
              object = cumulateTask.in;
            } 
            cumulateTask.out = object;
            while (true) {
              CumulateTask cumulateTask1;
              if ((cumulateTask1 = (CumulateTask)cumulateTask.getCompleter()) == null) {
                if ((i2 & 0x4) != 0)
                  cumulateTask.quietlyComplete(); 
                break;
              } 
              int i3 = cumulateTask1.getPendingCount();
              if ((i3 & i2 & 0x4) != 0) {
                cumulateTask = cumulateTask1;
                continue;
              } 
              if ((i3 & i2 & 0x2) != 0) {
                CumulateTask cumulateTask2;
                CumulateTask cumulateTask3;
                if ((cumulateTask2 = cumulateTask1.left) != null && (cumulateTask3 = cumulateTask1.right) != null) {
                  Object object1 = cumulateTask2.out;
                  cumulateTask1.out = (cumulateTask3.hi == k) ? object1 : binaryOperator.apply(object1, cumulateTask3.out);
                } 
                int i5 = ((i3 & true) == 0 && cumulateTask1.lo == j) ? 1 : 0;
                int i4;
                if ((i4 = i3 | i2 | i5) == i3 || cumulateTask1.compareAndSetPendingCount(i3, i4)) {
                  i2 = 2;
                  cumulateTask = cumulateTask1;
                  if (i5 != 0)
                    cumulateTask1.fork(); 
                } 
                continue;
              } 
              if (cumulateTask1.compareAndSetPendingCount(i3, i3 | i2))
                break; 
            } 
            break;
          } 
        } 
      } 
    }
  }
  
  static final class DoubleCumulateTask extends CountedCompleter<Void> {
    final double[] array;
    
    final DoubleBinaryOperator function;
    
    DoubleCumulateTask left;
    
    DoubleCumulateTask right;
    
    double in;
    
    double out;
    
    final int lo;
    
    final int hi;
    
    final int origin;
    
    final int fence;
    
    final int threshold;
    
    public DoubleCumulateTask(DoubleCumulateTask param1DoubleCumulateTask, DoubleBinaryOperator param1DoubleBinaryOperator, double[] param1ArrayOfDouble, int param1Int1, int param1Int2) {
      super(param1DoubleCumulateTask);
      this.function = param1DoubleBinaryOperator;
      this.array = param1ArrayOfDouble;
      this.lo = this.origin = param1Int1;
      this.hi = this.fence = param1Int2;
      int i;
      this.threshold = ((i = (param1Int2 - param1Int1) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16) ? 16 : i;
    }
    
    DoubleCumulateTask(DoubleCumulateTask param1DoubleCumulateTask, DoubleBinaryOperator param1DoubleBinaryOperator, double[] param1ArrayOfDouble, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      super(param1DoubleCumulateTask);
      this.function = param1DoubleBinaryOperator;
      this.array = param1ArrayOfDouble;
      this.origin = param1Int1;
      this.fence = param1Int2;
      this.threshold = param1Int3;
      this.lo = param1Int4;
      this.hi = param1Int5;
    }
    
    public final void compute() { // Byte code:
      //   0: aload_0
      //   1: getfield function : Ljava/util/function/DoubleBinaryOperator;
      //   4: dup
      //   5: astore_1
      //   6: ifnull -> 18
      //   9: aload_0
      //   10: getfield array : [D
      //   13: dup
      //   14: astore_2
      //   15: ifnonnull -> 26
      //   18: new java/lang/NullPointerException
      //   21: dup
      //   22: invokespecial <init> : ()V
      //   25: athrow
      //   26: aload_0
      //   27: getfield threshold : I
      //   30: istore_3
      //   31: aload_0
      //   32: getfield origin : I
      //   35: istore #4
      //   37: aload_0
      //   38: getfield fence : I
      //   41: istore #5
      //   43: aload_0
      //   44: astore #8
      //   46: aload #8
      //   48: getfield lo : I
      //   51: dup
      //   52: istore #6
      //   54: iflt -> 756
      //   57: aload #8
      //   59: getfield hi : I
      //   62: dup
      //   63: istore #7
      //   65: aload_2
      //   66: arraylength
      //   67: if_icmpgt -> 756
      //   70: iload #7
      //   72: iload #6
      //   74: isub
      //   75: iload_3
      //   76: if_icmple -> 336
      //   79: aload #8
      //   81: getfield left : Ljava/util/ArrayPrefixHelpers$DoubleCumulateTask;
      //   84: astore #9
      //   86: aload #8
      //   88: getfield right : Ljava/util/ArrayPrefixHelpers$DoubleCumulateTask;
      //   91: astore #10
      //   93: aload #9
      //   95: ifnonnull -> 172
      //   98: iload #6
      //   100: iload #7
      //   102: iadd
      //   103: iconst_1
      //   104: iushr
      //   105: istore #12
      //   107: aload #8
      //   109: new java/util/ArrayPrefixHelpers$DoubleCumulateTask
      //   112: dup
      //   113: aload #8
      //   115: aload_1
      //   116: aload_2
      //   117: iload #4
      //   119: iload #5
      //   121: iload_3
      //   122: iload #12
      //   124: iload #7
      //   126: invokespecial <init> : (Ljava/util/ArrayPrefixHelpers$DoubleCumulateTask;Ljava/util/function/DoubleBinaryOperator;[DIIIII)V
      //   129: dup_x1
      //   130: putfield right : Ljava/util/ArrayPrefixHelpers$DoubleCumulateTask;
      //   133: dup
      //   134: astore #10
      //   136: astore #11
      //   138: aload #8
      //   140: new java/util/ArrayPrefixHelpers$DoubleCumulateTask
      //   143: dup
      //   144: aload #8
      //   146: aload_1
      //   147: aload_2
      //   148: iload #4
      //   150: iload #5
      //   152: iload_3
      //   153: iload #6
      //   155: iload #12
      //   157: invokespecial <init> : (Ljava/util/ArrayPrefixHelpers$DoubleCumulateTask;Ljava/util/function/DoubleBinaryOperator;[DIIIII)V
      //   160: dup_x1
      //   161: putfield left : Ljava/util/ArrayPrefixHelpers$DoubleCumulateTask;
      //   164: dup
      //   165: astore #9
      //   167: astore #8
      //   169: goto -> 322
      //   172: aload #8
      //   174: getfield in : D
      //   177: dstore #12
      //   179: aload #9
      //   181: dload #12
      //   183: putfield in : D
      //   186: aconst_null
      //   187: dup
      //   188: astore #8
      //   190: astore #11
      //   192: aload #10
      //   194: ifnull -> 268
      //   197: aload #9
      //   199: getfield out : D
      //   202: dstore #14
      //   204: aload #10
      //   206: iload #6
      //   208: iload #4
      //   210: if_icmpne -> 218
      //   213: dload #14
      //   215: goto -> 228
      //   218: aload_1
      //   219: dload #12
      //   221: dload #14
      //   223: invokeinterface applyAsDouble : (DD)D
      //   228: putfield in : D
      //   231: aload #10
      //   233: invokevirtual getPendingCount : ()I
      //   236: dup
      //   237: istore #16
      //   239: iconst_1
      //   240: iand
      //   241: ifeq -> 247
      //   244: goto -> 268
      //   247: aload #10
      //   249: iload #16
      //   251: iload #16
      //   253: iconst_1
      //   254: ior
      //   255: invokevirtual compareAndSetPendingCount : (II)Z
      //   258: ifeq -> 231
      //   261: aload #10
      //   263: astore #8
      //   265: goto -> 268
      //   268: aload #9
      //   270: invokevirtual getPendingCount : ()I
      //   273: dup
      //   274: istore #14
      //   276: iconst_1
      //   277: iand
      //   278: ifeq -> 284
      //   281: goto -> 314
      //   284: aload #9
      //   286: iload #14
      //   288: iload #14
      //   290: iconst_1
      //   291: ior
      //   292: invokevirtual compareAndSetPendingCount : (II)Z
      //   295: ifeq -> 268
      //   298: aload #8
      //   300: ifnull -> 307
      //   303: aload #8
      //   305: astore #11
      //   307: aload #9
      //   309: astore #8
      //   311: goto -> 314
      //   314: aload #8
      //   316: ifnonnull -> 322
      //   319: goto -> 756
      //   322: aload #11
      //   324: ifnull -> 333
      //   327: aload #11
      //   329: invokevirtual fork : ()Ljava/util/concurrent/ForkJoinTask;
      //   332: pop
      //   333: goto -> 46
      //   336: aload #8
      //   338: invokevirtual getPendingCount : ()I
      //   341: dup
      //   342: istore #10
      //   344: iconst_4
      //   345: iand
      //   346: ifeq -> 352
      //   349: goto -> 756
      //   352: iload #10
      //   354: iconst_1
      //   355: iand
      //   356: ifeq -> 363
      //   359: iconst_4
      //   360: goto -> 376
      //   363: iload #6
      //   365: iload #4
      //   367: if_icmple -> 374
      //   370: iconst_2
      //   371: goto -> 376
      //   374: bipush #6
      //   376: istore #9
      //   378: aload #8
      //   380: iload #10
      //   382: iload #10
      //   384: iload #9
      //   386: ior
      //   387: invokevirtual compareAndSetPendingCount : (II)Z
      //   390: ifeq -> 336
      //   393: goto -> 396
      //   396: iload #9
      //   398: iconst_2
      //   399: if_icmpeq -> 474
      //   402: iload #6
      //   404: iload #4
      //   406: if_icmpne -> 424
      //   409: aload_2
      //   410: iload #4
      //   412: daload
      //   413: dstore #10
      //   415: iload #4
      //   417: iconst_1
      //   418: iadd
      //   419: istore #12
      //   421: goto -> 435
      //   424: aload #8
      //   426: getfield in : D
      //   429: dstore #10
      //   431: iload #6
      //   433: istore #12
      //   435: iload #12
      //   437: istore #13
      //   439: iload #13
      //   441: iload #7
      //   443: if_icmpge -> 471
      //   446: aload_2
      //   447: iload #13
      //   449: aload_1
      //   450: dload #10
      //   452: aload_2
      //   453: iload #13
      //   455: daload
      //   456: invokeinterface applyAsDouble : (DD)D
      //   461: dup2
      //   462: dstore #10
      //   464: dastore
      //   465: iinc #13, 1
      //   468: goto -> 439
      //   471: goto -> 530
      //   474: iload #7
      //   476: iload #5
      //   478: if_icmpge -> 523
      //   481: aload_2
      //   482: iload #6
      //   484: daload
      //   485: dstore #10
      //   487: iload #6
      //   489: iconst_1
      //   490: iadd
      //   491: istore #12
      //   493: iload #12
      //   495: iload #7
      //   497: if_icmpge -> 520
      //   500: aload_1
      //   501: dload #10
      //   503: aload_2
      //   504: iload #12
      //   506: daload
      //   507: invokeinterface applyAsDouble : (DD)D
      //   512: dstore #10
      //   514: iinc #12, 1
      //   517: goto -> 493
      //   520: goto -> 530
      //   523: aload #8
      //   525: getfield in : D
      //   528: dstore #10
      //   530: aload #8
      //   532: dload #10
      //   534: putfield out : D
      //   537: aload #8
      //   539: invokevirtual getCompleter : ()Ljava/util/concurrent/CountedCompleter;
      //   542: checkcast java/util/ArrayPrefixHelpers$DoubleCumulateTask
      //   545: dup
      //   546: astore #12
      //   548: ifnonnull -> 566
      //   551: iload #9
      //   553: iconst_4
      //   554: iand
      //   555: ifeq -> 756
      //   558: aload #8
      //   560: invokevirtual quietlyComplete : ()V
      //   563: goto -> 756
      //   566: aload #12
      //   568: invokevirtual getPendingCount : ()I
      //   571: istore #13
      //   573: iload #13
      //   575: iload #9
      //   577: iand
      //   578: iconst_4
      //   579: iand
      //   580: ifeq -> 590
      //   583: aload #12
      //   585: astore #8
      //   587: goto -> 753
      //   590: iload #13
      //   592: iload #9
      //   594: iand
      //   595: iconst_2
      //   596: iand
      //   597: ifeq -> 735
      //   600: aload #12
      //   602: getfield left : Ljava/util/ArrayPrefixHelpers$DoubleCumulateTask;
      //   605: dup
      //   606: astore #15
      //   608: ifnull -> 662
      //   611: aload #12
      //   613: getfield right : Ljava/util/ArrayPrefixHelpers$DoubleCumulateTask;
      //   616: dup
      //   617: astore #16
      //   619: ifnull -> 662
      //   622: aload #15
      //   624: getfield out : D
      //   627: dstore #17
      //   629: aload #12
      //   631: aload #16
      //   633: getfield hi : I
      //   636: iload #5
      //   638: if_icmpne -> 646
      //   641: dload #17
      //   643: goto -> 659
      //   646: aload_1
      //   647: dload #17
      //   649: aload #16
      //   651: getfield out : D
      //   654: invokeinterface applyAsDouble : (DD)D
      //   659: putfield out : D
      //   662: iload #13
      //   664: iconst_1
      //   665: iand
      //   666: ifne -> 683
      //   669: aload #12
      //   671: getfield lo : I
      //   674: iload #4
      //   676: if_icmpne -> 683
      //   679: iconst_1
      //   680: goto -> 684
      //   683: iconst_0
      //   684: istore #17
      //   686: iload #13
      //   688: iload #9
      //   690: ior
      //   691: iload #17
      //   693: ior
      //   694: dup
      //   695: istore #14
      //   697: iload #13
      //   699: if_icmpeq -> 714
      //   702: aload #12
      //   704: iload #13
      //   706: iload #14
      //   708: invokevirtual compareAndSetPendingCount : (II)Z
      //   711: ifeq -> 732
      //   714: iconst_2
      //   715: istore #9
      //   717: aload #12
      //   719: astore #8
      //   721: iload #17
      //   723: ifeq -> 732
      //   726: aload #12
      //   728: invokevirtual fork : ()Ljava/util/concurrent/ForkJoinTask;
      //   731: pop
      //   732: goto -> 753
      //   735: aload #12
      //   737: iload #13
      //   739: iload #13
      //   741: iload #9
      //   743: ior
      //   744: invokevirtual compareAndSetPendingCount : (II)Z
      //   747: ifeq -> 753
      //   750: goto -> 756
      //   753: goto -> 537
      //   756: return }
  }
  
  static final class IntCumulateTask extends CountedCompleter<Void> {
    final int[] array;
    
    final IntBinaryOperator function;
    
    IntCumulateTask left;
    
    IntCumulateTask right;
    
    int in;
    
    int out;
    
    final int lo;
    
    final int hi;
    
    final int origin;
    
    final int fence;
    
    final int threshold;
    
    public IntCumulateTask(IntCumulateTask param1IntCumulateTask, IntBinaryOperator param1IntBinaryOperator, int[] param1ArrayOfInt, int param1Int1, int param1Int2) {
      super(param1IntCumulateTask);
      this.function = param1IntBinaryOperator;
      this.array = param1ArrayOfInt;
      this.lo = this.origin = param1Int1;
      this.hi = this.fence = param1Int2;
      int i;
      this.threshold = ((i = (param1Int2 - param1Int1) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16) ? 16 : i;
    }
    
    IntCumulateTask(IntCumulateTask param1IntCumulateTask, IntBinaryOperator param1IntBinaryOperator, int[] param1ArrayOfInt, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      super(param1IntCumulateTask);
      this.function = param1IntBinaryOperator;
      this.array = param1ArrayOfInt;
      this.origin = param1Int1;
      this.fence = param1Int2;
      this.threshold = param1Int3;
      this.lo = param1Int4;
      this.hi = param1Int5;
    }
    
    public final void compute() {
      IntBinaryOperator intBinaryOperator;
      int[] arrayOfInt;
      if ((intBinaryOperator = this.function) == null || (arrayOfInt = this.array) == null)
        throw new NullPointerException(); 
      int i = this.threshold;
      int j = this.origin;
      int k = this.fence;
      IntCumulateTask intCumulateTask = this;
      int m;
      int n;
      while ((m = intCumulateTask.lo) >= 0 && (n = intCumulateTask.hi) <= arrayOfInt.length) {
        if (n - m > i) {
          IntCumulateTask intCumulateTask3;
          IntCumulateTask intCumulateTask1 = intCumulateTask.left;
          IntCumulateTask intCumulateTask2 = intCumulateTask.right;
          if (intCumulateTask1 == null) {
            int i2 = m + n >>> 1;
            intCumulateTask3 = intCumulateTask2 = intCumulateTask.right = new IntCumulateTask(intCumulateTask, intBinaryOperator, arrayOfInt, j, k, i, i2, n);
            intCumulateTask = intCumulateTask1 = intCumulateTask.left = new IntCumulateTask(intCumulateTask, intBinaryOperator, arrayOfInt, j, k, i, m, i2);
          } else {
            int i2 = intCumulateTask.in;
            intCumulateTask1.in = i2;
            intCumulateTask3 = intCumulateTask = null;
            if (intCumulateTask2 != null) {
              int i4 = intCumulateTask1.out;
              intCumulateTask2.in = (m == j) ? i4 : intBinaryOperator.applyAsInt(i2, i4);
              int i5;
              while (((i5 = intCumulateTask2.getPendingCount()) & true) == 0) {
                if (intCumulateTask2.compareAndSetPendingCount(i5, i5 | true)) {
                  intCumulateTask = intCumulateTask2;
                  break;
                } 
              } 
            } 
            int i3;
            while (((i3 = intCumulateTask1.getPendingCount()) & true) == 0) {
              if (intCumulateTask1.compareAndSetPendingCount(i3, i3 | true)) {
                if (intCumulateTask != null)
                  intCumulateTask3 = intCumulateTask; 
                intCumulateTask = intCumulateTask1;
                break;
              } 
            } 
            if (intCumulateTask == null)
              break; 
          } 
          if (intCumulateTask3 != null)
            intCumulateTask3.fork(); 
          continue;
        } 
        int i1;
        while (((i1 = intCumulateTask.getPendingCount()) & 0x4) == 0) {
          int i2 = ((i1 & true) != 0) ? 4 : ((m > j) ? 2 : 6);
          if (intCumulateTask.compareAndSetPendingCount(i1, i1 | i2)) {
            if (i2 != 2) {
              int i3;
              if (m == j) {
                i1 = arrayOfInt[j];
                i3 = j + 1;
              } else {
                i1 = intCumulateTask.in;
                i3 = m;
              } 
              for (int i4 = i3; i4 < n; i4++)
                arrayOfInt[i4] = i1 = intBinaryOperator.applyAsInt(i1, arrayOfInt[i4]); 
            } else if (n < k) {
              i1 = arrayOfInt[m];
              for (int i3 = m + 1; i3 < n; i3++)
                i1 = intBinaryOperator.applyAsInt(i1, arrayOfInt[i3]); 
            } else {
              i1 = intCumulateTask.in;
            } 
            intCumulateTask.out = i1;
            while (true) {
              IntCumulateTask intCumulateTask1;
              if ((intCumulateTask1 = (IntCumulateTask)intCumulateTask.getCompleter()) == null) {
                if ((i2 & 0x4) != 0)
                  intCumulateTask.quietlyComplete(); 
                break;
              } 
              int i3 = intCumulateTask1.getPendingCount();
              if ((i3 & i2 & 0x4) != 0) {
                intCumulateTask = intCumulateTask1;
                continue;
              } 
              if ((i3 & i2 & 0x2) != 0) {
                IntCumulateTask intCumulateTask2;
                IntCumulateTask intCumulateTask3;
                if ((intCumulateTask2 = intCumulateTask1.left) != null && (intCumulateTask3 = intCumulateTask1.right) != null) {
                  int i6 = intCumulateTask2.out;
                  intCumulateTask1.out = (intCumulateTask3.hi == k) ? i6 : intBinaryOperator.applyAsInt(i6, intCumulateTask3.out);
                } 
                int i5 = ((i3 & true) == 0 && intCumulateTask1.lo == j) ? 1 : 0;
                int i4;
                if ((i4 = i3 | i2 | i5) == i3 || intCumulateTask1.compareAndSetPendingCount(i3, i4)) {
                  i2 = 2;
                  intCumulateTask = intCumulateTask1;
                  if (i5 != 0)
                    intCumulateTask1.fork(); 
                } 
                continue;
              } 
              if (intCumulateTask1.compareAndSetPendingCount(i3, i3 | i2))
                break; 
            } 
            break;
          } 
        } 
      } 
    }
  }
  
  static final class LongCumulateTask extends CountedCompleter<Void> {
    final long[] array;
    
    final LongBinaryOperator function;
    
    LongCumulateTask left;
    
    LongCumulateTask right;
    
    long in;
    
    long out;
    
    final int lo;
    
    final int hi;
    
    final int origin;
    
    final int fence;
    
    final int threshold;
    
    public LongCumulateTask(LongCumulateTask param1LongCumulateTask, LongBinaryOperator param1LongBinaryOperator, long[] param1ArrayOfLong, int param1Int1, int param1Int2) {
      super(param1LongCumulateTask);
      this.function = param1LongBinaryOperator;
      this.array = param1ArrayOfLong;
      this.lo = this.origin = param1Int1;
      this.hi = this.fence = param1Int2;
      int i;
      this.threshold = ((i = (param1Int2 - param1Int1) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16) ? 16 : i;
    }
    
    LongCumulateTask(LongCumulateTask param1LongCumulateTask, LongBinaryOperator param1LongBinaryOperator, long[] param1ArrayOfLong, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      super(param1LongCumulateTask);
      this.function = param1LongBinaryOperator;
      this.array = param1ArrayOfLong;
      this.origin = param1Int1;
      this.fence = param1Int2;
      this.threshold = param1Int3;
      this.lo = param1Int4;
      this.hi = param1Int5;
    }
    
    public final void compute() { // Byte code:
      //   0: aload_0
      //   1: getfield function : Ljava/util/function/LongBinaryOperator;
      //   4: dup
      //   5: astore_1
      //   6: ifnull -> 18
      //   9: aload_0
      //   10: getfield array : [J
      //   13: dup
      //   14: astore_2
      //   15: ifnonnull -> 26
      //   18: new java/lang/NullPointerException
      //   21: dup
      //   22: invokespecial <init> : ()V
      //   25: athrow
      //   26: aload_0
      //   27: getfield threshold : I
      //   30: istore_3
      //   31: aload_0
      //   32: getfield origin : I
      //   35: istore #4
      //   37: aload_0
      //   38: getfield fence : I
      //   41: istore #5
      //   43: aload_0
      //   44: astore #8
      //   46: aload #8
      //   48: getfield lo : I
      //   51: dup
      //   52: istore #6
      //   54: iflt -> 756
      //   57: aload #8
      //   59: getfield hi : I
      //   62: dup
      //   63: istore #7
      //   65: aload_2
      //   66: arraylength
      //   67: if_icmpgt -> 756
      //   70: iload #7
      //   72: iload #6
      //   74: isub
      //   75: iload_3
      //   76: if_icmple -> 336
      //   79: aload #8
      //   81: getfield left : Ljava/util/ArrayPrefixHelpers$LongCumulateTask;
      //   84: astore #9
      //   86: aload #8
      //   88: getfield right : Ljava/util/ArrayPrefixHelpers$LongCumulateTask;
      //   91: astore #10
      //   93: aload #9
      //   95: ifnonnull -> 172
      //   98: iload #6
      //   100: iload #7
      //   102: iadd
      //   103: iconst_1
      //   104: iushr
      //   105: istore #12
      //   107: aload #8
      //   109: new java/util/ArrayPrefixHelpers$LongCumulateTask
      //   112: dup
      //   113: aload #8
      //   115: aload_1
      //   116: aload_2
      //   117: iload #4
      //   119: iload #5
      //   121: iload_3
      //   122: iload #12
      //   124: iload #7
      //   126: invokespecial <init> : (Ljava/util/ArrayPrefixHelpers$LongCumulateTask;Ljava/util/function/LongBinaryOperator;[JIIIII)V
      //   129: dup_x1
      //   130: putfield right : Ljava/util/ArrayPrefixHelpers$LongCumulateTask;
      //   133: dup
      //   134: astore #10
      //   136: astore #11
      //   138: aload #8
      //   140: new java/util/ArrayPrefixHelpers$LongCumulateTask
      //   143: dup
      //   144: aload #8
      //   146: aload_1
      //   147: aload_2
      //   148: iload #4
      //   150: iload #5
      //   152: iload_3
      //   153: iload #6
      //   155: iload #12
      //   157: invokespecial <init> : (Ljava/util/ArrayPrefixHelpers$LongCumulateTask;Ljava/util/function/LongBinaryOperator;[JIIIII)V
      //   160: dup_x1
      //   161: putfield left : Ljava/util/ArrayPrefixHelpers$LongCumulateTask;
      //   164: dup
      //   165: astore #9
      //   167: astore #8
      //   169: goto -> 322
      //   172: aload #8
      //   174: getfield in : J
      //   177: lstore #12
      //   179: aload #9
      //   181: lload #12
      //   183: putfield in : J
      //   186: aconst_null
      //   187: dup
      //   188: astore #8
      //   190: astore #11
      //   192: aload #10
      //   194: ifnull -> 268
      //   197: aload #9
      //   199: getfield out : J
      //   202: lstore #14
      //   204: aload #10
      //   206: iload #6
      //   208: iload #4
      //   210: if_icmpne -> 218
      //   213: lload #14
      //   215: goto -> 228
      //   218: aload_1
      //   219: lload #12
      //   221: lload #14
      //   223: invokeinterface applyAsLong : (JJ)J
      //   228: putfield in : J
      //   231: aload #10
      //   233: invokevirtual getPendingCount : ()I
      //   236: dup
      //   237: istore #16
      //   239: iconst_1
      //   240: iand
      //   241: ifeq -> 247
      //   244: goto -> 268
      //   247: aload #10
      //   249: iload #16
      //   251: iload #16
      //   253: iconst_1
      //   254: ior
      //   255: invokevirtual compareAndSetPendingCount : (II)Z
      //   258: ifeq -> 231
      //   261: aload #10
      //   263: astore #8
      //   265: goto -> 268
      //   268: aload #9
      //   270: invokevirtual getPendingCount : ()I
      //   273: dup
      //   274: istore #14
      //   276: iconst_1
      //   277: iand
      //   278: ifeq -> 284
      //   281: goto -> 314
      //   284: aload #9
      //   286: iload #14
      //   288: iload #14
      //   290: iconst_1
      //   291: ior
      //   292: invokevirtual compareAndSetPendingCount : (II)Z
      //   295: ifeq -> 268
      //   298: aload #8
      //   300: ifnull -> 307
      //   303: aload #8
      //   305: astore #11
      //   307: aload #9
      //   309: astore #8
      //   311: goto -> 314
      //   314: aload #8
      //   316: ifnonnull -> 322
      //   319: goto -> 756
      //   322: aload #11
      //   324: ifnull -> 333
      //   327: aload #11
      //   329: invokevirtual fork : ()Ljava/util/concurrent/ForkJoinTask;
      //   332: pop
      //   333: goto -> 46
      //   336: aload #8
      //   338: invokevirtual getPendingCount : ()I
      //   341: dup
      //   342: istore #10
      //   344: iconst_4
      //   345: iand
      //   346: ifeq -> 352
      //   349: goto -> 756
      //   352: iload #10
      //   354: iconst_1
      //   355: iand
      //   356: ifeq -> 363
      //   359: iconst_4
      //   360: goto -> 376
      //   363: iload #6
      //   365: iload #4
      //   367: if_icmple -> 374
      //   370: iconst_2
      //   371: goto -> 376
      //   374: bipush #6
      //   376: istore #9
      //   378: aload #8
      //   380: iload #10
      //   382: iload #10
      //   384: iload #9
      //   386: ior
      //   387: invokevirtual compareAndSetPendingCount : (II)Z
      //   390: ifeq -> 336
      //   393: goto -> 396
      //   396: iload #9
      //   398: iconst_2
      //   399: if_icmpeq -> 474
      //   402: iload #6
      //   404: iload #4
      //   406: if_icmpne -> 424
      //   409: aload_2
      //   410: iload #4
      //   412: laload
      //   413: lstore #10
      //   415: iload #4
      //   417: iconst_1
      //   418: iadd
      //   419: istore #12
      //   421: goto -> 435
      //   424: aload #8
      //   426: getfield in : J
      //   429: lstore #10
      //   431: iload #6
      //   433: istore #12
      //   435: iload #12
      //   437: istore #13
      //   439: iload #13
      //   441: iload #7
      //   443: if_icmpge -> 471
      //   446: aload_2
      //   447: iload #13
      //   449: aload_1
      //   450: lload #10
      //   452: aload_2
      //   453: iload #13
      //   455: laload
      //   456: invokeinterface applyAsLong : (JJ)J
      //   461: dup2
      //   462: lstore #10
      //   464: lastore
      //   465: iinc #13, 1
      //   468: goto -> 439
      //   471: goto -> 530
      //   474: iload #7
      //   476: iload #5
      //   478: if_icmpge -> 523
      //   481: aload_2
      //   482: iload #6
      //   484: laload
      //   485: lstore #10
      //   487: iload #6
      //   489: iconst_1
      //   490: iadd
      //   491: istore #12
      //   493: iload #12
      //   495: iload #7
      //   497: if_icmpge -> 520
      //   500: aload_1
      //   501: lload #10
      //   503: aload_2
      //   504: iload #12
      //   506: laload
      //   507: invokeinterface applyAsLong : (JJ)J
      //   512: lstore #10
      //   514: iinc #12, 1
      //   517: goto -> 493
      //   520: goto -> 530
      //   523: aload #8
      //   525: getfield in : J
      //   528: lstore #10
      //   530: aload #8
      //   532: lload #10
      //   534: putfield out : J
      //   537: aload #8
      //   539: invokevirtual getCompleter : ()Ljava/util/concurrent/CountedCompleter;
      //   542: checkcast java/util/ArrayPrefixHelpers$LongCumulateTask
      //   545: dup
      //   546: astore #12
      //   548: ifnonnull -> 566
      //   551: iload #9
      //   553: iconst_4
      //   554: iand
      //   555: ifeq -> 756
      //   558: aload #8
      //   560: invokevirtual quietlyComplete : ()V
      //   563: goto -> 756
      //   566: aload #12
      //   568: invokevirtual getPendingCount : ()I
      //   571: istore #13
      //   573: iload #13
      //   575: iload #9
      //   577: iand
      //   578: iconst_4
      //   579: iand
      //   580: ifeq -> 590
      //   583: aload #12
      //   585: astore #8
      //   587: goto -> 753
      //   590: iload #13
      //   592: iload #9
      //   594: iand
      //   595: iconst_2
      //   596: iand
      //   597: ifeq -> 735
      //   600: aload #12
      //   602: getfield left : Ljava/util/ArrayPrefixHelpers$LongCumulateTask;
      //   605: dup
      //   606: astore #15
      //   608: ifnull -> 662
      //   611: aload #12
      //   613: getfield right : Ljava/util/ArrayPrefixHelpers$LongCumulateTask;
      //   616: dup
      //   617: astore #16
      //   619: ifnull -> 662
      //   622: aload #15
      //   624: getfield out : J
      //   627: lstore #17
      //   629: aload #12
      //   631: aload #16
      //   633: getfield hi : I
      //   636: iload #5
      //   638: if_icmpne -> 646
      //   641: lload #17
      //   643: goto -> 659
      //   646: aload_1
      //   647: lload #17
      //   649: aload #16
      //   651: getfield out : J
      //   654: invokeinterface applyAsLong : (JJ)J
      //   659: putfield out : J
      //   662: iload #13
      //   664: iconst_1
      //   665: iand
      //   666: ifne -> 683
      //   669: aload #12
      //   671: getfield lo : I
      //   674: iload #4
      //   676: if_icmpne -> 683
      //   679: iconst_1
      //   680: goto -> 684
      //   683: iconst_0
      //   684: istore #17
      //   686: iload #13
      //   688: iload #9
      //   690: ior
      //   691: iload #17
      //   693: ior
      //   694: dup
      //   695: istore #14
      //   697: iload #13
      //   699: if_icmpeq -> 714
      //   702: aload #12
      //   704: iload #13
      //   706: iload #14
      //   708: invokevirtual compareAndSetPendingCount : (II)Z
      //   711: ifeq -> 732
      //   714: iconst_2
      //   715: istore #9
      //   717: aload #12
      //   719: astore #8
      //   721: iload #17
      //   723: ifeq -> 732
      //   726: aload #12
      //   728: invokevirtual fork : ()Ljava/util/concurrent/ForkJoinTask;
      //   731: pop
      //   732: goto -> 753
      //   735: aload #12
      //   737: iload #13
      //   739: iload #13
      //   741: iload #9
      //   743: ior
      //   744: invokevirtual compareAndSetPendingCount : (II)Z
      //   747: ifeq -> 753
      //   750: goto -> 756
      //   753: goto -> 537
      //   756: return }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\ArrayPrefixHelpers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */