package net.datastructures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ExtendedAVLTree<K,V> extends AVLTree<K,V>{
	//there we implement clone with recursion method clone2.
	public static <K, V> AVLTree<K, V> clone(AVLTree<K,V> tree){
		AVLTree<K, V> tree2=new AVLTree<K, V>();
		tree2.root.setElement(tree.root.element());
		clone2(tree2.root,tree.root);
		tree2.size = tree.size;
		return tree2;
	}
	//we search the tree from left then right, every node only got on chance
	//so the complexity is O(n),n is the number of nodes in tree.
	//when scan a node, copy the node.element and store it in a new AVLNode
	//add this AVLNode to new tree
	public static <K, V> void clone2(BTPosition<Entry<K, V>> root,BTPosition<Entry<K, V>> root2){
		if (root2.getLeft()!=null)
		{
			AVLNode lnode = new AVLNode();
			BSTEntry<K,V> a = new BSTEntry<K,V>();
			if (root2.getLeft().element()!=null)
			{	
				a.key=root2.getLeft().element().getKey();
				a.value=root2.getLeft().element().getValue();
			}
			else
				//if this node is external, the element should be null
				a=null;
			lnode.setElement(a);
			lnode.setParent(root);
			root.setLeft(lnode);
			clone2(root.getLeft(),root2.getLeft());
		}
		if (root2.getRight()!=null)
		{
			AVLNode rnode = new AVLNode();
			BSTEntry<K,V> a = new BSTEntry<K,V>();
			if (root2.getRight().element()!=null)
			{	
				a.key=root2.getRight().element().getKey();
				a.value=root2.getRight().element().getValue();
			}
			else
				//if this node is external, the element should be null
				a=null;
			rnode.setElement(a);
			rnode.setParent(root);
			root.setRight(rnode);
			clone2(root.getRight(),root2.getRight());
		}

	}
	//In this merge method, firstly we store the trees in 2 NodePositionList. (O(m+n) because every node is visited only once)
	//by overriding inorderPositions method, we can get NodePositionList and also destroy the tree.
	//The second step is merge the two NodePositionLists by orders. 
	//If the first element of one NodePositionList is larger than the other one, it should be put into new NodePositionLists first
	//the complexity is still O(m+n).
	//then 
	public static <K, V> AVLTree<K, V> merge(AVLTree<K,V> tree1,
			AVLTree<K,V> tree2 )
	{
		NodePositionList<Position<Entry<K,V>>> npl = new NodePositionList<Position<Entry<K,V>>>();
		NodePositionList<Position<Entry<K,V>>> npl2 = new NodePositionList<Position<Entry<K,V>>>();
		AVLTree<K, V> tree3=new AVLTree<K, V>();
		int size = tree1.size+tree2.size;
		npl = returnlist(tree1);
		npl2 = returnlist(tree2);
		tree3 = mergelist_buildtree(npl,npl2);
		tree3.size=size;
		return tree3;	
	}
	
	public static <K, V> NodePositionList<Position<Entry<K,V>>> returnlist(AVLTree<K,V> tree1){
		NodePositionList<Position<Entry<K,V>>> npl = new NodePositionList<Position<Entry<K,V>>>();
		inorderPositions(tree1.root,tree1, npl);
    	DNode<Position<Entry<K, V>>> n = npl.header.getNext();
    	npl.header = n.getNext();
    	while (n!=npl.trailer){
    		//there we eliminate the null to decrease the complexity
    		if (n.element().element()==null){
    	    	DNode<Position<Entry<K, V>>> nt = new DNode<Position<Entry<K, V>>>(null,null,null);
    	    	nt.setNext(n.getNext());
    			npl.remove(n);
    			n = nt.getNext();
    		}
    		else
    			n = n.getNext();
    	}
		return npl;
	}
	//O(n), every node is visited once and then be deleted from old tree
	// we also remove the old tree when visiting nodes, if this node is root's left
	// root.left = null, root.right becomes new root and the old root becomes left
	// if this node is right, do the same.
	public static <K, V> void inorderPositions(BTPosition<Entry<K,V>> v, AVLTree<K,V> tree, NodePositionList<Position<Entry<K,V>>> l){
		if (v.getLeft()!=null){
			inorderPositions(v.getLeft(),tree,l);
		}
	    l.addLast(v);
		if (v.getRight()!=null){
			inorderPositions(v.getRight(),tree,l);
		}
		//we destroy the tree by drop the leaves from tree.
		tree.remove(v);
	}
	

	//we merge 2 NodePositionLists into 1 there. Time complexity is m+n
	//then we build tree there, also visit every node once ,O(m+n)
	public static <K, V> AVLTree<K, V> mergelist_buildtree(NodePositionList<Position<Entry<K,V>>> l1,NodePositionList<Position<Entry<K,V>>> l2){
		NodePositionList<Position<Entry<K, V>>> l3 = new NodePositionList<Position<Entry<K, V>>>();
    	DNode<Position<Entry<K, V>>> n1 = l1.header;
    	DNode<Position<Entry<K, V>>> n2 = l2.header;
    	while (n1!=l1.trailer && n2!=l2.trailer){
    		if (Integer.parseInt(n1.element().element().getKey().toString())
    				<Integer.parseInt((n2.element().element().getKey().toString()))){
    	    	l3.addLast(n1.element());
    	    	n1=n1.getNext();
    		}
    		else{
    			l3.addLast(n2.element());
    			n2 = n2.getNext();
    		}
    	}
    	if (l2.size()>l1.size()){
    		while (n2!=l2.trailer){
    			l3.addLast(n2.element());
    			n2 = n2.getNext();
    		}
    	}
    	if (l2.size()<l1.size()){
    		while (n1!=l1.trailer){
    			l3.addLast(n1.element());
    			n1 = n1.getNext();
    		}
    	}
    	AVLTree<K, V> tree=new AVLTree<K, V>();
		tree.root = buildtree(l3,0,l3.size()-1);

		return tree;
	}
	//there is exactly where we build the new tree.
	//firstly, we set the middle number of list is the root of the tree, and then we found the 
	//left sub tree by using(mid/2) and right subtree (mid+mid/2)
	//by recursion we can get the whole tree
	//there we visit the left node first and then root, later right node.
	//which exactly fits the order in the list.
	// the time complexity is o(n), every node is visited only once.
	public static <K, V> BTPosition<Entry<K,V>> buildtree 
	(NodePositionList<Position<Entry<K,V>>> l,int start,int end){
		BTPosition<Entry<K,V>> troot = new BTNode();
		BTPosition<Entry<K,V>> tlchild = new BTNode();
		BTPosition<Entry<K,V>> trchild = new BTNode();
		if (start<=end)
		{
			tlchild=buildtree(l,start,(start+end)/2-1);
			if(tlchild!=null){
				tlchild.setParent(troot);
			}
			troot.setElement(l.first().element().element());
			troot.setLeft(tlchild);	
			
			l.remove(l.first());
			
			trchild=buildtree(l,(start+end)/2+1,end);
			if(trchild!=null){
			trchild.setParent(troot);
			}
			troot.setRight(trchild);
		}
		return troot;
	}
	//we use Swing there to represent the GUI
	//the frame size automatically fits the screen, because I chose 2/3 width and 2/3 height of screen as frame attribute
	//Also according to the screen size,the diamater of circle and width of square automatically fits the frame.
	//For deeper nodes, the diamater is smaller. Because of this, the lines will never intersects.
	public static <K, V> void print(AVLTree<K, V> tree){
		int height=0;
		//we need to get the height of tree to 
		for (int i = 0; i < 10; i++) {
			if (Math.pow(2, i)>tree.size)
			{
				height = i;
				break;
			}
		}

        JFrame frame = new JFrame("AVLTREE automatically fits screen");
        //we get the screen size there
        int frameWidth=(int)((java.awt.Toolkit.getDefaultToolkit().getScreenSize().width)/1.5);
        int frameHeight = (int)((java.awt.Toolkit.getDefaultToolkit().getScreenSize().height)/1.5); 
        frame.setSize(frameWidth,frameHeight);
        frame.isResizable();
        frame.setLocation(frameWidth/5, frameHeight/5);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //we have to guarantee, for the leaves of tree(last row),they can't intersct
        //we could have at most Math.pow(2, height) leaves, but we also need to consider the space between every leaf
        //so there i use Math.pow(2, height+1). *(height+1) means according to different depth, it differs.
        int circle_diameter = (int) (frameWidth/(Math.pow(2, height+1)))*(height+1);
        //height_diff is the space between every depth(row).
        int height_diff = frameHeight/(height+2);
        //because the screen width mostly is larger than height, if diameter is larger than the space between different depth
        //the slope could be very weird, therefore for the beauty and accuracy we adjust there.
        if (circle_diameter>height_diff){
        	circle_diameter = height_diff;
        }
        //MyPanel extends JPanel and provide the drawing.
        MyPanel mypanel =new MyPanel(circle_diameter,tree,
        		(int)frameWidth/2,frameHeight/(height+2),
        		(int)frameWidth/2-circle_diameter,height_diff,height+1);
        mypanel.setBackground(Color.BLACK);
        frame.add(mypanel);        
        frame.setVisible(true);
	}
	
	static class MyPanel<K,V> extends JPanel {

		public int circle_diameter;
		public AVLTree<K,V> tree;
		public int x;
		public int y;
		public int diff;
		public int height_diff;
		public int tree_height;
		public MyPanel(int circle_diameter,AVLTree<K,V> tree,int x,int y,
				int diff,int height_diff,int tree_height) {
			this.circle_diameter = circle_diameter;
			this.tree = tree;
			this.x = x;
			this.y = y;
			this.diff = diff;
			this.height_diff=height_diff;
			this.tree_height = tree_height;
			// TODO Auto-generated constructor stub
		}
		 public void paint(Graphics g) {
		  super.paint(g);
		  getGraph(g,tree.root,x
					,y,circle_diameter,1,tree_height,diff,height_diff);
		 }
	}
	
	//here we visite every node once and draw the node. Still O(n)
	//we visit the left node and then root, later right node.
	public static<K,V> void getGraph(Graphics g,BTPosition<Entry<K, V>> root,
			int x,int y,int circle_diameter,int height,int tree_height,int diff,int height_diff){
		g.setColor(Color.WHITE);
		g.setFont(new Font(null, Font.BOLD, 50));
		int current_circle_diameter = circle_diameter*(tree_height+1-height)/tree_height;
		int next_circle_diameter = circle_diameter*(tree_height-height)/tree_height;
	
		if (root.getLeft()!=null){
			g.drawLine(x, y+(current_circle_diameter)/2, 
					x-(int)((diff)/(Math.pow(2, height))), y+height_diff-(next_circle_diameter)/2);
			getGraph(g,root.getLeft(),x-(int)((diff)/(Math.pow(2, height))),
					y+height_diff,circle_diameter,height+1,tree_height,diff,height_diff);
		}
		if (root.element()==null){
			g.drawRect(x-(current_circle_diameter)/2, 
					y-(current_circle_diameter)/2, 
					current_circle_diameter,
					current_circle_diameter);}
		else{
			g.drawString(root.element().getKey().toString(), x-current_circle_diameter/4, 
					y+current_circle_diameter/4);
			g.drawOval(x-(current_circle_diameter)/2, 
					y-(current_circle_diameter)/2, 
					current_circle_diameter,
					current_circle_diameter);
			}

		if (root.getRight()!=null){
			g.drawLine(x, y+(current_circle_diameter)/2, 
					x+(int)((diff)/(Math.pow(2, height))),
					y+height_diff-(next_circle_diameter)/2);
			getGraph(g,root.getRight(),x+(int)((diff)/(Math.pow(2, height))),
					y+height_diff,circle_diameter,height+1,tree_height,diff,height_diff);
		}

		//return t;
	}

}

