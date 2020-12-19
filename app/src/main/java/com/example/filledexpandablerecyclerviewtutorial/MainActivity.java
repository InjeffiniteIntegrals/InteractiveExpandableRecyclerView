package com.example.filledexpandablerecyclerviewtutorial;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements ItemListener {
    private int index;
    private EditText addCategoryItemName;
    private EditText addCategoryName;
    private EditText addCategoryItemPrice;
    private Button addCategory;
    private EditText itemName;
    private FrameLayout addANewCategory;
    ArrayList<CategoryObject> itemCategories = new ArrayList<>();
    ArrayList<String> itemCategoryNames = new ArrayList<>();
    ArrayList<ItemObject> allItems = new ArrayList<>();
    ArrayList<String> itemNames = new ArrayList<>();
    Triplet<ArrayList<ItemObject>,ArrayList<String>,ArrayList<String>> itemsNamesAndCategories = new Triplet<>(allItems,itemNames,itemCategoryNames);
    ArrayList<ItemObject> tempItemSelection = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //one-time write items and their categories, then comment out until you want to reset
        ReadAndWriteItems readAndWriteItems = new ReadAndWriteItems();
        String[] itemsArray = getResources().getStringArray(R.array.default_items_list);
        ArrayList<String> itemsStrings = new ArrayList<>();
        ArrayList<ItemObject> defaultItems = new ArrayList<>();
        Collections.addAll(itemsStrings, itemsArray);
        for(int i = 0; i < itemsStrings.size(); i++)
        {
            ArrayList<String> itemLine = new ArrayList<>(Arrays.asList(itemsStrings.get(i).split(",")));
            defaultItems.add(new ItemObject(itemLine.get(0),itemLine.get(1),itemLine.get(2)));
        }
        readAndWriteItems.writeItemList(this, defaultItems);


        //read the items and their categories
        itemsNamesAndCategories = readAndWriteItems.readItemList(this);
        allItems = itemsNamesAndCategories.getFirst();
        itemNames = itemsNamesAndCategories.getSecond();
        itemCategoryNames = itemsNamesAndCategories.getThird();

        addANewCategory = findViewById(R.id.add_item_category_layout);
        addCategory = findViewById(R.id.add_item_category);
        Button addCategoryCancel = findViewById(R.id.cancel_add_category);
        Button addCategoryConfirm = findViewById(R.id.add_category_confirm);
        addCategoryItemName = findViewById(R.id.add_item_of_category_name);
        addCategoryItemPrice = findViewById(R.id.add_item_of_category_price);
        addCategoryName = findViewById(R.id.add_category_name);
        RecyclerView recyclerView = findViewById(R.id.item_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemCategories.clear();

        //See if there was a temp selection made when user added a item or category
        Boolean isThereTemp = false;
        try {
            Pair<Boolean, ArrayList<ItemObject>> result = new Pair<>(readAndWriteItems.readTempSelectedItemList(this).first,readAndWriteItems.readTempSelectedItemList(this).second);
            tempItemSelection = result.second;
            isThereTemp = result.first;
            deleteFile("tempWebsitesSelection.csv");
        }
        catch (Exception e)
        {
            //Do nothing
        }

        //sort all the items into diff categories
        boxTheItems();

        //if a temp file for a previous selection was made when user added an item or category
        if(isThereTemp)
        {
            System.out.println("How big is the temp selection arraylist? " + tempItemSelection.size());
            for (ItemObject object : tempItemSelection) {
                System.out.println(object.itemCategory + "," + object.name + "," + object.itemPrice);
            }
            System.out.println("Okay, it made it up to the for loop");
            for (CategoryObject itemCategory : itemCategories) {
                for (int j = 0; j < itemCategory.getItems().size(); j++) {
                    for (ItemObject itemObject : tempItemSelection) {
                        if (itemCategory.getItems().get(j).itemCategory.equals(itemObject.itemCategory) && itemCategory.getItems().get(j).name.equals(itemObject.name) && itemCategory.getItems().get(j).itemPrice.equals(itemObject.itemPrice)) {
                            itemCategory.getItems().get(j).setItemSelected(true);
                        }
                    }
                }
            }
        }

        //put the categories with the boxed items in the adapter
        ItemAdapter adapter = new ItemAdapter(itemCategories, this, this);
        recyclerView.setAdapter(adapter);

        addCategoryCancel.setOnClickListener(v -> {
            addCategoryName.setText("");
            addCategoryItemName.setText("");
            addCategoryItemPrice.setText("");
            addANewCategory.setVisibility(View.INVISIBLE);
            addCategory.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            addCategory.setClickable(true);
        });

        addCategory.setOnClickListener(v -> {
            addCategory.setVisibility(View.INVISIBLE);
            addCategory.setClickable(false);
            recyclerView.setVisibility(View.INVISIBLE);
            addANewCategory.setVisibility(View.VISIBLE);
        });

        //Finish adding the category to the recyclerview with data validation
        addCategoryConfirm.setOnClickListener(v -> {
            //If category name empty
            if (addCategoryName.getText().toString().trim().matches("")) {
                Toast.makeText(MainActivity.this, "Please enter a name for the category", Toast.LENGTH_SHORT).show();
            }
            //If item name empty
            else if (addCategoryName.getText().toString().trim().matches("")) {
                Toast.makeText(MainActivity.this, "Please enter a name for the item", Toast.LENGTH_SHORT).show();
            }
            //If item price empty
            else if (addCategoryItemPrice.getText().toString().trim().matches("")) {
                Toast.makeText(MainActivity.this, "Please enter an item price", Toast.LENGTH_SHORT).show();
            }
            //If either field has commas
            else if (addCategoryName.getText().toString().trim().contains(",") || addCategoryName.getText().toString().trim().contains(",") || addCategoryItemPrice.getText().toString().trim().contains(",")) {
                Toast.makeText(MainActivity.this, "Fields cannot contain commas \",\"", Toast.LENGTH_SHORT).show();
            }
            //If price field has spaces
            else if (addCategoryItemPrice.getText().toString().trim().contains(" ")) {
                Toast.makeText(MainActivity.this, "Prices cannot contain spaces \" \"", Toast.LENGTH_SHORT).show();
            }
            //If category name already exists
            else if (doesCategoryNameExist(addCategoryName.getText().toString().trim())) {
                Toast.makeText(MainActivity.this, "That category already exists", Toast.LENGTH_SHORT).show();
            }
            else {
                readAndWriteItems.writeTempSelectedItemList(getApplicationContext(), getAllSelectedItems());
                itemCategories.clear();
                allItems.add(new ItemObject(addCategoryName.getText().toString().trim(), addCategoryItemName.getText().toString().trim(), addCategoryItemPrice.getText().toString().trim()));
                boxTheItems();
                readAndWriteItems.writeItemList(getApplicationContext(), allItems);
                addCategory.setVisibility(View.VISIBLE);
                addCategory.setClickable(true);
                recyclerView.setVisibility(View.VISIBLE);
                addANewCategory.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, addCategoryItemName.getText().toString() + " added to categories!", Toast.LENGTH_LONG).show();
                onCreate(new Bundle());
            }
        });
    }

    public ArrayList<ItemObject> getAllSelectedItems()
    {
        ArrayList<ItemObject> allSelectedItems = new ArrayList<>();
        for (CategoryObject itemCategory : itemCategories) {
            for (int j = 0; j < itemCategory.getItems().size(); j++) {
                if (itemCategory.getItems().get(j).isItemSelected) {
                    allSelectedItems.add(new ItemObject(itemCategory.getItems().get(j).getItemCategory(), itemCategory.getItems().get(j).getName(), itemCategory.getItems().get(j).getItemPrice()));
                }
            }
        }
        return allSelectedItems;
    }

    //Box the items
    private void boxTheItems()
    {
        for (String categoryName : itemCategoryNames) {
            itemCategories.add(new CategoryObject(categoryName, boxTheBox(categoryName)));
        }
    }

    private ArrayList<ItemObject> boxTheBox(String categoryName)
    {
        ArrayList<ItemObject> assocItems = new ArrayList<>();
        for (ItemObject allItem : allItems) {
            if (categoryName.matches(allItem.itemCategory)) {
                assocItems.add(new ItemObject(allItem.itemCategory, allItem.name, allItem.itemPrice));
            }
        }
        return assocItems;
    }

    public void openAddItem(String categoryTitle)
    {
        FrameLayout addItemTo = findViewById(R.id.add_item_to_category_layout);
        Button cancel = findViewById(R.id.cancel_add_item);
        Button add = findViewById(R.id.add_item_confirmation);
        EditText itemTitle = findViewById(R.id.add_item_name);
        EditText itemAddress = findViewById(R.id.add_item_address);
        addCategory.setVisibility(View.INVISIBLE);
        RecyclerView recyclerView = findViewById(R.id.item_list_recyclerview);
        recyclerView.setVisibility(View.INVISIBLE);
        addCategory.setClickable(false);
        addItemTo.setVisibility(View.VISIBLE);
        add.setOnClickListener(v -> {
                ReadAndWriteItems readAndWriteRestrictions = new ReadAndWriteItems();
                readAndWriteRestrictions.writeTempSelectedItemList(getApplicationContext(), getAllSelectedItems());
                itemCategories.clear();
                allItems.add(new ItemObject(categoryTitle,itemTitle.getText().toString().trim(),itemAddress.getText().toString().trim()));
                boxTheItems();
                readAndWriteRestrictions.writeItemList(getApplicationContext(),allItems);
                addCategory.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                addCategory.setClickable(true);
                addItemTo.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, itemTitle.getText().toString() + " has been added to " + categoryTitle + "!", Toast.LENGTH_LONG).show();
                onCreate(new Bundle());
        });

        cancel.setOnClickListener(v -> {
            itemTitle.setText("");
            itemAddress.setText("");
            addItemTo.setVisibility(View.INVISIBLE);
            addCategory.setVisibility(View.VISIBLE);
            RecyclerView recyclerView1 = findViewById(R.id.item_list_recyclerview);
            recyclerView1.setVisibility(View.VISIBLE);
            addCategory.setClickable(true);
        });
    }

    public boolean doesCategoryNameExist(String str)
    {
        boolean doesCategoryNameExist = false;
        for (CategoryObject domainCategory : itemCategories) {
            if (str.matches(domainCategory.getTitle())) {
                doesCategoryNameExist = true;
            }
        }
        return doesCategoryNameExist;
    }

    @Override
    public void onItemAction(Boolean isSelected) {
    }
}