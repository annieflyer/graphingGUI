package graphingGUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import graphingGUI.Village;


public class Road {

	// Road class
		
		private Village start;
		private Village end;
		private boolean visible=true;
		private int num_roads=0;// how many roads have already been connected 
		private boolean two_road=false ;
		private boolean isFull=false;
		private int cost;
		private int Imgsize;
		private int capacity=10;
		public  Color minimalColor=Color.CYAN;
		private Color out= Color.red;
		private Color in= Color.BLUE;
		
		public Road(Village start, Village end){
		  this.start=start;
		  this.end=end;
		}
		public void drawRoad(Graphics g){
        if(this.isVisible()==true){
			if(two_road==false)
		    {   if(isFull==true){
				this.setOut(Color.magenta);
			    this.setIn(Color.magenta);
		    }
			g.setColor(out);
			Graphics2D g1=(Graphics2D)g;
			g1.setFont(new Font("TimesRoman",Font.PLAIN,20));		
			g1.setStroke(new BasicStroke(4));
			g1.drawLine((int)(start.getPosition().getX()+0.5*Imgsize), (int)(start.getPosition().getY()+0.5*Imgsize),(int)(start.getPosition().getX()/2+end.getPosition().getX()/2+0.5*Imgsize),(int)(end.getPosition().getY()/2+start.getPosition().getY()/2+0.5*Imgsize));
			g1.setColor(in);
			g1.drawLine((int)(start.getPosition().getX()/2+end.getPosition().getX()/2+0.5*Imgsize),(int)(end.getPosition().getY()/2+start.getPosition().getY()/2+0.5*Imgsize), (int)(end.getPosition().getX()+0.5*Imgsize),(int)(end.getPosition().getY()+0.5*Imgsize));
			g1.setColor(Color.yellow);
			g1.drawString(""+cost,(int)(start.getPosition().getX()/2+end.getPosition().getX()/2+0.5*Imgsize), (int)(start.getPosition().getY()/2+end.getPosition().getY()/2+0.5*Imgsize));
			
		    }
			
			else
			{
				if(isFull==true){
				this.setOut(Color.magenta);
			    this.setIn(Color.magenta);}
		    g.setColor(out);
			Graphics2D g1=(Graphics2D)g;
			g1.setFont(new Font("TimesRoman",Font.PLAIN,20));		
			g1.setStroke(new BasicStroke(4));
			g1.drawLine((int)(start.getPosition().getX()+0.4*Imgsize), (int)(start.getPosition().getY()+0.5*Imgsize),(int)(start.getPosition().getX()/2+end.getPosition().getX()/2+0.4*Imgsize),(int)(end.getPosition().getY()/2+start.getPosition().getY()/2+0.5*Imgsize));
			g1.setColor(in);
			g1.drawLine((int)(start.getPosition().getX()/2+end.getPosition().getX()/2+0.4*Imgsize),(int)(end.getPosition().getY()/2+start.getPosition().getY()/2+0.5*Imgsize), (int)(end.getPosition().getX()+0.4*Imgsize),(int)(end.getPosition().getY()+0.5*Imgsize));
			g1.setColor(Color.yellow);
			g1.drawString(""+cost,(int)(start.getPosition().getX()/2+end.getPosition().getX()/2+0.3*Imgsize), (int)(start.getPosition().getY()/2+end.getPosition().getY()/2+0.5*Imgsize));
			}
        }
        else{
        	return;
        }
        }
		/**
		 * @return the visible
		 */
		public boolean isVisible() {
			return visible;
		}
		/**
		 * @param visible the visible to set
		 */
		public void setVisible(boolean visible) {
			this.visible = visible;
		}
		public Village getStart(){
			return this.start;
		}
		public Village getEnd(){
			return this.end;
		}
		/**
		 * @return the num_roads
		 */
		public int getNum_roads() {
			return num_roads;
		}
		public void setCost(int cost){
			this.cost=cost;
		}
		/**
		 * @param num_roads the num_roads to set
		 */
		public void setNum_roads(int num_roads) {
			this.num_roads = num_roads;
		}
		/**
		 * @return the two_road
		 */
		public boolean isTwo_road() {
			return two_road;
		}
		/**
		 * @param two_road the two_road to set
		 */
		public void setTwo_road(boolean two_road) {
			this.two_road = two_road;
		}
		/**
		 * @return the isFull
		 */
		public boolean isFull() {
			return this.capacity<=0;
		}
		/**
		 * @param isFull the isFull to set
		 */
		public void setFull(boolean isFull) {
			this.isFull = isFull;
		}
		public void setImgsize(int size){
			this.Imgsize=size;
		}
		/**
		 * @return the minimalColor
		 */
		public Color getMinimalColor() {
			return minimalColor;
		}
		/**
		 * @param minimalColor the minimalColor to set
		 */
		public void setMinimalColor(Color minimalColor) {
			this.minimalColor = minimalColor;
		}
		/**
		 * @return the out
		 */
		public Color getOut() {
			return out;
		}
		/**
		 * @param out set out color 
		 */
		public void setOut(Color out) {
			this.out = out;
		}
		/**
		 * @return the in
		 */
		public Color getIn() {
			return in;
		}
		/**
		 * @param in set in color
		 */
		public void setIn(Color in) {
			this.in = in;
		}
        public void setCapacity(int cap){
        	this.capacity=cap;
        }	
        public int getCapacity(){
        	return this.capacity;
        }

}


