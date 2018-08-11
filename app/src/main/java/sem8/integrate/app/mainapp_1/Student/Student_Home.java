package sem8.integrate.app.mainapp_1.Student;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import sem8.integrate.app.mainapp_1.Login.Student_Login;
import sem8.integrate.app.mainapp_1.R;

public class Student_Home extends AppCompatActivity {

    FloatingActionButton download;

    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_student_home_actitvity);

        contextOfApplication = getApplicationContext();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        download = (FloatingActionButton)findViewById(R.id.download_fab);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(StudentHome.this, "Cloud Download...", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Student_Home.this,Student_Download.class);
                startActivity(i);
            }
        });

        loadFragment(new S_Notices_Fragment());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.notice:
                    //Toast.makeText(MainActivity.this, "Cart", Toast.LENGTH_SHORT).show();
                    fragment = new S_Notices_Fragment();
                    loadFragment(fragment);
                    return true;

                case R.id.dfiles:
                    //Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                    fragment = new S_Downloaded_Files_Fragment();
                    loadFragment(fragment);
                    return true;

                case R.id.checkprofile:
                    //Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    fragment = new S_Check_Profile_Fragment();
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
        getMenuInflater().inflate(R.menu.common_logout_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.logout:
                //Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Student_Home.this,Student_Login.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {}
}
