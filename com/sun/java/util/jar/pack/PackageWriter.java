package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class PackageWriter extends BandStructure {
  Package pkg;
  
  OutputStream finalOut;
  
  Package.Version packageVersion;
  
  Set<ConstantPool.Entry> requiredEntries;
  
  Map<Attribute.Layout, int[]> backCountTable;
  
  int[][] attrCounts;
  
  int[] maxFlags;
  
  List<Map<Attribute.Layout, int[]>> allLayouts;
  
  Attribute.Layout[] attrDefsWritten;
  
  private Code curCode;
  
  private Package.Class curClass;
  
  private ConstantPool.Entry[] curCPMap;
  
  int[] codeHist = new int[256];
  
  int[] ldcHist = new int[20];
  
  PackageWriter(Package paramPackage, OutputStream paramOutputStream) throws IOException {
    this.pkg = paramPackage;
    this.finalOut = paramOutputStream;
    initHighestClassVersion(paramPackage.getHighestClassVersion());
  }
  
  void write() throws IOException {
    boolean bool = false;
    try {
      if (this.verbose > 0)
        Utils.log.info("Setting up constant pool..."); 
      setup();
      if (this.verbose > 0)
        Utils.log.info("Packing..."); 
      writeConstantPool();
      writeFiles();
      writeAttrDefs();
      writeInnerClasses();
      writeClassesAndByteCodes();
      writeAttrCounts();
      if (this.verbose > 1)
        printCodeHist(); 
      if (this.verbose > 0)
        Utils.log.info("Coding..."); 
      this.all_bands.chooseBandCodings();
      writeFileHeader();
      writeAllBandsTo(this.finalOut);
      bool = true;
    } catch (Exception exception) {
      Utils.log.warning("Error on output: " + exception, exception);
      if (this.verbose > 0)
        this.finalOut.close(); 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      if (exception instanceof RuntimeException)
        throw (RuntimeException)exception; 
      throw new Error("error packing", exception);
    } 
  }
  
  void setup() throws IOException {
    this.requiredEntries = new HashSet();
    setArchiveOptions();
    trimClassAttributes();
    collectAttributeLayouts();
    this.pkg.buildGlobalConstantPool(this.requiredEntries);
    setBandIndexes();
    makeNewAttributeBands();
    collectInnerClasses();
  }
  
  void chooseDefaultPackageVersion() throws IOException {
    if (this.pkg.packageVersion != null) {
      this.packageVersion = this.pkg.packageVersion;
      if (this.verbose > 0)
        Utils.log.info("package version overridden with: " + this.packageVersion); 
      return;
    } 
    Package.Version version = getHighestClassVersion();
    if (version.lessThan(Constants.JAVA6_MAX_CLASS_VERSION)) {
      this.packageVersion = Constants.JAVA5_PACKAGE_VERSION;
    } else if (version.equals(Constants.JAVA6_MAX_CLASS_VERSION) || (version.equals(Constants.JAVA7_MAX_CLASS_VERSION) && !this.pkg.cp.haveExtraTags())) {
      this.packageVersion = Constants.JAVA6_PACKAGE_VERSION;
    } else if (version.equals(Constants.JAVA7_MAX_CLASS_VERSION)) {
      this.packageVersion = Constants.JAVA7_PACKAGE_VERSION;
    } else {
      this.packageVersion = Constants.JAVA8_PACKAGE_VERSION;
    } 
    if (this.verbose > 0)
      Utils.log.info("Highest version class file: " + version + " package version: " + this.packageVersion); 
  }
  
  void checkVersion() throws IOException {
    assert this.packageVersion != null;
    if (this.packageVersion.lessThan(Constants.JAVA7_PACKAGE_VERSION) && testBit(this.archiveOptions, 8))
      throw new IOException("Format bits for Java 7 must be zero in previous releases"); 
    if (testBit(this.archiveOptions, -8192))
      throw new IOException("High archive option bits are reserved and must be zero: " + Integer.toHexString(this.archiveOptions)); 
  }
  
  void setArchiveOptions() throws IOException {
    int i = this.pkg.default_modtime;
    int j = this.pkg.default_modtime;
    int k = -1;
    int m = 0;
    this.archiveOptions |= this.pkg.default_options;
    for (Package.File file : this.pkg.files) {
      int i1 = file.modtime;
      int i2 = file.options;
      if (i == 0) {
        i = j = i1;
      } else {
        if (i > i1)
          i = i1; 
        if (j < i1)
          j = i1; 
      } 
      k &= i2;
      m |= i2;
    } 
    if (this.pkg.default_modtime == 0)
      this.pkg.default_modtime = i; 
    if (i != 0 && i != j)
      this.archiveOptions |= 0x40; 
    if (!testBit(this.archiveOptions, 32) && k != -1) {
      if (testBit(k, 1)) {
        this.archiveOptions |= 0x20;
        k--;
        m--;
      } 
      this.pkg.default_options |= k;
      if (k != m || k != this.pkg.default_options)
        this.archiveOptions |= 0x80; 
    } 
    HashMap hashMap = new HashMap();
    int n = 0;
    Package.Version version = null;
    for (Package.Class clazz : this.pkg.classes) {
      Package.Version version1 = clazz.getVersion();
      int[] arrayOfInt = (int[])hashMap.get(version1);
      if (arrayOfInt == null) {
        arrayOfInt = new int[1];
        hashMap.put(version1, arrayOfInt);
      } 
      int i1 = arrayOfInt[0] = arrayOfInt[0] + 1;
      if (n < i1) {
        n = i1;
        version = version1;
      } 
    } 
    hashMap.clear();
    if (version == null)
      version = Constants.JAVA_MIN_CLASS_VERSION; 
    this.pkg.defaultClassVersion = version;
    if (this.verbose > 0)
      Utils.log.info("Consensus version number in segment is " + version); 
    if (this.verbose > 0)
      Utils.log.info("Highest version number in segment is " + this.pkg.getHighestClassVersion()); 
    for (Package.Class clazz : this.pkg.classes) {
      if (!clazz.getVersion().equals(version)) {
        Attribute attribute = makeClassFileVersionAttr(clazz.getVersion());
        if (this.verbose > 1)
          Utils.log.fine("Version " + clazz.getVersion() + " of " + clazz + " doesn't match package version " + version); 
        clazz.addAttribute(attribute);
      } 
    } 
    for (Package.File file : this.pkg.files) {
      long l = file.getFileLength();
      if (l != (int)l) {
        this.archiveOptions |= 0x100;
        if (this.verbose > 0)
          Utils.log.info("Note: Huge resource file " + file.getFileName() + " forces 64-bit sizing"); 
        break;
      } 
    } 
    boolean bool = false;
    byte b = 0;
    for (Package.Class clazz : this.pkg.classes) {
      for (Package.Class.Method method : clazz.getMethods()) {
        if (method.code != null) {
          if (method.code.attributeSize() == 0) {
            b++;
            continue;
          } 
          if (shortCodeHeader(method.code) != 0)
            bool += true; 
        } 
      } 
    } 
    if (bool > b)
      this.archiveOptions |= 0x4; 
    if (this.verbose > 0)
      Utils.log.info("archiveOptions = 0b" + Integer.toBinaryString(this.archiveOptions)); 
  }
  
  void writeFileHeader() throws IOException {
    chooseDefaultPackageVersion();
    writeArchiveMagic();
    writeArchiveHeader();
  }
  
  private void putMagicInt32(int paramInt) throws IOException {
    int i = paramInt;
    for (byte b = 0; b < 4; b++) {
      this.archive_magic.putByte(0xFF & i >>> 24);
      i <<= 8;
    } 
  }
  
  void writeArchiveMagic() throws IOException {
    this.pkg.getClass();
    putMagicInt32(-889270259);
  }
  
  void writeArchiveHeader() throws IOException {
    byte b = 15;
    boolean bool1 = testBit(this.archiveOptions, 1);
    if (!bool1) {
      bool1 |= ((this.band_headers.length() != 0));
      bool1 |= ((this.attrDefsWritten.length != 0));
      if (bool1)
        this.archiveOptions |= 0x1; 
    } 
    if (bool1)
      b += 2; 
    boolean bool2 = testBit(this.archiveOptions, 16);
    if (!bool2) {
      bool2 |= ((this.archiveNextCount > 0));
      bool2 |= ((this.pkg.default_modtime != 0));
      if (bool2)
        this.archiveOptions |= 0x10; 
    } 
    if (bool2)
      b += 5; 
    boolean bool3 = testBit(this.archiveOptions, 2);
    if (!bool3) {
      bool3 |= this.pkg.cp.haveNumbers();
      if (bool3)
        this.archiveOptions |= 0x2; 
    } 
    if (bool3)
      b += 4; 
    boolean bool4 = testBit(this.archiveOptions, 8);
    if (!bool4) {
      bool4 |= this.pkg.cp.haveExtraTags();
      if (bool4)
        this.archiveOptions |= 0x8; 
    } 
    if (bool4)
      b += 4; 
    checkVersion();
    this.archive_header_0.putInt(this.packageVersion.minor);
    this.archive_header_0.putInt(this.packageVersion.major);
    if (this.verbose > 0)
      Utils.log.info("Package Version for this segment:" + this.packageVersion); 
    this.archive_header_0.putInt(this.archiveOptions);
    assert this.archive_header_0.length() == 3;
    if (bool2) {
      assert this.archive_header_S.length() == 0;
      this.archive_header_S.putInt(0);
      assert this.archive_header_S.length() == 1;
      this.archive_header_S.putInt(0);
      assert this.archive_header_S.length() == 2;
    } 
    if (bool2) {
      this.archive_header_1.putInt(this.archiveNextCount);
      this.archive_header_1.putInt(this.pkg.default_modtime);
      this.archive_header_1.putInt(this.pkg.files.size());
    } else {
      assert this.pkg.files.isEmpty();
    } 
    if (bool1) {
      this.archive_header_1.putInt(this.band_headers.length());
      this.archive_header_1.putInt(this.attrDefsWritten.length);
    } else {
      assert this.band_headers.length() == 0;
      assert this.attrDefsWritten.length == 0;
    } 
    writeConstantPoolCounts(bool3, bool4);
    this.archive_header_1.putInt(this.pkg.getAllInnerClasses().size());
    this.archive_header_1.putInt(this.pkg.defaultClassVersion.minor);
    this.archive_header_1.putInt(this.pkg.defaultClassVersion.major);
    this.archive_header_1.putInt(this.pkg.classes.size());
    assert this.archive_header_0.length() + this.archive_header_S.length() + this.archive_header_1.length() == b;
    this.archiveSize0 = 0L;
    this.archiveSize1 = this.all_bands.outputSize();
    this.archiveSize0 += this.archive_magic.outputSize();
    this.archiveSize0 += this.archive_header_0.outputSize();
    this.archiveSize0 += this.archive_header_S.outputSize();
    this.archiveSize1 -= this.archiveSize0;
    if (bool2) {
      int i = (int)(this.archiveSize1 >>> 32);
      int j = (int)(this.archiveSize1 >>> false);
      this.archive_header_S.patchValue(0, i);
      this.archive_header_S.patchValue(1, j);
      int k = UNSIGNED5.getLength(0);
      this.archiveSize0 += (UNSIGNED5.getLength(i) - k);
      this.archiveSize0 += (UNSIGNED5.getLength(j) - k);
    } 
    if (this.verbose > 1)
      Utils.log.fine("archive sizes: " + this.archiveSize0 + "+" + this.archiveSize1); 
    assert this.all_bands.outputSize() == this.archiveSize0 + this.archiveSize1;
  }
  
  void writeConstantPoolCounts(boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    for (byte b : ConstantPool.TAGS_IN_ORDER) {
      int i = this.pkg.cp.getIndexByTag(b).size();
      switch (b) {
        case 1:
          if (i > 0 && !$assertionsDisabled && this.pkg.cp.getIndexByTag(b).get(false) != ConstantPool.getUtf8Entry(""))
            throw new AssertionError(); 
        case 3:
        case 4:
        case 5:
        case 6:
          if (!paramBoolean1) {
            assert i == 0;
            break;
          } 
        case 15:
        case 16:
        case 17:
        case 18:
          if (!paramBoolean2) {
            assert i == 0;
            break;
          } 
        default:
          this.archive_header_1.putInt(i);
          break;
      } 
    } 
  }
  
  protected ConstantPool.Index getCPIndex(byte paramByte) { return this.pkg.cp.getIndexByTag(paramByte); }
  
  void writeConstantPool() throws IOException {
    ConstantPool.IndexGroup indexGroup = this.pkg.cp;
    if (this.verbose > 0)
      Utils.log.info("Writing CP"); 
    for (byte b : ConstantPool.TAGS_IN_ORDER) {
      byte b1;
      ConstantPool.Index index = indexGroup.getIndexByTag(b);
      ConstantPool.Entry[] arrayOfEntry = index.cpMap;
      if (this.verbose > 0)
        Utils.log.info("Writing " + arrayOfEntry.length + " " + ConstantPool.tagName(b) + " entries..."); 
      if (this.optDumpBands)
        try (PrintStream null = new PrintStream(getDumpStream(index, ".idx"))) {
          printArrayTo(printStream, arrayOfEntry, 0, arrayOfEntry.length);
        }  
      switch (b) {
        case 1:
          writeUtf8Bands(arrayOfEntry);
          break;
        case 3:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.NumberEntry numberEntry = (ConstantPool.NumberEntry)arrayOfEntry[b1];
            int i = ((Integer)numberEntry.numberValue()).intValue();
            this.cp_Int.putInt(i);
          } 
          break;
        case 4:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.NumberEntry numberEntry = (ConstantPool.NumberEntry)arrayOfEntry[b1];
            float f = ((Float)numberEntry.numberValue()).floatValue();
            int i = Float.floatToIntBits(f);
            this.cp_Float.putInt(i);
          } 
          break;
        case 5:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.NumberEntry numberEntry = (ConstantPool.NumberEntry)arrayOfEntry[b1];
            long l = ((Long)numberEntry.numberValue()).longValue();
            this.cp_Long_hi.putInt((int)(l >>> 32));
            this.cp_Long_lo.putInt((int)(l >>> false));
          } 
          break;
        case 6:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.NumberEntry numberEntry = (ConstantPool.NumberEntry)arrayOfEntry[b1];
            double d = ((Double)numberEntry.numberValue()).doubleValue();
            long l = Double.doubleToLongBits(d);
            this.cp_Double_hi.putInt((int)(l >>> 32));
            this.cp_Double_lo.putInt((int)(l >>> false));
          } 
          break;
        case 8:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.StringEntry stringEntry = (ConstantPool.StringEntry)arrayOfEntry[b1];
            this.cp_String.putRef(stringEntry.ref);
          } 
          break;
        case 7:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry)arrayOfEntry[b1];
            this.cp_Class.putRef(classEntry.ref);
          } 
          break;
        case 13:
          writeSignatureBands(arrayOfEntry);
          break;
        case 12:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.DescriptorEntry descriptorEntry = (ConstantPool.DescriptorEntry)arrayOfEntry[b1];
            this.cp_Descr_name.putRef(descriptorEntry.nameRef);
            this.cp_Descr_type.putRef(descriptorEntry.typeRef);
          } 
          break;
        case 9:
          writeMemberRefs(b, arrayOfEntry, this.cp_Field_class, this.cp_Field_desc);
          break;
        case 10:
          writeMemberRefs(b, arrayOfEntry, this.cp_Method_class, this.cp_Method_desc);
          break;
        case 11:
          writeMemberRefs(b, arrayOfEntry, this.cp_Imethod_class, this.cp_Imethod_desc);
          break;
        case 15:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.MethodHandleEntry methodHandleEntry = (ConstantPool.MethodHandleEntry)arrayOfEntry[b1];
            this.cp_MethodHandle_refkind.putInt(methodHandleEntry.refKind);
            this.cp_MethodHandle_member.putRef(methodHandleEntry.memRef);
          } 
          break;
        case 16:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.MethodTypeEntry methodTypeEntry = (ConstantPool.MethodTypeEntry)arrayOfEntry[b1];
            this.cp_MethodType.putRef(methodTypeEntry.typeRef);
          } 
          break;
        case 18:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.InvokeDynamicEntry invokeDynamicEntry = (ConstantPool.InvokeDynamicEntry)arrayOfEntry[b1];
            this.cp_InvokeDynamic_spec.putRef(invokeDynamicEntry.bssRef);
            this.cp_InvokeDynamic_desc.putRef(invokeDynamicEntry.descRef);
          } 
          break;
        case 17:
          for (b1 = 0; b1 < arrayOfEntry.length; b1++) {
            ConstantPool.BootstrapMethodEntry bootstrapMethodEntry = (ConstantPool.BootstrapMethodEntry)arrayOfEntry[b1];
            this.cp_BootstrapMethod_ref.putRef(bootstrapMethodEntry.bsmRef);
            this.cp_BootstrapMethod_arg_count.putInt(bootstrapMethodEntry.argRefs.length);
            for (ConstantPool.Entry entry : bootstrapMethodEntry.argRefs)
              this.cp_BootstrapMethod_arg.putRef(entry); 
          } 
          break;
        default:
          throw new AssertionError("unexpected CP tag in package");
      } 
    } 
    if (this.optDumpBands || this.verbose > 1)
      for (byte b = 50; b < 54; b = (byte)(b + 1)) {
        ConstantPool.Index index = indexGroup.getIndexByTag(b);
        if (index != null && !index.isEmpty()) {
          ConstantPool.Entry[] arrayOfEntry = index.cpMap;
          if (this.verbose > 1)
            Utils.log.info("Index group " + ConstantPool.tagName(b) + " contains " + arrayOfEntry.length + " entries."); 
          if (this.optDumpBands)
            try (PrintStream null = new PrintStream(getDumpStream(index.debugName, b, ".gidx", index))) {
              printArrayTo(printStream, arrayOfEntry, 0, arrayOfEntry.length, true);
            }  
        } 
      }  
  }
  
  void writeUtf8Bands(ConstantPool.Entry[] paramArrayOfEntry) throws IOException {
    if (paramArrayOfEntry.length == 0)
      return; 
    assert paramArrayOfEntry[0].stringValue().equals("");
    char[][] arrayOfChar = new char[paramArrayOfEntry.length][];
    for (byte b = 0; b < arrayOfChar.length; b++)
      arrayOfChar[b] = paramArrayOfEntry[b].stringValue().toCharArray(); 
    int[] arrayOfInt = new int[paramArrayOfEntry.length];
    char[] arrayOfChar1 = new char[0];
    int i;
    for (i = 0; i < arrayOfChar.length; i++) {
      byte b1 = 0;
      char[] arrayOfChar2 = arrayOfChar[i];
      int j = Math.min(arrayOfChar2.length, arrayOfChar1.length);
      while (b1 < j && arrayOfChar2[b1] == arrayOfChar1[b1])
        b1++; 
      arrayOfInt[i] = b1;
      if (i >= 2) {
        this.cp_Utf8_prefix.putInt(b1);
      } else {
        assert b1 == 0;
      } 
      arrayOfChar1 = arrayOfChar2;
    } 
    for (i = 0; i < arrayOfChar.length; i++) {
      char[] arrayOfChar2 = arrayOfChar[i];
      int j = arrayOfInt[i];
      int k = arrayOfChar2.length - arrayOfInt[i];
      boolean bool = false;
      if (k == 0) {
        bool = (i >= 1);
      } else if (this.optBigStrings && this.effort > 1 && k > 100) {
        byte b1 = 0;
        for (int m = 0; m < k; m++) {
          if (arrayOfChar2[j + m] > '')
            b1++; 
        } 
        if (b1 > 100)
          bool = tryAlternateEncoding(i, b1, arrayOfChar2, j); 
      } 
      if (i < 1) {
        assert !bool;
        assert k == 0;
      } else if (bool) {
        this.cp_Utf8_suffix.putInt(0);
        this.cp_Utf8_big_suffix.putInt(k);
      } else {
        assert k != 0;
        this.cp_Utf8_suffix.putInt(k);
        for (int m = 0; m < k; m++) {
          char c = arrayOfChar2[j + m];
          this.cp_Utf8_chars.putInt(c);
        } 
      } 
    } 
    if (this.verbose > 0) {
      i = this.cp_Utf8_chars.length();
      int j = this.cp_Utf8_big_chars.length();
      int k = i + j;
      Utils.log.info("Utf8string #CHARS=" + k + " #PACKEDCHARS=" + j);
    } 
  }
  
  private boolean tryAlternateEncoding(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) {
    int i = paramArrayOfChar.length - paramInt3;
    int[] arrayOfInt1 = new int[i];
    for (int j = 0; j < i; j++)
      arrayOfInt1[j] = paramArrayOfChar[paramInt3 + j]; 
    CodingChooser codingChooser = getCodingChooser();
    Coding coding1 = this.cp_Utf8_big_chars.regularCoding;
    String str = "(Utf8_big_" + paramInt1 + ")";
    int[] arrayOfInt2 = { 0, 0 };
    if (this.verbose > 1 || codingChooser.verbose > 1)
      Utils.log.fine("--- chooseCoding " + str); 
    CodingMethod codingMethod = codingChooser.choose(arrayOfInt1, coding1, arrayOfInt2);
    Coding coding2 = this.cp_Utf8_chars.regularCoding;
    if (this.verbose > 1)
      Utils.log.fine("big string[" + paramInt1 + "] len=" + i + " #wide=" + paramInt2 + " size=" + arrayOfInt2[0] + "/z=" + arrayOfInt2[1] + " coding " + codingMethod); 
    if (codingMethod != coding2) {
      int k = arrayOfInt2[1];
      int[] arrayOfInt = codingChooser.computeSize(coding2, arrayOfInt1);
      int m = arrayOfInt[1];
      int n = Math.max(5, m / 1000);
      if (this.verbose > 1)
        Utils.log.fine("big string[" + paramInt1 + "] normalSize=" + arrayOfInt[0] + "/z=" + arrayOfInt[1] + " win=" + ((k < m - n) ? 1 : 0)); 
      if (k < m - n) {
        BandStructure.IntBand intBand = this.cp_Utf8_big_chars.newIntBand(str);
        intBand.initializeValues(arrayOfInt1);
        return true;
      } 
    } 
    return false;
  }
  
  void writeSignatureBands(ConstantPool.Entry[] paramArrayOfEntry) throws IOException {
    for (byte b = 0; b < paramArrayOfEntry.length; b++) {
      ConstantPool.SignatureEntry signatureEntry = (ConstantPool.SignatureEntry)paramArrayOfEntry[b];
      this.cp_Signature_form.putRef(signatureEntry.formRef);
      for (byte b1 = 0; b1 < signatureEntry.classRefs.length; b1++)
        this.cp_Signature_classes.putRef(signatureEntry.classRefs[b1]); 
    } 
  }
  
  void writeMemberRefs(byte paramByte, ConstantPool.Entry[] paramArrayOfEntry, BandStructure.CPRefBand paramCPRefBand1, BandStructure.CPRefBand paramCPRefBand2) throws IOException {
    for (byte b = 0; b < paramArrayOfEntry.length; b++) {
      ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)paramArrayOfEntry[b];
      paramCPRefBand1.putRef(memberEntry.classRef);
      paramCPRefBand2.putRef(memberEntry.descRef);
    } 
  }
  
  void writeFiles() throws IOException {
    int i = this.pkg.files.size();
    if (i == 0)
      return; 
    int j = this.archiveOptions;
    boolean bool1 = testBit(j, 256);
    boolean bool2 = testBit(j, 64);
    boolean bool3 = testBit(j, 128);
    if (!bool3)
      for (Package.File file : this.pkg.files) {
        if (file.isClassStub()) {
          bool3 = true;
          j |= 0x80;
          this.archiveOptions = j;
          break;
        } 
      }  
    if (bool1 || bool2 || bool3 || !this.pkg.files.isEmpty()) {
      j |= 0x10;
      this.archiveOptions = j;
    } 
    for (Package.File file : this.pkg.files) {
      this.file_name.putRef(file.name);
      long l = file.getFileLength();
      this.file_size_lo.putInt((int)l);
      if (bool1)
        this.file_size_hi.putInt((int)(l >>> 32)); 
      if (bool2)
        this.file_modtime.putInt(file.modtime - this.pkg.default_modtime); 
      if (bool3)
        this.file_options.putInt(file.options); 
      file.writeTo(this.file_bits.collectorStream());
      if (this.verbose > 1)
        Utils.log.fine("Wrote " + l + " bytes of " + file.name.stringValue()); 
    } 
    if (this.verbose > 0)
      Utils.log.info("Wrote " + i + " resource files"); 
  }
  
  void collectAttributeLayouts() throws IOException {
    this.maxFlags = new int[4];
    this.allLayouts = new FixedList(4);
    byte b;
    for (b = 0; b < 4; b++)
      this.allLayouts.set(b, new HashMap()); 
    for (Package.Class clazz : this.pkg.classes) {
      visitAttributeLayoutsIn(0, clazz);
      for (Package.Class.Field field : clazz.getFields())
        visitAttributeLayoutsIn(1, field); 
      for (Package.Class.Method method : clazz.getMethods()) {
        visitAttributeLayoutsIn(2, method);
        if (method.code != null)
          visitAttributeLayoutsIn(3, method.code); 
      } 
    } 
    for (b = 0; b < 4; b++) {
      int i = ((Map)this.allLayouts.get(b)).size();
      boolean bool = haveFlagsHi(b);
      if (i >= 24) {
        int j = 1 << 9 + b;
        this.archiveOptions |= j;
        bool = true;
        if (this.verbose > 0)
          Utils.log.info("Note: Many " + Attribute.contextName(b) + " attributes forces 63-bit flags"); 
      } 
      if (this.verbose > 1) {
        Utils.log.fine(Attribute.contextName(b) + ".maxFlags = 0x" + Integer.toHexString(this.maxFlags[b]));
        Utils.log.fine(Attribute.contextName(b) + ".#layouts = " + i);
      } 
      assert haveFlagsHi(b) == bool;
    } 
    initAttrIndexLimit();
    for (b = 0; b < 4; b++)
      assert (this.attrFlagMask[b] & this.maxFlags[b]) == 0L; 
    this.backCountTable = new HashMap();
    this.attrCounts = new int[4][];
    for (b = 0; b < 4; b++) {
      long l = (this.maxFlags[b] | this.attrFlagMask[b]) ^ 0xFFFFFFFFFFFFFFFFL;
      assert this.attrIndexLimit[b] > 0;
      assert this.attrIndexLimit[b] < 64;
      l &= (1L << this.attrIndexLimit[b]) - 1L;
      byte b1 = 0;
      Map map = (Map)this.allLayouts.get(b);
      Map.Entry[] arrayOfEntry = new Map.Entry[map.size()];
      map.entrySet().toArray(arrayOfEntry);
      Arrays.sort(arrayOfEntry, new Comparator<Map.Entry<Attribute.Layout, int[]>>() {
            public int compare(Map.Entry<Attribute.Layout, int[]> param1Entry1, Map.Entry<Attribute.Layout, int[]> param1Entry2) {
              int i = -((int[])param1Entry1.getValue()[0] - (int[])param1Entry2.getValue()[0]);
              return (i != 0) ? i : ((Attribute.Layout)param1Entry1.getKey()).compareTo((Attribute.Layout)param1Entry2.getKey());
            }
          });
      this.attrCounts[b] = new int[this.attrIndexLimit[b] + arrayOfEntry.length];
      for (byte b2 = 0; b2 < arrayOfEntry.length; b2++) {
        int j;
        Map.Entry entry = arrayOfEntry[b2];
        Attribute.Layout layout = (Attribute.Layout)entry.getKey();
        int i = (int[])entry.getValue()[0];
        Integer integer = (Integer)this.attrIndexTable.get(layout);
        if (integer != null) {
          j = integer.intValue();
        } else if (l != 0L) {
          while ((l & 0x1L) == 0L) {
            l >>>= true;
            b1++;
          } 
          l--;
          j = setAttributeLayoutIndex(layout, b1);
        } else {
          j = setAttributeLayoutIndex(layout, -1);
        } 
        this.attrCounts[b][j] = i;
        Attribute.Layout.Element[] arrayOfElement = layout.getCallables();
        int[] arrayOfInt = new int[arrayOfElement.length];
        for (byte b3 = 0; b3 < arrayOfElement.length; b3++) {
          assert (arrayOfElement[b3]).kind == 10;
          if (!arrayOfElement[b3].flagTest((byte)8))
            arrayOfInt[b3] = -1; 
        } 
        this.backCountTable.put(layout, arrayOfInt);
        if (integer == null) {
          ConstantPool.Utf8Entry utf8Entry1 = ConstantPool.getUtf8Entry(layout.name());
          String str = layout.layoutForClassVersion(getHighestClassVersion());
          ConstantPool.Utf8Entry utf8Entry2 = ConstantPool.getUtf8Entry(str);
          this.requiredEntries.add(utf8Entry1);
          this.requiredEntries.add(utf8Entry2);
          if (this.verbose > 0)
            if (j < this.attrIndexLimit[b]) {
              Utils.log.info("Using free flag bit 1<<" + j + " for " + i + " occurrences of " + layout);
            } else {
              Utils.log.info("Using overflow index " + j + " for " + i + " occurrences of " + layout);
            }  
        } 
      } 
    } 
    this.maxFlags = null;
    this.allLayouts = null;
  }
  
  void visitAttributeLayoutsIn(int paramInt, Attribute.Holder paramHolder) {
    this.maxFlags[paramInt] = this.maxFlags[paramInt] | paramHolder.flags;
    for (Attribute attribute : paramHolder.getAttributes()) {
      Attribute.Layout layout = attribute.layout();
      Map map = (Map)this.allLayouts.get(paramInt);
      int[] arrayOfInt = (int[])map.get(layout);
      if (arrayOfInt == null)
        map.put(layout, arrayOfInt = new int[1]); 
      if (arrayOfInt[0] < Integer.MAX_VALUE)
        arrayOfInt[0] = arrayOfInt[0] + 1; 
    } 
  }
  
  void writeAttrDefs() throws IOException {
    ArrayList arrayList = new ArrayList();
    int i;
    for (i = 0; i < 4; i++) {
      int j = ((List)this.attrDefs.get(i)).size();
      for (byte b = 0; b < j; b++) {
        byte b1 = i;
        if (b < this.attrIndexLimit[i]) {
          b1 |= b + 1 << 2;
          assert b1 < 'Ā';
          if (!testBit(this.attrDefSeen[i], 1L << b))
            continue; 
        } 
        Attribute.Layout layout = (Attribute.Layout)((List)this.attrDefs.get(i)).get(b);
        arrayList.add(new Object[] { Integer.valueOf(b1), layout });
        assert Integer.valueOf(b).equals(this.attrIndexTable.get(layout));
        continue;
      } 
    } 
    i = arrayList.size();
    Object[][] arrayOfObject = new Object[i][];
    arrayList.toArray(arrayOfObject);
    Arrays.sort(arrayOfObject, new Comparator<Object[]>() {
          public int compare(Object[] param1ArrayOfObject1, Object[] param1ArrayOfObject2) {
            int i = ((Comparable)param1ArrayOfObject1[0]).compareTo(param1ArrayOfObject2[0]);
            if (i != 0)
              return i; 
            Integer integer1 = (Integer)PackageWriter.this.attrIndexTable.get(param1ArrayOfObject1[1]);
            Integer integer2 = (Integer)PackageWriter.this.attrIndexTable.get(param1ArrayOfObject2[1]);
            assert integer1 != null;
            assert integer2 != null;
            return integer1.compareTo(integer2);
          }
        });
    this.attrDefsWritten = new Attribute.Layout[i];
    try (PrintStream null = !this.optDumpBands ? null : new PrintStream(getDumpStream(this.attr_definition_headers, ".def"))) {
      arrayOfInt = Arrays.copyOf(this.attrIndexLimit, 4);
      for (byte b = 0; b < arrayOfObject.length; b++) {
        int j = ((Integer)arrayOfObject[b][0]).intValue();
        Attribute.Layout layout = (Attribute.Layout)arrayOfObject[b][1];
        this.attrDefsWritten[b] = layout;
        assert (j & 0x3) == layout.ctype();
        this.attr_definition_headers.putByte(j);
        this.attr_definition_name.putRef(ConstantPool.getUtf8Entry(layout.name()));
        String str = layout.layoutForClassVersion(getHighestClassVersion());
        this.attr_definition_layout.putRef(ConstantPool.getUtf8Entry(str));
        boolean bool = false;
        assert bool = true;
        if (bool) {
          int k = (j >> 2) - 1;
          if (k < 0) {
            arrayOfInt[layout.ctype()] = arrayOfInt[layout.ctype()] + 1;
            k = arrayOfInt[layout.ctype()];
          } 
          int m = ((Integer)this.attrIndexTable.get(layout)).intValue();
          assert k == m;
        } 
        if (printStream != null) {
          int k = (j >> 2) - 1;
          printStream.println(k + " " + layout);
        } 
      } 
    } 
  }
  
  void writeAttrCounts() throws IOException {
    for (byte b = 0; b < 4; b++) {
      BandStructure.MultiBand multiBand = this.attrBands[b];
      BandStructure.IntBand intBand = getAttrBand(multiBand, 4);
      Attribute.Layout[] arrayOfLayout = new Attribute.Layout[((List)this.attrDefs.get(b)).size()];
      ((List)this.attrDefs.get(b)).toArray(arrayOfLayout);
      boolean bool;
      for (bool = true;; bool = false) {
        for (byte b1 = 0; b1 < arrayOfLayout.length; b1++) {
          Attribute.Layout layout = arrayOfLayout[b1];
          if (layout != null && bool == isPredefinedAttr(b, b1)) {
            int i = this.attrCounts[b][b1];
            if (i != 0) {
              int[] arrayOfInt = (int[])this.backCountTable.get(layout);
              for (byte b2 = 0; b2 < arrayOfInt.length; b2++) {
                if (arrayOfInt[b2] >= 0) {
                  int j = arrayOfInt[b2];
                  arrayOfInt[b2] = -1;
                  intBand.putInt(j);
                  assert layout.getCallables()[b2].flagTest((byte)8);
                } else {
                  assert !layout.getCallables()[b2].flagTest((byte)8);
                } 
              } 
            } 
          } 
        } 
        if (!bool)
          break; 
      } 
    } 
  }
  
  void trimClassAttributes() throws IOException {
    for (Package.Class clazz : this.pkg.classes) {
      clazz.minimizeSourceFile();
      assert clazz.getAttribute(Package.attrBootstrapMethodsEmpty) == null;
    } 
  }
  
  void collectInnerClasses() throws IOException {
    HashMap hashMap = new HashMap();
    for (Package.Class clazz : this.pkg.classes) {
      if (!clazz.hasInnerClasses())
        continue; 
      for (Package.InnerClass innerClass1 : clazz.getInnerClasses()) {
        Package.InnerClass innerClass2 = (Package.InnerClass)hashMap.put(innerClass1.thisClass, innerClass1);
        if (innerClass2 != null && !innerClass2.equals(innerClass1) && innerClass2.predictable)
          hashMap.put(innerClass2.thisClass, innerClass2); 
      } 
    } 
    Package.InnerClass[] arrayOfInnerClass = new Package.InnerClass[hashMap.size()];
    hashMap.values().toArray(arrayOfInnerClass);
    hashMap = null;
    Arrays.sort(arrayOfInnerClass);
    this.pkg.setAllInnerClasses(Arrays.asList(arrayOfInnerClass));
    for (Package.Class clazz : this.pkg.classes)
      clazz.minimizeLocalICs(); 
  }
  
  void writeInnerClasses() throws IOException {
    for (Package.InnerClass innerClass : this.pkg.getAllInnerClasses()) {
      int i = innerClass.flags;
      assert (i & 0x10000) == 0;
      if (!innerClass.predictable)
        i |= 0x10000; 
      this.ic_this_class.putRef(innerClass.thisClass);
      this.ic_flags.putInt(i);
      if (!innerClass.predictable) {
        this.ic_outer_class.putRef(innerClass.outerClass);
        this.ic_name.putRef(innerClass.name);
      } 
    } 
  }
  
  void writeLocalInnerClasses(Package.Class paramClass) throws IOException {
    List list = paramClass.getInnerClasses();
    this.class_InnerClasses_N.putInt(list.size());
    for (Package.InnerClass innerClass : list) {
      this.class_InnerClasses_RC.putRef(innerClass.thisClass);
      if (innerClass.equals(this.pkg.getGlobalInnerClass(innerClass.thisClass))) {
        this.class_InnerClasses_F.putInt(0);
        continue;
      } 
      int i = innerClass.flags;
      if (i == 0)
        i = 65536; 
      this.class_InnerClasses_F.putInt(i);
      this.class_InnerClasses_outer_RCN.putRef(innerClass.outerClass);
      this.class_InnerClasses_name_RUN.putRef(innerClass.name);
    } 
  }
  
  void writeClassesAndByteCodes() throws IOException {
    Package.Class[] arrayOfClass = new Package.Class[this.pkg.classes.size()];
    this.pkg.classes.toArray(arrayOfClass);
    if (this.verbose > 0)
      Utils.log.info("  ...scanning " + arrayOfClass.length + " classes..."); 
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfClass.length; b2++) {
      Package.Class clazz = arrayOfClass[b2];
      if (this.verbose > 1)
        Utils.log.fine("Scanning " + clazz); 
      ConstantPool.ClassEntry classEntry1 = clazz.thisClass;
      ConstantPool.ClassEntry classEntry2 = clazz.superClass;
      ConstantPool.ClassEntry[] arrayOfClassEntry = clazz.interfaces;
      assert classEntry2 != classEntry1;
      if (classEntry2 == null)
        classEntry2 = classEntry1; 
      this.class_this.putRef(classEntry1);
      this.class_super.putRef(classEntry2);
      this.class_interface_count.putInt(clazz.interfaces.length);
      for (byte b = 0; b < arrayOfClassEntry.length; b++)
        this.class_interface.putRef(arrayOfClassEntry[b]); 
      writeMembers(clazz);
      writeAttrs(0, clazz, clazz);
      if (this.verbose > 0 && ++b1 % 'Ϩ' == '\000')
        Utils.log.info("Have scanned " + b1 + " classes..."); 
    } 
  }
  
  void writeMembers(Package.Class paramClass) throws IOException {
    List list1 = paramClass.getFields();
    this.class_field_count.putInt(list1.size());
    for (Package.Class.Field field : list1) {
      this.field_descr.putRef(field.getDescriptor());
      writeAttrs(1, field, paramClass);
    } 
    List list2 = paramClass.getMethods();
    this.class_method_count.putInt(list2.size());
    for (Package.Class.Method method : list2) {
      this.method_descr.putRef(method.getDescriptor());
      writeAttrs(2, method, paramClass);
      assert false;
      if (method.code != null) {
        writeCodeHeader(method.code);
        writeByteCodes(method.code);
      } 
    } 
  }
  
  void writeCodeHeader(Code paramCode) throws IOException {
    boolean bool = testBit(this.archiveOptions, 4);
    int i = paramCode.attributeSize();
    int j = shortCodeHeader(paramCode);
    if (!bool && i > 0)
      j = 0; 
    if (this.verbose > 2) {
      int k = paramCode.getMethod().getArgumentSize();
      Utils.log.fine("Code sizes info " + paramCode.max_stack + " " + paramCode.max_locals + " " + paramCode.getHandlerCount() + " " + k + " " + i + ((j > 0) ? (" SHORT=" + j) : ""));
    } 
    this.code_headers.putByte(j);
    if (j == 0) {
      this.code_max_stack.putInt(paramCode.getMaxStack());
      this.code_max_na_locals.putInt(paramCode.getMaxNALocals());
      this.code_handler_count.putInt(paramCode.getHandlerCount());
    } else {
      assert bool || i == 0;
      assert paramCode.getHandlerCount() < this.shortCodeHeader_h_limit;
    } 
    writeCodeHandlers(paramCode);
    if (j == 0 || bool)
      writeAttrs(3, paramCode, paramCode.thisClass()); 
  }
  
  void writeCodeHandlers(Code paramCode) throws IOException {
    byte b = 0;
    int i = paramCode.getHandlerCount();
    while (b < i) {
      this.code_handler_class_RCN.putRef(paramCode.handler_class[b]);
      int j = paramCode.encodeBCI(paramCode.handler_start[b]);
      this.code_handler_start_P.putInt(j);
      int k = paramCode.encodeBCI(paramCode.handler_end[b]) - j;
      this.code_handler_end_PO.putInt(k);
      j += k;
      k = paramCode.encodeBCI(paramCode.handler_catch[b]) - j;
      this.code_handler_catch_PO.putInt(k);
      b++;
    } 
  }
  
  void writeAttrs(int paramInt, final Attribute.Holder h, Package.Class paramClass) throws IOException {
    BandStructure.MultiBand multiBand = this.attrBands[paramInt];
    BandStructure.IntBand intBand1 = getAttrBand(multiBand, 0);
    BandStructure.IntBand intBand2 = getAttrBand(multiBand, 1);
    boolean bool = haveFlagsHi(paramInt);
    assert this.attrIndexLimit[paramInt] == (bool ? 63 : 32);
    if (paramHolder.attributes == null) {
      intBand2.putInt(paramHolder.flags);
      if (bool)
        intBand1.putInt(0); 
      return;
    } 
    if (this.verbose > 3)
      Utils.log.fine("Transmitting attrs for " + paramHolder + " flags=" + Integer.toHexString(paramHolder.flags)); 
    long l1 = this.attrFlagMask[paramInt];
    long l2 = 0L;
    byte b = 0;
    for (Attribute attribute : paramHolder.attributes) {
      Attribute.Layout layout = attribute.layout();
      int i = ((Integer)this.attrIndexTable.get(layout)).intValue();
      assert ((List)this.attrDefs.get(paramInt)).get(i) == layout;
      if (this.verbose > 3)
        Utils.log.fine("add attr @" + i + " " + attribute + " in " + paramHolder); 
      if (i < this.attrIndexLimit[paramInt] && testBit(l1, 1L << i)) {
        if (this.verbose > 3)
          Utils.log.fine("Adding flag bit 1<<" + i + " in " + Long.toHexString(l1)); 
        assert !testBit(paramHolder.flags, 1L << i);
        l2 |= 1L << i;
        l1 -= (1L << i);
      } else {
        l2 |= 0x10000L;
        b++;
        if (this.verbose > 3)
          Utils.log.fine("Adding overflow attr #" + b); 
        BandStructure.IntBand intBand = getAttrBand(multiBand, 3);
        intBand.putInt(i);
      } 
      if (layout.bandCount == 0) {
        if (layout == this.attrInnerClassesEmpty)
          writeLocalInnerClasses((Package.Class)paramHolder); 
        continue;
      } 
      assert attribute.fixups == null;
      final Band[] ab = (Band[])this.attrBandTable.get(layout);
      assert arrayOfBand != null;
      assert arrayOfBand.length == layout.bandCount;
      final int[] bc = (int[])this.backCountTable.get(layout);
      assert arrayOfInt != null;
      assert arrayOfInt.length == layout.getCallables().length;
      if (this.verbose > 2)
        Utils.log.fine("writing " + attribute + " in " + paramHolder); 
      boolean bool1 = (paramInt == 1 && layout == this.attrConstantValue) ? 1 : 0;
      if (bool1)
        setConstantValueIndex((Package.Class.Field)paramHolder); 
      attribute.parse(paramClass, attribute.bytes(), 0, attribute.size(), new Attribute.ValueStream() {
            public void putInt(int param1Int1, int param1Int2) { ((BandStructure.IntBand)ab[param1Int1]).putInt(param1Int2); }
            
            public void putRef(int param1Int, ConstantPool.Entry param1Entry) { ((BandStructure.CPRefBand)ab[param1Int]).putRef(param1Entry); }
            
            public int encodeBCI(int param1Int) {
              Code code = (Code)h;
              return code.encodeBCI(param1Int);
            }
            
            public void noteBackCall(int param1Int) throws IOException {
              assert bc[param1Int] >= 0;
              bc[param1Int] = bc[param1Int] + 1;
            }
          });
      if (bool1)
        setConstantValueIndex(null); 
    } 
    if (b > 0) {
      BandStructure.IntBand intBand = getAttrBand(multiBand, 2);
      intBand.putInt(b);
    } 
    intBand2.putInt(paramHolder.flags | (int)l2);
    if (bool) {
      intBand1.putInt((int)(l2 >>> 32));
    } else {
      assert l2 >>> 32 == 0L;
    } 
    assert (paramHolder.flags & l2) == 0L : paramHolder + ".flags=" + Integer.toHexString(paramHolder.flags) + "^" + Long.toHexString(l2);
  }
  
  private void beginCode(Code paramCode) throws IOException {
    assert this.curCode == null;
    this.curCode = paramCode;
    this.curClass = paramCode.m.thisClass();
    this.curCPMap = paramCode.getCPMap();
  }
  
  private void endCode() throws IOException {
    this.curCode = null;
    this.curClass = null;
    this.curCPMap = null;
  }
  
  private int initOpVariant(Instruction paramInstruction, ConstantPool.Entry paramEntry) {
    if (paramInstruction.getBC() != 183)
      return -1; 
    ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)paramInstruction.getCPRef(this.curCPMap);
    if (!"<init>".equals(memberEntry.descRef.nameRef.stringValue()))
      return -1; 
    ConstantPool.ClassEntry classEntry = memberEntry.classRef;
    return (classEntry == this.curClass.thisClass) ? 230 : ((classEntry == this.curClass.superClass) ? 231 : ((classEntry == paramEntry) ? 232 : -1));
  }
  
  private int selfOpVariant(Instruction paramInstruction) {
    int i = paramInstruction.getBC();
    if (i < 178 || i > 184)
      return -1; 
    ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)paramInstruction.getCPRef(this.curCPMap);
    if ((i == 183 || i == 184) && memberEntry.tagEquals(11))
      return -1; 
    ConstantPool.ClassEntry classEntry = memberEntry.classRef;
    int j = 202 + i - 178;
    return (classEntry == this.curClass.thisClass) ? j : ((classEntry == this.curClass.superClass) ? (j + 14) : -1);
  }
  
  void writeByteCodes(Code paramCode) throws IOException {
    beginCode(paramCode);
    ConstantPool.IndexGroup indexGroup = this.pkg.cp;
    boolean bool = false;
    ConstantPool.Entry entry = null;
    for (Instruction instruction = paramCode.instructionAt(0); instruction != null; instruction = instruction.next()) {
      if (this.verbose > 3)
        Utils.log.fine(instruction.toString()); 
      if (instruction.isNonstandard()) {
        String str = paramCode.getMethod() + " contains an unrecognized bytecode " + instruction + "; please use the pass-file option on this class.";
        Utils.log.warning(str);
        throw new IOException(str);
      } 
      if (instruction.isWide()) {
        if (this.verbose > 1) {
          Utils.log.fine("_wide opcode in " + paramCode);
          Utils.log.fine(instruction.toString());
        } 
        this.bc_codes.putByte(196);
        this.codeHist[196] = this.codeHist[196] + 1;
      } 
      int i = instruction.getBC();
      if (i == 42) {
        Instruction instruction1 = paramCode.instructionAt(instruction.getNextPC());
        if (selfOpVariant(instruction1) >= 0) {
          bool = true;
          continue;
        } 
      } 
      int j = initOpVariant(instruction, entry);
      if (j >= 0) {
        if (bool) {
          this.bc_codes.putByte(42);
          this.codeHist[42] = this.codeHist[42] + 1;
          bool = false;
        } 
        this.bc_codes.putByte(j);
        this.codeHist[j] = this.codeHist[j] + 1;
        ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)instruction.getCPRef(this.curCPMap);
        int k = indexGroup.getOverloadingIndex(memberEntry);
        this.bc_initref.putInt(k);
      } else {
        int k = selfOpVariant(instruction);
        if (k >= 0) {
          boolean bool1 = Instruction.isFieldOp(i);
          boolean bool2 = (k >= 216) ? 1 : 0;
          boolean bool3 = bool;
          bool = false;
          if (bool3)
            k += 7; 
          this.bc_codes.putByte(k);
          this.codeHist[k] = this.codeHist[k] + 1;
          ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)instruction.getCPRef(this.curCPMap);
          BandStructure.CPRefBand cPRefBand = selfOpRefBand(k);
          ConstantPool.Index index = indexGroup.getMemberIndex(memberEntry.tag, memberEntry.classRef);
          cPRefBand.putRef(memberEntry, index);
        } else {
          int i3;
          int i2;
          int i1;
          int n;
          ConstantPool.Entry entry1;
          int m;
          Instruction.Switch switch;
          assert !bool;
          this.codeHist[i] = this.codeHist[i] + 1;
          switch (i) {
            case 170:
            case 171:
              this.bc_codes.putByte(i);
              switch = (Instruction.Switch)instruction;
              n = switch.getAlignedPC();
              i1 = switch.getNextPC();
              i2 = switch.getCaseCount();
              this.bc_case_count.putInt(i2);
              putLabel(this.bc_label, paramCode, instruction.getPC(), switch.getDefaultLabel());
              for (i3 = 0; i3 < i2; i3++)
                putLabel(this.bc_label, paramCode, instruction.getPC(), switch.getCaseLabel(i3)); 
              if (i == 170) {
                this.bc_case_value.putInt(switch.getCaseValue(0));
                break;
              } 
              for (i3 = 0; i3 < i2; i3++)
                this.bc_case_value.putInt(switch.getCaseValue(i3)); 
              break;
            default:
              m = instruction.getBranchLabel();
              if (m >= 0) {
                this.bc_codes.putByte(i);
                putLabel(this.bc_label, paramCode, instruction.getPC(), m);
                break;
              } 
              entry1 = instruction.getCPRef(this.curCPMap);
              if (entry1 != null) {
                BandStructure.CPRefBand cPRefBand;
                if (i == 187)
                  entry = entry1; 
                if (i == 18)
                  this.ldcHist[entry1.tag] = this.ldcHist[entry1.tag] + 1; 
                i2 = i;
                switch (instruction.getCPTag()) {
                  case 51:
                    switch (entry1.tag) {
                      case 3:
                        cPRefBand = this.bc_intref;
                        switch (i) {
                          case 18:
                            i2 = 234;
                            break;
                          case 19:
                            i2 = 237;
                            break;
                        } 
                        assert false;
                        break;
                      case 4:
                        cPRefBand = this.bc_floatref;
                        switch (i) {
                          case 18:
                            i2 = 235;
                            break;
                          case 19:
                            i2 = 238;
                            break;
                        } 
                        assert false;
                        break;
                      case 5:
                        cPRefBand = this.bc_longref;
                        assert i == 20;
                        i2 = 20;
                        break;
                      case 6:
                        cPRefBand = this.bc_doubleref;
                        assert i == 20;
                        i2 = 239;
                        break;
                      case 8:
                        cPRefBand = this.bc_stringref;
                        switch (i) {
                          case 18:
                            i2 = 18;
                            break;
                          case 19:
                            i2 = 19;
                            break;
                        } 
                        assert false;
                        break;
                      case 7:
                        cPRefBand = this.bc_classref;
                        switch (i) {
                          case 18:
                            i2 = 233;
                            break;
                          case 19:
                            i2 = 236;
                            break;
                        } 
                        assert false;
                        break;
                    } 
                    if (getHighestClassVersion().lessThan(Constants.JAVA7_MAX_CLASS_VERSION))
                      throw new IOException("bad class file major version for Java 7 ldc"); 
                    cPRefBand = this.bc_loadablevalueref;
                    switch (i) {
                      case 18:
                        i2 = 240;
                        break;
                      case 19:
                        i2 = 241;
                        break;
                    } 
                    assert false;
                    break;
                  case 7:
                    if (entry1 == this.curClass.thisClass)
                      entry1 = null; 
                    cPRefBand = this.bc_classref;
                    break;
                  case 9:
                    cPRefBand = this.bc_fieldref;
                    break;
                  case 10:
                    if (entry1.tagEquals(11)) {
                      if (i == 183)
                        i2 = 242; 
                      if (i == 184)
                        i2 = 243; 
                      cPRefBand = this.bc_imethodref;
                      break;
                    } 
                    cPRefBand = this.bc_methodref;
                    break;
                  case 11:
                    cPRefBand = this.bc_imethodref;
                    break;
                  case 18:
                    cPRefBand = this.bc_indyref;
                    break;
                  default:
                    cPRefBand = null;
                    assert false;
                    break;
                } 
                if (entry1 != null && cPRefBand.index != null && !cPRefBand.index.contains(entry1)) {
                  String str = paramCode.getMethod() + " contains a bytecode " + instruction + " with an unsupported constant reference; please use the pass-file option on this class.";
                  Utils.log.warning(str);
                  throw new IOException(str);
                } 
                this.bc_codes.putByte(i2);
                cPRefBand.putRef(entry1);
                if (i == 197) {
                  assert instruction.getConstant() == paramCode.getByte(instruction.getPC() + 3);
                  this.bc_byte.putByte(0xFF & instruction.getConstant());
                  break;
                } 
                if (i == 185) {
                  assert instruction.getLength() == 5;
                  assert instruction.getConstant() == 1 + ((ConstantPool.MemberEntry)entry1).descRef.typeRef.computeSize(true) << 8;
                  break;
                } 
                if (i == 186) {
                  if (getHighestClassVersion().lessThan(Constants.JAVA7_MAX_CLASS_VERSION))
                    throw new IOException("bad class major version for Java 7 invokedynamic"); 
                  assert instruction.getLength() == 5;
                  assert instruction.getConstant() == 0;
                  break;
                } 
                assert instruction.getLength() == ((i == 18) ? 2 : 3);
                break;
              } 
              i1 = instruction.getLocalSlot();
              if (i1 >= 0) {
                this.bc_codes.putByte(i);
                this.bc_local.putInt(i1);
                i2 = instruction.getConstant();
                if (i == 132) {
                  if (!instruction.isWide()) {
                    this.bc_byte.putByte(0xFF & i2);
                    break;
                  } 
                  this.bc_short.putInt(0xFFFF & i2);
                  break;
                } 
                assert i2 == 0;
                break;
              } 
              this.bc_codes.putByte(i);
              i2 = instruction.getPC() + 1;
              i3 = instruction.getNextPC();
              if (i2 < i3) {
                switch (i) {
                  case 17:
                    this.bc_short.putInt(0xFFFF & instruction.getConstant());
                    break;
                  case 16:
                    this.bc_byte.putByte(0xFF & instruction.getConstant());
                    break;
                  case 188:
                    this.bc_byte.putByte(0xFF & instruction.getConstant());
                    break;
                } 
                assert false;
              } 
              break;
          } 
        } 
      } 
      continue;
    } 
    this.bc_codes.putByte(255);
    this.bc_codes.elementCountForDebug++;
    this.codeHist[255] = this.codeHist[255] + 1;
    endCode();
  }
  
  void printCodeHist() throws IOException {
    assert this.verbose > 0;
    String[] arrayOfString = new String[this.codeHist.length];
    int i = 0;
    int j;
    for (j = 0; j < this.codeHist.length; j++)
      i += this.codeHist[j]; 
    for (j = 0; j < this.codeHist.length; j++) {
      if (this.codeHist[j] == 0) {
        arrayOfString[j] = "";
      } else {
        String str1 = Instruction.byteName(j);
        String str2 = "" + this.codeHist[j];
        str2 = "         ".substring(str2.length()) + str2;
        String str3;
        for (str3 = "" + (this.codeHist[j] * 10000 / i); str3.length() < 4; str3 = "0" + str3);
        str3 = str3.substring(0, str3.length() - 2) + "." + str3.substring(str3.length() - 2);
        arrayOfString[j] = str2 + "  " + str3 + "%  " + str1;
      } 
    } 
    Arrays.sort(arrayOfString);
    System.out.println("Bytecode histogram [" + i + "]");
    j = arrayOfString.length;
    while (--j >= 0) {
      if ("".equals(arrayOfString[j]))
        continue; 
      System.out.println(arrayOfString[j]);
    } 
    for (j = 0; j < this.ldcHist.length; j++) {
      int k = this.ldcHist[j];
      if (k != 0)
        System.out.println("ldc " + ConstantPool.tagName(j) + " " + k); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\PackageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */