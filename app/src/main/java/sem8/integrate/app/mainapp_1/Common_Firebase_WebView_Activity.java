package sem8.integrate.app.mainapp_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import sem8.integrate.app.mainapp_1.Login.Student_Login;
import sem8.integrate.app.mainapp_1.Student.Student_Download;

public class Common_Firebase_WebView_Activity extends AppCompatActivity {

    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_firebase_webview_activity);

        wv = (WebView)findViewById(R.id.f_wv);
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("https://console.firebase.google.com/");

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
                Intent i = new Intent(Common_Firebase_WebView_Activity.this,Student_Login.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
