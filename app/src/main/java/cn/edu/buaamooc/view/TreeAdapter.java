package cn.edu.buaamooc.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.nmbb.oplayer.ui.player.VideoActivity;

import java.util.List;

import cn.edu.buaamooc.R;

public class TreeAdapter extends BaseAdapter {

	private Context context;
	/** 当前需要显示的数据 */
	private List<Node> mDataList;
	/** 节点点击事件 */
	NodeOnClickListener mNodeOnClickListener;

	public TreeAdapter(Context context, List<Node> objects) {
		this.context = context;
		this.mDataList = objects;
		mNodeOnClickListener = new NodeOnClickListener();
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Node getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.listview_item_course_detail, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.label = (TextView) convertView.findViewById(R.id.label);
			convertView.setTag(holder);
			convertView.setOnClickListener(mNodeOnClickListener);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Node node = getItem(position);
		// 设置深度,层级越深距离左边越远
		convertView.setPadding(30 * node.getDeepLevel()+10, 10, 10, 10);
		if (node.getChildCount() == 0) {// 如果沒有子节点说明为叶子节点,去掉icon
			holder.icon.setVisibility(View.INVISIBLE);
		} else {
			holder.icon.setVisibility(View.VISIBLE);
			if (node.isExpand()) {//如果需要显示判断一下是否是需要显示展开的样式
				holder.icon.setImageResource(R.drawable.expand);
			} else {
				holder.icon.setImageResource(R.drawable.unexpand);
			}
		}
		holder.label.setTag(node);//label的tag里面存放Node,方便点击事件处理
		holder.label.setText(node.getLabel());

		return convertView;
	}

	/** 目录点击事件 */
	class NodeOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			ViewHolder holder = (ViewHolder) v.getTag();
			Node node = (Node) holder.label.getTag();
			// 如果存在孩子节点就需要做展开或者隐藏操作
			if (node.getChildCount() > 0) {
				TreeUtils.filterNodeList(mDataList, node);
			} else {
				Intent intent = new Intent(context, VideoActivity.class);
				String url_t=node.getUrl();
				if(url_t!=null&&!url_t.equals("")) {
					intent.setData(Uri.parse(node.getUrl()));
//					intent.setData(Uri.parse("http://www.baidu.com"));
					// intent.putExtra("path", f.path);
					intent.putExtra("displayName", node.getLabel());
					context.startActivity(intent);
//				Toast.makeText(context, "点击了:" + node.getLabel(),
//						Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(context,"对不起，此视频不存在。",Toast.LENGTH_SHORT).show();
				}
				return;
			}
			notifyDataSetChanged();
		}
	}

	private static class ViewHolder {
		private ImageView icon;
		private TextView label;
	}

}
