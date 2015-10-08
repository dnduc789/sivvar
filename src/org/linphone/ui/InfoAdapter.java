package org.linphone.ui;

import java.util.ArrayList;

import org.linphone.LinphoneActivity;

import com.sivvar.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InfoAdapter extends ArrayAdapter<Info> {
	private final ArrayList<Info> value;
	private final Context context;
	private Info mLisItem;

	private LayoutInflater mInflater;

	public InfoAdapter(Context context, ArrayList<Info> value) {
		super(context, 0, value);
		this.context = context;
		this.value = value;
		this.mInflater = (LayoutInflater) LinphoneActivity.instance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return value.size();
	}

	@Override
	public Info getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder mHolder;
		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.infor_item, null);
			mHolder = new ViewHolder();

			mHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
			mHolder.mContent = (TextView) convertView.findViewById(R.id.content);
			convertView.setTag(mHolder);

		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mLisItem = value.get(position);
		mHolder.mTitle.setText(mLisItem.getTitle());
		mHolder.mContent.setText(mLisItem.getContent());

		return convertView;

	}

	private class ViewHolder {
		private TextView mTitle;
		private TextView mContent;
	}
}
