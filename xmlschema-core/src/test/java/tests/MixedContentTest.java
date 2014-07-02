/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package tests;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContent;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.junit.Assert;
import org.junit.Test;

public class MixedContentTest extends Assert {
    @Test
    public void testMixedContent() throws Exception {
        QName elementQName = new QName("http://soapinterop.org/xsd", "complexElt");

        QName typeQName = new QName("http://soapinterop.org/xsd", "NoAssemblyRequiredProduct");

        InputStream is = new FileInputStream(Resources.asURI("mixedContent.xsd"));
        XmlSchemaCollection schema = new XmlSchemaCollection();
        XmlSchema s = schema.read(new StreamSource(is));

        XmlSchemaElement elementByName = s.getElementByName(elementQName);
        assertNotNull(elementByName);

        XmlSchemaType schemaType = elementByName.getSchemaType();
        assertNotNull(schemaType);

        assertTrue(schemaType.isMixed());

        XmlSchemaComplexType typeByName = (XmlSchemaComplexType)s.getTypeByName(typeQName);
        assertNotNull(typeByName);

        assertTrue(((XmlSchemaComplexContent)typeByName.getContentModel()).isMixed());

    }
}
