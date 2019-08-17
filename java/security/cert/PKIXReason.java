package java.security.cert;

public static enum PKIXReason implements CertPathValidatorException.Reason {
  NAME_CHAINING, INVALID_KEY_USAGE, INVALID_POLICY, NO_TRUST_ANCHOR, UNRECOGNIZED_CRIT_EXT, NOT_CA_CERT, PATH_TOO_LONG, INVALID_NAME;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\PKIXReason.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */