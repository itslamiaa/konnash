package com.example.konnashapp;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ColorPicker extends BottomSheetDialogFragment {

    private GridLayout grid;

    public interface Listener {
        void onColorSelected(int color);
    }

    private Listener listener;

    public ColorPicker(Listener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.bottom_colors, container, false);
        Button cancelBtn = view.findViewById(R.id.cancel);

        cancelBtn.setOnClickListener(v -> {
            dismiss();  // quits from the bottom sheet color picker
        });

        grid = view.findViewById(R.id.colorGrid);

        int[] colors = getResources().getIntArray(R.array.palette);
        for (int color : colors) {
            grid.addView(createColorSquare(color));
        }

        return view;
    }

    private View createColorSquare(int color) {
        View v = new View(getContext());

        int size = dp(45);


        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = size;
        params.height = size;
        params.setMargins(dp(8), dp(8), dp(8), dp(8));

        v.setTag(color);
        v.setLayoutParams(params);
        v.setBackground(makeSquare(color)); // default state

        v.setOnClickListener(childView -> {
            listener.onColorSelected(color);
            dismiss();
        });

        return v;
    }

    private GradientDrawable makeSquare(int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(color);
        shape.setCornerRadius(dp(5));
        return shape;

    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}