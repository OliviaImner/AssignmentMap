
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.*;

public class NamedPlace extends Place {
	public NamedPlace(String name, Position pos, String category) {
		super(name, pos, category);
	}

	@Override
	void drawRect(Graphics g) {
		
		if (fold) {
			setBounds(pos.getX(), pos.getY(), 30, 30);
			g.fillPolygon(p);
		} else {
			setBounds(pos.getX(), pos.getY(), 60, 30);
			g.setFont(new Font("Arial", Font.BOLD, 16));
			g.drawString(getName(), 0, 15);
		}
	}
}
