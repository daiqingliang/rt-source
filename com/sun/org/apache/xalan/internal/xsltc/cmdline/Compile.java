package com.sun.org.apache.xalan.internal.xsltc.cmdline;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;
import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOptsException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.io.File;
import java.net.URL;
import java.util.Vector;
import jdk.xml.internal.JdkXmlFeatures;

public final class Compile {
  private static int VERSION_MAJOR = 1;
  
  private static int VERSION_MINOR = 4;
  
  private static int VERSION_DELTA = 0;
  
  private static boolean _allowExit = true;
  
  public static void printUsage() {
    System.err.println("XSLTC version " + VERSION_MAJOR + "." + VERSION_MINOR + ((VERSION_DELTA > 0) ? ("." + VERSION_DELTA) : "") + "\n" + new ErrorMsg("COMPILE_USAGE_STR"));
    if (_allowExit)
      System.exit(-1); 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      boolean bool;
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      GetOpt getOpt = new GetOpt(paramArrayOfString, "o:d:j:p:uxhsinv");
      if (paramArrayOfString.length < 1)
        printUsage(); 
      XSLTC xSLTC = new XSLTC(new JdkXmlFeatures(false));
      xSLTC.init();
      int i;
      while ((i = getOpt.getNextOption()) != -1) {
        switch (i) {
          case 105:
            bool2 = true;
            continue;
          case 111:
            xSLTC.setClassName(getOpt.getOptionArg());
            bool3 = true;
            continue;
          case 100:
            xSLTC.setDestDirectory(getOpt.getOptionArg());
            continue;
          case 112:
            xSLTC.setPackageName(getOpt.getOptionArg());
            continue;
          case 106:
            xSLTC.setJarFileName(getOpt.getOptionArg());
            continue;
          case 120:
            xSLTC.setDebug(true);
            continue;
          case 117:
            bool1 = true;
            continue;
          case 115:
            _allowExit = false;
            continue;
          case 110:
            xSLTC.setTemplateInlining(true);
            continue;
        } 
        printUsage();
      } 
      if (bool2) {
        if (!bool3) {
          System.err.println(new ErrorMsg("COMPILE_STDIN_ERR"));
          if (_allowExit)
            System.exit(-1); 
        } 
        bool = xSLTC.compile(System.in, xSLTC.getClassName());
      } else {
        String[] arrayOfString = getOpt.getCmdArgs();
        Vector vector = new Vector();
        for (byte b = 0; b < arrayOfString.length; b++) {
          URL uRL;
          String str = arrayOfString[b];
          if (bool1) {
            uRL = new URL(str);
          } else {
            uRL = (new File(str)).toURI().toURL();
          } 
          vector.addElement(uRL);
        } 
        bool = xSLTC.compile(vector);
      } 
      if (bool) {
        xSLTC.printWarnings();
        if (xSLTC.getJarFileName() != null)
          xSLTC.outputToJar(); 
        if (_allowExit)
          System.exit(0); 
      } else {
        xSLTC.printWarnings();
        xSLTC.printErrors();
        if (_allowExit)
          System.exit(-1); 
      } 
    } catch (GetOptsException getOptsException) {
      System.err.println(getOptsException);
      printUsage();
    } catch (Exception exception) {
      exception.printStackTrace();
      if (_allowExit)
        System.exit(-1); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\cmdline\Compile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */