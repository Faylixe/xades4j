/*
 * XAdES4j - A Java library for generation and verification of XAdES signatures.
 * Copyright (C) 2010 Luis Goncalves.
 *
 * XAdES4j is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or any later version.
 *
 * XAdES4j is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with XAdES4j. If not, see <http://www.gnu.org/licenses/>.
 */
package xades4j.production;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Element;
import xades4j.properties.CompleteCertificateRefsProperty;
import xades4j.properties.CompleteRevocationRefsProperty;
import xades4j.properties.QualifyingProperty;
import xades4j.properties.SigAndRefsTimeStampProperty;
import xades4j.properties.SignatureTimeStampProperty;
import xades4j.properties.data.PropertyDataObject;
import xades4j.properties.data.SigAndRefsTimeStampData;
import xades4j.providers.TimeStampTokenGenerationException;
import xades4j.providers.TimeStampTokenProvider;
import xades4j.providers.TimeStampTokenProvider.TimeStampTokenRes;
import xades4j.utils.CannotAddDataToDigestInputException;
import xades4j.utils.DOMHelper;
import xades4j.utils.TimeStampDigestInput;

/**
 * Assumes that all the needed input elements are already in the signature.
 * @author Luís
 */
class DataGenSigAndRefsTimeStamp implements PropertyDataObjectGenerator<SigAndRefsTimeStampProperty>
{
    private final TimeStampTokenProvider timeStampTokenProvider;

    @Inject
    public DataGenSigAndRefsTimeStamp(
            TimeStampTokenProvider timeStampTokenProvider)
    {
        this.timeStampTokenProvider = timeStampTokenProvider;
    }

    @Override
    public PropertyDataObject generatePropertyData(
            SigAndRefsTimeStampProperty prop,
            PropertiesDataGenerationContext ctx) throws PropertyDataGenerationException
    {
        String canonAlgUri = ctx.getAlgorithmsProvider().getCanonicalizationAlgorithmForTimeStampProperties();
        TimeStampDigestInput tsDigestInput = new TimeStampDigestInput(canonAlgUri);
        Element unsignedSigPropsElem = DOMHelper.getFirstDescendant(
                ctx.getTargetXmlSignature().getElement(),
                QualifyingProperty.XADES_XMLNS, QualifyingProperty.UNSIGNED_SIGNATURE_PROPS_TAG);
        if (null == unsignedSigPropsElem)
            throw new PropertyDataGenerationException("no unsigned signature properties to get inputs", prop);

        /**
         * This property contains a time-stamp token that covers the following data
         * objects: {@code ds:SignatureValue} element, all present {@code SignatureTimeStamp}
         * elements, {@code CompleteCertificateRefs}, {@code CompleteRevocationRefs}, and
         * when present, {@code AttributeCertificateRefs} and {@code AttributeRevocationRefs}.
         * 
         * "Those (...) that appear before SigAndRefsTimeStamp, in their order of
         * appearance within the UnsignedSignatureProperties element."
         */
        Map<String, Integer> elegiblePropsCnt = new HashMap<String, Integer>(5);
        elegiblePropsCnt.put(CompleteCertificateRefsProperty.PROP_NAME, 0);
        elegiblePropsCnt.put(CompleteRevocationRefsProperty.PROP_NAME, 0);
        elegiblePropsCnt.put(SignatureTimeStampProperty.PROP_NAME, 0);
        elegiblePropsCnt.put("AttributeCertificateRefs", 0);
        elegiblePropsCnt.put("AttributeRevocationRefs", 0);

        try
        {
            // SignatureValue.
            Element e = DOMHelper.getFirstDescendant(
                    ctx.getTargetXmlSignature().getElement(),
                    Constants.SignatureSpecNS, Constants._TAG_SIGNATUREVALUE);
            tsDigestInput.addNode(e);

            e = DOMHelper.getFirstChildElement(unsignedSigPropsElem);
            // UnsignedProperties shouldn't be empty!
            do
            {
                Integer pCnt = elegiblePropsCnt.get(e.getLocalName());
                if (pCnt != null)
                {
                    elegiblePropsCnt.put(e.getLocalName(), pCnt += 1);
                    tsDigestInput.addNode(e);
                }

            } while ((e = DOMHelper.getNextSiblingElement(e)) != null);

            // SignatureTimeStamp has to be present.
            if (elegiblePropsCnt.get(SignatureTimeStampProperty.PROP_NAME) == 0)
                throw new PropertyDataGenerationException("no signature time-stamps for input", prop);

            // CompleteCertificateRefs has to be present.
            if (elegiblePropsCnt.get(CompleteCertificateRefsProperty.PROP_NAME) != 1)
                throw new PropertyDataGenerationException("no CompleteCertificateRefs for input", prop);

            // CompleteRevocationRefs has to be present.
            if (elegiblePropsCnt.get(CompleteRevocationRefsProperty.PROP_NAME) != 1)
                throw new PropertyDataGenerationException("no CompleteRevocationRefs for input", prop);

        } catch (CannotAddDataToDigestInputException ex)
        {
            throw new PropertyDataGenerationException("cannot create timestamp input: " + ex.getMessage(), prop);
        }

        try
        {
            TimeStampTokenRes tsTknRes = timeStampTokenProvider.getTimeStampToken(
                    tsDigestInput.getBytes(),
                    ctx.getAlgorithmsProvider().getDigestAlgorithmForTimeStampProperties());
            prop.setTime(tsTknRes.timeStampTime);
            return new SigAndRefsTimeStampData(canonAlgUri, tsTknRes.encodedTimeStampToken);
        } catch (TimeStampTokenGenerationException ex)
        {
            throw new PropertyDataGenerationException("cannot get a time-stamp: " + ex.getMessage(), prop);
        }
    }
}
