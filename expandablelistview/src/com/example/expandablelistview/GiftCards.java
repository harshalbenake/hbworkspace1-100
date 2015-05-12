package com.example.expandablelistview;


import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author harshalb
 *This is main class for gift cards.
 */
public class GiftCards extends Activity {
	
	TextView textviewCount;
	int rewardPoints=50;
	LayoutInflater inflaterLayout;
	LinearLayout linearLayoutGiftCard;
	ArrayList<GiftCardsPojo> giftCardsPojosArrayList;
	LinearLayout mainGiftCard;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gift_cards);

		textviewCount=(TextView)findViewById(R.id.gift_card_reward_pts);
		textviewCount.setText("Available Reward Points :"+rewardPoints);
		giftCardsPojosArrayList=new ArrayList<GiftCardsPojo>();
		linearLayoutGiftCard = (LinearLayout)findViewById(R.id.linlay_giftcard_sublayout);
		inflaterLayout = LayoutInflater.from(getBaseContext());
		GiftCardasyntask giftCardasyntask=new GiftCardasyntask();
		giftCardasyntask.execute();
	
	}

	
		/**
		 * @param container
		 * @param numberofitems
		 */
		public void getGiftCardConatainerLayout(LinearLayout container,int numberofitems) {
			 mainGiftCard=(LinearLayout)findViewById(R.id.linlay_giftcards_main);
			int count = -1;
			if (giftCardsPojosArrayList != null && giftCardsPojosArrayList.size() > 0) {
			for (GiftCardsPojo item : giftCardsPojosArrayList) {
				count++;
				if (count >= numberofitems)
					break;
				
				getItemView(R.layout.gift_card_item,mainGiftCard, 1,item);
			}
			container.removeAllViews();
			container.addView(mainGiftCard);
			}
		}
	/**
	 * @param giftCardItem
	 * @param position
	 * @param cardPoints
	 * @param limitCounter
	 * @param cardImage
	 */
	public void getItemView(int giftCardItemLayout,LinearLayout container,final int position,final GiftCardsPojo item)
	{
		// TODO Auto-generated method stub
		View giftCardItem = (View)inflaterLayout.inflate(giftCardItemLayout, null, false);
		TextView giftCardPoints = (TextView)giftCardItem.findViewById(R.id.gift_card_points);
		giftCardPoints.setText(item.cardPoints+"");
		ImageView gift_card_image = (ImageView)giftCardItem.findViewById(R.id.gift_card_image);
		gift_card_image.setImageResource(item.cardImage);
		ImageView buttonPlus=(ImageView)giftCardItem.findViewById(R.id.button_plus);
		ImageView  buttonMinus=(ImageView)giftCardItem.findViewById(R.id.button_minus);
		final EditText editTextcountItem=(EditText)giftCardItem.findViewById(R.id.countitem);
		final CheckBox checkboxSelection=(CheckBox)giftCardItem.findViewById(R.id.checkboxcount);
		textviewCount=(TextView)findViewById(R.id.gift_card_reward_pts);
		textviewCount.setText("Available Reward Points :"+rewardPoints);
		editTextcountItem.setTag(0);
		buttonPlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Integer counter = (Integer) editTextcountItem.getTag();
				if(rewardPoints>=item.cardPoints)
				{
					counter++;
					editTextcountItem.setText(counter+"");
					editTextcountItem.setTag(counter);
					rewardPoints=rewardPoints-item.cardPoints;
					textviewCount.setText("Available Reward Points :"+rewardPoints);
					if(counter!=0)
						checkboxSelection.setChecked(true);
				}else{
					Toast.makeText(getBaseContext(), "You do not have sufficient reward point", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});

		buttonMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Integer counter = (Integer) editTextcountItem.getTag();
				checkboxSelection.getTag();
				if(counter>0)
				{
					counter--;
					editTextcountItem.setText(counter+"");
					editTextcountItem.setTag(counter);
					rewardPoints=rewardPoints+item.cardPoints;
					textviewCount.setText("Available Reward Points :"+rewardPoints);
					if(counter==0)
						checkboxSelection.setChecked(false);
				}else{
					Toast.makeText(getBaseContext(), "You do not have sufficient reward point", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
		container.addView(giftCardItem);

	}
	
	
	
	
	/**
	 * @author harshalb
	 *This asyntask is used for gift card.
	 */
	private class GiftCardasyntask extends AsyncTask<String, String, Object> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
		}

		@Override
		protected String doInBackground(String... params) {

		
			GiftCardsPojo giftCardsPojo=new GiftCardsPojo();
			giftCardsPojosArrayList=giftCardsPojo.getAppData();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
		
			getGiftCardConatainerLayout(linearLayoutGiftCard, 5);
			getGiftCardConatainerLayout(linearLayoutGiftCard, 5);

			super.onPostExecute(result);
		}

	}
}