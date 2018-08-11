package sem8.integrate.app.mainapp_1.Faculty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.Login.Student_Login;
import sem8.integrate.app.mainapp_1.R;

public class Faculty_Upload_Notice extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference db_ref;
    FirebaseUser user;

    ArrayList<String> fac_subjects;
    ArrayAdapter adapter;
    HashMap<String,String> append_map = new HashMap<>();

    Button uploadnotice_btn;
    EditText title_et, msg_et;
    int FILE_NOTICE = 0;

    Spinner sub_spinner;
    String sub_item, bundle_sub;
    String FAC_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_faculty_upload_notice_activity);

        title_et = (EditText)findViewById(R.id.title_et);
        msg_et = (EditText)findViewById(R.id.msg_et);

        Bundle b = getIntent().getExtras();
        if (b != null)
        {
            FILE_NOTICE = 1;
            bundle_sub = b.get("Bundle_Sub").toString();
            title_et.setText(b.get(DC.TITLE).toString());
            msg_et.setText(b.get(DC.MESSAGE).toString());
        }

        mAuth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        fac_subjects = new ArrayList<>();

        //-----------------------SUBJECT SPINNER----------------------

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fac_subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sub_spinner = (Spinner)findViewById(R.id.sub_spinner);
        sub_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sub_item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(FacultyUploadFile.this, sub_item+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.COMMON_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FAC_NAME = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.SUBJECT_NAMES)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String sname_ds = dataSnapshot.child(DC.SUBJECT_NAME).getValue().toString();
                        String sem_ds = dataSnapshot.child(DC.SEMESTER).getValue().toString();
                        String div_ds = dataSnapshot.child(DC.DIVISION).getValue().toString();
                        String dept_ds = dataSnapshot.child(DC.DEPARTMENT).getValue().toString();
                        String append_info = dept_ds+sem_ds+div_ds;

                        append_map.put(sname_ds, append_info);

                        fac_subjects.add(sname_ds);
                        sub_spinner.setAdapter(adapter);
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

        //--------------------------------NOTICE UPLOAD BUTTON--------------------------------------

        uploadnotice_btn = (Button)findViewById(R.id.uploadnotice_btn);
        uploadnotice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (title_et.getText().length() > 0 && title_et.getText().length() < 21 && msg_et.getText().length() > 0)
                {
                    if (FILE_NOTICE == 1 && (!bundle_sub.equals(sub_item)))
                    {
                        Toast.makeText(Faculty_Upload_Notice.this, "Select "+bundle_sub+" as Subject...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Iterator hashMap = append_map.keySet().iterator();
                        while (hashMap.hasNext()) {
                            String key = (String) hashMap.next();

                            if (sub_item.equals(key)) {
                                String value = (String) append_map.get(key);
                                //Toast.makeText(Faculty_Upload_Notice.this, value+"", Toast.LENGTH_SHORT).show();

                                Long tsLong = System.currentTimeMillis()/1000;

                                Long diff = 2147483647 - tsLong;

                                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                cal.setTimeInMillis(tsLong*1000);
                                String date = DateFormat.format("dd MMM yyyy  hh:mm a", cal).toString();

                                db_ref.child(DC.DEPARTMENT).child(value.substring(0,2).toString()).child(DC.SEMESTER).child(value.substring(2,3).toString())
                                        .child(DC.DIVISION).child(value.substring(3,4).toString()).child(DC.NOTICES).child(diff.toString()).child(DC.TITLE)
                                        .setValue("Title: - "+title_et.getText().toString()+"\nMessage: - "+msg_et.getText().toString()+"\nUploaded by: - "+FAC_NAME+"\nTime: - "+date);

                                db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.FACULTY_NOTICES).child(diff.toString()).child(DC.TITLE)
                                        .setValue("Title: - "+title_et.getText().toString()+"\nMessage: - "+msg_et.getText().toString()+"\nTime: - "+date);

                                Toast.makeText(Faculty_Upload_Notice.this, "Notice Added...", Toast.LENGTH_SHORT).show();
                                title_et.requestFocus();
                                title_et.setText("");
                                msg_et.setText("");
                            }
                        }
                    }
                }
                else if (title_et.getText().length() == 0)
                {
                    Toast.makeText(Faculty_Upload_Notice.this, "Add Title...", Toast.LENGTH_SHORT).show();
                }
                else if (msg_et.getText().length() == 0)
                {
                    Toast.makeText(Faculty_Upload_Notice.this, "Add Message...", Toast.LENGTH_SHORT).show();
                }
                else if (title_et.getText().length() >= 21)
                {
                    Toast.makeText(Faculty_Upload_Notice.this, "Minimize the Title Content to 20 characters...", Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(Faculty_Upload_Notice.this,Student_Login.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent backToHome = new Intent(Faculty_Upload_Notice.this, Faculty_Home.class);
        startActivity(backToHome);
    }
}
