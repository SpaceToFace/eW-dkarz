package com.example.test;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AtlasAdapter extends RecyclerView.Adapter<AtlasAdapter.MyViewHolder> {
    ArrayList<AtlasItems> atlasItemsList;
    Context context;

    public AtlasAdapter(ArrayList<AtlasItems> atlasItemsList, Context context) {
        this.atlasItemsList = atlasItemsList;
        this.context = context;
    }

    public void setFilteredList(ArrayList<AtlasItems> filteredList) {
        this.atlasItemsList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.atlas_card_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.atlasTitle.setText(atlasItemsList.get(position).getAtlasTitle());
        holder.atlasProtection.setText(atlasItemsList.get(position).getAtlasProtection());
        holder.atlasSize.setText(atlasItemsList.get(position).getAtlasSize());
        holder.atlasImage.setImageResource(atlasItemsList.get(position).getAtlasImage());

    }

    @Override
    public int getItemCount() {
        return atlasItemsList.size();
    }


        public class MyViewHolder extends RecyclerView.ViewHolder  {

            TextView atlasTitle;

            TextView atlasProtection;
            TextView atlasSize;
            ImageView atlasImage;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                context = itemView.getContext();
                atlasTitle = itemView.findViewById(R.id.atlas_title);
                atlasProtection = itemView.findViewById(R.id.atlas_protection);
                atlasSize = itemView.findViewById(R.id.atlas_size);
                atlasImage = itemView.findViewById(R.id.atlas_image);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int itemPosition = getLayoutPosition();
                        Intent intent = new Intent(v.getContext(),FishActivity.class);
                        intent.putExtra("Title", atlasItemsList.get(itemPosition).getAtlasTitle());
                        intent.putExtra("Protection", atlasItemsList.get(itemPosition).getAtlasProtection());
                        intent.putExtra("Size", atlasItemsList.get(itemPosition).getAtlasSize());
                        intent.putExtra("Image", atlasItemsList.get(itemPosition).getAtlasImage());
                        context.startActivity(intent);
                    }
                });
            }
        }
    }