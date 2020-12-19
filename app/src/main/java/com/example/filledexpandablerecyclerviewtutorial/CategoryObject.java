package com.example.filledexpandablerecyclerviewtutorial;

import java.util.List;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

public class CategoryObject extends ExpandableGroup<ItemObject> {
    private List<ItemObject> itemObjects;
    Boolean selectAllClicked = false;
    public CategoryObject(String title, List<ItemObject> items) {
        super(title, items);
    }

    public List<ItemObject> getChildList() {
        return itemObjects;
    }

    public boolean isInitiallyExpanded(){
        return false;
    }
}