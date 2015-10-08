package org.linphone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ListView;

import java.util.ArrayList;

import org.linphone.ui.Info;
import org.linphone.ui.InfoAdapter;

import com.sivvar.R;

public class ChatListEmptyFragment extends Fragment {
	private ArrayList<Info> mLinkedList;
	private InfoAdapter customAdapter;
	private ListView listviewData;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.chatlist_coming_soon, container, false);
		LinphoneActivity.instance().hideStatusBar();
		listviewData = (ListView) view.findViewById(R.id.info_list);
		mLinkedList = new ArrayList<Info>();
		for (int index = 0; index < 8; index++) {
			if (index % 2 == 0) {
				mLinkedList.add(new Info("firstmonie", "First monie channel: first monie"));
			}else {
//				mLinkedList.add(new Info("firstmonie", "First monie channel: first monie introduce more channel"
//						+ "of payment"));
				mLinkedList.add(new Info("firstmonie", "First monie channel: first monie introduce more channel"
						+ "of payment through a partnew ship with the codec systems limited"
						+ " a leading voice solution company. more detail @wwwtcsl.tc/1L4AP27AP"
						+ " a leading voice solution company. more detail @wwwtcsl.tc/1L4AP27AP"
						+ " a leading voice solution company. more detail @wwwtcsl.tc/1L4AP27AP"
						+ " a leading voice solution company. more detail @wwwtcsl.tc/1L4AP27AP"
						+ " a leading voice solution company. more detail @wwwtcsl.tc/1L4AP27AP"
						+ " a leading voice solution company. more detail @wwwtcsl.tc/1L4AP27AP"
						+ " a leading voice solution company. more detail @wwwtcsl.tc/1L4AP27AP"));
			}
					}
		customAdapter = new InfoAdapter(LinphoneActivity.instance(), mLinkedList);
		listviewData.setAdapter(customAdapter);
//		mLinkedList.add(new Info("firstmonie", "First monie channel: first monie introduce more channel"
//				+ "of payment through a partnew ship with the codec systems limited"
//				+ " a leading voice solution company. more detail @wwwtcsl.tc/1L4AP27AP"));
		return view;
	}
	
}
