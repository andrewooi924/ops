package com.optcg.app;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CardDetailsDialogFragment extends DialogFragment {

    private static final String ARG_IMAGE_RES_ID = "imageResId";
    private static final String ARG_CARD_NAME = "cardName";
    private static final String ARG_CARD_ID = "cardId";
    private static final String ARG_CARD_RARITY = "cardRarity";
    private static final String ARG_CARD_ROLE = "cardRole";
    private static final String ARG_CARD_COST = "cardCost";
    private static final String ARG_CARD_ATTRIBUTE = "cardAttribute";
    private static final String ARG_CARD_POWER = "cardPower";
    private static final String ARG_CARD_COUNTER = "cardCounter";
    private static final String ARG_CARD_COLOR = "cardColor";
    private static final String ARG_CARD_TYPE = "cardType";
    private static final String ARG_CARD_EFFECT = "cardEffect";
    private static final String ARG_CARD_SET = "cardSet";
    private static final String ARG_CARD_COUNT = "cardCount";

    public static CardDetailsDialogFragment newInstance(int imageResId, String cardName, String cardId, String cardRarity, String cardRole, String cardCost, String cardAttribute, String cardPower, String cardCounter, String cardColor, String cardType, String cardEffect, String cardSet, String cardCount) {
        CardDetailsDialogFragment fragment = new CardDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES_ID, imageResId);
        args.putString(ARG_CARD_NAME, cardName);
        args.putString(ARG_CARD_ID, cardId);
        args.putString(ARG_CARD_RARITY, cardRarity);
        args.putString(ARG_CARD_ROLE, cardRole);
        args.putString(ARG_CARD_COST, cardCost);
        args.putString(ARG_CARD_ATTRIBUTE, cardAttribute);
        args.putString(ARG_CARD_POWER, cardPower);
        args.putString(ARG_CARD_COUNTER, cardCounter);
        args.putString(ARG_CARD_COLOR, cardColor);
        args.putString(ARG_CARD_TYPE, cardType);
        args.putString(ARG_CARD_EFFECT, cardEffect);
        args.putString(ARG_CARD_SET, cardSet);
        args.putString(ARG_CARD_COUNT, cardCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_details_dialog, container, false);

        ImageView cardImage = view.findViewById(R.id.cardImage);
        TextView cardNameText = view.findViewById(R.id.cardName);
        TextView cardIdText = view.findViewById(R.id.cardId);
        TextView cardRarityText = view.findViewById(R.id.cardRarity);
        TextView cardRoleText = view.findViewById(R.id.cardRole);
        TextView cardCostText = view.findViewById(R.id.cardCost);
        TextView cardAttributeText = view.findViewById(R.id.cardAttribute);
        TextView cardPowerText = view.findViewById(R.id.cardPower);
        TextView cardCounterText = view.findViewById(R.id.cardCounter);
        TextView cardColorText = view.findViewById(R.id.cardColor);
        TextView cardTypeText = view.findViewById(R.id.cardType);
        TextView cardEffectText = view.findViewById(R.id.cardEffect);
        TextView cardSetText = view.findViewById(R.id.cardSet);
        TextView cardCountText = view.findViewById(R.id.cardCount);

        View colorDivider = view.findViewById(R.id.colorDivider);

        // Set background to transparent
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Retrieve and display data
        if (getArguments() != null) {
            int imageResId = getArguments().getInt(ARG_IMAGE_RES_ID);
            String cardName = getArguments().getString(ARG_CARD_NAME);
            String cardId = getArguments().getString(ARG_CARD_ID);
            String cardRarity = getArguments().getString(ARG_CARD_RARITY);
            String cardRole = getArguments().getString(ARG_CARD_ROLE);
            String cardCost = getArguments().getString(ARG_CARD_COST);
            String cardAttribute = getArguments().getString(ARG_CARD_ATTRIBUTE);
            String cardPower = getArguments().getString(ARG_CARD_POWER);
            String cardCounter = getArguments().getString(ARG_CARD_COUNTER);
            String cardColor = getArguments().getString(ARG_CARD_COLOR);
            String cardType = getArguments().getString(ARG_CARD_TYPE);
            String cardEffect = getArguments().getString(ARG_CARD_EFFECT);
            String cardSet = getArguments().getString(ARG_CARD_SET);
            String cardCount = getArguments().getString(ARG_CARD_COUNT);

            cardImage.setImageResource(imageResId);
            cardNameText.setText(cardName);
            cardIdText.setText(cardId);
            cardRarityText.setText(cardRarity);
            cardRoleText.setText(cardRole);
            cardCostText.setText(cardCost);
            cardAttributeText.setText(cardAttribute);
            cardPowerText.setText(cardPower);
            cardCounterText.setText(cardCounter);
            cardColorText.setText(cardColor);
            cardTypeText.setText(cardType);
            cardEffectText.setText(cardEffect);
            cardSetText.setText(cardSet);
            cardCountText.setText(String.valueOf(cardCount));

            setDividerColor(colorDivider, cardColor);
        }
        return view;
    }

    private void setDividerColor(View divider, String colorString) {
        colorString = colorString.replace("Color:", "").trim();
        if (colorString.contains("/")) {
            String[] colors = colorString.split("/");
            int color1 = Color.parseColor(colors[0].toLowerCase().trim());
            int color2 = Color.parseColor(colors[1].toLowerCase().trim());
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT, new int[]{color1, color2});
            divider.setBackground(gradient);
        }
        else {
            int color = Color.parseColor(colorString.toLowerCase().trim());
            divider.setBackgroundColor(color);
        }
    }
}