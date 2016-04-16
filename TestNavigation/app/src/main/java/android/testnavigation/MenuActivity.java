package android.testnavigation;

import android.app.Fragment;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OffersWindow.DataPassListener {
    private Toolbar toolbar;
    private TextView titleName;
    private ImageButton refreshBtn;
    private String userId;
    private String userName;
    private String userMail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
        userMail = getIntent().getStringExtra("userMail");
        Log.d("WAU",userMail + " " + userName);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        android.support.v4.app.Fragment fragment = new OffersWindow();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_omg, fragment);
        ft.commit();

//        View v = findViewById(R.id.nav_view);
//        TextView  txtUserName = (TextView) v.findViewById(R.id.txtUserName);
//        txtUserName.setText(userName);
//        TextView txtUserMail = (TextView) v.findViewById(R.id.txtUserMail);
//        txtUserMail.setText(userMail);
        //nejde


        titleName = (TextView) findViewById(R.id.textView5);
        titleName.setText("PONUKY");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //menu.getItem(R.id.details).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.info) {

        } else if (id == R.id.bug_report) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_offer) {
            titleName.setText("PRIDAŤ PONUKU");
        } else if (id == R.id.offers) {
            titleName.setText("PONUKY");
            android.support.v4.app.Fragment fragment = new OffersWindow();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container_omg, fragment);
            ft.commit();
        } else if (id == R.id.my_offers) {
            titleName.setText("MOJE PONUKY");
            android.support.v4.app.Fragment fragment = new MyOffersWindow();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            fragment.setArguments(bundle);
            ft.replace(R.id.container_omg, fragment);
            ft.commit();
        } else if (id == R.id.summer) {
            titleName.setText("LETO");
        } else if (id == R.id.winter) {
            titleName.setText("ZIMA");
        } else if (id == R.id.logout) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void passData(String data) {
        android.support.v4.app.Fragment fragment = new OfferDetailsWindow();
        Bundle args = new Bundle();
        args.putString(OfferDetailsWindow.DATA_RECEIVE, data);
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_omg, fragment);
        ft.commit();
    }
}
