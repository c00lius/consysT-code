/* Generated By:JavaCC: Do not edit this line. Rcc.java */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jute.compiler.generated;

import org.apache.jute.compiler.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

@SuppressWarnings("unused")
public class Rcc implements RccConstants {
    private static Hashtable<String, JRecord> recTab = new Hashtable<String, JRecord>();
    private static String curDir = System.getProperty("user.dir");
    private static String curFileName;
    private static String curModuleName;

    public static void main(String args[]) {
        String language = "java";
        ArrayList<String> recFiles = new ArrayList<String>();
        JFile curFile=null;

        for (int i=0; i<args.length; i++) {
            if ("-l".equalsIgnoreCase(args[i]) ||
                "--language".equalsIgnoreCase(args[i])) {
                language = args[i+1].toLowerCase();
                i++;
            } else {
                recFiles.add(args[i]);
            }
        }
        if (!"c++".equals(language) && !"java".equals(language) && !"c".equals(language)) {
            System.out.println("Cannot recognize language:" + language);
            System.exit(1);
        }
        if (recFiles.size() == 0) {
            System.out.println("No record files specified. Exiting.");
            System.exit(1);
        }
        for (int i=0; i<recFiles.size(); i++) {
            curFileName = recFiles.get(i);
            File file = new File(curFileName);
            try {
                curFile = parseFile(file);
            } catch (FileNotFoundException e) {
                System.out.println("File " + recFiles.get(i) + " Not found.");
                System.exit(1);
            } catch (ParseException e) {
                System.out.println(e.toString());
                System.exit(1);
            }
            System.out.println(recFiles.get(i) + " Parsed Successfully");
            try {
                curFile.genCode(language, new File("."));
            } catch (IOException e) {
                System.out.println(e.toString());
                System.exit(1);
            }
        }
    }

    public static JFile parseFile(File file) throws FileNotFoundException, ParseException {
        curDir = file.getParent();
        curFileName = file.getName();
        FileReader reader = new FileReader(file);
        try {
            Rcc parser = new Rcc(reader);
            recTab = new Hashtable<String, JRecord>();
            return parser.Input();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

  final public JFile Input() throws ParseException {
    ArrayList<JFile> ilist = new ArrayList<JFile>();
    ArrayList<JRecord> rlist = new ArrayList<JRecord>();
    JFile i;
    ArrayList <JRecord>l;
    label_1:
    while (true) {
      if (jj_2_1(2)) {
        i = Include();
          ilist.add(i);
      } else if (jj_2_2(2)) {
        l = Module();
          rlist.addAll(l);
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
      if (jj_2_3(2)) {
        ;
      } else {
        break label_1;
      }
    }
    jj_consume_token(0);
      {if (true) return new JFile(curFileName, ilist, rlist);}
    throw new Error("Missing return statement in function");
  }

  final public JFile Include() throws ParseException {
    String fname;
    Token t;
    jj_consume_token(INCLUDE_TKN);
    t = jj_consume_token(CSTRING_TKN);
        JFile ret = null;
        fname = t.image.replaceAll("^\u005c"", "").replaceAll("\u005c"$","");
        File file = new File(curDir, fname);
        String tmpDir = curDir;
        String tmpFile = curFileName;
        curDir = file.getParent();
        curFileName = file.getName();
        try {
            FileReader reader = new FileReader(file);
            Rcc parser = new Rcc(reader);
            try {
                ret = parser.Input();
                System.out.println(fname + " Parsed Successfully");
            } catch (ParseException e) {
                System.out.println(e.toString());
                System.exit(1);
            }
            try {
                reader.close();
            } catch (IOException e) {
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + fname +
                " Not found.");
            System.exit(1);
        }
        curDir = tmpDir;
        curFileName = tmpFile;
        {if (true) return ret;}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<JRecord> Module() throws ParseException {
    String mName;
    ArrayList<JRecord> rlist;
    jj_consume_token(MODULE_TKN);
    mName = ModuleName();
      curModuleName = mName;
    jj_consume_token(LBRACE_TKN);
    rlist = RecordList();
    jj_consume_token(RBRACE_TKN);
      {if (true) return rlist;}
    throw new Error("Missing return statement in function");
  }

  final public String ModuleName() throws ParseException {
    String name = "";
    Token t;
    t = jj_consume_token(IDENT_TKN);
      name += t.image;
    label_2:
    while (true) {
      if (jj_2_4(2)) {
        ;
      } else {
        break label_2;
      }
      jj_consume_token(DOT_TKN);
      t = jj_consume_token(IDENT_TKN);
          name += "." + t.image;
    }
      {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<JRecord> RecordList() throws ParseException {
    ArrayList<JRecord> rlist = new ArrayList<JRecord>();
    JRecord r;
    label_3:
    while (true) {
      r = Record();
          rlist.add(r);
      if (jj_2_5(2)) {
        ;
      } else {
        break label_3;
      }
    }
      {if (true) return rlist;}
    throw new Error("Missing return statement in function");
  }

  final public JRecord Record() throws ParseException {
    String rname;
    ArrayList<JField> flist = new ArrayList<JField>();
    Token t;
    JField f;
    jj_consume_token(RECORD_TKN);
    t = jj_consume_token(IDENT_TKN);
      rname = t.image;
    jj_consume_token(LBRACE_TKN);
    label_4:
    while (true) {
      f = Field();
          flist.add(f);
      jj_consume_token(SEMICOLON_TKN);
      if (jj_2_6(2)) {
        ;
      } else {
        break label_4;
      }
    }
    jj_consume_token(RBRACE_TKN);
        String fqn = curModuleName + "." + rname;
        JRecord r = new JRecord(fqn, flist);
        recTab.put(fqn, r);
        {if (true) return r;}
    throw new Error("Missing return statement in function");
  }

  final public JField Field() throws ParseException {
    JType jt;
    Token t;
    jt = Type();
    t = jj_consume_token(IDENT_TKN);
      {if (true) return new JField(jt, t.image);}
    throw new Error("Missing return statement in function");
  }

  final public JType Type() throws ParseException {
    JType jt;
    Token t;
    String rname;
    if (jj_2_7(2)) {
      jt = Map();
      {if (true) return jt;}
    } else if (jj_2_8(2)) {
      jt = Vector();
      {if (true) return jt;}
    } else if (jj_2_9(2)) {
      jj_consume_token(BYTE_TKN);
      {if (true) return new JByte();}
    } else if (jj_2_10(2)) {
      jj_consume_token(BOOLEAN_TKN);
      {if (true) return new JBoolean();}
    } else if (jj_2_11(2)) {
      jj_consume_token(INT_TKN);
      {if (true) return new JInt();}
    } else if (jj_2_12(2)) {
      jj_consume_token(LONG_TKN);
      {if (true) return new JLong();}
    } else if (jj_2_13(2)) {
      jj_consume_token(FLOAT_TKN);
      {if (true) return new JFloat();}
    } else if (jj_2_14(2)) {
      jj_consume_token(DOUBLE_TKN);
      {if (true) return new JDouble();}
    } else if (jj_2_15(2)) {
      jj_consume_token(USTRING_TKN);
      {if (true) return new JString();}
    } else if (jj_2_16(2)) {
      jj_consume_token(BUFFER_TKN);
      {if (true) return new JBuffer();}
    } else if (jj_2_17(2)) {
      rname = ModuleName();
        if (rname.indexOf('.', 0) < 0) {
            rname = curModuleName + "." + rname;
        }
        JRecord r = recTab.get(rname);
        if (r == null) {
            System.out.println("Type " + rname + " not known. Exiting.");
            System.exit(1);
        }
        {if (true) return r;}
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public JMap Map() throws ParseException {
    JType jt1;
    JType jt2;
    jj_consume_token(MAP_TKN);
    jj_consume_token(LT_TKN);
    jt1 = Type();
    jj_consume_token(COMMA_TKN);
    jt2 = Type();
    jj_consume_token(GT_TKN);
      {if (true) return new JMap(jt1, jt2);}
    throw new Error("Missing return statement in function");
  }

  final public JVector Vector() throws ParseException {
    JType jt;
    jj_consume_token(VECTOR_TKN);
    jj_consume_token(LT_TKN);
    jt = Type();
    jj_consume_token(GT_TKN);
      {if (true) return new JVector(jt);}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_10(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  private boolean jj_2_11(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_11(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  private boolean jj_2_12(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_12(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  private boolean jj_2_13(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_13(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(12, xla); }
  }

  private boolean jj_2_14(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_14(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(13, xla); }
  }

  private boolean jj_2_15(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_15(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(14, xla); }
  }

  private boolean jj_2_16(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_16(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(15, xla); }
  }

  private boolean jj_2_17(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_17(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(16, xla); }
  }

  private boolean jj_3_11() {
    if (jj_scan_token(INT_TKN)) return true;
    return false;
  }

  private boolean jj_3R_10() {
    if (jj_scan_token(VECTOR_TKN)) return true;
    if (jj_scan_token(LT_TKN)) return true;
    return false;
  }

  private boolean jj_3_3() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_1()) {
    jj_scanpos = xsp;
    if (jj_3_2()) return true;
    }
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_5()) return true;
    return false;
  }

  private boolean jj_3_10() {
    if (jj_scan_token(BOOLEAN_TKN)) return true;
    return false;
  }

  private boolean jj_3_9() {
    if (jj_scan_token(BYTE_TKN)) return true;
    return false;
  }

  private boolean jj_3_8() {
    if (jj_3R_10()) return true;
    return false;
  }

  private boolean jj_3_5() {
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3R_12() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_7()) {
    jj_scanpos = xsp;
    if (jj_3_8()) {
    jj_scanpos = xsp;
    if (jj_3_9()) {
    jj_scanpos = xsp;
    if (jj_3_10()) {
    jj_scanpos = xsp;
    if (jj_3_11()) {
    jj_scanpos = xsp;
    if (jj_3_12()) {
    jj_scanpos = xsp;
    if (jj_3_13()) {
    jj_scanpos = xsp;
    if (jj_3_14()) {
    jj_scanpos = xsp;
    if (jj_3_15()) {
    jj_scanpos = xsp;
    if (jj_3_16()) {
    jj_scanpos = xsp;
    if (jj_3_17()) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3_7() {
    if (jj_3R_9()) return true;
    return false;
  }

  private boolean jj_3R_9() {
    if (jj_scan_token(MAP_TKN)) return true;
    if (jj_scan_token(LT_TKN)) return true;
    return false;
  }

  private boolean jj_3R_8() {
    if (jj_3R_12()) return true;
    if (jj_scan_token(IDENT_TKN)) return true;
    return false;
  }

  private boolean jj_3_4() {
    if (jj_scan_token(DOT_TKN)) return true;
    if (jj_scan_token(IDENT_TKN)) return true;
    return false;
  }

  private boolean jj_3R_11() {
    if (jj_scan_token(IDENT_TKN)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_4()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3_6() {
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3_17() {
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_5() {
    if (jj_scan_token(INCLUDE_TKN)) return true;
    if (jj_scan_token(CSTRING_TKN)) return true;
    return false;
  }

  private boolean jj_3_16() {
    if (jj_scan_token(BUFFER_TKN)) return true;
    return false;
  }

  private boolean jj_3_15() {
    if (jj_scan_token(USTRING_TKN)) return true;
    return false;
  }

  private boolean jj_3_14() {
    if (jj_scan_token(DOUBLE_TKN)) return true;
    return false;
  }

  private boolean jj_3R_6() {
    if (jj_scan_token(MODULE_TKN)) return true;
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3_13() {
    if (jj_scan_token(FLOAT_TKN)) return true;
    return false;
  }

  private boolean jj_3R_7() {
    if (jj_scan_token(RECORD_TKN)) return true;
    if (jj_scan_token(IDENT_TKN)) return true;
    return false;
  }

  private boolean jj_3_12() {
    if (jj_scan_token(LONG_TKN)) return true;
    return false;
  }

  private boolean jj_3_2() {
    if (jj_3R_6()) return true;
    return false;
  }

  /** Generated Token Manager. */
  public RccTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[0];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[17];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Rcc(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Rcc(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new RccTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Rcc(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new RccTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Rcc(RccTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(RccTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[33];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 0; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 33; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 17; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
            case 9: jj_3_10(); break;
            case 10: jj_3_11(); break;
            case 11: jj_3_12(); break;
            case 12: jj_3_13(); break;
            case 13: jj_3_14(); break;
            case 14: jj_3_15(); break;
            case 15: jj_3_16(); break;
            case 16: jj_3_17(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}