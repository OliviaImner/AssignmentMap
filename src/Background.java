import javax.swing.*;
import java.awt.*;

  //This is for putting out the triangle at any place on the map
  public class Background extends JPanel {

    private ImageIcon pic;

    public Background(){
          
    }
      
    public void setBackground(String fileName) {
      pic = new ImageIcon(fileName);
      setLayout(null);
    }
    // placement and the size of it
    
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
        g.drawImage(pic.getImage(), 0, 0, this);
        setPreferredSize(new Dimension(pic.getIconWidth(), pic.getIconHeight()));
    }
  }
