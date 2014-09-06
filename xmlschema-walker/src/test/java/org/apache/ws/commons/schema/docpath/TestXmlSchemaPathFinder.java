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

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.resolver.XmlSchemaMultiBaseUriResolver;
import org.apache.ws.commons.schema.testutils.UtilsForTests;
import org.apache.ws.commons.schema.walker.XmlSchemaWalker;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

public class TestXmlSchemaPathFinder {

  private static DocumentBuilderFactory dbf;

  private DocumentBuilder docBuilder;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
  }

  @Before
  public void setUp() throws Exception {
    docBuilder = dbf.newDocumentBuilder();
  }

  @Test
  public void testRoot() throws Exception {
    final QName root = new QName("http://avro.apache.org/AvroTest", "root");

    final File schemaFile =
        UtilsForTests.buildFile("src", "test", "resources", "test_schema.xsd");

    final File xmlFile =
        UtilsForTests.buildFile("src", "test", "resources", "test1_root.xml");

    runTest(schemaFile, xmlFile, root);
  }

  @Test
  public void testChildren() throws Exception {
    final QName root = new QName("http://avro.apache.org/AvroTest", "root");

    final File schemaFile =
        UtilsForTests.buildFile("src", "test", "resources", "test_schema.xsd");

    final File xmlFile =
        UtilsForTests.buildFile(
            "src",
            "test",
            "resources",
            "test2_children.xml");

    runTest(schemaFile, xmlFile, root);
  }

  @Test
  public void testGrandchildren() throws Exception {
    final QName root = new QName("http://avro.apache.org/AvroTest", "root");

    final File schemaFile =
        UtilsForTests.buildFile("src", "test", "resources", "test_schema.xsd");

    final File xmlFile =
        UtilsForTests.buildFile(
            "src",
            "test",
            "resources",
            "test3_grandchildren.xml");

    runTest(schemaFile, xmlFile, root);
  }

  @Test
  public void testComplex() throws Exception {
    final QName root = new QName("urn:avro:complex_schema", "root");

    final File complexSchemaFile =
        UtilsForTests.buildFile(
            "src",
            "test",
            "resources",
            "complex_schema.xsd");

    final File testSchemaFile =
        UtilsForTests.buildFile("src", "test", "resources", "test_schema.xsd");

    final File xmlFile =
        UtilsForTests.buildFile(
            "src",
            "test",
            "resources",
            "complex_test1.xml");

    XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
    xmlSchemaCollection.setSchemaResolver(new XmlSchemaMultiBaseUriResolver());

    FileReader schemaFileReader = null;
    try {
      schemaFileReader = new FileReader(complexSchemaFile);
      xmlSchemaCollection.read(new StreamSource(schemaFileReader));
    } finally {
      if (schemaFileReader != null) {
        schemaFileReader.close();
      }
    }

    schemaFileReader = null;
    try {
      schemaFileReader = new FileReader(testSchemaFile);
      xmlSchemaCollection.read(new StreamSource(schemaFileReader));
    } finally {
      if (schemaFileReader != null) {
        schemaFileReader.close();
      }
    }

    runTest(xmlSchemaCollection, xmlFile, root);
  }

  private XmlSchemaPathNode runTest(File schemaFile, File xmlFile, QName root)
      throws Exception{

    XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
    xmlSchemaCollection.setSchemaResolver(new XmlSchemaMultiBaseUriResolver());

    FileReader schemaFileReader = null;
    try {
      schemaFileReader = new FileReader(schemaFile);
      xmlSchemaCollection.read(new StreamSource(schemaFileReader));
    } finally {
      if (schemaFileReader != null) {
        schemaFileReader.close();
      }
    }

    return runTest(xmlSchemaCollection, xmlFile, root);
  }

  private XmlSchemaPathNode runTest(
      XmlSchemaCollection xmlSchemaCollection,
      File xmlFile,
      QName root)
  throws Exception {

    XmlSchemaStateMachineGenerator stateMachineGen =
        new XmlSchemaStateMachineGenerator();

    XmlSchemaWalker walker =
        new XmlSchemaWalker(xmlSchemaCollection, stateMachineGen);

    XmlSchemaElement rootElement = xmlSchemaCollection.getElementByQName(root);

    walker.walk(rootElement);

    XmlSchemaStateMachineNode stateMachine = stateMachineGen.getStartNode();

    XmlSchemaPathFinder pathFinder = new XmlSchemaPathFinder(stateMachine);

    Document xmlDoc = docBuilder.parse(xmlFile);

    SaxWalkerOverDom saxWalker = new SaxWalkerOverDom(pathFinder);

    saxWalker.walk(xmlDoc);

    return pathFinder.getXmlSchemaTraversal();
  }
}
