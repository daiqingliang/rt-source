package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.java_cup.internal.runtime.Scanner;
import com.sun.java_cup.internal.runtime.Symbol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

class XPathLexer implements Scanner {
  private final int YY_BUFFER_SIZE = 512;
  
  private final int YY_F = -1;
  
  private final int YY_NO_STATE = -1;
  
  private final int YY_NOT_ACCEPT = 0;
  
  private final int YY_START = 1;
  
  private final int YY_END = 2;
  
  private final int YY_NO_ANCHOR = 4;
  
  private final int YY_BOL = 65536;
  
  private final int YY_EOF = 65537;
  
  public final int YYEOF = -1;
  
  int last;
  
  int beforeLast;
  
  private BufferedReader yy_reader;
  
  private int yy_buffer_index = 0;
  
  private int yy_buffer_read = 0;
  
  private int yy_buffer_start = 0;
  
  private int yy_buffer_end = 0;
  
  private char[] yy_buffer = new char[512];
  
  private boolean yy_at_bol = true;
  
  private int yy_lexical_state = 0;
  
  private boolean yy_eof_done = false;
  
  private final int YYINITIAL = 0;
  
  private final int[] yy_state_dtrans = { 0 };
  
  private boolean yy_last_was_cr = false;
  
  private final int YY_E_INTERNAL = 0;
  
  private final int YY_E_MATCH = 1;
  
  private String[] yy_error_string = { "Error: Internal error.\n", "Error: Unmatched input.\n" };
  
  private int[] yy_acpt = { 
      0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 0, 4, 4, 4, 4, 0, 
      4, 4, 0, 4, 4, 0, 4, 4, 0, 4, 
      0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 
      0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 
      0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 
      0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 
      0, 4, 0, 4, 0, 4, 0, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 0, 0, 4, 0, 4, 
      0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4 };
  
  private static int[] yy_cmap = unpackFromString(1, 65538, "54:9,27:2,54,27:2,54:18,27,17,53,54,15,54:2,55,25,26,1,3,11,4,13,2,56:10,10,54,18,16,19,54,12,44,57:3,46,57:3,51,57:4,48,52,43,57,47,50,45,57:3,49,57:2,41,54,42,54,58,54,35,38,29,5,21,39,33,36,6,57,20,37,8,28,9,30,57,31,32,23,34,7,40,24,22,57,54,14,54:58,60,54:8,57:23,54,57:31,54,57:58,58:2,57:11,58:2,57:8,58,57:53,58,57:68,58:9,57:36,58:3,57:2,58:4,57:30,58:56,57:89,58:18,57:7,58:62,60:70,54:26,60:2,54:14,58:14,54,58:7,57,58,57:3,58,57,58,57:20,58,57:44,58,57:7,58:3,57,58,57,58,57,58,57,58,57:18,58:13,57:12,58,57:66,58,57:12,58,57:36,58:14,57:53,58:2,57:2,58:2,57:2,58:3,57:28,58:2,57:8,58:2,57:2,58:55,57:38,58:2,57,58:7,57:38,58:73,57:27,58:5,57:3,58:46,57:26,58:6,57:10,58:21,59:10,58:7,57:71,58:2,57:5,58,57:15,58,57:4,58,57,58:15,57:2,58:9,59:10,58:523,57:53,58:3,57,58:26,57:10,58:4,59:10,58:21,57:8,58:2,57:2,58:2,57:22,58,57:7,58,57,58:3,57:4,58:34,57:2,58,57:3,58:4,59:10,57:2,58:19,57:6,58:4,57:2,58:2,57:22,58,57:7,58,57:2,58,57:2,58,57:2,58:31,57:4,58,57,58:7,59:10,58:2,57:3,58:16,57:7,58,57,58,57:3,58,57:22,58,57:7,58,57:2,58,57:5,58:3,57,58:34,57,58:5,59:10,58:21,57:8,58:2,57:2,58:2,57:22,58,57:7,58,57:2,58:2,57:4,58:3,57,58:30,57:2,58,57:3,58:4,59:10,58:21,57:6,58:3,57:3,58,57:4,58:3,57:2,58,57,58,57:2,58:3,57:2,58:3,57:3,58:3,57:8,58,57:3,58:45,59:9,58:21,57:8,58,57:3,58,57:23,58,57:10,58,57:5,58:38,57:2,58:4,59:10,58:21,57:8,58,57:3,58,57:23,58,57:10,58,57:5,58:36,57,58,57:2,58:4,59:10,58:21,57:8,58,57:3,58,57:23,58,57:16,58:38,57:2,58:4,59:10,58:145,57:46,58,57,58,57:2,58:12,57:6,58:10,59:10,58:39,57:2,58,57,58:2,57:2,58,57,58:2,57,58:6,57:4,58,57:7,58,57:3,58,57,58,57,58:2,57:2,58,57:2,58,57,58,57:2,58:9,57,58:2,57:5,58:11,59:10,58:70,59:10,58:22,57:8,58,57:33,58:310,57:38,58:10,57:39,58:9,57,58,57:2,58,57:3,58,57,58,57:2,58,57:5,58:41,57,58,57,58,57,58:11,57,58,57,58,57,58:3,57:2,58:3,57,58:5,57:3,58,57,58,57,58,57,58,57,58:3,57:2,58:3,57:2,58,57,58:40,57,58:9,57,58:2,57,58:2,57:2,58:7,57:2,58,57,58,57:7,58:40,57,58:4,57,58:8,57,58:3078,57:156,58:4,57:90,58:6,57:22,58:2,57:6,58:2,57:38,58:2,57:6,58:2,57:8,58,57,58,57,58,57,58,57:31,58:2,57:53,58,57:7,58,57,58:3,57:3,58,57:7,58:3,57:4,58:2,57:6,58:4,57:13,58:5,57:3,58,57:7,58:3,54:12,58:2,54:98,58:182,57,58:3,57:2,58:2,57,58:81,57:3,58:13,54:2672,58:1008,54:17,58:64,57:84,58:12,57:90,58:10,57:40,58:31443,57:11172,58:92,54:8448,58:1232,54:32,58:526,54:2,0:2")[0];
  
  private static int[] yy_rmap = unpackFromString(1, 234, "0,1:2,2,1:2,3,4,1,5,6,1:3,7,8,1:5,9,1,10:2,1:3,11,1:5,12,10,1,10:5,1:2,10,1:2,13,1,10,1,14,10,15,16,1:2,10:4,17,1:2,18,19,20,21,22,23,24,25,26,27,1,25,10,28:2,29,5,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,10,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181")[0];
  
  private static int[][] yy_nxt = unpackFromString(182, 61, "1,2,3,4,5,6,65,184,204,70,7,8,9,10,11,12,13,66,14,15,211,184:2,215,184,16,17,18,218,220,221,184,222,184:2,223,184:3,224,184,19,20,184:10,71,74,77,21,184:2,67,74,-1:63,22,-1:62,184:2,73,184:3,64,-1:2,76,-1:6,184,79,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:10,25,-1:51,26,-1:72,27,-1:42,28,-1:2,28,-1:17,30,-1:26,69,-1:2,72,-1:30,31,-1:57,34,-1:42,21,-1:2,21,-1:5,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:56,28,-1:2,28,-1:57,34,-1:2,34,-1:5,155,184:5,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,209,184:5,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,233,184:5,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,158,184:5,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,122,-1,124,183,184:12,-1:2,184:10,-1:3,76,184,76:3,-1,36,-1:3,103:5,-1:2,80,-1:7,103:5,-1:3,103:13,-1:2,103:10,-1:4,103:3,-1:5,184,23,184:4,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:16,29,-1:48,184:6,64,-1:2,68,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,67,184,76,67,76,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,68,184,76,68,76,-1:44,82,-1:20,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:3,24,184:9,-1:2,184:10,-1:3,76,184,76:3,-1,75:52,32,75:7,-1:49,84,-1:15,184:3,35,184:2,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1,78:54,33,78:5,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:4,105,184:8,-1:2,184:10,-1:3,76,184,76:3,-1:4,184,37,184:4,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:45,185,-1:19,184:6,64,-1:2,76,-1:6,184:2,38,184:2,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:43,86,-1:21,184:6,64,-1:2,76,-1:6,184:4,191,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:47,186,-1:17,184,107,184:4,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:46,96,-1:18,184:4,193,184,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:26,42,-1:38,184:2,205,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:25,100,-1,92,-1:37,184:5,192,64,-1:2,76,-1:6,184,228,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:26,43,-1:38,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:3,206,184:9,-1:2,184:10,-1:3,76,184,76:3,-1:47,104,-1:17,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:9,111,184:3,-1:2,184:10,-1:3,76,184,76:3,-1:50,190,-1:14,184:6,64,-1:2,76,-1:6,184:3,113,184,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:26,45,-1:38,184,39,184:4,64,-1:2,76,-1:6,184:5,-1:3,184,212,184:11,-1:2,184:10,-1:3,76,184,76:3,-1:26,46,-1:38,103:6,-1:3,103,-1:6,103:5,-1:3,103:13,-1:2,103:10,-1:3,103:5,-1:48,106,-1:16,184:6,64,-1:2,76,-1:6,184:5,-1:3,184,216,184:11,-1:2,184:10,-1:3,76,184,76:3,-1:19,48,-1:45,184:6,64,-1:2,76,-1:6,184,119,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:51,114,-1:13,184:4,123,184,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:26,50,-1:38,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:11,40,184,-1:2,184:10,-1:3,76,184,76:3,-1:25,116,-1,112,-1:37,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:3,128,184:9,-1:2,184:10,-1:3,76,184,76:3,-1:52,118,-1:12,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:9,129,184:3,-1:2,184:10,-1:3,76,184,76:3,-1:26,55,-1:38,184:6,64,-1:2,76,-1:6,184:3,130,184,90,-1,92,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:48,120,-1:16,184:6,64,-1:2,76,-1:6,184,131,184:3,94,-1,188,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:19,56,-1:45,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:4,132,184:8,-1:2,184:10,-1:3,76,184,76:3,-1:26,62,-1:38,184:6,64,-1:2,76,-1:6,184,208,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:25,126,-1,124,-1:37,184,41,184:4,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:26,63,-1:38,184:6,64,-1:2,76,-1:6,184:5,-1:3,135,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:2,136,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:5,138,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,130,184,-1:2,92,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,131,184:3,-1:2,188,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:2,139,184:10,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:4,197,184:8,-1:2,184:10,-1:3,76,184,76:3,-1:4,184,140,184:4,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,44,184,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:10,141,184:2,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,142,184,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:12,225,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:7,143,184:5,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:2,145,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:6,146,184:6,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:5,147,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184,148,184:11,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,149,184,110,-1,112,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,150,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,151,184,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:3,47,184:9,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,49,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,149,184,-1:2,112,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:5,51,184:7,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,52,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:5,53,184:7,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,54,184,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:5,156,184:7,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:5,157,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,159,184:5,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:3,160,184:9,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:5,161,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:2,162,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,213,184:5,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:3,226,184:9,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,217,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:10,164,184:2,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:9,167,184:3,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,168,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,170,184,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:2,171,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:9,172,184:3,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,173,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:3,174,184:9,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,175,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:11,57,184,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:9,177,184:3,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:6,178,184:6,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:5,58,184:7,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:5,59,184:7,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:11,60,184,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184,179,184:11,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,180,184,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:2,181,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:5,182,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,61,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:2,124,183,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:45,88,-1:61,98,-1:18,184:4,109,184,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:25,102,-1,188,-1:37,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:9,115,184:3,-1:2,184:10,-1:3,76,184,76:3,-1:50,108,-1:14,184:6,64,-1:2,76,-1:6,184:3,117,184,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184,195,184:11,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,121,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:4,137,184:8,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,133,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,198,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:4,229,184:8,-1:2,184:10,-1:3,76,184,76:3,-1:4,184,200,184:4,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,144,184,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:7,210,184:5,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,152,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:2,163,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,176,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:5,81,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:9,125,184:3,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,127,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,134,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,199,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:4,202,184:8,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,153,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,83,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,194,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:4,165,184:8,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,154,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,85,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,196,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:4,166,184:8,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:5,87,64,-1:2,76,-1:6,184:5,-1:3,184:7,89,184:5,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:4,169,184:8,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:5,187,64,-1:2,76,-1:6,184:5,-1:3,184:8,91,184:4,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:3,93,184:3,95,184:5,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184,97,184:3,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:3,99,184,-1:3,101,184:12,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:5,189,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:2,201,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,219,184:5,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:2,203,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184,207,184:11,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:2,214,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:9,227,184:3,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:10,230,184:2,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:2,231,184:3,64,-1:2,76,-1:6,184:5,-1:3,184:13,-1:2,184:10,-1:3,76,184,76:3,-1:4,184:6,64,-1:2,76,-1:6,184:5,-1:3,184:4,232,184:8,-1:2,184:10,-1:3,76,184,76:3");
  
  void initialize() { this.last = this.beforeLast = -1; }
  
  static boolean isWhitespace(int paramInt) { return (paramInt == 32 || paramInt == 9 || paramInt == 13 || paramInt == 10 || paramInt == 12); }
  
  Symbol disambiguateAxisOrFunction(int paramInt) throws Exception {
    int i;
    for (i = this.yy_buffer_index; i < this.yy_buffer_read && isWhitespace(this.yy_buffer[i]); i++);
    return (i >= this.yy_buffer_read) ? new Symbol(paramInt) : (((this.yy_buffer[i] == ':' && this.yy_buffer[i + 1] == ':') || this.yy_buffer[i] == '(') ? newSymbol(paramInt) : newSymbol(27, yytext()));
  }
  
  Symbol disambiguateOperator(int paramInt) throws Exception {
    switch (this.last) {
      case 9:
        if (this.beforeLast != 27)
          break; 
      case -1:
      case 2:
      case 4:
      case 6:
      case 7:
      case 10:
      case 12:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 32:
      case 33:
        return newSymbol(27, yytext());
    } 
    return newSymbol(paramInt);
  }
  
  Symbol newSymbol(int paramInt) throws Exception {
    this.beforeLast = this.last;
    this.last = paramInt;
    return new Symbol(paramInt);
  }
  
  Symbol newSymbol(int paramInt, String paramString) {
    this.beforeLast = this.last;
    this.last = paramInt;
    return new Symbol(paramInt, paramString);
  }
  
  Symbol newSymbol(int paramInt, Long paramLong) {
    this.beforeLast = this.last;
    this.last = paramInt;
    return new Symbol(paramInt, paramLong);
  }
  
  Symbol newSymbol(int paramInt, Double paramDouble) {
    this.beforeLast = this.last;
    this.last = paramInt;
    return new Symbol(paramInt, paramDouble);
  }
  
  XPathLexer(Reader paramReader) {
    this();
    if (null == paramReader)
      throw new Error("Error: Bad input stream initializer."); 
    this.yy_reader = new BufferedReader(paramReader);
  }
  
  XPathLexer(InputStream paramInputStream) {
    this();
    if (null == paramInputStream)
      throw new Error("Error: Bad input stream initializer."); 
    this.yy_reader = new BufferedReader(new InputStreamReader(paramInputStream));
  }
  
  private XPathLexer() {}
  
  private void yybegin(int paramInt) { this.yy_lexical_state = paramInt; }
  
  private int yy_advance() throws IOException {
    if (this.yy_buffer_index < this.yy_buffer_read)
      return this.yy_buffer[this.yy_buffer_index++]; 
    if (0 != this.yy_buffer_start) {
      int j = this.yy_buffer_start;
      byte b;
      for (b = 0; j < this.yy_buffer_read; b++) {
        this.yy_buffer[b] = this.yy_buffer[j];
        j++;
      } 
      this.yy_buffer_end -= this.yy_buffer_start;
      this.yy_buffer_start = 0;
      this.yy_buffer_read = b;
      this.yy_buffer_index = b;
      int i = this.yy_reader.read(this.yy_buffer, this.yy_buffer_read, this.yy_buffer.length - this.yy_buffer_read);
      if (-1 == i)
        return 65537; 
      this.yy_buffer_read += i;
    } 
    while (this.yy_buffer_index >= this.yy_buffer_read) {
      if (this.yy_buffer_index >= this.yy_buffer.length)
        this.yy_buffer = yy_double(this.yy_buffer); 
      int i = this.yy_reader.read(this.yy_buffer, this.yy_buffer_read, this.yy_buffer.length - this.yy_buffer_read);
      if (-1 == i)
        return 65537; 
      this.yy_buffer_read += i;
    } 
    return this.yy_buffer[this.yy_buffer_index++];
  }
  
  private void yy_move_end() {
    if (this.yy_buffer_end > this.yy_buffer_start && '\n' == this.yy_buffer[this.yy_buffer_end - 1])
      this.yy_buffer_end--; 
    if (this.yy_buffer_end > this.yy_buffer_start && '\r' == this.yy_buffer[this.yy_buffer_end - 1])
      this.yy_buffer_end--; 
  }
  
  private void yy_mark_start() { this.yy_buffer_start = this.yy_buffer_index; }
  
  private void yy_mark_end() { this.yy_buffer_end = this.yy_buffer_index; }
  
  private void yy_to_mark() {
    this.yy_buffer_index = this.yy_buffer_end;
    this.yy_at_bol = (this.yy_buffer_end > this.yy_buffer_start && ('\r' == this.yy_buffer[this.yy_buffer_end - 1] || '\n' == this.yy_buffer[this.yy_buffer_end - 1] || '߬' == this.yy_buffer[this.yy_buffer_end - 1] || '߭' == this.yy_buffer[this.yy_buffer_end - 1]));
  }
  
  private String yytext() { return new String(this.yy_buffer, this.yy_buffer_start, this.yy_buffer_end - this.yy_buffer_start); }
  
  private int yylength() throws IOException { return this.yy_buffer_end - this.yy_buffer_start; }
  
  private char[] yy_double(char[] paramArrayOfChar) {
    char[] arrayOfChar = new char[2 * paramArrayOfChar.length];
    for (byte b = 0; b < paramArrayOfChar.length; b++)
      arrayOfChar[b] = paramArrayOfChar[b]; 
    return arrayOfChar;
  }
  
  private void yy_error(int paramInt, boolean paramBoolean) {
    System.out.print(this.yy_error_string[paramInt]);
    System.out.flush();
    if (paramBoolean)
      throw new Error("Fatal Error.\n"); 
  }
  
  private static int[][] unpackFromString(int paramInt1, int paramInt2, String paramString) {
    int i = -1;
    int j = 0;
    int k = 0;
    int[][] arrayOfInt = new int[paramInt1][paramInt2];
    for (byte b = 0; b < paramInt1; b++) {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        if (j) {
          arrayOfInt[b][b1] = k;
          j--;
        } else {
          int m = paramString.indexOf(',');
          String str = (m == -1) ? paramString : paramString.substring(0, m);
          paramString = paramString.substring(m + 1);
          i = str.indexOf(':');
          if (i == -1) {
            arrayOfInt[b][b1] = Integer.parseInt(str);
          } else {
            String str1 = str.substring(i + 1);
            j = Integer.parseInt(str1);
            str = str.substring(0, i);
            k = Integer.parseInt(str);
            arrayOfInt[b][b1] = k;
            j--;
          } 
        } 
      } 
    } 
    return arrayOfInt;
  }
  
  public Symbol next_token() throws IOException, Exception {
    int i = 4;
    int j = this.yy_state_dtrans[this.yy_lexical_state];
    int k = -1;
    int m = -1;
    boolean bool = true;
    yy_mark_start();
    int n = this.yy_acpt[j];
    if (0 != n) {
      m = j;
      yy_mark_end();
    } 
    while (true) {
      int i1;
      if (bool && this.yy_at_bol) {
        i1 = 65536;
      } else {
        i1 = yy_advance();
      } 
      k = -1;
      k = yy_nxt[yy_rmap[j]][yy_cmap[i1]];
      if (65537 == i1 && true == bool)
        return newSymbol(0); 
      if (-1 != k) {
        j = k;
        bool = false;
        n = this.yy_acpt[j];
        if (0 != n) {
          m = j;
          yy_mark_end();
        } 
        continue;
      } 
      if (-1 == m)
        throw new Error("Lexical Error: Unmatched Input."); 
      i = this.yy_acpt[m];
      if (0 != (0x2 & i))
        yy_move_end(); 
      yy_to_mark();
      switch (m) {
        case -2:
        case 1:
          break;
        case 2:
          return newSymbol(9);
        case -3:
          break;
        case 3:
          return newSymbol(2);
        case -4:
          break;
        case 4:
          return newSymbol(22);
        case -5:
          break;
        case 5:
          return newSymbol(23);
        case -6:
          break;
        case 6:
          return newSymbol(27, yytext());
        case -7:
          break;
        case 7:
          throw new Exception(yytext());
        case -8:
          break;
        case 8:
          return newSymbol(10);
        case -9:
          break;
        case 9:
          return newSymbol(12);
        case -10:
          break;
        case 10:
          return newSymbol(3);
        case -11:
          break;
        case 11:
          return newSymbol(6);
        case -12:
          break;
        case 12:
          return newSymbol(11);
        case -13:
          break;
        case 13:
          return newSymbol(16);
        case -14:
          break;
        case 14:
          return newSymbol(18);
        case -15:
          break;
        case 15:
          return newSymbol(19);
        case -16:
          break;
        case 16:
          return newSymbol(7);
        case -17:
          break;
        case 17:
          return newSymbol(8);
        case -18:
        case -19:
        case 18:
          break;
        case 19:
          return newSymbol(4);
        case -20:
          break;
        case 20:
          return newSymbol(5);
        case -21:
          break;
        case 21:
          return newSymbol(51, new Long(yytext()));
        case -22:
          break;
        case 22:
          return newSymbol(15);
        case -23:
          break;
        case 23:
          return disambiguateAxisOrFunction(28);
        case -24:
          break;
        case 24:
          return disambiguateOperator(32);
        case -25:
          break;
        case 25:
          return newSymbol(14);
        case -26:
          break;
        case 26:
          return newSymbol(27, yytext());
        case -27:
          break;
        case 27:
          return newSymbol(13);
        case -28:
          break;
        case 28:
          return newSymbol(50, new Double(yytext()));
        case -29:
          break;
        case 29:
          return newSymbol(17);
        case -30:
          break;
        case 30:
          return newSymbol(20);
        case -31:
          break;
        case 31:
          return newSymbol(21);
        case -32:
          break;
        case 32:
          return newSymbol(26, yytext().substring(1, yytext().length() - 1));
        case -33:
          break;
        case 33:
          return newSymbol(26, yytext().substring(1, yytext().length() - 1));
        case -34:
          break;
        case 34:
          return newSymbol(50, new Double(yytext()));
        case -35:
          break;
        case 35:
          return disambiguateOperator(24);
        case -36:
          break;
        case 36:
          return newSymbol(27, yytext());
        case -37:
          break;
        case 37:
          return disambiguateOperator(25);
        case -38:
          break;
        case 38:
          return disambiguateAxisOrFunction(29);
        case -39:
          break;
        case 39:
          return disambiguateOperator(33);
        case -40:
          break;
        case 40:
          return disambiguateAxisOrFunction(38);
        case -41:
          break;
        case 41:
          return disambiguateAxisOrFunction(40);
        case -42:
          break;
        case 42:
          return newSymbol(30);
        case -43:
          break;
        case 43:
          return newSymbol(31);
        case -44:
          break;
        case 44:
          return disambiguateAxisOrFunction(39);
        case -45:
          break;
        case 45:
          return newSymbol(30);
        case -46:
          break;
        case 46:
          return newSymbol(31);
        case -47:
          break;
        case 47:
          return disambiguateAxisOrFunction(42);
        case -48:
          break;
        case 48:
          initialize();
          return new Symbol(52);
        case -49:
          break;
        case 49:
          return disambiguateAxisOrFunction(48);
        case -50:
          break;
        case 50:
          return newSymbol(34);
        case -51:
          break;
        case 51:
          return disambiguateAxisOrFunction(49);
        case -52:
          break;
        case 52:
          return disambiguateAxisOrFunction(41);
        case -53:
          break;
        case 53:
          return disambiguateAxisOrFunction(46);
        case -54:
          break;
        case 54:
          return disambiguateAxisOrFunction(44);
        case -55:
          break;
        case 55:
          return newSymbol(34);
        case -56:
          break;
        case 56:
          initialize();
          return new Symbol(53);
        case -57:
          break;
        case 57:
          return disambiguateAxisOrFunction(43);
        case -58:
          break;
        case 58:
          return disambiguateAxisOrFunction(37);
        case -59:
          break;
        case 59:
          return disambiguateAxisOrFunction(47);
        case -60:
          break;
        case 60:
          return disambiguateAxisOrFunction(45);
        case -61:
          break;
        case 61:
          return disambiguateAxisOrFunction(36);
        case -62:
          break;
        case 62:
          return newSymbol(35);
        case -63:
          break;
        case 63:
          return newSymbol(35);
        case -64:
          break;
        case 65:
          return newSymbol(27, yytext());
        case -65:
          break;
        case 66:
          throw new Exception(yytext());
        case -66:
          break;
        case 67:
          return newSymbol(51, new Long(yytext()));
        case -67:
          break;
        case 68:
          return newSymbol(50, new Double(yytext()));
        case -68:
          break;
        case 70:
          return newSymbol(27, yytext());
        case -69:
          break;
        case 71:
          throw new Exception(yytext());
        case -70:
          break;
        case 73:
          return newSymbol(27, yytext());
        case -71:
          break;
        case 74:
          throw new Exception(yytext());
        case -72:
          break;
        case 76:
          return newSymbol(27, yytext());
        case -73:
          break;
        case 77:
          throw new Exception(yytext());
        case -74:
          break;
        case 79:
          return newSymbol(27, yytext());
        case -75:
          break;
        case 81:
          return newSymbol(27, yytext());
        case -76:
          break;
        case 83:
          return newSymbol(27, yytext());
        case -77:
          break;
        case 85:
          return newSymbol(27, yytext());
        case -78:
          break;
        case 87:
          return newSymbol(27, yytext());
        case -79:
          break;
        case 89:
          return newSymbol(27, yytext());
        case -80:
          break;
        case 91:
          return newSymbol(27, yytext());
        case -81:
          break;
        case 93:
          return newSymbol(27, yytext());
        case -82:
          break;
        case 95:
          return newSymbol(27, yytext());
        case -83:
          break;
        case 97:
          return newSymbol(27, yytext());
        case -84:
          break;
        case 99:
          return newSymbol(27, yytext());
        case -85:
          break;
        case 101:
          return newSymbol(27, yytext());
        case -86:
          break;
        case 103:
          return newSymbol(27, yytext());
        case -87:
          break;
        case 105:
          return newSymbol(27, yytext());
        case -88:
          break;
        case 107:
          return newSymbol(27, yytext());
        case -89:
          break;
        case 109:
          return newSymbol(27, yytext());
        case -90:
          break;
        case 111:
          return newSymbol(27, yytext());
        case -91:
          break;
        case 113:
          return newSymbol(27, yytext());
        case -92:
          break;
        case 115:
          return newSymbol(27, yytext());
        case -93:
          break;
        case 117:
          return newSymbol(27, yytext());
        case -94:
          break;
        case 119:
          return newSymbol(27, yytext());
        case -95:
          break;
        case 121:
          return newSymbol(27, yytext());
        case -96:
          break;
        case 123:
          return newSymbol(27, yytext());
        case -97:
          break;
        case 125:
          return newSymbol(27, yytext());
        case -98:
          break;
        case 127:
          return newSymbol(27, yytext());
        case -99:
          break;
        case 128:
          return newSymbol(27, yytext());
        case -100:
          break;
        case 129:
          return newSymbol(27, yytext());
        case -101:
          break;
        case 130:
          return newSymbol(27, yytext());
        case -102:
          break;
        case 131:
          return newSymbol(27, yytext());
        case -103:
          break;
        case 132:
          return newSymbol(27, yytext());
        case -104:
          break;
        case 133:
          return newSymbol(27, yytext());
        case -105:
          break;
        case 134:
          return newSymbol(27, yytext());
        case -106:
          break;
        case 135:
          return newSymbol(27, yytext());
        case -107:
          break;
        case 136:
          return newSymbol(27, yytext());
        case -108:
          break;
        case 137:
          return newSymbol(27, yytext());
        case -109:
          break;
        case 138:
          return newSymbol(27, yytext());
        case -110:
          break;
        case 139:
          return newSymbol(27, yytext());
        case -111:
          break;
        case 140:
          return newSymbol(27, yytext());
        case -112:
          break;
        case 141:
          return newSymbol(27, yytext());
        case -113:
          break;
        case 142:
          return newSymbol(27, yytext());
        case -114:
          break;
        case 143:
          return newSymbol(27, yytext());
        case -115:
          break;
        case 144:
          return newSymbol(27, yytext());
        case -116:
          break;
        case 145:
          return newSymbol(27, yytext());
        case -117:
          break;
        case 146:
          return newSymbol(27, yytext());
        case -118:
          break;
        case 147:
          return newSymbol(27, yytext());
        case -119:
          break;
        case 148:
          return newSymbol(27, yytext());
        case -120:
          break;
        case 149:
          return newSymbol(27, yytext());
        case -121:
          break;
        case 150:
          return newSymbol(27, yytext());
        case -122:
          break;
        case 151:
          return newSymbol(27, yytext());
        case -123:
          break;
        case 152:
          return newSymbol(27, yytext());
        case -124:
          break;
        case 153:
          return newSymbol(27, yytext());
        case -125:
          break;
        case 154:
          return newSymbol(27, yytext());
        case -126:
          break;
        case 155:
          return newSymbol(27, yytext());
        case -127:
          break;
        case 156:
          return newSymbol(27, yytext());
        case -128:
          break;
        case 157:
          return newSymbol(27, yytext());
        case -129:
          break;
        case 158:
          return newSymbol(27, yytext());
        case -130:
          break;
        case 159:
          return newSymbol(27, yytext());
        case -131:
          break;
        case 160:
          return newSymbol(27, yytext());
        case -132:
          break;
        case 161:
          return newSymbol(27, yytext());
        case -133:
          break;
        case 162:
          return newSymbol(27, yytext());
        case -134:
          break;
        case 163:
          return newSymbol(27, yytext());
        case -135:
          break;
        case 164:
          return newSymbol(27, yytext());
        case -136:
          break;
        case 165:
          return newSymbol(27, yytext());
        case -137:
          break;
        case 166:
          return newSymbol(27, yytext());
        case -138:
          break;
        case 167:
          return newSymbol(27, yytext());
        case -139:
          break;
        case 168:
          return newSymbol(27, yytext());
        case -140:
          break;
        case 169:
          return newSymbol(27, yytext());
        case -141:
          break;
        case 170:
          return newSymbol(27, yytext());
        case -142:
          break;
        case 171:
          return newSymbol(27, yytext());
        case -143:
          break;
        case 172:
          return newSymbol(27, yytext());
        case -144:
          break;
        case 173:
          return newSymbol(27, yytext());
        case -145:
          break;
        case 174:
          return newSymbol(27, yytext());
        case -146:
          break;
        case 175:
          return newSymbol(27, yytext());
        case -147:
          break;
        case 176:
          return newSymbol(27, yytext());
        case -148:
          break;
        case 177:
          return newSymbol(27, yytext());
        case -149:
          break;
        case 178:
          return newSymbol(27, yytext());
        case -150:
          break;
        case 179:
          return newSymbol(27, yytext());
        case -151:
          break;
        case 180:
          return newSymbol(27, yytext());
        case -152:
          break;
        case 181:
          return newSymbol(27, yytext());
        case -153:
          break;
        case 182:
          return newSymbol(27, yytext());
        case -154:
          break;
        case 183:
          return newSymbol(27, yytext());
        case -155:
          break;
        case 184:
          return newSymbol(27, yytext());
        case -156:
          break;
        case 187:
          return newSymbol(27, yytext());
        case -157:
          break;
        case 189:
          return newSymbol(27, yytext());
        case -158:
          break;
        case 191:
          return newSymbol(27, yytext());
        case -159:
          break;
        case 192:
          return newSymbol(27, yytext());
        case -160:
          break;
        case 193:
          return newSymbol(27, yytext());
        case -161:
          break;
        case 194:
          return newSymbol(27, yytext());
        case -162:
          break;
        case 195:
          return newSymbol(27, yytext());
        case -163:
          break;
        case 196:
          return newSymbol(27, yytext());
        case -164:
          break;
        case 197:
          return newSymbol(27, yytext());
        case -165:
          break;
        case 198:
          return newSymbol(27, yytext());
        case -166:
          break;
        case 199:
          return newSymbol(27, yytext());
        case -167:
          break;
        case 200:
          return newSymbol(27, yytext());
        case -168:
          break;
        case 201:
          return newSymbol(27, yytext());
        case -169:
          break;
        case 202:
          return newSymbol(27, yytext());
        case -170:
          break;
        case 203:
          return newSymbol(27, yytext());
        case -171:
          break;
        case 204:
          return newSymbol(27, yytext());
        case -172:
          break;
        case 205:
          return newSymbol(27, yytext());
        case -173:
          break;
        case 206:
          return newSymbol(27, yytext());
        case -174:
          break;
        case 207:
          return newSymbol(27, yytext());
        case -175:
          break;
        case 208:
          return newSymbol(27, yytext());
        case -176:
          break;
        case 209:
          return newSymbol(27, yytext());
        case -177:
          break;
        case 210:
          return newSymbol(27, yytext());
        case -178:
          break;
        case 211:
          return newSymbol(27, yytext());
        case -179:
          break;
        case 212:
          return newSymbol(27, yytext());
        case -180:
          break;
        case 213:
          return newSymbol(27, yytext());
        case -181:
          break;
        case 214:
          return newSymbol(27, yytext());
        case -182:
          break;
        case 215:
          return newSymbol(27, yytext());
        case -183:
          break;
        case 216:
          return newSymbol(27, yytext());
        case -184:
          break;
        case 217:
          return newSymbol(27, yytext());
        case -185:
          break;
        case 218:
          return newSymbol(27, yytext());
        case -186:
          break;
        case 219:
          return newSymbol(27, yytext());
        case -187:
          break;
        case 220:
          return newSymbol(27, yytext());
        case -188:
          break;
        case 221:
          return newSymbol(27, yytext());
        case -189:
          break;
        case 222:
          return newSymbol(27, yytext());
        case -190:
          break;
        case 223:
          return newSymbol(27, yytext());
        case -191:
          break;
        case 224:
          return newSymbol(27, yytext());
        case -192:
          break;
        case 225:
          return newSymbol(27, yytext());
        case -193:
          break;
        case 226:
          return newSymbol(27, yytext());
        case -194:
          break;
        case 227:
          return newSymbol(27, yytext());
        case -195:
          break;
        case 228:
          return newSymbol(27, yytext());
        case -196:
          break;
        case 229:
          return newSymbol(27, yytext());
        case -197:
          break;
        case 230:
          return newSymbol(27, yytext());
        case -198:
          break;
        case 231:
          return newSymbol(27, yytext());
        case -199:
          break;
        case 232:
          return newSymbol(27, yytext());
        case -200:
          break;
        case 233:
          return newSymbol(27, yytext());
        case -201:
          break;
        default:
          yy_error(0, false);
          break;
        case -1:
          break;
      } 
      bool = true;
      j = this.yy_state_dtrans[this.yy_lexical_state];
      k = -1;
      m = -1;
      yy_mark_start();
      n = this.yy_acpt[j];
      if (0 != n) {
        m = j;
        yy_mark_end();
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\XPathLexer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */