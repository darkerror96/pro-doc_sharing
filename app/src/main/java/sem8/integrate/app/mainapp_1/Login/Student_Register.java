package sem8.integrate.app.mainapp_1.Login;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.R;

public class Student_Register extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference db_ref;
    FirebaseUser user;

    EditText name_et,dept_et,sem_et,div_et,rno_et,sid_et,pwd_et;
    Button reg_btn;
    int flag = 0, SNAME_FLAG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_student_register_activity);

        mAuth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        name_et = (EditText)findViewById(R.id.name_et);
        name_et.requestFocus();
        dept_et = (EditText)findViewById(R.id.dept_et);
        sem_et = (EditText)findViewById(R.id.sem_et);
        div_et = (EditText)findViewById(R.id.div_et);
        rno_et = (EditText)findViewById(R.id.rno_et);
        sid_et = (EditText)findViewById(R.id.sid_et);
        pwd_et = (EditText)findViewById(R.id.pwd_et);

        reg_btn = (Button)findViewById(R.id.reg_btn);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (flag == 0)
                {
                    sid_et.setText("");
                    if (name_et.getText().length() != 0)
                    {
                        String f_name = name_et.getText().toString();
                        String[] word_len = f_name.split("\\s+");
                        String[] words = f_name.split(" ");
                        for (String word : words)
                        {
                            //Toast.makeText(FacultyRegister.this,word.charAt(0)+"", Toast.LENGTH_SHORT).show();
                            if (word.charAt(0) > 64 && word.charAt(0) < 91)
                            {
                                SNAME_FLAG++;
                            }
                            else
                            {
                                SNAME_FLAG = 0;
                            }
                        }
                        if (word_len.length == 3 && SNAME_FLAG == 3)
                        {
                            SNAME_FLAG = 0;

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
                                                if (rno_et.getText().length() == 3)
                                                {
                                                    sid_et.append(dept_et.getText().toString().toLowerCase()+""+sem_et.getText()+""+div_et.getText().toString().toLowerCase()+""+rno_et.getText()+"@ddu.com");

                                                    name_et.setEnabled(false);
                                                    dept_et.setEnabled(false);
                                                    sem_et.setEnabled(false);
                                                    div_et.setEnabled(false);
                                                    rno_et.setEnabled(false);

                                                    sid_et.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.login_check_icon,0);
                                                    reg_btn.setText("Register");
                                                    pwd_et.requestFocus();
                                                    pwd_et.setEnabled(true);
                                                    flag = 1;
                                                }
                                                else
                                                {
                                                    Toast.makeText(Student_Register.this, "Length of Roll No should be 3...", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else
                                            {
                                                Toast.makeText(Student_Register.this, "Check Division again...", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(Student_Register.this, "Length of Division should be 1...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(Student_Register.this, "Range of Semester should be in between 1 to 8...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(Student_Register.this, "Check Department again...", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(Student_Register.this, "Length of Department should be 2...", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(Student_Register.this, "Enter Name in form of 'F M L'", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Student_Register.this, "Enter Name...", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (flag == 1)
                {
                    if (pwd_et.getText().length() > 5)
                    {
                        mAuth.createUserWithEmailAndPassword(sid_et.getText().toString(), pwd_et.getText().toString())
                                .addOnCompleteListener(Student_Register.this, new OnCompleteListener<AuthResult>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString())
                                                    .child(DC.DIVISION).child(div_et.getText().toString()).child(DC.STUDENTS).child(rno_et.getText().toString())
                                                    .child(DC.COMMON_NAME).setValue(name_et.getText().toString());

                                            db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString())
                                                    .child(DC.DIVISION).child(div_et.getText().toString()).child(DC.STUDENTS_COUNT).child(DC.TOTAL_COUNT)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            //Toast.makeText(Student_Register.this, dataSnapshot.getValue()+"", Toast.LENGTH_LONG).show();
                                                            if (dataSnapshot.exists() == false)
                                                            {
                                                                db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString())
                                                                        .child(DC.DIVISION).child(div_et.getText().toString()).child(DC.STUDENTS_COUNT).child(DC.TOTAL_COUNT)
                                                                        .setValue(1);
                                                            }
                                                            else
                                                            {
                                                                String scount = dataSnapshot.getValue().toString();
                                                                db_ref.child(DC.DEPARTMENT).child(dept_et.getText().toString()).child(DC.SEMESTER).child(sem_et.getText().toString())
                                                                        .child(DC.DIVISION).child(div_et.getText().toString()).child(DC.STUDENTS_COUNT).child(DC.TOTAL_COUNT)
                                                                        .setValue(Integer.parseInt(scount)+1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {}
                                                    });


                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    //.setDisplayName(name_et.getText().toString())
                                                    .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/cloud-3fe34.appspot.com/o/Common%20Profile%20Picture%2Fuser_couple.png?alt=media&token=5f2f3e34-6ec0-466a-bc93-fa9d89f48d9b"))
                                                    .build();

                                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {}
                                            });

                                            db_ref.child(DC.DEPARTMENT).child(user.getEmail().substring(0,2).toUpperCase()).child(DC.SEMESTER).child(user.getEmail().substring(2,3)).child(DC.DIVISION)
                                                    .child(user.getEmail().substring(3,4).toUpperCase()).child(DC.STUDENTS).child(user.getEmail().substring(4,7)).child(DC.PHOTO_URL)
                                                    .setValue("https://firebasestorage.googleapis.com/v0/b/cloud-3fe34.appspot.com/o/Common%20Profile%20Picture%2Fuser_couple.png?alt=media&token=5f2f3e34-6ec0-466a-bc93-fa9d89f48d9b");

                                            Toast.makeText(Student_Register.this, "Account Created...", Toast.LENGTH_SHORT).show();
                                            Intent login = new Intent(Student_Register.this, Student_Login.class);
                                            startActivity(login);
                                        }
                                        else
                                        {
                                            Toast.makeText(Student_Register.this, "Account already created...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(Student_Register.this, "Enter Password of at least 6 characters...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
