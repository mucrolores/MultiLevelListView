package com.example.multilevellistview;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

import static com.example.multilevellistview.Constants.LISTVIEW_ITEM_TAG;

public class MultiLevelListViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private MultiLevelListViewItem rootItem;
    private ArrayList<MultiLevelListViewItem> showingItem;

    private TextView editTitleTextView;
    private EditText targetTitleEditText;
    private Button cancelEditTitleDialogButton, confirmEditTitleDialogButton;
    private Dialog editTitleDialog;

    private TextView addChildClassificationTextView;
    private EditText childNameEditText;
    private Button cancelAddChildDialogButton, confirmButtonAddChildDialogButton;
    private Dialog addChildDialog;

    private int mainWindowHeight, mainWindowWidth;

    static class ViewHolder {
        TextView titleTextView;
        ImageButton editTitleImageButton;
        ImageButton addChildImageButton;
    }

    public MultiLevelListViewAdapter(Context inputContext, String itemListJSONString, DisplayMetrics displayMetrics) {
        context = inputContext;
        layoutInflater = LayoutInflater.from(inputContext);
        showingItem = new ArrayList<>();

        mainWindowHeight = displayMetrics.heightPixels;
        mainWindowWidth = displayMetrics.widthPixels;

        initialDialog();
        parseJsonString(itemListJSONString);
        checkDisplaying();
    }

    @Override
    public int getCount() {
        return showingItem.size();
    }

    @Override
    public Object getItem(int position) {
        return showingItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.multi_level_list_view_item_layout, null);
            viewHolder.titleTextView = convertView.findViewById(R.id.titleTextViewId);
            viewHolder.titleTextView.setText(showingItem.get(position).getTitle());

            viewHolder.editTitleImageButton = convertView.findViewById(R.id.editTitleImageButtonId);
            viewHolder.editTitleImageButton.setFocusable(false);
            viewHolder.editTitleImageButton.setFocusableInTouchMode(false);

            viewHolder.addChildImageButton = convertView.findViewById(R.id.addChildImageButtonId);
            viewHolder.addChildImageButton.setFocusable(false);
            viewHolder.addChildImageButton.setFocusableInTouchMode(false);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.titleTextView.setText(showingItem.get(position).getTitle());
        }

        // set edit title function
        if(position>0) {
            viewHolder.editTitleImageButton.setOnClickListener((View v) -> {
                Log.d(LISTVIEW_ITEM_TAG, showingItem.get(position).getTitle() + ", edit title clicked");

                editTitleTextView.setText(showingItem.get(position).getTitle());
                targetTitleEditText.setText("");

                cancelEditTitleDialogButton.setOnClickListener((View cancelButtonView) -> {
                    editTitleDialog.dismiss();
                });
                confirmEditTitleDialogButton.setOnClickListener((View confirmButtonView) -> {
                    if(targetTitleEditText.getText().length() > 0) {
                        MultiLevelListViewItem currentItem = showingItem.get(position);

                        String originTitle = currentItem.getTitle();
                        String targetTitle = targetTitleEditText.getText().toString();

                        if(originTitle.equals(targetTitle)) {
                            Toast.makeText(context, "Target name is same as old classification name",Toast.LENGTH_SHORT).show();
                        }
                        else if(currentItem.getParent().containsChild(targetTitle)) {
                            Toast.makeText(context, "Target name already exist",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            currentItem.setTitle(targetTitle);
                            notifyDataSetChanged();
                            editTitleDialog.dismiss();
                            Toast.makeText(context, "classification: " + originTitle + " has rename to " + targetTitle, Toast.LENGTH_SHORT).show();
                            Log.d(LISTVIEW_ITEM_TAG,"Confirm: " + originTitle + "to " + targetTitle);
                        }
                    }
                    else {
                        Toast.makeText(context, "Please fill up the name row", Toast.LENGTH_SHORT).show();
                    }
                });

                editTitleDialog.show();
                editTitleDialog.getWindow().setLayout((int)(mainWindowWidth*0.9f),(int)(mainWindowHeight *0.4f));
            });
        }
        else {
            viewHolder.titleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT,7.0f
            ));
            viewHolder.editTitleImageButton.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 0f));
        }


        // set add child function
        viewHolder.addChildImageButton.setOnClickListener((View v) -> {
            Log.d(LISTVIEW_ITEM_TAG, showingItem.get(position).getTitle() + ", add child clicked");

            addChildClassificationTextView.setText(showingItem.get(position).getTitle());
            childNameEditText.setText("");

            cancelAddChildDialogButton.setOnClickListener((View cancelButtonView)-> {
                addChildDialog.dismiss();
            });
            confirmButtonAddChildDialogButton.setOnClickListener((View confirmButtonView) -> {
                if(childNameEditText.getText().length() > 0) {
                    String childTitle = childNameEditText.getText().toString();

                    MultiLevelListViewItem parentItem = showingItem.get(position);
                    ArrayList<String> parentItemPath = new ArrayList<>(parentItem.getParentPath());
                    parentItemPath.add(parentItem.getTitle());

                    if(!parentItem.containsChild(childTitle)){

                        parentItem.addChildToLast(new MultiLevelListViewItem(childNameEditText.getText().toString(), parentItem, parentItemPath));

                        checkDisplaying();
                        addChildDialog.dismiss();
                        Toast.makeText(context, "classification: " + childNameEditText.getText().toString() + " added", Toast.LENGTH_SHORT).show();

                        Log.d(LISTVIEW_ITEM_TAG,"Confirm: " + showingItem.get(position).getTitle() + ", " + childNameEditText.getText().toString());

                    }
                    else {
                        Toast.makeText(context, "add failed : the classification name already exist", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "Please fill up the name row", Toast.LENGTH_SHORT).show();
                }
            });

            addChildDialog.show();
            addChildDialog.getWindow().setLayout((int)(mainWindowWidth*0.9f),(int)(mainWindowHeight *0.4f));
        });

        MultiLevelListViewItem multiLevelListViewItem = showingItem.get(position);
        viewHolder.titleTextView.setPadding(multiLevelListViewItem.getDepth()*50,0,0,0);

        return convertView;
    }


    private void initialDialog() {
        // initialize editTitleDialog
        View editTitleDialogView = layoutInflater.inflate(R.layout.dialog_multi_level_list_view_edit_title_layout, null);
        editTitleTextView = editTitleDialogView.findViewById(R.id.editTitleTextView_ID);
        targetTitleEditText = editTitleDialogView.findViewById(R.id.targetTitleEditText_ID);
        cancelEditTitleDialogButton = editTitleDialogView.findViewById(R.id.cancelEditTitleDialogButton_ID);
        confirmEditTitleDialogButton = editTitleDialogView.findViewById(R.id.confirmEditTitleDialogButton_ID);

        editTitleDialog = new Dialog(context);
        editTitleDialog.setContentView(editTitleDialogView);
        editTitleDialog.setCancelable(false);

        // initialize addChildDialog
        View addChildDialogView = layoutInflater.inflate(R.layout.dialog_multi_level_list_view_add_child_layout, null);
        addChildClassificationTextView = addChildDialogView.findViewById(R.id.addChildClassificationTextView_ID);
        childNameEditText = addChildDialogView.findViewById(R.id.childNameEditText_ID);
        cancelAddChildDialogButton = addChildDialogView.findViewById(R.id.cancelButton_ID);
        confirmButtonAddChildDialogButton = addChildDialogView.findViewById(R.id.confirmButton_ID);

        addChildDialog = new Dialog(context);
        addChildDialog.setContentView(addChildDialogView);
        addChildDialog.setCancelable(false);

    }

    private void parseJsonString(String itemListJSONString) {
        Stack<MultiLevelListViewItem> traversalItemStack = new Stack<>();
        Stack<JSONObject> traversalJSONQueue = new Stack<>();
        try {
            JSONObject allDataJSONObject = new JSONObject(itemListJSONString);
            String title = allDataJSONObject.getString("title");
            rootItem = new MultiLevelListViewItem(title, null, new ArrayList<>());
            traversalItemStack.add(rootItem);

            traversalJSONQueue.add(allDataJSONObject);

            JSONObject tmpJSONObject;
            MultiLevelListViewItem tmpMultiLevelListViewItem,childMultiLevelListViewItem;

            while(!traversalItemStack.isEmpty() && !traversalJSONQueue.isEmpty()) {
                tmpMultiLevelListViewItem = traversalItemStack.pop();
                tmpJSONObject = traversalJSONQueue.pop();
                showingItem.add(tmpMultiLevelListViewItem);
                if(tmpJSONObject.has("children"))
                {
                    JSONArray childrenJSONArray = tmpJSONObject.getJSONArray("children");
                    ArrayList<String> tmpMultiLevelListViewItemParentPath = new ArrayList<>(tmpMultiLevelListViewItem.getParentPath());
                    tmpMultiLevelListViewItemParentPath.add(tmpMultiLevelListViewItem.getTitle());
                    for(int i=childrenJSONArray.length()-1;i>=0;i--) {
                        childMultiLevelListViewItem = new MultiLevelListViewItem( childrenJSONArray.getJSONObject(i).getString("title"),
                                tmpMultiLevelListViewItem, tmpMultiLevelListViewItemParentPath);
                        traversalItemStack.add(childMultiLevelListViewItem);
                        tmpMultiLevelListViewItem.addChildToFront(childMultiLevelListViewItem);
                        traversalJSONQueue.add(childrenJSONArray.getJSONObject(i));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*for(MultiLevelListViewItem item : showingItem) {
            StringBuilder logMessageStringBuilder = new StringBuilder();
            logMessageStringBuilder.append("title").append(item.getTitle());
            if(item.getParent()!= null) {
                logMessageStringBuilder.append(", parent title: ").append(item.getParent().getTitle());
            }
            else {
                logMessageStringBuilder.append(", parent title: null");
            }
            if(item.hasChildren()) {
                ArrayList<MultiLevelListViewItem> tmp = item.getChildren();
                logMessageStringBuilder.append(", ");
                for(int i=0;i<tmp.size();i++) {
                    logMessageStringBuilder.append("children").append(i).append(": ").append(tmp.get(i).getTitle());

                    if(i < tmp.size()-1) {
                        logMessageStringBuilder.append(", ");
                    }
                }
            }
            logMessageStringBuilder.append(", depth: ").append(item.getDepth());
            Log.d("MultiLevelListViewItem", logMessageStringBuilder.toString());
        }*/
    }

    public void checkDisplaying() {
        Stack<MultiLevelListViewItem> traversalItemStack = new Stack<>();
        MultiLevelListViewItem tmpMultiLevelListViewItem;

        showingItem.clear();

        traversalItemStack.add(rootItem);
        while(!traversalItemStack.isEmpty()) {
            tmpMultiLevelListViewItem = traversalItemStack.pop();
            showingItem.add(tmpMultiLevelListViewItem);
            if(tmpMultiLevelListViewItem.isExpanding() && tmpMultiLevelListViewItem.hasChildren()) {
                ArrayList<MultiLevelListViewItem> tmpChildren =  tmpMultiLevelListViewItem.getChildren();
                for(int i=tmpChildren.size()-1;i>=0;i--) {
                    traversalItemStack.add(tmpChildren.get(i));
                }
            }
        }
    }

    public MultiLevelListViewItem getClickedItem(int position) {
        return showingItem.get(position);
    }

    public void updateView(int position) {
        showingItem.get(position).convertDisplayState();
        checkDisplaying();

        MultiLevelListViewAdapter.this.notifyDataSetChanged();
    }

    public String getAdapterBeautifyStringStructure() {

        StringBuilder resultStringBuilder = new StringBuilder();

        Stack<MultiLevelListViewItem> traversalStack = new Stack<>();
        MultiLevelListViewItem tmp;
        traversalStack.add(rootItem);
        while(!traversalStack.isEmpty()) {
            tmp = traversalStack.pop();
            resultStringBuilder.append(getAlignmentString(2*tmp.getDepth()-1)).append("{\n")
                    .append(getAlignmentString(2*tmp.getDepth())).append("\"title\":\"").append(tmp.getTitle()).append("\"");
            if(tmp.hasChildren()) { // if hasChildren then add children header
                resultStringBuilder.append(",\n").append(getAlignmentString(2*tmp.getDepth())).append("\"children\":\n")
                        .append(getAlignmentString(2*tmp.getDepth())).append("[\n");
                ArrayList<MultiLevelListViewItem> tmpChildren = tmp.getChildren();
                for(int i=tmpChildren.size()-1;i>=0;i--) {
                    traversalStack.add(tmpChildren.get(i));
                }
            } // end of if
            else { // current is the leaf node
                resultStringBuilder.append("\n");
                if(traversalStack.size()>0) { // if node is not the last node of tree
                    int depthDifference = tmp.getDepth() - traversalStack.peek().getDepth();
                    if (depthDifference != 0) {
                        for (int i = depthDifference; i > 0; i--) {
                            resultStringBuilder.append(getAlignmentString(2*i+1)).append("}\n");
                            resultStringBuilder.append(getAlignmentString(2*i)).append("]\n");
                        }
                        resultStringBuilder.append(getAlignmentString(2*depthDifference-1)).append("},\n");
                    }
                    else {
                        resultStringBuilder.append(getAlignmentString(2*tmp.getDepth()-1)).append("},\n");
                    }
                }
                else { // if node is the last node of tree
                    for(int i=tmp.getDepth();i>0;i--) {
                        resultStringBuilder.append(getAlignmentString(2*i-1)).append("}\n");
                        resultStringBuilder.append(getAlignmentString(2*(i-1))).append("]\n");

                    }
                    resultStringBuilder.append("}\n");
                }
            }

        }
        return resultStringBuilder.toString();
    }

    public String getAdapterSingleLineStringStructure() {
        StringBuilder resultStringBuilder = new StringBuilder();

        Stack<MultiLevelListViewItem> traversalStack = new Stack<>();
        MultiLevelListViewItem tmp;
        traversalStack.add(rootItem);
        while(!traversalStack.isEmpty()) {
            tmp = traversalStack.pop();
            resultStringBuilder.append("{").append("\"title\":\"").append(tmp.getTitle()).append("\"");
            if(tmp.hasChildren()) { // if hasChildren then add children header
                resultStringBuilder.append(",").append("\"children\": [");
                ArrayList<MultiLevelListViewItem> tmpChildren = tmp.getChildren();
                for(int i=tmpChildren.size()-1;i>=0;i--) {
                    traversalStack.add(tmpChildren.get(i));
                }
            } // end of if
            else { // current is the leaf node
                if(traversalStack.size()>0) { // if node is not the last node of tree
                    int depthDifference = tmp.getDepth() - traversalStack.peek().getDepth();
                    if (depthDifference != 0) {
                        for (int i = depthDifference; i > 0; i--) {
                            resultStringBuilder.append("}]");
                        }
                    }
                    resultStringBuilder.append("},");
                }
                else { // if node is the last node of tree
                    for(int i=tmp.getDepth();i>0;i--) {
                        resultStringBuilder.append("}]");

                    }
                    resultStringBuilder.append("}");
                }
            }

        }
        return resultStringBuilder.toString();
    }

    public String getAdapterJsonString() {
        return rootItem.toJsonString();
    }

    private String getAlignmentString(int depth) {
        StringBuilder result = new StringBuilder();
        for(int i=0;i<=depth;i++) {
            result.append("\t");
        }

        return result.toString();
    }

}
