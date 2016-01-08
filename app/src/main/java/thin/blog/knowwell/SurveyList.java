package thin.blog.knowwell;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/*
* SurveyList Activity is the main UI Activity of the Application
* This activity contains the Navigation Drawer and takes care of fragment replacement when options of Navigation View are selected
* */
public class SurveyList extends AppCompatActivity implements SurveyListFragment.OnFragmentInteractionListener {
    @Bind(R.id.app_bar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, MODE_PRIVATE);
        View v = navigationView.getHeaderView(0);
        TextView name = (TextView) v.findViewById(R.id.name);
        TextView email = (TextView) v.findViewById(R.id.email);
        CircleImageView profilePhoto = (CircleImageView) v.findViewById(R.id.profile_photo);
        ImageView coverPhoto = (ImageView) v.findViewById(R.id.cover_photo);

        String userName = sharedPreferences.getString(Constants.USER_DATA_NAME, "");
        String userEmail = sharedPreferences.getString(Constants.USER_DATA_EMAIL, "");
        String userProfilePictureUrl = sharedPreferences.getString(Constants.USER_DATA_GOOGLE_PROFILE_PHOTO, "");
        String userCoverPictureUrl = sharedPreferences.getString(Constants.USER_DATA_GOOGLE_COVER_PHOTO, "R.drawable.background_material");
        name.setText(userName);
        email.setText(userEmail);
        if (!userProfilePictureUrl.contentEquals("")) {
            Picasso.with(this).load(userProfilePictureUrl).into(profilePhoto);
        }
        Picasso.with(this).load(userCoverPictureUrl).placeholder(R.drawable.background_material).error(R.drawable.background_material).into(coverPhoto);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setEnabled(true);
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.my_surveys:
                        SurveyListFragment mySurvey = (SurveyListFragment) getSupportFragmentManager().findFragmentByTag("MY_SURVEYS");
                        if (mySurvey == null) {
                            mySurvey = SurveyListFragment.newInstance(2);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_layout, mySurvey, "MY_SURVEYS").commit();
                        break;
                    case R.id.surveys:
                        SurveyListFragment survey = (SurveyListFragment) getSupportFragmentManager().findFragmentByTag("SURVEYS");
                        if (survey == null) {
                            survey = SurveyListFragment.newInstance(1);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_layout, survey, "SURVEYS").commit();
                        break;

                    case R.id.logout:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constants.SUCCESSFUL_LOGIN_HISTORY, false);
                        editor.apply();
                        startActivity(new Intent(SurveyList.this, LoginActivity.class));
                        finish();
                        break;
                }

                return true;
            }
        });

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        SurveyListFragment survey = (SurveyListFragment) getSupportFragmentManager().findFragmentByTag("SURVEYS");
        if (survey == null) {
            survey = SurveyListFragment.newInstance(1);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_layout, survey, "SURVEYS").commit();
    }

    @Override
    public void onFragmentInteraction() {
        return;
    }
}
