package sem8.integrate.app.mainapp_1.Faculty;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class F_Notices_Fragment extends Fragment {

    View v;

    ListView notice_lv;

    Common_List_Adapter adapter;

    ArrayList<String> title_al;

    FirebaseAuth mAuth;
    DatabaseReference getNotice;
    FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.common_notices_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Notices");

        mAuth = FirebaseAuth.getInstance();
        getNotice = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        title_al = new ArrayList<>();

        adapter = new Common_List_Adapter((Activity) getContext(), title_al);

        notice_lv = (ListView)v.findViewById(R.id.notice_lv);
        notice_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        });

        getNotice.child(DC.FACULTIES).child(user.getUid()).child(DC.FACULTY_NOTICES).limitToFirst(50).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                title_al.add(dataSnapshot.child(DC.TITLE).getValue().toString());
                notice_lv.setAdapter(adapter);
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
