package cn.edu.buaamooc.view;

import java.util.ArrayList;
import java.util.List;

public class Node {

	/** 当前节点层级 */
	private int deepLevel;

	/** 当前节点内容 */
	private String label;
	private String url="";

	/** 父节点 */
	private Node parentNode;

	/** 是否展开了 */
	private boolean isExpand;

	public boolean isExpand() {
		return isExpand;
	}

	public void setExpand(boolean isExpand) {
		this.isExpand = isExpand;
	}

	/** 子节点列表 */
	private List<Node> childList;

	public int getChildCount() {
		if (childList == null) {
			return 0;
		} else {
			return childList.size();
		}
	}

	public List<Node> getChildList() {
		return childList;
	}

	public Node(Node parent, String label) {
		this.parentNode = parent;
		this.label = label;
		if (parent != null) {
			if (parent.childList == null) {
				parent.childList = new ArrayList<Node>();
			}
			parent.childList.add(this);
			this.deepLevel = parent.deepLevel + 1;
		}
	}

	public Node(Node parent, String label,String url) {
		this.url=url;
		this.parentNode = parent;
		this.label = label;
		if (parent != null) {
			if (parent.childList == null) {
				parent.childList = new ArrayList<Node>();
			}
			parent.childList.add(this);
			this.deepLevel = parent.deepLevel + 1;
		}
	}

	public String getUrl(){return url;}

	public int getDeepLevel() {
		return deepLevel;
	}

	public void setDeepLevel(int deepLevel) {
		this.deepLevel = deepLevel;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

}
