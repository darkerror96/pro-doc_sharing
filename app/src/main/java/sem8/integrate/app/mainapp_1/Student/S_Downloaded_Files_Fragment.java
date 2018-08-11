package sem8.integrate.app.mainapp_1.Student;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import sem8.integrate.app.mainapp_1.Common_List_Adapter;
import sem8.integrate.app.mainapp_1.DC;
import sem8.integrate.app.mainapp_1.R;

public class S_Downloaded_Files_Fragment extends Fragment {

    View v;

    ListView dfiles_lv;

    ArrayList<String> ufilename_arr;
    Common_List_Adapter adapter;

    FirebaseAuth mAuth;
    DatabaseReference db_ref;
    FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.student_s_downloaded_files_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Downloaded Files");

        mAuth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        ufilename_arr = new ArrayList<>();

        adapter = new Common_List_Adapter((Activity) getContext(), ufilename_arr);

        dfiles_lv = (ListView)v.findViewById(R.id.dfiles_lv);
        dfiles_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //-------------------------OPEN DOWNLOAD FOLDER FROM APPLICATION--------------------

                //Toast.makeText(getContext(), "Opening Download Folder...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
            }
        });

        db_ref.child(DC.DEPARTMENT).child(user.getEmail().substring(0,2).toUpperCase().toString()).child(DC.SEMESTER).child(user.getEmail().substring(2,3).toString())
                .child(DC.DIVISION).child(user.getEmail().substring(3,4).toUpperCase().toString()).child(DC.STUDENTS).child(user.getEmail().substring(4,7).toString())
                .child(DC.DOWNLOADED_FILES).limitToFirst(50).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ufilename_arr.add(dataSnapshot.child(DC.TITLE).getValue().toString());
                dfiles_lv.setAdapter(adapter);
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
}
