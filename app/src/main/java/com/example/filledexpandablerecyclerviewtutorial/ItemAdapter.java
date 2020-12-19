package com.example.filledexpandablerecyclerviewtutorial;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class ItemAdapter extends ExpandableRecyclerViewAdapter<ItemAdapter.ItemCategoryViewHolder, ItemAdapter.ItemViewHolder> {
    private List<ItemObject> itemObjects;
    private ItemListener itemListener;
    private Context mContext;

    public ItemAdapter(List<? extends ExpandableGroup> groups, ItemListener itemListener, Context context)
    {
        super(groups);
        this.itemListener = itemListener;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemCategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_row, parent,false);
        return new ItemCategoryViewHolder(v);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(v);
    }


    @Override
    public void onBindChildViewHolder(ItemViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        itemObjects = group.getItems();
        ItemObject itemObject = itemObjects.get(childIndex);
        holder.bind(itemObject);

    }

    @Override
    public void onBindGroupViewHolder(ItemCategoryViewHolder holder, int flatPosition, ExpandableGroup group) {
        final CategoryObject itemCategoryObject = (CategoryObject) group;
        holder.bind(itemCategoryObject);
    }

    public List<ItemObject> getSelectedItems()
    {
        List<ItemObject> selectedItems = new ArrayList<>();
        for (ItemObject itemObject : itemObjects)
        {
            if(itemObject.isItemSelected)
            {
                selectedItems.add(itemObject);
            }
        }
        return selectedItems;
    }

    class ItemViewHolder extends ChildViewHolder {
        private final TextView itemName;
        private final TextView itemPrice;
        private final ConstraintLayout itemLayout;
        private final View backgroundView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.item_row_layout);
            backgroundView = itemView.findViewById(R.id.item_row_background);
            itemName = itemView.findViewById(R.id.item_row_textview);
            itemPrice = itemView.findViewById(R.id.price_textview);
        }

        public void bind(final ItemObject itemObject){
            itemName.setText(itemObject.name);
            itemPrice.setText(itemObject.itemPrice);
            if (itemObject.isItemSelected)
            {
                backgroundView.setBackgroundResource(R.drawable.item_selected);
                itemName.setTextColor(Color.WHITE);
                itemPrice.setTextColor(Color.WHITE);
            }
            else
            {
                backgroundView.setBackgroundResource(R.drawable.item_background);
                itemName.setTextColor(Color.parseColor("#46BDFD"));
                itemPrice.setTextColor(Color.parseColor("#46BDFD"));
            }
            itemLayout.setOnClickListener(v -> {
                if(itemObject.isItemSelected)
                {
                    backgroundView.setBackgroundResource(R.drawable.item_background);
                    itemName.setTextColor(Color.parseColor("#46BDFD"));
                    itemPrice.setTextColor(Color.parseColor("#46BDFD"));
                    itemObject.isItemSelected = false;
                    if(getSelectedItems().size() == 0)
                    {
                        itemListener.onItemAction(false);
                    }
                }
                else
                {
                    backgroundView.setBackgroundResource(R.drawable.item_selected);
                    itemObject.isItemSelected = true;
                    itemName.setTextColor(Color.WHITE);
                    itemPrice.setTextColor(Color.WHITE);
                }
            });
        }
    }

    public class ItemCategoryViewHolder extends GroupViewHolder
    {
        private TextView myTextView;
        private Button selectAll;
        private Button addNewItem;
        private ImageView expandCollapseArrow;
        public ItemCategoryViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.item_category_textview);
            selectAll = itemView.findViewById(R.id.select_all_items_button);
            addNewItem = itemView.findViewById(R.id.add_item_to_category_button);
            expandCollapseArrow = itemView.findViewById(R.id.expand_collapse_button);
        }

        public void bind(final CategoryObject itemCategoryObject){
            final List<ItemObject> children = itemCategoryObject.getItems();
            myTextView.setText(itemCategoryObject.getTitle());

            //Select button work
            if (itemCategoryObject.selectAllClicked)
            {
                selectAll.setBackgroundResource(R.drawable.deselect_all);
            }
            else
            {
                selectAll.setBackgroundResource(R.drawable.select_all);
            }
            selectAll.setOnClickListener(v -> {
                if(itemCategoryObject.selectAllClicked)
                {
                    selectAll.setBackgroundResource(R.drawable.select_all);
                    itemCategoryObject.selectAllClicked = false;
                    //deselect all child items
                    for(int i = 0; i < children.size(); i++)
                    {
                        children.get(i).isItemSelected = false;
                        notifyDataSetChanged();
                    }
                    Toast.makeText(v.getContext(), "All " + itemCategoryObject.getTitle() + " websites deselected.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    selectAll.setBackgroundResource(R.drawable.deselect_all);
                    itemCategoryObject.selectAllClicked = true;
                    //select all child items
                    for(int i = 0; i < children.size(); i++)
                    {
                        children.get(i).isItemSelected = true;
                        notifyDataSetChanged();
                    }
                    Toast.makeText(v.getContext(), "All " + itemCategoryObject.getTitle() + " websites selected.", Toast.LENGTH_SHORT).show();
                }
            });

            //Add button work
            addNewItem.setOnClickListener(v -> {
                if (mContext instanceof MainActivity)
                {
                    ((MainActivity)mContext).openAddItem(itemCategoryObject.getTitle());
                }

            });
        }

        //Animations for the arrows. Fun!
        @Override
        public void expand() {
            animateExpand();
        }

        @Override
        public void collapse() {
            animateCollapse();
        }

        private void animateExpand() {
            RotateAnimation rotate =
                    new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            expandCollapseArrow.setAnimation(rotate);
        }

        private void animateCollapse() {
            RotateAnimation rotate =
                    new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            expandCollapseArrow.setAnimation(rotate);
        }
    }

}

