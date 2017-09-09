
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class AssignMap extends JFrame {
  private JFrame window = new JFrame();
  private Background map;
  
  private String[] theOptions = { "Bus", "Underground", "Train" };
  private JTextField searchLabel = new JTextField("Search...");

  private JList<String> theJList = new JList<>(theOptions);
  private JScrollPane toScroll = new JScrollPane(theJList);
  private JScrollPane scrollBack = new JScrollPane();
  
  private boolean controlit = false;
  private boolean change = false;
  private boolean breakListener = false;
//  File filee = new File("");
    
//  private JFileChooser fileChooser = new JFileChooser(".");
  
  NewPlace place = new NewPlace();
  
  private JRadioButton nameButton = new JRadioButton("Named", true );
  private JRadioButton DButton = new JRadioButton("Described");
  ButtonGroup group = new ButtonGroup();
  
  Map<String, Collection<Place>> namedMap = new HashMap<>();
  Map<String, Collection<Place>> categoryMap = new HashMap<>();
  Map<Position, Place> positionMap = new HashMap<>();
  Collection<Place> placeMarkedList = new ArrayList<>();
  
  
  public AssignMap() {
    super("Assignment 2");
    addWindowListener(new WindowListener());
    setLayout(new BorderLayout());
    add(new North(), BorderLayout.NORTH);
    add(new East(), BorderLayout.EAST);
    
    JMenuBar menuBar = new JMenuBar();
    
    JMenu menu = new JMenu("Archive");
    menuBar.add(menu);
    JMenuItem newMap = new JMenuItem("New Map ");
    JMenuItem load = new JMenuItem("Load Places");
    JMenuItem save = new JMenuItem("Save");
    JMenuItem exit = new JMenuItem("Exit");
    
    //adding the actionlistener to the archive menu
    menu.add(newMap);
    newMap.addActionListener(new OpenListener());
    menu.add(load);
    load.addActionListener(new LoadPlaces());
    menu.add(save);
    save.addActionListener(new SavePlaces());
    menu.add(exit);
    exit.addActionListener(new ExitListener());
    
    setJMenuBar(menuBar);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1000, 400);
    setLocationRelativeTo(null);
    window.pack();
    setVisible(true);
  }
  
  class North extends JPanel {
    // north panel
    North() {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      add(new JLabel("                  "));
      JButton newButton = new JButton ("New");
      add(newButton);
      newButton.addActionListener(new NewListener());
    
      group.add(DButton);
      group.add(nameButton);
      Box box = Box.createVerticalBox();
      box.add(nameButton);
      box.add(DButton);
      add(box);
      
      add(searchLabel);
      searchLabel.addMouseListener(new ClearListener());
      add(new JLabel("  "));
      JButton searchButton = new JButton("Search");
      add(searchButton);
      searchButton.addActionListener(new SearchListener());
      add(new JLabel("  "));
      JButton hide = new JButton("Hide");
      add(hide);
      hide.addActionListener(new HideListener());
      add(new JLabel("  "));
      JButton remove = new JButton("Remove");
      add(remove);
      remove.addActionListener(new RemoveListener());
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
      theJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      add(new JLabel("Categories:"));
      add(toScroll);
      
      JButton hideTheCategory = new JButton("Hide Category");
      add(hideTheCategory);
      hideTheCategory.addActionListener(new HideCategoryListener());
      ListSelectionListener listListener = new ListSelectionListener() {
        
        @Override
        public void valueChanged(ListSelectionEvent e) {
          for (Map.Entry<Position, Place> pp : positionMap.entrySet()) {
            try {
              
              if (pp.getValue().getCategory().equals(theJList.getSelectedValue())) {
                pp.getValue().setFold(true);
                pp.getValue().setVisible(true);
              }
            } catch (NullPointerException eve) {
            }
          }
          map.validate();
          map.repaint();
        }
      };
      theJList.addListSelectionListener(listListener);
    }
  }
    
//    public void save() {
//        File saveFile = null;
//        if (controlit) {
//            saveFile = filee;
//        } else {
//            JFileChooser jfc = new JFileChooser("user.dir");
//            FileNameExtensionFilter fileName = new FileNameExtensionFilter(
//                                                                          "Karta", "karta", "krt");
//            jfc.setFileFilter(fileName);
//            int answer = jfc.showSaveDialog(this);
//            if (answer == JFileChooser.APPROVE_OPTION) {
//                saveFile = jfc.getSelectedFile();
//                controlit = true;
//            }
//        }
//    }
  // Insert the map
  class OpenListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent eve) {
    
//        if (map != null) {
//            int result = 0;
//            if (change) {
//                
//                result = JOptionPane.showConfirmDialog(null, "Do you want to save your change?",
//                                                         "Varning", JOptionPane.YES_NO_CANCEL_OPTION);
//                if (result == JOptionPane.YES_OPTION) {
//                    save();
//                } else if (result == JOptionPane.CANCEL_OPTION) {
//                    return;
//                }
//            }
    
        
        String str = System.getProperty(".");
      JFileChooser fileChooser = new JFileChooser(str);
      FileFilter filter = new FileNameExtensionFilter("Pictures", "jpg", "gif", "png");
      fileChooser.setFileFilter(filter);
      int theFile = fileChooser.showOpenDialog(AssignMap.this);
      
      if (theFile != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File selected = fileChooser.getSelectedFile();
      String fileName = selected.getAbsolutePath();
      map = new Background(fileName);
      scrollBack = new JScrollPane(map);
      scrollBack.setMaximumSize(new Dimension(map.getWidth(), map.getHeight()));
      add(scrollBack, BorderLayout.CENTER);
      pack();
      validate();
      repaint();
      }
    }
//  }
  //This is for putting out the triangle at any place on the map
  public class Background extends JPanel {
    private ImageIcon pic;
    public Background(String fileName) {
      pic = new ImageIcon(fileName);
      setPreferredSize(new Dimension(pic.getIconWidth(), pic.getIconHeight()));
      setLayout(null);
    }
    // placement and the size of it
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.drawImage(pic.getImage(), 0, 0, pic.getIconWidth(), pic.getIconHeight(), this);
    }
  }
  
  // add new place to the Maps 
  public void addingToMap(Place place) {
    map.add(place);
    place.addMouseListener(new MouseFocus());
    positionMap.put(place.getPos(), place);
    
    if (namedMap.containsKey(place.getName())) {
      namedMap.get(place.getName()).add(place);
    } else {
      namedMap.put(place.getName(), new ArrayList<>());
      namedMap.get(place.getName()).add(place);
    }
    
    if (categoryMap.containsKey(place.getCategory())) {
      categoryMap.get(place.getCategory()).add(place);
    } else {
      categoryMap.put(place.getCategory(), new ArrayList<>());
      categoryMap.get(place.getCategory()).add(place);
    }
  }
  
  // Mark the place and show information dialog when push on the right button.
  class MouseFocus extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
      Place markedPlace = (Place) e.getSource();
      
      if (e.getButton() == MouseEvent.BUTTON1 && markedPlace.getLocked() == false) { //add a boolean called locked to Place, if this is TRUE do not allow changes to the selection
        if(markedPlace.getLocked() == false) {
          markedPlace.setMarked(!markedPlace.getMarked());
          
          if (markedPlace.getMarked() == true) {
            
            //select
            markedPlace.setBorder(new LineBorder(Color.RED));
            placeMarkedList.add(markedPlace);
            
          } else {
            
            //unselect
            placeMarkedList.remove(markedPlace);
            markedPlace.setBorder(null);
            
          }
        }
      } else if(e.getButton() == MouseEvent.BUTTON1  && markedPlace.getLocked() == true) {
        JOptionPane.showMessageDialog(null, "There is already a place at these coordinates.", "Information", JOptionPane.INFORMATION_MESSAGE);
        breakListener = true;
      } else if (e.getButton() == MouseEvent.BUTTON3) {
        for (Place p : positionMap.values()) {
          if(p.equals(markedPlace)){
            if (p instanceof DescriptionPlace) {
              JOptionPane.showMessageDialog(
                                            null,
                                            "Name: " + p.getName() +
                                            "{" + p.getX() +
                                            "," + p.getY() +
                                            "}. \n" +
                                            "Description: " +
                                            ((DescriptionPlace) p).getDescription(),
                                            "Place infomation ", JOptionPane.INFORMATION_MESSAGE
              );
            
            } else if (p instanceof NamedPlace) {
              JOptionPane.showMessageDialog(null, p.getName() + "{" + p.getX() +
                                            "," + p.getY() + "}",
                                            "Place infomation ",
                                            JOptionPane.INFORMATION_MESSAGE
              );
            }
          }
        }
      }
    }
  }
  // Load the places.txt file to the map
  public class LoadPlaces implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      try {
        Place thePlace = null;
        String str = System.getProperty(".");
        JFileChooser fileChooser = new JFileChooser(str);
        // What the name of the file can end with
        FileFilter filter = new FileNameExtensionFilter("Places", "places", "txt");
        fileChooser.setFileFilter(filter);
        int theFile = fileChooser.showOpenDialog(AssignMap.this);
        
        if (theFile != JFileChooser.APPROVE_OPTION) {
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
                thePlace = new NamedPlace(name, new Position(x, y), category);
              
              }else if (tokens[0].equals("Described")) {
                  String description = tokens[5];
                    thePlace = new DescriptionPlace(name, new Position(x, y), category, description);
          }
          addingToMap(thePlace);
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
  
  // Creates a new place if there's a named place or a described place
  class NewPlace extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent mev) {
      Place place = null;
      
      //now check for marked positions and if they overlap with the new position
      boolean run = true;
    thisLoop: for (Place name: positionMap.values()){
        if(name.getMarked()) {
          Position value = name.getPos();
          int newX = mev.getX();
          int newY = mev.getY();
          int oldX = value.getX();
          int oldY = value.getY();
          if (oldX-10 < newX && newX < oldX+10 || oldY-10 < newY && newY < oldY+10) {
            run = false;
            break thisLoop;
          }
        }
      }
      
      if(run){
        Position newPos = new Position(mev.getX() - 15, mev.getY() - 30);

        if (nameButton.isSelected()) {
          JPanel namedPanel = new JPanel();
          namedPanel.add(new JLabel("Name: "));
          JTextField nameInput = new JTextField(10);
          namedPanel.add(nameInput);
          int nameDialog = JOptionPane.showConfirmDialog(null, namedPanel, "Name:",
                                                         JOptionPane.OK_CANCEL_OPTION);
          
          if (nameInput.getText().equals("") && nameDialog == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "You haven't typed in a valid name");
          } else if (nameDialog == JOptionPane.OK_OPTION && !nameInput.getText().equals("")) {
            place = new NamedPlace(nameInput.getText(), newPos, theJList.getSelectedValue());
            controlit = true;
          }
          
        }else if (DButton.isSelected()) {
              JPanel descPanel = new JPanel();
              descPanel.add(new JLabel("Name: "));
              JTextField nameInput = new JTextField(10);
              descPanel.add(nameInput);
              descPanel.add(new JLabel("Description: "));
              JTextField descInput = new JTextField(10);
              descPanel.add(descInput);
              int descDialog = JOptionPane.showConfirmDialog(null, descPanel, "Name",
                                                         JOptionPane.OK_CANCEL_OPTION);
          
              if((descInput.getText().equals("") || nameInput.getText().equals(""))
                 && descDialog == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "You haven't typed in name or description!");
             }else if (descDialog == JOptionPane.OK_OPTION && !nameInput.getText().equals("") && !descInput.getText().equals("")) {
            
                  place = new DescriptionPlace(nameInput.getText(), newPos, theJList.getSelectedValue(), descInput.getText());
                  controlit = true;
          }
          
        }
        
        if (controlit) {
          addingToMap(place);
          change = true;
          map.validate();
          map.repaint();
          controlit = false;
        }
        // shows if there's already a place n that position
      }
      
      //unlock all Places
      for (Position name: positionMap.keySet()){
        Place value = positionMap.get(name);
        value.setLocked(false);
      }
      
      map.removeMouseListener(this);
      map.setCursor(Cursor.getDefaultCursor());
      
    }
  }
  //New button
  class NewListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {

      //first, when the button is clicked change the lock property of all objects to TRUE
      for (Position name: positionMap.keySet()){
        Place value = positionMap.get(name);
        value.setLocked(true);
      }
      
      if(!breakListener) {
        map.addMouseListener(place);
        map.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        //JOptionPane.showMessageDialog(null, "NewListener after: " + run);
      } else {
        breakListener = false;
      }
    }
  }
  // search after a place with input coordinates and return the place.
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
          
          if (coorX > 0 && coorX < map.getWidth() && coorY > 0 && coorY < map.getHeight()){
            Position theCoordinates = new Position(coorX, coorY);
            
            if(positionMap.containsKey(theCoordinates)){
              Iterator<Place> itr = placeMarkedList.iterator();
              
              while (itr.hasNext()) {
                Place p = itr.next();
                p.setMarked(false);
                itr.remove();
              }
              placeMarkedList.clear();
              Place pp = positionMap.get(theCoordinates);
              pp.setVisible(true);
              pp.setBorder(new LineBorder(Color.RED));
              placeMarkedList.add(pp);
              
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
  
  //search for name on the place and it will show the place if it's hidden or not.
  class SearchListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      if (!searchLabel.getText().equals("")) {
        
        String name = searchLabel.getText();
        Iterator<Place> itr = placeMarkedList.iterator();
        while (itr.hasNext()) {
          Place p = (Place) itr.next();
          p.setBorder(null);
        }
        placeMarkedList.clear();
        if (namedMap.get(name) != null) {
          for (Place p : namedMap.get(name)) {
            
            p.setVisible(true);
            p.setBorder(new LineBorder(Color.RED));
            placeMarkedList.add(p);
          }
        }
      }
    }
  }
  
  //remove the marked place.
  class RemoveListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      Iterator<Place> itr = placeMarkedList.iterator();
      
      while (itr.hasNext()) {
        Place next = (Place) itr.next();
        positionMap.remove(next.getPos());
        namedMap.get(next.getName()).remove(next);
        
        if (namedMap.get(next.getName()).isEmpty()) {
          namedMap.remove(next.getName());
        }
        categoryMap.get(next.getCategory()).remove(next);
        
        if (categoryMap.get(next.getCategory()).isEmpty()) {
          categoryMap.remove(next.getCategory());
        }
        map.remove(next);
        itr.remove();
      }
      change = true;
      repaint();
      placeMarkedList.clear();
    }
  }
  
  // Hide the categorys on the map
  class HideCategoryListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      
      if (theJList.getSelectedValue() != null) {
        for (Place p : categoryMap.get(theJList.getSelectedValue())) {
          p.setVisible(false);
          p.setMarked(false);
        }
      }
      placeMarkedList.clear();
      map.validate();
      map.repaint();
    }
  }
  //hide the place on the map
  class HideListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      Iterator<Place> itr = placeMarkedList.iterator();
      
      while (itr.hasNext()) {
        Place next = (Place) itr.next();
        next.setVisible(false);
        next.setBorder(null);
      }
      placeMarkedList.clear();
      validate();
      repaint();
    }
  }

  // save places to the file.
  class SavePlaces implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ave) {
      try {
        String str = System.getProperty(".");
        JFileChooser fileChooser = new JFileChooser(str);
        int file = fileChooser.showSaveDialog(AssignMap.this);
        if (file != JFileChooser.APPROVE_OPTION) {
          return;
        }
        File selected = fileChooser.getSelectedFile();
        FileWriter outFile = new FileWriter(selected + ".places");
        PrintWriter out = new PrintWriter(outFile);
          for (Place p : positionMap.values()) {
            if (p instanceof DescriptionPlace) {
            out.println("Described" + "," + p.getCategory() + "," + p.getX() + "," + p.getY() + ","
                        + p.getName() + "," + ((DescriptionPlace) p).getDescription());
            } else {
                    out.println(
                        "Named" + "," + p.getCategory() + "," + p.getX() + "," + p.getY() + "," + p.getName());
          }
        }
        change = false;
        out.close();
        outFile.close();
      } catch (FileNotFoundException e) {
        JOptionPane.showMessageDialog(null, "Can't open the file.");
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
      }
    }
  }
  //Exit dialog
  public void exitFrame() {
    if (change == true) {
      int confirm = JOptionPane.showOptionDialog(window, "You have unsaved changes, do you want to quit anaway?",
                                                                    "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
      if (confirm == JOptionPane.YES_OPTION) {
        System.exit(0);
      }
    } else {
      System.exit(0);
    }
  }
  
  class WindowListener extends WindowAdapter {
    
    @Override
    public void windowClosing(WindowEvent e) {
      exitFrame();
    }
  }
  
  class ExitListener implements ActionListener {
    
    @Override
    public void actionPerformed(ActionEvent ave) {
      exitFrame();
    }
  }
  
  class ClearListener extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent mev) {
      searchLabel.setText("");
      searchLabel.removeMouseListener(this);
    }
  }
  
  public static void main(String[] args) {
    
    new AssignMap();
  }
}