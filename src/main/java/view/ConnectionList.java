/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import controller.Controller;

public class ConnectionList extends JPanel {

	private final JTree tree;
	private final DefaultMutableTreeNode rootNode;
	private final DefaultTreeModel treeModel;

    public ConnectionList() {
		this.setPreferredSize(new Dimension(300,100));
		this.setLayout(new BorderLayout());
		rootNode = new DefaultMutableTreeNode("Root Node");
		treeModel = new DefaultTreeModel(rootNode);
		tree = new JTree(treeModel);
		this.tree.setBackground(new Color(0x1C, 0x1C, 0x1C));
		this.tree.setForeground(new Color(0xFF, 0xFF, 0xFF));
        ScrollPane sp = new ScrollPane();
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// tree.expandRow(0);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		this.add(sp);
		sp.add(tree);
	}

	/**
	 * Clear all servers and channels
	 */
	public void clear() {
		rootNode.removeAllChildren();
		treeModel.reload();
	}

	/**
	 * Add new server
	 * @param name name of server
	 * @return New node of server
	 */
	public DefaultMutableTreeNode addServer(String name) {
		DefaultMutableTreeNode server;
		if ((server = this.getServer(name)) != null) {
			this.tree.setSelectionPath(new TreePath(server.getPath()));
			return server;
		}
		server = new DefaultMutableTreeNode(name);
		this.treeModel.insertNodeInto(server, this.rootNode,
				this.rootNode.getChildCount());
		this.treeModel.reload();
		tree.expandPath(new TreePath(server));
		this.tree.setSelectionPath(new TreePath(server.getPath()));
		return server;
	}

	/**
	 * Remove server
	 * @param name name of server
	 * @return always null
	 */
	public DefaultMutableTreeNode removeServer(String name) {
		DefaultMutableTreeNode server;
		if ((server = this.getServer(name)) == null) {
			return server;
		}
		treeModel.removeNodeFromParent(server);
		this.treeModel.reload();
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		return server;
	}

	/**
	 * @param name
	 * @return Node of server that matches name
	 */
	public DefaultMutableTreeNode getServer(String name) {
		Enumeration<DefaultMutableTreeNode> e = this.rootNode
				.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = e.nextElement();
			if (node.toString().equalsIgnoreCase(name)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Set selection to given server
	 * @param name
	 */
	public void selectServer(String name) {
		DefaultMutableTreeNode s = this.getServer(name);
		this.tree.setSelectionPath(new TreePath(s.getPath()));
	}

	/**
	 * Add new channel to view
	 * @param server_name name of server 
	 * @param channel_name name of channel
	 * @return Node of new channel
	 */
	public DefaultMutableTreeNode addChannel(String server_name,
			String channel_name) {
		DefaultMutableTreeNode s = this.getServer(server_name);
		DefaultMutableTreeNode c = new DefaultMutableTreeNode(channel_name);
		this.treeModel.insertNodeInto(c, s, s.getChildCount());
		this.treeModel.reload();
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		this.tree.setSelectionPath(new TreePath(c.getPath()));
		return c;
	}

	/**
	 * @param server_name name of server 
 	 * @param channel_name name of channel
	 * @return Node of channel that matches channel_name
	 */
	public DefaultMutableTreeNode getChannel(String server_name,
			String channel_name) {
		DefaultMutableTreeNode s = this.getServer(server_name);
		if (s == null) {
			return s;
		}

		Enumeration<DefaultMutableTreeNode> e = s.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = e.nextElement();
			if (node.toString().equalsIgnoreCase(channel_name)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Remove channel form view
	 * @param server_name name of server 
	 * @param channel_name name of channel
	 * @return always null
	 */
	public DefaultMutableTreeNode removeChannel(String server_name,
			String channel_name) {
		DefaultMutableTreeNode c = this.getChannel(server_name, channel_name);
		treeModel.removeNodeFromParent(c);
		this.treeModel.reload();
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		return null;
	}

	/**
	 * Set selection to given channel
	 * @param server_name name of server
	 * @param channel_name name of channel
	 */
	public void selectChannel(String server_name, String channel_name) {
		DefaultMutableTreeNode c = this.getChannel(server_name, channel_name);
		this.tree.setSelectionPath(new TreePath(c.getPath()));
	}

	/**
	 * Connect itself to listener
	 * @param c Controller object to be listener
	 */
	public void setListener(Controller c) {
		this.tree.addTreeSelectionListener(c);
	}
}
