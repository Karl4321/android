package com.example.mireaproject.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {

    public interface FileSelectionListener {
        void onFileSelected(File file);
    }

    private final List<FileEntry> files;
    private final FileSelectionListener selectionListener;

    public FileListAdapter(List<FileEntry> files, FileSelectionListener listener) {
        this.files = files;
        this.selectionListener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileEntry entry = files.get(position);
        holder.fileNameView.setText(entry.getDisplayName());

        holder.itemView.setOnClickListener(v -> {
            if (selectionListener != null) {
                selectionListener.onFileSelected(entry.getFileReference());
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameView;

        FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameView = itemView.findViewById(android.R.id.text1);
        }
    }
}