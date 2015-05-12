
package com.pinterest.samples.pinit;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinterest.pinit.PinItButton;

import java.io.IOException;
import java.io.InputStream;

public class DemoFromGallery extends Activity {

    private static final String TAG = DemoMainActivity.TAG;
    private static final String WEB_URL = "http://placekitten.com";
    private static final String DEFAULT_DESCRIPTION = "This image is from gallery";
    private static final int IMAGE_SELECT = 801;

    private ImageView mImage;
    private Button mButton;
    private TextView mDescriptEt;
    private PinItButton mPinIt;
    private TextView mUriTv;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_from_gallery);

        mButton = (Button) findViewById(R.id.gallery_bt);
        mButton.setOnClickListener(mButtonClicked);
        mImage = (ImageView) findViewById(R.id.source_iv);
        mUriTv = (TextView) findViewById(R.id.uri_tv);
        mDescriptEt = (TextView) findViewById(R.id.desc_tv);
        mDescriptEt.setText(DEFAULT_DESCRIPTION);

        mPinIt = (PinItButton) findViewById(R.id.pin_bt);

        PinItButton.setDebugMode(true);
        PinItButton.setPartnerId("myApp");
        mPinIt.setUrl(WEB_URL);
        mPinIt.setDescription(DEFAULT_DESCRIPTION);
        PinItButton.setPartnerId(DemoMainActivity.CLIENT_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_SELECT) {
            // Check for returned image from gallery
            if (data == null)
                return;

            Uri imageUri = data.getData();
            setImageUri(imageUri);

        }
    }

    public void setImageUri(Uri imageUri) {
        if (imageUri == null)
            return;

        mImageUri = imageUri;
        mUriTv.setText(imageUri.toString());
        mPinIt.setImageUri(mImageUri);
        try {
            Bitmap pinthumb = imageFromUri(this, mImageUri, 400, 300);
            mImage.setImageBitmap(pinthumb);
        } catch (IOException ignored) {
        }
    }

    /**
     * Get the image from Uri. If the image is too large, sample it.
     * @param context
     * @param uri
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public static Bitmap imageFromUri(Context context, Uri uri, int width,
        int height) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        InputStream input = resolver.openInputStream(uri);

        // Just get some info
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDensity = Bitmap.DENSITY_NONE;
        onlyBoundsOptions.inPurgeable = true;
        onlyBoundsOptions.inInputShareable = true;
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int xSample = 0, ySample = 0;
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        else {
            xSample = (int) Math.floor(onlyBoundsOptions.outWidth / width);
            ySample = (int) Math.floor(onlyBoundsOptions.outHeight / height);
        }

        // Decode
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmapOptions.inDensity = Bitmap.DENSITY_NONE;
        bitmapOptions.inPurgeable = true;
        bitmapOptions.inInputShareable = true;
        bitmapOptions.inSampleSize = Math.min(xSample, ySample);

        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return bitmap;
    }

    private OnClickListener mButtonClicked = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_SELECT);
        }
    };

}
