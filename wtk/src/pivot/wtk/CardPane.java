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

import pivot.collections.Sequence;
import pivot.util.ListenerList;
import pivot.util.Vote;

/**
 * Container that behaves like a deck of cards, only one of which may be
 * visible at a time.
 *
 * @author gbrown
 */
public class CardPane extends Container {
    private static class CardPaneListenerList extends ListenerList<CardPaneListener>
        implements CardPaneListener {
        public void orientationChanged(CardPane cardPane, Orientation previousOrientation) {
            for (CardPaneListener listener : this) {
                listener.orientationChanged(cardPane, previousOrientation);
            }
        }

    	public Vote previewSelectedIndexChange(CardPane cardPane, int selectedIndex) {
            Vote vote = Vote.APPROVE;

            for (CardPaneListener listener : this) {
                vote = vote.tally(listener.previewSelectedIndexChange(cardPane, selectedIndex));
            }

            return vote;
    	}

    	public void selectedIndexChangeVetoed(CardPane cardPane, Vote reason) {
            for (CardPaneListener listener : this) {
                listener.selectedIndexChangeVetoed(cardPane, reason);
            }
    	}

        public void selectedIndexChanged(CardPane cardPane, int previousSelectedIndex) {
            for (CardPaneListener listener : this) {
                listener.selectedIndexChanged(cardPane, previousSelectedIndex);
            }
        }
    }

    private Orientation orientation = null;
    private int selectedIndex = -1;
    private CardPaneListenerList cardPaneListeners = new CardPaneListenerList();

    public CardPane() {
        installSkin(CardPane.class);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        Orientation previousOrientation = this.orientation;

        if (previousOrientation != orientation) {
            this.orientation = orientation;
            cardPaneListeners.orientationChanged(this, previousOrientation);
        }
    }

    public final void setOrientation(String orientation) {
        if (orientation == null) {
            throw new IllegalArgumentException("orientation is null.");
        }

        setOrientation(Orientation.decode(orientation));
    }

    /**
     * Returns the currently selected card index.
     *
     * @return
     * The selected card index, or <tt>-1</tt> if no card is selected.
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Sets the selected card index.
     *
     * @param selectedIndex
     * The selected card index, or <tt>-1</tt> for no selection.
     */
    public void setSelectedIndex(int selectedIndex) {
        if (selectedIndex < -1
            || selectedIndex > getLength() - 1) {
            throw new IndexOutOfBoundsException();
        }

        int previousSelectedIndex = this.selectedIndex;

        if (previousSelectedIndex != selectedIndex) {
        	Vote vote = cardPaneListeners.previewSelectedIndexChange(this, selectedIndex);

        	if (vote == Vote.APPROVE) {
                this.selectedIndex = selectedIndex;
                cardPaneListeners.selectedIndexChanged(this, previousSelectedIndex);
        	} else {
        		cardPaneListeners.selectedIndexChangeVetoed(this, vote);
        	}
        }
    }

    public Component getSelectedCard() {
    	return (selectedIndex == -1) ? null : get(selectedIndex);
    }

    @Override
    public void insert(Component component, int index) {
        // Update the selection
        if (selectedIndex >= index) {
            selectedIndex++;
        }

        super.insert(component, index);
    }

    @Override
    public Sequence<Component> remove(int index, int count) {
        // Update the selection
        if (selectedIndex >= index) {
            if (selectedIndex < index + count) {
                selectedIndex = -1;
            } else {
                selectedIndex -= count;
            }
        }

        return super.remove(index, count);
    }

    public ListenerList<CardPaneListener> getCardPaneListeners() {
        return cardPaneListeners;
    }
}