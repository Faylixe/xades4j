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

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import xades4j.properties.QualifyingProperty;
import xades4j.UnsupportedAlgorithmException;
import xades4j.properties.data.BaseCertRefsData;
import xades4j.properties.data.PropertyDataObject;
import xades4j.providers.MessageDigestEngineProvider;

/**
 *
 * @author Luís
 */
class DataGenBaseCertRefs
{
    private final MessageDigestEngineProvider messageDigestProvider;

    public DataGenBaseCertRefs(MessageDigestEngineProvider messageDigestProvider)
    {
        this.messageDigestProvider = messageDigestProvider;
    }

    protected PropertyDataObject generate(
            Collection<X509Certificate> certs,
            BaseCertRefsData certRefsData,
            PropertiesDataGenerationContext ctx,
            QualifyingProperty prop) throws PropertyDataGenerationException
    {
        if (null == certs)
            throw new PropertyDataGenerationException("certificates not provided", prop);

        try
        {
            CertRefUtils.createAndAddCertificateReferences(
                    certs,
                    certRefsData,
                    ctx.getAlgorithmsProvider(),
                    messageDigestProvider);
            return certRefsData;
        } catch (UnsupportedAlgorithmException ex)
        {
            throw new PropertyDataGenerationException(ex.getMessage(), prop);
        } catch (CertificateEncodingException ex)
        {
            throw new PropertyDataGenerationException(ex.getMessage(), prop);
        }
    }
}
