package com.example.filledexpandablerecyclerviewtutorial;

import android.content.Context;
import android.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ReadAndWriteItems {

    ArrayList<ItemObject> items = new ArrayList<>();
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<String> itemCategories = new ArrayList<>();
    ArrayList<String> itemCategorieswithDuplicates = new ArrayList<>();
    ArrayList<ItemObject> tempItemSelection = new ArrayList<>();
    private String itemCategory, theItemName, itemAddressName;

    public Triplet<ArrayList<ItemObject>, ArrayList<String>,ArrayList<String>> readItemList(Context context)
    {
        String line;
        try {
            FileInputStream fileInputStream = context.openFileInput("items.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while((line = bufferedReader.readLine()) != null)
            {
                ArrayList<String> itemLine = new ArrayList<>(Arrays.asList(line.split(",")));
                this.items.add(new ItemObject(itemLine.get(0), itemLine.get(1), itemLine.get(2)));
                this.itemNames.add(itemLine.get(1));
                this.itemCategorieswithDuplicates.add(itemLine.get(0));
            }
            bufferedReader.close();
            itemCategories = removeDuplicates(itemCategorieswithDuplicates);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }catch (IOException e) {
            System.out.println(e);
        }
        return new Triplet<>(items,itemNames,itemCategories);
    }

    public Pair<Boolean,ArrayList<ItemObject>> readTempSelectedItemList(Context context)
    {
        boolean isThereFile;
        String line;
        tempItemSelection.clear();
        try {
            FileInputStream fileInputStream = context.openFileInput("tempItemsSelection.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while((line = bufferedReader.readLine()) != null)
            {
                ArrayList<String> selectedItemLine = new ArrayList<>(Arrays.asList(line.split(",")));
                    this.tempItemSelection.add(new ItemObject(selectedItemLine.get(0), selectedItemLine.get(1), selectedItemLine.get(2)));
            }
            isThereFile = true;
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            //Do nothing
            isThereFile = false;
        }catch (IOException e) {
            //Do nothing
            isThereFile = false;
        }
        return new Pair<>(isThereFile, tempItemSelection);
    }

    public void writeItemList(Context context, ArrayList<ItemObject> items)
    {
        String clearFile = "";
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("items.csv", context.MODE_PRIVATE);
            fileOutputStream.write(clearFile.getBytes());
            Iterator i = items.iterator();

            while (i.hasNext())
            {
                ItemObject item = (ItemObject) i.next();
                String line = item.getItemCategory() + "," + item.getName() + "," + item.getItemPrice() + "\n";
                fileOutputStream.write(line.getBytes());
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Ain't it fam");
            e.printStackTrace();
        }
    }

    public void writeTempSelectedItemList(Context context, List<ItemObject> selectedItems)
    {
        String clearFile = "";
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("tempItemsSelection.csv", context.MODE_PRIVATE);
            fileOutputStream.write(clearFile.getBytes());
            Iterator i = selectedItems.iterator();
            while (i.hasNext())
            {
                ItemObject selectedItem = (ItemObject) i.next();
                System.out.println(selectedItem.getItemCategory() + "," + selectedItem.getName() + "," + selectedItem.getItemPrice());
                String line = selectedItem.getItemCategory() + "," + selectedItem.getName() + "," + selectedItem.getItemPrice() + "\n";
                fileOutputStream.write(line.getBytes());
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Ain't it fam");
            e.printStackTrace();
        }
    }

    // Function to remove duplicates from an ArrayList
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {
        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {
            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }
        // return the new list
        return newList;
    }

    private void nullifyingEmptyAutoCompletesForItem(ItemObject item)
    {
        itemCategory = item.getItemCategory();
        theItemName = item.getItemPrice();
        itemAddressName = item.getItemPrice();

        if(itemCategory.trim().isEmpty())
        {
            itemCategory = null;
        }

        if(theItemName.trim().isEmpty())
        {
            theItemName = null;
        }

        if(itemAddressName.trim().isEmpty())
        {
            itemAddressName = null;
        }
    }
}
