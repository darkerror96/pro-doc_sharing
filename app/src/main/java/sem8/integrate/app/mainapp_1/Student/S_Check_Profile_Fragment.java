package sem8.integrate.app.mainapp_1.Student;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.R;

import static android.app.Activity.RESULT_OK;

public class S_Check_Profile_Fragment extends Fragment {

    FirebaseAuth mAuth;
    DatabaseReference db_ref;
    FirebaseUser user;

    View v;

    ImageButton lockbtn;
    EditText pwd_et;
    Boolean password_lock = true;

    TextView editimage_tv, student_name_tv, sid_tv;

    ListView sub_lv;
    ArrayList<String> subject_al;
    ArrayAdapter adapter;
    int sub_count = 1, FILE_SELECT = 7;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.student_s_check_profile_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Check Profile");

        mAuth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        subject_al = new ArrayList<>();                               //subject_al.add("Subject Name ( Faculty Name )");

        pwd_et = (EditText)v.findViewById(R.id.pwd_et);

        student_name_tv = (TextView)v.findViewById(R.id.student_name_tv);
        sid_tv =(TextView)v.findViewById(R.id.sid_tv);

        adapter = new ArrayAdapter<String>(getContext(), R.layout.student_subjects_listview, subject_al);
        sub_lv = (ListView)v.findViewById(R.id.sub_lv);
        sub_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        });


        //---------------------------initialise the check profile fragment--------------------------

        sid_tv.setText(user.getEmail());

        db_ref.child(DC.DEPARTMENT).child(user.getEmail().substring(0,2).toUpperCase()).child(DC.SEMESTER).child(user.getEmail().substring(2,3)).child(DC.DIVISION)
                .child(user.getEmail().substring(3,4).toUpperCase()).child(DC.STUDENTS).child(user.getEmail().substring(4,7)).child(DC.COMMON_NAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        student_name_tv.setText(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

        CircleImageView civ = (CircleImageView)v.findViewById(R.id.profile_image);
        Glide.with(getContext())
                .load(user.getPhotoUrl())
                .dontAnimate()
                .into(civ);

        db_ref.child(DC.DEPARTMENT).child(user.getEmail().substring(0,2).toUpperCase()).child(DC.SEMESTER).child(user.getEmail().substring(2,3)).child(DC.DIVISION)
                .child(user.getEmail().substring(3,4).toUpperCase()).child(DC.FACULTY_SUBJECT)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String f_name = dataSnapshot.child(DC.COMMON_NAME).getValue().toString().toUpperCase();
                        String s_name = dataSnapshot.child(DC.SUBJECT_NAME).getValue().toString();
                        //Toast.makeText(getContext(), f_name+" "+s_name, Toast.LENGTH_SHORT).show();
                        subject_al.add(sub_count+" ) "+s_name+" ("+f_name+")");
                        sub_lv.setAdapter(adapter);
                        sub_count++;
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

        //------------------------EDIT PROFILE PICTURE TEXT VIEW HYPERLINK--------------------------

        editimage_tv = (TextView)v.findViewById(R.id.editimage_tv);
        editimage_tv.setText(Html.fromHtml("<font color=blue>Edit</font>"));
        editimage_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Edit Image...", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, FILE_SELECT);

            }
        });

        lockbtn = (ImageButton)v.findViewById(R.id.lock_btn);
        lockbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (password_lock == true)
                {
                    password_lock = false;                                                  //true means password is locked and false means password is unlocked

                    pwd_et.setEnabled(true);
                    pwd_et.requestFocus();
                    pwd_et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    lockbtn.setImageResource(R.drawable.common_lock_opened_icon);
                }
                else
                {
                    if (pwd_et.getText().length() != 0)
                    {
                        if (pwd_et.getText().length() > 5)
                        {
                            user.updatePassword(pwd_et.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Password changed successfully...", Toast.LENGTH_SHORT).show();
                                        password_lock = true;
                                        pwd_et.setEnabled(false);
                                        pwd_et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        lockbtn.setImageResource(R.drawable.common_lock_closed_icon);
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Enter Password of at least 6 characters...", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Enter New Password...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK && requestCode == FILE_SELECT && data.getData() != null)
        {
            Uri filePath = data.getData();
            try
            {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(filePath)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Profile Updated...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                db_ref.child(DC.DEPARTMENT).child(user.getEmail().substring(0,2).toUpperCase()).child(DC.SEMESTER).child(user.getEmail().substring(2,3)).child(DC.DIVISION)
                        .child(user.getEmail().substring(3,4).toUpperCase()).child(DC.STUDENTS).child(user.getEmail().substring(4,7)).child(DC.PHOTO_URL)
                        .setValue(filePath.toString());

                Context applicationContext = Student_Home.getContextOfApplication();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), filePath);
                CircleImageView civ = (CircleImageView)v.findViewById(R.id.profile_image);
                civ.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
