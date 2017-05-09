//this is a comment

import java.awt.*;
public class DescPlace extends Place {
	private String description;
	DescPlace(String name, Position pos, String category, String description) {
		super(name, pos, category);
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	@Override
	void drawRect(Graphics g) {
		if (folded) {
			setBounds(pos.getX(), pos.getY(), 30, 30);
			g.fillPolygon(p);
		} else {
			setBounds(pos.getX(), pos.getY(), 80, 40);
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 80, 50);
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
			g.setFont(new Font("Arial", Font.BOLD, 14));
			g.drawString(getName(), 0, 15);
			g.drawString(getDescription(), 0, 35);
		}
	}
}
