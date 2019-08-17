package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

class PackageReader extends BandStructure {
  Package pkg;
  
  byte[] bytes;
  
  LimitedBuffer in;
  
  Package.Version packageVersion;
  
  int[] tagCount = new int[19];
  
  int numFiles;
  
  int numAttrDefs;
  
  int numInnerClasses;
  
  int numClasses;
  
  static final int MAGIC_BYTES = 4;
  
  Map<ConstantPool.Utf8Entry, ConstantPool.SignatureEntry> utf8Signatures;
  
  static final int NO_FLAGS_YET = 0;
  
  Comparator<ConstantPool.Entry> entryOutputOrder = new Comparator<ConstantPool.Entry>() {
      public int compare(ConstantPool.Entry param1Entry1, ConstantPool.Entry param1Entry2) {
        int i = PackageReader.this.getOutputIndex(param1Entry1);
        int j = PackageReader.this.getOutputIndex(param1Entry2);
        return (i >= 0 && j >= 0) ? (i - j) : ((i == j) ? param1Entry1.compareTo(param1Entry2) : ((i >= 0) ? -1 : 1));
      }
    };
  
  Code[] allCodes;
  
  List<Code> codesWithFlags;
  
  Map<Package.Class, Set<ConstantPool.Entry>> ldcRefMap = new HashMap();
  
  PackageReader(Package paramPackage, InputStream paramInputStream) throws IOException {
    this.pkg = paramPackage;
    this.in = new LimitedBuffer(paramInputStream);
  }
  
  void read() throws IOException {
    boolean bool = false;
    try {
      readFileHeader();
      readBandHeaders();
      readConstantPool();
      readAttrDefs();
      readInnerClasses();
      Package.Class[] arrayOfClass = readClasses();
      readByteCodes();
      readFiles();
      assert this.archiveSize1 == 0L || this.in.atLimit();
      assert this.archiveSize1 == 0L || this.in.getBytesServed() == this.archiveSize0 + this.archiveSize1;
      this.all_bands.doneDisbursing();
      for (byte b = 0; b < arrayOfClass.length; b++)
        reconstructClass(arrayOfClass[b]); 
      bool = true;
    } catch (Exception exception) {
      Utils.log.warning("Error on input: " + exception, exception);
      if (this.verbose > 0)
        Utils.log.info("Stream offsets: served=" + this.in.getBytesServed() + " buffered=" + this.in.buffered + " limit=" + this.in.limit); 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      if (exception instanceof RuntimeException)
        throw (RuntimeException)exception; 
      throw new Error("error unpacking", exception);
    } 
  }
  
  void readFileHeader() throws IOException {
    readArchiveMagic();
    readArchiveHeader();
  }
  
  private int getMagicInt32() throws IOException {
    int i = 0;
    for (byte b = 0; b < 4; b++) {
      i <<= 8;
      i |= this.archive_magic.getByte() & 0xFF;
    } 
    return i;
  }
  
  void readArchiveMagic() throws IOException {
    this.in.setReadLimit(19L);
    this.archive_magic.expectLength(4);
    this.archive_magic.readFrom(this.in);
    int i = getMagicInt32();
    this.pkg.getClass();
    if (-889270259 != i) {
      this.pkg.getClass();
      throw new IOException("Unexpected package magic number: got " + i + "; expected " + -889270259);
    } 
    this.archive_magic.doneDisbursing();
  }
  
  void checkArchiveVersion() throws IOException {
    Package.Version version = null;
    for (Package.Version version1 : new Package.Version[] { Constants.JAVA8_PACKAGE_VERSION, Constants.JAVA7_PACKAGE_VERSION, Constants.JAVA6_PACKAGE_VERSION, Constants.JAVA5_PACKAGE_VERSION }) {
      if (this.packageVersion.equals(version1)) {
        version = version1;
        break;
      } 
    } 
    if (version == null) {
      String str = Constants.JAVA8_PACKAGE_VERSION.toString() + "OR" + Constants.JAVA7_PACKAGE_VERSION.toString() + " OR " + Constants.JAVA6_PACKAGE_VERSION.toString() + " OR " + Constants.JAVA5_PACKAGE_VERSION.toString();
      throw new IOException("Unexpected package minor version: got " + this.packageVersion.toString() + "; expected " + str);
    } 
  }
  
  void readArchiveHeader() throws IOException {
    this.archive_header_0.expectLength(3);
    this.archive_header_0.readFrom(this.in);
    int i = this.archive_header_0.getInt();
    int j = this.archive_header_0.getInt();
    this.packageVersion = Package.Version.of(j, i);
    checkArchiveVersion();
    initHighestClassVersion(Constants.JAVA7_MAX_CLASS_VERSION);
    this.archiveOptions = this.archive_header_0.getInt();
    this.archive_header_0.doneDisbursing();
    boolean bool1 = testBit(this.archiveOptions, 1);
    boolean bool2 = testBit(this.archiveOptions, 16);
    boolean bool3 = testBit(this.archiveOptions, 2);
    boolean bool4 = testBit(this.archiveOptions, 8);
    initAttrIndexLimit();
    this.archive_header_S.expectLength(bool2 ? 2 : 0);
    this.archive_header_S.readFrom(this.in);
    if (bool2) {
      long l1 = this.archive_header_S.getInt();
      long l2 = this.archive_header_S.getInt();
      this.archiveSize1 = (l1 << 32) + (l2 << 32 >>> 32);
      this.in.setReadLimit(this.archiveSize1);
    } else {
      this.archiveSize1 = 0L;
      this.in.setReadLimit(-1L);
    } 
    this.archive_header_S.doneDisbursing();
    this.archiveSize0 = this.in.getBytesServed();
    byte b = 10;
    if (bool2)
      b += 5; 
    if (bool1)
      b += 2; 
    if (bool3)
      b += 4; 
    if (bool4)
      b += 4; 
    this.archive_header_1.expectLength(b);
    this.archive_header_1.readFrom(this.in);
    if (bool2) {
      this.archiveNextCount = this.archive_header_1.getInt();
      this.pkg.default_modtime = this.archive_header_1.getInt();
      this.numFiles = this.archive_header_1.getInt();
    } else {
      this.archiveNextCount = 0;
      this.numFiles = 0;
    } 
    if (bool1) {
      this.band_headers.expectLength(this.archive_header_1.getInt());
      this.numAttrDefs = this.archive_header_1.getInt();
    } else {
      this.band_headers.expectLength(0);
      this.numAttrDefs = 0;
    } 
    readConstantPoolCounts(bool3, bool4);
    this.numInnerClasses = this.archive_header_1.getInt();
    i = (short)this.archive_header_1.getInt();
    j = (short)this.archive_header_1.getInt();
    this.pkg.defaultClassVersion = Package.Version.of(j, i);
    this.numClasses = this.archive_header_1.getInt();
    this.archive_header_1.doneDisbursing();
    if (testBit(this.archiveOptions, 32))
      this.pkg.default_options |= 0x1; 
  }
  
  void readBandHeaders() throws IOException {
    this.band_headers.readFrom(this.in);
    this.bandHeaderBytePos = 1;
    this.bandHeaderBytes = new byte[this.bandHeaderBytePos + this.band_headers.length()];
    for (int i = this.bandHeaderBytePos; i < this.bandHeaderBytes.length; i++)
      this.bandHeaderBytes[i] = (byte)this.band_headers.getByte(); 
    this.band_headers.doneDisbursing();
  }
  
  void readConstantPoolCounts(boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    byte b = 0;
    while (b < ConstantPool.TAGS_IN_ORDER.length) {
      byte b1 = ConstantPool.TAGS_IN_ORDER[b];
      if (!paramBoolean1) {
        switch (b1) {
          case 3:
          case 4:
          case 5:
          case 6:
            break;
          default:
            if (!paramBoolean2)
              switch (b1) {
                case 15:
                case 16:
                case 17:
                case 18:
                  break;
              }  
            this.tagCount[b1] = this.archive_header_1.getInt();
            break;
        } 
        b++;
      } 
    } 
  }
  
  protected ConstantPool.Index getCPIndex(byte paramByte) { return this.pkg.cp.getIndexByTag(paramByte); }
  
  ConstantPool.Index initCPIndex(byte paramByte, ConstantPool.Entry[] paramArrayOfEntry) {
    if (this.verbose > 3)
      for (byte b = 0; b < paramArrayOfEntry.length; b++)
        Utils.log.fine("cp.add " + paramArrayOfEntry[b]);  
    ConstantPool.Index index = ConstantPool.makeIndex(ConstantPool.tagName(paramByte), paramArrayOfEntry);
    if (this.verbose > 1)
      Utils.log.fine("Read " + index); 
    this.pkg.cp.initIndexByTag(paramByte, index);
    return index;
  }
  
  void checkLegacy(String paramString) {
    if (this.packageVersion.lessThan(Constants.JAVA7_PACKAGE_VERSION))
      throw new RuntimeException("unexpected band " + paramString); 
  }
  
  void readConstantPool() throws IOException {
    if (this.verbose > 0)
      Utils.log.info("Reading CP"); 
    byte b;
    for (b = 0; b < ConstantPool.TAGS_IN_ORDER.length; b++) {
      byte b2;
      int j;
      byte b1 = ConstantPool.TAGS_IN_ORDER[b];
      int i = this.tagCount[b1];
      ConstantPool.Entry[] arrayOfEntry = new ConstantPool.Entry[i];
      if (this.verbose > 0)
        Utils.log.info("Reading " + arrayOfEntry.length + " " + ConstantPool.tagName(b1) + " entries..."); 
      switch (b1) {
        case 1:
          readUtf8Bands(arrayOfEntry);
          break;
        case 3:
          this.cp_Int.expectLength(arrayOfEntry.length);
          this.cp_Int.readFrom(this.in);
          for (j = 0; j < arrayOfEntry.length; j++) {
            int k = this.cp_Int.getInt();
            arrayOfEntry[j] = ConstantPool.getLiteralEntry(Integer.valueOf(k));
          } 
          this.cp_Int.doneDisbursing();
          break;
        case 4:
          this.cp_Float.expectLength(arrayOfEntry.length);
          this.cp_Float.readFrom(this.in);
          for (j = 0; j < arrayOfEntry.length; j++) {
            int k = this.cp_Float.getInt();
            float f = Float.intBitsToFloat(k);
            arrayOfEntry[j] = ConstantPool.getLiteralEntry(Float.valueOf(f));
          } 
          this.cp_Float.doneDisbursing();
          break;
        case 5:
          this.cp_Long_hi.expectLength(arrayOfEntry.length);
          this.cp_Long_hi.readFrom(this.in);
          this.cp_Long_lo.expectLength(arrayOfEntry.length);
          this.cp_Long_lo.readFrom(this.in);
          for (j = 0; j < arrayOfEntry.length; j++) {
            long l1 = this.cp_Long_hi.getInt();
            long l2 = this.cp_Long_lo.getInt();
            long l3 = (l1 << 32) + (l2 << 32 >>> 32);
            arrayOfEntry[j] = ConstantPool.getLiteralEntry(Long.valueOf(l3));
          } 
          this.cp_Long_hi.doneDisbursing();
          this.cp_Long_lo.doneDisbursing();
          break;
        case 6:
          this.cp_Double_hi.expectLength(arrayOfEntry.length);
          this.cp_Double_hi.readFrom(this.in);
          this.cp_Double_lo.expectLength(arrayOfEntry.length);
          this.cp_Double_lo.readFrom(this.in);
          for (j = 0; j < arrayOfEntry.length; j++) {
            long l1 = this.cp_Double_hi.getInt();
            long l2 = this.cp_Double_lo.getInt();
            long l3 = (l1 << 32) + (l2 << 32 >>> 32);
            double d = Double.longBitsToDouble(l3);
            arrayOfEntry[j] = ConstantPool.getLiteralEntry(Double.valueOf(d));
          } 
          this.cp_Double_hi.doneDisbursing();
          this.cp_Double_lo.doneDisbursing();
          break;
        case 8:
          this.cp_String.expectLength(arrayOfEntry.length);
          this.cp_String.readFrom(this.in);
          this.cp_String.setIndex(getCPIndex((byte)1));
          for (j = 0; j < arrayOfEntry.length; j++)
            arrayOfEntry[j] = ConstantPool.getLiteralEntry(this.cp_String.getRef().stringValue()); 
          this.cp_String.doneDisbursing();
          break;
        case 7:
          this.cp_Class.expectLength(arrayOfEntry.length);
          this.cp_Class.readFrom(this.in);
          this.cp_Class.setIndex(getCPIndex((byte)1));
          for (j = 0; j < arrayOfEntry.length; j++)
            arrayOfEntry[j] = ConstantPool.getClassEntry(this.cp_Class.getRef().stringValue()); 
          this.cp_Class.doneDisbursing();
          break;
        case 13:
          readSignatureBands(arrayOfEntry);
          break;
        case 12:
          this.cp_Descr_name.expectLength(arrayOfEntry.length);
          this.cp_Descr_name.readFrom(this.in);
          this.cp_Descr_name.setIndex(getCPIndex((byte)1));
          this.cp_Descr_type.expectLength(arrayOfEntry.length);
          this.cp_Descr_type.readFrom(this.in);
          this.cp_Descr_type.setIndex(getCPIndex((byte)13));
          for (j = 0; j < arrayOfEntry.length; j++) {
            ConstantPool.Entry entry1 = this.cp_Descr_name.getRef();
            ConstantPool.Entry entry2 = this.cp_Descr_type.getRef();
            arrayOfEntry[j] = ConstantPool.getDescriptorEntry((ConstantPool.Utf8Entry)entry1, (ConstantPool.SignatureEntry)entry2);
          } 
          this.cp_Descr_name.doneDisbursing();
          this.cp_Descr_type.doneDisbursing();
          break;
        case 9:
          readMemberRefs(b1, arrayOfEntry, this.cp_Field_class, this.cp_Field_desc);
          break;
        case 10:
          readMemberRefs(b1, arrayOfEntry, this.cp_Method_class, this.cp_Method_desc);
          break;
        case 11:
          readMemberRefs(b1, arrayOfEntry, this.cp_Imethod_class, this.cp_Imethod_desc);
          break;
        case 15:
          if (arrayOfEntry.length > 0)
            checkLegacy(this.cp_MethodHandle_refkind.name()); 
          this.cp_MethodHandle_refkind.expectLength(arrayOfEntry.length);
          this.cp_MethodHandle_refkind.readFrom(this.in);
          this.cp_MethodHandle_member.expectLength(arrayOfEntry.length);
          this.cp_MethodHandle_member.readFrom(this.in);
          this.cp_MethodHandle_member.setIndex(getCPIndex((byte)52));
          for (j = 0; j < arrayOfEntry.length; j++) {
            byte b3 = (byte)this.cp_MethodHandle_refkind.getInt();
            ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)this.cp_MethodHandle_member.getRef();
            arrayOfEntry[j] = ConstantPool.getMethodHandleEntry(b3, memberEntry);
          } 
          this.cp_MethodHandle_refkind.doneDisbursing();
          this.cp_MethodHandle_member.doneDisbursing();
          break;
        case 16:
          if (arrayOfEntry.length > 0)
            checkLegacy(this.cp_MethodType.name()); 
          this.cp_MethodType.expectLength(arrayOfEntry.length);
          this.cp_MethodType.readFrom(this.in);
          this.cp_MethodType.setIndex(getCPIndex((byte)13));
          for (j = 0; j < arrayOfEntry.length; j++) {
            ConstantPool.SignatureEntry signatureEntry = (ConstantPool.SignatureEntry)this.cp_MethodType.getRef();
            arrayOfEntry[j] = ConstantPool.getMethodTypeEntry(signatureEntry);
          } 
          this.cp_MethodType.doneDisbursing();
          break;
        case 18:
          if (arrayOfEntry.length > 0)
            checkLegacy(this.cp_InvokeDynamic_spec.name()); 
          this.cp_InvokeDynamic_spec.expectLength(arrayOfEntry.length);
          this.cp_InvokeDynamic_spec.readFrom(this.in);
          this.cp_InvokeDynamic_spec.setIndex(getCPIndex((byte)17));
          this.cp_InvokeDynamic_desc.expectLength(arrayOfEntry.length);
          this.cp_InvokeDynamic_desc.readFrom(this.in);
          this.cp_InvokeDynamic_desc.setIndex(getCPIndex((byte)12));
          for (j = 0; j < arrayOfEntry.length; j++) {
            ConstantPool.BootstrapMethodEntry bootstrapMethodEntry = (ConstantPool.BootstrapMethodEntry)this.cp_InvokeDynamic_spec.getRef();
            ConstantPool.DescriptorEntry descriptorEntry = (ConstantPool.DescriptorEntry)this.cp_InvokeDynamic_desc.getRef();
            arrayOfEntry[j] = ConstantPool.getInvokeDynamicEntry(bootstrapMethodEntry, descriptorEntry);
          } 
          this.cp_InvokeDynamic_spec.doneDisbursing();
          this.cp_InvokeDynamic_desc.doneDisbursing();
          break;
        case 17:
          if (arrayOfEntry.length > 0)
            checkLegacy(this.cp_BootstrapMethod_ref.name()); 
          this.cp_BootstrapMethod_ref.expectLength(arrayOfEntry.length);
          this.cp_BootstrapMethod_ref.readFrom(this.in);
          this.cp_BootstrapMethod_ref.setIndex(getCPIndex((byte)15));
          this.cp_BootstrapMethod_arg_count.expectLength(arrayOfEntry.length);
          this.cp_BootstrapMethod_arg_count.readFrom(this.in);
          j = this.cp_BootstrapMethod_arg_count.getIntTotal();
          this.cp_BootstrapMethod_arg.expectLength(j);
          this.cp_BootstrapMethod_arg.readFrom(this.in);
          this.cp_BootstrapMethod_arg.setIndex(getCPIndex((byte)51));
          for (b2 = 0; b2 < arrayOfEntry.length; b2++) {
            ConstantPool.MethodHandleEntry methodHandleEntry = (ConstantPool.MethodHandleEntry)this.cp_BootstrapMethod_ref.getRef();
            int k = this.cp_BootstrapMethod_arg_count.getInt();
            ConstantPool.Entry[] arrayOfEntry1 = new ConstantPool.Entry[k];
            for (byte b3 = 0; b3 < k; b3++)
              arrayOfEntry1[b3] = this.cp_BootstrapMethod_arg.getRef(); 
            arrayOfEntry[b2] = ConstantPool.getBootstrapMethodEntry(methodHandleEntry, arrayOfEntry1);
          } 
          this.cp_BootstrapMethod_ref.doneDisbursing();
          this.cp_BootstrapMethod_arg_count.doneDisbursing();
          this.cp_BootstrapMethod_arg.doneDisbursing();
          break;
        default:
          throw new AssertionError("unexpected CP tag in package");
      } 
      ConstantPool.Index index = initCPIndex(b1, arrayOfEntry);
      if (this.optDumpBands)
        try (PrintStream null = new PrintStream(getDumpStream(index, ".idx"))) {
          printArrayTo(printStream, index.cpMap, 0, index.cpMap.length);
        }  
    } 
    this.cp_bands.doneDisbursing();
    if (this.optDumpBands || this.verbose > 1)
      for (b = 50; b < 54; b = (byte)(b + 1)) {
        ConstantPool.Index index = this.pkg.cp.getIndexByTag(b);
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
    setBandIndexes();
  }
  
  void readUtf8Bands(ConstantPool.Entry[] paramArrayOfEntry) throws IOException {
    int i = paramArrayOfEntry.length;
    if (i == 0)
      return; 
    this.cp_Utf8_prefix.expectLength(Math.max(0, i - 2));
    this.cp_Utf8_prefix.readFrom(this.in);
    this.cp_Utf8_suffix.expectLength(Math.max(0, i - 1));
    this.cp_Utf8_suffix.readFrom(this.in);
    char[][] arrayOfChar = new char[i][];
    byte b1 = 0;
    this.cp_Utf8_chars.expectLength(this.cp_Utf8_suffix.getIntTotal());
    this.cp_Utf8_chars.readFrom(this.in);
    int j;
    for (j = 0; j < i; j++) {
      boolean bool = (j < 1) ? 0 : this.cp_Utf8_suffix.getInt();
      if (!bool && j >= 1) {
        b1++;
      } else {
        arrayOfChar[j] = new char[bool];
        for (byte b = 0; b < bool; b++) {
          int k = this.cp_Utf8_chars.getInt();
          assert k == (char)k;
          arrayOfChar[j][b] = (char)k;
        } 
      } 
    } 
    this.cp_Utf8_chars.doneDisbursing();
    j = 0;
    this.cp_Utf8_big_suffix.expectLength(b1);
    this.cp_Utf8_big_suffix.readFrom(this.in);
    this.cp_Utf8_suffix.resetForSecondPass();
    for (byte b2 = 0; b2 < i; b2++) {
      int k = (b2 < 1) ? 0 : this.cp_Utf8_suffix.getInt();
      int m = (b2 < 2) ? 0 : this.cp_Utf8_prefix.getInt();
      if (!k && b2 >= 1) {
        assert arrayOfChar[b2] == null;
        k = this.cp_Utf8_big_suffix.getInt();
      } else {
        assert arrayOfChar[b2] != null;
      } 
      if (j < m + k)
        j = m + k; 
    } 
    char[] arrayOfChar1 = new char[j];
    this.cp_Utf8_suffix.resetForSecondPass();
    this.cp_Utf8_big_suffix.resetForSecondPass();
    byte b3;
    for (b3 = 0; b3 < i; b3++) {
      if (b3 >= 1) {
        int k = this.cp_Utf8_suffix.getInt();
        if (k == 0) {
          k = this.cp_Utf8_big_suffix.getInt();
          arrayOfChar[b3] = new char[k];
          if (k != 0) {
            BandStructure.IntBand intBand = this.cp_Utf8_big_chars.newIntBand("(Utf8_big_" + b3 + ")");
            intBand.expectLength(k);
            intBand.readFrom(this.in);
            for (byte b = 0; b < k; b++) {
              int m = intBand.getInt();
              assert m == (char)m;
              arrayOfChar[b3][b] = (char)m;
            } 
            intBand.doneDisbursing();
          } 
        } 
      } 
    } 
    this.cp_Utf8_big_chars.doneDisbursing();
    this.cp_Utf8_prefix.resetForSecondPass();
    this.cp_Utf8_suffix.resetForSecondPass();
    this.cp_Utf8_big_suffix.resetForSecondPass();
    for (b3 = 0; b3 < i; b3++) {
      int k = (b3 < 2) ? 0 : this.cp_Utf8_prefix.getInt();
      int m = (b3 < 1) ? 0 : this.cp_Utf8_suffix.getInt();
      if (!m && b3 >= 1)
        m = this.cp_Utf8_big_suffix.getInt(); 
      System.arraycopy(arrayOfChar[b3], 0, arrayOfChar1, k, m);
      paramArrayOfEntry[b3] = ConstantPool.getUtf8Entry(new String(arrayOfChar1, 0, k + m));
    } 
    this.cp_Utf8_prefix.doneDisbursing();
    this.cp_Utf8_suffix.doneDisbursing();
    this.cp_Utf8_big_suffix.doneDisbursing();
  }
  
  void readSignatureBands(ConstantPool.Entry[] paramArrayOfEntry) throws IOException {
    this.cp_Signature_form.expectLength(paramArrayOfEntry.length);
    this.cp_Signature_form.readFrom(this.in);
    this.cp_Signature_form.setIndex(getCPIndex((byte)1));
    int[] arrayOfInt = new int[paramArrayOfEntry.length];
    byte b;
    for (b = 0; b < paramArrayOfEntry.length; b++) {
      ConstantPool.Utf8Entry utf8Entry = (ConstantPool.Utf8Entry)this.cp_Signature_form.getRef();
      arrayOfInt[b] = ConstantPool.countClassParts(utf8Entry);
    } 
    this.cp_Signature_form.resetForSecondPass();
    this.cp_Signature_classes.expectLength(getIntTotal(arrayOfInt));
    this.cp_Signature_classes.readFrom(this.in);
    this.cp_Signature_classes.setIndex(getCPIndex((byte)7));
    this.utf8Signatures = new HashMap();
    for (b = 0; b < paramArrayOfEntry.length; b++) {
      ConstantPool.Utf8Entry utf8Entry = (ConstantPool.Utf8Entry)this.cp_Signature_form.getRef();
      ConstantPool.ClassEntry[] arrayOfClassEntry = new ConstantPool.ClassEntry[arrayOfInt[b]];
      for (byte b1 = 0; b1 < arrayOfClassEntry.length; b1++)
        arrayOfClassEntry[b1] = (ConstantPool.ClassEntry)this.cp_Signature_classes.getRef(); 
      ConstantPool.SignatureEntry signatureEntry = ConstantPool.getSignatureEntry(utf8Entry, arrayOfClassEntry);
      paramArrayOfEntry[b] = signatureEntry;
      this.utf8Signatures.put(signatureEntry.asUtf8Entry(), signatureEntry);
    } 
    this.cp_Signature_form.doneDisbursing();
    this.cp_Signature_classes.doneDisbursing();
  }
  
  void readMemberRefs(byte paramByte, ConstantPool.Entry[] paramArrayOfEntry, BandStructure.CPRefBand paramCPRefBand1, BandStructure.CPRefBand paramCPRefBand2) throws IOException {
    paramCPRefBand1.expectLength(paramArrayOfEntry.length);
    paramCPRefBand1.readFrom(this.in);
    paramCPRefBand1.setIndex(getCPIndex((byte)7));
    paramCPRefBand2.expectLength(paramArrayOfEntry.length);
    paramCPRefBand2.readFrom(this.in);
    paramCPRefBand2.setIndex(getCPIndex((byte)12));
    for (byte b = 0; b < paramArrayOfEntry.length; b++) {
      ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry)paramCPRefBand1.getRef();
      ConstantPool.DescriptorEntry descriptorEntry = (ConstantPool.DescriptorEntry)paramCPRefBand2.getRef();
      paramArrayOfEntry[b] = ConstantPool.getMemberEntry(paramByte, classEntry, descriptorEntry);
    } 
    paramCPRefBand1.doneDisbursing();
    paramCPRefBand2.doneDisbursing();
  }
  
  void readFiles() throws IOException {
    if (this.verbose > 0)
      Utils.log.info("  ...building " + this.numFiles + " files..."); 
    this.file_name.expectLength(this.numFiles);
    this.file_size_lo.expectLength(this.numFiles);
    int i = this.archiveOptions;
    boolean bool1 = testBit(i, 256);
    boolean bool2 = testBit(i, 64);
    boolean bool3 = testBit(i, 128);
    if (bool1)
      this.file_size_hi.expectLength(this.numFiles); 
    if (bool2)
      this.file_modtime.expectLength(this.numFiles); 
    if (bool3)
      this.file_options.expectLength(this.numFiles); 
    this.file_name.readFrom(this.in);
    this.file_size_hi.readFrom(this.in);
    this.file_size_lo.readFrom(this.in);
    this.file_modtime.readFrom(this.in);
    this.file_options.readFrom(this.in);
    this.file_bits.setInputStreamFrom(this.in);
    Iterator iterator = this.pkg.getClasses().iterator();
    long l = 0L;
    long[] arrayOfLong = new long[this.numFiles];
    for (byte b1 = 0; b1 < this.numFiles; b1++) {
      long l1 = this.file_size_lo.getInt() << 32 >>> 32;
      if (bool1)
        l1 += (this.file_size_hi.getInt() << 32); 
      arrayOfLong[b1] = l1;
      l += l1;
    } 
    assert this.in.getReadLimit() == -1L || this.in.getReadLimit() == l;
    byte[] arrayOfByte = new byte[65536];
    for (byte b2 = 0; b2 < this.numFiles; b2++) {
      ConstantPool.Utf8Entry utf8Entry = (ConstantPool.Utf8Entry)this.file_name.getRef();
      long l1 = arrayOfLong[b2];
      this.pkg.getClass();
      Package.File file = new Package.File(this.pkg, utf8Entry);
      file.modtime = this.pkg.default_modtime;
      file.options = this.pkg.default_options;
      if (bool2)
        file.modtime += this.file_modtime.getInt(); 
      if (bool3)
        file.options |= this.file_options.getInt(); 
      if (this.verbose > 1)
        Utils.log.fine("Reading " + l1 + " bytes of " + utf8Entry.stringValue()); 
      long l2;
      for (l2 = l1; l2 > 0L; l2 -= j) {
        int j = arrayOfByte.length;
        if (j > l2)
          j = (int)l2; 
        j = this.file_bits.getInputStream().read(arrayOfByte, 0, j);
        if (j < 0)
          throw new EOFException(); 
        file.addBytes(arrayOfByte, 0, j);
      } 
      this.pkg.addFile(file);
      if (file.isClassStub()) {
        assert file.getFileLength() == 0L;
        Package.Class clazz = (Package.Class)iterator.next();
        clazz.initFile(file);
      } 
    } 
    while (iterator.hasNext()) {
      Package.Class clazz = (Package.Class)iterator.next();
      clazz.initFile(null);
      clazz.file.modtime = this.pkg.default_modtime;
    } 
    this.file_name.doneDisbursing();
    this.file_size_hi.doneDisbursing();
    this.file_size_lo.doneDisbursing();
    this.file_modtime.doneDisbursing();
    this.file_options.doneDisbursing();
    this.file_bits.doneDisbursing();
    this.file_bands.doneDisbursing();
    if (this.archiveSize1 != 0L && !this.in.atLimit())
      throw new RuntimeException("Predicted archive_size " + this.archiveSize1 + " != " + (this.in.getBytesServed() - this.archiveSize0)); 
  }
  
  void readAttrDefs() throws IOException {
    this.attr_definition_headers.expectLength(this.numAttrDefs);
    this.attr_definition_name.expectLength(this.numAttrDefs);
    this.attr_definition_layout.expectLength(this.numAttrDefs);
    this.attr_definition_headers.readFrom(this.in);
    this.attr_definition_name.readFrom(this.in);
    this.attr_definition_layout.readFrom(this.in);
    try (PrintStream null = !this.optDumpBands ? null : new PrintStream(getDumpStream(this.attr_definition_headers, ".def"))) {
      for (b = 0; b < this.numAttrDefs; b++) {
        int i = this.attr_definition_headers.getByte();
        ConstantPool.Utf8Entry utf8Entry1 = (ConstantPool.Utf8Entry)this.attr_definition_name.getRef();
        ConstantPool.Utf8Entry utf8Entry2 = (ConstantPool.Utf8Entry)this.attr_definition_layout.getRef();
        int j = i & 0x3;
        int k = (i >> 2) - 1;
        Attribute.Layout layout = new Attribute.Layout(j, utf8Entry1.stringValue(), utf8Entry2.stringValue());
        String str = layout.layoutForClassVersion(getHighestClassVersion());
        if (!str.equals(layout.layout()))
          throw new IOException("Bad attribute layout in archive: " + layout.layout()); 
        setAttributeLayoutIndex(layout, k);
        if (printStream != null)
          printStream.println(k + " " + layout); 
      } 
    } 
    this.attr_definition_headers.doneDisbursing();
    this.attr_definition_name.doneDisbursing();
    this.attr_definition_layout.doneDisbursing();
    makeNewAttributeBands();
    this.attr_definition_bands.doneDisbursing();
  }
  
  void readInnerClasses() throws IOException {
    this.ic_this_class.expectLength(this.numInnerClasses);
    this.ic_this_class.readFrom(this.in);
    this.ic_flags.expectLength(this.numInnerClasses);
    this.ic_flags.readFrom(this.in);
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.numInnerClasses; b2++) {
      int i = this.ic_flags.getInt();
      boolean bool = ((i & 0x10000) != 0) ? 1 : 0;
      if (bool)
        b1++; 
    } 
    this.ic_outer_class.expectLength(b1);
    this.ic_outer_class.readFrom(this.in);
    this.ic_name.expectLength(b1);
    this.ic_name.readFrom(this.in);
    this.ic_flags.resetForSecondPass();
    ArrayList arrayList = new ArrayList(this.numInnerClasses);
    for (byte b3 = 0; b3 < this.numInnerClasses; b3++) {
      ConstantPool.Utf8Entry utf8Entry;
      ConstantPool.ClassEntry classEntry2;
      int i = this.ic_flags.getInt();
      boolean bool = ((i & 0x10000) != 0) ? 1 : 0;
      i &= 0xFFFEFFFF;
      ConstantPool.ClassEntry classEntry1 = (ConstantPool.ClassEntry)this.ic_this_class.getRef();
      if (bool) {
        classEntry2 = (ConstantPool.ClassEntry)this.ic_outer_class.getRef();
        utf8Entry = (ConstantPool.Utf8Entry)this.ic_name.getRef();
      } else {
        String str1 = classEntry1.stringValue();
        String[] arrayOfString = Package.parseInnerClassName(str1);
        assert arrayOfString != null;
        String str2 = arrayOfString[0];
        String str3 = arrayOfString[2];
        if (str2 == null) {
          classEntry2 = null;
        } else {
          classEntry2 = ConstantPool.getClassEntry(str2);
        } 
        if (str3 == null) {
          utf8Entry = null;
        } else {
          utf8Entry = ConstantPool.getUtf8Entry(str3);
        } 
      } 
      Package.InnerClass innerClass = new Package.InnerClass(classEntry1, classEntry2, utf8Entry, i);
      assert bool || innerClass.predictable;
      arrayList.add(innerClass);
    } 
    this.ic_flags.doneDisbursing();
    this.ic_this_class.doneDisbursing();
    this.ic_outer_class.doneDisbursing();
    this.ic_name.doneDisbursing();
    this.pkg.setAllInnerClasses(arrayList);
    this.ic_bands.doneDisbursing();
  }
  
  void readLocalInnerClasses(Package.Class paramClass) throws IOException {
    int i = this.class_InnerClasses_N.getInt();
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry)this.class_InnerClasses_RC.getRef();
      int j = this.class_InnerClasses_F.getInt();
      if (j == 0) {
        Package.InnerClass innerClass = this.pkg.getGlobalInnerClass(classEntry);
        assert innerClass != null;
        arrayList.add(innerClass);
      } else {
        if (j == 65536)
          j = 0; 
        ConstantPool.ClassEntry classEntry1 = (ConstantPool.ClassEntry)this.class_InnerClasses_outer_RCN.getRef();
        ConstantPool.Utf8Entry utf8Entry = (ConstantPool.Utf8Entry)this.class_InnerClasses_name_RUN.getRef();
        arrayList.add(new Package.InnerClass(classEntry, classEntry1, utf8Entry, j));
      } 
    } 
    paramClass.setInnerClasses(arrayList);
  }
  
  Package.Class[] readClasses() throws IOException {
    Package.Class[] arrayOfClass = new Package.Class[this.numClasses];
    if (this.verbose > 0)
      Utils.log.info("  ...building " + arrayOfClass.length + " classes..."); 
    this.class_this.expectLength(this.numClasses);
    this.class_super.expectLength(this.numClasses);
    this.class_interface_count.expectLength(this.numClasses);
    this.class_this.readFrom(this.in);
    this.class_super.readFrom(this.in);
    this.class_interface_count.readFrom(this.in);
    this.class_interface.expectLength(this.class_interface_count.getIntTotal());
    this.class_interface.readFrom(this.in);
    for (byte b = 0; b < arrayOfClass.length; b++) {
      ConstantPool.ClassEntry classEntry1 = (ConstantPool.ClassEntry)this.class_this.getRef();
      ConstantPool.ClassEntry classEntry2 = (ConstantPool.ClassEntry)this.class_super.getRef();
      ConstantPool.ClassEntry[] arrayOfClassEntry = new ConstantPool.ClassEntry[this.class_interface_count.getInt()];
      for (byte b1 = 0; b1 < arrayOfClassEntry.length; b1++)
        arrayOfClassEntry[b1] = (ConstantPool.ClassEntry)this.class_interface.getRef(); 
      if (classEntry2 == classEntry1)
        classEntry2 = null; 
      this.pkg.getClass();
      Package.Class clazz = new Package.Class(this.pkg, 0, classEntry1, classEntry2, arrayOfClassEntry);
      arrayOfClass[b] = clazz;
    } 
    this.class_this.doneDisbursing();
    this.class_super.doneDisbursing();
    this.class_interface_count.doneDisbursing();
    this.class_interface.doneDisbursing();
    readMembers(arrayOfClass);
    countAndReadAttrs(0, Arrays.asList(arrayOfClass));
    this.pkg.trimToSize();
    readCodeHeaders();
    return arrayOfClass;
  }
  
  private int getOutputIndex(ConstantPool.Entry paramEntry) {
    assert paramEntry.tag != 13;
    int i = this.pkg.cp.untypedIndexOf(paramEntry);
    if (i >= 0)
      return i; 
    if (paramEntry.tag == 1) {
      ConstantPool.Entry entry = (ConstantPool.Entry)this.utf8Signatures.get(paramEntry);
      return this.pkg.cp.untypedIndexOf(entry);
    } 
    return -1;
  }
  
  void reconstructClass(Package.Class paramClass) throws IOException {
    if (this.verbose > 1)
      Utils.log.fine("reconstruct " + paramClass); 
    Attribute attribute = paramClass.getAttribute(this.attrClassFileVersion);
    if (attribute != null) {
      paramClass.removeAttribute(attribute);
      paramClass.version = parseClassFileVersionAttr(attribute);
    } else {
      paramClass.version = this.pkg.defaultClassVersion;
    } 
    paramClass.expandSourceFile();
    paramClass.setCPMap(reconstructLocalCPMap(paramClass));
  }
  
  ConstantPool.Entry[] reconstructLocalCPMap(Package.Class paramClass) {
    Set set = (Set)this.ldcRefMap.get(paramClass);
    HashSet hashSet1 = new HashSet();
    paramClass.visitRefs(0, hashSet1);
    ArrayList arrayList = new ArrayList();
    paramClass.addAttribute(Package.attrBootstrapMethodsEmpty.canonicalInstance());
    ConstantPool.completeReferencesIn(hashSet1, true, arrayList);
    int i = paramClass.expandLocalICs();
    if (i != 0) {
      if (i > 0) {
        paramClass.visitInnerClassRefs(0, hashSet1);
      } else {
        hashSet1.clear();
        paramClass.visitRefs(0, hashSet1);
      } 
      ConstantPool.completeReferencesIn(hashSet1, true, arrayList);
    } 
    if (arrayList.isEmpty()) {
      paramClass.attributes.remove(Package.attrBootstrapMethodsEmpty.canonicalInstance());
    } else {
      hashSet1.add(Package.getRefString("BootstrapMethods"));
      Collections.sort(arrayList);
      paramClass.setBootstrapMethods(arrayList);
    } 
    byte b1 = 0;
    for (ConstantPool.Entry entry : hashSet1) {
      if (entry.isDoubleWord())
        b1++; 
    } 
    ConstantPool.Entry[] arrayOfEntry = new ConstantPool.Entry[1 + b1 + hashSet1.size()];
    byte b2 = 1;
    if (set != null) {
      assert hashSet1.containsAll(set);
      for (ConstantPool.Entry entry : set)
        arrayOfEntry[b2++] = entry; 
      assert b2 == 1 + set.size();
      hashSet1.removeAll(set);
      set = null;
    } 
    HashSet hashSet2 = hashSet1;
    hashSet1 = null;
    int j = b2;
    for (ConstantPool.Entry entry : hashSet2)
      arrayOfEntry[b2++] = entry; 
    assert b2 == j + hashSet2.size();
    Arrays.sort(arrayOfEntry, 1, j, this.entryOutputOrder);
    Arrays.sort(arrayOfEntry, j, b2, this.entryOutputOrder);
    if (this.verbose > 3) {
      Utils.log.fine("CP of " + this + " {");
      for (byte b = 0; b < b2; b++) {
        ConstantPool.Entry entry = arrayOfEntry[b];
        Utils.log.fine("  " + ((entry == null) ? -1 : getOutputIndex(entry)) + " : " + entry);
      } 
      Utils.log.fine("}");
    } 
    int k = arrayOfEntry.length;
    byte b3 = b2;
    while (--b3 >= 1) {
      ConstantPool.Entry entry = arrayOfEntry[b3];
      if (entry.isDoubleWord())
        arrayOfEntry[--k] = null; 
      arrayOfEntry[--k] = entry;
    } 
    assert k == 1;
    return arrayOfEntry;
  }
  
  void readMembers(Package.Class[] paramArrayOfClass) throws IOException {
    assert paramArrayOfClass.length == this.numClasses;
    this.class_field_count.expectLength(this.numClasses);
    this.class_method_count.expectLength(this.numClasses);
    this.class_field_count.readFrom(this.in);
    this.class_method_count.readFrom(this.in);
    int i = this.class_field_count.getIntTotal();
    int j = this.class_method_count.getIntTotal();
    this.field_descr.expectLength(i);
    this.method_descr.expectLength(j);
    if (this.verbose > 1)
      Utils.log.fine("expecting #fields=" + i + " and #methods=" + j + " in #classes=" + this.numClasses); 
    ArrayList arrayList1 = new ArrayList(i);
    this.field_descr.readFrom(this.in);
    for (byte b1 = 0; b1 < paramArrayOfClass.length; b1++) {
      Package.Class clazz = paramArrayOfClass[b1];
      int k = this.class_field_count.getInt();
      for (byte b = 0; b < k; b++) {
        clazz.getClass();
        Package.Class.Field field = new Package.Class.Field(clazz, 0, (ConstantPool.DescriptorEntry)this.field_descr.getRef());
        arrayList1.add(field);
      } 
    } 
    this.class_field_count.doneDisbursing();
    this.field_descr.doneDisbursing();
    countAndReadAttrs(1, arrayList1);
    arrayList1 = null;
    ArrayList arrayList2 = new ArrayList(j);
    this.method_descr.readFrom(this.in);
    for (byte b2 = 0; b2 < paramArrayOfClass.length; b2++) {
      Package.Class clazz = paramArrayOfClass[b2];
      int k = this.class_method_count.getInt();
      for (byte b = 0; b < k; b++) {
        clazz.getClass();
        Package.Class.Method method = new Package.Class.Method(clazz, 0, (ConstantPool.DescriptorEntry)this.method_descr.getRef());
        arrayList2.add(method);
      } 
    } 
    this.class_method_count.doneDisbursing();
    this.method_descr.doneDisbursing();
    countAndReadAttrs(2, arrayList2);
    this.allCodes = buildCodeAttrs(arrayList2);
  }
  
  Code[] buildCodeAttrs(List<Package.Class.Method> paramList) {
    ArrayList arrayList = new ArrayList(paramList.size());
    for (Package.Class.Method method : paramList) {
      if (method.getAttribute(this.attrCodeEmpty) != null) {
        method.code = new Code(method);
        arrayList.add(method.code);
      } 
    } 
    Code[] arrayOfCode = new Code[arrayList.size()];
    arrayList.toArray(arrayOfCode);
    return arrayOfCode;
  }
  
  void readCodeHeaders() throws IOException {
    boolean bool = testBit(this.archiveOptions, 4);
    this.code_headers.expectLength(this.allCodes.length);
    this.code_headers.readFrom(this.in);
    ArrayList arrayList = new ArrayList(this.allCodes.length / 10);
    for (byte b = 0; b < this.allCodes.length; b++) {
      Code code = this.allCodes[b];
      int i = this.code_headers.getByte();
      assert i == (i & 0xFF);
      if (this.verbose > 2)
        Utils.log.fine("codeHeader " + code + " = " + i); 
      if (i == 0) {
        arrayList.add(code);
      } else {
        code.setMaxStack(shortCodeHeader_max_stack(i));
        code.setMaxNALocals(shortCodeHeader_max_na_locals(i));
        code.setHandlerCount(shortCodeHeader_handler_count(i));
        assert shortCodeHeader(code) == i;
      } 
    } 
    this.code_headers.doneDisbursing();
    this.code_max_stack.expectLength(arrayList.size());
    this.code_max_na_locals.expectLength(arrayList.size());
    this.code_handler_count.expectLength(arrayList.size());
    this.code_max_stack.readFrom(this.in);
    this.code_max_na_locals.readFrom(this.in);
    this.code_handler_count.readFrom(this.in);
    for (Code code : arrayList) {
      code.setMaxStack(this.code_max_stack.getInt());
      code.setMaxNALocals(this.code_max_na_locals.getInt());
      code.setHandlerCount(this.code_handler_count.getInt());
    } 
    this.code_max_stack.doneDisbursing();
    this.code_max_na_locals.doneDisbursing();
    this.code_handler_count.doneDisbursing();
    readCodeHandlers();
    if (bool) {
      this.codesWithFlags = Arrays.asList(this.allCodes);
    } else {
      this.codesWithFlags = arrayList;
    } 
    countAttrs(3, this.codesWithFlags);
  }
  
  void readCodeHandlers() throws IOException {
    int i = 0;
    for (byte b1 = 0; b1 < this.allCodes.length; b1++) {
      Code code = this.allCodes[b1];
      i += code.getHandlerCount();
    } 
    BandStructure.ValueBand[] arrayOfValueBand = { this.code_handler_start_P, this.code_handler_end_PO, this.code_handler_catch_PO, this.code_handler_class_RCN };
    byte b2;
    for (b2 = 0; b2 < arrayOfValueBand.length; b2++) {
      arrayOfValueBand[b2].expectLength(i);
      arrayOfValueBand[b2].readFrom(this.in);
    } 
    for (b2 = 0; b2 < this.allCodes.length; b2++) {
      Code code = this.allCodes[b2];
      byte b = 0;
      int j = code.getHandlerCount();
      while (b < j) {
        code.handler_class[b] = this.code_handler_class_RCN.getRef();
        code.handler_start[b] = this.code_handler_start_P.getInt();
        code.handler_end[b] = this.code_handler_end_PO.getInt();
        code.handler_catch[b] = this.code_handler_catch_PO.getInt();
        b++;
      } 
    } 
    for (b2 = 0; b2 < arrayOfValueBand.length; b2++)
      arrayOfValueBand[b2].doneDisbursing(); 
  }
  
  void fixupCodeHandlers() throws IOException {
    for (byte b = 0; b < this.allCodes.length; b++) {
      Code code = this.allCodes[b];
      byte b1 = 0;
      int i = code.getHandlerCount();
      while (b1 < i) {
        int j = code.handler_start[b1];
        code.handler_start[b1] = code.decodeBCI(j);
        j += code.handler_end[b1];
        code.handler_end[b1] = code.decodeBCI(j);
        j += code.handler_catch[b1];
        code.handler_catch[b1] = code.decodeBCI(j);
        b1++;
      } 
    } 
  }
  
  void countAndReadAttrs(int paramInt, Collection<? extends Attribute.Holder> paramCollection) throws IOException {
    countAttrs(paramInt, paramCollection);
    readAttrs(paramInt, paramCollection);
  }
  
  void countAttrs(int paramInt, Collection<? extends Attribute.Holder> paramCollection) throws IOException {
    BandStructure.MultiBand multiBand = this.attrBands[paramInt];
    long l = this.attrFlagMask[paramInt];
    if (this.verbose > 1)
      Utils.log.fine("scanning flags and attrs for " + Attribute.contextName(paramInt) + "[" + paramCollection.size() + "]"); 
    List list = (List)this.attrDefs.get(paramInt);
    Attribute.Layout[] arrayOfLayout = new Attribute.Layout[list.size()];
    list.toArray(arrayOfLayout);
    BandStructure.IntBand intBand1 = getAttrBand(multiBand, 0);
    BandStructure.IntBand intBand2 = getAttrBand(multiBand, 1);
    BandStructure.IntBand intBand3 = getAttrBand(multiBand, 2);
    BandStructure.IntBand intBand4 = getAttrBand(multiBand, 3);
    BandStructure.IntBand intBand5 = getAttrBand(multiBand, 4);
    int i = this.attrOverflowMask[paramInt];
    byte b1 = 0;
    boolean bool = haveFlagsHi(paramInt);
    intBand1.expectLength(bool ? paramCollection.size() : 0);
    intBand1.readFrom(this.in);
    intBand2.expectLength(paramCollection.size());
    intBand2.readFrom(this.in);
    assert (l & i) == i;
    for (Attribute.Holder holder : paramCollection) {
      int j = intBand2.getInt();
      holder.flags = j;
      if ((j & i) != 0)
        b1++; 
    } 
    intBand3.expectLength(b1);
    intBand3.readFrom(this.in);
    intBand4.expectLength(intBand3.getIntTotal());
    intBand4.readFrom(this.in);
    int[] arrayOfInt = new int[arrayOfLayout.length];
    for (Attribute.Holder holder : paramCollection) {
      assert holder.attributes == null;
      long l1 = (holder.flags & l) << 32 >>> 32;
      holder.flags -= (int)l1;
      assert holder.flags == (char)holder.flags;
      assert paramInt != 3 || holder.flags == 0;
      if (bool)
        l1 += (intBand1.getInt() << 32); 
      if (l1 == 0L)
        continue; 
      int j = 0;
      long l2 = l1 & i;
      assert l2 >= 0L;
      l1 -= l2;
      if (l2 != 0L)
        j = intBand3.getInt(); 
      int k = 0;
      long l3 = l1;
      for (byte b = 0; l3 != 0L; b++) {
        if ((l3 & 1L << b) != 0L) {
          l3 -= (1L << b);
          k++;
        } 
      } 
      ArrayList arrayList = new ArrayList(k + j);
      holder.attributes = arrayList;
      l3 = l1;
      int m;
      for (m = 0; l3 != 0L; m++) {
        if ((l3 & 1L << m) != 0L) {
          l3 -= (1L << m);
          arrayOfInt[m] = arrayOfInt[m] + 1;
          if (arrayOfLayout[m] == null)
            badAttrIndex(m, paramInt); 
          Attribute attribute = arrayOfLayout[m].canonicalInstance();
          arrayList.add(attribute);
          k--;
        } 
      } 
      assert k == 0;
      while (j > 0) {
        m = intBand4.getInt();
        arrayOfInt[m] = arrayOfInt[m] + 1;
        if (arrayOfLayout[m] == null)
          badAttrIndex(m, paramInt); 
        Attribute attribute = arrayOfLayout[m].canonicalInstance();
        arrayList.add(attribute);
        j--;
      } 
    } 
    intBand1.doneDisbursing();
    intBand2.doneDisbursing();
    intBand3.doneDisbursing();
    intBand4.doneDisbursing();
    byte b2 = 0;
    boolean bool1;
    for (bool1 = true;; bool1 = false) {
      for (byte b = 0; b < arrayOfLayout.length; b++) {
        Attribute.Layout layout = arrayOfLayout[b];
        if (layout != null && bool1 == isPredefinedAttr(paramInt, b)) {
          int j = arrayOfInt[b];
          if (j != 0) {
            Attribute.Layout.Element[] arrayOfElement = layout.getCallables();
            for (byte b3 = 0; b3 < arrayOfElement.length; b3++) {
              assert (arrayOfElement[b3]).kind == 10;
              if (arrayOfElement[b3].flagTest((byte)8))
                b2++; 
            } 
          } 
        } 
      } 
      if (!bool1)
        break; 
    } 
    intBand5.expectLength(b2);
    intBand5.readFrom(this.in);
    for (bool1 = true;; bool1 = false) {
      for (byte b = 0; b < arrayOfLayout.length; b++) {
        Attribute.Layout layout = arrayOfLayout[b];
        if (layout != null && bool1 == isPredefinedAttr(paramInt, b)) {
          int j = arrayOfInt[b];
          Band[] arrayOfBand = (Band[])this.attrBandTable.get(layout);
          if (layout == this.attrInnerClassesEmpty) {
            this.class_InnerClasses_N.expectLength(j);
            this.class_InnerClasses_N.readFrom(this.in);
            int k = this.class_InnerClasses_N.getIntTotal();
            this.class_InnerClasses_RC.expectLength(k);
            this.class_InnerClasses_RC.readFrom(this.in);
            this.class_InnerClasses_F.expectLength(k);
            this.class_InnerClasses_F.readFrom(this.in);
            k -= this.class_InnerClasses_F.getIntCount(0);
            this.class_InnerClasses_outer_RCN.expectLength(k);
            this.class_InnerClasses_outer_RCN.readFrom(this.in);
            this.class_InnerClasses_name_RUN.expectLength(k);
            this.class_InnerClasses_name_RUN.readFrom(this.in);
          } else if (!this.optDebugBands && j == 0) {
            for (byte b3 = 0; b3 < arrayOfBand.length; b3++)
              arrayOfBand[b3].doneWithUnusedBand(); 
          } else {
            boolean bool2 = layout.hasCallables();
            if (!bool2) {
              readAttrBands(layout.elems, j, new int[0], arrayOfBand);
            } else {
              Attribute.Layout.Element[] arrayOfElement = layout.getCallables();
              int[] arrayOfInt1 = new int[arrayOfElement.length];
              arrayOfInt1[0] = j;
              for (byte b3 = 0; b3 < arrayOfElement.length; b3++) {
                assert (arrayOfElement[b3]).kind == 10;
                int k = arrayOfInt1[b3];
                arrayOfInt1[b3] = -1;
                if (j > 0 && arrayOfElement[b3].flagTest((byte)8))
                  k += intBand5.getInt(); 
                readAttrBands((arrayOfElement[b3]).body, k, arrayOfInt1, arrayOfBand);
              } 
            } 
            if (this.optDebugBands && j == 0)
              for (byte b3 = 0; b3 < arrayOfBand.length; b3++)
                arrayOfBand[b3].doneDisbursing();  
          } 
        } 
      } 
      if (!bool1)
        break; 
    } 
    intBand5.doneDisbursing();
  }
  
  void badAttrIndex(int paramInt1, int paramInt2) throws IOException { throw new IOException("Unknown attribute index " + paramInt1 + " for " + Constants.ATTR_CONTEXT_NAME[paramInt2] + " attribute"); }
  
  void readAttrs(int paramInt, Collection<? extends Attribute.Holder> paramCollection) throws IOException {
    HashSet hashSet = new HashSet();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    for (Attribute.Holder holder : paramCollection) {
      if (holder.attributes == null)
        continue; 
      ListIterator listIterator = holder.attributes.listIterator();
      while (listIterator.hasNext()) {
        Attribute attribute = (Attribute)listIterator.next();
        Attribute.Layout layout = attribute.layout();
        if (layout.bandCount == 0) {
          if (layout == this.attrInnerClassesEmpty)
            readLocalInnerClasses((Package.Class)holder); 
          continue;
        } 
        hashSet.add(layout);
        boolean bool = (paramInt == 1 && layout == this.attrConstantValue) ? 1 : 0;
        if (bool)
          setConstantValueIndex((Package.Class.Field)holder); 
        if (this.verbose > 2)
          Utils.log.fine("read " + attribute + " in " + holder); 
        final Band[] ab = (Band[])this.attrBandTable.get(layout);
        byteArrayOutputStream.reset();
        Object object = attribute.unparse(new Attribute.ValueStream() {
              public int getInt(int param1Int) { return ((BandStructure.IntBand)ab[param1Int]).getInt(); }
              
              public ConstantPool.Entry getRef(int param1Int) { return ((BandStructure.CPRefBand)ab[param1Int]).getRef(); }
              
              public int decodeBCI(int param1Int) {
                Code code = (Code)h;
                return code.decodeBCI(param1Int);
              }
            },  byteArrayOutputStream);
        listIterator.set(attribute.addContent(byteArrayOutputStream.toByteArray(), object));
        if (bool)
          setConstantValueIndex(null); 
      } 
    } 
    for (Attribute.Layout layout : hashSet) {
      if (layout == null)
        continue; 
      final Band[] ab = (Band[])this.attrBandTable.get(layout);
      for (byte b1 = 0; b1 < arrayOfBand.length; b1++)
        arrayOfBand[b1].doneDisbursing(); 
    } 
    if (paramInt == 0) {
      this.class_InnerClasses_N.doneDisbursing();
      this.class_InnerClasses_RC.doneDisbursing();
      this.class_InnerClasses_F.doneDisbursing();
      this.class_InnerClasses_outer_RCN.doneDisbursing();
      this.class_InnerClasses_name_RUN.doneDisbursing();
    } 
    BandStructure.MultiBand multiBand = this.attrBands[paramInt];
    for (byte b = 0; b < multiBand.size(); b++) {
      BandStructure.Band band = multiBand.get(b);
      if (band instanceof BandStructure.MultiBand)
        band.doneDisbursing(); 
    } 
    multiBand.doneDisbursing();
  }
  
  private void readAttrBands(Attribute.Layout.Element[] paramArrayOfElement, int paramInt, int[] paramArrayOfInt, BandStructure.Band[] paramArrayOfBand) throws IOException {
    for (byte b = 0; b < paramArrayOfElement.length; b++) {
      byte b1;
      int j;
      int i;
      Attribute.Layout.Element element = paramArrayOfElement[b];
      BandStructure.Band band = null;
      if (element.hasBand()) {
        band = paramArrayOfBand[element.bandIndex];
        band.expectLength(paramInt);
        band.readFrom(this.in);
      } 
      switch (element.kind) {
        case 5:
          i = ((BandStructure.IntBand)band).getIntTotal();
          readAttrBands(element.body, i, paramArrayOfInt, paramArrayOfBand);
          break;
        case 7:
          j = paramInt;
          for (b1 = 0; b1 < element.body.length; b1++) {
            int k;
            if (b1 == element.body.length - 1) {
              k = j;
            } else {
              k = 0;
              boolean bool = b1;
              while (b1 == bool || (b1 < element.body.length && element.body[b1].flagTest((byte)8))) {
                k += ((BandStructure.IntBand)band).getIntCount((element.body[b1]).value);
                b1++;
              } 
              b1--;
            } 
            j -= k;
            readAttrBands((element.body[b1]).body, k, paramArrayOfInt, paramArrayOfBand);
          } 
          assert j == 0;
          break;
        case 9:
          assert element.body.length == 1;
          assert (element.body[0]).kind == 10;
          if (!element.flagTest((byte)8)) {
            assert paramArrayOfInt[element.value] >= 0;
            paramArrayOfInt[element.value] = paramArrayOfInt[element.value] + paramInt;
          } 
          break;
        case 10:
          assert false;
          break;
      } 
    } 
  }
  
  void readByteCodes() throws IOException {
    this.bc_codes.elementCountForDebug = this.allCodes.length;
    this.bc_codes.setInputStreamFrom(this.in);
    readByteCodeOps();
    this.bc_codes.doneDisbursing();
    BandStructure.Band[] arrayOfBand = { 
        this.bc_case_value, this.bc_byte, this.bc_short, this.bc_local, this.bc_label, this.bc_intref, this.bc_floatref, this.bc_longref, this.bc_doubleref, this.bc_stringref, 
        this.bc_loadablevalueref, this.bc_classref, this.bc_fieldref, this.bc_methodref, this.bc_imethodref, this.bc_indyref, this.bc_thisfield, this.bc_superfield, this.bc_thismethod, this.bc_supermethod, 
        this.bc_initref, this.bc_escref, this.bc_escrefsize, this.bc_escsize };
    byte b;
    for (b = 0; b < arrayOfBand.length; b++)
      arrayOfBand[b].readFrom(this.in); 
    this.bc_escbyte.expectLength(this.bc_escsize.getIntTotal());
    this.bc_escbyte.readFrom(this.in);
    expandByteCodeOps();
    this.bc_case_count.doneDisbursing();
    for (b = 0; b < arrayOfBand.length; b++)
      arrayOfBand[b].doneDisbursing(); 
    this.bc_escbyte.doneDisbursing();
    this.bc_bands.doneDisbursing();
    readAttrs(3, this.codesWithFlags);
    fixupCodeHandlers();
    this.code_bands.doneDisbursing();
    this.class_bands.doneDisbursing();
  }
  
  private void readByteCodeOps() throws IOException {
    byte[] arrayOfByte = new byte[4096];
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < this.allCodes.length; b++) {
      Code code = this.allCodes[b];
      for (byte b1 = 0;; b1++) {
        int i = this.bc_codes.getByte();
        if (b1 + 10 > arrayOfByte.length)
          arrayOfByte = realloc(arrayOfByte); 
        arrayOfByte[b1] = (byte)i;
        boolean bool = false;
        if (i == 196) {
          i = this.bc_codes.getByte();
          arrayOfByte[++b1] = (byte)i;
          bool = true;
        } 
        assert i == (0xFF & i);
        switch (i) {
          case 170:
          case 171:
            this.bc_case_count.expectMoreLength(1);
            arrayList.add(Integer.valueOf(i));
            break;
          case 132:
            this.bc_local.expectMoreLength(1);
            if (bool) {
              this.bc_short.expectMoreLength(1);
              break;
            } 
            this.bc_byte.expectMoreLength(1);
            break;
          case 17:
            this.bc_short.expectMoreLength(1);
            break;
          case 16:
            this.bc_byte.expectMoreLength(1);
            break;
          case 188:
            this.bc_byte.expectMoreLength(1);
            break;
          case 197:
            assert getCPRefOpBand(i) == this.bc_classref;
            this.bc_classref.expectMoreLength(1);
            this.bc_byte.expectMoreLength(1);
            break;
          case 253:
            this.bc_escrefsize.expectMoreLength(1);
            this.bc_escref.expectMoreLength(1);
            break;
          case 254:
            this.bc_escsize.expectMoreLength(1);
            break;
          default:
            if (Instruction.isInvokeInitOp(i)) {
              this.bc_initref.expectMoreLength(1);
              break;
            } 
            if (Instruction.isSelfLinkerOp(i)) {
              BandStructure.CPRefBand cPRefBand = selfOpRefBand(i);
              cPRefBand.expectMoreLength(1);
              break;
            } 
            if (Instruction.isBranchOp(i)) {
              this.bc_label.expectMoreLength(1);
              break;
            } 
            if (Instruction.isCPRefOp(i)) {
              BandStructure.CPRefBand cPRefBand = getCPRefOpBand(i);
              cPRefBand.expectMoreLength(1);
              assert i != 197;
              break;
            } 
            if (Instruction.isLocalSlotOp(i))
              this.bc_local.expectMoreLength(1); 
            break;
          case 255:
            code.bytes = realloc(arrayOfByte, b1);
            break;
        } 
      } 
    } 
    this.bc_case_count.readFrom(this.in);
    for (Integer integer : arrayList) {
      int i = integer.intValue();
      int j = this.bc_case_count.getInt();
      this.bc_label.expectMoreLength(1 + j);
      this.bc_case_value.expectMoreLength((i == 170) ? 1 : j);
    } 
    this.bc_case_count.resetForSecondPass();
  }
  
  private void expandByteCodeOps() throws IOException {
    byte[] arrayOfByte = new byte[4096];
    int[] arrayOfInt1 = new int[4096];
    int[] arrayOfInt2 = new int[1024];
    Fixups fixups = new Fixups();
    for (byte b = 0; b < this.allCodes.length; b++) {
      Code code = this.allCodes[b];
      byte[] arrayOfByte1 = code.bytes;
      code.bytes = null;
      Package.Class clazz = code.thisClass();
      Set set = (Set)this.ldcRefMap.get(clazz);
      if (set == null)
        this.ldcRefMap.put(clazz, set = new HashSet()); 
      ConstantPool.ClassEntry classEntry1 = clazz.thisClass;
      ConstantPool.ClassEntry classEntry2 = clazz.superClass;
      ConstantPool.ClassEntry classEntry3 = null;
      int i = 0;
      byte b1 = 0;
      byte b2 = 0;
      boolean bool = false;
      fixups.clear();
      for (byte b3 = 0; b3 < arrayOfByte1.length; b3++) {
        int n;
        ConstantPool.Entry entry;
        byte b5;
        Instruction.Switch switch;
        int m;
        int j = Instruction.getByte(arrayOfByte1, b3);
        int k = i;
        arrayOfInt1[b1++] = k;
        if (i + 10 > arrayOfByte.length)
          arrayOfByte = realloc(arrayOfByte); 
        if (b1 + 10 > arrayOfInt1.length)
          arrayOfInt1 = realloc(arrayOfInt1); 
        if (b2 + 10 > arrayOfInt2.length)
          arrayOfInt2 = realloc(arrayOfInt2); 
        boolean bool1 = false;
        if (j == 196) {
          arrayOfByte[i++] = (byte)j;
          j = Instruction.getByte(arrayOfByte1, ++b3);
          bool1 = true;
        } 
        switch (j) {
          case 170:
          case 171:
            m = this.bc_case_count.getInt();
            while (i + 30 + m * 8 > arrayOfByte.length)
              arrayOfByte = realloc(arrayOfByte); 
            arrayOfByte[i++] = (byte)j;
            Arrays.fill(arrayOfByte, i, i + 30, (byte)0);
            switch = (Instruction.Switch)Instruction.at(arrayOfByte, k);
            switch.setCaseCount(m);
            if (j == 170) {
              switch.setCaseValue(0, this.bc_case_value.getInt());
            } else {
              for (byte b6 = 0; b6 < m; b6++)
                switch.setCaseValue(b6, this.bc_case_value.getInt()); 
            } 
            arrayOfInt2[b2++] = k;
            i = switch.getNextPC();
            break;
          case 132:
            arrayOfByte[i++] = (byte)j;
            m = this.bc_local.getInt();
            if (bool1) {
              int i1 = this.bc_short.getInt();
              Instruction.setShort(arrayOfByte, i, m);
              i += 2;
              Instruction.setShort(arrayOfByte, i, i1);
              i += 2;
              break;
            } 
            b5 = (byte)this.bc_byte.getByte();
            arrayOfByte[i++] = (byte)m;
            arrayOfByte[i++] = (byte)b5;
            break;
          case 17:
            m = this.bc_short.getInt();
            arrayOfByte[i++] = (byte)j;
            Instruction.setShort(arrayOfByte, i, m);
            i += 2;
            break;
          case 16:
          case 188:
            m = this.bc_byte.getByte();
            arrayOfByte[i++] = (byte)j;
            arrayOfByte[i++] = (byte)m;
            break;
          case 253:
            bool = true;
            m = this.bc_escrefsize.getInt();
            entry = this.bc_escref.getRef();
            if (m == 1)
              set.add(entry); 
            switch (m) {
              case 1:
                fixups.addU1(i, entry);
                break;
              case 2:
                fixups.addU2(i, entry);
                break;
              default:
                assert false;
                n = 0;
                break;
            } 
            arrayOfByte[i + 1] = 0;
            arrayOfByte[i + 0] = 0;
            i += m;
            break;
          case 254:
            bool = true;
            m = this.bc_escsize.getInt();
            while (i + m > arrayOfByte.length)
              arrayOfByte = realloc(arrayOfByte); 
            while (m-- > 0)
              arrayOfByte[i++] = (byte)this.bc_escbyte.getByte(); 
            break;
          default:
            if (Instruction.isInvokeInitOp(j)) {
              ConstantPool.ClassEntry classEntry;
              m = j - 230;
              char c = '·';
              switch (m) {
                case 0:
                  classEntry = classEntry1;
                  break;
                case 1:
                  classEntry = classEntry2;
                  break;
                default:
                  assert m == 2;
                  classEntry = classEntry3;
                  break;
              } 
              arrayOfByte[i++] = (byte)c;
              int i1 = this.bc_initref.getInt();
              ConstantPool.MemberEntry memberEntry = this.pkg.cp.getOverloadingForIndex((byte)10, classEntry, "<init>", i1);
              fixups.addU2(i, memberEntry);
              arrayOfByte[i + 1] = 0;
              arrayOfByte[i + 0] = 0;
              i += 2;
              assert Instruction.opLength(c) == i - k;
              break;
            } 
            if (Instruction.isSelfLinkerOp(j)) {
              ConstantPool.Index index;
              BandStructure.CPRefBand cPRefBand;
              m = j - 202;
              boolean bool2 = (m >= 14) ? 1 : 0;
              if (bool2)
                m -= 14; 
              n = (m >= 7) ? 1 : 0;
              if (n)
                m -= 7; 
              int i1 = 178 + m;
              boolean bool3 = Instruction.isFieldOp(i1);
              ConstantPool.ClassEntry classEntry = bool2 ? classEntry2 : classEntry1;
              if (bool3) {
                cPRefBand = bool2 ? this.bc_superfield : this.bc_thisfield;
                index = this.pkg.cp.getMemberIndex((byte)9, classEntry);
              } else {
                cPRefBand = bool2 ? this.bc_supermethod : this.bc_thismethod;
                index = this.pkg.cp.getMemberIndex((byte)10, classEntry);
              } 
              assert cPRefBand == selfOpRefBand(j);
              ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)cPRefBand.getRef(index);
              if (n) {
                arrayOfByte[i++] = 42;
                k = i;
                arrayOfInt1[b1++] = k;
              } 
              arrayOfByte[i++] = (byte)i1;
              fixups.addU2(i, memberEntry);
              arrayOfByte[i + 1] = 0;
              arrayOfByte[i + 0] = 0;
              i += 2;
              assert Instruction.opLength(i1) == i - k;
              break;
            } 
            if (Instruction.isBranchOp(j)) {
              arrayOfByte[i++] = (byte)j;
              assert !bool1;
              m = k + Instruction.opLength(j);
              arrayOfInt2[b2++] = k;
              while (i < m)
                arrayOfByte[i++] = 0; 
              break;
            } 
            if (Instruction.isCPRefOp(j)) {
              boolean bool2;
              BandStructure.CPRefBand cPRefBand = getCPRefOpBand(j);
              entry = cPRefBand.getRef();
              if (entry == null)
                if (cPRefBand == this.bc_classref) {
                  entry = classEntry1;
                } else {
                  assert false;
                }  
              n = j;
              int i1 = 2;
              switch (j) {
                case 243:
                  n = 184;
                  break;
                case 242:
                  n = 183;
                  break;
                case 18:
                case 233:
                case 234:
                case 235:
                case 240:
                  n = 18;
                  i1 = 1;
                  set.add(entry);
                  break;
                case 19:
                case 236:
                case 237:
                case 238:
                case 241:
                  n = 19;
                  break;
                case 20:
                case 239:
                  n = 20;
                  break;
                case 187:
                  classEntry3 = (ConstantPool.ClassEntry)entry;
                  break;
              } 
              arrayOfByte[i++] = (byte)n;
              switch (i1) {
                case 1:
                  fixups.addU1(i, entry);
                  break;
                case 2:
                  fixups.addU2(i, entry);
                  break;
                default:
                  assert false;
                  bool2 = false;
                  break;
              } 
              arrayOfByte[i + 1] = 0;
              arrayOfByte[i + 0] = 0;
              i += i1;
              if (n == 197) {
                int i2 = this.bc_byte.getByte();
                arrayOfByte[i++] = (byte)i2;
              } else if (n == 185) {
                int i2 = ((ConstantPool.MemberEntry)entry).descRef.typeRef.computeSize(true);
                arrayOfByte[i++] = (byte)(1 + i2);
                arrayOfByte[i++] = 0;
              } else if (n == 186) {
                arrayOfByte[i++] = 0;
                arrayOfByte[i++] = 0;
              } 
              assert Instruction.opLength(n) == i - k;
              break;
            } 
            if (Instruction.isLocalSlotOp(j)) {
              arrayOfByte[i++] = (byte)j;
              m = this.bc_local.getInt();
              if (bool1) {
                Instruction.setShort(arrayOfByte, i, m);
                i += 2;
                if (j == 132) {
                  int i1 = this.bc_short.getInt();
                  Instruction.setShort(arrayOfByte, i, i1);
                  i += 2;
                } 
              } else {
                Instruction.setByte(arrayOfByte, i, m);
                i++;
                if (j == 132) {
                  int i1 = this.bc_byte.getByte();
                  Instruction.setByte(arrayOfByte, i, i1);
                  i++;
                } 
              } 
              assert Instruction.opLength(j) == i - k;
              break;
            } 
            if (j >= 202)
              Utils.log.warning("unrecognized bytescode " + j + " " + Instruction.byteName(j)); 
            assert j < 202;
            arrayOfByte[i++] = (byte)j;
            assert Instruction.opLength(j) == i - k;
            break;
        } 
      } 
      code.setBytes(realloc(arrayOfByte, i));
      code.setInstructionMap(arrayOfInt1, b1);
      Instruction instruction = null;
      for (byte b4 = 0; b4 < b2; b4++) {
        int j = arrayOfInt2[b4];
        instruction = Instruction.at(code.bytes, j, instruction);
        if (instruction instanceof Instruction.Switch) {
          Instruction.Switch switch = (Instruction.Switch)instruction;
          switch.setDefaultLabel(getLabel(this.bc_label, code, j));
          int k = switch.getCaseCount();
          for (byte b5 = 0; b5 < k; b5++)
            switch.setCaseLabel(b5, getLabel(this.bc_label, code, j)); 
        } else {
          instruction.setBranchLabel(getLabel(this.bc_label, code, j));
        } 
      } 
      if (fixups.size() > 0) {
        if (this.verbose > 2)
          Utils.log.fine("Fixups in code: " + fixups); 
        code.addFixups(fixups);
      } 
    } 
  }
  
  static class LimitedBuffer extends BufferedInputStream {
    long served;
    
    int servedPos = this.pos;
    
    long limit;
    
    long buffered;
    
    public boolean atLimit() {
      boolean bool = (getBytesServed() == this.limit);
      assert !bool || this.limit == this.buffered;
      return bool;
    }
    
    public long getBytesServed() { return this.served + (this.pos - this.servedPos); }
    
    public void setReadLimit(long param1Long) {
      if (param1Long == -1L) {
        this.limit = -1L;
      } else {
        this.limit = getBytesServed() + param1Long;
      } 
    }
    
    public long getReadLimit() { return (this.limit == -1L) ? this.limit : (this.limit - getBytesServed()); }
    
    public int read() throws IOException {
      if (this.pos < this.count)
        return this.buf[this.pos++] & 0xFF; 
      this.served += (this.pos - this.servedPos);
      int i = super.read();
      this.servedPos = this.pos;
      if (i >= 0)
        this.served++; 
      assert this.served <= this.limit || this.limit == -1L;
      return i;
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      this.served += (this.pos - this.servedPos);
      int i = super.read(param1ArrayOfByte, param1Int1, param1Int2);
      this.servedPos = this.pos;
      if (i >= 0)
        this.served += i; 
      return i;
    }
    
    public long skip(long param1Long) throws IOException { throw new RuntimeException("no skipping"); }
    
    LimitedBuffer(InputStream param1InputStream) {
      super(null, 16384);
      this.in = new FilterInputStream(param1InputStream) {
          public int read() throws IOException {
            if (PackageReader.LimitedBuffer.this.buffered == PackageReader.LimitedBuffer.this.limit)
              return -1; 
            PackageReader.LimitedBuffer.this.buffered++;
            return super.read();
          }
          
          public int read(byte[] param2ArrayOfByte, int param2Int1, int param2Int2) throws IOException {
            if (PackageReader.LimitedBuffer.this.buffered == PackageReader.LimitedBuffer.this.limit)
              return -1; 
            if (PackageReader.LimitedBuffer.this.limit != -1L) {
              long l = PackageReader.LimitedBuffer.this.limit - PackageReader.LimitedBuffer.this.buffered;
              if (param2Int2 > l)
                param2Int2 = (int)l; 
            } 
            int i = super.read(param2ArrayOfByte, param2Int1, param2Int2);
            if (i >= 0)
              PackageReader.LimitedBuffer.this.buffered += i; 
            return i;
          }
        };
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\PackageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */