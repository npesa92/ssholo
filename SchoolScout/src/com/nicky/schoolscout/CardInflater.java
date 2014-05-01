package com.nicky.schoolscout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/*import com.tipsandtricks.HelloCard.R;
import com.tipsandtricks.HelloCard.adapters.BaseInflaterAdapter;
import com.tipsandtricks.HelloCard.adapters.CardItemData;
import com.tipsandtricks.HelloCard.adapters.IAdapterViewInflater;*/

/**
 * Created with IntelliJ IDEA.
 * User: Justin
 * Date: 10/6/13
 * Time: 12:47 AM
 */
public class CardInflater implements IAdapterViewInflater<CardItemData>
{
	
	
	
	@Override
	public View inflate(final BaseInflaterAdapter<CardItemData> adapter, final int pos, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if (convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.list_item_card, parent, false);
			holder = new ViewHolder(convertView);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		final CardItemData item = adapter.getTItem(pos);
		holder.updateDisplay(item);

		return convertView;
	}

	private class ViewHolder
	{
		private View m_rootView;
		private TextView m_text1;
		private TextView m_text2;
		private TextView m_text3;
		private TextView m_text4;
		private ImageView m_image;

		public ViewHolder(View rootView)
		{
			m_rootView = rootView;
			m_text1 = (TextView) m_rootView.findViewById(R.id.text1);
			m_text2 = (TextView) m_rootView.findViewById(R.id.text2);
			m_text3 = (TextView) m_rootView.findViewById(R.id.text3);
			m_text4 = (TextView) m_rootView.findViewById(R.id.schname);
			m_image = (ImageView) m_rootView.findViewById(R.id.image1);
			rootView.setTag(this);
		}

		public void updateDisplay(CardItemData item)
		{
			m_text1.setText(item.getText1());
			m_text2.setText(item.getText2());
			m_text3.setText(item.getText3());
			m_text4.setText(item.getText1());
			m_image.setImageResource(item.getImage());
		}
	}
}