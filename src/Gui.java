
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class Gui extends JFrame {
  private boolean changed = false;
  private JFrame window = new JFrame();
  private Background background;
  private JScrollPane scrollBack = new JScrollPane();
  private String[] option = { "Buss", "Tunnelbana", "Tåg" };
  private JList<String> list = new JList<>(option);
  private JScrollPane scroll = new JScrollPane(list);
  private JTextField searchLabel = new JTextField("Search...");
  private boolean controlit = false;
  NewPlace place = new NewPlace();
  Coordinates cor = new Coordinates();
  
  private JRadioButton nameButton = new JRadioButton("Named", true );
  private JRadioButton DButton = new JRadioButton("Described");
  ButtonGroup group = new ButtonGroup();

  Map<Position, Place> positionMap = new HashMap<>();
  Collection<Place> markedList = new ArrayList<>();
  Map<String, Collection<Place>> namedMap = new HashMap<>();
  Map<String, Collection<Place>> catMap = new HashMap<>();
  
  public Gui() {
    super("Assignment 2");
    addWindowListener(new WindowLis());
    setLayout(new BorderLayout());
    add(new East(), BorderLayout.EAST);
    add(new North(), BorderLayout.NORTH);
    JMenuBar menuBar = new JMenuBar();
    
    JMenu menu = new JMenu("Archive");
    menuBar.add(menu);
    JMenuItem newMap = new JMenuItem("New Map ");
    JMenuItem load = new JMenuItem("Load Places");
    JMenuItem save = new JMenuItem("Save ");
    JMenuItem exit = new JMenuItem("Exit ");
    
    menu.add(newMap);
    newMap.addActionListener(new OpenLis());
    menu.add(load);
    load.addActionListener(new LoadPlaces());
    menu.add(save);
    save.addActionListener(new SavePlaces());
    menu.add(exit);
    exit.addActionListener(new ExitLis());
    
    setJMenuBar(menuBar);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(800, 500);
    setLocationRelativeTo(null);
    window.pack();
    setVisible(true);
  }
  
  class North extends JPanel {
    // north panel
    North() {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      add(new JLabel("                      "));
      JButton newButton = new JButton ("New");
      add(newButton);
    
      group.add(DButton);
      group.add(nameButton);
      Box box = Box.createVerticalBox();
      box.add(nameButton);
      box.add(DButton);
      add(box);
      
      nameButton.addActionListener(new NewLis());
      DButton.addActionListener(new NewLis());
      
      add(searchLabel);
      searchLabel.addMouseListener(new ClearLis());
      add(new JLabel("  "));
      JButton searchButton = new JButton("Search");
      add(searchButton);
      searchButton.addActionListener(new SearchLis());
      add(new JLabel("  "));
      JButton hide = new JButton("Hide");
      add(hide);
      hide.addActionListener(new HideLis());
      add(new JLabel("  "));
      JButton remove = new JButton("Remove");
      add(remove);
      remove.addActionListener(new RemoveLis());
      add(new JLabel("  "));
      JButton coordinateButton = new JButton("Coordinates");
      add(coordinateButton);
      coordinateButton.addActionListener(new CoordinateListener());
      add(new JLabel("                     "));
    }
  }
  
  class East extends JPanel {
    //right panel
    East() {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      add(new JLabel("Categories:"));
      add(scroll);
      JButton hideCat = new JButton("Hide Category");
      add(hideCat);
      hideCat.addActionListener(new HideCatLis());
      ListSelectionListener listListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          for (Map.Entry<Position, Place> pp : positionMap.entrySet()) {
            try {
              
              if (pp.getValue().getCategory().equals(list.getSelectedValue())) {
                pp.getValue().setFolded(true);
                pp.getValue().setVisible(true);
              }
            } catch (NullPointerException eve) {
            }
          }
          background.validate();
          background.repaint();
        }
      };
      list.addListSelectionListener(listListener);
    }
  }
  
  class OpenLis implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent eve) {
      String str = System.getProperty(".");
      JFileChooser fileChooser = new JFileChooser(str);
      FileFilter filter = new FileNameExtensionFilter("Pictures", "jpg", "gif", "png");
      fileChooser.setFileFilter(filter);
      int file = fileChooser.showOpenDialog(Gui.this);
      
      if (file != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File selected = fileChooser.getSelectedFile();
      String fileName = selected.getAbsolutePath();
      background = new Background(fileName);
      scrollBack = new JScrollPane(background);
      scrollBack.setMaximumSize(new Dimension(background.getWidth(), background.getHeight()));
      add(scrollBack, BorderLayout.CENTER);
      pack();
      validate();
      repaint();
    }
  }
  
  class Background extends JPanel {
    private ImageIcon picture;
    public Background(String fileName) {
      picture = new ImageIcon(fileName);
      setPreferredSize(new Dimension(picture.getIconWidth(), picture.getIconHeight()));
      setLayout(null);
      //This is for putting out the triangel at any place on the map
    }
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.drawImage(picture.getImage(), 0, 0, picture.getIconWidth(), picture.getIconHeight(), this);
      // Placing and the size
    }
  }
  
  class NewPlace extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent mev) {
      Place place = null;
      Position newPos = new Position(mev.getX() - 15, mev.getY() - 30);
      
      if (nameButton.isSelected()) {
        JPanel namedPanel = new JPanel();
        namedPanel.add(new JLabel("Name: "));
        JTextField nameInput = new JTextField(10);
        namedPanel.add(nameInput);
        int nameDialog = JOptionPane.showConfirmDialog(null, namedPanel, "Name:",
                                                       JOptionPane.OK_CANCEL_OPTION);
        
        if (nameInput.getText().equals("") && nameDialog == JOptionPane.OK_OPTION) {
          JOptionPane.showMessageDialog(null, "You haven't typed in correct");
        }
        else if (nameDialog == JOptionPane.OK_OPTION && !nameInput.getText().equals("")) {
          place = new NamedPlace(nameInput.getText(), newPos, list.getSelectedValue());
          controlit = true;
        }
        
      } else if (DButton.isSelected()) {
        JPanel descPanel = new JPanel();
        descPanel.add(new JLabel("Name: "));
        JTextField nameInput = new JTextField(10);
        descPanel.add(nameInput);
        descPanel.add(new JLabel("Description: "));
        JTextField descInput = new JTextField(10);
        descPanel.add(descInput);
        int descDialog = JOptionPane.showConfirmDialog(null, descPanel, "Name",
                                                       JOptionPane.OK_CANCEL_OPTION);
        
        if ((descInput.getText().equals("") || nameInput.getText().equals(""))
            && descDialog == JOptionPane.OK_OPTION) {
          JOptionPane.showMessageDialog(null, "You haven't typed in name or description!");
        } else if (descDialog == JOptionPane.OK_OPTION && !nameInput.getText().equals("") && !descInput.getText().equals("")) {
          
          place = new DescPlace(nameInput.getText(), newPos, list.getSelectedValue(), descInput.getText());
          controlit = true;
        }
        
      }
      if (controlit) {
        addPlaceMaps(place);
        changed = true;
        background.validate();
        background.repaint();
        controlit = false;
      }
      background.removeMouseListener(this);
      background.setCursor(Cursor.getDefaultCursor());
    }
  }
  
  public void addPlaceMaps(Place place) {
    background.add(place);
    place.addMouseListener(new MouseFocus());
    positionMap.put(place.getPos(), place);
    
    if (namedMap.containsKey(place.getName())) {
      namedMap.get(place.getName()).add(place);
    } else {
      namedMap.put(place.getName(), new ArrayList<>());
      namedMap.get(place.getName()).add(place);
    }
    
    if (catMap.containsKey(place.getCategory())) {
      catMap.get(place.getCategory()).add(place);
    } else {
      catMap.put(place.getCategory(), new ArrayList<>());
      catMap.get(place.getCategory()).add(place);
    }
  }
  
  class HideCatLis implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      
      if (list.getSelectedValue() != null) {
        for (Place p : catMap.get(list.getSelectedValue())) {
          p.setVisible(false);
          p.setMarked(false);
        }
      }
      markedList.clear();
      background.validate();
      background.repaint();
    }
  }
  
  class HideLis implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      Iterator<Place> itr = markedList.iterator();
      
      while (itr.hasNext()) {
        Place next = (Place) itr.next();
        next.setVisible(false);
        next.setBorder(null);
      }
      markedList.clear();
      validate();
      repaint();
    }
  }
  
  class RemoveLis implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      Iterator<Place> itr = markedList.iterator();
      
      while (itr.hasNext()) {
        Place next = (Place) itr.next();
        positionMap.remove(next.getPos());
        namedMap.get(next.getName()).remove(next);
        
        if (namedMap.get(next.getName()).isEmpty()) {
          namedMap.remove(next.getName());
        }
        catMap.get(next.getCategory()).remove(next);
        
        if (catMap.get(next.getCategory()).isEmpty()) {
          catMap.remove(next.getCategory());
        }
        background.remove(next);
        itr.remove();
      }
      changed = true;
      repaint();
      markedList.clear();
    }
  }
  
  class NewLis implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      background.addMouseListener(place);
      background.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
  }
  
  class MouseFocus extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent mev) {
      Place markedPlace = (Place) mev.getSource();
      
      if (mev.getButton() == MouseEvent.BUTTON1) {
        markedPlace.setMarked(!markedPlace.getMarked());
        
        if (markedPlace.getMarked() == true) {
          markedPlace.setBorder(new LineBorder(Color.RED));
          markedList.add(markedPlace);
        } else {
          markedList.remove(markedPlace);
          markedPlace.setBorder(null);
        }
      } else if (mev.getButton() == MouseEvent.BUTTON3) {
        
            for (Place p : positionMap.values()) {
              if(p.equals(markedPlace)){
              
                  if (p instanceof DescPlace) {
            
                      JOptionPane.showMessageDialog(null, "Name: " + p.getName() + "{" + p.getX() + "," + p.getY() + "}. \n" +
                                          "Description: " + ((DescPlace) p).getDescription(), "Place infomation ", JOptionPane.INFORMATION_MESSAGE);
            
                  } else if (p instanceof NamedPlace) {
                    
                      JOptionPane.showMessageDialog(null, p.getName() + "{" + p.getX() + "," + p.getY() + "}",
                                                      "Place infomation ", JOptionPane.INFORMATION_MESSAGE);
            break;
              }
            }
          }
        }
      }
    }
  
  public class LoadPlaces implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      try {
        Place place = null;
        String str = System.getProperty(".");
        JFileChooser fileChooser = new JFileChooser(str);
        FileFilter filter = new FileNameExtensionFilter("Places", "places", "txt");
        fileChooser.setFileFilter(filter);
        int file = fileChooser.showOpenDialog(Gui.this);
        
        if (file != JFileChooser.APPROVE_OPTION) {
          return;
        }
        File selected = fileChooser.getSelectedFile();
        FileReader reader = new FileReader(selected.getAbsolutePath());
        BufferedReader br = new BufferedReader(reader);
        String line;
        
        while ((line = br.readLine()) != null) {
          String[] tokens = line.split(",");
          String category = tokens[1];
          int x = Integer.parseInt(tokens[2]);
          int y = Integer.parseInt(tokens[3]);
          String name = tokens[4];
          if (tokens[0].equals("Named")) {
            place = new NamedPlace(name, new Position(x, y), category);
          } else if (tokens[0].equals("Described")) {
            String description = tokens[5];
            place = new DescPlace(name, new Position(x, y), category, description);
          }
          addPlaceMaps(place);
        }
        validate();
        repaint();
        br.close();
        reader.close();
      } catch (FileNotFoundException e) {
        JOptionPane.showMessageDialog(null, "Can't open file");
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error :" + e.getMessage());
      }
    }
  }
  
  class SavePlaces implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      try {
        String str = System.getProperty(".");
        JFileChooser fileChooser = new JFileChooser(str);
        int file = fileChooser.showSaveDialog(Gui.this);
        if (file != JFileChooser.APPROVE_OPTION) {
          return;
        }
        File selected = fileChooser.getSelectedFile();
        FileWriter outFile = new FileWriter(selected + ".places");
        PrintWriter out = new PrintWriter(outFile);
        for (Place p : positionMap.values()) {
          if (p instanceof DescPlace) {
            out.println("Described" + "," + p.getCategory() + "," + p.getX() + "," + p.getY() + ","
                        + p.getName() + "," + ((DescPlace) p).getDescription());
          } else {
            out.println(
                        "Named" + "," + p.getCategory() + "," + p.getX() + "," + p.getY() + "," + p.getName());
          }
        }
        changed = false;
        out.close();
        outFile.close();
      } catch (FileNotFoundException e) {
        JOptionPane.showMessageDialog(null, "Can't open the file.");
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
      }
    }
  }
  
  public void exitFrame() {
    if (changed == true) {
      int confirm = JOptionPane.showOptionDialog(window, "You have unsaved changes, do you want to guit anaway?",
                                                 "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
      if (confirm == JOptionPane.YES_OPTION) {
        System.exit(0);
      }
    } else {
      System.exit(0);
    }
  }
  
  class WindowLis extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
      exitFrame();
    }
  }
  
  class ExitLis implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      exitFrame();
    }
  }
  
  class SearchLis implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      if (!searchLabel.getText().equals("")) {
        
        String name = searchLabel.getText();
        Iterator<Place> itr = markedList.iterator();
        while (itr.hasNext()) {
          Place p = (Place) itr.next();
          p.setBorder(null);
          
        }
        markedList.clear();
        if (namedMap.get(name) != null) {
          for (Place p : namedMap.get(name)) {
            
            p.setVisible(true);
            p.setBorder(new LineBorder(Color.RED));
            markedList.add(p);
          }
        }
      }
    }
  }

  class CoordinateListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave){
      
      JPanel coordinatePanel = new JPanel();
      coordinatePanel.add(new JLabel("X: "));
      JTextField xInput = new JTextField(10);
      coordinatePanel.add(xInput);

      coordinatePanel.add(new JLabel("Y: "));
      JTextField yInput = new JTextField(10);
      coordinatePanel.add(yInput);
      

      int coordinateDialog = JOptionPane.showConfirmDialog(null, coordinatePanel, "Input coordinates",
                                                     JOptionPane.OK_CANCEL_OPTION);
    

      if(coordinateDialog == JOptionPane.OK_OPTION){

        try{
        
              int coorX = Integer.parseInt(xInput.getText());
              int coorY = Integer.parseInt(yInput.getText());
        
          if (coorX > 0 && coorX < background.getWidth() && coorY > 0 && coorY < background.getHeight()){
            Position theCoordinates = new Position(coorX, coorY);
            
                  if(positionMap.containsKey(theCoordinates)){
                    Iterator<Place> itr = markedList.iterator();
                    
                    while (itr.hasNext()) {
                      Place p = itr.next();
                      p.setMarked(false);
                      itr.remove();
                    }
                    markedList.clear();
                    Place pp = positionMap.get(theCoordinates);
                    pp.setVisible(true);
                    pp.setMarked(true);
                    markedList.add(pp);
            
                    }else{
                        JOptionPane.showMessageDialog
                        (null, "There is no place on that position", "Information", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
            
          }else{
              JOptionPane.showMessageDialog
                (null, "These coordiantes are outside of this map", "Information", JOptionPane.INFORMATION_MESSAGE);
          }

          }catch(NumberFormatException e){
          JOptionPane.showMessageDialog(null, "You need to write in numbers!");
        }
      }
    }
  }
  
        
              //if ((xInput.equals("") || yInput.getText().equals(""))
                    //  && coordinateDialog == JOptionPane.OK_OPTION) {
                   //   JOptionPane.showMessageDialog(null, "You haven't typed in coordinates!");
                


//      background.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
//      background.addMouseListener(cor);

  
  class Coordinates extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent mev) {
      for (int i = mev.getX() - 10; i < mev.getX() + 10; i++) {
        for (int j = mev.getY() - 10; j < mev.getY() + 10; j++) {
          Position area = new Position(i - 15, j - 30);
          if (positionMap.containsKey(area)) {
            Place p = positionMap.get(area);
            
            
            p.setVisible(true);
          }
        }
      }
      background.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      background.removeMouseListener(cor);
      repaint();
    }
  }
  
  class ClearLis extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent mev) {
      searchLabel.setText("");
      searchLabel.removeMouseListener(this);
    }
  }
  
  public static void main(String[] args) {
    
    new Gui();
  }
}
