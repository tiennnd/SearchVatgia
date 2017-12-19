package com.tiennd.searchdemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tiennd.searchdemo.activity.DetailActivity;
import com.tiennd.searchdemo.activity.MainActivity;
import com.tiennd.searchdemo.model.Product;

import java.util.List;

/**
 * Created by nguyentien on 12/19/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductVH> {

    private List<Product> productList;
    private Activity activity;

    public ProductAdapter(List<Product> productList, Activity activity) {
        this.productList = productList;
        this.activity = activity;
    }

    @Override
    public ProductVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductVH(parent, LayoutInflater.from(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ProductVH holder, final int position) {
        holder.title.setText(productList.get(position).getTitle());
        holder.price.setText(productList.get(position).getPrice());
        try {
            Picasso.with(activity).load(productList.get(position).getThumbnail()).into(holder.imageView);
        } catch (Exception e) {
            Log.e("ADAPTER",e.getMessage());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DetailActivity.class);
                intent.putExtra(MainActivity.LINK, productList.get(position).getLink());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void addProduct(List<Product> productList) {
        this.productList.addAll(productList);
    }

    class ProductVH extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView title, price;
        public ProductVH(ViewGroup viewGroup, LayoutInflater inflater) {
            super(inflater.inflate(R.layout.item_product, viewGroup, false));
            imageView = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
        }
    }
}
