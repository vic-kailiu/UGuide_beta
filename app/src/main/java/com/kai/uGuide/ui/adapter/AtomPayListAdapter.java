package com.kai.uGuide.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kai.uGuide.AtomPayment;
import com.kai.uGuide.R;

import java.util.List;

public class AtomPayListAdapter extends ArrayAdapter<AtomPayment> {

	protected static final String LOG_TAG = AtomPayListAdapter.class.getSimpleName();
	
	private List<AtomPayment> items;
	private int layoutResourceId;
	private Context context;

	public AtomPayListAdapter(Context context, int layoutResourceId, List<AtomPayment> items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		AtomPaymentHolder holder = null;

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new AtomPaymentHolder();
		holder.atomPayment = items.get(position);
		holder.removePaymentButton = (ImageButton)row.findViewById(R.id.result_play);
		holder.removePaymentButton.setTag(holder.atomPayment);

		holder.name = (TextView)row.findViewById(R.id.title);
		holder.value = (ImageView)row.findViewById(R.id.logo);

		row.setTag(holder);

		setupItem(holder);
		return row;
	}

	private void setupItem(AtomPaymentHolder holder) {
		holder.name.setText(holder.atomPayment.getName());
		holder.value.setImageResource((int)(holder.atomPayment.getValue()));
	}

	public static class AtomPaymentHolder {
		AtomPayment atomPayment;
		TextView name;
        ImageView value;
		ImageButton removePaymentButton;
	}
}
