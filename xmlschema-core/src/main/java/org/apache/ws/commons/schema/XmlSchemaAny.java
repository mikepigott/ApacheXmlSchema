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

package org.apache.ws.commons.schema;

/**
 * Enables any element from the specified namespace or namespaces to appear in the containing complexType
 * element. Represents the World Wide Web Consortium (W3C) any element.
 */

public class XmlSchemaAny extends XmlSchemaParticle 
    implements XmlSchemaChoiceMember, XmlSchemaSequenceMember {

    /**
     * Namespaces containing the elements that can be used.
     */
    private String namespace;
    private XmlSchemaContentProcessing processContent;
    private String targetNamespace;

    /**
     * Creates new XmlSchemaAny
     */
    public XmlSchemaAny() {
        processContent = XmlSchemaContentProcessing.NONE;
    }


    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public XmlSchemaContentProcessing getProcessContent() {
        return processContent;
    }

    public void setProcessContent(XmlSchemaContentProcessing processContent) {
        this.processContent = processContent;
    }

    /**
     * {@link #getNamespace()} returns the namespace or set of namespaces
     * that this wildcard element is valid for.  The target namespaces may
     * include <code>##other</code>, <code>##targetNamespace</code>.  The
     * <code>##other</code> directive means any namespace other than the
     * schema's target namespace, while the <code>##targetNamespace</code>
     * directive means the element <i>must be</i> in the schema's target
     * namespace.  Resolving either of these requires knowledge of what
     * the schema's target namespace is, which is returned by this method.
     *
     * @return The wildcard element's target namespace.
     */
    public String getTargetNamespace() {
    	return targetNamespace;
    }

    /**
     * Sets the schema's target namespace.
     *
     * @param namespace The schema's target namespace.
     *
     * @see #getTargetNamespace()
     */
    public void setTargetNamespace(String namespace) {
    	targetNamespace = namespace;
    }
}
