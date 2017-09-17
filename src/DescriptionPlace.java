
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
}
