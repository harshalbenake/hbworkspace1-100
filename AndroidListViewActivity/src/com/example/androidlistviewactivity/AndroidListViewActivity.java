package com.example.androidlistviewactivity;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidListViewActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        // storing string resources into Array
        String[] adobe_products = getResources().getStringArray(R.array.adobe_products);
         
        // Binding resources Array to ListAdapter
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, R.id.label, adobe_products));
         
        ListView lv = getListView();
 
        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
               
              // selected item
              String product = ((TextView) view).getText().toString();
               
              // Launching new Activity on selecting single List Item
              Intent i = new Intent(getApplicationContext(), SingleListItem.class);
              // sending data to new activity
              i.putExtra("product", product);
              startActivity(i);
             
          }
        });
    }
}