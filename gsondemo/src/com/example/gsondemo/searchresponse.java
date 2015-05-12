package com.example.gsondemo;

import java.util.List;


import com.google.gson.annotations.SerializedName;

public class searchresponse {
	public List<Results> groupInfo;
		     
	    @SerializedName("status")
	   public String status;
	    
	   @SerializedName("message")
    public String message;      
	     
	}

