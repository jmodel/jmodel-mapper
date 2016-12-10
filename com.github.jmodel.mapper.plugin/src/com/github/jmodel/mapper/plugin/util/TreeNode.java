package com.github.jmodel.mapper.plugin.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TreeNode<T> implements Iterable<TreeNode<T>> {

	private T data;
	private TreeNode<T> parent;
	private Map<TreeNode<T>, NodeMeta> children;

	public TreeNode(T data) {
		this.data = data;
		this.children = new HashMap<TreeNode<T>, NodeMeta>();
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public TreeNode<T> getParent() {
		return parent;
	}

	public void setParent(TreeNode<T> parent) {
		this.parent = parent;
	}

	public void addChild(TreeNode<T> childNode, NodeMeta meta) {
		childNode.parent = this;
		this.children.put(childNode, meta);
	}

	public Map<TreeNode<T>, NodeMeta> getChildren() {
		return children;
	}

	@Override
	public Iterator<TreeNode<T>> iterator() {
		return children.keySet().iterator();
	}

}
