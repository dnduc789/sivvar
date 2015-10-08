package com.sivvar.ui.grid;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sivvar.AppUtil;
import com.sivvar.R;
import com.sivvar.objects.IndustryDTO;
import com.sivvar.objects.ServiceDTO;


public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private boolean industriesView;
	public List<IndustryDTO> industryItems;
	public List<ServiceDTO> serviceItems;
	private LayoutInflater inflater;
	
	// Constructor
	public ImageAdapter(Context c, List<ServiceDTO> items, LayoutInflater layoutInflater){
		mContext = c;
		serviceItems = items;
		inflater = layoutInflater;
		industriesView = false;
	}
	
	public ImageAdapter(Context c, List<IndustryDTO> items, LayoutInflater layoutInflater, boolean industriesView){
		mContext = c;
		industryItems = items;
		inflater = layoutInflater;
		this.industriesView = industriesView;
	}

	@Override
	public int getCount() {
		if (industriesView) {
			return industryItems.size();
		}
		return serviceItems.size();
	}

	@Override
	public Object getItem(int position) {
		if (industriesView) {
			return industryItems.get(position);
		}
		return serviceItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public boolean isEnabled(int position) {
		if (industriesView) {
			return true;
		}
		return ServiceDTO.SERVICE_STATUS_ACTIVE.equalsIgnoreCase(serviceItems.get(position).getServiceStatus());
	}
	
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		View view = inflater.inflate(R.layout.service_item, null);
		if (industriesView) {
			TextView itemText = (TextView) view.findViewById(R.id.item_text);
			SquareImageView itemImage = (SquareImageView) view.findViewById(R.id.item_image);
			ProgressBar loadingProgress = (ProgressBar) view.findViewById(R.id.image_loading);
			itemText.setText(industryItems.get(position).getIndustryName());
			AppUtil.loadLogo(industryItems.get(position), itemImage, loadingProgress);
	        itemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			TextView itemText = (TextView) view.findViewById(R.id.item_text);
			SquareImageView itemImage = (SquareImageView) view.findViewById(R.id.item_image);
			ProgressBar loadingProgress = (ProgressBar) view.findViewById(R.id.image_loading);
			itemText.setText(serviceItems.get(position).getServiceName());
			AppUtil.loadLogo(serviceItems.get(position), itemImage, loadingProgress);
	        itemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
		
        return view;
	}

}
