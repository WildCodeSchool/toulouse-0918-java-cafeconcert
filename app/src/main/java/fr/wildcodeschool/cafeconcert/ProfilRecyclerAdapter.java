package fr.wildcodeschool.cafeconcert;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProfilRecyclerAdapter extends RecyclerView.Adapter<ProfilRecyclerAdapter.ViewHolder> {

    private ArrayList<Bar> mBar = new ArrayList<>();
    private Context context;

    public ProfilRecyclerAdapter(ArrayList<Bar> bars, Context context) {
        mBar = bars;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivLogo;

        public ViewHolder(View v) {
            super(v);
            this.ivLogo = v.findViewById(R.id.iv_logo_bar);
        }
    }

    @Override
    public ProfilRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fav, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProfilRecyclerAdapter.ViewHolder holder, int position) {

        Bar barModel = mBar.get(position);
        //holder.ivLogo.setImageBitmap(barModel.getLogo());

        Glide.with(context).load(barModel.getLogo()).into(holder.ivLogo);
    }

    @Override
    public int getItemCount() {

        return mBar.size();
    }
}
