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

import org.apache.ws.commons.schema.*;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

public class ConstraintsTest extends Assert {

    /**
     * This method will test the unique, key, and keyref constaints.
     *
     * @throws Exception Any exception encountered
     */
    @Test
    public void testConstraints() throws Exception {

        /*
         * <schema xmlns="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema"
         * xmlns:tns="http://soapinterop.org/types" targetNamespace="http://soapinterop.org/types"
         * elementFormDefault="qualified"> <element name="constraintTest"> <complexType> <sequence> <element
         * name="manufacturers" type="tns:ManufacturerType"/> <element name="products"
         * type="tns:ProductType"/> </sequence> </complexType> <unique name="uniqueTest"> <selector
         * xpath="tns:manufacturers/tns:location"/> <field xpath="@district"/> </unique> <key name="keyTest">
         * <selector xpath="tns:products/tns:productName"/> <field xpath="@productId"/> </key> <keyref
         * name="keyRefTest" refer="tns:keyTest"> <selector
         * xpath="tns:manufacturers/tns:location/tns:productName"/> <field xpath="@productId"/> </keyref>
         * </element> <complexType name="ManufacturerType"> <sequence> <element name="location"
         * maxOccurs="unbounded"> <complexType> <sequence> <element name="productName" maxOccurs="unbounded"/>
         * <complexType> <complexContent> <extension base="string"> <attribute name="productId"
         * type="integer"/> <attribute name="units" type="integer"/> </extension> </complexContent>
         * </complexType> </element> </sequence> <attribute name="district" type="integer"/> </complexType>
         * </element> </sequence> </complexType> <complexType name="ProductType"> <sequence> <element
         * name="productName" maxOccurs="unbounded"> <complexType> <simpleContent> <extension base="string">
         * <attribute name="productId" type="integer"/> </extension> </simpleContent> </complexType>
         * </element> </sequence> </complexType> </schema>
         */

        QName elementQName = new QName("http://soapinterop.org/types", "constraintTest");
        InputStream is = new FileInputStream(Resources.asURI("constraints.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is));

        XmlSchemaElement elem = schemaCol.getElementByQName(elementQName);
        assertNotNull(elem);
        assertEquals("constraintTest", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "constraintTest"), elem.getQName());

        List<XmlSchemaIdentityConstraint> c = elem.getConstraints();
        assertEquals(6, c.size());

        {
            assertTrue(c.get(0) instanceof XmlSchemaKey);
            XmlSchemaKey key = (XmlSchemaKey) c.get(0);
            assertEquals("keyTest", key.getName());
            XmlSchemaXPath selectorXpath = key.getSelector();
            assertEquals("tns:products/tns:productName", selectorXpath.getXPath());

            List<XmlSchemaXPath> fields = key.getFields();
            assertEquals(1, fields.size());
            XmlSchemaXPath fieldXpath = null;
            for (int j = 0; j < fields.size(); j++) {
                fieldXpath = fields.get(j);
            }
            assertNotNull(fieldXpath);
            assertEquals("@productId", fieldXpath.getXPath());
        }
        {
            assertTrue(c.get(1) instanceof XmlSchemaKey);
            XmlSchemaKey key = (XmlSchemaKey) c.get(1);
            assertEquals("keyTest2", key.getName());
            XmlSchemaXPath selectorXpath = key.getSelector();
            assertEquals("tns:products/tns:productName", selectorXpath.getXPath());

            List<XmlSchemaXPath> fields = key.getFields();
            assertEquals(1, fields.size());
            XmlSchemaXPath fieldXpath = null;
            for (int j = 0; j < fields.size(); j++) {
                fieldXpath = fields.get(j);
            }
            assertNotNull(fieldXpath);
            assertEquals("@productId", fieldXpath.getXPath());
        }
        {
            assertTrue(c.get(2) instanceof XmlSchemaKeyref);
            XmlSchemaKeyref keyref = (XmlSchemaKeyref) c.get(2);
            assertEquals("keyRefTest", keyref.getName());
            assertEquals(new QName("http://soapinterop.org/types", "keyTest"), keyref.getRefer());

            XmlSchemaXPath selectorXpath = keyref.getSelector();
            assertEquals("tns:manufacturers/tns:location/tns:productName", selectorXpath.getXPath());

            List<XmlSchemaXPath> fields = keyref.getFields();
            assertEquals(1, fields.size());
            XmlSchemaXPath fieldXpath = null;
            for (int j = 0; j < fields.size(); j++) {
                fieldXpath = fields.get(j);
            }
            assertNotNull(fieldXpath);
            assertEquals("@productId", fieldXpath.getXPath());
        }
        {
            assertTrue(c.get(3) instanceof XmlSchemaKeyref);
            XmlSchemaKeyref keyref = (XmlSchemaKeyref) c.get(3);
            assertEquals("keyRefTest2", keyref.getName());
            assertEquals(new QName("http://soapinterop.org/types", "keyTest2"), keyref.getRefer());

            XmlSchemaXPath selectorXpath = keyref.getSelector();
            assertEquals("tns:manufacturers/tns:location/tns:productName", selectorXpath.getXPath());

            List<XmlSchemaXPath> fields = keyref.getFields();
            assertEquals(1, fields.size());
            XmlSchemaXPath fieldXpath = null;
            for (int j = 0; j < fields.size(); j++) {
                fieldXpath = fields.get(j);
            }
            assertNotNull(fieldXpath);
            assertEquals("@productId", fieldXpath.getXPath());

        }
        {
            assertTrue(c.get(4) instanceof XmlSchemaUnique);
            XmlSchemaUnique unique = (XmlSchemaUnique) c.get(4);
            assertNotNull(unique);
            assertEquals("uniqueTest", unique.getName());
            XmlSchemaXPath selectorXpath = unique.getSelector();
            assertEquals("tns:manufacturers/tns:location", selectorXpath.getXPath());

            List<XmlSchemaXPath> fields = unique.getFields();
            assertEquals(1, fields.size());
            XmlSchemaXPath fieldXpath = null;
            for (int j = 0; j < fields.size(); j++) {
                fieldXpath = fields.get(j);
            }
            assertNotNull(fieldXpath);
            assertEquals("@district", fieldXpath.getXPath());
        }
        {
            assertTrue(c.get(5) instanceof XmlSchemaUnique);
            XmlSchemaUnique unique = (XmlSchemaUnique) c.get(5);
            assertNotNull(unique);
            assertEquals("uniqueTest2", unique.getName());
            XmlSchemaXPath selectorXpath = unique.getSelector();
            assertEquals("tns:products/tns:productName", selectorXpath.getXPath());

            List<XmlSchemaXPath> fields = unique.getFields();
            assertEquals(1, fields.size());
            XmlSchemaXPath fieldXpath = null;
            for (int j = 0; j < fields.size(); j++) {
                fieldXpath = fields.get(j);
            }
            assertNotNull(fieldXpath);
            assertEquals("@productId", fieldXpath.getXPath());
        }

        StringWriter writer = new StringWriter();
        schema.write(writer);
        String str = writer.toString();
        assertTrue(str.contains("name=\"uniqueTest\""));
        assertTrue(str.contains("name=\"uniqueTest2\""));
        assertTrue(str.contains("name=\"keyTest\""));
        assertTrue(str.contains("name=\"keyTest2\""));
        assertTrue(str.contains("name=\"keyRefTest\""));
        assertTrue(str.contains("name=\"keyRefTest2\""));
    }

}
