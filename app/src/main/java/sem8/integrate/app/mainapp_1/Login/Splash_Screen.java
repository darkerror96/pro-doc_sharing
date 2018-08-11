package sem8.integrate.app.mainapp_1.Login;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import sem8.integrate.app.mainapp_1.R;

public class Splash_Screen extends AppCompatActivity {

    private static int time_out = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_splash_screen_activity);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash_Screen.this,Student_Login.class);
                startActivity(i);
            }
        },time_out);
    }
}
