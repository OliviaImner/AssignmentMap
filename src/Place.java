
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;
import javax.swing.*;

public abstract class Place extends JComponent {
	protected String category;
	private boolean marked = false;
	protected boolean fold = true;
	protected Position pos;
	private String name;
	private int[] x = { 0, 15, 30 };
	private int[] y = { 0, 30, 0 };
	protected Polygon p = new Polygon(x, y, 3);
	ArrayList<Place> markedList = new ArrayList<>();

	public Place(String name, Position pos, String category) {
		this.name = name;
		this.category = category;
		this.pos = pos;
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
  public Position getPos(){
  setBounds(pos.getX(), pos.getY(), 30, 30);
  setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  validate();
  repaint();
  return pos;
	}
	public boolean getMarked() {
		return marked;
	}
	public void setMarked(boolean marked) {
		this.marked = marked;
		setBorder(null);
	}
	public void setFold(boolean fold) {
		this.fold = fold;
	}
	public boolean getFold() {
		return fold;
	}
}
