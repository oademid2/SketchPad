/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sketch;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.scene.input.*;
import javafx.scene.paint.Paint;
import javafx.util.Pair;


/**
 *
 * @author kitan
 */
public class FXMLDocumentController implements Initializable  {
    
    @FXML
    private Label label;
    
    //Buttons for draw modes...
    @FXML
    private Button rectBtn, lineBtn, squareBtn, circBtn, elipBtn, polyBtn,freeBtn, polyOpenBtn;
    //Buttons for edit modes...
    @FXML
    private Button editBtn, delBtn, copyBtn, pasteBtn;
    //Buttons for group/ungroup modes...
    @FXML
    private Button doneBtn, groupBtn, ungroupBtn;
    //Buttons for save/undos modes...
    @FXML
    private Button undoBtn, redoBtn, saveBtn, loadBtn;
    //color picker....
    @FXML
    ColorPicker colorselect;
    //pane for drawing shapes
    @FXML
    private AnchorPane pane;
    //Store saved nodes...
    ArrayList<Node> savedNodes =  new ArrayList<Node>();
    

    
    //keep track of all the shapes
    private ArrayList<Shape> shapes =  new ArrayList<Shape>();
    
    //keep track of all the info needed for undo....
    private ArrayList<String> logList = new ArrayList<String>();
    private ArrayList<Object> memList = new ArrayList<Object>();
    private ArrayList<Double> xyList = new ArrayList<Double>();
    
    //keep track of all the info needed for redo....
    private ArrayList<String> reList = new ArrayList<String>();
    private ArrayList<Object> remList = new ArrayList<Object>();
    private ArrayList<Double> r_xyList = new ArrayList<Double>();
    
    //varaibles to store x and y for drawing...
    private double xPrev, yPrev;
    private String mode = "rect";
   
    //empty shapes for drawing.....
    Line line = new Line();  //line
    Rectangle rect = new Rectangle(); //rectabgle
    Circle circle = new Circle(); //circle
    Polyline free = new Polyline(); //free draw
    Rectangle square = new Rectangle(); //square
    Ellipse elip = new Ellipse(); //elipse
    Polygon poly = new Polygon(); //closed polygon
    Polyline polyOpen = new Polyline(); //open polygon
    ObservableList<Double> polyPoints =  FXCollections.observableArrayList(); //keep track of points to draw
    
    Group polyGroup = new Group();
    Group polyOpenGroup = new Group();
    
    
    //keep track of nodes
    Node curr; //selected nodes
    Group currG; //if selected node is a group
    Node copy; //copied node
    ArrayList<Shape> newgroup = new ArrayList<Shape>(); //selected nodes when creating a new crib...
    
    //CODE FOR DRAWING THE SHAPE ON THE PANE
    
    @FXML
    private void paneMousePress(MouseEvent ev) {
        
        if(mode == "rect"){
        
            xPrev = ev.getX();
            yPrev = ev.getY();
            rect.setX(ev.getX()); 
            rect.setY(ev.getY()); 
            rect.setWidth(0);
            rect.setHeight(0);
            rect.setFill(colorselect.getValue());
            pane.getChildren().add(rect);
            
       }else if(mode == "line"){
           
            
            line.setStartX(ev.getX()); 
            line.setStartY(ev.getY()); 
            line.setEndX(ev.getX()); 
            line.setEndY(ev.getY());
           
            line.setStroke(colorselect.getValue());
            pane.getChildren().add(line);
            
       }else if(mode == "circle"){
           
            double dx,dy;
            xPrev = ev.getX();
            yPrev = ev.getY();

                // set the position of center of the  circle 
             circle.setCenterX(ev.getX()); 
             circle.setCenterY(ev.getY()); 
             
             
             dx = ev.getX() - xPrev;
             dy = ev.getY() - yPrev;
             double r = Math.pow(Math.pow(dx,2) + Math.pow(dy,2), 0.5);

             // set Radius of the circle 
             circle.setRadius(r); 
             circle.setFill(colorselect.getValue());
             pane.getChildren().add(circle);
       }else if(mode == "free"){
           
            pane.getChildren().add(free);
            free.setStroke(colorselect.getValue());
            free.getPoints().addAll(ev.getX(), ev.getY());
           
           
           
       }else if(mode == "square"){
           
            
            xPrev = ev.getX();
            yPrev = ev.getY();
            square.setX(ev.getX()); 
            square.setY(ev.getY()); 
            square.setWidth(0);
            square.setHeight(0);
            square.setFill(colorselect.getValue());
            pane.getChildren().add(square);
           
       }else if (mode == "elip"){
           double dx,dy;
           xPrev = ev.getX();
           yPrev = ev.getY();
           dx = ev.getX() - xPrev;
           dy = ev.getY() - yPrev;
            
           elip.setCenterX(ev.getX()); 
           elip.setCenterY(ev.getY()); 
           elip.setRadiusX(dx); 
           elip.setRadiusY(dy);
           elip.setFill(colorselect.getValue());
           pane.getChildren().add(elip);
       }else if (mode == "poly"){
           //pane.getChildren().remove(polyGroup);
           xPrev = ev.getX();
           yPrev = ev.getY();
           Circle point = new Circle (ev.getX(), ev.getY(), 5);
          
           polyGroup.getChildren().add(point);
           poly.getPoints().addAll(ev.getX(), ev.getY());
           poly.setFill(colorselect.getValue());
           polyPoints.addAll(ev.getX(), ev.getY());
           
       }else if (mode == "polyOpen"){
           //pane.getChildren().remove(polyGroup);
           xPrev = ev.getX();
           yPrev = ev.getY();
           Circle point = new Circle (ev.getX(), ev.getY(), 5);
          
           polyOpenGroup.getChildren().add(point);
           polyOpen.getPoints().addAll(ev.getX(), ev.getY());
           polyOpen.setStroke(colorselect.getValue());
       }
    }
    
    @FXML
     private void paneMouseDrag(MouseEvent ev) {
       
         if(mode == "rect"){
           
     
            double dx, dy;

            dx = ev.getX() - xPrev; 
            dy = ev.getY() - yPrev; 

            if(dx < 0){
               rect.setTranslateX(dx);
               rect.setWidth(-dx);
            }else{
                rect.setTranslateX(0);
                rect.setWidth(dx);
            }

            if(dx < 0){
                rect.setTranslateY(dy);
                rect.setHeight(-dy);
            }else{
                rect.setTranslateY(0);
                rect.setHeight(dy);
            }
         
        }else if(mode == "line"){
           
            line.setEndX(ev.getX()); 
            line.setEndY(ev.getY());
            
        }else if(mode == "circle"){
            
             double dx, dy;
             dx = ev.getX() - xPrev;
             dy = ev.getY() - yPrev;
             double r = Math.pow(Math.pow(dx,2) + Math.pow(dy,2), 0.5);

             // set Radius of the circle 
             circle.setRadius(r); 

             // set Radius of the circle 
             circle.setRadius(r); 
       }else if(mode == "free"){  
            free.getPoints().addAll(ev.getX(), ev.getY());
       }else if(mode == "square"){
            
             double dx, dy;
             dx = ev.getX() - xPrev;
             dy = ev.getY() - yPrev;
             double l = Math.pow((Math.pow(dx,2) + Math.pow(dy,2))/2, 0.5);

            if(dx < 0){
               square.setTranslateX(l);
               square.setWidth(l);
            }else{
                square.setTranslateX(0);
                square.setWidth(l);
            }

            if(dx < 0){
                square.setTranslateY(l);
                square.setHeight(l);
            }else{
                square.setTranslateY(0);
                square.setHeight(l);
            }
       }else if (mode == "elip"){
           
           double dx,dy;
           dx = ev.getX() - xPrev;
           dy = ev.getY() - yPrev;
           elip.setRadiusX(dx); 
           elip.setRadiusY(dy);
       }

       
    }
     
    @FXML
    private void paneMouseRelease(MouseEvent ev) {
        
        if(mode == "rect"){
            shapes.add(rect);
            rect.addEventFilter(MouseEvent.ANY , shapeHandler);
            if(mode != "undo" || mode == "redo") log("draw", rect); //save the action for the redo
            rect = new Rectangle();
        }else if(mode == "line"){
           
            line.setEndX(ev.getX()); 
            line.setEndY(ev.getY());
            shapes.add(line);
            line.addEventFilter(MouseEvent.ANY, shapeHandler);
            
            log("draw", line);
            line = new Line();
            
       }else if(mode == "free"){
           
            shapes.add(free);
            free.addEventFilter(MouseEvent.ANY, shapeHandler);
            log("draw", free);
            free = new Polyline();
            
       }else if(mode == "circle"){
            shapes.add(circle);
            circle.addEventFilter(MouseEvent.ANY, shapeHandler);
            
            log("draw", circle);
            circle = new Circle();
       }else if(mode == "square"){
            //mode = "none";
            shapes.add(square);
            square.addEventFilter(MouseEvent.ANY, shapeHandler);
            log("draw", square);
            square = new Rectangle();
       }else if(mode == "elip"){
            //mode = "none";
            shapes.add(elip);
            elip.addEventFilter(MouseEvent.ANY, shapeHandler);
            log("draw", elip);
            elip = new Ellipse();
       }
        
            
    }
    
    //allows shapes to be clickable
    EventHandler<MouseEvent> shapeHandler = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent t) {
            handleShapeEvent(t, (Shape) t.getTarget());
        }
    };
    
    //editing functions
    private void deleteShape(Node _n){
        if(mode != "undo")log("cut",_n);
        pane.getChildren().remove(_n);
        
    }
    private void copyShape(){
        copy = curr;
        //pane.getChildren().add(n);
       
    }
    private void pasteShape(){
        
        Class c = curr.getClass();
        Shape p = new Rectangle();//default
        
        if(c == Rectangle.class){
          Rectangle n = (Rectangle) curr;
          p =  new Rectangle(n.getX(), n.getY(), n.getWidth(), n.getHeight());
          p.setTranslateX(curr.getTranslateX() + 20);
          p.setTranslateY(curr.getTranslateY() + 20);
           
        }else if(c == Circle.class){
             Circle n = (Circle) curr;
             p =  new Circle(n.getRadius());
             p.setTranslateX(curr.getTranslateX() + 20);
             p.setTranslateY(curr.getTranslateY() + 20);
              
        }else if(c == Ellipse.class){
             Ellipse n = (Ellipse) curr;
             p =  new Ellipse(n.getRadiusX(),n.getRadiusY());
             p.setTranslateX(curr.getTranslateX() + 20);
             p.setTranslateY(curr.getTranslateY() + 20);
              
        }else if(c == Polygon.class){
            Polygon n = (Polygon) curr;
            int l = n.getPoints().size();
            double[] temp = new double[l];
            int i = 0;
            for (double point: n.getPoints()) {
                temp[i] = point;
                i += 1;
            }
            p =  new Polygon(temp);
            p.setTranslateX(curr.getTranslateX() + 20);
            p.setTranslateY(curr.getTranslateY() + 20);
           
        }else if(c == Polyline.class){
            Polyline n = (Polyline) curr;
            int l = n.getPoints().size();
            double[] temp = new double[l];
            int i = 0;
            for (double point: n.getPoints()) {
                temp[i] = point;
                i += 1;
            }
            p =  new Polyline(temp);
            p.setTranslateX(curr.getTranslateX() + 20);
            p.setTranslateY(curr.getTranslateY() + 20);

        }
        
        p.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent t) {
                    handleShapeEvent(t, (Shape) t.getSource());
                   
                }
        });
        pane.getChildren().add(p);
        log("paste", p);
        
        System.out.println("pasted shape");
        //copy.setTranslateX(copy.getTranslateX() + copy.getWidth());
        //copy.setTranslateY(0);
   
    }
    
    //clickable shapes can be selected (edit), grouped, moved etc..
    private void handleShapeEvent(MouseEvent t, Node r){
            
         if(mode == "group"){
            if(t.getEventType() == MouseEvent.MOUSE_CLICKED){
                curr = r;
                r.setOpacity(0.6);
                newgroup.add((Shape)r);
            }
           
         }else if(mode == "edit"){
            
            if(t.getEventType() == MouseEvent.MOUSE_PRESSED){
                clearSelection();
                xPrev = t.getX();
                yPrev = t.getY();
                r.setOpacity(0.6);
                
                //add this to list of actions
                log("move", r);
                xyList.add(0.0);
                xyList.add(0.0);
                
               
            }else if(t.getEventType() == MouseEvent.MOUSE_DRAGGED){
                double dx, dy, x,y;
                
                y = r.getTranslateY();
                x = r.getTranslateX();
                dx = t.getX() - xPrev;
                dy = t.getY() - yPrev;
                
                r.setOpacity(0.6);
                r.setTranslateX(x + dx);
                r.setTranslateY(y + dy);
                
                //store parameters for undo 
                int j = xyList.size() - 1;
                int k = memList.size() - 1;
                xyList.set(j,  xyList.get(j) + dy);
                xyList.set(j-1, xyList.get(j-1) + dx);
                memList.set(k, r);
            }else if(t.getEventType() == MouseEvent.MOUSE_RELEASED){
                clearSelection();
                r.setOpacity(0.6);
                curr = r;
                
            }else if(t.getEventType() == MouseEvent.MOUSE_CLICKED){
                System.out.println("Shape Moved");
                clearSelection();
                r.setOpacity(0.6);
            }
            

        }
    }
    
   //clickable groups can be selected (edit), moved, ungrouped etc..
    private void handleGroupEvent(MouseEvent t, Group r){
            
         if(mode == "group"){
            if(t.getEventType() == MouseEvent.MOUSE_CLICKED){  
            }
         } if(mode == "edit"){
             if(t.getEventType() == MouseEvent.MOUSE_PRESSED){
                clearSelection();
                xPrev = t.getX();
                yPrev = t.getY();
                r.setOpacity(0.6);
                r.getTranslateX();
                r.getTranslateY();
         
            }else if(t.getEventType() == MouseEvent.MOUSE_DRAGGED){
                double dx, dy, x,y;
              
                y = r.getTranslateY();
                x = r.getTranslateX();
                dx = t.getX() - xPrev;
                dy = t.getY() - yPrev;
                r.setOpacity(0.6);
                r.setTranslateX(x + dx);
                r.setTranslateY(y + dy);
            }else if(t.getEventType() == MouseEvent.MOUSE_CLICKED){
                clearSelection();
                r.setOpacity(0.6);
              
            }
             curr = r;
         }
    }

    //Create a group...
    private void createGroup(ArrayList<Shape> _newgroup){
        Group g = new Group();
        ObservableList<Node> ok =  pane.getChildren();
         Node[] k = new Node[ok.size()];
        int i =0;
        for(Shape s: _newgroup){
            s.setDisable(false);
            s.setOpacity(1);
            s.removeEventFilter(MouseEvent.ANY, shapeHandler);
        }
        pane.getChildren().removeAll(_newgroup);
        g.getChildren().addAll(_newgroup);
        pane.getChildren().add(g);
        
        g.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent t) {
                    handleGroupEvent(t, (Group) t.getSource());
                }
        });
        
        if(mode != "undo" || mode == "redo")log("group", g);
        else if(mode == "undo")logRedo("ungroup", g);
        newgroup = new ArrayList<Shape>();
        //curr = g;
    }
    
    private void unGroup(Group _g){
        currG = (Group) _g;
        ObservableList<Node> l = currG.getChildren();
        Node[] k = new Node[l.size()];
        ArrayList<Shape> m = new ArrayList<Shape>();
        int i =0;
        for(Node s: currG.getChildren()){ 
            s.setDisable(false);
            s.addEventFilter(MouseEvent.ANY, shapeHandler);
            k[i] = (Shape)s;
            m.add((Shape)s);
            i += 1;
            
        }
        currG.getChildren().removeAll(k);
        pane.getChildren().addAll(k);
        pane.getChildren().remove(currG);
        
        if(mode != "undo")log("ungroup", currG);
        else if(mode == "undo")logRedo("group", m);
        currG = new Group();
      
    }
    
    private void clearSelection(){
        for (Node component : pane.getChildren()) {
            if (component instanceof Shape) {
                component.setOpacity(1);
            } 
         }
    }
    //to save actions so we can use undo later..
    private void log(String action, Object mem){
       System.out.println("logged: "+action);
       logList.add(action);
       memList.add(mem); 
    }
    
    //to save actions so we can use redo later..
     private void logRedo(String action, Object mem){
       System.out.println("logged redo: "+action);
       reList.add(action);
       remList.add(mem); 
    }
     
     //to undo action...
    private void undo(){
        //the action taken
        String _act = logList.remove(logList.size()-1);
        //the object used/stored
        Object _mem = memList.remove(memList.size()-1);
        //enable redo btn
        redoBtn.setDisable(false);
        redoBtn.setOpacity(1);
        
        System.out.println("undo: "+_act);
        switch(_act){
        
            case "group":
                unGroup((Group)_mem);
            break;
            case "ungroup":
                createGroup((ArrayList<Shape>) _mem);
            break;
            case "move":
                double y = xyList.remove(xyList.size()-1);
                double x =  xyList.remove(xyList.size()-1);
                moveNode((Node)_mem, x, y);
                r_xyList.add(-x);
                r_xyList.add(-y);
                logRedo("move",_mem);
            break;
            case "draw":
                deleteShape((Node)_mem);
                logRedo("draw", _mem);
            break;    
            case "cut":
                pane.getChildren().add((Node)_mem);
                ((Node)_mem).setOpacity(1);
                logRedo("cut", _mem);
            break;
            case "paste":
                logRedo("paste", _mem);
                pane.getChildren().remove((Node)_mem);
            break;
      
    }
    }
   
    //to undo action...
    private void redo(){
        String _act = reList.remove(reList.size()-1);
        Object _mem = remList.remove(remList.size()-1);
        if(reList.size() == 0){
            redoBtn.setDisable(true);
            redoBtn.setOpacity(0.6);
        }
        System.out.println("redo: "+_act);
        
        switch(_act){
        
            case "group":
                createGroup((ArrayList<Shape>)_mem);
            break;
            case "ungroup":
                unGroup((Group) _mem);
            break;
            case "move":
                double y = r_xyList.remove(r_xyList.size()-1);
                double x =  r_xyList.remove(r_xyList.size()-1);
                moveNode((Node)_mem, x, y);
                xyList.add(-x);
                xyList.add(-y);
                log("move", _mem);
            break;
            case "draw":
                 pane.getChildren().add((Node)_mem);
                ((Node)_mem).setOpacity(1);
                log("draw", _mem);
            break;    
            case "cut":
                deleteShape((Node)_mem);
                log("cut", _mem);
            break;
            case "paste":
                pane.getChildren().add((Node)_mem);
                log("paste", _mem);
                ((Node)_mem).setOpacity(1);
                
            break;
            default:
            break;
        }
    }
    
    //Additional functions for undo/redo capabilities...
    private void moveNode(Node _n, double _dx, double _dy){
        _n.setTranslateX(_n.getTranslateX() - _dx); 
        _n.setTranslateY(_n.getTranslateY() - _dy); 
    
    }
    
   //save nodes from sketchpad
    private void saveConfig(){
        for(Node _n: pane.getChildren()){
            savedNodes.add(_n);
        }
    }
    
    //load last saved nodes
    private void loadConfig(){
        pane.getChildren().removeAll(pane.getChildren());
        pane.getChildren().addAll(savedNodes);
    }
    
        
    private void checkIfPolyMode(String _mode){
        
        //if we're drawing a polygon already and we click polygon
        //finish the polygon.....
        if (mode == "poly"){
           pane.getChildren().remove(polyGroup);
           mode = "none";
            shapes.add(poly);
            System.out.println(poly);
            poly.addEventFilter(MouseEvent.ANY , new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent t) {
                    handleShapeEvent(t, (Shape) t.getTarget());
                }
            });
           log("draw", poly);
         //otherwise it's a new polygon...
        }else if(_mode == "poly"){
            pane.getChildren().add(polyGroup);
            pane.getChildren().add(poly);
            label.setText("Click for points for polygon. When done click any button to complete.");
         //if we're drawing an open polygon already and we click open polygon
        //finish the open polygon.....
        }else if (mode == "polyOpen"){
           pane.getChildren().remove(polyOpenGroup);
           
           mode = "none";
            shapes.add(polyOpen);
            System.out.println(polyOpen);
            polyOpen.addEventFilter(MouseEvent.ANY , new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent t) {
                    handleShapeEvent(t, (Shape) t.getTarget());         
                }
            });
            log("draw", polyOpen);
        //otherwise it's a new open polygon...
        }else if(_mode == "polyOpen"){
            label.setText("Click for points for polygon. When done click any button to complete.");
            pane.getChildren().add(polyOpenGroup);
            pane.getChildren().add(polyOpen);
        }
        
       
        
    }
    private void changeMode(ActionEvent ev, String _mode) {
        
        clearSelection();
        label.setText("To select a shape click edit. To draw select shape type.");

        checkIfPolyMode(_mode);
        mode = _mode;
        System.out.println("Mode selected: "+mode);
        
        if(_mode == "donegroup"){
            createGroup(newgroup);
            _mode = "none";
        }else if(mode == "group"){
            label.setText("Select all shapes to be in group then click done.");
        }else if(_mode == "ungroup"){
            unGroup((Group)curr);
        }else if(_mode == "undo"){
            undo();
            _mode = "none";
        }else if(_mode == "redo"){
            redo();
            _mode = "none";
        }else if(_mode == "save"){
            saveConfig();
            _mode = "none";
        }else if(_mode == "load"){
            loadConfig();
            _mode = "none";
        }
        mode = _mode;
        System.out.println("Current mode: "+mode);
        
        //System.out.println(mode);
        
        
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        
        redoBtn.setDisable(true);
        redoBtn.setOpacity(0.5);
        circBtn.setOnAction(event -> changeMode(event, "circle"));
        rectBtn.setOnAction(event -> changeMode(event, "rect"));
        polyBtn.setOnAction(event -> changeMode(event, "poly"));
        polyOpenBtn.setOnAction(event -> changeMode(event, "polyOpen"));
        lineBtn.setOnAction(event -> changeMode(event, "line"));
        elipBtn.setOnAction(event -> changeMode(event, "elip"));
        squareBtn.setOnAction(event -> changeMode(event, "square"));
        freeBtn.setOnAction(event -> changeMode(event, "free"));
        editBtn.setOnAction(event -> changeMode(event, "edit"));
        doneBtn.setOnAction(event ->  changeMode(event, "donegroup"));
        groupBtn.setOnAction(event ->  changeMode(event, "group"));
        ungroupBtn.setOnAction(event ->  changeMode(event, "ungroup"));
        undoBtn.setOnAction(event ->  changeMode(event, "undo"));
        redoBtn.setOnAction(event ->  changeMode(event, "redo"));
        saveBtn.setOnAction(event ->  changeMode(event, "save"));
        loadBtn.setOnAction(event ->  changeMode(event, "load"));
        delBtn.setOnAction(event -> deleteShape(curr));
        copyBtn.setOnAction(event -> copyShape());
        pasteBtn.setOnAction(event -> pasteShape());
    }    
    
    
    
}



/*
 gc.strokeLine(100, 100, 300, 50);
        gc.beginPath();
        gc.moveTo(ev.getX(), ev.getY());
        gc.stroke();
        gc.closePath();

*/
