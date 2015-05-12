package com.example.expandablelistview;

import java.util.ArrayList;

/**
 * @author harshalb
 *This is pojo class for gift cards.
 */
public class GiftCardsPojo {
	int cardPoints;
	int cardImage;
	
	/**
	 * @param cardPoints
	 * @param cardImage
	 */
	public GiftCardsPojo(int cardPoints,int cardImage){
		this.cardPoints=cardPoints;
		this.cardImage=cardImage;
		
	}
	
	/**
	 * 
	 */
	public GiftCardsPojo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	public ArrayList<GiftCardsPojo> getAppData(){
		ArrayList<GiftCardsPojo> appdata=new ArrayList<GiftCardsPojo>();
		/****************get apps data****************/
		appdata.add(new GiftCardsPojo(10,R.drawable.gift_card_noimg_g));
		appdata.add(new GiftCardsPojo(25,R.drawable.gift_card_noimg_g));
		appdata.add(new GiftCardsPojo(50,R.drawable.gift_card_noimg_g));
		appdata.add(new GiftCardsPojo(10,R.drawable.gift_card_noimg_i));
		appdata.add(new GiftCardsPojo(25,R.drawable.gift_card_noimg_i));
		appdata.add(new GiftCardsPojo(50,R.drawable.gift_card_noimg_i));
		

		
		return appdata;
	}
}
