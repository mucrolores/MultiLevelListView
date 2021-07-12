
# MultiLevelListView

Using customize BaseAdapter to adapter on origin listview, create a MultiLevel function Listview.
## Preview

<img src="README_assets\Preview.gif" alt="BasicPage" width="50%" height="50%" ></img>



## Features

* support expand and collapse view items.

* support edit title of the items(just click the pencil icon on target item row)

* support add child node information

  <p>
      <img src="README_assets\BasicPage.jpg" alt="BasicPage" width="25%" height="25%" />
      <img src="README_assets\EditTitle.jpg" alt="EditTitle" width="25%" height="25%" />
      <img src="README_assets\AddChild.jpg" alt="AddChild" width="25%" height="25%" />
  </p>

* using File object to record the modified structure JSON string.
* The adapter parse JSON string to build up the structure of listview

## Classes

* MainActivity
* MultiLevelListViewItem
* MultiLevelListViewAdapter

couples of styles and constants

## Code Core

using single MultiLevelListViewItem as the root of structure tree

The object in MultiLevelListViewItem, tree node like data

``` java
public class MultiLevelListViewItem {
    private String title;
    private ArrayList<String> parentPath;
    private MultiLevelListViewItem parent;
    private ArrayList<MultiLevelListViewItem> children;
    private boolean expanding;
    private int depth;
}
```

using ArrayList to record displaying MultiLevelListViewItem

``` java
private ArrayList<MultiLevelListViewItem> showingItem;
```

notify the Adapter when Item clicked in the Activity

``` java
((MultiLevelListViewAdapter)listView.getAdapter()).updateView(position);
```

using Tree data structure to do the rename



some more details where in the code, hope this simple way to implement the MultiLevelListView help you out.

