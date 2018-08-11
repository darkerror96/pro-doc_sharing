package sem8.integrate.app.mainapp_1.Student;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.List;
import java.util.Locale;

import sem8.integrate.app.mainapp_1.Common_Upload_Storage;
import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.Login.Student_Login;
import sem8.integrate.app.mainapp_1.R;

public class Student_Download extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference db_ref;
    FirebaseUser user;

    Spinner sub_spinner, cat_spinner, file_spinner;
    Button get_btn;
    TextView list_tv;
    String cat_item, sub_item, file_item;

    ArrayList<String> fac_stu_subjects;
    ArrayAdapter adapter;

    int CLEAR_LIST = 0;

    ListView listView;
    List<Common_Upload_Storage> uploadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_student_download_activity);

        mAuth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        list_tv = (TextView) findViewById(R.id.list_tv);

        fac_stu_subjects = new ArrayList<>();
        fac_stu_subjects.add(DC.GENERAL);        //-----------GENERAL SUBJECT IS FOR ADMIN-----------

        uploadList = new ArrayList<>();

        //-------------------------------LIST VIEW OF PDF FILES-------------------------------------

        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Common_Upload_Storage upload = uploadList.get(position);

                //-----------CLICKED FILES WILL BE ADDED INTO DOWNLOADED FILE LIST------------------

                Long tsLong = System.currentTimeMillis()/1000;
                Long diff = 2147483647 - tsLong;

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(tsLong*1000);
                String date = DateFormat.format("dd MMM yyyy  hh:mm a", cal).toString();


                db_ref.child(DC.DEPARTMENT).child(user.getEmail().substring(0,2).toUpperCase().toString()).child(DC.SEMESTER).child(user.getEmail().substring(2,3).toString())
                        .child(DC.DIVISION).child(user.getEmail().substring(3,4).toUpperCase().toString()).child(DC.STUDENTS).child(user.getEmail().substring(4,7).toString())
                        .child(DC.DOWNLOADED_FILES).child(diff.toString()).child(DC.TITLE).setValue("File Name: - "+upload.getName()+"\nSubject Info: - "+sub_item+" ("+cat_item+")"+
                        "\nTime: - "+date);

                //-------------------GET CLICKED FILE URL AND OPEN THE FILE-------------------------

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(upload.getUrl()));
                startActivity(intent);
            }
        });

        //---------------------------------------SUBJECT SPINNER------------------------------------

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fac_stu_subjects);
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

        db_ref.child(DC.DEPARTMENT).child(user.getEmail().substring(0,2).toUpperCase().toString()).child(DC.SEMESTER).child(user.getEmail().substring(2,3).toString())
                .child(DC.DIVISION).child(user.getEmail().substring(3,4).toUpperCase().toString()).child(DC.FACULTY_SUBJECT).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String sname_ds = dataSnapshot.child(DC.SUBJECT_NAME).getValue().toString();

                fac_stu_subjects.add(sname_ds);
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

        //-------------------------------------CATEGORY SPINNER-------------------------------------

        cat_spinner = (Spinner)findViewById(R.id.cat_spinner);
        cat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cat_item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(FacultyUploadFile.this, cat_item+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        List<String> categories = new ArrayList<String>();
        categories.add("Book");
        categories.add("Exam Paper");
        categories.add("Fee Notice");
        categories.add("Notes");
        categories.add("Other");
        categories.add("Syllabus");
        categories.add("Time Table");

        ArrayAdapter<String> dataAdapterCat = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cat_spinner.setAdapter(dataAdapterCat);

        //-------------------------------------FILE TYPE SPINNER------------------------------------

        file_spinner = (Spinner)findViewById(R.id.file_spinner);
        file_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                file_item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(FacultyUploadFile.this, file_item+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final List<String> file_types = new ArrayList<String>();
        file_types.add("Image");
        file_types.add("PDF");

        ArrayAdapter<String> dataAdapterFile = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, file_types);
        dataAdapterFile.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        file_spinner.setAdapter(dataAdapterFile);

        //---------------------------------GET FILE LIST BUTTON-------------------------------------

        get_btn = (Button) findViewById(R.id.get_btn);
        get_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(StudentDownload.this, "Get List of Available Files", Toast.LENGTH_SHORT).show();
                uploadList.clear();

                if (CLEAR_LIST == 0)
                {
                    db_ref.child(DC.DEPARTMENT).child(user.getEmail().substring(0,2).toUpperCase().toString()).child(DC.SEMESTER).child(user.getEmail().substring(2,3).toString())
                            .child(DC.DIVISION).child(user.getEmail().substring(3,4).toUpperCase().toString()).child(DC.UPLOADED_FILES).child(sub_item).child(cat_item).child(file_item)
                            .addValueEventListener(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                    {
                                        Common_Upload_Storage upload = postSnapshot.getValue(Common_Upload_Storage.class);
                                        uploadList.add(upload);
                                    }

                                    //Toast.makeText(Student_Download.this,uploadList.size()+"", Toast.LENGTH_SHORT).show();

                                    if (uploadList.size() > 0)
                                    {
                                        String[] uploads = new String[uploadList.size()];
                                        for (int i = 0; i < uploads.length; i++)
                                        {
                                            uploads[i] = uploadList.get(i).getName();
                                        }
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, uploads);
                                        listView.setAdapter(adapter);

                                        CLEAR_LIST = 1;
                                        list_tv.setBackgroundColor(Color.parseColor("#50cbe1"));
                                        listView.setVisibility(View.VISIBLE);
                                        sub_spinner.setEnabled(false);
                                        cat_spinner.setEnabled(false);
                                        file_spinner.setEnabled(false);
                                        get_btn.setText("Clear List");
                                    }
                                    else
                                    {
                                        list_tv.setBackgroundColor(Color.parseColor("#ffffff"));
                                        listView.setVisibility(View.INVISIBLE);
                                        Toast.makeText(Student_Download.this, "No File available...", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                }
                else
                {
                    CLEAR_LIST = 0;
                    list_tv.setBackgroundColor(Color.parseColor("#ffffff"));
                    listView.setVisibility(View.INVISIBLE);
                    sub_spinner.setEnabled(true);
                    cat_spinner.setEnabled(true);
                    file_spinner.setEnabled(true);
                    get_btn.setText("Get List");
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
                Intent i = new Intent(Student_Download.this,Student_Login.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent backtoHome = new Intent(Student_Download.this,Student_Home.class);
        startActivity(backtoHome);
    }
}
