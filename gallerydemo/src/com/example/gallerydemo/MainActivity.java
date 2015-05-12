package com.example.gallerydemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {


    ImageView selectedImage; 
    private Integer[] mImageIds = {
               R.drawable.one,
               R.drawable.two,
               R.drawable.three,
               R.drawable.four,
               R.drawable.five,
               
       };
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
     
            Gallery gallery = (Gallery) findViewById(R.id.gallery1);
       selectedImage=(ImageView)findViewById(R.id.imageView1);
       gallery.setSpacing(1);
       gallery.setAdapter(new GalleryImageAdapter(this));

        // clicklistener for Gallery
       gallery.setOnItemClickListener(new OnItemClickListener() {
           public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
               Toast.makeText(MainActivity.this, "Your selected position = " + position, Toast.LENGTH_SHORT).show();
               // show the selected Image
               selectedImage.setImageResource(mImageIds[position]);
           }
       });
   }

public class GalleryImageAdapter extends BaseAdapter
{
    private Context mContext;

    private Integer[] mImageIds = {
    		 R.drawable.one,
             R.drawable.two,
             R.drawable.three,
             R.drawable.four,
             R.drawable.five,
    };

    public GalleryImageAdapter(Context context)
    {
        mContext = context;
    }

    public int getCount() {
        return mImageIds.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }


    // Override this method according to your need
    public View getView(int index, View view, ViewGroup viewGroup)
    {
        // TODO Auto-generated method stub
        ImageView i = new ImageView(mContext);

        i.setImageResource(mImageIds[index]);
        i.setLayoutParams(new Gallery.LayoutParams(200, 200));
   
        i.setScaleType(ImageView.ScaleType.FIT_XY);

        return i;
    }

	
}
}
