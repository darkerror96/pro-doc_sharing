package sem8.integrate.app.mainapp_1.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import sem8.integrate.app.mainapp_1.Common_Firebase_WebView_Activity;
import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.Faculty.Faculty_Upload_File;
import sem8.integrate.app.mainapp_1.R;

public class A_Manage_Student_Fragment extends Fragment {

    View v;

    DatabaseReference db_ref;

    EditText sid_et, name_et, dept_et, sem_et, div_et, rno_et;
    ImageButton view_ibtn, edit_ibtn, del_ibtn, subs_ibtn;
    TextView list_tv;

    CircleImageView civ;

    Spinner subs_spinner;

    ArrayList<String> stu_subjects;
    ArrayAdapter adapter;

    ScrollView sprofile_sv;

    String dept_sp, sem_sp, div_sp, roll_sp, old_name, new_name;

    int VIEW_FLAG = 0, EDIT_FLAG = 0, NAME_FLAG = 0, DEL_FLAG = 0, ERR_FLAG = 0, STUDENT_COUNT;
    Boolean CHILD_FLAG = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.admin_a_manage_student_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Manage Student");

        db_ref = FirebaseDatabase.getInstance().getReference();

        //-----------------------SUBJECT SPINNER----------------------

        stu_subjects = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stu_subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subs_spinner = (Spinner)v.findViewById(R.id.subs_spinner);
        stu_subjects.add("Subject (Faculty)");
        subs_spinner.setAdapter(adapter);

        list_tv = (TextView)v.findViewById(R.id.list_tv);

        sprofile_sv = (ScrollView) v.findViewById(R.id.sprofile_sv);

        civ = (CircleImageView)v.findViewById(R.id.profile_image);

        sid_et = (EditText)v.findViewById(R.id.sid_et);
        sid_et.requestFocus();
        name_et = (EditText)v.findViewById(R.id.name_et);
        dept_et = (EditText)v.findViewById(R.id.dept_et);
        sem_et = (EditText)v.findViewById(R.id.sem_et);
        div_et = (EditText)v.findViewById(R.id.div_et);
        rno_et = (EditText)v.findViewById(R.id.rno_et);

        view_ibtn = (ImageButton)v.findViewById(R.id.view_ibtn);
        view_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {

                if (checkSId(sid_et.getText().toString()))
                {
                    //--------------------CHECK WHETHER THE ENTERED STUDENT ID HAS REGISTERED OR NOT-----------------------

                    db_ref.child(DC.DEPARTMENT).child(dept_sp).child(DC.SEMESTER).child(sem_sp).child(DC.DIVISION).child(div_sp).child(DC.STUDENTS)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    CHILD_FLAG = dataSnapshot.hasChild(roll_sp);
                                    //Toast.makeText(getContext(), dataSnapshot.hasChild(roll_sp)+"", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });

                    if (CHILD_FLAG)
                    {
                        db_ref.child(DC.DEPARTMENT).child(dept_sp).child(DC.SEMESTER).child(sem_sp).child(DC.DIVISION).child(div_sp).child(DC.STUDENTS_COUNT).child(DC.TOTAL_COUNT)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Toast.makeText(getContext(), "Total Students Registered in \n\t\t\t\t\t\t\t\t\t"+dept_sp+" - "+sem_sp+" - "+div_sp+" \n\t\t\t\t\t\t\t\t\t\t\t( "
                                                +dataSnapshot.getValue().toString()+" )", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });

                        db_ref.child(DC.DEPARTMENT).child(dept_sp).child(DC.SEMESTER).child(sem_sp).child(DC.DIVISION).child(div_sp).child(DC.STUDENTS).child(roll_sp).child(DC.PHOTO_URL)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Glide.with(getContext())
                                                .load(dataSnapshot.getValue().toString())
                                                .dontAnimate()
                                                .into(civ);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });

                        db_ref.child(DC.DEPARTMENT).child(dept_sp).child(DC.SEMESTER).child(sem_sp).child(DC.DIVISION).child(div_sp).child(DC.STUDENTS).child(roll_sp).child(DC.COMMON_NAME)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        name_et.setText(dataSnapshot.getValue().toString());
                                        name_et.setEnabled(false);

                                        dept_et.setText(dept_sp);
                                        dept_et.setEnabled(false);
                                        sem_et.setText(sem_sp);
                                        sem_et.setEnabled(false);
                                        div_et.setText(div_sp);
                                        div_et.setEnabled(false);
                                        rno_et.setText(roll_sp);
                                        rno_et.setEnabled(false);

                                        sid_et.setEnabled(false);
                                        sid_et.append("@ddu.com");
                                        view_ibtn.setEnabled(false);

                                        VIEW_FLAG = 1;  //--------admin needs to first press on view button and then only can edit or delete the student profile

                                        subs_ibtn.setVisibility(View.VISIBLE);
                                        list_tv.setVisibility(View.VISIBLE);
                                        sprofile_sv.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });
                    }
                    else
                    {
                        if (ERR_FLAG == 0)
                        {
                            ERR_FLAG++;
                            Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
                        }
                        else if (ERR_FLAG == 1)
                        {
                            ERR_FLAG = 0;
                            Toast.makeText(getContext(), "Student yet to Register...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    VIEW_FLAG = 0;

                    list_tv.setVisibility(View.INVISIBLE);
                    sprofile_sv.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Check Student Id Format...\n\t  (Dept Sem Div RollNo)", Toast.LENGTH_LONG).show();
                }
            }
        });

        edit_ibtn = (ImageButton)v.findViewById(R.id.edit_ibtn);
        edit_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (VIEW_FLAG == 1)
                {
                    if (EDIT_FLAG == 0)
                    {
                        //---------edit student profile----------
                        old_name = name_et.getText().toString();

                        name_et.setEnabled(true);
                        name_et.requestFocus();
                        edit_ibtn.setImageResource(R.drawable.admin_edit_done_icon);

                        del_ibtn.setEnabled(false);

                        EDIT_FLAG = 1;
                    }
                    else if (EDIT_FLAG == 1)
                    {
                        new_name = name_et.getText().toString();

                        if (!old_name.equals(new_name))
                        {
                            //--------------------------save changes to DB--------------------------

                            String[] word_len = new_name.split("\\s+");
                            String[] words = new_name.split(" ");
                            for (String word : words)
                            {
                                //Toast.makeText(FacultyRegister.this,word.charAt(0)+"", Toast.LENGTH_SHORT).show();
                                if (word.charAt(0) > 64 && word.charAt(0) < 91)
                                {
                                    NAME_FLAG++;
                                }
                                else
                                {
                                    NAME_FLAG = 0;
                                }
                            }
                            if (word_len.length == 3 && NAME_FLAG == 3)
                            {
                                NAME_FLAG = 0;

                                db_ref.child(DC.DEPARTMENT).child(dept_sp).child(DC.SEMESTER).child(sem_sp).child(DC.DIVISION).child(div_sp).child(DC.STUDENTS).child(roll_sp).child(DC.COMMON_NAME)
                                        .setValue(new_name.toString());

                                name_et.setEnabled(false);
                                edit_ibtn.setImageResource(R.drawable.admin_edit_icon);
                                del_ibtn.setEnabled(true);
                                EDIT_FLAG = 0;

                                Toast.makeText(getContext(), "Profile Updated...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Enter Name in form of 'F M L'", Toast.LENGTH_SHORT).show();

                                NAME_FLAG = 0;
                                name_et.setEnabled(true);
                                edit_ibtn.setImageResource(R.drawable.admin_edit_done_icon);
                                del_ibtn.setEnabled(false);
                                EDIT_FLAG = 1;
                            }
                        }
                        else
                        {
                            name_et.setEnabled(false);
                            edit_ibtn.setImageResource(R.drawable.admin_edit_icon);
                            del_ibtn.setEnabled(true);
                            EDIT_FLAG = 0;
                            Toast.makeText(getContext(), "No Changes...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "View Student Profile first...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        del_ibtn = (ImageButton)v.findViewById(R.id.del_ibtn);
        del_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (VIEW_FLAG == 1)
                {
                    //---------delete student----------
                    if (DEL_FLAG == 0)
                    {
                        DEL_FLAG++;
                        Toast.makeText(getContext(), "Confirm once again...", Toast.LENGTH_SHORT).show();
                    }
                    else if (DEL_FLAG == 1)
                    {
                        db_ref.child(DC.DEPARTMENT).child(dept_sp).child(DC.SEMESTER).child(sem_sp).child(DC.DIVISION).child(div_sp).child(DC.STUDENTS).child(roll_sp)
                                .removeValue();

                        db_ref.child(DC.DEPARTMENT).child(dept_sp).child(DC.SEMESTER).child(sem_sp).child(DC.DIVISION).child(div_sp).child(DC.STUDENTS_COUNT).child(DC.TOTAL_COUNT)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Toast.makeText(getContext(), dataSnapshot.getValue()+"", Toast.LENGTH_SHORT).show();

                                        STUDENT_COUNT = Integer.parseInt(dataSnapshot.getValue().toString());
                                        STUDENT_COUNT--;
                                        db_ref.child(DC.DEPARTMENT).child(dept_sp).child(DC.SEMESTER).child(sem_sp).child(DC.DIVISION).child(div_sp).child(DC.STUDENTS_COUNT).child(DC.TOTAL_COUNT)
                                                .setValue(STUDENT_COUNT);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });

                        Toast.makeText(getContext(), "Student Database Deleted...", Toast.LENGTH_SHORT).show();

                        edit_ibtn.setEnabled(false);
                        del_ibtn.setEnabled(false);
                        list_tv.setVisibility(View.INVISIBLE);
                        sprofile_sv.setVisibility(View.INVISIBLE);

                        //--------------------------ALERT DIALOG FOR DELETE STUDENT ACCOUNT-------------------

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                        alertBuilder.setTitle("Delete Student Account                ' "+dept_sp+sem_sp+div_sp+roll_sp+"@ddu.com '");
                        alertBuilder.setMessage("Redirecting to Firebase Authentication...");
                        alertBuilder.setPositiveButton("Go",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Intent i = new Intent(getContext(), Common_Firebase_WebView_Activity.class);
                                        startActivity(i);
                                    }
                                });

                        AlertDialog delDialog = alertBuilder.create();
                        delDialog.show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "View Student Profile first...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        subs_ibtn = (ImageButton)v.findViewById(R.id.subs_ibtn);
        subs_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                subs_ibtn.setVisibility(View.INVISIBLE);
                stu_subjects.clear();

                db_ref.child(DC.DEPARTMENT).child(dept_sp).child(DC.SEMESTER).child(sem_sp).child(DC.DIVISION).child(div_sp).child(DC.FACULTY_SUBJECT)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                String sname_ds = dataSnapshot.child(DC.SUBJECT_NAME).getValue().toString();
                                String fac_name = dataSnapshot.child(DC.COMMON_NAME).getValue().toString();

                                //Toast.makeText(getContext(), sname_ds+fac_name+"", Toast.LENGTH_SHORT).show();

                                stu_subjects.add(sname_ds+" ("+fac_name+")");
                                subs_spinner.setAdapter(adapter);
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
            }
        });
    }

    public boolean checkSId(String sid)
    {
        if (sid.length() == 7)
        {
            int dept1 = sid.charAt(0);
            int dept2 = sid.charAt(1);
            int div = sid.charAt(3);
            int sem = Character.getNumericValue(sid.charAt(2));
            int r1 = Character.getNumericValue(sid.charAt(4));
            int r2 = Character.getNumericValue(sid.charAt(5));
            int r3 = Character.getNumericValue(sid.charAt(6));

            if (dept1 > 96 && dept1 < 123 && dept2 > 96 && dept2 < 123 &&  div > 96 && div < 123 && sem > 0 && sem < 9 && r1 >= 0 && r1 <= 9 && r2 >= 0 && r2 <= 9 && r3 >= 0 && r3 <= 9)
            {
                dept_sp = sid.substring(0,2).toUpperCase();
                sem_sp = sid.substring(2,3);
                div_sp = sid.substring(3,4).toUpperCase();
                roll_sp = sid.substring(4,7);
                //Toast.makeText(getContext(),dept_sp+sem_sp+div_sp+roll_sp+ "", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
}
