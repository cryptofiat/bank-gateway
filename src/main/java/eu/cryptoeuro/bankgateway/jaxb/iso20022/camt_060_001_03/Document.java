//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.18 at 03:34:37 PM EET 
//


package eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Document complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Document"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AcctRptgReq" type="{urn:iso:std:iso:20022:tech:xsd:camt.060.001.03}AccountReportingRequestV03"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", propOrder = {
    "acctRptgReq"
})
public class Document {

    @XmlElement(name = "AcctRptgReq", required = true)
    protected AccountReportingRequestV03 acctRptgReq;

    /**
     * Gets the value of the acctRptgReq property.
     * 
     * @return
     *     possible object is
     *     {@link AccountReportingRequestV03 }
     *     
     */
    public AccountReportingRequestV03 getAcctRptgReq() {
        return acctRptgReq;
    }

    /**
     * Sets the value of the acctRptgReq property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountReportingRequestV03 }
     *     
     */
    public void setAcctRptgReq(AccountReportingRequestV03 value) {
        this.acctRptgReq = value;
    }

}