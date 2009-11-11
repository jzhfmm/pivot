/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pivot.xml;

/**
 * Element listener interface.
 */
public interface ElementListener {
    /**
     * Element listener adapter.
     */
    public static class Adapter implements ElementListener {
        public void namespacePrefixChanged(Element element, String previousNamespacePrefix) {
        }

        public void localNameChanged(Element element, String previousLocalName) {
        }

        public void attributeAdded(Element element, String attribute) {
        }

        public void attributeUpdated(Element element, String attribute, String previousValue) {
        }

        public void attributeRemoved(Element element, String attribute) {
        }

        public void namespaceAdded(Element element, String prefix) {
        }

        public void namespaceUpdated(Element element, String prefix, String previousURI) {
        }

        public void namespaceRemoved(Element element, String prefix) {
        }
    }

    /**
     * Called when an element's namespace prefix has changed.
     *
     * @param element
     * @param previousNamespacePrefix
     */
    public void namespacePrefixChanged(Element element, String previousNamespacePrefix);

    /**
     * Called when an element's local name has changed.
     *
     * @param element
     * @param previousLocalName
     */
    public void localNameChanged(Element element, String previousLocalName);

    /**
     * Called when an attribute has been added to an element.
     *
     * @param element
     * @param attribute
     */
    public void attributeAdded(Element element, String attribute);

    /**
     * Called when an element attribute has been updated.
     *
     * @param element
     * @param attribute
     * @param previousValue
     */
    public void attributeUpdated(Element element, String attribute, String previousValue);

    /**
     * Called when an attribute has been removed from an element.
     *
     * @param element
     * @param attribute
     */
    public void attributeRemoved(Element element, String attribute);

    /**
     * Called when a namespace has been added to an element.
     *
     * @param element
     * @param prefix
     */
    public void namespaceAdded(Element element, String prefix);

    /**
     * Called when an element attribute has been updated.
     *
     * @param element
     * @param prefix
     * @param previousURI
     */
    public void namespaceUpdated(Element element, String prefix, String previousURI);

    /**
     * Called when a namespace has been removed from an element.
     *
     * @param element
     * @param prefix
     */
    public void namespaceRemoved(Element element, String prefix);
}
