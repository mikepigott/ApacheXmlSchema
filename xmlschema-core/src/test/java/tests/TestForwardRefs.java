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

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.junit.Assert;
import org.junit.Test;

/**
 */
public class TestForwardRefs extends Assert {

    @Test
    public void testForwardRefs() throws Exception {
        QName elementQName = new QName("http://soapinterop.org/types", "attrTest");
        InputStream is = new FileInputStream(Resources.asURI("forwardRef.xsd"));
        XmlSchemaCollection schema = new XmlSchemaCollection();
        schema.read(new StreamSource(is));

        XmlSchemaElement elem = schema.getElementByQName(elementQName);
        assertNotNull(elem);
        XmlSchemaType type = elem.getSchemaType();
        assertNotNull(type);
        assertTrue(type instanceof XmlSchemaComplexType);
        XmlSchemaComplexType cType = (XmlSchemaComplexType)type;
        XmlSchemaSequence seq = (XmlSchemaSequence)cType.getParticle();
        assertNotNull(seq);
    }
}
