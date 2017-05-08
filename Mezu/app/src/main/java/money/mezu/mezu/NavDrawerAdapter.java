package money.mezu.mezu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import com.squareup.picasso.Picasso;

public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[];
    private int mIcons[];

    private String name;
    private Uri image;
    private String email;

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them
    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView Name;
        TextView email;

        public ViewHolder(View itemView,int ViewType) {
            super(itemView);

            if(ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                Holderid = 1;
            }
            else{
                Name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                profile = (ImageView) itemView.findViewById(R.id.circleView);
                Holderid = 0;
            }
        }
    }

    NavDrawerAdapter(String Titles[],int Icons[],String Name,String Email, Uri Image){
        mNavTitles = Titles;
        mIcons = Icons;
        name = Name;
        email = Email;
        image = Image;
    }

    @Override
    public NavDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_item_row,parent,false);
            ViewHolder vhItem = new ViewHolder(v,viewType);

            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_header,parent,false);
            ViewHolder vhHeader = new ViewHolder(v,viewType);

            return vhHeader;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(NavDrawerAdapter.ViewHolder holder, int position) {
        if(holder.Holderid ==1) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position -1]);
        }
        else{
            if (image != null) {
                Picasso.with(staticContext.mContext).load(image).into(holder.profile);
            } else {
                holder.profile.setImageResource(R.drawable.rich_man_small);
            }
            holder.Name.setText(name);
            holder.email.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}