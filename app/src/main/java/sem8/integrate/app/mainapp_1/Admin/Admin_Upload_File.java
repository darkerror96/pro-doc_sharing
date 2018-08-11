package sem8.integrate.app.mainapp_1.Admin;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import sem8.integrate.app.mainapp_1.Common_Upload_Storage;
import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.Faculty.Faculty_Upload_File;
import sem8.integrate.app.mainapp_1.Faculty.Faculty_Upload_Notice;
import sem8.integrate.app.mainapp_1.Login.Student_Login;
import sem8.integrate.app.mainapp_1.R;

public class Admin_Upload_File extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
    StorageReference st_ref = FirebaseStorage.getInstance().getReference();
    Uri filePath;
    UploadTask uploadTask;

    Spinner cat_spinner, file_spinner;
    Button get_btn;
    ImageButton del_upfile_ibtn;
    TextView sumfile_tv;

    ScrollView sc_id;
    RelativeLayout rl_id;

    EditText filename_et, dept_et, div_et, sem_et;
    TextView cats_tv, filetype_tv, dept_tv, sem_tv, div_tv, subs_tv;
    String cat_item, file_item, filename, uploadFileName;
    int FILE_SELECT = 7, FLAG = 0;

    Snackbar done_sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_admin_upload_file_activity);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        filename_et = (EditText) findViewById(R.id.filename_et);
        dept_et = (EditText) findViewById(R.id.dept_et);
        div_et = (EditText) findViewById(R.id.div_et);
        sem_et = (EditText) findViewById(R.id.sem_et);

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

        done_sc = Snackbar.make(rl_id,"File Uploading...",Snackbar.LENGTH_LONG)
                .setAction("Cancel",new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {

                        //use firebase upload cancel() method to cancel file upload
                        uploadTask.cancel();

                        Toast.makeText(Admin_Upload_File.this, "Uploading Canceled...", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Admin_Upload_File.this, Admin_Upload_File.class);
                        startActivity(i);
                    }
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
        categories.add("Time Table");
        categories.add("Fee Notice");
        categories.add("Other");

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

        List<String> file_types = new ArrayList<String>();
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
                Intent i = new Intent(Admin_Upload_File.this, Admin_Upload_File.class);
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
                    if (dept_et.getText().length() == 2)
                    {
                        if (dept_et.getText().charAt(0) > 64 && dept_et.getText().charAt(0) < 91 && dept_et.getText().charAt(1) > 64 && dept_et.getText().charAt(1) < 91)
                        {
                            if (sem_et.getText().length() == 1 && Integer.parseInt(sem_et.getText().toString()) > 0 && Integer.parseInt(sem_et.getText().toString()) < 9)
                            {
                                if (div_et.getText().length() == 1)
                                {
                                    if (div_et.getText().charAt(0) > 64 && div_et.getText().charAt(0) < 91)
                                    {
                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                        if (file_item.equals("Image"))
                                        {
                                            intent.setType("image/*");
                                            Toast.makeText(Admin_Upload_File.this, "Select Image...", Toast.LENGTH_LONG).show();
                                        }
                                        else if (file_item.equals("PDF"))
                                        {
                                            intent.setType("application/pdf");
                                            Toast.makeText(Admin_Upload_File.this, "Select PDF...", Toast.LENGTH_LONG).show();
                                        }
                                        startActivityForResult(intent, FILE_SELECT);
                                    }
                                    else
                                    {
                                        Toast.makeText(Admin_Upload_File.this, "Check Division again...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(Admin_Upload_File.this, "Length of Division should be 1...", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(Admin_Upload_File.this, "Range of Semester should be in between 1 to 8...", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(Admin_Upload_File.this, "Check Department again...", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Admin_Upload_File.this, "Length of Department should be 2...", Toast.LENGTH_SHORT).show();
                    }
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

                        StorageReference uploadFile = st_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString())
                                .child(DC.DIVISION).child(div_et.getText().toString()).child(DC.SUBJECT_NAMES).child(DC.GENERAL).child(cat_item).child(file_item).child(uploadFileName);

                        uploadTask = uploadFile.putFile(filePath);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                //upload the URL of file into firebase realtime db
                                Common_Upload_Storage upload = new Common_Upload_Storage(uploadFileName, taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                                String key = db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString()).child(DC.DIVISION)
                                        .child(div_et.getText().toString()).child(DC.UPLOADED_FILES).child(DC.GENERAL).child(cat_item).child(file_item).push().getKey();

                                db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString()).child(DC.DIVISION)
                                        .child(div_et.getText().toString()).child(DC.UPLOADED_FILES).child(DC.GENERAL).child(cat_item).child(file_item).child(key).setValue(upload);

                                //add notifications in uploaded files of admin

                                Long tsLong = System.currentTimeMillis()/1000;
                                Long diff = 2147483647 - tsLong;

                                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                cal.setTimeInMillis(tsLong*1000);
                                String date = DateFormat.format("dd MMM yyyy  hh:mm a", cal).toString();

                                db_ref.child(DC.ADMIN).child(DC.UPLOADED_FILES).child(diff.toString()).child(DC.TITLE)
                                        .setValue("File Name: - "+uploadFileName+"\nSubject Info: - "+DC.GENERAL+" ( "+dept_et.getText().toString()+" - "+sem_et.getText().toString()
                                                +" - "+div_et.getText().toString()+" )"+"\nTime: - "+date);

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
                    Intent i = new Intent(Admin_Upload_File.this,Admin_Upload_Notice.class);

                    //pass department, semester, division, filename and subject name as  parameters to
                    Bundle b = new Bundle();

                    b.putString(DC.DEPARTMENT, dept_et.getText().toString());
                    b.putString(DC.SEMESTER, sem_et.getText().toString());
                    b.putString(DC.DIVISION, div_et.getText().toString());

                    b.putString(DC.TITLE, DC.GENERAL+" "+cat_item+".");
                    b.putString(DC.MESSAGE,uploadFileName+" is uploaded as "+file_item+".");

                    i.putExtras(b);

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

                FLAG = 1;
                get_btn.setText("Upload File");
                sumfile_tv.setVisibility(View.VISIBLE);
                sumfile_tv.setBackgroundColor(Color.parseColor("#50cbe1"));
                sc_id.setVisibility(View.VISIBLE);
                filename_et.setText(filename);
                cats_tv.append(cat_item);
                filetype_tv.append(file_item);
                subs_tv.append(DC.GENERAL);

                dept_et.setEnabled(false);
                sem_et.setEnabled(false);
                div_et.setEnabled(false);

                dept_tv.append(dept_et.getText().toString());
                sem_tv.append(sem_et.getText().toString());
                div_tv.append(div_et.getText().toString());

                del_upfile_ibtn.setVisibility(View.VISIBLE);
                cat_spinner.setEnabled(false);
                file_spinner.setEnabled(false);
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
                Intent admin_i = new Intent(Admin_Upload_File.this, Student_Login.class);
                startActivity(admin_i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent backtohome = new Intent(Admin_Upload_File.this,Admin_Home.class);
        startActivity(backtohome);
    }
}
