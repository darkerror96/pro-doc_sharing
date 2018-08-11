package sem8.integrate.app.mainapp_1;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Common_List_Adapter extends ArrayAdapter<String>
{
    private final Activity context;
    private final ArrayList<String> title;

    public Common_List_Adapter(Activity context, ArrayList<String> title)
    {
        super(context, R.layout.common_listview, title);

        this.context = context;
        this.title = title;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.common_listview, null, true);

        TextView titleText=(TextView)rowView.findViewById(R.id.title_tv);
        titleText.setText(title.get(position));

        return rowView;
    }
}
