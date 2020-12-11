package com.sovereign.budgetmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sovereign.budgetmanager.login.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    AppBarConfiguration appBarConfiguration;
    DrawerLayout drawer;

    FirebaseAuth mAuth;
    FirebaseUser user;

    TextView username, userEmail, settingsLogin;
    ImageView pfp;
    Button login;
    MenuItem logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        drawer = findViewById(R.id.drawer);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_ledger, R.id.navigation_stats, R.id.navigation_goals)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        NavigationView navigationView = findViewById(R.id.drawer_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);
        username = header.findViewById(R.id.username);
        userEmail = header.findViewById(R.id.userEmail);
        settingsLogin = header.findViewById(R.id.settingsLogin);
        pfp = header.findViewById(R.id.pfp);

        logout = navigationView.getMenu().getItem(3);

        if(mAuth.getCurrentUser()==null){
            username.setText("Demo User");
            userEmail.setText("demouser@budgetmanager");
            settingsLogin.setVisibility(View.VISIBLE);
            settingsLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =  new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            logout.setVisible(false);
        } else {
            user = mAuth.getCurrentUser();
            username.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            settingsLogin.setVisibility(View.GONE);
            if (settingsLogin.hasOnClickListeners()){
                settingsLogin.setOnClickListener(null);
            }
            logout.setVisible(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_Settings:
                Log.i("Nav", "Settings");
                break;
            case R.id.nav_About:
                Log.i("Nav", "About");
                break;

            case R.id.nav_logout:
                Log.i("Nav", "Logout");
                mAuth.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        drawer.close();
        return true;
    }
}