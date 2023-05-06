package com.example.magic.screens;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magic.databinding.ItemEmptyItemBinding;
import com.example.magic.databinding.ItemItemBinding;
import com.example.magic.models.Item;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static int MAX_ITEMS = 12;

    public static int ITEM = 1;

    public static int EMPTY = 0;

    private List<Item> inventory = new ArrayList<>();

    public void updateInventory(List<Item> inventory) {
        this.inventory.clear();
        this.inventory.addAll(inventory);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < inventory.size()) {
            return ITEM;
        } else {
            return EMPTY;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM) {
            return new InventoryViewHolder(
                    ItemItemBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false)
            );
        } else {
            return new EmptyViewHolder(
                    ItemEmptyItemBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InventoryViewHolder) {
            ((InventoryViewHolder) holder).bind(inventory.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return MAX_ITEMS;
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        private ItemEmptyItemBinding binding;

        public EmptyViewHolder(ItemEmptyItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder {

        private ItemItemBinding binding;

        public InventoryViewHolder(ItemItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Item item) {
            binding.image.setImageResource(item.getResId());
        }
    }
}
