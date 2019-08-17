package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class Driver {
  private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("com.sun.java.util.jar.pack.DriverResource");
  
  private static final String PACK200_OPTION_MAP = "--repack                 $ \n  -r +>- @--repack              $ \n--no-gzip                $ \n  -g +>- @--no-gzip             $ \n--strip-debug            $ \n  -G +>- @--strip-debug         $ \n--no-keep-file-order     $ \n  -O +>- @--no-keep-file-order  $ \n--segment-limit=      *> = \n  -S +>  @--segment-limit=      = \n--effort=             *> = \n  -E +>  @--effort=             = \n--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--modification-time=  *> = \n  -m +>  @--modification-time=  = \n--pass-file=        *> &\000 \n  -P +>  @--pass-file=        &\000 \n--unknown-attribute=  *> = \n  -U +>  @--unknown-attribute=  = \n--class-attribute=  *> &\000 \n  -C +>  @--class-attribute=  &\000 \n--field-attribute=  *> &\000 \n  -F +>  @--field-attribute=  &\000 \n--method-attribute= *> &\000 \n  -M +>  @--method-attribute= &\000 \n--code-attribute=   *> &\000 \n  -D +>  @--code-attribute=   &\000 \n--config-file=      *>   . \n  -f +>  @--config-file=        . \n--no-strip-debug  !--strip-debug         \n--gzip            !--no-gzip             \n--keep-file-order !--no-keep-file-order  \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n--           . \n-   +?    >- . \n";
  
  private static final String UNPACK200_OPTION_MAP = "--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--remove-pack-file       $ \n  -r +>- @--remove-pack-file    $ \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--config-file=        *> . \n  -f +>  @--config-file=        . \n--           . \n-   +?    >- . \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n";
  
  private static final String[] PACK200_PROPERTY_TO_OPTION = { 
      "pack.segment.limit", "--segment-limit=", "pack.keep.file.order", "--no-keep-file-order", "pack.effort", "--effort=", "pack.deflate.hint", "--deflate-hint=", "pack.modification.time", "--modification-time=", 
      "pack.pass.file.", "--pass-file=", "pack.unknown.attribute", "--unknown-attribute=", "pack.class.attribute.", "--class-attribute=", "pack.field.attribute.", "--field-attribute=", "pack.method.attribute.", "--method-attribute=", 
      "pack.code.attribute.", "--code-attribute=", "com.sun.java.util.jar.pack.verbose", "--verbose", "com.sun.java.util.jar.pack.strip.debug", "--strip-debug" };
  
  private static final String[] UNPACK200_PROPERTY_TO_OPTION = { "unpack.deflate.hint", "--deflate-hint=", "com.sun.java.util.jar.pack.verbose", "--verbose", "com.sun.java.util.jar.pack.unpack.remove.packfile", "--remove-pack-file" };
  
  public static void main(String[] paramArrayOfString) throws IOException {
    String[] arrayOfString;
    String str4;
    ArrayList arrayList = new ArrayList(Arrays.asList(paramArrayOfString));
    boolean bool = true;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = true;
    String str1 = null;
    String str2 = "com.sun.java.util.jar.pack.verbose";
    String str3 = arrayList.isEmpty() ? "" : (String)arrayList.get(0);
    switch (str3) {
      case "--pack":
        arrayList.remove(0);
        break;
      case "--unpack":
        arrayList.remove(0);
        bool = false;
        bool1 = true;
        break;
    } 
    HashMap hashMap1 = new HashMap();
    hashMap1.put(str2, System.getProperty(str2));
    if (bool) {
      str4 = "--repack                 $ \n  -r +>- @--repack              $ \n--no-gzip                $ \n  -g +>- @--no-gzip             $ \n--strip-debug            $ \n  -G +>- @--strip-debug         $ \n--no-keep-file-order     $ \n  -O +>- @--no-keep-file-order  $ \n--segment-limit=      *> = \n  -S +>  @--segment-limit=      = \n--effort=             *> = \n  -E +>  @--effort=             = \n--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--modification-time=  *> = \n  -m +>  @--modification-time=  = \n--pass-file=        *> &\000 \n  -P +>  @--pass-file=        &\000 \n--unknown-attribute=  *> = \n  -U +>  @--unknown-attribute=  = \n--class-attribute=  *> &\000 \n  -C +>  @--class-attribute=  &\000 \n--field-attribute=  *> &\000 \n  -F +>  @--field-attribute=  &\000 \n--method-attribute= *> &\000 \n  -M +>  @--method-attribute= &\000 \n--code-attribute=   *> &\000 \n  -D +>  @--code-attribute=   &\000 \n--config-file=      *>   . \n  -f +>  @--config-file=        . \n--no-strip-debug  !--strip-debug         \n--gzip            !--no-gzip             \n--keep-file-order !--no-keep-file-order  \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n--           . \n-   +?    >- . \n";
      arrayOfString = PACK200_PROPERTY_TO_OPTION;
    } else {
      str4 = "--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--remove-pack-file       $ \n  -r +>- @--remove-pack-file    $ \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--config-file=        *> . \n  -f +>  @--config-file=        . \n--           . \n-   +?    >- . \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n";
      arrayOfString = UNPACK200_PROPERTY_TO_OPTION;
    } 
    HashMap hashMap2 = new HashMap();
    try {
      String str;
      while (true) {
        str = parseCommandOptions(arrayList, str4, hashMap2);
        Iterator iterator = hashMap2.keySet().iterator();
        while (iterator.hasNext()) {
          String str10 = (String)iterator.next();
          String str11 = null;
          for (boolean bool5 = false; bool5 < arrayOfString.length; bool5 += true) {
            if (str10.equals(arrayOfString[true + bool5])) {
              str11 = arrayOfString[false + bool5];
              break;
            } 
          } 
          if (str11 != null) {
            String str12 = (String)hashMap2.get(str10);
            iterator.remove();
            if (!str11.endsWith(".")) {
              if (!str10.equals("--verbose") && !str10.endsWith("=")) {
                boolean bool6 = (str12 != null) ? 1 : 0;
                if (str10.startsWith("--no-"))
                  bool6 = !bool6 ? 1 : 0; 
                str12 = bool6 ? "true" : "false";
              } 
              hashMap1.put(str11, str12);
              continue;
            } 
            if (str11.contains(".attribute.")) {
              for (String str13 : str12.split("\000")) {
                String[] arrayOfString1 = str13.split("=", 2);
                hashMap1.put(str11 + arrayOfString1[0], arrayOfString1[1]);
              } 
              continue;
            } 
            byte b = 1;
            for (String str13 : str12.split("\000")) {
              String str14;
              do {
                str14 = str11 + "cli." + b++;
              } while (hashMap1.containsKey(str14));
              hashMap1.put(str14, str13);
            } 
          } 
        } 
        if ("--config-file=".equals(str)) {
          String str10 = (String)arrayList.remove(0);
          Properties properties = new Properties();
          try (FileInputStream null = new FileInputStream(str10)) {
            properties.load(fileInputStream);
          } 
          if (hashMap1.get(str2) != null)
            properties.list(System.out); 
          for (Map.Entry entry : properties.entrySet())
            hashMap1.put((String)entry.getKey(), (String)entry.getValue()); 
          continue;
        } 
        break;
      } 
      if ("--version".equals(str)) {
        System.out.println(MessageFormat.format(RESOURCE.getString("VERSION"), new Object[] { Driver.class.getName(), "1.31, 07/05/05" }));
        return;
      } 
      if ("--help".equals(str)) {
        printUsage(bool, true, System.out);
        System.exit(1);
        return;
      } 
    } catch (IllegalArgumentException illegalArgumentException) {
      System.err.println(MessageFormat.format(RESOURCE.getString("BAD_ARGUMENT"), new Object[] { illegalArgumentException }));
      printUsage(bool, false, System.err);
      System.exit(2);
      return;
    } 
    for (String str10 : hashMap2.keySet()) {
      String str11 = (String)hashMap2.get(str10);
      switch (str10) {
        case "--repack":
          bool2 = true;
          continue;
        case "--no-gzip":
          bool3 = (str11 == null) ? 1 : 0;
          continue;
        case "--log-file=":
          str1 = str11;
          continue;
      } 
      throw new InternalError(MessageFormat.format(RESOURCE.getString("BAD_OPTION"), new Object[] { str10, hashMap2.get(str10) }));
    } 
    if (str1 != null && !str1.equals(""))
      if (str1.equals("-")) {
        System.setErr(System.out);
      } else {
        FileOutputStream fileOutputStream = new FileOutputStream(str1);
        System.setErr(new PrintStream(fileOutputStream));
      }  
    boolean bool4 = (hashMap1.get(str2) != null) ? 1 : 0;
    String str5 = "";
    if (!arrayList.isEmpty())
      str5 = (String)arrayList.remove(0); 
    str6 = "";
    if (!arrayList.isEmpty())
      str6 = (String)arrayList.remove(0); 
    String str7 = "";
    str8 = "";
    str9 = "";
    if (bool2) {
      if (str5.toLowerCase().endsWith(".pack") || str5.toLowerCase().endsWith(".pac") || str5.toLowerCase().endsWith(".gz")) {
        System.err.println(MessageFormat.format(RESOURCE.getString("BAD_REPACK_OUTPUT"), new Object[] { str5 }));
        printUsage(bool, false, System.err);
        System.exit(2);
      } 
      str7 = str5;
      if (str6.equals(""))
        str6 = str7; 
      str9 = createTempFile(str7, ".pack").getPath();
      str5 = str9;
      bool3 = false;
    } 
    if (!arrayList.isEmpty() || (!str6.toLowerCase().endsWith(".jar") && !str6.toLowerCase().endsWith(".zip") && (!str6.equals("-") || bool))) {
      printUsage(bool, false, System.err);
      System.exit(2);
      return;
    } 
    if (bool2) {
      bool = bool1 = true;
    } else if (bool) {
      bool1 = false;
    } 
    Pack200.Packer packer = Pack200.newPacker();
    Pack200.Unpacker unpacker = Pack200.newUnpacker();
    packer.properties().putAll(hashMap1);
    unpacker.properties().putAll(hashMap1);
    if (bool2 && str7.equals(str6)) {
      String str = getZipComment(str6);
      if (bool4 && str.length() > 0)
        System.out.println(MessageFormat.format(RESOURCE.getString("DETECTED_ZIP_COMMENT"), new Object[] { str })); 
      if (str.indexOf("PACK200") >= 0) {
        System.out.println(MessageFormat.format(RESOURCE.getString("SKIP_FOR_REPACKED"), new Object[] { str6 }));
        bool = false;
        bool1 = false;
        bool2 = false;
      } 
    } 
    try {
      if (bool) {
        BufferedOutputStream bufferedOutputStream;
        JarFile jarFile = new JarFile(new File(str6));
        if (str5.equals("-")) {
          bufferedOutputStream = System.out;
          System.setOut(System.err);
        } else if (bool3) {
          if (!str5.endsWith(".gz")) {
            System.err.println(MessageFormat.format(RESOURCE.getString("WRITE_PACK_FILE"), new Object[] { str5 }));
            printUsage(bool, false, System.err);
            System.exit(2);
          } 
          FileOutputStream fileOutputStream = new FileOutputStream(str5);
          BufferedOutputStream bufferedOutputStream1 = new BufferedOutputStream(fileOutputStream);
          bufferedOutputStream = new GZIPOutputStream(bufferedOutputStream1);
        } else {
          if (!str5.toLowerCase().endsWith(".pack") && !str5.toLowerCase().endsWith(".pac")) {
            System.err.println(MessageFormat.format(RESOURCE.getString("WRITE_PACKGZ_FILE"), new Object[] { str5 }));
            printUsage(bool, false, System.err);
            System.exit(2);
          } 
          FileOutputStream fileOutputStream = new FileOutputStream(str5);
          bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        } 
        packer.pack(jarFile, bufferedOutputStream);
        bufferedOutputStream.close();
      } 
      if (bool2 && str7.equals(str6)) {
        File file = createTempFile(str6, ".bak");
        file.delete();
        boolean bool5 = (new File(str6)).renameTo(file);
        if (!bool5)
          throw new Error(MessageFormat.format(RESOURCE.getString("SKIP_FOR_MOVE_FAILED"), new Object[] { str8 })); 
        str8 = file.getPath();
      } 
      if (bool1) {
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream;
        if (str5.equals("-")) {
          fileInputStream = System.in;
        } else {
          fileInputStream = new FileInputStream(new File(str5));
        } 
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        GZIPInputStream gZIPInputStream = bufferedInputStream;
        if (Utils.isGZIPMagic(Utils.readMagic(bufferedInputStream)))
          gZIPInputStream = new GZIPInputStream(gZIPInputStream); 
        String str = str7.equals("") ? str6 : str7;
        if (str.equals("-")) {
          fileOutputStream = System.out;
        } else {
          fileOutputStream = new FileOutputStream(str);
        } 
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        try (JarOutputStream null = new JarOutputStream(bufferedOutputStream)) {
          unpacker.unpack(gZIPInputStream, jarOutputStream);
        } 
      } 
      if (!str8.equals("")) {
        (new File(str8)).delete();
        str8 = "";
      } 
    } finally {
      if (!str8.equals("")) {
        File file = new File(str6);
        file.delete();
        (new File(str8)).renameTo(file);
      } 
      if (!str9.equals(""))
        (new File(str9)).delete(); 
    } 
  }
  
  private static File createTempFile(String paramString1, String paramString2) throws IOException {
    File file1 = new File(paramString1);
    String str = file1.getName();
    if (str.length() < 3)
      str = str + "tmp"; 
    File file2 = (file1.getParentFile() == null && paramString2.equals(".bak")) ? (new File(".")).getAbsoluteFile() : file1.getParentFile();
    Path path = (file2 == null) ? Files.createTempFile(str, paramString2, new java.nio.file.attribute.FileAttribute[0]) : Files.createTempFile(file2.toPath(), str, paramString2, new java.nio.file.attribute.FileAttribute[0]);
    return path.toFile();
  }
  
  private static void printUsage(boolean paramBoolean1, boolean paramBoolean2, PrintStream paramPrintStream) {
    String str = paramBoolean1 ? "pack200" : "unpack200";
    String[] arrayOfString1 = (String[])RESOURCE.getObject("PACK_HELP");
    String[] arrayOfString2 = (String[])RESOURCE.getObject("UNPACK_HELP");
    String[] arrayOfString3 = paramBoolean1 ? arrayOfString1 : arrayOfString2;
    for (byte b = 0; b < arrayOfString3.length; b++) {
      paramPrintStream.println(arrayOfString3[b]);
      if (!paramBoolean2) {
        paramPrintStream.println(MessageFormat.format(RESOURCE.getString("MORE_INFO"), new Object[] { str }));
        break;
      } 
    } 
  }
  
  private static String getZipComment(String paramString) throws IOException {
    byte[] arrayOfByte = new byte[1000];
    long l1 = (new File(paramString)).length();
    if (l1 <= 0L)
      return ""; 
    long l2 = Math.max(0L, l1 - arrayOfByte.length);
    try (FileInputStream null = new FileInputStream(new File(paramString))) {
      fileInputStream.skip(l2);
      fileInputStream.read(arrayOfByte);
      for (int i = arrayOfByte.length - 4; i >= 0; i--) {
        if (arrayOfByte[i + 0] == 80 && arrayOfByte[i + 1] == 75 && arrayOfByte[i + 2] == 5 && arrayOfByte[i + 3] == 6) {
          i += 22;
          if (i < arrayOfByte.length)
            return new String(arrayOfByte, i, arrayOfByte.length - i, "UTF8"); 
          return "";
        } 
      } 
      return "";
    } 
  }
  
  private static String parseCommandOptions(List<String> paramList, String paramString, Map<String, String> paramMap) {
    String str = null;
    TreeMap treeMap = new TreeMap();
    for (String str1 : paramString.split("\n")) {
      String[] arrayOfString = str1.split("\\p{Space}+");
      if (arrayOfString.length != 0) {
        String str2 = arrayOfString[0];
        arrayOfString[0] = "";
        if (str2.length() == 0 && arrayOfString.length >= 1) {
          str2 = arrayOfString[1];
          arrayOfString[1] = "";
        } 
        if (str2.length() != 0) {
          String[] arrayOfString1 = (String[])treeMap.put(str2, arrayOfString);
          if (arrayOfString1 != null)
            throw new RuntimeException(MessageFormat.format(RESOURCE.getString("DUPLICATE_OPTION"), new Object[] { str1.trim() })); 
        } 
      } 
    } 
    ListIterator listIterator1 = paramList.listIterator();
    ListIterator listIterator2 = (new ArrayList()).listIterator();
    label144: while (true) {
      String str1;
      if (listIterator2.hasPrevious()) {
        str1 = (String)listIterator2.previous();
        listIterator2.remove();
      } else if (listIterator1.hasNext()) {
        str1 = (String)listIterator1.next();
      } else {
        break;
      } 
      int i = str1.length();
      while (true) {
        String str2 = str1.substring(0, i);
        if (treeMap.containsKey(str2)) {
          str2 = str2.intern();
          assert str1.startsWith(str2);
          assert str2.length() == i;
          String str3 = str1.substring(i);
          boolean bool1 = false;
          boolean bool2 = false;
          int j = listIterator2.nextIndex();
          String[] arrayOfString = (String[])treeMap.get(str2);
          for (String str4 : arrayOfString) {
            if (str4.length() != 0) {
              boolean bool;
              if (str4.startsWith("#"))
                break; 
              byte b = 0;
              char c = str4.charAt(b++);
              switch (c) {
                case '+':
                  bool = (str3.length() != 0) ? 1 : 0;
                  c = str4.charAt(b++);
                  break;
                case '*':
                  bool = true;
                  c = str4.charAt(b++);
                  break;
                default:
                  bool = (str3.length() == 0) ? 1 : 0;
                  break;
              } 
              if (bool) {
                String str8;
                boolean bool3;
                String str7;
                String str6;
                String str5 = str4.substring(b);
                switch (c) {
                  case '.':
                    str = (str5.length() != 0) ? str5.intern() : str2;
                    break label144;
                  case '?':
                    str = (str5.length() != 0) ? str5.intern() : str1;
                    bool2 = true;
                    break;
                  case '@':
                    str2 = str5.intern();
                    break;
                  case '>':
                    listIterator2.add(str5 + str3);
                    str3 = "";
                    break;
                  case '!':
                    str6 = (str5.length() != 0) ? str5.intern() : str2;
                    paramMap.remove(str6);
                    paramMap.put(str6, null);
                    bool1 = true;
                    break;
                  case '$':
                    if (str5.length() != 0) {
                      str7 = str5;
                    } else {
                      String str9 = (String)paramMap.get(str2);
                      if (str9 == null || str9.length() == 0) {
                        str7 = "1";
                      } else {
                        str7 = "" + (1 + Integer.parseInt(str9));
                      } 
                    } 
                    paramMap.put(str2, str7);
                    bool1 = true;
                    break;
                  case '&':
                  case '=':
                    bool3 = (c == '&') ? 1 : 0;
                    if (listIterator2.hasPrevious()) {
                      str8 = (String)listIterator2.previous();
                      listIterator2.remove();
                    } else if (listIterator1.hasNext()) {
                      str8 = (String)listIterator1.next();
                    } else {
                      str = str1 + " ?";
                      bool2 = true;
                      break;
                    } 
                    if (bool3) {
                      String str9 = (String)paramMap.get(str2);
                      if (str9 != null) {
                        String str10 = str5;
                        if (str10.length() == 0)
                          str10 = " "; 
                        str8 = str9 + str5 + str8;
                      } 
                    } 
                    paramMap.put(str2, str8);
                    bool1 = true;
                    break;
                  default:
                    throw new RuntimeException(MessageFormat.format(RESOURCE.getString("BAD_SPEC"), new Object[] { str2, str4 }));
                } 
              } 
            } 
          } 
          if (bool1 && !bool2)
            continue label144; 
          while (listIterator2.nextIndex() > j) {
            listIterator2.previous();
            listIterator2.remove();
          } 
          if (bool2)
            throw new IllegalArgumentException(str); 
          if (i != 0) {
            i--;
            continue;
          } 
        } else if (i != 0) {
          SortedMap sortedMap = treeMap.headMap(str2);
          byte b = sortedMap.isEmpty() ? 0 : ((String)sortedMap.lastKey()).length();
          i = Math.min(b, i - 1);
          str2 = str1.substring(0, i);
          continue;
        } 
        listIterator2.add(str1);
        break;
      } 
      break;
    } 
    paramList.subList(0, listIterator1.nextIndex()).clear();
    while (listIterator2.hasPrevious())
      paramList.add(0, listIterator2.previous()); 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\Driver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */