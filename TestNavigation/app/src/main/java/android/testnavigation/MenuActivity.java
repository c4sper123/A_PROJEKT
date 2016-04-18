package android.testnavigation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.testnavigation.fragments.DataPassListener;
import android.testnavigation.fragments.MyOffersWindow;
import android.testnavigation.fragments.OfferDetailsWindow;
import android.testnavigation.fragments.OffersWindow;
import android.testnavigation.fragments.SummerOffersWindow;
import android.testnavigation.fragments.WinterOffersWindow;
import android.util.Log;
import android.support.design.widget.NavigationView;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DataPassListener {
    private Toolbar toolbar;
    private TextView titleName;
    private ImageButton refreshBtn;
    private String userId;
    private String userName;
    private String userMail;
    private AlertDialog.Builder myAlert;
    private AlertDialog.Builder myAlert2;


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

        myAlert = new AlertDialog.Builder(this);
        myAlert2 = new AlertDialog.Builder(this);

        titleName = (TextView) findViewById(R.id.textView5);
        titleName.setText("PONUKY");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);

        TextView  txtUserName = (TextView) v.findViewById(R.id.txtUserName);
        txtUserName.setText(userName);
        TextView txtUserMail = (TextView) v.findViewById(R.id.txtUserMail);
        txtUserMail.setText(userMail);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.info) {
            myAlert.setMessage("Zľavový portál v1.0\n\nby Adrián Kasperkevič & Tomáš Vrtal\n\nMTAA © 2016").create();
            myAlert.setTitle("Info");
            myAlert.setIcon(R.drawable.ic_info_black_24dp);
            myAlert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            myAlert.show();

        } else if (id == R.id.bug_report) {
            final EditText input = new EditText(this);
            input.setHint("Detaily o chybe...");
            myAlert2.setView(input);
            myAlert2.setTitle("Bug report");
            myAlert2.setIcon(R.mipmap.ic_bug_black);
            myAlert2.setNegativeButton("Nahlásiť", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            myAlert2.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_offer) {
           // titleName.setText("PRIDAŤ PONUKU");
        } else if (id == R.id.offers) {
            //titleName.setText("PONUKY");
            android.support.v4.app.Fragment fragment = new OffersWindow();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container_omg, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.my_offers) {
          //  titleName.setText("MOJE PONUKY");
            android.support.v4.app.Fragment fragment = new MyOffersWindow();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            fragment.setArguments(bundle);
            ft.replace(R.id.container_omg, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.summer) {
           // titleName.setText("LETO");
            android.support.v4.app.Fragment fragment = new SummerOffersWindow();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container_omg, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.winter) {
            android.support.v4.app.Fragment fragment = new WinterOffersWindow();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container_omg, fragment);
            ft.addToBackStack(null);
            ft.commit();
           // titleName.setText("ZIMA");
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
        ft.addToBackStack(null);
        ft.commit();
    }


}
