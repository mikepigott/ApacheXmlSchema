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

package org.apache.ws.commons.schema.docpath;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.testutils.UtilsForTests;
import org.apache.ws.commons.schema.walker.XmlSchemaWalker;
import org.junit.Test;

public class TestXmlSchemaStateMachineGenerator {

  private static final String TESTSCHEMA_NS = "http://avro.apache.org/AvroTest";
  private static final String COMPLEX_SCHEMA_NS = "urn:avro:complex_schema";

  @Test
  public void testSchema() throws IOException {
    final File schemaFile =
        UtilsForTests.buildFile("src", "test", "resources", "test_schema.xsd");

    XmlSchemaStateMachineNode stateMachine =
        buildSchema(schemaFile, new QName(TESTSCHEMA_NS, "root"));

    HashSet<QName> seen = new HashSet<QName>();
  }

  @Test
  public void testComplex() throws IOException {
    final File schemaFile =
        UtilsForTests.buildFile(
            "src",
            "test",
            "resources",
            "complex_schema.xsd");

    XmlSchemaStateMachineNode stateMachine =
        buildSchema(schemaFile, new QName(COMPLEX_SCHEMA_NS, "root"));

    HashSet<QName> seen = new HashSet<QName>();
  }

  private XmlSchemaStateMachineNode buildSchema(File schemaFile, QName root)
      throws IOException {

    XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();

    FileReader schemaFileReader = null;
    try {
      schemaFileReader = new FileReader(schemaFile);
      xmlSchemaCollection.read(new StreamSource(schemaFileReader));
    } finally {
      if (schemaFileReader != null) {
        schemaFileReader.close();
      }
    }

    XmlSchemaStateMachineGenerator stateMachineGen =
        new XmlSchemaStateMachineGenerator();

    XmlSchemaWalker walker =
        new XmlSchemaWalker(xmlSchemaCollection, stateMachineGen);

    XmlSchemaElement rootElement = xmlSchemaCollection.getElementByQName(root);

    walker.walk(rootElement);

    return stateMachineGen.getStartNode();
  }
}
