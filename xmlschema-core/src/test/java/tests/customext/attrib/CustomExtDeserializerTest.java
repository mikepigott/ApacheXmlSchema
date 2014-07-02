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
package tests.customext.attrib;

import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.constants.Constants;

import org.junit.Assert;
import org.junit.Test;

import tests.Resources;

/**
 * Deserialize the custom extension types
 */
public class CustomExtDeserializerTest extends Assert {

    @Test
    public void testDeserialization() throws Exception {
        // set the system property for the custom extension registry
        System.setProperty(Constants.SystemConstants.EXTENSION_REGISTRY_KEY, CustomExtensionRegistry.class
            .getName());

        try {
            // create a DOM document
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            Document doc = documentBuilderFactory.newDocumentBuilder()
                .parse(Resources.asURI("/external/externalAnnotations.xsd"));

            XmlSchemaCollection schemaCol = new XmlSchemaCollection();
            XmlSchema schema = schemaCol.read(doc, null);
            assertNotNull(schema);

            // get the elements and check whether their annotations are properly
            // populated
            for (XmlSchemaElement elt : schema.getElements().values()) {
                assertNotNull(elt);
                Map<Object, Object> metaInfoMap = elt.getMetaInfoMap();
                assertNotNull(metaInfoMap);

                CustomAttribute customAttrib = (CustomAttribute)metaInfoMap
                    .get(CustomAttribute.CUSTOM_ATTRIBUTE_QNAME);
                assertNotNull(customAttrib);

            }
        } finally {
            // remove our system property
            System.getProperties().remove(Constants.SystemConstants.EXTENSION_REGISTRY_KEY);

        }
    }
}
