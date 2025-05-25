package com.example.coursework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.domain.entity.MaterialCatalog;
import com.example.coursework.domain.entity.ToolCatalog;

import java.util.ArrayList;
import java.util.List;

public class CatalogPagerAdapter extends RecyclerView.Adapter<CatalogPagerAdapter.PageViewHolder> {

    private final Context context;
    private List<MaterialCatalog> materials = new ArrayList<>();
    private List<ToolCatalog> tools = new ArrayList<>();

    public CatalogPagerAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<MaterialCatalog> materials, List<ToolCatalog> tools) {
        this.materials = materials;
        this.tools = tools;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.catalog_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        if (position == 0) {
            holder.listView.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, materials));
        } else {
            holder.listView.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, tools));
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        ListView listView;
        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            listView = itemView.findViewById(R.id.catalogListView);
        }
    }
}
