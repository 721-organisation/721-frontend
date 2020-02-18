package com.travel721.card;

import java.util.Comparator;

public class CardComparator<T extends Card> implements Comparator<T> {

    public CardComparator() {

    }

    @Override
    public int compare(T p1, T p2) {
        if (p1 instanceof EventCard && p2 instanceof EventCard) {
            return ((EventCard) p1).compareTo((EventCard) p2);
        } else {
            return 0;
        }
    }
}
