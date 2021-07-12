package com.example.multilevellistview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

public class MultiLevelListViewItem {

    private String title;
    private ArrayList<String> parentPath;
    private MultiLevelListViewItem parent;
    private ArrayList<MultiLevelListViewItem> children;
    private boolean expanding;
    private int depth;


    public MultiLevelListViewItem(String inputTitle, MultiLevelListViewItem inputParent, ArrayList<String> inputParentPath) {

        title = inputTitle;
        parent = inputParent;
        children = new ArrayList<>();
        if(inputParent == null) {
            depth = 0;

        }
        else {
            depth = inputParent.getDepth()+1;
        }

        parentPath = inputParentPath;
        expanding = false;
    }

    public String getTitle() {
        return title;
    }

    public MultiLevelListViewItem getParent() {
        return parent;
    }

    public boolean addChildToFront(MultiLevelListViewItem child) {
        for(MultiLevelListViewItem originChild : children) {
            if(child.getTitle().equals(originChild.getTitle())) {
                return false;
            }
        }
        children.add(0,child);
        return true;
    }
    public boolean addChildToLast(MultiLevelListViewItem child) {
        for(MultiLevelListViewItem originChild : children) {
            if(child.getTitle().equals(originChild.getTitle())) {
                return false;
            }
        }
        children.add(child);
        return true;
    }

    public boolean containsChild(String childTitle) {
        for(MultiLevelListViewItem child : children) {
            if(child.getTitle().equals(childTitle)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<MultiLevelListViewItem> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.size()!=0;
    }

    public boolean isExpanding() {
        return expanding;
    }

    public int getDepth() {
        return depth;
    }

    public ArrayList<String> getParentPath() {
        return parentPath;
    }

    public void setTitle(String inputTitle) {
        title = inputTitle;
    }
    public void setExpanding(boolean inputDisplaying) {
        expanding = inputDisplaying;
    }
    public void convertDisplayState() {
        expanding = !expanding;
    }

    public String toJsonString() {
        Stack<MultiLevelListViewItem> traversalStack = new Stack<>();
        Stack<JSONObject> jsonTraversalStack = new Stack<>();
        JSONObject rootJsonObject = new JSONObject();
        try {
            rootJsonObject.put("title",this.getTitle());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        traversalStack.add(this);
        jsonTraversalStack.add(rootJsonObject);

        MultiLevelListViewItem tmpMultiLevelListViewItem;
        JSONObject tmpJsonObject;

        while(!traversalStack.isEmpty() && !jsonTraversalStack.isEmpty()) {
            tmpMultiLevelListViewItem = traversalStack.pop();
            tmpJsonObject = jsonTraversalStack.pop();

            if(tmpMultiLevelListViewItem.hasChildren()) {
                ArrayList<MultiLevelListViewItem> tmpChildrenMultiLevelListViewItem = tmpMultiLevelListViewItem.getChildren();
                JSONArray tmpChildrenJsonArray = new JSONArray();

                for(int i=0;i<tmpChildrenMultiLevelListViewItem.size();i++) {

                    traversalStack.add(tmpChildrenMultiLevelListViewItem.get(i));
                    JSONObject tmpChildJsonObject = new JSONObject();
                    try {
                        tmpChildJsonObject.put("title",tmpChildrenMultiLevelListViewItem.get(i).getTitle());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    tmpChildrenJsonArray.put(tmpChildJsonObject);
                    jsonTraversalStack.add(tmpChildJsonObject);

                }
                try {
                    tmpJsonObject.put("children",tmpChildrenJsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return rootJsonObject.toString();
    }

}


