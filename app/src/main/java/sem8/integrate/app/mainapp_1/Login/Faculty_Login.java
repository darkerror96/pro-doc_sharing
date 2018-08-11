package sem8.integrate.app.mainapp_1.Login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import sem8.integrate.app.mainapp_1.Faculty.Faculty_Home;
import sem8.integrate.app.mainapp_1.R;

public class Faculty_Login extends AppCompatActivity {

    FirebaseAuth mAuth;

    TextView reg_tv;
    EditText fid_et, pwd_et;
    Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_faculty_login_activity);

        mAuth =FirebaseAuth.getInstance();

        fid_et = (EditText)findViewById(R.id.sid_et);
        fid_et.requestFocus();
        pwd_et = (EditText)findViewById(R.id.pwd_et);

        login_btn = (Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fid_et.getText().length() != 0 && pwd_et.getText().length() >= 6)
                {
                    String[] parts = fid_et.getText().toString().split("\\@");

                    if (parts[0].length() == 3 && parts[1].equals("ddu.com"))
                    {
                        char fid1 = parts[0].charAt(0);
                        char fid2 = parts[0].charAt(1);
                        char fid3 = parts[0].charAt(2);

                        if (fid1 > 96 && fid1 < 123 && fid2 > 96 && fid2 < 123 && fid3 > 96 && fid3 < 123)
                        {
                            mAuth.signInWithEmailAndPassword(fid_et.getText().toString(), pwd_et.getText().toString())
                                    .addOnCompleteListener(Faculty_Login.this, new OnCompleteListener<AuthResult>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Intent i = new Intent(Faculty_Login.this, Faculty_Home.class);
                                                startActivity(i);
                                            }
                                            else
                                            {
                                                Toast.makeText(Faculty_Login.this, "Login Failed. Try again...", Toast.LENGTH_SHORT).show();
                                                pwd_et.setText("");
                                                pwd_et.requestFocus();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            Toast.makeText(Faculty_Login.this, "Check Faculty Id again...", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Faculty_Login.this, "Check Faculty Id again...", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Faculty_Login.this, "Enter Login Credentials Correctly...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reg_tv = (TextView)findViewById(R.id.reg_tv);
        reg_tv.setText(Html.fromHtml("<font color=black><u><b><i>New Faculty? Register here...</i></b></u></font>"));
        reg_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(StudentLogin.this, "Student Registration...", Toast.LENGTH_SHORT).show();
                Intent fac_reg = new Intent(Faculty_Login.this, Faculty_Register.class);
                startActivity(fac_reg);
            }
        });
    }
}
