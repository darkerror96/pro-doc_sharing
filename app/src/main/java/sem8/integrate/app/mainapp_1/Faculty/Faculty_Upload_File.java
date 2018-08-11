package sem8.integrate.app.mainapp_1.Faculty;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import sem8.integrate.app.mainapp_1.Common_Upload_Storage;
import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.Login.Student_Login;
import sem8.integrate.app.mainapp_1.R;

public class Faculty_Upload_File extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
    StorageReference st_ref = FirebaseStorage.getInstance().getReference();
    Uri filePath;
    UploadTask uploadTask;

    ArrayList<String> fac_subjects;
    ArrayAdapter adapter;
    HashMap<String,String> append_map = new HashMap<>();

    Spinner sub_spinner, cat_spinner, file_spinner;
    Button get_btn;
    ImageButton del_upfile_ibtn;
    TextView sumfile_tv;

    ScrollView sc_id;
    RelativeLayout rl_id;

    EditText filename_et;
    TextView cats_tv, filetype_tv, dept_tv, sem_tv, div_tv, subs_tv;
    String sub_item, cat_item, file_item, filename, value, uploadFileName;
    int FILE_SELECT = 7, FLAG = 0;

    Snackbar done_sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_faculty_upload_file_activity);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        fac_subjects = new ArrayList<>();

        filename_et = (EditText) findViewById(R.id.filename_et);
        cats_tv = (TextView)findViewById(R.id.cats_tv);
        filetype_tv = (TextView)findViewById(R.id.filetype_tv);
        dept_tv = (TextView)findViewById(R.id.dept_tv);
        sem_tv = (TextView)findViewById(R.id.sem_tv);
        div_tv = (TextView)findViewById(R.id.div_tv);
        subs_tv = (TextView)findViewById(R.id.subs_tv);

        sc_id = (ScrollView)findViewById(R.id.sc_id);
        rl_id = (RelativeLayout)findViewById(R.id.rl_id);

        sumfile_tv = (TextView)findViewById(R.id.sumfile_tv);

        //-----------------------SNACKBAR-----------------------------

        done_sc = Snackbar.make(rl_id,"File Uploading...",Snackbar.LENGTH_INDEFINITE)
                .setAction("Cancel",new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {

                        //use firebase upload cancel() method to cancel file upload
                        uploadTask.cancel();

                        Toast.makeText(Faculty_Upload_File.this, "Uploading Canceled...", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Faculty_Upload_File.this, Faculty_Upload_File.class);
                        startActivity(i);
                    }
                });

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

        //-----------------------CATEGORY SPINNER---------------------

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
        categories.add("Notes");
        categories.add("Syllabus");

        ArrayAdapter<String> dataAdapterCat = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cat_spinner.setAdapter(dataAdapterCat);

        //---------------------FILE TYPE SPINNER---------------------

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

        //----------------------------Delete Upload File ImageButton---------------------

        del_upfile_ibtn = (ImageButton)findViewById(R.id.del_upfile_ibtn);
        del_upfile_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Faculty_Upload_File.this, Faculty_Upload_File.class);
                startActivity(i);
            }
        });

        //----------------------------------Upload Button--------------------------------

        get_btn = (Button)findViewById(R.id.get_btn);
        get_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(StudentDownload.this, "Select File", Toast.LENGTH_SHORT).show();

                if (FLAG == 0)
                {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    if (file_item.equals("Image"))
                    {
                        intent.setType("image/*");
                        Toast.makeText(Faculty_Upload_File.this, "Select Image...", Toast.LENGTH_LONG).show();
                    }
                    else if (file_item.equals("PDF"))
                    {
                        intent.setType("application/pdf");
                        Toast.makeText(Faculty_Upload_File.this, "Select PDF...", Toast.LENGTH_LONG).show();
                    }
                    startActivityForResult(intent, FILE_SELECT);
                }
                else if (FLAG == 1)
                {
                    done_sc.show();
                    del_upfile_ibtn.setEnabled(false);
                    filename_et.setEnabled(false);

                    //firebase upload code logic
                    if (filePath != null)
                    {
                        uploadFileName = filename_et.getText() + "." + getFileExtension(filePath);

                        StorageReference uploadFile = st_ref.child(DC.DEPARTMENT).child(value.substring(0,2).toString()).child(DC.SEMESTER).child(value.substring(2,3).toString())
                                .child(DC.DIVISION).child(value.substring(3,4).toString()).child(DC.SUBJECT_NAMES).child(sub_item).child(cat_item).child(file_item).child(uploadFileName);

                        uploadTask = uploadFile.putFile(filePath);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        //upload the URL of file into firebase realtime db
                                        Common_Upload_Storage upload = new Common_Upload_Storage(uploadFileName, taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                                        String key = db_ref.child(DC.DEPARTMENT).child(value.substring(0,2).toString()).child(DC.SEMESTER).child(value.substring(2,3).toString()).child(DC.DIVISION)
                                                .child(value.substring(3,4).toString()).child(DC.UPLOADED_FILES).child(sub_item).child(cat_item).child(file_item).push().getKey();

                                        db_ref.child(DC.DEPARTMENT).child(value.substring(0,2).toString()).child(DC.SEMESTER).child(value.substring(2,3).toString()).child(DC.DIVISION)
                                                .child(value.substring(3,4).toString()).child(DC.UPLOADED_FILES).child(sub_item).child(cat_item).child(file_item).child(key).setValue(upload);

                                        //add notifications in uploaded files of faculty
                                        Long tsLong = System.currentTimeMillis()/1000;
                                        Long diff = 2147483647 - tsLong;

                                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                        cal.setTimeInMillis(tsLong*1000);
                                        String date = DateFormat.format("dd MMM yyyy  hh:mm a", cal).toString();

                                        db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.UPLOADED_FILES).child(diff.toString()).child(DC.TITLE)
                                                .setValue("File Name: - "+uploadFileName+"\nSubject Info: - "+sub_item+" ( "+value.substring(0,2).toString()+" - "+value.substring(2,3).toString()
                                                +" - "+value.substring(3,4)+" )"+"\nTime: - "+date);

                                        done_sc.dismiss();
                                        FLAG = 2;
                                        get_btn.setText("Upload Notice");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                //and displaying error message
                                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                    }
                }
                else if (FLAG == 2)
                {
                    Intent i = new Intent(Faculty_Upload_File.this,Faculty_Upload_Notice.class);
                    //pass department, semester, division, filename and subject name as  parameters to
                    Bundle b = new Bundle();
                    b.putString(DC.TITLE, cat_item+" of "+sub_item+".");
                    b.putString(DC.MESSAGE,uploadFileName+" is uploaded as "+file_item+".");
                    b.putString("Bundle_Sub",sub_item);
                    i.putExtras(b);
                    Toast.makeText(Faculty_Upload_File.this, "Select "+sub_item+" as Subject...", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == FILE_SELECT && resultCode == RESULT_OK && data != null && data.getData() != null && data.getData().getPath() != null)
        {
            filePath = data.getData();

            //---------------------GET FILENAME FROM ABSOLUTE PATH------------------

            String path = data.getData().getPath();
            filename = path.substring(path.lastIndexOf("/")+1);

            int pos = filename.lastIndexOf(".");
            if (pos != -1)
            {
                filename = filename.substring(0, pos);
            }
            //Toast.makeText(this, filename+"", Toast.LENGTH_SHORT).show();

            if (filename != null)
            {
                Iterator hashMap = append_map.keySet().iterator();
                while (hashMap.hasNext())
                {
                    String key = (String) hashMap.next();
                    if (sub_item.equals(key))
                    {
                        FLAG = 1;
                        get_btn.setText("Upload File");
                        sumfile_tv.setVisibility(View.VISIBLE);
                        sumfile_tv.setBackgroundColor(Color.parseColor("#50cbe1"));
                        sc_id.setVisibility(View.VISIBLE);
                        filename_et.setText(filename);
                        cats_tv.append(cat_item);
                        filetype_tv.append(file_item);
                        subs_tv.append(sub_item);

                        value = (String) append_map.get(key);
                        dept_tv.append(value.substring(0, 2).toString());
                        sem_tv.append(value.substring(2, 3).toString());
                        div_tv.append(value.substring(3, 4).toString());

                        del_upfile_ibtn.setVisibility(View.VISIBLE);
                        cat_spinner.setEnabled(false);
                        sub_spinner.setEnabled(false);
                        file_spinner.setEnabled(false);
                    }
                }
            }
        }
    }

    public String getFileExtension(Uri uri)
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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
                Intent i = new Intent(Faculty_Upload_File.this,Student_Login.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent backToHome = new Intent(Faculty_Upload_File.this, Faculty_Home.class);
        startActivity(backToHome);
    }
}
