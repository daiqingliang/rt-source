package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

class CodingChooser {
  int verbose;
  
  int effort;
  
  boolean optUseHistogram = true;
  
  boolean optUsePopulationCoding = true;
  
  boolean optUseAdaptiveCoding = true;
  
  boolean disablePopCoding;
  
  boolean disableRunCoding;
  
  boolean topLevel = true;
  
  double fuzz;
  
  Coding[] allCodingChoices;
  
  Choice[] choices;
  
  ByteArrayOutputStream context;
  
  CodingChooser popHelper;
  
  CodingChooser runHelper;
  
  Random stress;
  
  private int[] values;
  
  private int start;
  
  private int end;
  
  private int[] deltas;
  
  private int min;
  
  private int max;
  
  private Histogram vHist;
  
  private Histogram dHist;
  
  private int searchOrder;
  
  private Choice regularChoice;
  
  private Choice bestChoice;
  
  private CodingMethod bestMethod;
  
  private int bestByteSize;
  
  private int bestZipSize;
  
  private int targetSize;
  
  public static final int MIN_EFFORT = 1;
  
  public static final int MID_EFFORT = 5;
  
  public static final int MAX_EFFORT = 9;
  
  public static final int POP_EFFORT = 4;
  
  public static final int RUN_EFFORT = 3;
  
  public static final int BYTE_SIZE = 0;
  
  public static final int ZIP_SIZE = 1;
  
  private Sizer zipSizer = new Sizer();
  
  private Deflater zipDef = new Deflater();
  
  private DeflaterOutputStream zipOut = new DeflaterOutputStream(this.zipSizer, this.zipDef);
  
  private Sizer byteSizer = new Sizer(this.zipOut);
  
  private Sizer byteOnlySizer = new Sizer();
  
  CodingChooser(int paramInt, Coding[] paramArrayOfCoding) {
    PropMap propMap = Utils.currentPropMap();
    if (propMap != null) {
      this.verbose = Math.max(propMap.getInteger("com.sun.java.util.jar.pack.verbose"), propMap.getInteger("com.sun.java.util.jar.pack.verbose.coding"));
      this.optUseHistogram = !propMap.getBoolean("com.sun.java.util.jar.pack.no.histogram");
      this.optUsePopulationCoding = !propMap.getBoolean("com.sun.java.util.jar.pack.no.population.coding");
      this.optUseAdaptiveCoding = !propMap.getBoolean("com.sun.java.util.jar.pack.no.adaptive.coding");
      int i = propMap.getInteger("com.sun.java.util.jar.pack.stress.coding");
      if (i != 0)
        this.stress = new Random(i); 
    } 
    this.effort = paramInt;
    this.allCodingChoices = paramArrayOfCoding;
    this.fuzz = 1.0D + 0.0025D * (paramInt - 5);
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < paramArrayOfCoding.length; b2++) {
      if (paramArrayOfCoding[b2] != null)
        b1++; 
    } 
    this.choices = new Choice[b1];
    b1 = 0;
    for (b2 = 0; b2 < paramArrayOfCoding.length; b2++) {
      if (paramArrayOfCoding[b2] != null) {
        int[] arrayOfInt = new int[this.choices.length];
        this.choices[b1++] = new Choice(paramArrayOfCoding[b2], b2, arrayOfInt);
      } 
    } 
    for (b2 = 0; b2 < this.choices.length; b2++) {
      Coding coding = (this.choices[b2]).coding;
      assert coding.distanceFrom(coding) == 0;
      for (byte b = 0; b < b2; b++) {
        Coding coding1 = (this.choices[b]).coding;
        int i = coding.distanceFrom(coding1);
        assert i > 0;
        assert i == coding1.distanceFrom(coding);
        (this.choices[b2]).distance[b] = i;
        (this.choices[b]).distance[b2] = i;
      } 
    } 
  }
  
  Choice makeExtraChoice(Coding paramCoding) {
    int[] arrayOfInt = new int[this.choices.length];
    for (byte b = 0; b < arrayOfInt.length; b++) {
      Coding coding = (this.choices[b]).coding;
      int i = paramCoding.distanceFrom(coding);
      assert i > 0;
      assert i == coding.distanceFrom(paramCoding);
      arrayOfInt[b] = i;
    } 
    Choice choice = new Choice(paramCoding, -1, arrayOfInt);
    choice.reset();
    return choice;
  }
  
  ByteArrayOutputStream getContext() {
    if (this.context == null)
      this.context = new ByteArrayOutputStream(65536); 
    return this.context;
  }
  
  private void reset(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    this.values = paramArrayOfInt;
    this.start = paramInt1;
    this.end = paramInt2;
    this.deltas = null;
    this.min = Integer.MAX_VALUE;
    this.max = Integer.MIN_VALUE;
    this.vHist = null;
    this.dHist = null;
    this.searchOrder = 0;
    this.regularChoice = null;
    this.bestChoice = null;
    this.bestMethod = null;
    this.bestZipSize = Integer.MAX_VALUE;
    this.bestByteSize = Integer.MAX_VALUE;
    this.targetSize = Integer.MAX_VALUE;
  }
  
  CodingMethod choose(int[] paramArrayOfInt1, int paramInt1, int paramInt2, Coding paramCoding, int[] paramArrayOfInt2) {
    reset(paramArrayOfInt1, paramInt1, paramInt2);
    if (this.effort <= 1 || paramInt1 >= paramInt2) {
      if (paramArrayOfInt2 != null) {
        int[] arrayOfInt = computeSizePrivate(paramCoding);
        paramArrayOfInt2[0] = arrayOfInt[0];
        paramArrayOfInt2[1] = arrayOfInt[1];
      } 
      return paramCoding;
    } 
    if (this.optUseHistogram) {
      getValueHistogram();
      getDeltaHistogram();
    } 
    int i;
    for (i = paramInt1; i < paramInt2; i++) {
      int i2 = paramArrayOfInt1[i];
      if (this.min > i2)
        this.min = i2; 
      if (this.max < i2)
        this.max = i2; 
    } 
    i = markUsableChoices(paramCoding);
    if (this.stress != null) {
      int i2 = this.stress.nextInt(i * 2 + 4);
      CodingMethod codingMethod1 = null;
      for (byte b = 0; b < this.choices.length; b++) {
        Choice choice = this.choices[b];
        if (choice.searchOrder >= 0 && i2-- == 0) {
          codingMethod1 = choice.coding;
          break;
        } 
      } 
      if (codingMethod1 == null)
        if ((i2 & 0x7) != 0) {
          codingMethod1 = paramCoding;
        } else {
          codingMethod1 = stressCoding(this.min, this.max);
        }  
      if (!this.disablePopCoding && this.optUsePopulationCoding && this.effort >= 4)
        codingMethod1 = stressPopCoding(codingMethod1); 
      if (!this.disableRunCoding && this.optUseAdaptiveCoding && this.effort >= 3)
        codingMethod1 = stressAdaptiveCoding(codingMethod1); 
      return codingMethod1;
    } 
    double d = 1.0D;
    int j;
    for (j = this.effort; j < 9; j++)
      d /= 1.414D; 
    j = (int)Math.ceil(i * d);
    this.bestChoice = this.regularChoice;
    evaluate(this.regularChoice);
    int k = updateDistances(this.regularChoice);
    int m = this.bestZipSize;
    int n = this.bestByteSize;
    if (this.regularChoice.coding == paramCoding && this.topLevel) {
      int i2 = BandStructure.encodeEscapeValue(115, paramCoding);
      if (paramCoding.canRepresentSigned(i2)) {
        int i3 = paramCoding.getLength(i2);
        this.regularChoice.zipSize -= i3;
        this.bestByteSize = this.regularChoice.byteSize;
        this.bestZipSize = this.regularChoice.zipSize;
      } 
    } 
    int i1 = 1;
    while (this.searchOrder < j) {
      if (i1 > k)
        i1 = 1; 
      int i2 = k / i1;
      int i3 = k / i1 *= 2 + 1;
      Choice choice = findChoiceNear(this.bestChoice, i2, i3);
      if (choice == null)
        continue; 
      assert choice.coding.canRepresent(this.min, this.max);
      evaluate(choice);
      int i4 = updateDistances(choice);
      if (choice == this.bestChoice) {
        k = i4;
        if (this.verbose > 5)
          Utils.log.info("maxd = " + k); 
      } 
    } 
    Coding coding = this.bestChoice.coding;
    assert coding == this.bestMethod;
    if (this.verbose > 2)
      Utils.log.info("chooser: plain result=" + this.bestChoice + " after " + this.bestChoice.searchOrder + " rounds, " + (this.regularChoice.zipSize - this.bestZipSize) + " fewer bytes than regular " + paramCoding); 
    this.bestChoice = null;
    if (!this.disablePopCoding && this.optUsePopulationCoding && this.effort >= 4 && this.bestMethod instanceof Coding)
      tryPopulationCoding(coding); 
    if (!this.disableRunCoding && this.optUseAdaptiveCoding && this.effort >= 3 && this.bestMethod instanceof Coding)
      tryAdaptiveCoding(coding); 
    if (paramArrayOfInt2 != null) {
      paramArrayOfInt2[0] = this.bestByteSize;
      paramArrayOfInt2[1] = this.bestZipSize;
    } 
    if (this.verbose > 1)
      Utils.log.info("chooser: result=" + this.bestMethod + " " + (m - this.bestZipSize) + " fewer bytes than regular " + paramCoding + "; win=" + pct((m - this.bestZipSize), m)); 
    CodingMethod codingMethod = this.bestMethod;
    reset(null, 0, 0);
    return codingMethod;
  }
  
  CodingMethod choose(int[] paramArrayOfInt, int paramInt1, int paramInt2, Coding paramCoding) { return choose(paramArrayOfInt, paramInt1, paramInt2, paramCoding, null); }
  
  CodingMethod choose(int[] paramArrayOfInt1, Coding paramCoding, int[] paramArrayOfInt2) { return choose(paramArrayOfInt1, 0, paramArrayOfInt1.length, paramCoding, paramArrayOfInt2); }
  
  CodingMethod choose(int[] paramArrayOfInt, Coding paramCoding) { return choose(paramArrayOfInt, 0, paramArrayOfInt.length, paramCoding, null); }
  
  private int markUsableChoices(Coding paramCoding) {
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < this.choices.length; b2++) {
      Choice choice = this.choices[b2];
      choice.reset();
      if (!choice.coding.canRepresent(this.min, this.max)) {
        choice.searchOrder = -1;
        if (this.verbose > 1 && choice.coding == paramCoding)
          Utils.log.info("regular coding cannot represent [" + this.min + ".." + this.max + "]: " + paramCoding); 
      } else {
        if (choice.coding == paramCoding)
          this.regularChoice = choice; 
        b1++;
      } 
    } 
    if (this.regularChoice == null && paramCoding.canRepresent(this.min, this.max)) {
      this.regularChoice = makeExtraChoice(paramCoding);
      if (this.verbose > 1)
        Utils.log.info("*** regular choice is extra: " + this.regularChoice.coding); 
    } 
    if (this.regularChoice == null) {
      for (b2 = 0; b2 < this.choices.length; b2++) {
        Choice choice = this.choices[b2];
        if (choice.searchOrder != -1) {
          this.regularChoice = choice;
          break;
        } 
      } 
      if (this.verbose > 1) {
        Utils.log.info("*** regular choice does not apply " + paramCoding);
        Utils.log.info("    using instead " + this.regularChoice.coding);
      } 
    } 
    if (this.verbose > 2) {
      Utils.log.info("chooser: #choices=" + b1 + " [" + this.min + ".." + this.max + "]");
      if (this.verbose > 4)
        for (b2 = 0; b2 < this.choices.length; b2++) {
          Choice choice = this.choices[b2];
          if (choice.searchOrder >= 0)
            Utils.log.info("  " + choice); 
        }  
    } 
    return b1;
  }
  
  private Choice findChoiceNear(Choice paramChoice, int paramInt1, int paramInt2) {
    if (this.verbose > 5)
      Utils.log.info("findChoice " + paramInt1 + ".." + paramInt2 + " near: " + paramChoice); 
    int[] arrayOfInt = paramChoice.distance;
    Choice choice = null;
    for (byte b = 0; b < this.choices.length; b++) {
      Choice choice1 = this.choices[b];
      if (choice1.searchOrder >= this.searchOrder && arrayOfInt[b] >= paramInt2 && arrayOfInt[b] <= paramInt1) {
        if (choice1.minDistance >= paramInt2 && choice1.minDistance <= paramInt1) {
          if (this.verbose > 5)
            Utils.log.info("findChoice => good " + choice1); 
          return choice1;
        } 
        choice = choice1;
      } 
    } 
    if (this.verbose > 5)
      Utils.log.info("findChoice => found " + choice); 
    return choice;
  }
  
  private void evaluate(Choice paramChoice) {
    boolean bool;
    assert paramChoice.searchOrder == Integer.MAX_VALUE;
    paramChoice.searchOrder = this.searchOrder++;
    if (paramChoice == this.bestChoice || paramChoice.isExtra()) {
      bool = true;
    } else if (this.optUseHistogram) {
      Histogram histogram = getHistogram(paramChoice.coding.isDelta());
      paramChoice.histSize = (int)Math.ceil(histogram.getBitLength(paramChoice.coding) / 8.0D);
      paramChoice.byteSize = paramChoice.histSize;
      bool = (paramChoice.byteSize <= this.targetSize) ? 1 : 0;
    } else {
      bool = true;
    } 
    if (bool) {
      int[] arrayOfInt = computeSizePrivate(paramChoice.coding);
      paramChoice.byteSize = arrayOfInt[0];
      paramChoice.zipSize = arrayOfInt[1];
      if (noteSizes(paramChoice.coding, paramChoice.byteSize, paramChoice.zipSize))
        this.bestChoice = paramChoice; 
    } 
    if (paramChoice.histSize >= 0 && !$assertionsDisabled && paramChoice.byteSize != paramChoice.histSize)
      throw new AssertionError(); 
    if (this.verbose > 4)
      Utils.log.info("evaluated " + paramChoice); 
  }
  
  private boolean noteSizes(CodingMethod paramCodingMethod, int paramInt1, int paramInt2) {
    assert paramInt2 > 0 && paramInt1 > 0;
    boolean bool = (paramInt2 < this.bestZipSize) ? 1 : 0;
    if (this.verbose > 3)
      Utils.log.info("computed size " + paramCodingMethod + " " + paramInt1 + "/zs=" + paramInt2 + ((bool && this.bestMethod != null) ? (" better by " + pct((this.bestZipSize - paramInt2), paramInt2)) : "")); 
    if (bool) {
      this.bestMethod = paramCodingMethod;
      this.bestZipSize = paramInt2;
      this.bestByteSize = paramInt1;
      this.targetSize = (int)(paramInt1 * this.fuzz);
      return true;
    } 
    return false;
  }
  
  private int updateDistances(Choice paramChoice) {
    int[] arrayOfInt = paramChoice.distance;
    int i = 0;
    for (byte b = 0; b < this.choices.length; b++) {
      Choice choice = this.choices[b];
      if (choice.searchOrder >= this.searchOrder) {
        int j = arrayOfInt[b];
        if (this.verbose > 5)
          Utils.log.info("evaluate dist " + j + " to " + choice); 
        int k = choice.minDistance;
        if (k > j)
          choice.minDistance = k = j; 
        if (i < j)
          i = j; 
      } 
    } 
    if (this.verbose > 5)
      Utils.log.info("evaluate maxd => " + i); 
    return i;
  }
  
  public void computeSize(CodingMethod paramCodingMethod, int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2) {
    if (paramInt2 <= paramInt1) {
      paramArrayOfInt2[1] = 0;
      paramArrayOfInt2[0] = 0;
      return;
    } 
    try {
      resetData();
      paramCodingMethod.writeArrayTo(this.byteSizer, paramArrayOfInt1, paramInt1, paramInt2);
      paramArrayOfInt2[0] = getByteSize();
      paramArrayOfInt2[1] = getZipSize();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public void computeSize(CodingMethod paramCodingMethod, int[] paramArrayOfInt1, int[] paramArrayOfInt2) { computeSize(paramCodingMethod, paramArrayOfInt1, 0, paramArrayOfInt1.length, paramArrayOfInt2); }
  
  public int[] computeSize(CodingMethod paramCodingMethod, int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int[] arrayOfInt = { 0, 0 };
    computeSize(paramCodingMethod, paramArrayOfInt, paramInt1, paramInt2, arrayOfInt);
    return arrayOfInt;
  }
  
  public int[] computeSize(CodingMethod paramCodingMethod, int[] paramArrayOfInt) { return computeSize(paramCodingMethod, paramArrayOfInt, 0, paramArrayOfInt.length); }
  
  private int[] computeSizePrivate(CodingMethod paramCodingMethod) {
    int[] arrayOfInt = { 0, 0 };
    computeSize(paramCodingMethod, this.values, this.start, this.end, arrayOfInt);
    return arrayOfInt;
  }
  
  public int computeByteSize(CodingMethod paramCodingMethod, int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i < 0)
      return 0; 
    if (paramCodingMethod instanceof Coding) {
      Coding coding = (Coding)paramCodingMethod;
      int j = coding.getLength(paramArrayOfInt, paramInt1, paramInt2);
      int k;
      assert j == (k = countBytesToSizer(paramCodingMethod, paramArrayOfInt, paramInt1, paramInt2)) : paramCodingMethod + " : " + j + " != " + k;
      return j;
    } 
    return countBytesToSizer(paramCodingMethod, paramArrayOfInt, paramInt1, paramInt2);
  }
  
  private int countBytesToSizer(CodingMethod paramCodingMethod, int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    try {
      this.byteOnlySizer.reset();
      paramCodingMethod.writeArrayTo(this.byteOnlySizer, paramArrayOfInt, paramInt1, paramInt2);
      return this.byteOnlySizer.getSize();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  int[] getDeltas(int paramInt1, int paramInt2) {
    if ((paramInt1 | paramInt2) != 0)
      return Coding.makeDeltas(this.values, this.start, this.end, paramInt1, paramInt2); 
    if (this.deltas == null)
      this.deltas = Coding.makeDeltas(this.values, this.start, this.end, 0, 0); 
    return this.deltas;
  }
  
  Histogram getValueHistogram() {
    if (this.vHist == null) {
      this.vHist = new Histogram(this.values, this.start, this.end);
      if (this.verbose > 3) {
        this.vHist.print("vHist", System.out);
      } else if (this.verbose > 1) {
        this.vHist.print("vHist", null, System.out);
      } 
    } 
    return this.vHist;
  }
  
  Histogram getDeltaHistogram() {
    if (this.dHist == null) {
      this.dHist = new Histogram(getDeltas(0, 0));
      if (this.verbose > 3) {
        this.dHist.print("dHist", System.out);
      } else if (this.verbose > 1) {
        this.dHist.print("dHist", null, System.out);
      } 
    } 
    return this.dHist;
  }
  
  Histogram getHistogram(boolean paramBoolean) { return paramBoolean ? getDeltaHistogram() : getValueHistogram(); }
  
  private void tryPopulationCoding(Coding paramCoding) {
    Histogram histogram = getValueHistogram();
    Coding coding1 = paramCoding.getValueCoding();
    Coding coding2 = BandStructure.UNSIGNED5.setL(64);
    Coding coding3 = paramCoding.getValueCoding();
    int i = 4 + Math.max(coding1.getLength(this.min), coding1.getLength(this.max));
    int m = coding2.getLength(0);
    int j = m * (this.end - this.start);
    int k = (int)Math.ceil(histogram.getBitLength(coding3) / 8.0D);
    int n = i + j + k;
    byte b1 = 0;
    int[] arrayOfInt1 = new int[1 + histogram.getTotalLength()];
    byte b2 = -1;
    byte b3 = -1;
    int[][] arrayOfInt = histogram.getMatrix();
    byte b4 = -1;
    int i1 = 1;
    int i2 = 0;
    int i3;
    for (i3 = 1; i3 <= histogram.getTotalLength(); i3++) {
      if (i1 == 1) {
        i2 = arrayOfInt[++b4][0];
        i1 = arrayOfInt[b4].length;
      } 
      int i5 = arrayOfInt[b4][--i1];
      arrayOfInt1[i3] = i5;
      int i6 = coding1.getLength(i5);
      i += i6;
      int i7 = i2;
      byte b = i3;
      j += (coding2.getLength(b) - m) * i7;
      k -= i6 * i7;
      int i8 = i + j + k;
      if (n > i8) {
        if (i8 <= this.targetSize) {
          b3 = i3;
          if (b2 < 0)
            b2 = i3; 
          if (this.verbose > 4)
            Utils.log.info("better pop-size at fvc=" + i3 + " by " + pct((n - i8), n)); 
        } 
        n = i8;
        b1 = i3;
      } 
    } 
    if (b2 < 0) {
      if (this.verbose > 1 && this.verbose > 1)
        Utils.log.info("no good pop-size; best was " + n + " at " + b1 + " worse by " + pct((n - this.bestByteSize), this.bestByteSize)); 
      return;
    } 
    if (this.verbose > 1)
      Utils.log.info("initial best pop-size at fvc=" + b1 + " in [" + b2 + ".." + b3 + "] by " + pct((this.bestByteSize - n), this.bestByteSize)); 
    i3 = this.bestZipSize;
    int[] arrayOfInt2 = PopulationCoding.LValuesCoded;
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    ArrayList arrayList3 = new ArrayList();
    if (b1 <= 255) {
      arrayList1.add(BandStructure.BYTE1);
    } else {
      int i5 = 5;
      boolean bool = (this.effort > 4) ? 1 : 0;
      if (bool)
        arrayList2.add(BandStructure.BYTE1.setS(1)); 
      for (int i6 = arrayOfInt2.length - 1; i6 >= 1; i6--) {
        int i7 = arrayOfInt2[i6];
        Coding coding4 = PopulationCoding.fitTokenCoding(b2, i7);
        Coding coding5 = PopulationCoding.fitTokenCoding(b1, i7);
        Coding coding6 = PopulationCoding.fitTokenCoding(b3, i7);
        if (coding5 != null) {
          if (!arrayList1.contains(coding5))
            arrayList1.add(coding5); 
          if (i5 > coding5.B())
            i5 = coding5.B(); 
        } 
        if (bool) {
          if (coding6 == null)
            coding6 = coding5; 
          for (int i8 = coding4.B(); i8 <= coding6.B(); i8++) {
            if (i8 != coding5.B() && i8 != 1) {
              Coding coding = coding6.setB(i8).setS(1);
              if (!arrayList2.contains(coding))
                arrayList2.add(coding); 
            } 
          } 
        } 
      } 
      Iterator iterator = arrayList1.iterator();
      while (iterator.hasNext()) {
        Coding coding = (Coding)iterator.next();
        if (coding.B() > i5) {
          iterator.remove();
          arrayList3.add(0, coding);
        } 
      } 
    } 
    ArrayList arrayList4 = new ArrayList();
    Iterator iterator1 = arrayList1.iterator();
    null = arrayList2.iterator();
    Iterator iterator2 = arrayList3.iterator();
    while (iterator1.hasNext() || null.hasNext() || iterator2.hasNext()) {
      if (iterator1.hasNext())
        arrayList4.add(iterator1.next()); 
      if (null.hasNext())
        arrayList4.add(null.next()); 
      if (iterator2.hasNext())
        arrayList4.add(iterator2.next()); 
    } 
    arrayList1.clear();
    arrayList2.clear();
    arrayList3.clear();
    int i4 = arrayList4.size();
    if (this.effort == 4) {
      i4 = 2;
    } else if (i4 > 4) {
      i4 -= 4;
      i4 = i4 * (this.effort - 4) / 5;
      i4 += 4;
    } 
    if (arrayList4.size() > i4) {
      if (this.verbose > 4)
        Utils.log.info("allFits before clip: " + arrayList4); 
      arrayList4.subList(i4, arrayList4.size()).clear();
    } 
    if (this.verbose > 3)
      Utils.log.info("allFits: " + arrayList4); 
    for (Coding coding : arrayList4) {
      int i5;
      boolean bool = false;
      if (coding.S() == 1) {
        bool = true;
        coding = coding.setS(0);
      } 
      if (!bool) {
        i5 = b1;
        assert coding.umax() >= i5;
        assert coding.B() == 1 || coding.setB(coding.B() - 1).umax() < i5;
      } else {
        i5 = Math.min(coding.umax(), b3);
        if (i5 < b2 || i5 == b1)
          continue; 
      } 
      PopulationCoding populationCoding = new PopulationCoding();
      populationCoding.setHistogram(histogram);
      populationCoding.setL(coding.L());
      populationCoding.setFavoredValues(arrayOfInt1, i5);
      assert populationCoding.tokenCoding == coding;
      populationCoding.resortFavoredValues();
      int[] arrayOfInt3 = computePopSizePrivate(populationCoding, coding1, coding3);
      noteSizes(populationCoding, arrayOfInt3[0], 4 + arrayOfInt3[1]);
    } 
    if (this.verbose > 3) {
      Utils.log.info("measured best pop, size=" + this.bestByteSize + "/zs=" + this.bestZipSize + " better by " + pct((i3 - this.bestZipSize), i3));
      if (this.bestZipSize < i3)
        Utils.log.info(">>> POP WINS BY " + (i3 - this.bestZipSize)); 
    } 
  }
  
  private int[] computePopSizePrivate(PopulationCoding paramPopulationCoding, Coding paramCoding1, Coding paramCoding2) {
    int[] arrayOfInt4;
    if (this.popHelper == null) {
      this.popHelper = new CodingChooser(this.effort, this.allCodingChoices);
      if (this.stress != null)
        this.popHelper.addStressSeed(this.stress.nextInt()); 
      this.popHelper.topLevel = false;
      this.popHelper.verbose--;
      this.popHelper.disablePopCoding = true;
      this.popHelper.disableRunCoding = this.disableRunCoding;
      if (this.effort < 5)
        this.popHelper.disableRunCoding = true; 
    } 
    int i = paramPopulationCoding.fVlen;
    if (this.verbose > 2) {
      Utils.log.info("computePopSizePrivate fvlen=" + i + " tc=" + paramPopulationCoding.tokenCoding);
      Utils.log.info("{ //BEGIN");
    } 
    int[] arrayOfInt1 = paramPopulationCoding.fValues;
    int[][] arrayOfInt = paramPopulationCoding.encodeValues(this.values, this.start, this.end);
    int[] arrayOfInt2 = arrayOfInt[0];
    int[] arrayOfInt3 = arrayOfInt[1];
    if (this.verbose > 2)
      Utils.log.info("-- refine on fv[" + i + "] fc=" + paramCoding1); 
    paramPopulationCoding.setFavoredCoding(this.popHelper.choose(arrayOfInt1, 1, 1 + i, paramCoding1));
    if (paramPopulationCoding.tokenCoding instanceof Coding && (this.stress == null || this.stress.nextBoolean())) {
      if (this.verbose > 2)
        Utils.log.info("-- refine on tv[" + arrayOfInt2.length + "] tc=" + paramPopulationCoding.tokenCoding); 
      arrayOfInt4 = this.popHelper.choose(arrayOfInt2, (Coding)paramPopulationCoding.tokenCoding);
      if (arrayOfInt4 != paramPopulationCoding.tokenCoding) {
        if (this.verbose > 2)
          Utils.log.info(">>> refined tc=" + arrayOfInt4); 
        paramPopulationCoding.setTokenCoding(arrayOfInt4);
      } 
    } 
    if (arrayOfInt3.length == 0) {
      paramPopulationCoding.setUnfavoredCoding(null);
    } else {
      if (this.verbose > 2)
        Utils.log.info("-- refine on uv[" + arrayOfInt3.length + "] uc=" + paramPopulationCoding.unfavoredCoding); 
      paramPopulationCoding.setUnfavoredCoding(this.popHelper.choose(arrayOfInt3, paramCoding2));
    } 
    if (this.verbose > 3) {
      Utils.log.info("finish computePopSizePrivate fvlen=" + i + " fc=" + paramPopulationCoding.favoredCoding + " tc=" + paramPopulationCoding.tokenCoding + " uc=" + paramPopulationCoding.unfavoredCoding);
      arrayOfInt4 = new StringBuilder();
      arrayOfInt4.append("fv = {");
      for (byte b = 1; b <= i; b++) {
        if (b % 10 == 0)
          arrayOfInt4.append('\n'); 
        arrayOfInt4.append(" ").append(arrayOfInt1[b]);
      } 
      arrayOfInt4.append('\n');
      arrayOfInt4.append("}");
      Utils.log.info(arrayOfInt4.toString());
    } 
    if (this.verbose > 2)
      Utils.log.info("} //END"); 
    if (this.stress != null)
      return null; 
    try {
      resetData();
      paramPopulationCoding.writeSequencesTo(this.byteSizer, arrayOfInt2, arrayOfInt3);
      arrayOfInt4 = new int[] { getByteSize(), getZipSize() };
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    int[] arrayOfInt5 = null;
    assert (arrayOfInt5 = computeSizePrivate(paramPopulationCoding)) != null;
    assert arrayOfInt5[0] == arrayOfInt4[0] : arrayOfInt5[false] + " != " + arrayOfInt4[false];
    return arrayOfInt4;
  }
  
  private void tryAdaptiveCoding(Coding paramCoding) {
    double d2;
    int i = this.bestZipSize;
    int j = this.start;
    int k = this.end;
    int[] arrayOfInt1 = this.values;
    int m = k - j;
    if (paramCoding.isDelta()) {
      arrayOfInt1 = getDeltas(0, 0);
      j = 0;
      k = arrayOfInt1.length;
    } 
    int[] arrayOfInt2 = new int[m + 1];
    byte b1 = 0;
    int n = 0;
    for (int i1 = j; i1 < k; i1++) {
      int i3 = arrayOfInt1[i1];
      arrayOfInt2[b1++] = n;
      d2 = paramCoding.getLength(i3);
      assert d2 < Integer.MAX_VALUE;
      n += d2;
    } 
    arrayOfInt2[b1++] = n;
    assert b1 == arrayOfInt2.length;
    double d1 = n / m;
    if (this.effort >= 5) {
      if (this.effort > 6) {
        d2 = 1.001D;
      } else {
        d2 = 1.003D;
      } 
    } else if (this.effort > 3) {
      d2 = 1.01D;
    } else {
      d2 = 1.03D;
    } 
    d2 *= d2;
    double d3 = d2 * d2;
    double d4 = d2 * d2 * d2;
    double[] arrayOfDouble1 = new double[1 + this.effort - 3];
    double d5 = Math.log(m);
    for (byte b2 = 0; b2 < arrayOfDouble1.length; b2++)
      arrayOfDouble1[b2] = Math.exp(d5 * (b2 + true) / (arrayOfDouble1.length + 1)); 
    int[] arrayOfInt3 = new int[arrayOfDouble1.length];
    byte b3 = 0;
    for (byte b4 = 0; b4 < arrayOfDouble1.length; b4++) {
      int i3 = (int)Math.round(arrayOfDouble1[b4]);
      i3 = AdaptiveCoding.getNextK(i3 - 1);
      if (i3 > 0 && i3 < m && (!b3 || i3 != arrayOfInt3[b3 - true]))
        arrayOfInt3[b3++] = i3; 
    } 
    arrayOfInt3 = BandStructure.realloc(arrayOfInt3, b3);
    int[] arrayOfInt4 = new int[arrayOfInt3.length];
    double[] arrayOfDouble2 = new double[arrayOfInt3.length];
    int i2;
    for (i2 = 0; i2 < arrayOfInt3.length; i2++) {
      double d;
      int i3 = arrayOfInt3[i2];
      if (i3 < 10) {
        d = d4;
      } else if (i3 < 100) {
        d = d3;
      } else {
        d = d2;
      } 
      arrayOfDouble2[i2] = d;
      arrayOfInt4[i2] = 4 + (int)Math.ceil(i3 * d1 * d);
    } 
    if (this.verbose > 1) {
      System.out.print("tryAdaptiveCoding [" + m + "] avgS=" + d1 + " fuzz=" + d2 + " meshes: {");
      for (i2 = 0; i2 < arrayOfInt3.length; i2++)
        System.out.print(" " + arrayOfInt3[i2] + "(" + arrayOfInt4[i2] + ")"); 
      Utils.log.info(" }");
    } 
    if (this.runHelper == null) {
      this.runHelper = new CodingChooser(this.effort, this.allCodingChoices);
      if (this.stress != null)
        this.runHelper.addStressSeed(this.stress.nextInt()); 
      this.runHelper.topLevel = false;
      this.runHelper.verbose--;
      this.runHelper.disableRunCoding = true;
      this.runHelper.disablePopCoding = this.disablePopCoding;
      if (this.effort < 5)
        this.runHelper.disablePopCoding = true; 
    } 
    for (i2 = 0; i2 < m; i2++) {
      i2 = AdaptiveCoding.getNextK(i2 - 1);
      if (i2 > m)
        i2 = m; 
      for (int i3 = arrayOfInt3.length - 1; i3 >= 0; i3--) {
        int i4 = arrayOfInt3[i3];
        int i5 = arrayOfInt4[i3];
        if (i2 + i4 <= m) {
          int i6 = arrayOfInt2[i2 + i4] - arrayOfInt2[i2];
          if (i6 >= i5) {
            CodingMethod codingMethod3;
            CodingMethod codingMethod1;
            int i7 = i2 + i4;
            int i8 = i6;
            double d = d1 * arrayOfDouble2[i3];
            while (i7 < m && i7 - i2 <= m / 2) {
              int i10 = i7;
              int i11 = i8;
              i7 += i4;
              i7 = i2 + AdaptiveCoding.getNextK(i7 - i2 - 1);
              if (i7 < 0 || i7 > m)
                i7 = m; 
              i8 = arrayOfInt2[i7] - arrayOfInt2[i2];
              if (i8 < 4.0D + (i7 - i2) * d) {
                i8 = i11;
                i7 = i10;
                break;
              } 
            } 
            int i9 = i7;
            if (this.verbose > 2) {
              Utils.log.info("bulge at " + i2 + "[" + (i7 - i2) + "] of " + pct(i8 - d1 * (i7 - i2), d1 * (i7 - i2)));
              Utils.log.info("{ //BEGIN");
            } 
            CodingMethod codingMethod2 = this.runHelper.choose(this.values, this.start + i2, this.start + i7, paramCoding);
            if (codingMethod2 == paramCoding) {
              codingMethod1 = paramCoding;
              codingMethod3 = paramCoding;
            } else {
              codingMethod1 = this.runHelper.choose(this.values, this.start, this.start + i2, paramCoding);
              codingMethod3 = this.runHelper.choose(this.values, this.start + i7, this.start + m, paramCoding);
            } 
            if (this.verbose > 2)
              Utils.log.info("} //END"); 
            if (codingMethod1 == codingMethod2 && i2 > 0 && AdaptiveCoding.isCodableLength(i7))
              i2 = 0; 
            if (codingMethod2 == codingMethod3 && i7 < m)
              i7 = m; 
            if (codingMethod1 != paramCoding || codingMethod2 != paramCoding || codingMethod3 != paramCoding) {
              AdaptiveCoding adaptiveCoding;
              int i10 = 0;
              if (i7 == m) {
                adaptiveCoding = codingMethod2;
              } else {
                adaptiveCoding = new AdaptiveCoding(i7 - i2, codingMethod2, codingMethod3);
                i10 += true;
              } 
              if (i2 > 0) {
                adaptiveCoding = new AdaptiveCoding(i2, codingMethod1, adaptiveCoding);
                i10 += true;
              } 
              int[] arrayOfInt = computeSizePrivate(adaptiveCoding);
              noteSizes(adaptiveCoding, arrayOfInt[0], arrayOfInt[1] + i10);
            } 
            i2 = i9;
            break;
          } 
        } 
      } 
    } 
    if (this.verbose > 3 && this.bestZipSize < i)
      Utils.log.info(">>> RUN WINS BY " + (i - this.bestZipSize)); 
  }
  
  private static String pct(double paramDouble1, double paramDouble2) { return (Math.round(paramDouble1 / paramDouble2 * 10000.0D) / 100.0D) + "%"; }
  
  private void resetData() {
    flushData();
    this.zipDef.reset();
    if (this.context != null)
      try {
        this.context.writeTo(this.byteSizer);
      } catch (IOException iOException) {
        throw new RuntimeException(iOException);
      }  
    this.zipSizer.reset();
    this.byteSizer.reset();
  }
  
  private void flushData() {
    try {
      this.zipOut.finish();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  private int getByteSize() { return this.byteSizer.getSize(); }
  
  private int getZipSize() {
    flushData();
    return this.zipSizer.getSize();
  }
  
  void addStressSeed(int paramInt) {
    if (this.stress == null)
      return; 
    this.stress.setSeed(paramInt + (this.stress.nextInt() << 32));
  }
  
  private CodingMethod stressPopCoding(CodingMethod paramCodingMethod) {
    assert this.stress != null;
    if (!(paramCodingMethod instanceof Coding))
      return paramCodingMethod; 
    Coding coding = ((Coding)paramCodingMethod).getValueCoding();
    Histogram histogram = getValueHistogram();
    int i = stressLen(histogram.getTotalLength());
    if (i == 0)
      return paramCodingMethod; 
    ArrayList arrayList = new ArrayList();
    if (this.stress.nextBoolean()) {
      HashSet hashSet = new HashSet();
      for (int m = this.start; m < this.end; m++) {
        if (hashSet.add(Integer.valueOf(this.values[m])))
          arrayList.add(Integer.valueOf(this.values[m])); 
      } 
    } else {
      int[][] arrayOfInt = histogram.getMatrix();
      for (byte b1 = 0; b1 < arrayOfInt.length; b1++) {
        int[] arrayOfInt3 = arrayOfInt[b1];
        for (byte b2 = 1; b2 < arrayOfInt3.length; b2++)
          arrayList.add(Integer.valueOf(arrayOfInt3[b2])); 
      } 
    } 
    int j = this.stress.nextInt();
    if ((j & 0x7) <= 2) {
      Collections.shuffle(arrayList, this.stress);
    } else {
      if ((j >>>= 3 & 0x7) <= 2)
        Collections.sort(arrayList); 
      if ((j >>>= 3 & 0x7) <= 2)
        Collections.reverse(arrayList); 
      if ((j >>>= 3 & 0x7) <= 2)
        Collections.rotate(arrayList, stressLen(arrayList.size())); 
    } 
    if (arrayList.size() > i)
      if ((j >>>= 3 & 0x7) <= 2) {
        arrayList.subList(i, arrayList.size()).clear();
      } else {
        arrayList.subList(0, arrayList.size() - i).clear();
      }  
    i = arrayList.size();
    int[] arrayOfInt1 = new int[1 + i];
    for (byte b = 0; b < i; b++)
      arrayOfInt1[true + b] = ((Integer)arrayList.get(b)).intValue(); 
    PopulationCoding populationCoding = new PopulationCoding();
    populationCoding.setFavoredValues(arrayOfInt1, i);
    int[] arrayOfInt2 = PopulationCoding.LValuesCoded;
    int k;
    for (k = 0; k < arrayOfInt2.length / 2; k++) {
      int m = arrayOfInt2[this.stress.nextInt(arrayOfInt2.length)];
      if (m >= 0 && PopulationCoding.fitTokenCoding(i, m) != null) {
        populationCoding.setL(m);
        break;
      } 
    } 
    if (populationCoding.tokenCoding == null) {
      k = arrayOfInt1[1];
      int m = k;
      for (byte b1 = 2; b1 <= i; b1++) {
        int n = arrayOfInt1[b1];
        if (k > n)
          k = n; 
        if (m < n)
          m = n; 
      } 
      populationCoding.tokenCoding = stressCoding(k, m);
    } 
    computePopSizePrivate(populationCoding, coding, coding);
    return populationCoding;
  }
  
  private CodingMethod stressAdaptiveCoding(CodingMethod paramCodingMethod) {
    assert this.stress != null;
    if (!(paramCodingMethod instanceof Coding))
      return paramCodingMethod; 
    Coding coding = (Coding)paramCodingMethod;
    int i = this.end - this.start;
    if (i < 2)
      return paramCodingMethod; 
    int j = stressLen(i - 1) + 1;
    if (j == i)
      return paramCodingMethod; 
    try {
      assert !this.disableRunCoding;
      this.disableRunCoding = true;
      int[] arrayOfInt = (int[])this.values.clone();
      CodingMethod codingMethod = null;
      int k = this.end;
      int m = this.start;
      while (k > m) {
        int i1;
        byte b = (k - m < 100) ? -1 : this.stress.nextInt();
        if ((b & 0x7) != 0) {
          i1 = (j == 1) ? j : (stressLen(j - 1) + 1);
        } else {
          byte b1 = b >>>= 3 & 0x3;
          short s = b >>>= 3 & 0xFF;
          while (true) {
            i1 = AdaptiveCoding.decodeK(b1, s);
            if (i1 <= k - m)
              break; 
            if (s != 3) {
              s = 3;
              continue;
            } 
            b1--;
          } 
          assert AdaptiveCoding.isCodableLength(i1);
        } 
        if (i1 > k - m)
          i1 = k - m; 
        while (!AdaptiveCoding.isCodableLength(i1))
          i1--; 
        int n = k - i1;
        assert n < k;
        assert n >= m;
        CodingMethod codingMethod1 = choose(arrayOfInt, n, k, coding);
        if (codingMethod == null) {
          codingMethod = codingMethod1;
        } else {
          codingMethod = new AdaptiveCoding(k - n, codingMethod1, codingMethod);
        } 
        k = n;
      } 
      return codingMethod;
    } finally {
      this.disableRunCoding = false;
    } 
  }
  
  private Coding stressCoding(int paramInt1, int paramInt2) {
    assert this.stress != null;
    for (byte b = 0; b < 100; b++) {
      Coding coding = Coding.of(this.stress.nextInt(5) + 1, this.stress.nextInt(256) + 1, this.stress.nextInt(3));
      if (coding.B() == 1)
        coding = coding.setH(256); 
      if (coding.H() == 256 && coding.B() >= 5)
        coding = coding.setB(4); 
      if (this.stress.nextBoolean()) {
        Coding coding1 = coding.setD(1);
        if (coding1.canRepresent(paramInt1, paramInt2))
          return coding1; 
      } 
      if (coding.canRepresent(paramInt1, paramInt2))
        return coding; 
    } 
    return BandStructure.UNSIGNED5;
  }
  
  private int stressLen(int paramInt) {
    assert this.stress != null;
    assert paramInt >= 0;
    int i = this.stress.nextInt(100);
    return (i < 20) ? Math.min(paramInt / 5, i) : ((i < 40) ? paramInt : this.stress.nextInt(paramInt));
  }
  
  static class Choice {
    final Coding coding;
    
    final int index;
    
    final int[] distance;
    
    int searchOrder;
    
    int minDistance;
    
    int zipSize;
    
    int byteSize;
    
    int histSize;
    
    Choice(Coding param1Coding, int param1Int, int[] param1ArrayOfInt) {
      this.coding = param1Coding;
      this.index = param1Int;
      this.distance = param1ArrayOfInt;
    }
    
    void reset() {
      this.searchOrder = Integer.MAX_VALUE;
      this.minDistance = Integer.MAX_VALUE;
      this.zipSize = this.byteSize = this.histSize = -1;
    }
    
    boolean isExtra() { return (this.index < 0); }
    
    public String toString() { return stringForDebug(); }
    
    private String stringForDebug() {
      String str = "";
      if (this.searchOrder < Integer.MAX_VALUE)
        str = str + " so: " + this.searchOrder; 
      if (this.minDistance < Integer.MAX_VALUE)
        str = str + " md: " + this.minDistance; 
      if (this.zipSize > 0)
        str = str + " zs: " + this.zipSize; 
      if (this.byteSize > 0)
        str = str + " bs: " + this.byteSize; 
      if (this.histSize > 0)
        str = str + " hs: " + this.histSize; 
      return "Choice[" + this.index + "] " + str + " " + this.coding;
    }
  }
  
  static class Sizer extends OutputStream {
    final OutputStream out;
    
    private int count;
    
    Sizer(OutputStream param1OutputStream) { this.out = param1OutputStream; }
    
    Sizer() { this(null); }
    
    public void write(int param1Int) {
      this.count++;
      if (this.out != null)
        this.out.write(param1Int); 
    }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      this.count += param1Int2;
      if (this.out != null)
        this.out.write(param1ArrayOfByte, param1Int1, param1Int2); 
    }
    
    public void reset() { this.count = 0; }
    
    public int getSize() { return this.count; }
    
    public String toString() {
      String str = super.toString();
      assert (str = stringForDebug()) != null;
      return str;
    }
    
    String stringForDebug() { return "<Sizer " + getSize() + ">"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\CodingChooser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */