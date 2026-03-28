package com.example.git_repo_4;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemSpacingDecorationJava extends RecyclerView.ItemDecoration {

    private final int verticalSpacingPx;

    public ItemSpacingDecorationJava(int verticalSpacingPx) {
        this.verticalSpacingPx = verticalSpacingPx;
    }

    @Override
    public void getItemOffsets(
            @NonNull Rect outRect,
            @NonNull View view,
            @NonNull RecyclerView parent,
            @NonNull RecyclerView.State state
    ) {
        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        if (position == 0) {
            outRect.top = verticalSpacingPx;
        }
        outRect.bottom = verticalSpacingPx;
    }
}

