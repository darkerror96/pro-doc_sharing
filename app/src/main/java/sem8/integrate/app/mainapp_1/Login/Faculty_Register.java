package sem8.integrate.app.mainapp_1.Login;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.aware.Characteristics;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.R;

public class Faculty_Register extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference db_ref;
    FirebaseUser user;

    Button reg_btn;
    ImageButton donesub_ibtn;
    EditText name_et, fid_et, pwd_et, dept_et, sem_et, div_et, sub_et;
    LinearLayout ll_id;
    int flag = 0, sub_count = 1, FID_FLAG = 0, REG_COUNT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_faculty_register_activity);

        mAuth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        ll_id = (LinearLayout)findViewById(R.id.ll_id);

        name_et = (EditText)findViewById(R.id.name_et);
        name_et.requestFocus();
        fid_et = (EditText)findViewById(R.id.fid_et);
        pwd_et = (EditText)findViewById(R.id.pwd_et);
        dept_et = (EditText)findViewById(R.id.dept_et);
        sem_et = (EditText)findViewById(R.id.sem_et);
        div_et = (EditText)findViewById(R.id.div_et);
        sub_et = (EditText)findViewById(R.id.sub_et);

        donesub_ibtn = (ImageButton)findViewById(R.id.donesub_ibtn);
        donesub_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (REG_COUNT == 0)
                {
                    Toast.makeText(Faculty_Register.this, "Press again to Complete the Registration Process...", Toast.LENGTH_SHORT).show();
                    REG_COUNT++;
                }
                else if (REG_COUNT == 1)
                {
                    Intent login = new Intent(Faculty_Register.this, Student_Login.class);
                    startActivity(login);
                }

            }
        });

        reg_btn = (Button)findViewById(R.id.reg_btn);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (flag == 0)
                {
                    String f_name = name_et.getText().toString();
                    String[] word_len = f_name.split("\\s+");
                    if (word_len.length == 3)
                    {
                        String[] words = f_name.split(" ");
                        for (String word : words)
                        {
                            //Toast.makeText(FacultyRegister.this,word.charAt(0)+"", Toast.LENGTH_SHORT).show();
                            if (word.charAt(0) > 64 && word.charAt(0) < 91)
                            {
                                fid_et.append(String.valueOf(word.charAt(0)).toLowerCase());
                                FID_FLAG++;
                            }
                            else
                            {
                                FID_FLAG = 0;
                            }
                        }
                        if (FID_FLAG != 3)
                        {
                            fid_et.setText("");
                            Toast.makeText(Faculty_Register.this, "Please Enter Name in form of 'F M L'", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            fid_et.append("@ddu.com");
                            fid_et.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.login_check_icon,0);
                            pwd_et.setEnabled(true);
                            name_et.setEnabled(false);
                            pwd_et.requestFocus();
                            reg_btn.setText("Register");
                            Toast.makeText(Faculty_Register.this, "Enter Password...", Toast.LENGTH_SHORT).show();

                            flag = 1;
                        }
                    }
                    else
                    {
                        Toast.makeText(Faculty_Register.this, "Enter Name in form of 'F M L'", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (flag == 1)
                {
                    if (pwd_et.getText().length() > 5)
                    {
                        mAuth.createUserWithEmailAndPassword(fid_et.getText().toString(), pwd_et.getText().toString())
                                .addOnCompleteListener(Faculty_Register.this, new OnCompleteListener<AuthResult>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.COMMON_NAME).setValue(name_et.getText().toString());

                                            db_ref.child(DC.FACULTY_RELATION).child(fid_et.getText().toString().substring(0,3)).setValue(user.getUid());

                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    //.setDisplayName(name_et.getText().toString())
                                                    .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/cloud-3fe34.appspot.com/o/Common%20Profile%20Picture%2Fuser_couple.png?alt=media&token=5f2f3e34-6ec0-466a-bc93-fa9d89f48d9b"))
                                                    .build();

                                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {}
                                                    });

                                            db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.PHOTO_URL)
                                                    .setValue("https://firebasestorage.googleapis.com/v0/b/cloud-3fe34.appspot.com/o/Common%20Profile%20Picture%2Fuser_couple.png?alt=media&token=5f2f3e34-6ec0-466a-bc93-fa9d89f48d9b");

                                            Toast.makeText(Faculty_Register.this, "Registration Done...", Toast.LENGTH_SHORT).show();
                                            pwd_et.setEnabled(false);
                                            reg_btn.setText("Add Subject (1)");
                                            donesub_ibtn.setVisibility(View.VISIBLE);
                                            ll_id.setVisibility(View.VISIBLE);
                                            flag = 2;

                                            dept_et.requestFocus();
                                        }
                                        else
                                        {
                                            Toast.makeText(Faculty_Register.this, "Account already created...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(Faculty_Register.this, "Length of Password must be at least 6 characters...", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (flag == 2)
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
                                            String fac_sub_key = db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.SUBJECT_NAMES).push().getKey();

                                            String key = db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString()).child(DC.DIVISION)
                                                    .child(div_et.getText().toString()).child(DC.FACULTY_SUBJECT).push().getKey();

                                            db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.SUBJECT_NAME)
                                                    .setValue(sub_et.getText().toString());
                                            db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.SEMESTER)
                                                    .setValue(sem_et.getText().toString());
                                            db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.DIVISION)
                                                    .setValue(div_et.getText().toString());
                                            db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.DEPARTMENT)
                                                    .setValue(dept_et.getText().toString());

                                            //---------------------------------------SAVING KEY OF SUBJECT ADDED INTO THE CLASS OF STUDENTS------------------------

                                            db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.SUBJECT_KEY)
                                                    .setValue(key);
                                            db_ref.child(DC.FACULTIES).child(user.getUid()).child(DC.SUBJECT_NAMES).child(fac_sub_key).child(DC.FACULTY_SUBJECT_KEY)
                                                    .setValue(fac_sub_key);

                                            //---------------------------------------------SUBJECT ADDED INTO CLASS----------------------------------------

                                            db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString()).child(DC.DIVISION)
                                                    .child(div_et.getText().toString()).child(DC.FACULTY_SUBJECT).child(key).child(DC.COMMON_NAME)
                                                    .setValue(name_et.getText().toString());

                                            db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString()).child(DC.DIVISION)
                                                    .child(div_et.getText().toString()).child(DC.FACULTY_SUBJECT).child(key).child(DC.SUBJECT_NAME)
                                                    .setValue(sub_et.getText().toString());

                                            //Toast.makeText(FacultyRegister.this, dept_et.getText().toString()+"\n"+sem_et.getText()+"\n"+div_et.getText()+"\n"+sub_et.getText().toString(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(Faculty_Register.this, "Subject Added...", Toast.LENGTH_SHORT).show();

                                            div_et.requestFocus();
                                            div_et.setText("");
                                            sub_et.setText("");
                                            sub_count++;
                                            reg_btn.setText("Add Subject ("+sub_count+")");
                                        }
                                        else
                                        {
                                            Toast.makeText(Faculty_Register.this, "Enter Subject Name in short...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(Faculty_Register.this, "Check Division again...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(Faculty_Register.this, "Length of Division should be 1...", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(Faculty_Register.this, "Range of Semester should be in between 1 to 8...", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(Faculty_Register.this, "Check Department again...", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Faculty_Register.this, "Length of Department should be 2...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
