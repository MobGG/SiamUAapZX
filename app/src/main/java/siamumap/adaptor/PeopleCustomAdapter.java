package siamumap.adaptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.siam.siamumap.AppMethod;
import edu.siam.siamumap.R;
import siamumap.dto.People;
import siamumap.dto.Post;

/**
 * Created by Mob on 09-Dec-15.
 */
public class PeopleCustomAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<People> people;
    People person;
    private ViewHolder myViewHolder;
    Bitmap bitmap;

    private static class ViewHolder {
        ImageView image;
        TextView id, name, faculty, department, loading;
        int position;
    }

    public PeopleCustomAdapter(Context context, ArrayList<People> people) {
        this.mContext = context;
        this.people = people;
    }

    @Override
    public int getCount() {
        return people.size();
    }

    @Override
    public Object getItem(int position) {
        return people.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.people_custom_listview, parent, false);
            myViewHolder = new ViewHolder();
            myViewHolder.image = (ImageView) convertView.findViewById(R.id.people_image);
            myViewHolder.id = (TextView) convertView.findViewById(R.id.people_id);
            myViewHolder.name = (TextView) convertView.findViewById(R.id.people_name);
            myViewHolder.faculty = (TextView) convertView.findViewById(R.id.people_faculty);
            myViewHolder.department = (TextView) convertView.findViewById(R.id.people_department);
            myViewHolder.loading = (TextView) convertView.findViewById(R.id.loading);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (ViewHolder) convertView.getTag();
        }

        myViewHolder.position = position;
        person = people.get(position);

        new decodeTask(position, myViewHolder, person).execute();
        myViewHolder.id.setText(person.getPeopleID());
        myViewHolder.name.setText(person.getPeopleName());
        myViewHolder.name.setTextColor(Color.parseColor("#000000"));
        myViewHolder.faculty.setText(person.getPeopleFaculty());
        myViewHolder.faculty.setTextColor(Color.parseColor("#000000"));
        myViewHolder.department.setText(person.getPeopleDepartment());
        myViewHolder.department.setTextColor(Color.parseColor("#000000"));
        return convertView;
    }

    private static class decodeTask extends AsyncTask<Void, Void, Bitmap> {
        AppMethod appMethod = new AppMethod();
        private int mPosition;
        private ViewHolder mHolder;
        private People mPpl;

        public decodeTask(int position, ViewHolder holder, People people) {
            mPosition = position;
            mHolder = holder;
            mPpl = people;
        }
        @Override
        protected void onPreExecute() {
            mHolder.image.setVisibility(View.INVISIBLE);
            mHolder.loading.setVisibility(View.VISIBLE);
        }
        @Override
        protected Bitmap doInBackground(Void... arg0) {
            if (mHolder.position == mPosition && mPpl.getPeopleImage() != null) {
                return mPpl.getPeopleImage();
            } else {
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mHolder.position == mPosition && bitmap != null) {
                mHolder.image.setImageBitmap(bitmap);
            } else if (mHolder.position == mPosition && bitmap == null) {
                mHolder.image.setImageResource(R.drawable.no_picture);
            }
            mHolder.loading.setVisibility(View.INVISIBLE);
            mHolder.image.setVisibility(View.VISIBLE);
        }
    }
}
