package de.yafp.jimgo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;
import java.util.Random;

public class Jimgo extends AppCompatActivity {

    private static final String TAG = "Jimgo";
    private final Random rnd = new Random();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_jimgo);

        // IMMERSIVE mode
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        // | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        final ImageView img = findViewById(R.id.image);
        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                on_next_image();
            }
        });

        // Log Firebase Event
        logFireBaseEvent("jimgo_app_Launch");

        // Show logo in actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_icon_actionbar);

        //final View v = this.findViewById(android.R.id.content).getRootView();
        on_next_image();
    }



    /**
     * write firebase event
     *
     * @param message
     */
    private void logFireBaseEvent(String message) {
        Log.d(TAG, "F: logFireBaseEvent");
        Bundle params = new Bundle();
        params.putString(message, "1");
        mFirebaseAnalytics.logEvent(message, params);
    }



    /**
     * loading next random image
     */
    private void on_next_image() {
        Log.d(TAG, "F: on_next_image");

        // imageview
        final ImageView img = findViewById(R.id.image);

        // -----------------------------------------------------------------------------------------
        // Part 1 - Select a new random image and display it
        // -----------------------------------------------------------------------------------------
        final String random_image_name = "img_" + rnd.nextInt(187); // last img_x+1 (starting by 0)
        Log.d(TAG, "new random image: "+random_image_name);

        // get resource ID of random image
        int drawableResourceId = this.getResources().getIdentifier(random_image_name, "drawable", this.getPackageName());
        // load image into imageview
        img.setImageResource(drawableResourceId);

        // Fade In
        //
        AlphaAnimation animation2 = new AlphaAnimation(0.0f, 1.0f);
        animation2.setDuration(1000);
        //animation1.setStartOffset(5000);
        animation2.setRepeatCount(0);
        animation2.setFillAfter(true);
        img.startAnimation(animation2);



        // -----------------------------------------------------------------------------------------
        // Part 2 - Get animal name of selected image and display it in TextView and actionbar
        // -----------------------------------------------------------------------------------------

        // get ressource id of animal string
        int resId = getResources().getIdentifier(random_image_name, "string", getPackageName());

        // show animal name in actionbar
        Objects.requireNonNull(getSupportActionBar()).setTitle((Html.fromHtml("<font color=\"#FFFFFF\">&nbsp;" + getResources().getString(resId) + "</font>")));


        // -----------------------------------------------------------------------------------------
        // Part 3 - Adjusting colors of UI
        // -----------------------------------------------------------------------------------------
        Log.d(TAG, "generating color palette");

        // image to bitmap
        Bitmap icon = BitmapFactory.decodeResource(getResources(), drawableResourceId );

        // extract colors from bitmap
        Palette palette = Palette.from(icon).generate();
        int defaultColor = 0x000000;

        // Returns the most vibrant color in the palette as an RGB packed int.
        int vibrant = palette.getVibrantColor(defaultColor);

        // Returns a light and vibrant color from the palette as an RGB packed int.
        int vibrantLight = palette.getLightVibrantColor(defaultColor);

        // Returns a dark and vibrant color from the palette as an RGB packed int.
        int vibrantDark = palette.getDarkVibrantColor(defaultColor);

        // Returns a muted color from the palette as an RGB packed int.
        int muted = palette.getMutedColor(defaultColor);

        // Returns a muted and light color from the palette as an RGB packed int.
        int mutedLight = palette.getLightMutedColor(defaultColor);

        // Returns a muted and dark color from the palette as an RGB packed int.
        int mutedDark = palette.getDarkMutedColor(defaultColor);

        // Returns the color of the dominant swatch from the palette, as an RGB packed int.
        int dominant = palette.getDominantColor(defaultColor);

        Log.d(TAG, "Vibrant: "+Integer.toString(vibrant));
        Log.d(TAG, "VibrantLight: "+Integer.toString(vibrantLight));
        Log.d(TAG, "VibrantDark: "+Integer.toString(vibrantDark));
        Log.d(TAG, "Muted: "+Integer.toString(muted));
        Log.d(TAG, "MutedLight: "+Integer.toString(mutedLight));
        Log.d(TAG, "MutedDark: "+Integer.toString(mutedDark));
        Log.d(TAG, "Dominant: "+Integer.toString(dominant));

        // Define primary & secondary color
        //
        //String primarycolor; // used for status bar & actionbar
        String secondaryColor; // used as general background color

        // Secondary Color (Background)
        //
        if(muted != 0) {
            secondaryColor = create_hex_color(muted);
        }
        else if(mutedDark != 0) {
            secondaryColor = create_hex_color(mutedDark);
        }
        else if(mutedLight != 0) {
            secondaryColor = create_hex_color(mutedLight);
        }
        else {
            secondaryColor = create_hex_color(dominant);
        }

        Log.d(TAG, "Secondary Color: "+secondaryColor);
        Log.d(TAG, "colorizing view background with: "+secondaryColor);

        // Colorizing view
        View v = this.findViewById(android.R.id.content).getRootView();
        v.setBackgroundColor(Color.parseColor(secondaryColor));

        // Log Firebase Event - image name
        logFireBaseEvent("jimgo_"+random_image_name);

        // Log Firebase Event
        logFireBaseEvent("jimgo_next_image");
    }


    /**
     * generate hex color from int
     *
     * @param color
     * @return
     */
    private String create_hex_color(int color) {
        Log.d(TAG, "F: create_hex_color");

        Log.d(TAG, "Input color int: "+Integer.toString(color));
        String hex = Integer.toHexString(color);

        if(color != 0){
            hex = hex.substring(2,8);
            hex = "#"+hex;
            Log.d(TAG, "Generated HEX color: "+hex);
        }
        else {
            Log.w(TAG, "Int color was empty. Unable to generate hex color. Using fallback DimGray");
            hex = "#696969"; // set fallback value

            // Log Firebase Event
            logFireBaseEvent("jimgo_empty_int_color");
        }
        return hex;
    }
}
