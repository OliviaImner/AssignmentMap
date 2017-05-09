
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;
import javax.swing.*;

public abstract class Place extends JComponent {
	protected String category;
	private boolean marked = false;
	protected boolean folded = true;
	protected Position pos;
	private String name;
	private int[] x1 = { 0, 15, 30 };
	private int[] y1 = { 0, 30, 0 };
	protected Polygon p = new Polygon(x1, y1, 3);
	ArrayList<Place> markedList = new ArrayList<>();

	public Place(String name, Position pos, String category) {
		this.name = name;
		this.category = category;
		this.pos = pos;
		setBounds(pos.getX(), pos.getY(), 30, 30);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		validate();
		repaint();
	}

	abstract void drawRect(Graphics g);

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			
			if (category.equals("Buss")) {
				g.setColor(Color.RED);
			} else if (category.equals("Tåg")) {
				g.setColor(Color.GREEN);
			} else if (category.equals("Tunnelbana")) {
				g.setColor(Color.BLUE);
			}
		} catch (NullPointerException e) {
			g.setColor(Color.BLACK);
		}
		drawRect(g);
	}
	public String getName() {
		return name;
	}
	public String getCategory() {
		return category;
	}
	public Position getPos() {
		return pos;
	}
	public boolean getMarked() {
		return marked;
	}
	public void setMarked(boolean marked) {
		this.marked = marked;
		setBorder(null);
	}
	public void setFolded(boolean folded) {
		this.folded = folded;
	}
	public boolean getFolded() {
		return folded;
	}
}
