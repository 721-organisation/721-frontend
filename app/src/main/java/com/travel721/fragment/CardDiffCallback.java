package com.travel721.fragment;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.travel721.card.Card;

import java.util.List;

public class CardDiffCallback extends DiffUtil.Callback {

    List<Card> oldCards;
    List<Card> newCards;

    public CardDiffCallback(List<Card> newCards, List<Card> oldCards) {
        this.newCards = newCards;
        this.oldCards = oldCards;
    }

    @Override
    public int getOldListSize() {
        return oldCards.size();
    }

    @Override
    public int getNewListSize() {
        return newCards.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if (oldCards.get(oldItemPosition).getClass().equals(newCards.get(newItemPosition).getClass())) {
            return oldCards.get(oldItemPosition) == newCards.get(newItemPosition);
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCards.get(oldItemPosition).equals(newCards.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}