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
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.junit.Assert;
import org.junit.Test;

public class TestSimpleRestriction extends Assert {
    @Test
    public void testSimpleRestriction() throws Exception {
        QName typeQName = new QName("http://soapinterop.org/types", "layoutComponentType");
        QName elementQName = new QName("http://soapinterop.org/types", "foo");

        InputStream is = new FileInputStream(Resources.asURI("SimpleContentRestriction.xsd"));
        XmlSchemaCollection schema = new XmlSchemaCollection();
        schema.read(new StreamSource(is));

        XmlSchemaType simpleType = schema.getTypeByQName(typeQName);
        assertNotNull(simpleType);

        XmlSchemaElement elem = schema.getElementByQName(elementQName);
        assertNotNull(elem);

        XmlSchemaType type = elem.getSchemaType();
        assertNotNull(type);
    }

    @Test
    public void testSimpleTypeRestrictionWithoutNamespace() throws Exception {
        InputStream is = new FileInputStream(Resources.asURI("includedWithoutNamespace.xsd"));
        XmlSchemaCollection schema = new XmlSchemaCollection();
        schema.read(new StreamSource(is));
        XmlSchemaType principalId = schema.getTypeByQName(new QName("", "XdwsPrincipalId"));
        assertNotNull(principalId);
        XmlSchemaSimpleType groupId = (XmlSchemaSimpleType)schema
            .getTypeByQName(new QName("", "XdwsGroupId"));
        assertNotNull(groupId);
        QName baseName = ((XmlSchemaSimpleTypeRestriction)groupId.getContent()).getBaseTypeName();

        assertEquals(principalId.getQName(), baseName);
    }
}
