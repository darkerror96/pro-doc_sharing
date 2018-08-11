package sem8.integrate.app.mainapp_1.Login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import sem8.integrate.app.mainapp_1.R;
import sem8.integrate.app.mainapp_1.Student.Student_Home;

public class Student_Login extends AppCompatActivity {

    FirebaseAuth mAuth;

    TextView reg_tv;
    EditText sid_et, pwd_et;
    Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_student_login_activity);

        mAuth = FirebaseAuth.getInstance();

        sid_et = (EditText)findViewById(R.id.sid_et);
        sid_et.requestFocus();
        pwd_et = (EditText)findViewById(R.id.pwd_et);

        login_btn = (Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sid_et.getText().length() == 15 && pwd_et.getText().length() >= 6)
                {
                    String s_id = sid_et.getText().toString();

                    int dept1 = s_id.charAt(0);
                    int dept2 = s_id.charAt(1);
                    int sem = Character.getNumericValue(s_id.charAt(2));
                    int div = s_id.charAt(3);
                    int r1 = Character.getNumericValue(s_id.charAt(4));
                    int r2 = Character.getNumericValue(s_id.charAt(5));
                    int r3 = Character.getNumericValue(s_id.charAt(6));
                    int special_sym = s_id.charAt(7);
                    String clg_name = s_id.substring(8,11);
                    int dot_sym = s_id.charAt(11);
                    String com_sym = s_id.substring(12,15);
                    //Toast.makeText(Student_Login.this,special_sym+""+clg_name+""+dot_sym+""+com_sym, Toast.LENGTH_SHORT).show();

                    if (dept1 > 96 && dept1 < 123 && dept2 > 96 && dept2 < 123 &&  div > 96 && div < 123
                            && sem > 0 && sem < 9 && r1 >= 0 && r1 <= 9 && r2 >= 0 && r2 <= 9 && r3 >= 0 && r3 <= 9
                            && special_sym == 64 && clg_name.equals("ddu") && dot_sym == 46 && com_sym.equals("com"))
                    {

                        mAuth.signInWithEmailAndPassword(s_id, pwd_et.getText().toString())
                                .addOnCompleteListener(Student_Login.this, new OnCompleteListener<AuthResult>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            Intent i = new Intent(Student_Login.this, Student_Home.class);
                                            startActivity(i);
                                        }
                                        else
                                        {
                                            Toast.makeText(Student_Login.this, "Login Failed. Try again...", Toast.LENGTH_SHORT).show();
                                            pwd_et.setText("");
                                            pwd_et.requestFocus();
                                        }
                                     }
                                });
                    }
                    else
                    {
                        Toast.makeText(Student_Login.this, "Check Student Id again...", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Student_Login.this, "Enter Login Credentials Correctly...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reg_tv = (TextView)findViewById(R.id.reg_tv);
        reg_tv.setText(Html.fromHtml("<font color=black><u><b><i>New Student? Register here...</i></b></u></font>"));
        reg_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(StudentLogin.this, "Student Registration...", Toast.LENGTH_SHORT).show();
                Intent stu_reg = new Intent(Student_Login.this, Student_Register.class);
                startActivity(stu_reg);
            }
        });
    }

    @Override
    public void onBackPressed() {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.faculty_login_item:
                //Toast.makeText(getApplicationContext(), "Faculty", Toast.LENGTH_SHORT).show();
                Intent student_i = new Intent(Student_Login.this,Faculty_Login.class);
                startActivity(student_i);
                return true;
            case R.id.admin_login_item:
                //Toast.makeText(getApplicationContext(), "Admin", Toast.LENGTH_SHORT).show();
                Intent admin_i = new Intent(Student_Login.this,Admin_Login.class);
                startActivity(admin_i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
