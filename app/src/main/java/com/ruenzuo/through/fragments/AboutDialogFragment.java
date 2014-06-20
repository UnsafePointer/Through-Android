package com.ruenzuo.through.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ruenzuo.through.R;

/**
 * Created by ruenzuo on 19/04/14.
 */
public class AboutDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.about_dialog_fragment, null);
        TextView txtViewDeveloper = (TextView) view.findViewById(R.id.txtViewDeveloper);
        txtViewDeveloper.setText(Html.fromHtml("Through by Renzo Crisóstomo <a href='http://www.twitter.com/Ruenzuo'>@Ruenzuo</a>"));
        txtViewDeveloper.setMovementMethod(LinkMovementMethod.getInstance());
        TextView txtViewOpenSource = (TextView) view.findViewById(R.id.txtViewOpenSource);
        txtViewOpenSource.setText(Html.fromHtml("Through for Android is built using open source software: <a href='com.ruenzuo.pokeffective://open-source/license'>license</a>"));
        txtViewOpenSource.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(view).setPositiveButton("Close", null);
        return builder.create();
    }

}
