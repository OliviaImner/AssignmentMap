
import java.awt.*;
public class DescriptionPlace extends Place {
	private String description;
	DescriptionPlace(String name, Position pos, String category, String description) {
		super(name, pos, category);
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	@Override
	void drawRect(Graphics g) {
		if (fold) {
			setBounds(pos.getX() -15, pos.getY() -30, 30, 30);
			g.fillPolygon(p);
		} else {
			setBounds(pos.getX() -15, pos.getY() -30, 80, 40);
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 80, 50);
			try {
				if (category.equals("Bus")) {
					g.setColor(Color.RED);
				} else if (category.equals("Train")) {
					g.setColor(Color.GREEN);
				} else if (category.equals("Underground")) {
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
