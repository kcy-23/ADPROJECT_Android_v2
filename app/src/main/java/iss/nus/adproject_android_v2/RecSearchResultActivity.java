package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecSearchResultActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener{

    Button closeDetailsBtn;
    ConstraintLayout entryDetails;
    TextView popup;
    String[] titles, authors, rFeelings, trackScores, imageUrls, descriptions;
    String goodResult;
    Map<String, Integer> feelings = new HashMap<>();
    Boolean readyFlag;
    NavigationBarView bottomNavigation;

    //for details
    ImageView detailImage, detailFeeling;
    TextView detailTitle, detailAuthor, detailTrack, detailDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_search_result);
        Intent intent = getIntent();
        titles = intent.getStringArrayExtra("titles");
        authors = intent.getStringArrayExtra("authors");
        rFeelings = intent.getStringArrayExtra("rFeelings");
        trackScores = intent.getStringArrayExtra("trackScores");
        imageUrls = intent.getStringArrayExtra("imageUrls");
        descriptions = intent.getStringArrayExtra("descriptions");
        goodResult = intent.getStringExtra("goodResult");
        System.out.println(goodResult);

        System.out.println("Inside RecSearchResultActivity");
        for (String s : titles){
            System.out.println("out " + s);
        }

        feelings.put("cry", R.drawable.emoji_crying);
        feelings.put("pensive", R.drawable.emoji_pensive);
        feelings.put("happy", R.drawable.emoji_happy);
        feelings.put("joy", R.drawable.emoji_joyful);

        ListView listView = findViewById(R.id.listView);
        if (listView != null) {
            listView.setAdapter(new RecSearchResultAdapter(this, titles, authors, rFeelings,
                    trackScores, imageUrls, feelings));
            listView.setOnItemClickListener(this);
        }

        closeDetailsBtn = findViewById(R.id.detailsCloseBtn);
        closeDetailsBtn.setOnClickListener(this);

        entryDetails = findViewById(R.id.detailsPopup);
        detailImage = findViewById(R.id.detailsImage);
        detailTitle = findViewById(R.id.detailsTitle);
        detailAuthor = findViewById(R.id.detailsAuthor);
        detailFeeling = findViewById(R.id.detailsFeeling);
        detailTrack = findViewById(R.id.detailsTrack);
        detailDesc = findViewById(R.id.detailsDesc);

        popup = findViewById(R.id.badResultPopup);
        if (goodResult.equalsIgnoreCase("false")){
            popup.setVisibility(View.VISIBLE);
            System.out.println("popup");
        }

        readyFlag = true;

        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        //set add selected
        bottomNavigation.setSelectedItemId(R.id.addMenu);

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.mealMenu:
                        Intent pastMeal = new Intent(getApplicationContext(), PastMealsActivity.class);
                        startActivity(pastMeal);
                        break;
                    case R.id.pathMenu:
                        Intent currentPath = new Intent(getApplicationContext(), ViewGoalActivity.class);
                        startActivity(currentPath);
                        break;
                    case R.id.addMenu:
                        finish();
                        startActivity(getIntent());
                        break;
                    case R.id.friendsMenu:
                        Intent friends = new Intent(getApplicationContext(), ManageSocialsActivity.class);
                        startActivity(friends);
                        break;
                    case R.id.settingsMenu:
                        Intent settings = new Intent(getApplicationContext(), SettingPage.class);
                        startActivity(settings);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == closeDetailsBtn){
            entryDetails.setVisibility(View.INVISIBLE);
            readyFlag = true;
            detailDesc.scrollTo(0, 0);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av,
                            View v, int pos, long id) {
        if (readyFlag){
            //details
            String imageApiUrl = getResources().getString(R.string.IP) + "/api/recommend/getEntryPic";
            String queryString = "?fileName=";
            Glide.with(v)
                    .load(imageApiUrl + queryString + imageUrls[pos])
                    .placeholder(R.drawable.no_img)
                    .into(detailImage);
            detailTitle.setText(titles[pos]);
            detailAuthor.setText("by " + authors[pos]);
            int feeling = feelings.get(rFeelings[pos].toLowerCase());
            detailFeeling.setImageResource(feeling);
            detailTrack.setText(trackScores[pos]);
            detailDesc.setText(descriptions[pos]);
            detailDesc.setMovementMethod(new ScrollingMovementMethod());

            entryDetails.setVisibility(View.VISIBLE);
            readyFlag = false;
        }
    }
}