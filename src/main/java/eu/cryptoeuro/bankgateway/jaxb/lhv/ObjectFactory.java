//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.18 at 03:34:38 PM EET 
//


package eu.cryptoeuro.bankgateway.jaxb.lhv;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.cryptoeuro.bankgateway.jaxb.lhv package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.cryptoeuro.bankgateway.jaxb.lhv
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Errors }
     * 
     */
    public Errors createErrors() {
        return new Errors();
    }

    /**
     * Create an instance of {@link HeartBeatResponse }
     * 
     */
    public HeartBeatResponse createHeartBeatResponse() {
        return new HeartBeatResponse();
    }

    /**
     * Create an instance of {@link HeartBeatResponse.Signatures }
     * 
     */
    public HeartBeatResponse.Signatures createHeartBeatResponseSignatures() {
        return new HeartBeatResponse.Signatures();
    }

    /**
     * Create an instance of {@link Errors.Error }
     * 
     */
    public Errors.Error createErrorsError() {
        return new Errors.Error();
    }

    /**
     * Create an instance of {@link HeartBeatResponse.AuthorizedUser }
     * 
     */
    public HeartBeatResponse.AuthorizedUser createHeartBeatResponseAuthorizedUser() {
        return new HeartBeatResponse.AuthorizedUser();
    }

    /**
     * Create an instance of {@link HeartBeatResponse.Signatures.Signature }
     * 
     */
    public HeartBeatResponse.Signatures.Signature createHeartBeatResponseSignaturesSignature() {
        return new HeartBeatResponse.Signatures.Signature();
    }

}
