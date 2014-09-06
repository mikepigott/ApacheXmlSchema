/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ws.commons.schema.docpath;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.resolver.XmlSchemaMultiBaseUriResolver;
import org.apache.ws.commons.schema.testutils.UtilsForTests;
import org.apache.ws.commons.schema.walker.XmlSchemaWalker;
import org.apache.ws.commons.schema.walker.XmlSchemaTypeInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

public class TestXmlSchemaPathFinder {

  private static final String TESTSCHEMA_NS = "http://avro.apache.org/AvroTest";
  private static final String COMPLEX_SCHEMA_NS = "urn:avro:complex_schema";

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

    XmlSchemaPathNode traversal = runTest(schemaFile, xmlFile, root);

    Map<QName, XmlSchemaTypeInfo.Type> expectedTypes =
        new HashMap<QName, XmlSchemaTypeInfo.Type>();

    expectedTypes.put(root, XmlSchemaTypeInfo.Type.COMPLEX);

    validate(traversal.getDocumentNode(), expectedTypes);
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

    XmlSchemaPathNode traversal = runTest(schemaFile, xmlFile, root);

    Map<QName, XmlSchemaTypeInfo.Type> expectedTypes =
        new HashMap<QName, XmlSchemaTypeInfo.Type>();

    expectedTypes.put(root, XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(TESTSCHEMA_NS, "primitive"),
        XmlSchemaTypeInfo.Type.ATOMIC);

    expectedTypes.put(
        new QName(TESTSCHEMA_NS, "nonNullPrimitive"),
        XmlSchemaTypeInfo.Type.ATOMIC);

    validate(traversal.getDocumentNode(), expectedTypes);
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

    XmlSchemaPathNode traversal = runTest(schemaFile, xmlFile, root);

    Map<QName, XmlSchemaTypeInfo.Type> expectedTypes =
        new HashMap<QName, XmlSchemaTypeInfo.Type>();

    expectedTypes.put(root, XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(TESTSCHEMA_NS, "primitive"),
        XmlSchemaTypeInfo.Type.ATOMIC);

    expectedTypes.put(
        new QName(TESTSCHEMA_NS, "nonNullPrimitive"),
        XmlSchemaTypeInfo.Type.ATOMIC);

    expectedTypes.put(
        new QName(TESTSCHEMA_NS, "map"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(TESTSCHEMA_NS, "record"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    validate(traversal.getDocumentNode(), expectedTypes);
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

    XmlSchemaPathNode traversal = runTest(xmlSchemaCollection, xmlFile, root);

    Map<QName, XmlSchemaTypeInfo.Type> expectedTypes =
        new HashMap<QName, XmlSchemaTypeInfo.Type>();

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "realRoot"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "backtrack"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "allTheThings"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "prohibit"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "anyAndFriends"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "simpleExtension"),
        XmlSchemaTypeInfo.Type.UNION);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "simpleRestriction"),
        XmlSchemaTypeInfo.Type.UNION);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "complexExtension"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "mixedType"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "qName"),
        XmlSchemaTypeInfo.Type.ATOMIC);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "avroEnum"),
        XmlSchemaTypeInfo.Type.ATOMIC);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "xmlEnum"),
        XmlSchemaTypeInfo.Type.ATOMIC);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "unsignedLongList"),
        XmlSchemaTypeInfo.Type.LIST);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "listOfUnion"),
        XmlSchemaTypeInfo.Type.LIST);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "firstMap"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "value"),
        XmlSchemaTypeInfo.Type.ATOMIC);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "secondMap"),
        XmlSchemaTypeInfo.Type.COMPLEX);

    expectedTypes.put(
        new QName(COMPLEX_SCHEMA_NS, "fixed"),
        XmlSchemaTypeInfo.Type.ATOMIC);

    validate(traversal.getDocumentNode(), expectedTypes);
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

  private void validate(
      XmlSchemaDocumentNode docNode,
      Map<QName, XmlSchemaTypeInfo.Type> expectedTypes) {

    for (int iter = 1; iter <= docNode.getIteration(); ++iter) {
      switch ( docNode.getStateMachineNode().getNodeType() ) {
      case ANY:
        break;
      case ELEMENT:
        {
          XmlSchemaStateMachineNode stateMachine =
              docNode.getStateMachineNode();

          QName elemQName = stateMachine.getElement().getQName();

          XmlSchemaTypeInfo.Type expectedType =
              expectedTypes.get(elemQName);

          assertNotNull(
              "No type information found for " + elemQName.toString(),
              expectedType);

          assertEquals(
              elemQName.toString(),
              expectedType,
              stateMachine.getElementType().getType());
        }
        /* falls through */
      default:
        {
          // If it's neither an element nor a wildcard, it's a group.
          SortedMap<Integer, XmlSchemaDocumentNode> children =
            docNode.getChildren(iter);

          if (children != null) {
            for (Map.Entry<Integer, XmlSchemaDocumentNode> child
                  : children.entrySet()) {
              validate(child.getValue(), expectedTypes);
            }
          }
        }
      }
    }
  }
}
