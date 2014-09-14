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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
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

    ExpectedNode node =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.<SortedMap<Integer, ExpectedNode>>singletonList(
                new TreeMap<Integer, ExpectedNode>()));

    node.setElemQName(
        new QName(TESTSCHEMA_NS, "root"));

    ExpectedNode.validate(
        root.toString(),
        node,
        traversal.getDocumentNode(),
        null);
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

    ExpectedNode primitive =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.<SortedMap<Integer, ExpectedNode>>singletonList(
                new TreeMap<Integer, ExpectedNode>()));
    primitive.setElemQName( new QName(TESTSCHEMA_NS, "primitive") );

    ExpectedNode nonNullPrimitive =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.<SortedMap<Integer, ExpectedNode>>singletonList(
                new TreeMap<Integer, ExpectedNode>()));
    nonNullPrimitive.setElemQName(
        new QName(TESTSCHEMA_NS, "nonNullPrimitive"));

    // avro:primitive is the first choice; its index is 0
    TreeMap<Integer, ExpectedNode> choicePrimitiveChild =
        new TreeMap<Integer, ExpectedNode>();
    choicePrimitiveChild.put(0, primitive);

    // avro:nonNullPrimitive is the second choice; its index is 1
    TreeMap<Integer, ExpectedNode> choiceNonNullPrimitiveChild =
        new TreeMap<Integer, ExpectedNode>();
    choiceNonNullPrimitiveChild.put(1, nonNullPrimitive);

    ArrayList<SortedMap<Integer, ExpectedNode>> choiceChildren =
        new ArrayList<SortedMap<Integer, ExpectedNode>>();

    for (int i = 0; i < 9; ++i) {
      choiceChildren.add(choicePrimitiveChild);
    }
    for (int i = 0; i < 8; ++i) {
      choiceChildren.add(choiceNonNullPrimitiveChild);
    }

    ExpectedNode choiceNode =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.CHOICE,
            0L,
            Long.MAX_VALUE,
            choiceChildren);

    TreeMap<Integer, ExpectedNode> sequenceChild =
        new TreeMap<Integer, ExpectedNode>();
    sequenceChild.put(0, choiceNode);

    ExpectedNode sequenceNode =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SEQUENCE,
            1,
            1,
            Collections.<SortedMap<Integer, ExpectedNode>>singletonList(
                sequenceChild));

    TreeMap<Integer, ExpectedNode> rootChild =
        new TreeMap<Integer, ExpectedNode>();
    rootChild.put(0, sequenceNode);

    ExpectedNode rootNode =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.<SortedMap<Integer, ExpectedNode>>singletonList(
                rootChild));
    rootNode.setElemQName(
        new QName(TESTSCHEMA_NS, "root"));

    ExpectedNode.validate(
        root.toString(),
        rootNode,
        traversal.getDocumentNode(),
        null);
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

    ExpectedNode primitive =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.<SortedMap<Integer, ExpectedNode>>singletonList(
                new TreeMap<Integer, ExpectedNode>()));
    primitive.setElemQName( new QName(TESTSCHEMA_NS, "primitive") );

    ExpectedNode nonNullPrimitive =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.<SortedMap<Integer, ExpectedNode>>singletonList(
                new TreeMap<Integer, ExpectedNode>()));
    nonNullPrimitive.setElemQName(
        new QName(TESTSCHEMA_NS, "nonNullPrimitive"));

    // avro:primitive is the first choice; its index is 0
    TreeMap<Integer, ExpectedNode> choicePrimitiveChild =
        new TreeMap<Integer, ExpectedNode>();
    choicePrimitiveChild.put(0, primitive);

    // avro:nonNullPrimitive is the second choice; its index is 1
    TreeMap<Integer, ExpectedNode> choiceNonNullPrimitiveChild =
        new TreeMap<Integer, ExpectedNode>();
    choiceNonNullPrimitiveChild.put(1, nonNullPrimitive);

    // map 1
    ArrayList<SortedMap<Integer, ExpectedNode>> map1ChoiceChildren =
        new ArrayList<SortedMap<Integer, ExpectedNode>>();
    map1ChoiceChildren.add(choicePrimitiveChild);
    map1ChoiceChildren.add(choiceNonNullPrimitiveChild);

    ExpectedNode map1Choice =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.CHOICE,
            0,
            Long.MAX_VALUE,
            map1ChoiceChildren);

    SortedMap<Integer, ExpectedNode> map1SeqChildren =
        new TreeMap<Integer, ExpectedNode>();
    map1SeqChildren.put(0, map1Choice);

    ExpectedNode map1Seq =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SEQUENCE,
            1,
            1,
            Collections.singletonList(map1SeqChildren));

    SortedMap<Integer, ExpectedNode> map1Children =
        new TreeMap<Integer, ExpectedNode>();
    map1Children.put(0, map1Seq);

    ExpectedNode map1 =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.singletonList(map1Children));
    map1.setElemQName(new QName(TESTSCHEMA_NS, "map"));

    // avro:map is a substitution of avro:record, its index is 2
    SortedMap<Integer, ExpectedNode> map1SubsGrpChild =
        new TreeMap<Integer, ExpectedNode>();
    map1SubsGrpChild.put(1, map1);

    ExpectedNode map1SubstGroup =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SUBSTITUTION_GROUP,
            1,
            1,
            Collections.singletonList(map1SubsGrpChild));

    SortedMap<Integer, ExpectedNode> choiceMap1Child =
        new TreeMap<Integer, ExpectedNode>();
    choiceMap1Child.put(2, map1SubstGroup);

    // map 2
    SortedMap<Integer, ExpectedNode> map2ChoiceChildren =
        new TreeMap<Integer, ExpectedNode>();
    map2ChoiceChildren.put(0, primitive);

    ExpectedNode map2Choice =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.CHOICE,
            0,
            Long.MAX_VALUE,
            Collections.singletonList(map2ChoiceChildren));

    SortedMap<Integer, ExpectedNode> map2SequenceChildren =
        new TreeMap<Integer, ExpectedNode>();
    map2SequenceChildren.put(0, map2Choice);

    ExpectedNode map2Seq =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SEQUENCE,
            1,
            1,
            Collections.singletonList(map2SequenceChildren));

    SortedMap<Integer, ExpectedNode> map2Children =
        new TreeMap<Integer, ExpectedNode>();
    map2Children.put(0, map2Seq);

    ExpectedNode map2 =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.singletonList(map2Children));
    map2.setElemQName(new QName(TESTSCHEMA_NS, "map"));

    SortedMap<Integer, ExpectedNode> map2SubstGrpChild =
        new TreeMap<Integer, ExpectedNode>();
    map2SubstGrpChild.put(1, map2);

    ExpectedNode map2SubstGrp =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SUBSTITUTION_GROUP,
            1,
            1,
            Collections.singletonList(map2SubstGrpChild));

    SortedMap<Integer, ExpectedNode> choiceMap2Child =
        new TreeMap<Integer, ExpectedNode>();
    choiceMap2Child.put(2, map2SubstGrp);

    // map 4, which is owned by map 3
    SortedMap<Integer, ExpectedNode> map4ChoiceChildren =
        new TreeMap<Integer, ExpectedNode>();
    map4ChoiceChildren.put(1, nonNullPrimitive);

    ExpectedNode map4Choice =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.CHOICE,
            0,
            Long.MAX_VALUE,
            Collections.singletonList(map4ChoiceChildren));

    SortedMap<Integer, ExpectedNode> map4SeqChildren =
        new TreeMap<Integer, ExpectedNode>();
    map4SeqChildren.put(0, map4Choice);

    ExpectedNode map4Seq =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SEQUENCE,
            1,
            1,
            Collections.singletonList(map4SeqChildren));

    SortedMap<Integer, ExpectedNode> map4Children =
        new TreeMap<Integer, ExpectedNode>();
    map4Children.put(0, map4Seq);

    ExpectedNode map4 =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.singletonList(map4Children));
    map4.setElemQName(new QName(TESTSCHEMA_NS, "map"));

    SortedMap<Integer, ExpectedNode> map4SubstGrpChildren =
        new TreeMap<Integer, ExpectedNode>();
    map4SubstGrpChildren.put(1, map4);

    ExpectedNode map4SubstGrp =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SUBSTITUTION_GROUP,
            1,
            1,
            Collections.singletonList(map4SubstGrpChildren));

    // map 5, which is owned by map 3
    SortedMap<Integer, ExpectedNode> map5ChoiceChildren =
        new TreeMap<Integer, ExpectedNode>();
    map5ChoiceChildren.put(0, primitive);

    ExpectedNode map5Choice =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.CHOICE,
            0, 
            Long.MAX_VALUE,
            Collections.singletonList(map5ChoiceChildren));

    SortedMap<Integer, ExpectedNode> map5SeqChildren =
        new TreeMap<Integer, ExpectedNode>();
    map5SeqChildren.put(0, map5Choice);

    ExpectedNode map5Seq =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SEQUENCE,
            1,
            1,
            Collections.singletonList(map5SeqChildren));

    SortedMap<Integer, ExpectedNode> map5Children =
        new TreeMap<Integer, ExpectedNode>();
    map5Children.put(0, map5Seq);

    ExpectedNode map5 =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.singletonList(map5Children));
    map5.setElemQName(new QName(TESTSCHEMA_NS, "map"));

    SortedMap<Integer, ExpectedNode> map5SubstGrpChildren =
        new TreeMap<Integer, ExpectedNode>();
    map5SubstGrpChildren.put(1, map5);

    ExpectedNode map5SubstGrp =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SUBSTITUTION_GROUP,
            1,
            1,
            Collections.singletonList(map5SubstGrpChildren));

    // map 3
    ArrayList<SortedMap<Integer, ExpectedNode>> map3ChoiceChildren =
        new ArrayList<SortedMap<Integer, ExpectedNode>>();

    SortedMap<Integer, ExpectedNode> map3Child1 =
        new TreeMap<Integer, ExpectedNode>();
    map3Child1.put(0, primitive);

    SortedMap<Integer, ExpectedNode> map3Child2 =
        new TreeMap<Integer, ExpectedNode>();
    map3Child2.put(2, map4SubstGrp);

    SortedMap<Integer, ExpectedNode> map3Child3 =
        new TreeMap<Integer, ExpectedNode>();
    map3Child3.put(2, map5SubstGrp);

    map3ChoiceChildren.add(map3Child1);
    map3ChoiceChildren.add(map3Child2);
    map3ChoiceChildren.add(map3Child3);

    ExpectedNode map3Choice =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.CHOICE,
            0,
            Long.MAX_VALUE,
            map3ChoiceChildren);

    SortedMap<Integer, ExpectedNode> map3SeqChildren =
        new TreeMap<Integer, ExpectedNode>();
    map3SeqChildren.put(0, map3Choice);

    ExpectedNode map3Seq =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SEQUENCE,
            1,
            1,
            Collections.singletonList(map3SeqChildren));

    SortedMap<Integer, ExpectedNode> map3Children =
        new TreeMap<Integer, ExpectedNode>();
    map3Children.put(0, map3Seq);

    ExpectedNode map3 =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.singletonList(map3Children));
    map3.setElemQName(new QName(TESTSCHEMA_NS, "map"));

    SortedMap<Integer, ExpectedNode> map3SubstGrpChildren =
        new TreeMap<Integer, ExpectedNode>();
    map3SubstGrpChildren.put(1, map3);

    ExpectedNode map3SubstGrp =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SUBSTITUTION_GROUP,
            1,
            1,
            Collections.singletonList(map3SubstGrpChildren));

    SortedMap<Integer, ExpectedNode> choiceMap3Child =
        new TreeMap<Integer, ExpectedNode>();
    choiceMap3Child.put(2, map3SubstGrp);

    // avro:record
    SortedMap<Integer, ExpectedNode> recordChoiceChildren =
        new TreeMap<Integer, ExpectedNode>();
    recordChoiceChildren.put(1, nonNullPrimitive);

    ExpectedNode recordChoice =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.CHOICE,
            0,
            Long.MAX_VALUE,
            Collections.singletonList(recordChoiceChildren));

    SortedMap<Integer, ExpectedNode> recordSeqChildren =
        new TreeMap<Integer, ExpectedNode>();
    recordSeqChildren.put(0, recordChoice);

    ExpectedNode recordSeq =
      new ExpectedNode(
              XmlSchemaStateMachineNode.Type.SEQUENCE,
              1,
              1,
              Collections.singletonList(recordSeqChildren));

    SortedMap<Integer, ExpectedNode> recordChildren =
        new TreeMap<Integer, ExpectedNode>();
    recordChildren.put(0, recordSeq);

    ExpectedNode record =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.singletonList(recordChildren));
    record.setElemQName(new QName(TESTSCHEMA_NS, "record"));

    SortedMap<Integer, ExpectedNode> recordSubstGrpChildren =
        new TreeMap<Integer, ExpectedNode>();
    recordSubstGrpChildren.put(0, record);

    ExpectedNode recordSubstGrp =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SUBSTITUTION_GROUP,
            1,
            1,
            Collections.singletonList(recordSubstGrpChildren));

    SortedMap<Integer, ExpectedNode> choiceRecordChild =
        new TreeMap<Integer, ExpectedNode>();
    choiceRecordChild.put(2, recordSubstGrp);

    // root
    ArrayList<SortedMap<Integer, ExpectedNode>> choiceChildren =
        new ArrayList<SortedMap<Integer, ExpectedNode>>();

    choiceChildren.add(choicePrimitiveChild);
    choiceChildren.add(choiceNonNullPrimitiveChild);
    choiceChildren.add(choiceMap1Child);
    choiceChildren.add(choiceMap2Child);
    choiceChildren.add(choiceRecordChild);
    choiceChildren.add(choiceMap3Child);
    choiceChildren.add(choiceNonNullPrimitiveChild);
    choiceChildren.add(choiceNonNullPrimitiveChild);

    ExpectedNode choiceNode =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.CHOICE,
            0L,
            Long.MAX_VALUE,
            choiceChildren);

    TreeMap<Integer, ExpectedNode> sequenceChild =
        new TreeMap<Integer, ExpectedNode>();
    sequenceChild.put(0, choiceNode);

    ExpectedNode sequenceNode =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.SEQUENCE,
            1,
            1,
            Collections.<SortedMap<Integer, ExpectedNode>>singletonList(
                sequenceChild));

    TreeMap<Integer, ExpectedNode> rootChild =
        new TreeMap<Integer, ExpectedNode>();
    rootChild.put(0, sequenceNode);

    ExpectedNode rootNode =
        new ExpectedNode(
            XmlSchemaStateMachineNode.Type.ELEMENT,
            1,
            1,
            Collections.<SortedMap<Integer, ExpectedNode>>singletonList(
                rootChild));
    rootNode.setElemQName(
        new QName(TESTSCHEMA_NS, "root"));

    ExpectedNode.validate(
        root.toString(),
        rootNode,
        traversal.getDocumentNode(),
        null);

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
