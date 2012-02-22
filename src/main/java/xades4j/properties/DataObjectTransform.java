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
package xades4j.properties;

import org.w3c.dom.Element;

/**
 * Represents a transform that is applied to a data object. Each instance of this
 * class that is applied to a data object will result in a {@code ds:Transform}
 * element within the corresponding {@code ds:Reference} in the signature.
 *
 * @see DataObjectDesc#withTransform(xades4j.properties.DataObjectTransform)
 *
 * @author Luís
 */
public class DataObjectTransform
{
    private final String transformUri;
    private final Element transformParams;

    public DataObjectTransform(
            String transformUri,
            Element paramsElement)
    {
        this.transformUri = transformUri;
        this.transformParams = paramsElement;
    }

    public DataObjectTransform(String transformUri)
    {
        this(transformUri, null);
    }

    public String getTransformUri()
    {
        return transformUri;
    }

    public Element getTransformParams()
    {
        return transformParams;
    }
}
