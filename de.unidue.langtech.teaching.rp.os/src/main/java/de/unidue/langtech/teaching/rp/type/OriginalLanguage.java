

/* First created by JCasGen Thu Nov 19 16:03:16 CET 2015 */
package de.unidue.langtech.teaching.rp.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Nov 19 16:03:16 CET 2015
 * XML source: C:/Users/Onur/git/research-project/de.unidue.langtech.teaching.rp.os/src/main/resources/desc/type/LanguageType.xml
 * @generated */
public class OriginalLanguage extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OriginalLanguage.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected OriginalLanguage() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public OriginalLanguage(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public OriginalLanguage(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public OriginalLanguage(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: language

  /** getter for language - gets 
   * @generated
   * @return value of the feature 
   */
  public String getLanguage() {
    if (OriginalLanguage_Type.featOkTst && ((OriginalLanguage_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "de.unidue.langtech.teaching.rp.type.OriginalLanguage");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OriginalLanguage_Type)jcasType).casFeatCode_language);}
    
  /** setter for language - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLanguage(String v) {
    if (OriginalLanguage_Type.featOkTst && ((OriginalLanguage_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "de.unidue.langtech.teaching.rp.type.OriginalLanguage");
    jcasType.ll_cas.ll_setStringValue(addr, ((OriginalLanguage_Type)jcasType).casFeatCode_language, v);}    
  }

    