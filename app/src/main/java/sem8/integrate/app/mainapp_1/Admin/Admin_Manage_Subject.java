package sem8.integrate.app.mainapp_1.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sem8.integrate.app.mainapp_1.Common_Firebase_WebView_Activity;
import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.Login.Student_Login;
import sem8.integrate.app.mainapp_1.R;

public class Admin_Manage_Subject extends AppCompatActivity {

    DatabaseReference db_ref;

    WebView firebase_webview;

    String fac_key, fac_name;

    Button add_sub_btn, del_sub_btn;
    EditText dept_et, sem_et, div_et, sub_et;

    int DEL_FLAG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_admin_manage_subject_activity);

        Bundle b = getIntent().getExtras();
        if (b != null)
        {
            fac_key = (String) b.get("FAC_KEY");
            fac_name = (String) b.get("NAME");
        }

        db_ref = FirebaseDatabase.getInstance().getReference();

        firebase_webview = (WebView)findViewById(R.id.firebase_webview);
        firebase_webview.setWebViewClient(new WebViewClient());
        firebase_webview.getSettings().setJavaScriptEnabled(true);
        firebase_webview.loadUrl("https://console.firebase.google.com/");

        dept_et = (EditText)findViewById(R.id.dept_et);
        dept_et.requestFocus();
        sem_et = (EditText)findViewById(R.id.sem_et);
        div_et = (EditText)findViewById(R.id.div_et);
        sub_et = (EditText)findViewById(R.id.sub_et);

        add_sub_btn = (Button)findViewById(R.id.add_sub_btn);
        add_sub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (dept_et.getText().length() == 2)
                {
                    if (dept_et.getText().charAt(0) > 64 && dept_et.getText().charAt(0) < 91 && dept_et.getText().charAt(1) > 64 && dept_et.getText().charAt(1) < 91)
                    {
                        if (sem_et.getText().length() != 0 && Integer.parseInt(sem_et.getText().toString()) > 0 && Integer.parseInt(sem_et.getText().toString()) < 9)
                        {
                            if (div_et.getText().length() == 1)
                            {
                                if (div_et.getText().charAt(0) > 64 && div_et.getText().charAt(0) < 91)
                                {
                                    if (sub_et.getText().length() != 0)
                                    {
                                        String fac_sub_key = db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES).push().getKey();

                                        String key = db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString()).child(DC.DIVISION)
                                                .child(div_et.getText().toString()).child(DC.FACULTY_SUBJECT).push().getKey();

                                        db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.SUBJECT_NAME)
                                                .setValue(sub_et.getText().toString());
                                        db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.SEMESTER)
                                                .setValue(sem_et.getText().toString());
                                        db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.DIVISION)
                                                .setValue(div_et.getText().toString());
                                        db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.DEPARTMENT)
                                                .setValue(dept_et.getText().toString());

                                        //---------------------------------------SAVING KEY OF SUBJECT ADDED INTO THE CLASS OF STUDENTS------------------------

                                        db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.SUBJECT_KEY)
                                                .setValue(key);
                                        db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.FACULTY_SUBJECT_KEY)
                                                .setValue(fac_sub_key);

                                        //---------------------------------------------SUBJECT ADDED INTO CLASS----------------------------------------

                                        db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString()).child(DC.DIVISION)
                                                .child(div_et.getText().toString()).child(DC.FACULTY_SUBJECT).child(key).child(DC.COMMON_NAME)
                                                .setValue(fac_name);

                                        db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString()).child(DC.DIVISION)
                                                .child(div_et.getText().toString()).child(DC.FACULTY_SUBJECT).child(key).child(DC.SUBJECT_NAME)
                                                .setValue(sub_et.getText().toString());

                                        //Toast.makeText(FacultyRegister.this, dept_et.getText().toString()+"\n"+sem_et.getText()+"\n"+div_et.getText()+"\n"+sub_et.getText().toString(), Toast.LENGTH_SHORT).show();
                                        Toast.makeText(Admin_Manage_Subject.this, "Subject Added...", Toast.LENGTH_SHORT).show();

                                        dept_et.setText("");
                                        dept_et.requestFocus();
                                        sem_et.setText("");
                                        div_et.setText("");
                                        sub_et.setText("");
                                    }
                                    else
                                    {
                                        Toast.makeText(Admin_Manage_Subject.this, "Enter Subject Name in short...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(Admin_Manage_Subject.this, "Check Division again...", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(Admin_Manage_Subject.this, "Length of Division should be 1...", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(Admin_Manage_Subject.this, "Range of Semester should be in between 1 to 8...", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Admin_Manage_Subject.this, "Check Department again...", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Admin_Manage_Subject.this, "Length of Department should be 2...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        del_sub_btn = (Button)findViewById(R.id.del_sub_btn);
        del_sub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (DEL_FLAG == 0)
                {
                    DEL_FLAG++;
                    Toast.makeText(Admin_Manage_Subject.this, "Confirm once again...", Toast.LENGTH_SHORT).show();
                }
                else if (DEL_FLAG == 1)
                {
                    db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            String sem_ds = dataSnapshot.child(DC.SEMESTER).getValue().toString();
                            String div_ds = dataSnapshot.child(DC.DIVISION).getValue().toString();
                            String dept_ds = dataSnapshot.child(DC.DEPARTMENT).getValue().toString();
                            String sub_key_ds = dataSnapshot.child(DC.SUBJECT_KEY).getValue().toString();
                            String sub_name_ds = dataSnapshot.child(DC.SUBJECT_NAME).getValue().toString();
                            String fac_sub_key_ds = dataSnapshot.child(DC.FACULTY_SUBJECT_KEY).getValue().toString();

                            if (dept_et.getText().toString().equals(dept_ds) && sem_et.getText().toString().equals(sem_ds) && div_et.getText().toString().equals(div_ds)
                                    && sub_et.getText().toString().equals(sub_name_ds))
                            {
                                db_ref.child(DC.DEPARTMENT).child(dept_ds).child(DC.SEMESTER).child(sem_ds).child(DC.DIVISION).child(div_ds).child(DC.FACULTY_SUBJECT)
                                        .child(sub_key_ds).removeValue();

                                db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES).child(fac_sub_key_ds).removeValue();

                                Toast.makeText(Admin_Manage_Subject.this, "Subject Deleted...", Toast.LENGTH_SHORT).show();
                                DEL_FLAG = 0;
                                dept_et.setText("");
                                dept_et.requestFocus();
                                sem_et.setText("");
                                div_et.setText("");
                                sub_et.setText("");
                            }
                            else
                            {
                                DEL_FLAG = 2;
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {}

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                else if (DEL_FLAG == 2)
                {
                    DEL_FLAG = 0;
                    Toast.makeText(Admin_Manage_Subject.this, "Incorrect Subject Details entered...", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
                Intent i = new Intent(Admin_Manage_Subject.this,Student_Login.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(Admin_Manage_Subject.this, Admin_Home.class);
        startActivity(i);
    }
}
