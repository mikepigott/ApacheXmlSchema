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
package org.apache.ws.commons.schema.resolver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.xml.sax.InputSource;

/**
 * This resolver provides the means of resolving the imports and includes of a given schema document. The
 * system will call this default resolver if there is no other resolver present in the system.
 */
public class DefaultURIResolver implements CollectionURIResolver {

    private String collectionBaseURI;

    /**
     * Try to resolve a schema location to some data.
     *
     * @param namespace target namespace.
     * @param schemaLocation system ID.
     * @param baseUri base URI for the schema.
     */
    public InputSource resolveEntity(String namespace, String schemaLocation, String baseUri) {

        if (baseUri != null) {
            try {
                File baseFile = null;
                try {
                    URI uri = new URI(baseUri);
                    baseFile = new File(uri);
                    if (!baseFile.exists()) {
                        baseFile = new File(baseUri);
                    }
                } catch (Throwable ex) {
                    baseFile = new File(baseUri);
                }
                if (baseFile.exists()) {
                    baseUri = baseFile.toURI().toString();
                } else if (collectionBaseURI != null) {
                    baseFile = new File(collectionBaseURI);
                    if (baseFile.exists()) {
                        baseUri = baseFile.toURI().toString();
                    }
                }

                String ref = new URL(new URL(baseUri), schemaLocation).toString();

                return new InputSource(ref);
            } catch (MalformedURLException e1) {
                throw new RuntimeException(e1);
            }

        }
        return new InputSource(schemaLocation);

    }

    /**
     * Find whether a given uri is relative or not
     *
     * @param uri
     * @return boolean
     */
    protected boolean isAbsolute(String uri) {
        return uri.startsWith("http://")
            || uri.startsWith("https://")
            || uri.startsWith("urn:");
    }

    /**
     * This is essentially a call to "new URL(contextURL, spec)" with extra handling in case spec is a file.
     *
     * @param contextURL
     * @param spec
     * @throws java.io.IOException
     */
    protected URL getURL(URL contextURL, String spec) throws IOException {

        // First, fix the slashes as windows filenames may have backslashes
        // in them, but the URL class wont do the right thing when we later
        // process this URL as the contextURL.
        String path = spec.replace('\\', '/');

        // See if we have a good URL.
        URL url;

        try {

            // first, try to treat spec as a full URL
            url = new URL(contextURL, path);

            // if we are deail with files in both cases, create a url
            // by using the directory of the context URL.
            if (contextURL != null && url.getProtocol().equals("file")
                && contextURL.getProtocol().equals("file")) {
                url = getFileURL(contextURL, path);
            }
        } catch (MalformedURLException me) {

            // try treating is as a file pathname
            url = getFileURL(contextURL, path);
        }

        // Everything is OK with this URL, although a file url constructed
        // above may not exist. This will be caught later when the URL is
        // accessed.
        return url;
    } // getURL

    /**
     * Method getFileURL
     *
     * @param contextURL
     * @param path
     * @throws IOException
     */
    protected URL getFileURL(URL contextURL, String path) throws IOException {

        if (contextURL != null) {

            // get the parent directory of the contextURL, and append
            // the spec string to the end.
            String contextFileName = contextURL.getFile();
            URL parent = null;
            // the logic for finding the parent file is this.
            // 1.if the contextURI represents a file then take the parent file
            // of it
            // 2. If the contextURI represents a directory, then take that as
            // the parent
            File parentFile;
            File contextFile = new File(contextFileName);
            if (contextFile.isDirectory()) {
                parentFile = contextFile;
            } else {
                parentFile = contextFile.getParentFile();
            }

            if (parentFile != null) {
                parent = parentFile.toURI().toURL();
            }
            if (parent != null) {
                return new URL(parent, path);
            }
        }

        return new URL("file", "", path);
    } // getFileURL

    /**
     * Get the base URI derived from a schema collection. It serves as a fallback from the specified base.
     *
     * @return URI
     */
    public String getCollectionBaseURI() {
        return collectionBaseURI;
    }

    /**
     * set the collection base URI, which serves as a fallback from the base of the immediate schema.
     *
     * @param collectionBaseURI the URI.
     */
    public void setCollectionBaseURI(String collectionBaseURI) {
        this.collectionBaseURI = collectionBaseURI;
    }
}
