package com.example.expandablelistview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * @author harshalb
 *This is main class for reward points.
 */
public class RewardPoints extends Activity {

	    ExpandableListAdapter listAdapter;
	    ExpandableListView expListView;
	    Button giftCardButton;
	    List<String> listDataHeader;
	    HashMap<String, List<String>> listDataChild;
	 
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	 
	        // get the listview
	        expListView = (ExpandableListView) findViewById(R.id.rewardListView);
	        giftCardButton=(Button)findViewById(R.id.gift_card_button);
	 
	        giftCardButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intentGiftCard=new Intent(getApplicationContext(),GiftCards.class);
					startActivity(intentGiftCard);
				}
			});
	        // preparing list data
	        prepareListData();
	 
	        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
	 
	        // setting list adapter
	        expListView.setAdapter(listAdapter);
	    }
	
	    /*
	     * Preparing the list data
	     */
	    private void prepareListData(){
	        listDataHeader = new ArrayList<String>();
	        listDataChild = new HashMap<String, List<String>>();
	 
	        // Adding child data
	        listDataHeader.add("How can I earn rewards points?");
	        listDataHeader.add("Look for our App e-Guy Logo,");
	        listDataHeader.add("Do I only earn rewards points for purchases made through my personal My Apps Co e-Store?");
	        listDataHeader.add("How many points can I earn?");
	        listDataHeader.add("Where can I find my current points balance?");
	        listDataHeader.add("How do I redeem my points?");
	        listDataHeader.add("Do Reward Points expire?");
	        listDataHeader.add("What Items Are Eligible For Rewards?");
	        
	 
	        // Adding child data
	        List<String> one = new ArrayList<String>();
	        one.add("Simply download the My Apps Co app and use it in place of your existing App store. For every $1.00, you spend for My Apps Co products directly, you accumulate 1 point. Your points will be calculated for each purchase, $1=1 point.");
	 
	        List<String> two = new ArrayList<String>();
	        two.add("to know when it's a My Apps Co direct purchase. Additionally, you can earn reward points for selling cell phone plans or renewals directly from your My Apps Co e-store, 15 points for each renewal & 50 points for each new line.");
	 
	        List<String> three = new ArrayList<String>();
	        three.add("Yes, all purchases must be made directly from your e-Store. No rewards points are given for purchases in iTunes or Google Play even though we do promote Apps in those platforms.");
	 
	        List<String> four = new ArrayList<String>();
	        four.add("There is no limit to how many Reward points you may earn. The more you spend with My Apps Co, the more you earn.");
	 
	        List<String> five = new ArrayList<String>();
	        five.add("Login to your account on your My Apps Co app or on www.myappsco.com to view your current rewards totals and or redeem them.");
	 
	        List<String> six = new ArrayList<String>();
	        six.add("It's very simple to redeem your My Apps Co Reward Points. Simply login to your account and follow the tutorial to redeem your points.");
	 
	        List<String> seven = new ArrayList<String>();
	        seven.add("Yes, you must redeem your My Apps Co Reward Points within 12 months of earning (or receiving them). Points earned on January 10, 2013 will expire on January 10, 2014. Any account with no activity for 1 year will be closed.");
	 
	        List<String> eight = new ArrayList<String>();
	        eight.add("Look for the App e-Guy Logo to know when it's a My Apps Co direct purchase. Currently we have the following items available for Rewards. We are always adding to our list of direct purchases:Select Android Apps in the My Apps Co Store All Books in the My Apps Co e-Book Store Tablets (Excluding Apportunity Tablets purchased during ABO enrollment) Cell Phones Plans - new lines and renewals");
	
	       
	        listDataChild.put(listDataHeader.get(0), one); // Header, Child data
	        listDataChild.put(listDataHeader.get(1), two);
	        listDataChild.put(listDataHeader.get(2), three);
	        listDataChild.put(listDataHeader.get(3), four);
	        listDataChild.put(listDataHeader.get(4), five);
	        listDataChild.put(listDataHeader.get(5), six);
	        listDataChild.put(listDataHeader.get(6), seven);
	        listDataChild.put(listDataHeader.get(7), eight);
	   
	    }}

class ExpandableListAdapter extends BaseExpandableListAdapter {
		 
	    private Context _context;
	    private List<String> _listDataHeader; // header titles
	    // child data in format of header title, child title
	    private HashMap<String, List<String>> _listDataChild;
	 
	    public ExpandableListAdapter(Context context, List<String> listDataHeader,
	            HashMap<String, List<String>> listChildData) {
	        this._context = context;
	        this._listDataHeader = listDataHeader;
	        this._listDataChild = listChildData;
	    }
	 
	    @Override
	    public Object getChild(int groupPosition, int childPosititon) {
	        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
	                .get(childPosititon);
	    }
	 
	    @Override
	    public long getChildId(int groupPosition, int childPosition) {
	        return childPosition;
	    }
	 
	    @Override
	    public View getChildView(int groupPosition, final int childPosition,
	            boolean isLastChild, View convertView, ViewGroup parent) {
	 
	        final String childText = (String) getChild(groupPosition, childPosition);
	 
	        if (convertView == null) {
	            LayoutInflater infalInflater = (LayoutInflater) this._context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = infalInflater.inflate(R.layout.list_item, null);
	        }
	 
	        TextView txtListChild = (TextView) convertView
	                .findViewById(R.id.lblListItem);
	 
	        txtListChild.setText(childText);
	        return convertView;
	    }
	 
	    @Override
	    public int getChildrenCount(int groupPosition) {
	        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
	                .size();
	    }
	 
	    @Override
	    public Object getGroup(int groupPosition) {
	        return this._listDataHeader.get(groupPosition);
	    }
	 
	    @Override
	    public int getGroupCount() {
	        return this._listDataHeader.size();
	    }
	 
	    @Override
	    public long getGroupId(int groupPosition) {
	        return groupPosition;
	    }
	 
	    @Override
	    public View getGroupView(int groupPosition, boolean isExpanded,
	            View convertView, ViewGroup parent) {
	        String headerTitle = (String) getGroup(groupPosition);
	        if (convertView == null) {
	            LayoutInflater infalInflater = (LayoutInflater) this._context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = infalInflater.inflate(R.layout.list_group, null);
	        }
	 
	        TextView lblListHeader = (TextView) convertView
	                .findViewById(R.id.lblListHeader);
	        lblListHeader.setTypeface(null, Typeface.BOLD);
	        lblListHeader.setText(headerTitle);
	 
	        return convertView;
	    }
	 
	    @Override
	    public boolean hasStableIds() {
	        return false;
	    }
	 
	    @Override
	    public boolean isChildSelectable(int groupPosition, int childPosition) {
	        return true;
	    }
	}

