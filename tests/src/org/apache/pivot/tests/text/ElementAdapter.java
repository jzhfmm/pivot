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
package org.apache.pivot.tests.text;

import java.util.Comparator;
import java.util.Iterator;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.ListListener;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.ImmutableIterator;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.wtk.text.Element;
import org.apache.pivot.wtk.text.ElementListener;
import org.apache.pivot.wtk.text.Node;
import org.apache.pivot.wtk.text.TextNode;


public class ElementAdapter extends NodeAdapter implements List<NodeAdapter> {
    private ElementListener elementListener = new ElementListener() {
        @Override
        public void nodeInserted(Element element, int index) {
            // Insert/attach node
            Node node = element.get(index);
            NodeAdapter nodeAdapter = createNodeAdapter(node);
            nodeAdapters.insert(nodeAdapter, index);
            nodeAdapter.setParent(ElementAdapter.this);

            listListeners.itemInserted(ElementAdapter.this, index);
        }

        @Override
        public void nodesRemoved(Element element, int index, Sequence<Node> nodes) {
            // Remove/detach nodes
            Sequence<NodeAdapter> removed = nodeAdapters.remove(index, nodes.getLength());

            for (int i = 0, n = removed.getLength(); i < n; i++) {
                NodeAdapter nodeAdapter = removed.get(i);
                nodeAdapter.setParent(null);
            }

            listListeners.itemsRemoved(ElementAdapter.this, index, removed);
        }
    };

    private String text;
    private ArrayList<NodeAdapter> nodeAdapters = new ArrayList<NodeAdapter>();
    private ListListenerList<NodeAdapter> listListeners = new ListListenerList<NodeAdapter>();

    public ElementAdapter(Element element) {
        super(element);

        if (element == null) {
            text = null;
        } else {
            String elementClassName = element.getClass().getName();
            text = "<" + elementClassName.substring(elementClassName.lastIndexOf('.') + 1) + ">";
        }
    }

    @Override
    protected void setParent(ElementAdapter parent) {
        super.setParent(parent);

        Element element = (Element)getNode();

        if (parent == null) {
            // Remove all adapters
            Sequence<NodeAdapter> removed = nodeAdapters.remove(0, nodeAdapters.getLength());

            for (int i = 0, n = removed.getLength(); i < n; i++) {
                NodeAdapter nodeAdapter = removed.get(i);
                nodeAdapter.setParent(null);
            }

            element.getElementListeners().remove(elementListener);
        } else {
            // Build adapter list
            for (Node node : element) {
                NodeAdapter nodeAdapter = createNodeAdapter(node);
                nodeAdapters.add(nodeAdapter);
                nodeAdapter.setParent(this);
            }

            element.getElementListeners().add(elementListener);
        }
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int add(NodeAdapter nodeAdapter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(NodeAdapter nodeAdapter, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(NodeAdapter nodeAdapter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Sequence<NodeAdapter> remove(int index, int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeAdapter update(int index, NodeAdapter nodeAdapter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeAdapter get(int index) {
        return nodeAdapters.get(index);
    }

    @Override
    public int indexOf(NodeAdapter nodeAdapter) {
        return nodeAdapters.indexOf(nodeAdapter);
    }

    @Override
    public boolean isEmpty() {
        return (nodeAdapters.getLength() == 0);
    }

    @Override
    public int getLength() {
        return nodeAdapters.getLength();
    }

    @Override
    public Comparator<NodeAdapter> getComparator() {
        return null;
    }

    @Override
    public void setComparator(Comparator<NodeAdapter> comparator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListenerList<ListListener<NodeAdapter>> getListListeners() {
        return listListeners;
    }

    @Override
    public Iterator<NodeAdapter> iterator() {
        return new ImmutableIterator<NodeAdapter>(nodeAdapters.iterator());
    }

    protected void update(int index) {
        listListeners.itemUpdated(this, index, nodeAdapters.get(index));
    }

    private static NodeAdapter createNodeAdapter(Node node) {
        NodeAdapter nodeAdapter;

        if (node instanceof Element) {
            nodeAdapter = new ElementAdapter((Element)node);
        } else if (node instanceof TextNode) {
            nodeAdapter = new TextNodeAdapter((TextNode)node);
        } else {
            throw new IllegalArgumentException("Unsupported node type.");
        }

        return nodeAdapter;
    }
}
