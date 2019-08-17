package sun.security.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.security.cert.CertPathValidatorException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisabledAlgorithmConstraints extends AbstractAlgorithmConstraints {
  private static final Debug debug = Debug.getInstance("certpath");
  
  public static final String PROPERTY_CERTPATH_DISABLED_ALGS = "jdk.certpath.disabledAlgorithms";
  
  public static final String PROPERTY_TLS_DISABLED_ALGS = "jdk.tls.disabledAlgorithms";
  
  public static final String PROPERTY_JAR_DISABLED_ALGS = "jdk.jar.disabledAlgorithms";
  
  private final String[] disabledAlgorithms;
  
  private final Constraints algorithmConstraints;
  
  public DisabledAlgorithmConstraints(String paramString) { this(paramString, new AlgorithmDecomposer()); }
  
  public DisabledAlgorithmConstraints(String paramString, AlgorithmDecomposer paramAlgorithmDecomposer) {
    super(paramAlgorithmDecomposer);
    this.disabledAlgorithms = getAlgorithms(paramString);
    this.algorithmConstraints = new Constraints(this.disabledAlgorithms);
  }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, String paramString, AlgorithmParameters paramAlgorithmParameters) { return !checkAlgorithm(this.disabledAlgorithms, paramString, this.decomposer) ? false : ((paramAlgorithmParameters != null) ? this.algorithmConstraints.permits(paramString, paramAlgorithmParameters) : 1); }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, Key paramKey) { return checkConstraints(paramSet, "", paramKey, null); }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters) {
    if (paramString == null || paramString.length() == 0)
      throw new IllegalArgumentException("No algorithm name specified"); 
    return checkConstraints(paramSet, paramString, paramKey, paramAlgorithmParameters);
  }
  
  public final void permits(ConstraintsParameters paramConstraintsParameters) throws CertPathValidatorException { permits(paramConstraintsParameters.getAlgorithm(), paramConstraintsParameters); }
  
  public final void permits(String paramString1, Key paramKey, AlgorithmParameters paramAlgorithmParameters, String paramString2) throws CertPathValidatorException { permits(paramString1, new ConstraintsParameters(paramString1, paramAlgorithmParameters, paramKey, (paramString2 == null) ? "generic" : paramString2)); }
  
  public final void permits(String paramString, ConstraintsParameters paramConstraintsParameters) throws CertPathValidatorException { this.algorithmConstraints.permits(paramString, paramConstraintsParameters); }
  
  public boolean checkProperty(String paramString) {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    for (String str : this.disabledAlgorithms) {
      if (str.toLowerCase(Locale.ENGLISH).indexOf(paramString) >= 0)
        return true; 
    } 
    return false;
  }
  
  private boolean checkConstraints(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters) {
    if (paramKey == null)
      throw new IllegalArgumentException("The key cannot be null"); 
    return (paramString != null && paramString.length() != 0 && !permits(paramSet, paramString, paramAlgorithmParameters)) ? false : (!permits(paramSet, paramKey.getAlgorithm(), null) ? false : this.algorithmConstraints.permits(paramKey));
  }
  
  private static abstract class Constraint {
    String algorithm;
    
    Constraint nextConstraint = null;
    
    private Constraint() {}
    
    public boolean permits(Key param1Key) { return true; }
    
    public boolean permits(AlgorithmParameters param1AlgorithmParameters) { return true; }
    
    public abstract void permits(ConstraintsParameters param1ConstraintsParameters) throws CertPathValidatorException;
    
    boolean next(ConstraintsParameters param1ConstraintsParameters) throws CertPathValidatorException {
      if (this.nextConstraint != null) {
        this.nextConstraint.permits(param1ConstraintsParameters);
        return true;
      } 
      return false;
    }
    
    boolean next(Key param1Key) { return (this.nextConstraint != null && this.nextConstraint.permits(param1Key)); }
    
    String extendedMsg(ConstraintsParameters param1ConstraintsParameters) { return (param1ConstraintsParameters.getCertificate() == null) ? "." : (" used with certificate: " + param1ConstraintsParameters.getCertificate().getSubjectX500Principal() + ((param1ConstraintsParameters.getVariant() != "generic") ? (".  Usage was " + param1ConstraintsParameters.getVariant()) : ".")); }
    
    enum Operator {
      EQ, NE, LT, LE, GT, GE;
      
      static Operator of(String param2String) {
        switch (param2String) {
          case "==":
            return EQ;
          case "!=":
            return NE;
          case "<":
            return LT;
          case "<=":
            return LE;
          case ">":
            return GT;
          case ">=":
            return GE;
        } 
        throw new IllegalArgumentException("Error in security property. " + param2String + " is not a legal Operator");
      }
    }
  }
  
  private static class Constraints {
    private Map<String, List<DisabledAlgorithmConstraints.Constraint>> constraintsMap = new HashMap();
    
    public Constraints(String[] param1ArrayOfString) {
      for (String str : param1ArrayOfString) {
        if (str != null && !str.isEmpty()) {
          str = str.trim();
          if (debug != null)
            debug.println("Constraints: " + str); 
          int i = str.indexOf(' ');
          String str1 = AlgorithmDecomposer.hashName(((i > 0) ? str.substring(0, i) : str).toUpperCase(Locale.ENGLISH));
          List list = (List)this.constraintsMap.getOrDefault(str1, new ArrayList(1));
          for (String str2 : AlgorithmDecomposer.getAliases(str1))
            this.constraintsMap.putIfAbsent(str2, list); 
          if (i <= 0) {
            list.add(new DisabledAlgorithmConstraints.DisabledConstraint(str1));
          } else {
            String str2 = str.substring(i + 1);
            DisabledAlgorithmConstraints.UsageConstraint usageConstraint = null;
            boolean bool1 = false;
            boolean bool2 = false;
            for (String str3 : str2.split("&")) {
              DisabledAlgorithmConstraints.UsageConstraint usageConstraint1;
              str3 = str3.trim();
              if (str3.startsWith("keySize")) {
                if (debug != null)
                  debug.println("Constraints set to keySize: " + str3); 
                StringTokenizer stringTokenizer = new StringTokenizer(str3);
                if (!"keySize".equals(stringTokenizer.nextToken()))
                  throw new IllegalArgumentException("Error in security property. Constraint unknown: " + str3); 
                usageConstraint1 = new DisabledAlgorithmConstraints.KeySizeConstraint(str1, DisabledAlgorithmConstraints.Constraint.Operator.of(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken()));
              } else if (str3.equalsIgnoreCase("jdkCA")) {
                if (debug != null)
                  debug.println("Constraints set to jdkCA."); 
                if (bool1)
                  throw new IllegalArgumentException("Only one jdkCA entry allowed in property. Constraint: " + str); 
                usageConstraint1 = new DisabledAlgorithmConstraints.jdkCAConstraint(str1);
                bool1 = true;
              } else {
                Matcher matcher;
                if (str3.startsWith("denyAfter") && (matcher = DENY_AFTER_PATTERN.matcher(str3)).matches()) {
                  if (debug != null)
                    debug.println("Constraints set to denyAfter"); 
                  if (bool2)
                    throw new IllegalArgumentException("Only one denyAfter entry allowed in property. Constraint: " + str); 
                  int j = Integer.parseInt(matcher.group(1));
                  int k = Integer.parseInt(matcher.group(2));
                  int m = Integer.parseInt(matcher.group(3));
                  usageConstraint1 = new DisabledAlgorithmConstraints.DenyAfterConstraint(str1, j, k, m);
                  bool2 = true;
                } else if (str3.startsWith("usage")) {
                  String[] arrayOfString = str3.substring(5).trim().split(" ");
                  usageConstraint1 = new DisabledAlgorithmConstraints.UsageConstraint(str1, arrayOfString);
                  if (debug != null)
                    debug.println("Constraints usage length is " + arrayOfString.length); 
                } else {
                  throw new IllegalArgumentException("Error in security property. Constraint unknown: " + str3);
                } 
              } 
              if (usageConstraint == null) {
                list.add(usageConstraint1);
              } else {
                usageConstraint.nextConstraint = usageConstraint1;
              } 
              usageConstraint = usageConstraint1;
            } 
          } 
        } 
      } 
    }
    
    private List<DisabledAlgorithmConstraints.Constraint> getConstraints(String param1String) { return (List)this.constraintsMap.get(param1String); }
    
    public boolean permits(Key param1Key) {
      List list = getConstraints(param1Key.getAlgorithm());
      if (list == null)
        return true; 
      for (DisabledAlgorithmConstraints.Constraint constraint : list) {
        if (!constraint.permits(param1Key)) {
          if (debug != null)
            debug.println("keySizeConstraint: failed key constraint check " + KeyUtil.getKeySize(param1Key)); 
          return false;
        } 
      } 
      return true;
    }
    
    public boolean permits(String param1String, AlgorithmParameters param1AlgorithmParameters) {
      List list = getConstraints(param1String);
      if (list == null)
        return true; 
      for (DisabledAlgorithmConstraints.Constraint constraint : list) {
        if (!constraint.permits(param1AlgorithmParameters)) {
          if (debug != null)
            debug.println("keySizeConstraint: failed algorithm parameters constraint check " + param1AlgorithmParameters); 
          return false;
        } 
      } 
      return true;
    }
    
    public void permits(String param1String, ConstraintsParameters param1ConstraintsParameters) throws CertPathValidatorException {
      X509Certificate x509Certificate = param1ConstraintsParameters.getCertificate();
      if (debug != null)
        debug.println("Constraints.permits(): " + param1String + " Variant: " + param1ConstraintsParameters.getVariant()); 
      HashSet hashSet = new HashSet();
      if (param1String != null)
        hashSet.addAll(AlgorithmDecomposer.decomposeOneHash(param1String)); 
      if (x509Certificate != null)
        hashSet.add(x509Certificate.getPublicKey().getAlgorithm()); 
      if (param1ConstraintsParameters.getPublicKey() != null)
        hashSet.add(param1ConstraintsParameters.getPublicKey().getAlgorithm()); 
      for (String str : hashSet) {
        List list = getConstraints(str);
        if (list == null)
          continue; 
        for (DisabledAlgorithmConstraints.Constraint constraint : list)
          constraint.permits(param1ConstraintsParameters); 
      } 
    }
    
    private static class Holder {
      private static final Pattern DENY_AFTER_PATTERN = Pattern.compile("denyAfter\\s+(\\d{4})-(\\d{2})-(\\d{2})");
    }
  }
  
  private static class DenyAfterConstraint extends Constraint {
    private Date denyAfterDate;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d HH:mm:ss z yyyy");
    
    DenyAfterConstraint(String param1String, int param1Int1, int param1Int2, int param1Int3) {
      super(null);
      this.algorithm = param1String;
      if (debug != null)
        debug.println("DenyAfterConstraint read in as:  year " + param1Int1 + ", month = " + param1Int2 + ", day = " + param1Int3); 
      Calendar calendar = (new Calendar.Builder()).setTimeZone(TimeZone.getTimeZone("GMT")).setDate(param1Int1, param1Int2 - 1, param1Int3).build();
      if (param1Int1 > calendar.getActualMaximum(1) || param1Int1 < calendar.getActualMinimum(1))
        throw new IllegalArgumentException("Invalid year given in constraint: " + param1Int1); 
      if (param1Int2 - 1 > calendar.getActualMaximum(2) || param1Int2 - 1 < calendar.getActualMinimum(2))
        throw new IllegalArgumentException("Invalid month given in constraint: " + param1Int2); 
      if (param1Int3 > calendar.getActualMaximum(5) || param1Int3 < calendar.getActualMinimum(5))
        throw new IllegalArgumentException("Invalid Day of Month given in constraint: " + param1Int3); 
      this.denyAfterDate = calendar.getTime();
      if (debug != null)
        debug.println("DenyAfterConstraint date set to: " + dateFormat.format(this.denyAfterDate)); 
    }
    
    public void permits(ConstraintsParameters param1ConstraintsParameters) throws CertPathValidatorException {
      String str;
      Date date;
      if (param1ConstraintsParameters.getJARTimestamp() != null) {
        date = param1ConstraintsParameters.getJARTimestamp().getTimestamp();
        str = "JAR Timestamp date: ";
      } else if (param1ConstraintsParameters.getPKIXParamDate() != null) {
        date = param1ConstraintsParameters.getPKIXParamDate();
        str = "PKIXParameter date: ";
      } else {
        date = new Date();
        str = "Current date: ";
      } 
      if (!this.denyAfterDate.after(date)) {
        if (next(param1ConstraintsParameters))
          return; 
        throw new CertPathValidatorException("denyAfter constraint check failed: " + this.algorithm + " used with Constraint date: " + dateFormat.format(this.denyAfterDate) + "; " + str + dateFormat.format(date) + extendedMsg(param1ConstraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
      } 
    }
    
    public boolean permits(Key param1Key) {
      if (next(param1Key))
        return true; 
      if (debug != null)
        debug.println("DenyAfterConstraints.permits(): " + this.algorithm); 
      return this.denyAfterDate.after(new Date());
    }
  }
  
  private static class DisabledConstraint extends Constraint {
    DisabledConstraint(String param1String) {
      super(null);
      this.algorithm = param1String;
    }
    
    public void permits(ConstraintsParameters param1ConstraintsParameters) throws CertPathValidatorException { throw new CertPathValidatorException("Algorithm constraints check failed on disabled algorithm: " + this.algorithm + extendedMsg(param1ConstraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED); }
    
    public boolean permits(Key param1Key) { return false; }
  }
  
  private static class KeySizeConstraint extends Constraint {
    private int minSize;
    
    private int maxSize;
    
    private int prohibitedSize = -1;
    
    private int size;
    
    public KeySizeConstraint(String param1String, DisabledAlgorithmConstraints.Constraint.Operator param1Operator, int param1Int) {
      super(null);
      this.algorithm = param1String;
      switch (DisabledAlgorithmConstraints.null.$SwitchMap$sun$security$util$DisabledAlgorithmConstraints$Constraint$Operator[param1Operator.ordinal()]) {
        case 1:
          this.minSize = 0;
          this.maxSize = Integer.MAX_VALUE;
          this.prohibitedSize = param1Int;
          return;
        case 2:
          this.minSize = param1Int;
          this.maxSize = param1Int;
          return;
        case 3:
          this.minSize = param1Int;
          this.maxSize = Integer.MAX_VALUE;
          return;
        case 4:
          this.minSize = param1Int + 1;
          this.maxSize = Integer.MAX_VALUE;
          return;
        case 5:
          this.minSize = 0;
          this.maxSize = param1Int;
          return;
        case 6:
          this.minSize = 0;
          this.maxSize = (param1Int > 1) ? (param1Int - 1) : 0;
          return;
      } 
      this.minSize = Integer.MAX_VALUE;
      this.maxSize = -1;
    }
    
    public void permits(ConstraintsParameters param1ConstraintsParameters) throws CertPathValidatorException {
      Key key = null;
      if (param1ConstraintsParameters.getPublicKey() != null) {
        key = param1ConstraintsParameters.getPublicKey();
      } else if (param1ConstraintsParameters.getCertificate() != null) {
        key = param1ConstraintsParameters.getCertificate().getPublicKey();
      } 
      if (key != null && !permitsImpl(key)) {
        if (this.nextConstraint != null) {
          this.nextConstraint.permits(param1ConstraintsParameters);
          return;
        } 
        throw new CertPathValidatorException("Algorithm constraints check failed on keysize limits. " + this.algorithm + " " + KeyUtil.getKeySize(key) + "bit key" + extendedMsg(param1ConstraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
      } 
    }
    
    public boolean permits(Key param1Key) {
      if (this.nextConstraint != null && this.nextConstraint.permits(param1Key))
        return true; 
      if (debug != null)
        debug.println("KeySizeConstraints.permits(): " + this.algorithm); 
      return permitsImpl(param1Key);
    }
    
    public boolean permits(AlgorithmParameters param1AlgorithmParameters) {
      String str = param1AlgorithmParameters.getAlgorithm();
      if (!this.algorithm.equalsIgnoreCase(param1AlgorithmParameters.getAlgorithm())) {
        Collection collection = AlgorithmDecomposer.getAliases(this.algorithm);
        if (!collection.contains(str))
          return true; 
      } 
      int i = KeyUtil.getKeySize(param1AlgorithmParameters);
      return (i == 0) ? false : ((i > 0) ? ((i >= this.minSize && i <= this.maxSize && this.prohibitedSize != i)) : true);
    }
    
    private boolean permitsImpl(Key param1Key) {
      if (this.algorithm.compareToIgnoreCase(param1Key.getAlgorithm()) != 0)
        return true; 
      this.size = KeyUtil.getKeySize(param1Key);
      return (this.size == 0) ? false : ((this.size > 0) ? ((this.size >= this.minSize && this.size <= this.maxSize && this.prohibitedSize != this.size)) : true);
    }
  }
  
  private static class UsageConstraint extends Constraint {
    String[] usages;
    
    UsageConstraint(String param1String, String[] param1ArrayOfString) {
      super(null);
      this.algorithm = param1String;
      this.usages = param1ArrayOfString;
    }
    
    public void permits(ConstraintsParameters param1ConstraintsParameters) throws CertPathValidatorException {
      for (String str1 : this.usages) {
        String str2 = null;
        if (str1.compareToIgnoreCase("TLSServer") == 0) {
          str2 = "tls server";
        } else if (str1.compareToIgnoreCase("TLSClient") == 0) {
          str2 = "tls client";
        } else if (str1.compareToIgnoreCase("SignedJAR") == 0) {
          str2 = "plugin code signing";
        } 
        if (debug != null) {
          debug.println("Checking if usage constraint \"" + str2 + "\" matches \"" + param1ConstraintsParameters.getVariant() + "\"");
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          PrintStream printStream = new PrintStream(byteArrayOutputStream);
          (new Exception()).printStackTrace(printStream);
          debug.println(byteArrayOutputStream.toString());
        } 
        if (param1ConstraintsParameters.getVariant().compareTo(str2) == 0) {
          if (next(param1ConstraintsParameters))
            return; 
          throw new CertPathValidatorException("Usage constraint " + str1 + " check failed: " + this.algorithm + extendedMsg(param1ConstraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
        } 
      } 
    }
  }
  
  private static class jdkCAConstraint extends Constraint {
    jdkCAConstraint(String param1String) {
      super(null);
      this.algorithm = param1String;
    }
    
    public void permits(ConstraintsParameters param1ConstraintsParameters) throws CertPathValidatorException {
      if (debug != null)
        debug.println("jdkCAConstraints.permits(): " + this.algorithm); 
      if (param1ConstraintsParameters.isTrustedMatch()) {
        if (next(param1ConstraintsParameters))
          return; 
        throw new CertPathValidatorException("Algorithm constraints check failed on certificate anchor limits. " + this.algorithm + extendedMsg(param1ConstraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\DisabledAlgorithmConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */