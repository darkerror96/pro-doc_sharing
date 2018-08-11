package sem8.integrate.app.mainapp_1.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.Login.Faculty_Register;
import sem8.integrate.app.mainapp_1.Login.Student_Login;
import sem8.integrate.app.mainapp_1.R;

public class Admin_Upload_Notice extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference db_ref;
    FirebaseUser user;

    Button uploadnotice_btn;
    EditText title_et, msg_et, dept_et, sem_et, div_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_admin_upload_notice_activity);

        mAuth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        title_et = (EditText)findViewById(R.id.title_et);
        msg_et = (EditText)findViewById(R.id.msg_et);
        dept_et = (EditText)findViewById(R.id.dept_et);
        sem_et = (EditText)findViewById(R.id.sem_et);
        div_et = (EditText)findViewById(R.id.div_et);

        Bundle b = getIntent().getExtras();
        if (b != null)
        {
            dept_et.setText(b.get(DC.DEPARTMENT).toString());
            sem_et.setText(b.get(DC.SEMESTER).toString());
            div_et.setText(b.get(DC.DIVISION).toString());

            dept_et.setEnabled(false);
            sem_et.setEnabled(false);
            div_et.setEnabled(false);

            title_et.setText(b.get(DC.TITLE).toString());
            msg_et.setText(b.get(DC.MESSAGE).toString());
        }

        uploadnotice_btn = (Button)findViewById(R.id.uploadnotice_btn);
        uploadnotice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
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
                                    if (title_et.getText().length() > 0 && title_et.getText().length() < 21 && msg_et.getText().length() > 0)
                                    {
                                        Long tsLong = System.currentTimeMillis()/1000;
                                        Long diff = 2147483647 - tsLong;

                                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                        cal.setTimeInMillis(tsLong*1000);
                                        String date = DateFormat.format("dd MMM yyyy  hh:mm a", cal).toString();

                                        db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString())
                                                .child(DC.DIVISION).child(div_et.getText().toString()).child(DC.NOTICES).child(diff.toString()).child(DC.TITLE)
                                                .setValue("Title: - "+title_et.getText().toString()+"\nMessage: - "+msg_et.getText().toString()+"\nUploaded by: - HOD\nTime: - "+date);

                                        db_ref.child(DC.ADMIN).child(DC.NOTICES).child(diff.toString()).child(DC.TITLE)
                                                .setValue("Title: - "+title_et.getText().toString()+"\nMessage: - "+msg_et.getText().toString()
                                                        +"\nUploaded by: - "+dept_et.getText()+" HOD\nTime: - "+date);

                                        Toast.makeText(Admin_Upload_Notice.this, "Notice Added...", Toast.LENGTH_SHORT).show();

                                        dept_et.requestFocus();

                                        dept_et.setEnabled(true);
                                        sem_et.setEnabled(true);
                                        div_et.setEnabled(true);

                                        dept_et.setText("");
                                        sem_et.setText("");
                                        div_et.setText("");
                                        title_et.setText("");
                                        msg_et.setText("");
                                    }
                                    else if (title_et.getText().length() == 0)
                                    {
                                        Toast.makeText(Admin_Upload_Notice.this, "Add Title...", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (msg_et.getText().length() == 0)
                                    {
                                        Toast.makeText(Admin_Upload_Notice.this, "Add Message...", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (title_et.getText().length() >= 21)
                                    {
                                        Toast.makeText(Admin_Upload_Notice.this, "Minimize the Title Content to 20 characters...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(Admin_Upload_Notice.this, "Check Division again...", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(Admin_Upload_Notice.this, "Length of Division should be 1...", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(Admin_Upload_Notice.this, "Range of Semester should be in between 1 to 8...", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Admin_Upload_Notice.this, "Check Department again...", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Admin_Upload_Notice.this, "Length of Department should be 2...", Toast.LENGTH_SHORT).show();
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
                Intent admin_i = new Intent(Admin_Upload_Notice.this, Student_Login.class);
                startActivity(admin_i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent backtohome = new Intent(Admin_Upload_Notice.this,Admin_Home.class);
        startActivity(backtohome);
    }
}
