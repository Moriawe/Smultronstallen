package com.moriawe.smultronstallen;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceDecorator extends RecyclerView.ItemDecoration {

    private final int mSpaceHeight;

    public SpaceDecorator(int mSpaceHeight) {
        this.mSpaceHeight = mSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = mSpaceHeight;
//        outRect.top = mSpaceHeight;
//        outRect.left = mSpaceHeight;
//        outRect.right = mSpaceHeight;
    }
}
