package sem8.integrate.app.mainapp_1.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import sem8.integrate.app.mainapp_1.Common_Firebase_WebView_Activity;
import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.R;

public class A_Manage_Faculty_Fragment extends Fragment {

    View v;

    DatabaseReference db_ref;

    EditText fid_et, name_et;
    ImageButton view_ibtn, edit_ibtn, del_ibtn, nextsub_ibtn;
    TextView list_tv, sub_tv;

    CircleImageView civ;

    ScrollView sprofile_sv;

    String fac_key, old_name, new_name;

    int VIEW_FLAG = 0, EDIT_FLAG = 0, ERR_FLAG = 0, NAME_FLAG = 0, SAME_NAME_FLAG = 0, DEL_FLAG = 0, MANAGE_SUB = 0;

    ArrayList<Character> old_cmp, new_cmp;

    Spinner fac_sub_spinner;
    ArrayList<String> f_subject_arr;
    ArrayAdapter adapter;

    Query query;
    ChildEventListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.admin_a_manage_faculty_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Manage Faculty");

        db_ref = FirebaseDatabase.getInstance().getReference();

        old_cmp = new ArrayList<>();
        new_cmp = new ArrayList<>();

        f_subject_arr = new ArrayList<>();
        f_subject_arr.add("Subject (Dept-Sem-Div)");
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, f_subject_arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fac_sub_spinner = (Spinner) v.findViewById(R.id.fac_sub_spinner);
        fac_sub_spinner.setAdapter(adapter);

        list_tv = (TextView) v.findViewById(R.id.list_tv);
        sub_tv = (TextView)v.findViewById(R.id.sub_tv);

        civ = (CircleImageView) v.findViewById(R.id.profile_image);

        sprofile_sv = (ScrollView) v.findViewById(R.id.sprofile_sv);

        fid_et = (EditText) v.findViewById(R.id.fid_et);
        fid_et.requestFocus();
        name_et = (EditText) v.findViewById(R.id.name_et);

        view_ibtn = (ImageButton) v.findViewById(R.id.view_ibtn);
        view_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFId(fid_et.getText().toString())) {
                    //--------------------CHECK WHETHER THE ENTERED FACULTY ID HAS REGISTERED OR NOT-----------------------

                    db_ref.child(DC.FACULTY_RELATION).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Toast.makeText(getContext(), dataSnapshot.hasChild(fid_et.getText().toString())+"", Toast.LENGTH_SHORT).show();

                            if (dataSnapshot.hasChild(fid_et.getText().toString())) {
                                db_ref.child(DC.FACULTY_RELATION).child(fid_et.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        fac_key = dataSnapshot.getValue().toString();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    if (fac_key != null) {
                        db_ref.child(DC.FACULTIES).child(fac_key).child(DC.PHOTO_URL).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Glide.with(getContext())
                                        .load(dataSnapshot.getValue().toString())
                                        .dontAnimate()
                                        .into(civ);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        db_ref.child(DC.FACULTIES).child(fac_key).child(DC.COMMON_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                name_et.setText(dataSnapshot.getValue().toString());
                                name_et.setEnabled(false);

                                view_ibtn.setEnabled(false);
                                fid_et.append("@ddu.com");
                                fid_et.setEnabled(false);

                                VIEW_FLAG = 1;  //--------admin needs to first press on view button and then only can edit or delete the student profile

                                list_tv.setVisibility(View.VISIBLE);
                                sprofile_sv.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    } else {
                        if (ERR_FLAG == 0) {
                            ERR_FLAG++;
                            Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
                        } else if (ERR_FLAG == 1) {
                            ERR_FLAG = 0;
                            Toast.makeText(getContext(), "Faculty yet to Register...", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    VIEW_FLAG = 0;

                    list_tv.setVisibility(View.INVISIBLE);
                    sprofile_sv.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Check Faculty Id Format...\n\t\t\t\t\t\t\t\t ( F M L )", Toast.LENGTH_LONG).show();
                }
            }
        });

        edit_ibtn = (ImageButton) v.findViewById(R.id.edit_ibtn);
        edit_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VIEW_FLAG == 1) {
                    if (EDIT_FLAG == 0) {
                        //---------edit faculty profile----------
                        old_name = name_et.getText().toString();

                        name_et.setEnabled(true);
                        name_et.requestFocus();
                        edit_ibtn.setImageResource(R.drawable.admin_edit_done_icon);

                        del_ibtn.setEnabled(false);

                        EDIT_FLAG = 1;
                    } else if (EDIT_FLAG == 1) {
                        new_name = name_et.getText().toString();

                        if (!old_name.equals(new_name)) {
                            //--------------------------save changes to DB--------------------------

                            String[] word_len = new_name.split("\\s+");
                            String[] words = new_name.split(" ");
                            for (String word : words) {
                                //Toast.makeText(FacultyRegister.this,word.charAt(0)+"", Toast.LENGTH_SHORT).show();
                                if (word.charAt(0) > 64 && word.charAt(0) < 91) {
                                    NAME_FLAG++;
                                } else {
                                    NAME_FLAG = 0;
                                }
                            }
                            if (word_len.length == 3 && NAME_FLAG == 3) {
                                NAME_FLAG = 0;

                                String[] old_words = old_name.split(" ");
                                for (String word : old_words) {
                                    old_cmp.add(word.charAt(0));
                                }

                                String[] new_words = new_name.split(" ");
                                for (String word : new_words) {
                                    new_cmp.add(word.charAt(0));
                                }

                                for (int i = 0; i < 3; i++) {
                                    if (old_cmp.get(i) == new_cmp.get(i)) {
                                        SAME_NAME_FLAG++;
                                    } else {
                                        SAME_NAME_FLAG = 0;
                                    }
                                }

                                if (SAME_NAME_FLAG == 3) {
                                    SAME_NAME_FLAG = 0;

                                    old_cmp.clear();
                                    new_cmp.clear();

                                    db_ref.child(DC.FACULTIES).child(fac_key).child(DC.COMMON_NAME).setValue(new_name.toString());

                                    name_et.setEnabled(false);
                                    edit_ibtn.setImageResource(R.drawable.admin_edit_icon);
                                    del_ibtn.setEnabled(true);
                                    EDIT_FLAG = 0;

                                    Toast.makeText(getContext(), "Profile Updated...", Toast.LENGTH_SHORT).show();
                                } else {
                                    SAME_NAME_FLAG = 0;

                                    old_cmp.clear();
                                    new_cmp.clear();

                                    name_et.setEnabled(true);
                                    edit_ibtn.setImageResource(R.drawable.admin_edit_done_icon);
                                    del_ibtn.setEnabled(false);
                                    EDIT_FLAG = 1;

                                    Toast.makeText(getContext(), "Keep First Character of Name intact...", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Enter Name in form of 'F M L'", Toast.LENGTH_SHORT).show();

                                NAME_FLAG = 0;
                                name_et.setEnabled(true);
                                edit_ibtn.setImageResource(R.drawable.admin_edit_done_icon);
                                del_ibtn.setEnabled(false);
                                EDIT_FLAG = 1;
                            }
                        } else {
                            name_et.setEnabled(false);
                            edit_ibtn.setImageResource(R.drawable.admin_edit_icon);
                            del_ibtn.setEnabled(true);
                            EDIT_FLAG = 0;
                            Toast.makeText(getContext(), "No Changes...", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "View Faculty Profile first...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        del_ibtn = (ImageButton) v.findViewById(R.id.del_ibtn);
        del_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VIEW_FLAG == 1) {
                    //---------delete faculty----------
                    if (DEL_FLAG == 0) {
                        DEL_FLAG++;
                        Toast.makeText(getContext(), "Confirm once again...", Toast.LENGTH_SHORT).show();
                    } else if (DEL_FLAG == 1) {
                        db_ref.child(DC.FACULTY_RELATION).child(fid_et.getText().toString()).removeValue();

                        db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                String sem_ds = dataSnapshot.child(DC.SEMESTER).getValue().toString();
                                String div_ds = dataSnapshot.child(DC.DIVISION).getValue().toString();
                                String dept_ds = dataSnapshot.child(DC.DEPARTMENT).getValue().toString();
                                String sub_key_ds = dataSnapshot.child(DC.SUBJECT_KEY).getValue().toString();

                                db_ref.child(DC.DEPARTMENT).child(dept_ds).child(DC.SEMESTER).child(sem_ds).child(DC.DIVISION).child(div_ds).child(DC.FACULTY_SUBJECT)
                                        .child(sub_key_ds).removeValue();
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        Toast.makeText(getContext(), "Faculty Database Deleted...", Toast.LENGTH_SHORT).show();

                        edit_ibtn.setEnabled(false);
                        del_ibtn.setEnabled(false);
                        list_tv.setVisibility(View.INVISIBLE);
                        sprofile_sv.setVisibility(View.INVISIBLE);

                        //--------------------------ALERT DIALOG FOR DELETE STUDENT ACCOUNT-------------------

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                        alertBuilder.setTitle("Delete Faculty Account                  ' " + fid_et.getText().toString() + "@ddu.com '");
                        alertBuilder.setMessage("Redirecting to Firebase Authentication...");
                        alertBuilder.setPositiveButton("Go",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        db_ref.child(DC.FACULTIES).child(fac_key).removeValue();

                                        Intent i = new Intent(getContext(), Common_Firebase_WebView_Activity.class);
                                        startActivity(i);
                                    }
                                });

                        AlertDialog delDialog = alertBuilder.create();
                        delDialog.show();
                    }
                } else {
                    Toast.makeText(getContext(), "View Faculty Profile first...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nextsub_ibtn = (ImageButton) v.findViewById(R.id.nextsub_ibtn);
        nextsub_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MANAGE_SUB == 0)
                {
                    MANAGE_SUB = 1;
                    nextsub_ibtn.setImageResource(R.drawable.admin_manage_subject_icon);
                    sub_tv.setPadding(85,0,0,0);
                    f_subject_arr.clear();

                    query = db_ref.child(DC.FACULTIES).child(fac_key).child(DC.SUBJECT_NAMES);
                    listener = query.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    String sname_ds = dataSnapshot.child(DC.SUBJECT_NAME).getValue().toString();
                                    String sem_ds = dataSnapshot.child(DC.SEMESTER).getValue().toString();
                                    String div_ds = dataSnapshot.child(DC.DIVISION).getValue().toString();
                                    String dept_ds = dataSnapshot.child(DC.DEPARTMENT).getValue().toString();

                                    f_subject_arr.add(sname_ds+" ( "+dept_ds+" - "+sem_ds+" - "+div_ds+" )");
                                    fac_sub_spinner.setAdapter(adapter);
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
                else if (MANAGE_SUB == 1)
                {
                    if (VIEW_FLAG == 1 && EDIT_FLAG == 1)
                    {
                        Toast.makeText(getContext(), "Edit Faculty Profile first...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        query.removeEventListener(listener);

                        Intent i = new Intent(getContext(),Admin_Manage_Subject.class);
                        Bundle b = new Bundle();

                        b.putString("FAC_KEY", fac_key);
                        b.putString("NAME", name_et.getText().toString());
                        i.putExtras(b);

                        startActivity(i);
                    }
                }
            }
        });
    }

    public boolean checkFId(String fid)
    {
        if (fid.length() == 3)
        {
            int name1 = fid.charAt(0);
            int name2 = fid.charAt(1);
            int name3 = fid.charAt(2);

            if (name1 > 96 && name1 < 123 && name2 > 96 && name2 < 123 &&  name3 > 96 && name3 < 123)
            {
                return true;
            }
        }
        return false;
    }
}
