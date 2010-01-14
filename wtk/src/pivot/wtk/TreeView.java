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
package pivot.wtk;

import java.util.Comparator;

import pivot.collections.ArrayList;
import pivot.collections.List;
import pivot.collections.ListListener;
import pivot.collections.Sequence;
import pivot.util.ListenerList;
import pivot.wtk.content.TreeViewNodeRenderer;

/**
 * Class that displays a hierarchical data structure, allowing a user to select
 * one or more paths.
 *
 * @author tvolkert
 */
public class TreeView extends Component {
    /**
     * Enumeration defining supported selection modes. <tt>TreeView</tt>
     * defaults to single select mode.
     */
    public enum SelectMode {
        /**
         * Selection is disabled.
         */
        NONE,

        /**
         * A single path may be selected at a time.
         */
        SINGLE,

        /**
         * Multiple paths may be concurrently selected.
         */
        MULTI;

        public static SelectMode decode(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    /**
     * Enumeration defining node check states. Note that <tt>TreeView</tt> does
     * not involve itself in the propagation of checkmarks (either up or down
     * the tree). Developers who wish to propagate checkmarks may do so by
     * registering a {@link TreeViewNodeStateListener} and setting the desired
     * checkmark states manually.
     */
    public enum NodeCheckState {
        /**
         * The node is checked.
         */
        CHECKED,

        /**
         * The node is unchecked. If <tt>showMixedCheckmarkState</tt> is true,
         * this implies that all of the node's descendants are unchecked as
         * well.
         */
        UNCHECKED,

        /**
         * The node's check state is mixed, meaning that it is not checked,
         * but at least one of its descendants is checked. This state will only
         * be reported if <tt>showMixedCheckmarkState</tt> is true. Otherwise,
         * the node will be reported as {@link #UNCHECKED}.
         */
        MIXED;

        public static NodeCheckState decode(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    /**
     * Tree view node renderer interface.
     *
     * @author tvolkert
     */
    public interface NodeRenderer extends Renderer {
        public void render(Object node, TreeView treeView, boolean expanded,
            boolean selected, NodeCheckState checkState, boolean highlighted,
            boolean disabled);
    }

    /**
     * Tree view node editor interface.
     *
     * @author tvolkert
     */
    public interface NodeEditor extends Editor {
        /**
         * Notifies the editor that editing should begin.
         *
         * @param treeView
         * The tree view containing the node to be edited.
         *
         * @param path
         * The path to the node to edit.
         */
        public void edit(TreeView treeView, Sequence<Integer> path);
    }

    /**
     * Tree view skin interface. Tree view skins must implement this.
     *
     * @author tvolkert
     */
    public interface Skin {
        /**
         * Gets the path to the node found at the specified y-coordinate
         * (relative to the tree view).
         *
         * @param y
         * The y-coordinate in pixels.
         *
         * @return
         * The path to the node, or <tt>null</tt> if there is no node being
         * painted at the specified y-coordinate.
         */
        public Sequence<Integer> getNodeAt(int y);

        /**
         * Gets the bounds of the node at the specified path relative to the
         * tree view. Note that all nodes are left aligned with the tree; to
         * get the pixel value of a node's indent, use
         * {@link #getNodeIndent(int)}.
         *
         * @param path
         * The path to the node.
         *
         * @return
         * The bounds, or <tt>null</tt> if the node is not currently visible.
         */
        public Bounds getNodeBounds(Sequence<Integer> path);

        /**
         * Gets the pixel indent of nodes at the specified depth. Depth is
         * measured in generations away from the tree view's "root" node, which
         * is represented by the {@link #getTreeData() tree data}.
         *
         * @param depth
         * The depth, where the first child of the root has depth 1, the child
         * of that branch has depth 2, etc.
         *
         * @return
         * The indent in pixels.
         */
        public int getNodeIndent(int depth);
    }

    /**
     * Tree view listener list.
     *
     * @author tvolkert
     */
    private static class TreeViewListenerList extends ListenerList<TreeViewListener>
        implements TreeViewListener {

        public void treeDataChanged(TreeView treeView, List<?> previousTreeData) {
            for (TreeViewListener listener : this) {
                listener.treeDataChanged(treeView, previousTreeData);
            }
        }

        public void nodeRendererChanged(TreeView treeView,
            NodeRenderer previousNodeRenderer) {
            for (TreeViewListener listener : this) {
                listener.nodeRendererChanged(treeView, previousNodeRenderer);
            }
        }

        public void nodeEditorChanged(TreeView treeView,
            TreeView.NodeEditor previousNodeEditor) {
            for (TreeViewListener listener : this) {
                listener.nodeEditorChanged(treeView, previousNodeEditor);
            }
        }
        public void selectModeChanged(TreeView treeView, SelectMode previousSelectMode) {
            for (TreeViewListener listener : this) {
                listener.selectModeChanged(treeView, previousSelectMode);
            }
        }

        public void checkmarksEnabledChanged(TreeView treeView) {
            for (TreeViewListener listener : this) {
                listener.checkmarksEnabledChanged(treeView);
            }
        }

        public void showMixedCheckmarkStateChanged(TreeView treeView) {
            for (TreeViewListener listener : this) {
                listener.showMixedCheckmarkStateChanged(treeView);
            }
        }
    }

    /**
     * Tree view branch listener list.
     *
     * @author tvolkert
     */
    private static class TreeViewBranchListenerList extends ListenerList<TreeViewBranchListener>
        implements TreeViewBranchListener {
        public void branchExpanded(TreeView treeView, Sequence<Integer> path) {
            for (TreeViewBranchListener listener : this) {
                listener.branchExpanded(treeView, path);
            }
        }

        public void branchCollapsed(TreeView treeView, Sequence<Integer> path) {
            for (TreeViewBranchListener listener : this) {
                listener.branchCollapsed(treeView, path);
            }
        }
    }

    /**
     * Tree view node listener list.
     *
     * @author tvolkert
     */
    private static class TreeViewNodeListenerList extends ListenerList<TreeViewNodeListener>
        implements TreeViewNodeListener {
        public void nodeInserted(TreeView treeView, Sequence<Integer> path, int index) {
            for (TreeViewNodeListener listener : this) {
                listener.nodeInserted(treeView, path, index);
            }
        }

        public void nodesRemoved(TreeView treeView, Sequence<Integer> path, int index,
            int count) {
            for (TreeViewNodeListener listener : this) {
                listener.nodesRemoved(treeView, path, index, count);
            }
        }

        public void nodeUpdated(TreeView treeView, Sequence<Integer> path, int index) {
            for (TreeViewNodeListener listener : this) {
                listener.nodeUpdated(treeView, path, index);
            }
        }

        public void nodesSorted(TreeView treeView, Sequence<Integer> path) {
            for (TreeViewNodeListener listener : this) {
                listener.nodesSorted(treeView, path);
            }
        }
    }

    /**
     * Tree view node state listener list.
     *
     * @author tvolkert
     */
    private static class TreeViewNodeStateListenerList
        extends ListenerList<TreeViewNodeStateListener>
        implements TreeViewNodeStateListener {
        public void nodeDisabledChanged(TreeView treeView, Sequence<Integer> path) {
            for (TreeViewNodeStateListener listener : this) {
                listener.nodeDisabledChanged(treeView, path);
            }
        }

        public void nodeCheckStateChanged(TreeView treeView, Sequence<Integer> path,
            TreeView.NodeCheckState previousCheckState) {
            for (TreeViewNodeStateListener listener : this) {
                listener.nodeCheckStateChanged(treeView, path, previousCheckState);
            }
        }
    }

    /**
     * Tree view selection listener list.
     *
     * @author tvolkert
     */
    private static class TreeViewSelectionListenerList
        extends ListenerList<TreeViewSelectionListener>
        implements TreeViewSelectionListener {
        public void selectedPathAdded(TreeView treeView, Sequence<Integer> path) {
            for (TreeViewSelectionListener listener : this) {
                listener.selectedPathAdded(treeView, path);
            }
        }

        public void selectedPathRemoved(TreeView treeView, Sequence<Integer> path) {
            for (TreeViewSelectionListener listener : this) {
                listener.selectedPathRemoved(treeView, path);
            }
        }

        public void selectedPathsChanged(TreeView treeView,
            Sequence<Sequence<Integer>> previousSelectedPaths) {
            for (TreeViewSelectionListener listener : this) {
                listener.selectedPathsChanged(treeView, previousSelectedPaths);
            }
        }
    }

    /**
     * A comparator that sorts paths by the order in which they would visually
     * appear in a fully expanded tree, otherwise known as their "row order".
     *
     * @author tvolkert
     */
    public static final class PathComparator implements Comparator<Sequence<Integer>> {
        public int compare(Sequence<Integer> path1, Sequence<Integer> path2) {
            int path1Length = path1.getLength();
            int path2Length = path2.getLength();

            for (int i = 0, n = Math.min(path1Length, path2Length); i < n; i++) {
                int pathElement1 = path1.get(i);
                int pathElement2 = path2.get(i);

                if (pathElement1 != pathElement2) {
                    return pathElement1 - pathElement2;
                }
            }

            return path1Length - path2Length;
        }
    }

    /**
     * Notifies the tree of nested <tt>ListListener</tt> events that occur on
     * the tree data.
     *
     * @author tvolkert
     */
    private class BranchHandler extends ArrayList<BranchHandler> implements ListListener<Object> {
        private static final long serialVersionUID = -6132480635507615071L;

        // Reference to its parent allows for the construction of its path
        private BranchHandler parent;

        // The backing data structure
        private List<?> branchData;

        /**
         * Creates a new <tt>BranchHandler</tt> tied to the specified parent
         * and listening to events from the specified branch data.
         */
        @SuppressWarnings("unchecked")
        public BranchHandler(BranchHandler parent, List<?> branchData) {
            super(branchData.getLength());

            this.parent = parent;
            this.branchData = branchData;

            ((List<Object>)branchData).getListListeners().add(this);

            // Create placeholder child entries, to be loaded lazily
            for (int i = 0, n = branchData.getLength(); i < n; i++) {
                add(null);
            }
        }

        /**
         * Gets the branch data that this handler is monitoring.
         */
        public List<?> getBranchData() {
            return branchData;
        }

        /**
         * Unregisters this branch handler's interest in ListListener events.
         * This must be done to release references from the tree data to our
         * internal BranchHandler data structures. Failure to do so would mean
         * that our BranchHandler objects would remain in scope as long as the
         * tree data remained in scope, even if  we were no longer using the
         * BranchHandler objects.
         */
        @SuppressWarnings("unchecked")
        public void release() {
            ((List<Object>)branchData).getListListeners().remove(this);

            // Recursively have all child branches unregister interest
            for (int i = 0, n = getLength(); i < n; i++) {
                BranchHandler branchHandler = get(i);

                if (branchHandler != null) {
                    branchHandler.release();
                }
            }
        }

        /**
         * Gets the path that leads from the root of the tree data to this
         * branch. Note: <tt>rootBranchHandler.getPath()</tt> will return and
         * empty sequence.
         */
        @SuppressWarnings("unchecked")
        private Sequence<Integer> getPath() {
            Sequence<Integer> path = new ArrayList<Integer>();

            BranchHandler handler = this;

            while (handler.parent != null) {
                int index = ((List<Object>)handler.parent.branchData).indexOf(handler.branchData);
                path.insert(index, 0);

                handler = handler.parent;
            }

            return path;
        }

        public void itemInserted(List<Object> list, int index) {
            Sequence<Integer> path = getPath();

            // Insert child handler placeholder (lazily loaded)
            insert(null, index);

            // Update our data structures
            incrementPaths(expandedPaths, path, index);
            incrementPaths(selectedPaths, path, index);
            incrementPaths(disabledPaths, path, index);
            incrementPaths(checkedPaths, path, index);

            // Notify listeners
            treeViewNodeListeners.nodeInserted(TreeView.this, path, index);
        }

        public void itemsRemoved(List<Object> list, int index, Sequence<Object> items) {
            Sequence<Integer> path = getPath();

            // Remove child handlers
            int count = (items == null) ? getLength() : items.getLength();
            Sequence<BranchHandler> removed = remove(index, count);

            // Release each child handler that was removed
            for (int i = 0, n = removed.getLength(); i < n; i++) {
                BranchHandler handler = removed.get(i);

                if (handler != null) {
                    handler.release();
                }
            }

            // Update our data structures
            clearAndDecrementPaths(expandedPaths, path, index, count);
            clearAndDecrementPaths(selectedPaths, path, index, count);
            clearAndDecrementPaths(disabledPaths, path, index, count);
            clearAndDecrementPaths(checkedPaths, path, index, count);

            // Notify listeners
            treeViewNodeListeners.nodesRemoved(TreeView.this, getPath(), index,
                (items == null) ? -1 : items.getLength());
        }

        public void itemUpdated(List<Object> list, int index, Object previousItem) {
            Sequence<Integer> path = getPath();

            if (list.get(index) != previousItem) {
                // Release child handler
                BranchHandler handler = update(index, null);

                if (handler != null) {
                    handler.release();
                }

                // Update our data structures
                clearPaths(expandedPaths, path, index);
                clearPaths(selectedPaths, path, index);
                clearPaths(disabledPaths, path, index);
                clearPaths(checkedPaths, path, index);
            }

            // Notify listeners
            treeViewNodeListeners.nodeUpdated(TreeView.this, path, index);
        }

        public void comparatorChanged(List<Object> list,
            Comparator<Object> previousComparator) {
            if (list.getComparator() != null) {
                Sequence<Integer> path = getPath();

                // Release all child handlers. This is safe because of the
                // calls to clearPaths(). Failure to do this would result in
                // the indices of our child handlers not matching those of the
                // backing data structures, which would yield very hard to find
                // bugs
                for (int i = 0, n = getLength(); i < n; i++) {
                    BranchHandler handler = update(i, null);

                    if (handler != null) {
                        handler.release();
                    }
                }

                // Update our data structures
                clearPaths(expandedPaths, path);
                clearPaths(selectedPaths, path);
                clearPaths(disabledPaths, path);
                clearPaths(checkedPaths, path);

                // Notify listeners
                treeViewNodeListeners.nodesSorted(TreeView.this, path);
            }
        }

        /**
         * Updates the paths within the specified sequence in response to a tree
         * data path insertion.  For instance, if <tt>paths</tt> is
         * <tt>[[3, 0], [5, 0]]</tt>, <tt>basePath</tt> is <tt>[]</tt>, and
         * <tt>index</tt> is <tt>4</tt>, then <tt>paths</tt> will be updated to
         * <tt>[[3, 0], [6, 0]]</tt>. No events are fired.
         *
         * @param paths
         * Sequence of paths guaranteed to be sorted by "row order".
         *
         * @param basePath
         * The path to the parent of the inserted item.
         *
         * @param index
         * The index of the inserted item within its parent.
         */
        private void incrementPaths(Sequence<Sequence<Integer>> paths,
            Sequence<Integer> basePath, int index) {
            // Calculate the child's path
            Sequence<Integer> childPath = new ArrayList<Integer>(basePath);
            childPath.add(index);

            // Find the child path's place in our sorted paths sequence
            int i = Sequence.Search.binarySearch(paths, childPath, PATH_COMPARATOR);
            if (i < 0) {
                i = -(i + 1);
            }

            // Update all affected paths by incrementing the appropriate path element
            for (int depth = basePath.getLength(), n = paths.getLength(); i < n; i++) {
                Sequence<Integer> affectedPath = paths.get(i);

                if (!Sequence.Tree.isDescendant(basePath, affectedPath)) {
                    // All paths from here forward are guaranteed to be unaffected
                    break;
                }

                affectedPath.update(depth, affectedPath.get(depth) + 1);
            }
        }

        /**
         * Updates the paths within the specified sequence in response to items
         * having been removed from the base path. For instance, if
         * <tt>paths</tt> is <tt>[[3, 0], [3, 1], [6, 0]]</tt>,
         * <tt>basePath</tt> is <tt>[]</tt>, <tt>index</tt> is <tt>3</tt>, and
         * <tt>count</tt> is <tt>2</tt>, then <tt>paths</tt> will be updated to
         * <tt>[[4, 0]]</tt>. No events are fired.
         *
         * @param paths
         * Sequence of paths guaranteed to be sorted by "row order".
         *
         * @param basePath
         * The path to the parent of the removed items.
         *
         * @param index
         * The index of the first removed item within the base.
         *
         * @param count
         * The number of items removed.
         */
        private void clearAndDecrementPaths(Sequence<Sequence<Integer>> paths,
            Sequence<Integer> basePath, int index, int count) {
            int depth = basePath.getLength();

            // Find the index of the first path to clear (inclusive)
            Sequence<Integer> testPath = new ArrayList<Integer>(basePath);
            testPath.add(index);

            int start = Sequence.Search.binarySearch(paths, testPath, PATH_COMPARATOR);
            if (start < 0) {
                start = -(start + 1);
            }

            // Find the index of the last path to clear (exclusive)
            testPath.update(depth, index + count);

            int end = Sequence.Search.binarySearch(paths, testPath, PATH_COMPARATOR);
            if (end < 0) {
                end = -(end + 1);
            }

            // Clear affected paths
            if (end > start) {
                paths.remove(start, end - start);
            }

            // Decrement paths as necessary
            for (int i = start, n = paths.getLength(); i < n; i++) {
                Sequence<Integer> affectedPath = paths.get(i);

                if (!Sequence.Tree.isDescendant(basePath, affectedPath)) {
                    // All paths from here forward are guaranteed to be unaffected
                    break;
                }

                affectedPath.update(depth, affectedPath.get(depth) - count);
            }
        }

        /**
         * Removes affected paths from within the specified sequence in response
         * to an item having been updated in the base path.  For instance, if
         * <tt>paths</tt> is <tt>[[3], [3, 0], [3, 1], [5, 0]]</tt>,
         * <tt>basePath</tt> is <tt>[3]</tt>, and <tt>index</tt> is <tt>0</tt>,
         * then <tt>paths</tt> will be updated to
         * <tt>[[3], [3, 1], [5, 0]]</tt>. No events are fired.
         *
         * @param paths
         * Sequence of paths guaranteed to be sorted by "row order".
         *
         * @param basePath
         * The path to the parent of the updated item.
         *
         * @param index
         * The index of the updated item within its parent.
         */
        private void clearPaths(Sequence<Sequence<Integer>> paths,
            Sequence<Integer> basePath, int index) {
            // Calculate the child's path
            Sequence<Integer> childPath = new ArrayList<Integer>(basePath);
            childPath.add(index);

            // Find the child path's place in our sorted paths sequence
            int clearIndex = Sequence.Search.binarySearch(paths, childPath, PATH_COMPARATOR);
            if (clearIndex < 0) {
                clearIndex = -(clearIndex + 1);
            }

            // Remove the child and all descendants from the paths list
            for (int i = clearIndex, n = paths.getLength(); i < n; i++) {
                Sequence<Integer> affectedPath = paths.get(clearIndex);

                if (!Sequence.Tree.isDescendant(childPath, affectedPath)) {
                    break;
                }

                paths.remove(clearIndex, 1);
            }
        }

        /**
         * Removes affected paths from within the specified sequence in response
         * to a base path having been sorted.  For instance, if <tt>paths</tt>
         * is <tt>[[3], [3, 0], [3, 1], [5, 0]]</tt> and <tt>basePath</tt> is
         * <tt>[3]</tt>, then <tt>paths</tt> will be updated to
         * <tt>[[3], [5, 0]]</tt>. No events are fired.
         *
         * @param paths
         * Sequence of paths guaranteed to be sorted by "row order".
         *
         * @param basePath
         * The path whose children were sorted.
         */
        private void clearPaths(Sequence<Sequence<Integer>> paths, Sequence<Integer> basePath) {
            // Find first descendant in paths list, if it exists
            int index = Sequence.Search.binarySearch(paths, basePath, PATH_COMPARATOR);
            index = (index < 0 ? -(index + 1) : index + 1);

            // Remove all descendants from the paths list
            for (int i = index, n = paths.getLength(); i < n; i++) {
                Sequence<Integer> affectedPath = paths.get(index);

                if (!Sequence.Tree.isDescendant(basePath, affectedPath)) {
                    break;
                }

                paths.remove(index, 1);
            }
        }
    }

    // Core data model
    private List<?> treeData = null;

    // Ancillary data models
    private ArrayList<Sequence<Integer>> expandedPaths =
        new ArrayList<Sequence<Integer>>(PATH_COMPARATOR);
    private ArrayList<Sequence<Integer>> selectedPaths =
        new ArrayList<Sequence<Integer>>(PATH_COMPARATOR);
    private ArrayList<Sequence<Integer>> disabledPaths =
        new ArrayList<Sequence<Integer>>(PATH_COMPARATOR);
    private ArrayList<Sequence<Integer>> checkedPaths =
        new ArrayList<Sequence<Integer>>(PATH_COMPARATOR);

    // Properties
    private SelectMode selectMode = SelectMode.SINGLE;
    private boolean checkmarksEnabled = false;
    private boolean showMixedCheckmarkState = false;

    // Handlers
    private BranchHandler rootBranchHandler;

    // Renderer & editor
    private NodeRenderer nodeRenderer = DEFAULT_NODE_RENDERER;
    private NodeEditor nodeEditor = null;

    // Listener lists
    private TreeViewListenerList treeViewListeners = new TreeViewListenerList();
    private TreeViewBranchListenerList treeViewBranchListeners =
        new TreeViewBranchListenerList();
    private TreeViewNodeListenerList treeViewNodeListeners =
        new TreeViewNodeListenerList();
    private TreeViewNodeStateListenerList treeViewNodeStateListeners =
        new TreeViewNodeStateListenerList();
    private TreeViewSelectionListenerList treeViewSelectionListeners =
        new TreeViewSelectionListenerList();

    private static final NodeRenderer DEFAULT_NODE_RENDERER = new TreeViewNodeRenderer();

    private static final Comparator<Sequence<Integer>> PATH_COMPARATOR =
        new PathComparator();

    /**
     * Creates a new <tt>TreeView</tt> with empty tree data.
     */
    public TreeView() {
        this(new ArrayList<Object>());
    }

    /**
     * Creates a new <tt>TreeView</tt> with the specified tree data.
     *
     * @param treeData
     * Default data set to be used with the tree. This list represents the root
     * set of items displayed by the tree and will never itself be painted.
     * Sub-items that also implement the <tt>List</tt> interface are considered
     * branches; other items are considered leaves.
     *
     * @see #setTreeData(List)
     */
    public TreeView(List<?> treeData) {
        setTreeData(treeData);
        installSkin(TreeView.class);
    }

    /**
     * Sets the skin, replacing any previous skin. This ensures that the skin
     * being set implements the {@link TreeView.Skin} interface.
     *
     * @param skin
     * The new skin.
     */
    @Override
    protected void setSkin(pivot.wtk.Skin skin) {
        if (!(skin instanceof TreeView.Skin)) {
            throw new IllegalArgumentException("Skin class must implement "
                + TreeView.Skin.class.getName());
        }

        super.setSkin(skin);
    }

    /**
     * Returns the tree view's data model. This list represents the root
     * set of items displayed by the tree and will never itself be painted.
     * Sub-items that also implement the <tt>List</tt> interface are considered
     * branches; other items are considered leaves.
     * <p>
     * For instance, a tree view that displays a single root branch would be
     * backed by list with one child (also a list).
     *
     * @return
     * The tree view's data model.
     */
    public List<?> getTreeData() {
        return treeData;
    }

    /**
     * Sets the tree data. Note that it is the responsibility of the
     * caller to ensure that the current tree node renderer is capable of
     * displaying the contents of the tree structure. By default, an instance
     * of {@link TreeViewNodeRenderer} is used.
     * <p>
     * When the tree data is changed, the state of all nodes (expansion,
     * selection, disabled, and checked) will be cleared since the nodes
     * themselves are being replaced. Note that corresponding events will
     * <b>not</b> be fired, since these actions are implied by the
     * {@link TreeViewListener#treeDataChanged(TreeView,List) treeDataChanged}
     * event.
     *
     * @param treeData
     * The data to be presented by the tree.
     */
    public void setTreeData(List<?> treeData) {
        if (treeData == null) {
            throw new IllegalArgumentException("treeData is null.");
        }

        List<?> previousTreeData = this.treeData;

        if (previousTreeData != treeData) {
            if (previousTreeData != null) {
                // Reset our data models
                expandedPaths.clear();
                selectedPaths.clear();
                disabledPaths.clear();
                checkedPaths.clear();

                // Release our existing branch handlers
                rootBranchHandler.release();
            }

            // Update our root branch handler
            rootBranchHandler = new BranchHandler(null, treeData);

            // Update the tree data
            this.treeData = treeData;

            // Notify listeners
            treeViewListeners.treeDataChanged(this, previousTreeData);
        }
    }

    /**
     * Gets the tree view's node renderer, which is responsible for the
     * appearance of the node data. As such, note that there is an implied
     * coordination between the node renderer and the data model. The default
     * node renderer used is an instance of <tt>TreeViewNodeRenderer</tt>.
     *
     * @return
     * The current node renderer.
     *
     * @see TreeViewNodeRenderer
     */
    public NodeRenderer getNodeRenderer() {
        return nodeRenderer;
    }

    /**
     * Sets the tree view's node renderer, which is responsible for the
     * appearance of the node data.
     *
     * @param nodeRenderer
     * The new node renderer.
     */
    public void setNodeRenderer(NodeRenderer nodeRenderer) {
        if (nodeRenderer == null) {
            throw new IllegalArgumentException("nodeRenderer is null.");
        }

        NodeRenderer previousNodeRenderer = this.nodeRenderer;

        if (previousNodeRenderer != nodeRenderer) {
            this.nodeRenderer = nodeRenderer;

            treeViewListeners.nodeRendererChanged(this, previousNodeRenderer);
        }
    }

    /**
     * Returns the editor used to edit nodes in this tree.
     *
     * @return
     * The node editor, or <tt>null</tt> if no editor is installed.
     */
    public NodeEditor getNodeEditor() {
        return nodeEditor;
    }

    /**
     * Sets the editor used to edit nodes in this tree.
     *
     * @param nodeEditor
     * The node editor for the tree.
     */
    public void setNodeEditor(NodeEditor nodeEditor) {
        NodeEditor previousNodeEditor = this.nodeEditor;

        if (previousNodeEditor != nodeEditor) {
            this.nodeEditor = nodeEditor;
            treeViewListeners.nodeEditorChanged(this, previousNodeEditor);
        }
    }

    /**
     * Returns the current selection mode.
     *
     * @return
     * The current selection mode.
     */
    public SelectMode getSelectMode() {
        return selectMode;
    }

    /**
     * Sets the selection mode. Clears the selection if the mode has changed.
     * Note that if the selection is cleared, selection listeners will not
     * be notified, as the clearing of the selection is implied by the
     * {@link TreeViewListener#selectModeChanged(TreeView,TreeView.SelectMode)
     * selectModeChanged} event.
     *
     * @param selectMode
     * The new selection mode.
     *
     * @see
     * TreeViewListener
     *
     * @see
     * TreeViewSelectionListener
     */
    public void setSelectMode(SelectMode selectMode) {
        if (selectMode == null) {
            throw new IllegalArgumentException("selectMode is null");
        }

        SelectMode previousSelectMode = this.selectMode;

        if (selectMode != previousSelectMode) {
            // Clear any current selection
            selectedPaths.clear();

            // Update the selection mode
            this.selectMode = selectMode;

            // Fire select mode change event
            treeViewListeners.selectModeChanged(this, previousSelectMode);
        }
    }

    /**
     * Sets the selection mode.
     *
     * @param selectMode
     * The new selection mode.
     *
     * @see
     * #setSelectMode(SelectMode)
     */
    public final void setSelectMode(String selectMode) {
        if (selectMode == null) {
            throw new IllegalArgumentException("selectMode is null.");
        }

        setSelectMode(SelectMode.decode(selectMode));
    }

    /**
     *
     */
    public Sequence<Sequence<Integer>> getSelectedPaths() {
        int count = selectedPaths.getLength();

        Sequence<Sequence<Integer>> selectedPaths = new ArrayList<Sequence<Integer>>(count);

        // Deep copy the selected paths into a new list
        for (int i = 0; i < count; i++) {
            selectedPaths.add(new ArrayList<Integer>(this.selectedPaths.get(i)));
        }

        return selectedPaths;
    }

    /**
     *
     *
     * @throws IllegalStateException
     * If selection has been disabled (select mode <tt>NONE</tt>).
     */
    public void setSelectedPaths(Sequence<Sequence<Integer>> selectedPaths) {
        if (selectedPaths == null) {
            throw new IllegalArgumentException("selectedPaths is null.");
        }

        if (selectMode == SelectMode.NONE) {
            throw new IllegalStateException("Selection is not enabled.");
        }

        if (selectMode == SelectMode.SINGLE
            && selectedPaths.getLength() > 1) {
            throw new IllegalArgumentException("Selection length is greater than 1.");
        }

        Sequence<Sequence<Integer>> previousSelectedPaths = this.selectedPaths;

        if (selectedPaths != previousSelectedPaths) {
            this.selectedPaths = new ArrayList<Sequence<Integer>>(PATH_COMPARATOR);

            for (int i = 0, n = selectedPaths.getLength(); i < n; i++) {
                Sequence<Integer> path = selectedPaths.get(i);

                // Monitor the path's parent
                monitorBranch(new ArrayList<Integer>(path, 0, path.getLength() - 1));

                // Update the selection
                this.selectedPaths.add(new ArrayList<Integer>(path));
            }

            // Notify listeners
            treeViewSelectionListeners.selectedPathsChanged(this, previousSelectedPaths);
        }
    }

    /**
     * Returns the first selected path, as it would appear in a fully expanded
     * tree.
     *
     * @return
     * The first selected path, or <tt>null</tt> if nothing is selected.
     */
    public Sequence<Integer> getFirstSelectedPath() {
        Sequence<Integer> selectedPath = null;

        if (selectedPaths.getLength() > 0) {
            selectedPath = new ArrayList<Integer>(selectedPaths.get(0));
        }

        return selectedPath;
    }

    /**
     * Returns the last selected path, as it would appear in a fully expanded
     * tree.
     *
     * @return
     * The last selected path, or <tt>null</tt> if nothing is selected.
     */
    public Sequence<Integer> getLastSelectedPath() {
        Sequence<Integer> selectedPath = null;

        if (selectedPaths.getLength() > 0) {
            selectedPath = new ArrayList<Integer>
                (selectedPaths.get(selectedPaths.getLength() - 1));
        }

        return selectedPath;
    }

    /**
     *
     *
     * @return
     * The selected path, or <tt>null</tt> if nothing is selected.
     *
     * @throws IllegalStateException
     * If the tree view is not in single-select mode.
     */
    public Sequence<Integer> getSelectedPath() {
        if (selectMode != SelectMode.SINGLE) {
            throw new IllegalStateException("Tree view is not in single-select mode.");
        }

        Sequence<Integer> selectedPath = null;

        if (selectedPaths.getLength() > 0) {
            selectedPath = new ArrayList<Integer>(selectedPaths.get(0));
        }

        return selectedPath;
    }

    /**
     *
     */
    public void setSelectedPath(Sequence<Integer> path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        Sequence<Sequence<Integer>> selectedPaths = new ArrayList<Sequence<Integer>>(1);
        selectedPaths.add(new ArrayList<Integer>(path));

        setSelectedPaths(selectedPaths);
    }

    /**
     *
     *
     * @return
     * The selected object, or <tt>null</tt> if nothing is selected. Note that
     * technically, the selected path could be backed by a <tt>null</tt> data
     * value. If the caller wishes to distinguish between these cases, they can
     * use <tt>getSelectedPath()</tt> instead.
     */
    public Object getSelectedNode() {
        Sequence<Integer> path = getSelectedPath();
        Object node = null;

        if (path != null) {
            node = Sequence.Tree.get(treeData, path);
        }

        return node;
    }

    /**
     *
     *
     * @throws IllegalStateException
     * If multi-select is not enabled.
     */
    public void addSelectedPath(Sequence<Integer> path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        if (selectMode != SelectMode.MULTI) {
            throw new IllegalStateException("Tree view is not in multi-select mode.");
        }

        if (selectedPaths.indexOf(path) < 0) {
            // Monitor the path's parent
            monitorBranch(new ArrayList<Integer>(path, 0, path.getLength() - 1));

            // Update the selection
            selectedPaths.add(new ArrayList<Integer>(path));

            // Notify listeners
            treeViewSelectionListeners.selectedPathAdded(this, path);
        }
    }

    /**
     *
     *
     * @throws IllegalStateException
     * If multi-select is not enabled.
     */
    public void removeSelectedPath(Sequence<Integer> path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        if (selectMode != SelectMode.MULTI) {
            throw new IllegalStateException("Tree view is not in multi-select mode.");
        }

        int index = selectedPaths.indexOf(path);

        if (index >= 0) {
            // Update the selection
            selectedPaths.remove(index, 1);

            // Notify listeners
            treeViewSelectionListeners.selectedPathRemoved(this, path);
        }
    }

    /**
     *
     */
    public void clearSelection() {
        if (selectedPaths.getLength() > 0) {
            Sequence<Sequence<Integer>> previousSelectedPaths = selectedPaths;

            // Update the selection
            selectedPaths = new ArrayList<Sequence<Integer>>(PATH_COMPARATOR);

            // Notify listeners
            treeViewSelectionListeners.selectedPathsChanged(this, previousSelectedPaths);
        }
    }

    /**
     *
     */
    public boolean isNodeSelected(Sequence<Integer> path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        return (selectedPaths.indexOf(path) >= 0);
    }

    /**
     * Returns the disabled state of a given node.
     *
     * @param path
     * The path to the node whose disabled state is to be tested
     *
     * @return
     * <tt>true</tt> if the node is disabled; <tt>false</tt>,
     * otherwise
     */
    public boolean isNodeDisabled(Sequence<Integer> path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        return (disabledPaths.indexOf(path) >= 0);
    }

    /**
     * Sets the disabled state of a node. A disabled node is not interactive to
     * the user. Note, however, that disabled nodes may still be expanded,
     * selected, and checked <i>programatically</i>. Disabling a node does
     * <b>not</b> disable its children.
     *
     * @param path
     * The path to the node.
     *
     * @param disabled
     * <tt>true</tt> to disable the node; <tt>false</tt> to enable it.
     */
    public void setNodeDisabled(Sequence<Integer> path, boolean disabled) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        int index = disabledPaths.indexOf(path);

        if ((index < 0 && disabled)
            || (index >= 0 && !disabled)) {
            if (disabled) {
                // Monitor the path's parent
                monitorBranch(new ArrayList<Integer>(path, 0, path.getLength() - 1));

                // Update the disabled paths
                disabledPaths.add(new ArrayList<Integer>(path));
            } else {
                // Update the disabled paths
                disabledPaths.remove(index, 1);
            }

            // Notify listeners
            treeViewNodeStateListeners.nodeDisabledChanged(this, path);
        }
    }

    /**
     *
     */
    public Sequence<Sequence<Integer>> getDisabledPaths() {
        int count = disabledPaths.getLength();

        Sequence<Sequence<Integer>> disabledPaths = new ArrayList<Sequence<Integer>>(count);

        // Deep copy the disabled paths into a new list
        for (int i = 0; i < count; i++) {
            disabledPaths.add(new ArrayList<Integer>(this.disabledPaths.get(i)));
        }

        return disabledPaths;
    }

    /**
     *
     */
    public boolean getCheckmarksEnabled() {
        return checkmarksEnabled;
    }

    /**
     * Enables or disables checkmarks. If checkmarks are being disabled, all
     * checked nodes will be automatically unchecked. Note that the
     * corresponding event will <b>not</b> be fired, since the clearing of
     * existing checkmarks is implied by the
     * {@link TreeViewListener#checkmarksEnabledChanged(TreeView)
     * checkmarksEnabledChanged} event.
     *
     * @param checkmarksEnabled
     * <tt>true</tt> to enable checkmarks; <tt>false</tt> to disable them.
     */
    public void setCheckmarksEnabled(boolean checkmarksEnabled) {
        if (this.checkmarksEnabled != checkmarksEnabled) {
            // Clear any current check state
            checkedPaths.clear();

            // Update the checkmark mode
            this.checkmarksEnabled = checkmarksEnabled;

            // Fire checkmarks enabled change event
            treeViewListeners.checkmarksEnabledChanged(this);
        }
    }

    /**
     * Tells whether or not the mixed check state will be reported by this
     * tree view. This state is a derived state meaning "the node is not
     * checked, but one or more of its descendants are." When this state is
     * configured to not be shown, such nodes will simply be reported as
     * unchecked.
     *
     * @return
     * <tt>true</tt> if the tree view will report so-called mixed nodes as
     * mixed; <tt>false</tt> if it will report them as unchecked.
     *
     * @see
     * NodeCheckState#MIXED
     */
    public boolean getShowMixedCheckmarkState() {
        return showMixedCheckmarkState;
    }

    /**
     * Sets whether or not the "mixed" check state will be reported by this
     * tree view. This state is a derived state meaning "the node is not
     * checked, but one or more of its descendants are." When this state is
     * configured to not be shown, such nodes will simply be reported as
     * unchecked.
     * <p>
     * Changing this flag may result in some nodes changing their reported
     * check state. Note that the corresponding <tt>nodeCheckStateChanged</tt>
     * events will <b>not</b> be fired, since the possibility of such a change
     * in check state is implied by the
     * {@link TreeViewListener#showMixedCheckmarkStateChanged(TreeView)
     * showMixedCheckmarkStateChanged} event.
     *
     * @param showMixedCheckmarkState
     * <tt>true</tt> to show the derived mixed state; <tt>false</tt> to report
     * so-called "mixed" nodes as unchecked.
     *
     * @see
     * NodeCheckState#MIXED
     */
    public void setShowMixedCheckmarkState(boolean showMixedCheckmarkState) {
        if (this.showMixedCheckmarkState != showMixedCheckmarkState) {
            // Update the flag
            this.showMixedCheckmarkState = showMixedCheckmarkState;

            // Notify listeners
            treeViewListeners.showMixedCheckmarkStateChanged(this);
        }
    }

    /**
     * Tells whether or not the node at the specified path is checked. If
     * checkmarks are not enabled, this is guaranteed to be <tt>false</tt>. So
     * called mixed nodes will always be reported as unchecked in this method.
     *
     * @param path
     * The path to the node.
     *
     * @return
     * <tt>true</tt> if the node is explicitly checked; <tt>false</tt> otherwise.
     *
     * @see
     * #getCheckmarksEnabled()
     */
    public boolean isNodeChecked(Sequence<Integer> path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        return (checkedPaths.indexOf(path) >= 0);
    }

    /**
     * Returns the checkmark state of the node at the specified path. If
     * checkmarks are not enabled, this is guaranteed to be <tt>UNCHECKED</tt>.
     * <p>
     * Note that the <tt>MIXED</tt> check state (meaning "the node is not
     * checked, but one or more of its descendants are") is only reported when
     * the tree view is configured as such. Otherwise, such nodes will be
     * reported as <tt>UNCHECKED</tt>.
     *
     * @param path
     * The path to the node.
     *
     * @return
     * The checkmark state of the specified node.
     *
     * @see
     * #getCheckmarksEnabled()
     *
     * @see
     * #setShowMixedCheckmarkState(boolean)
     */
    public NodeCheckState getNodeCheckState(Sequence<Integer> path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        NodeCheckState checkState = NodeCheckState.UNCHECKED;

        if (checkmarksEnabled) {
            int index = Sequence.Search.binarySearch(checkedPaths, path, PATH_COMPARATOR);

            if (index >= 0) {
                checkState = NodeCheckState.CHECKED;
            } else if (showMixedCheckmarkState) {
                // Translate to the insertion index
                index = -(index + 1);

                if (index < checkedPaths.getLength()) {
                    Sequence<Integer> nextCheckedPath = checkedPaths.get(index);

                    if (Sequence.Tree.isDescendant(path, nextCheckedPath)) {
                        checkState = NodeCheckState.MIXED;
                    }
                }
            }
        }

        return checkState;
    }

    /**
     * Sets the check state of the node at the specified path. If the node
     * already has the specified check state, nothing happens.
     * <p>
     * Note that it is impossible to set the check state of a node to
     * <tt>MIXED</tt>. This is because the mixed check state is a derived state
     * meaning "the node is not checked, but one or more of its descendants
     * are."
     *
     * @param path
     * The path to the node.
     *
     * @param checked
     * <tt>true</tt> to check the node; <tt>false</tt> to uncheck it.
     *
     * @throws IllegalStateException
     * If checkmarks are not enabled (see {@link #getCheckmarksEnabled()}).
     *
     * @see
     * NodeCheckState#MIXED
     */
    public void setNodeChecked(Sequence<Integer> path, boolean checked) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        if (!checkmarksEnabled) {
            throw new IllegalStateException("Checkmarks are not enabled.");
        }

        int index = checkedPaths.indexOf(path);

        if ((index < 0 && checked)
            || (index >= 0 && !checked)) {
            NodeCheckState previousCheckState = getNodeCheckState(path);

            Sequence<NodeCheckState> ancestorCheckStates = null;

            if (showMixedCheckmarkState) {
                // Record the check states of our ancestors before we change
                // anything so we know which events to fire after we're done
                ancestorCheckStates = new ArrayList<NodeCheckState>(path.getLength() - 1);

                Sequence<Integer> ancestorPath = new ArrayList<Integer>
                    (path, 0, path.getLength() - 1);

                for (int i = ancestorPath.getLength() - 1; i >= 0; i--) {
                    ancestorCheckStates.insert(getNodeCheckState(ancestorPath), 0);

                    ancestorPath.remove(i, 1);
                }
            }

            if (checked) {
                // Monitor the path's parent
                monitorBranch(new ArrayList<Integer>(path, 0, path.getLength() - 1));

                // Update the checked paths
                checkedPaths.add(new ArrayList<Integer>(path));
            } else {
                // Update the checked paths
                checkedPaths.remove(index, 1);
            }

            // Notify listeners
            treeViewNodeStateListeners.nodeCheckStateChanged(this, path, previousCheckState);

            if (showMixedCheckmarkState) {
                // Notify listeners of any changes to our ancestors' check states
                Sequence<Integer> ancestorPath = new ArrayList<Integer>
                    (path, 0, path.getLength() - 1);

                for (int i = ancestorPath.getLength() - 1; i >= 0; i--) {
                    NodeCheckState ancestorPreviousCheckState = ancestorCheckStates.get(i);
                    NodeCheckState ancestorCheckState = getNodeCheckState(ancestorPath);

                    if (ancestorCheckState != ancestorPreviousCheckState) {
                        treeViewNodeStateListeners.nodeCheckStateChanged
                            (this, ancestorPath, ancestorPreviousCheckState);
                    }

                    ancestorPath.remove(i, 1);
                }
            }
        }
    }

    /**
     * Gets the sequence of node paths that are checked. If checkmarks are not
     * enabled (see {@link #getCheckmarksEnabled()}), this is guaranteed to
     * return an empty sequence.
     * <p>
     * Note that if the tree view is configured to show mixed checkmark states
     * (see {@link #getShowMixedCheckmarkState()}), this will still only return
     * the nodes that are fully checked.
     *
     * @return
     * The paths to the checked nodes in the tree, guaranteed to be
     * non-<tt>null</tt>.
     */
    public Sequence<Sequence<Integer>> getCheckedPaths() {
        int count = checkedPaths.getLength();

        Sequence<Sequence<Integer>> checkedPaths = new ArrayList<Sequence<Integer>>(count);

        // Deep copy the checked paths into a new list
        for (int i = 0; i < count; i++) {
            checkedPaths.add(new ArrayList<Integer>(this.checkedPaths.get(i)));
        }

        return checkedPaths;
    }

    /**
     * Sets the expansion state of the specified branch. If the branch already
     * has the specified expansion state, nothing happens.
     *
     * @param path
     * The path to the branch node.
     *
     * @param expanded
     * <tt>true</tt> to expand the branch; <tt>false</tt> to collapse it.
     */
    public void setBranchExpanded(Sequence<Integer> path, boolean expanded) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        int index = expandedPaths.indexOf(path);

        if (expanded && index < 0) {
            // Monitor the branch
            monitorBranch(path);

            // Update the expanded paths
            expandedPaths.add(new ArrayList<Integer>(path));

            // Notify listeners
            treeViewBranchListeners.branchExpanded(this, path);
        } else if (!expanded && index >= 0) {
            // Update the expanded paths
            expandedPaths.remove(index, 1);

            // Notify listeners
            treeViewBranchListeners.branchCollapsed(this, path);
        }
    }

    /**
     * Tells whether or not the specified branch is expanded.
     *
     * @param path
     * The path to the branch node.
     *
     * @return
     * <tt>true</tt> if the branch is expanded; <tt>false</tt> otherwise.
     */
    public boolean isBranchExpanded(Sequence<Integer> path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null.");
        }

        return (expandedPaths.indexOf(path) >= 0);
    }

    /**
     * Expands the branch at the specified path. If the branch is already
     * expanded, nothing happens.
     *
     * @param path
     * The path to the branch node.
     */
    public final void expandBranch(Sequence<Integer> path) {
        setBranchExpanded(path, true);
    }

    /**
     * Collapses the branch at the specified path. If the branch is already
     * collapsed, nothing happens.
     *
     * @param path
     * The path to the branch node.
     */
    public final void collapseBranch(Sequence<Integer> path) {
        setBranchExpanded(path, false);
    }

    /**
     * Ensures that this tree view is listening for list events on every branch
     * node along the specified path.
     *
     * @param path
     * A path leading to a nested branch node.
     *
     * @throws IndexOutOfBoundsException
     * If a path element is out of bounds.
     *
     * @throws IllegalArgumentException
     * If the path contains any leaf nodes.
     */
    @SuppressWarnings("unchecked")
    private void monitorBranch(Sequence<Integer> path) {
        BranchHandler parent = rootBranchHandler;

        for (int i = 0, n = path.getLength(); i < n; i++) {
            int index = path.get(i);
            if (index < 0
                || index >= parent.getLength()) {
                throw new IndexOutOfBoundsException
                    ("Branch path out of bounds: " + path);
            }

            BranchHandler child = parent.get(index);

            if (child == null) {
                List<?> parentBranchData = parent.getBranchData();
                Object childData = parentBranchData.get(index);

                if (!(childData instanceof List)) {
                    throw new IllegalArgumentException
                        ("Unexpected leaf in branch path: " + path);
                }

                child = new BranchHandler(parent, (List<?>)childData);
                parent.update(index, child);
            }

            parent = child;
        }
    }

    /**
     * Gets the path to the node found at the specified y-coordinate
     * (relative to the tree view).
     *
     * @param y
     * The y-coordinate in pixels.
     *
     * @return
     * The path to the node, or <tt>null</tt> if there is no node being
     * painted at the specified y-coordinate.
     */
    public Sequence<Integer> getNodeAt(int y) {
        TreeView.Skin treeViewSkin = (TreeView.Skin)getSkin();
        return treeViewSkin.getNodeAt(y);
    }

    /**
     * Gets the bounds of the node at the specified path relative to the
     * tree view. Note that all nodes are left aligned with the tree; to
     * get the pixel value of a node's indent, use
     * {@link #getNodeIndent(int)}.
     *
     * @param path
     * The path to the node.
     *
     * @return
     * The bounds, or <tt>null</tt> if the node is not currently visible.
     */
    public Bounds getNodeBounds(Sequence<Integer> path) {
        TreeView.Skin treeViewSkin = (TreeView.Skin)getSkin();
        return treeViewSkin.getNodeBounds(path);
    }

    /**
     * Gets the pixel indent of nodes at the specified depth. Depth is measured
     * in generations away from the tree view's "root" node, which is
     * represented by the {@link #getTreeData() tree data}.
     *
     * @param depth
     * The depth, where the first child of the root has depth 1, the child of
     * that branch has depth 2, etc.
     *
     * @return
     * The indent in pixels.
     */
    public int getNodeIndent(int depth) {
        TreeView.Skin treeViewSkin = (TreeView.Skin)getSkin();
        return treeViewSkin.getNodeIndent(depth);
    }

    /**
     * Gets the <tt>TreeViewListener</tt>s. Developers interested in these
     * events can register for notification on these events by adding
     * themselves to the listener list.
     *
     * @return
     * The tree view listeners.
     */
    public ListenerList<TreeViewListener> getTreeViewListeners() {
        return treeViewListeners;
    }

    /**
     * Gets the <tt>TreeViewBranchListener</tt>s. Developers interested in
     * these events can register for notification on these events by adding
     * themselves to the listener list.
     *
     * @return
     * The tree view branch listeners.
     */
    public ListenerList<TreeViewBranchListener> getTreeViewBranchListeners() {
        return treeViewBranchListeners;
    }

    /**
     * Gets the <tt>TreeViewNodeListener</tt>s. Developers interested in these
     * events can register for notification on these events by adding
     * themselves to the listener list.
     *
     * @return
     * The tree view node listeners.
     */
    public ListenerList<TreeViewNodeListener> getTreeViewNodeListeners() {
        return treeViewNodeListeners;
    }

    /**
     * Gets the <tt>TreeViewNodeStateListener</tt>s. Developers interested in
     * these events can register for notification on these events by adding
     * themselves to the listener list.
     *
     * @return
     * The tree view node state listeners.
     */
    public ListenerList<TreeViewNodeStateListener> getTreeViewNodeStateListeners() {
        return treeViewNodeStateListeners;
    }

    /**
     * Gets the <tt>TreeViewSelectionListener</tt>s. Developers interested in
     * these events can register for notification on these events by adding
     * themselves to the listener list.
     *
     * @return
     * The tree view selection listeners.
     */
    public ListenerList<TreeViewSelectionListener> getTreeViewSelectionListeners() {
        return treeViewSelectionListeners;
    }
}