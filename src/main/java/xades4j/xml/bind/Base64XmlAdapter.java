//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.04.09 at 09:56:29 PM BST 
//


package xades4j.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Base64XmlAdapter
    extends XmlAdapter<String, byte[]>
{


    @Override
    public byte[] unmarshal(String value) throws Exception {
        return (xades4j.utils.Base64.decode(value));
    }

    @Override
    public String marshal(byte[] value) {
        return (xades4j.utils.Base64.encodeBytes(value));
    }

}