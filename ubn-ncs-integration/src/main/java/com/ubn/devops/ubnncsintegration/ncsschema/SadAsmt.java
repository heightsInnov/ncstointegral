//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.08.27 at 12:07:55 PM WAT 
//


package com.ubn.devops.ubnncsintegration.ncsschema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;



@Data
@JacksonXmlRootElement(localName = "SadAsmt")
public class SadAsmt {

    @JsonProperty( "SADAssessmentSerial")
    protected String sadAssessmentSerial;
    @JsonProperty( "SADAssessmentNumber")
    protected String sadAssessmentNumber;
    @JsonProperty( "SADYear")
    protected int sadYear;

    

}
