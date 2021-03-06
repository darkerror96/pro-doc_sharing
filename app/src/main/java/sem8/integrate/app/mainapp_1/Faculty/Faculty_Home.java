package sem8.integrate.app.mainapp_1.Faculty;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import sem8.integrate.app.mainapp_1.Common_Firebase_WebView_Activity;
import sem8.integrate.app.mainapp_1.Login.Student_Login;
import sem8.integrate.app.mainapp_1.R;

public class Faculty_Home extends AppCompatActivity {

    com.github.clans.fab.FloatingActionButton menu1,menu2;

    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_faculty_home_activity);

        contextOfApplication = getApplicationContext();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        menu1 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.addnotice) ;
        menu2 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.uploadfile) ;

        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Toast.makeText(FacultyHome.this , "Add Notice", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Faculty_Home.this, Faculty_Upload_Notice.class);
                startActivity(i);
            }
        });

        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Toast.makeText(FacultyHome.this , "Upload File", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Faculty_Home.this, Faculty_Upload_File.class);
                startActivity(i);
            }
        });

        loadFragment(new F_Notices_Fragment());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.notice:
                    //Toast.makeText(MainActivity.this, "Cart", Toast.LENGTH_SHORT).show();
                    fragment = new F_Notices_Fragment();
                    loadFragment(fragment);
                    return true;

                case R.id.ufiles:
                    //Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                    fragment = new F_Uploaded_Files_Fragment();
                    loadFragment(fragment);
                    return true;

                case R.id.checkprofile:
                    //Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    fragment = new F_Check_Profile_Fragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.common_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.firebase:
                Toast.makeText(getApplicationContext(), "Opening Firebase Console...", Toast.LENGTH_LONG).show();
                Intent f_webview = new Intent(Faculty_Home.this,Common_Firebase_WebView_Activity.class);
                startActivity(f_webview);
                return true;

            case R.id.logout:
                //Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Faculty_Home.this,Student_Login.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {}
}
