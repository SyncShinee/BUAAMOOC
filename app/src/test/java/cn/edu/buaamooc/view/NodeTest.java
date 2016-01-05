package cn.edu.buaamooc.view;

import org.junit.Before;
import org.junit.Test;

import cn.edu.buaamooc.CONST;

import static org.junit.Assert.*;

/**
 * Created by skyxuan on 2016/1/5.
 */
public class NodeTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testIsExpand() throws Exception {
        Node node = new Node(null, "test");
        assertFalse(node.isExpand());
    }

    @Test
    public void testSetExpand() throws Exception {
        Node node = new Node(null, "test");
        node.setExpand(true);
        assertTrue(node.isExpand());
    }

    @Test
    public void testGetChildCount() throws Exception {
        Node root = new Node(null, "root");
        assertEquals(0, root.getChildCount());
        Node leaf = new Node(root, "leaf");
        assertEquals(1, root.getChildCount());
    }

    @Test
    public void testGetChildList() throws Exception {
        Node root = new Node(null, "root");
        assertNull(root.getChildList());
    }

    @Test
    public void testGetUrl() throws Exception {
        Node root = new Node(null, "root", "10.254.25.5");
        assertEquals(root.getUrl(), "10.254.25.5");
        Node leaf = new Node(root, "leaf", CONST.URL);
        assertEquals(leaf.getUrl(), null);
    }

    @Test
    public void testSetUrl() throws Exception {
        Node root = new Node(null, "root");
        assertEquals(root.getUrl(), "");
        root.setUrl("10.254.25.5");
        assertEquals(root.getUrl(), "10.254.25.5");
    }

    @Test
    public void testGetDeepLevel() throws Exception {
        Node root = new Node(null, "rt");
        assertEquals(root.getDeepLevel(), 0);
        Node leaf = new Node(root, "lf");
        assertEquals(leaf.getDeepLevel(), 1);
    }

    @Test
    public void testSetDeepLevel() throws Exception {
        Node root = new Node(null, "rt");
        root.setDeepLevel(1);
        assertEquals(root.getDeepLevel(), 1);
    }

    @Test
    public void testGetLabel() throws Exception {
        Node root = new Node(null, "rt");
        assertEquals(root.getLabel(), "rt");
    }

    @Test
    public void testSetLabel() throws Exception {
        Node root = new Node(null, "rt");
        assertEquals(root.getLabel(), "rt");
        root.setLabel("root");
        assertEquals(root.getLabel(), "root");
    }

    @Test
    public void testGetParentNode() throws Exception {
        Node root = new Node(null, "rt");
        assertNull(root.getParentNode());
    }

    @Test
    public void testSetParentNode() throws Exception {
        Node root = new Node(null, "rt");
        assertNull(root.getParentNode());
        Node rt = new Node(null, "rt");
        root.setParentNode(rt);
        assertEquals(root.getParentNode(), rt);
    }
}