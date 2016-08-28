package graphingGUI;

/**
 * This is the main tester, which will show you the layout of the window and so on. 
 */

import javax.imageio.ImageIO; //extension package, which is used to graph i/o
import javax.swing.*;//java display style 
import java.awt.*;  //Abstract Windowing ToolKit
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapPanel extends JPanel implements ActionListener{

	/**
	 * This is used to display the size of the window, button, layout and so on. 
	 */
	public static final int WINDOWSIZE_X =1500;
	public static final int WINDOWSIZE_Y =800;
	private static final long serialVersionUID = 1L;

	JPanel mainPanel;

	MapPanel displayPanel;

	JPanel HistoryPanel;
	JPanel CommentPanel;
	JTextArea records;// a Jtextfield to record all the operations by the user
	JTextArea comment=new JTextArea();
	private JPanel ButtonPanel=new JPanel();
	private DisplayPanel display= new DisplayPanel(new Dimension((int)(WINDOWSIZE_X*0.64),(int)(WINDOWSIZE_Y*0.8)));

	//buttons that you want to show up in your window 
	JButton AddVillage,DeleteVillage,AddRoad,DeleteRoad,AddGnome,Search,RandomWalk,TargetWalk,title,OK,cancel,TopoSort,refresh;
	boolean GelbinTalks=true;
	BufferedReader speech= new BufferedReader(new FileReader("speech")); //I import my read me file. 
	BufferedReader cancelReader= new BufferedReader(new FileReader("speech"));

	ArrayList<Node> villages= new ArrayList<>();
	List<Gnome> gnomes= new ArrayList<>();
	List<Road> roads= new ArrayList<>();

	private int R = 0;
	private int G = 0;
	private int B = 0;

	private boolean drawVillage=true;
	private boolean drawGnome=true;
	private boolean drawRoad=true;
	private boolean deletaVillage=true;
	private boolean search=true;
	private boolean deleteRoad=true;
	private boolean randomWalk=false;
	private boolean targetWalk=false;
	private boolean isMoving=false;
	private int Imgsize=50;
	private boolean move=true;
	private boolean people_on_road=false;
	private boolean cont=false;
	private DirectedGraph graph;
	Image background= ImageIO.read(this.getClass().getResource("background.png")).getScaledInstance((int)(WINDOWSIZE_X*0.61), (int)(WINDOWSIZE_Y*0.68), Image.SCALE_DEFAULT);



	//import the files that you prepare for the building of the window 
	public MapPanel() throws IOException{
		BufferedImage border= ImageIO.read(this.getClass().getResource("background_border.jpg"));
		this.setBorder(BorderFactory.createMatteBorder(10,10,10,10,new ImageIcon(border)));

		//beginning position to connect village 
		graph = new DirectedGraph();
		try (BufferedReader br = new BufferedReader(new FileReader("origin_data"))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				String[] str = sCurrentLine.split(" ");
				graph.addEdge(str[0], str[1], Integer.parseInt(str[2]));
			}

		} catch (IOException e) { // exceptions 
			System.out.println("No such file.");
			return;
		}

		villages=graph.getNodes(); 	 
		int j=1,k=1;
		int size=1200/villages.size();
		for (Node village:villages){
			//this is how you places your nodes on the map, I simulate the random walk 
			double xc= (size*j);
			double yc= (230+k*220*Math.random());
			village.setVillage(new Village(new coordinates(xc,yc),Color.red,(int)(2*Math.random()),village.getLabel(),Imgsize));
			if(k==1)
				j++;
			k=-k;
		}
		for(Node village:villages){
			for (Edge e : village.getEdges()){
				Road newRoad=new Road(village.getData(),e.getDest().getData());
				newRoad.setCapacity(10);
				if(findRoad(e.getDest().getData(),village.getData())!=null){
					newRoad.setTwo_road(true);
				}
				newRoad.setCost(e.getCost());
				newRoad.setImgsize(70);
				roads.add(newRoad);
			}
		}
		//the layout of the whole window 

		JFrame frame = new JFrame("Gnome Map");//main window
		this.setLayout(new BorderLayout());

		this.addButtonPanel(this.ButtonPanel);
		HistoryPanel= new JPanel();// the panel that shares the history of the map
		CommentPanel= new JPanel();// the panel that gives out comments.    

		this.addCommentPanel(CommentPanel);
		this.addHistoryPanel(HistoryPanel);

		//exact placement

		frame.add(ButtonPanel,BorderLayout.EAST);
		frame.add(display,BorderLayout.CENTER);  
		frame.add(CommentPanel, BorderLayout.SOUTH);  
		frame.add(HistoryPanel, BorderLayout.WEST);  



		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(WINDOWSIZE_X,WINDOWSIZE_Y));
		frame.pack();
		frame.setVisible(true);
	}
	//details of the window, eg: font, size, and so on. 
	private void addHistoryPanel(JPanel HistoryPanel) throws IOException {
		BufferedImage border= ImageIO.read(this.getClass().getResource("background_border.jpg"));
		HistoryPanel.setPreferredSize(new Dimension((int)(WINDOWSIZE_X*0.17),(int)(WINDOWSIZE_Y*0.8)));
		HistoryPanel.setBorder(BorderFactory.createMatteBorder(10,10,5,10,new ImageIcon(border)));
		HistoryPanel.setLayout(new BorderLayout());

		this.title= new JButton("History of operations");
		title.setPreferredSize(new Dimension(20,40));
		records= new JTextArea("");
		records.setFont(new Font("Serif", Font.BOLD, 15));
		records.setEditable(false);

		final JScrollPane scroll= new JScrollPane(records);
		HistoryPanel.add(title,BorderLayout.NORTH);
		HistoryPanel.add(scroll,BorderLayout.CENTER);
		title.addActionListener(new ActionListener(){


			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFrame frame1 = new JFrame("History of operations");
				frame1.setLayout(new BorderLayout());

				frame1.add(scroll,BorderLayout.CENTER);

				frame1.setPreferredSize(new Dimension(WINDOWSIZE_X/2,WINDOWSIZE_Y/2));
				frame1.pack();
				frame1.setVisible(true);
			}
		});


	}
	//keep on adding details to your window 
	private void addCommentPanel(JPanel CommentPanel) throws IOException {
		BufferedImage border= ImageIO.read(this.getClass().getResource("background_border.jpg"));
		CommentPanel.setPreferredSize(new Dimension((int)(WINDOWSIZE_X),(int)(WINDOWSIZE_Y*0.25)));
		CommentPanel.setBorder(BorderFactory.createMatteBorder(3, 10, 10, 10, new ImageIcon(border)));
		CommentPanel.setLayout(new BorderLayout());
		Image img=null;
		try{
			img=ImageIO.read(this.getClass().getResource("icon.jpg")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
		}
		catch (IOException e){
			System.out.println("ouch");
		}

		JLabel newLabel= new JLabel(new ImageIcon(img));

		newLabel.setPreferredSize(new Dimension(240,205));

		newLabel.setMaximumSize(new Dimension(300,300));
		newLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 6, new ImageIcon(border)));
		CommentPanel.add(newLabel,BorderLayout.WEST);

		JPanel conversation= new JPanel();
		conversation.setLayout(new BorderLayout());
		conversation.setBorder(BorderFactory.createMatteBorder(2, 5, 5, 5, new ImageIcon(border)));
		CommentPanel.add(conversation,BorderLayout.CENTER);

		this.comment= new JTextArea("\tClick Ok to start\n\t");
		comment.setCaretPosition(comment.getText().length() - 1);
		comment.setFont(new Font("Serif", Font.BOLD, 15));

		comment.setEditable(false);
		JScrollPane commentscroll= new JScrollPane(comment);
		conversation.add(commentscroll,BorderLayout.CENTER);



		JPanel selection= new JPanel();
		selection.setLayout(new GridBagLayout());


		conversation.add(selection,BorderLayout.SOUTH);
		this.OK= new JButton("OK");
		OK.addActionListener(this);
		this.cancel= new JButton("Cancel");
		cancel.addActionListener(this);
		GridBagConstraints yesConstraints= new GridBagConstraints();
		yesConstraints.gridx=0;
		yesConstraints.gridy=0;
		yesConstraints.weightx=0.5;
		yesConstraints.ipadx=60;
		GridBagConstraints noConstraints= new GridBagConstraints();
		noConstraints.gridx=1;
		noConstraints.gridy=0;
		noConstraints.weightx=0.5;
		noConstraints.ipadx=50;
		selection.add(OK,yesConstraints);
		selection.add(cancel,noConstraints);

	}

	public void addButtonPanel(JPanel ButtonPanel) throws IOException{
		BufferedImage border= ImageIO.read(this.getClass().getResource("background_border.jpg"));

		ButtonPanel.setPreferredSize(new Dimension((int)(WINDOWSIZE_X*0.13),(int)(WINDOWSIZE_Y*0.8)));
		ButtonPanel.setBorder(BorderFactory.createMatteBorder(10, 10,10, 10, new ImageIcon(border)));

		ButtonPanel.setLayout(new GridLayout(10,1));
		
		this.AddVillage = new JButton("<html><font size=5><font color=black>Add a village</font></font></html>");
		AddVillage.setHorizontalTextPosition(SwingConstants.CENTER);
		AddVillage.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new ImageIcon(border)));
		AddVillage.addActionListener(new ActionListener(){
			@Override

			//here you are going to decide where to place your new added node and provide the info of that node
			public void actionPerformed(ActionEvent e) {
				try{
					cont=true;
					String placex=JOptionPane.showInputDialog("Please enter the x coordinate");
					int x = (int) Double.parseDouble(placex);
					String placey=JOptionPane.showInputDialog("Please enter the y coordinate");
					int y = (int) Double.parseDouble(placey);
					cont=true;
					String id=JOptionPane.showInputDialog("Please enter the id of the village");

					Color color =new Color(R,G,B);
					Village toBeAdded=new Village(new coordinates(x, y), color,(int)(2*Math.random()),id,Imgsize);

					graph.addNode(id);
					villages = graph.getNodes();
					graph.getLookUp().get(id).setVillage(toBeAdded);

					comment.setText(null);	                
					display.repaint();
					records.append("  A new Village "+id+" has been built.\n");
				}
				catch (NullPointerException n1){  //if you don't follow the rule of the input, you will be shown this and re-input the info
					comment.setText("\tOooooooooooops, looks like you have made a mistake. Yah should learn from it!");
				}
			}});


		//second: delete a village from the map 
		this.DeleteVillage = new JButton("<html><font size=5><font color=black>Delete a village</font></font></html>");
		DeleteVillage.setHorizontalTextPosition(SwingConstants.CENTER);
		DeleteVillage.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0, new ImageIcon(border)));
		DeleteVillage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try{	String id=JOptionPane.showInputDialog("Please enter the id of the village");// user will tell us which one to remove 
				int id_number = (int) Double.parseDouble(id);
				graph.removeNode(""+id_number); 

				villages=graph.getNodes();

				roads= new ArrayList<Road>();
				for(Node village:villages){
					for (Edge e1 : village.getEdges()){
						Road newRoad=new Road(village.getData(),e1.getDest().getData());
						newRoad.setCapacity(10);//if you add people beyond the capacity, you will see this 
						newRoad.setCost(e1.getCost());
						newRoad.setImgsize(70);
						roads.add(newRoad);
					}	               

				}comment.setText(null);

				comment.append("\tAnother village in ruins :(");
				display.repaint();
				records.append("  A Village "+id_number+" has been destroyed :(\n");
				}
				catch(NullPointerException n2){
					comment.setText("\tIllegal input!!");
				}
			}

		});



		// Add a road to the map
		this.AddRoad = new JButton("<html><font size=5><font color=black>Add a road</font></font></html>.");
		AddRoad.setHorizontalTextPosition(SwingConstants.CENTER);
		AddRoad.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0, new ImageIcon(border)));
		AddRoad.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				cont=true;
				try	{String x1=JOptionPane.showInputDialog("Please enter id of the starting village");
				cont=true;
				String placey=JOptionPane.showInputDialog("Please enter the id of the ending village");	          
				int costRoad = (int) Double.parseDouble(JOptionPane.showInputDialog("Please enter the cost of the ending village"));

				Village start=findVillage(x1).getData();
				Village end=findVillage(placey).getData();

				Road toBeAdded= new Road(start,end);
				toBeAdded.setCost(costRoad);
				toBeAdded.setImgsize(Imgsize);

				graph.addEdge(x1,placey,costRoad); 

				if(findRoad(start,end)==null){
					if(findRoad(end,start)!=null){
						toBeAdded.setTwo_road(true);
					}
					roads.add(toBeAdded);	// roads are actually the current List<Edges>

				}
				else{
					comment.setText(null);
					comment.setText("\n\tSorry but there seems to be no place for this new road");
				}

				comment.setText(null);
				comment.append("\tConnection!");
				display.repaint();
				records.append("  A new road between Village "+start.getId()+" and\n  Village "+end.getId()+" has been built.\n");
				}
				catch(NullPointerException n3){
					comment.setText("\tThink again...look before your leap.Did you input everything?");
				}
			}
		});
		// Delete a road
		this.DeleteRoad = new JButton("<html><font size=5><font color=black>Delete a road</font></font></html>");
		DeleteRoad.setHorizontalTextPosition(SwingConstants.CENTER);
		DeleteRoad.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0, new ImageIcon(border)));
		DeleteRoad.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String x1=JOptionPane.showInputDialog("Please enter id of the starting village");
				String placey=JOptionPane.showInputDialog("Please enter the id of the ending village");  	 
				findVillage(x1).removeEdge(findVillage(placey));
				roads= new ArrayList<Road>();
				for(Node village:villages){
					for (Edge e1 : village.getEdges()){
						Road newRoad=new Road(village.getData(),e1.getDest().getData());
						newRoad.setCapacity(10);
						newRoad.setCost(e1.getCost());
						newRoad.setImgsize(70);
						roads.add(newRoad);
					}	               

				}   comment.setText(null);
				comment.append("\tThat road was destroyed by rivalries :(");
				display.repaint();
				records.append("  A road between Village "+x1+" and\n  Village "+placey+" has been destroyed :(.\n");
			}
		});

		// Add a gnome with a ID(either name or id) to the map
		this.AddGnome = new JButton("<html><font size=5><font color=black>Add a gnome</font></font></html>");
		AddGnome.setHorizontalTextPosition(SwingConstants.CENTER);
		AddGnome.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0, new ImageIcon(border)));
		AddGnome.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				cont=true;
				try{	String origin=JOptionPane.showInputDialog("Please enter the origin of this Gnome!(Using the Gnome Village ID)");
				cont=true;
				String ID=JOptionPane.showInputDialog("Please enter the id of this Gnome!(Using the Gnome Village ID)");

				Village Origin=findVillage(origin).getData();
				Color color =new Color(R,G,B);
				gnomes.add(new Gnome(color,(int)(2*Math.random()),Origin,ID,Origin.getPosition()));
				comment.setText(null);
				comment.append("\tWelcome! New visitor!");
				display.repaint();
				records.append("  A new gnome has been added.\n");
				} 
				catch(NullPointerException n4){
					comment.setText("\t...Mistakes were made");
				}
			}


		});

		// enter a specific criteria to search for a gnome
		this.Search= new JButton("<html><font size=5><font color=black>Search</font></font></html>");
		Search.setHorizontalTextPosition(SwingConstants.CENTER);
		Search.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0, new ImageIcon(border)));
		Search.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				cont=true; 

				try{ String ID=JOptionPane.showInputDialog("Please enter the id of this Gnome!(Using the Gnome Village ID)");
				Gnome found=findGnome(ID);
				found.setFound(true);
				display.repaint();
				records.append("  A gnome "+ID+" has been found.\n");
				}
				catch(NullPointerException n5){
					comment.setText("\tDid you check everything there is?");
				}
			}


		});
		// You want to find a mincost to build roads to connect all villages 
		this.RandomWalk = new JButton("<html><font size=5><font color=black>MST Route</font></font></html>");
		RandomWalk.setHorizontalTextPosition(SwingConstants.CENTER);
		RandomWalk.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0, new ImageIcon(border)));
		RandomWalk.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String initial= JOptionPane.showInputDialog("Please enter the starting village to draw the MST");
				graph.MST(initial);
				roads= new ArrayList<Road>();
				for(Node village:villages){
					for (Edge e1 : village.getEdges()){
						Road newRoad=new Road(village.getData(),e1.getDest().getData());
						newRoad.setCapacity(10);
						newRoad.setCost(e1.getCost());
						newRoad.setImgsize(70);
						if(e1.belongsToMST())
						{ newRoad.setOut(newRoad.minimalColor);
						newRoad.setIn(newRoad.minimalColor);}
						else{
							newRoad.setOut(Color.gray);

							newRoad.setIn(Color.gray);
						}
						roads.add(newRoad);
					}	               

				}

				display.repaint();

				records.append("  The minimal spanning tree is highlighted.\n");

			}



		});



		// enter a destination to let a gnome head to that village according to your instruction
		this.TargetWalk = new JButton ("<html><font size=4><font color=black>Choose a destination</font></font></html>");
		TargetWalk.setHorizontalTextPosition(SwingConstants.CENTER);
		TargetWalk.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new ImageIcon(border)));
		TargetWalk.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {

				cont=true;
				JOptionPane.showMessageDialog(display, "Please click on the gnome you want it to move");
				isMoving=true;
				display.repaint();
				comment.setText(null);
				comment.append("\tLet's move!");
				records.append("  A new gnome has started towards a destination");
			}


		});

		//topological sort to find an appropriate way for you to walk through
		this.TopoSort= new JButton("<html><font size=5><font color=red>Show Topo-order</font></font></html>");

		TopoSort.setHorizontalTextPosition(SwingConstants.CENTER);
		TopoSort.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0, new ImageIcon(border)));
		TopoSort.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				graph.tpSortBFS();

				ArrayList<Node> ha= graph.getNodes();
				if(graph.isIscycle()!=true)
				{comment.setText(null);
				comment.setText("\tHa!There is the power-map of this country!\n\tEach village ranked higher is more poweeerful.\n");
				for (Node n:ha){
					comment.append("\t"+n.getData().getId());    
				} 

				records.append("  The topological result has been shown\n");
				}

				else{
					comment.setText(null);
					comment.setText("\tThere is a cycle of power\n\tRemember each village ranked higher is more poweeerful.\n");

					//there is no way to have a cycle in a topological sort

					records.append("  The topological result has been shown\n");

				}
			}


		});

		//import the original info to help you build your map. This is the base, and following things are based on this map. 
		this.refresh = new JButton("<html><font size=5><font color=red>Refresh</font></font></html>");
		refresh.setHorizontalTextPosition(SwingConstants.CENTER);
		refresh.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0, new ImageIcon(border)));
		refresh.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				graph = new DirectedGraph();
				try (BufferedReader br = new BufferedReader(new FileReader("origin_data"))) {

					String sCurrentLine;

					while ((sCurrentLine = br.readLine()) != null) {
						String[] str = sCurrentLine.split(" ");
						graph.addEdge(str[0], str[1], Integer.parseInt(str[2]));
					}

				} catch (IOException ex) {
					System.out.println("No such file.");
					return;
				}
				graph.tpSortBFS();//use breadth first search to find the topological order 

				villages= new ArrayList<>();
				roads=new ArrayList<>();
				gnomes= new ArrayList<>();
				villages=graph.getNodes(); 	 
				int j=1,k=1;
				int size=1200/villages.size();
				for (Node village:villages){
					double xc= (size*j);
					double yc= (250+k*200*Math.random());
					village.setVillage(new Village(new coordinates(xc,yc),Color.red,(int)(2*Math.random()),village.getLabel(),Imgsize));
					if(k==1)
						j++;
					k=-k;
				}
				for(Node village:villages){
					for (Edge e1 : village.getEdges()){
						Road newRoad=new Road(village.getData(),e1.getDest().getData());
						if(findRoad(e1.getDest().getData(),village.getData())!=null){
							newRoad.setTwo_road(true);
						}
						newRoad.setCapacity(10);
						newRoad.setCost(e1.getCost());
						newRoad.setImgsize(70);
						roads.add(newRoad);
					}
				} 
				display.repaint();
				comment.setText(null);
				comment.append("\tIs that what you plan for our new city. If not, press refresh button");
				records.append("  The map has been refreshed.\n");// go back to default condition 
			}
		});

		//placement of all your buttons
		ButtonPanel.add(AddVillage,BorderLayout.CENTER);
		ButtonPanel.add(DeleteVillage,BorderLayout.CENTER);
		ButtonPanel.add(AddRoad,BorderLayout.CENTER);
		ButtonPanel.add(DeleteRoad,BorderLayout.CENTER);
		ButtonPanel.add(AddGnome,BorderLayout.CENTER);
		ButtonPanel.add(Search,BorderLayout.CENTER);
		ButtonPanel.add(RandomWalk,BorderLayout.CENTER);
		ButtonPanel.add(TargetWalk,BorderLayout.CENTER);
		ButtonPanel.add(TopoSort,BorderLayout.CENTER);   
		ButtonPanel.add(refresh,BorderLayout.CENTER);
	}
	private void glows(JComponent n) throws IOException, InterruptedException{
		BufferedImage border= ImageIO.read(this.getClass().getResource("background_border.jpg"));
		BufferedImage border1= ImageIO.read(this.getClass().getResource("background_border.jpg"));


		for(int i=0;i<10;i++)
		{if(i%2==0)  
		{n.setBorder(BorderFactory.createMatteBorder(10, 10,10, 10, new ImageIcon(border)));
		Thread.sleep(300);
		n.repaint();
		}else
		{n.setBorder(BorderFactory.createMatteBorder(10, 10,10, 10, new ImageIcon(border1)));
		Thread.sleep(300);
		n.repaint();
		}     	
		}		

	}
	private class DisplayPanel extends JPanel  {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Dimension preferredSize;


		private Gnome traveler;
		private Village depart;

		int i=0;
		public DisplayPanel(Dimension prefer) throws IOException{
			this.preferredSize=prefer;
			BufferedImage border= ImageIO.read(this.getClass().getResource("background_border.jpg"));
			//Add border of 10 pixels at L and bottom, and 4 pixels at the top and R.
			setBorder(BorderFactory.createMatteBorder(10,5,5,5,new ImageIcon(border)));
			randomWalk=false;
			setBackground(Color.white);	   
			this.setLayout(new BorderLayout());

			addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					i=i+1;
					if(isMoving){
						// moveSquare(e.getX(),e.getY());
						double max= Double.MAX_VALUE;
						String id="";
						coordinates click= new coordinates(e.getX()-0.5*Imgsize,e.getY()-0.5*Imgsize);


						for(Gnome gnome:gnomes){
							double length=gnome.getLocation().length(click, gnome.getLocation());
							if (length <max)
							{   id=gnome.getID();
							max=length;
							}
						}
						traveler=findGnome(id);  // pick up the Gnome that you want to assign a destination
						depart= traveler.getOrigin();
						cont=true;
						String reply=JOptionPane.showInputDialog("Are you going with this Gnome :)?");

						if (reply.equalsIgnoreCase("no"))
						{   traveler.setLazy(true);
						cont=true;
						int destination_number= Integer.parseInt(JOptionPane.showInputDialog("Please enter the id of the destination village"));

						ArrayList<Node> route;
						route = graph.shortestPath(""+depart.getId(),""+destination_number);

						traveler.setDestination(route);       

						comment.append("\n\tThe shortest path to the destination is "+route.toString());


						new Thread(traveler).start();    
						}
						if(reply.equalsIgnoreCase("yes"))
						{

							traveler.setLazy(false);
							drawGnome=false;
							cont=true;
							String destination_number=  JOptionPane.showInputDialog("Please enter the id of the destination village");

							ArrayList<Node> route;
							route =graph.shortestPath(""+depart.getId(),""+destination_number);
							comment.append("\n\tThe shortest path to the destination is "+route.toString());
							traveler.setDestination(route);    
							drawGnome=true;	              
							// Work out the entire list of shortest path
							// for a always set a previous one to be the origin and the next one to be the destination until it is done
							new Thread(traveler).start();
						}

						else{
							comment.setText(null);
							comment.setText("Invalid input, answer using yes or no");//a guide to show you the things that you should input
						}


					} 
					else{
						if (i==1)
						{comment.setText(null);
						comment.setText("\tHa, Looks like you need to click on the button first!");
						}	
						if (i==2)
						{comment.setText(null);
						comment.setText("\tDidn't you pay attention to the head technician? Button first!");
						}
						if (i==3)
						{comment.setText(null);
						comment.setText("\tYoung man you are really a headache to me, if you want to continue, click on the button..");
						}
						if (i==4)
						{comment.setText(null);
						comment.setText("\tIf you continue to do this, you will be stopped");
						} if (i==5)
						{comment.setText(null);
						comment.setText("\tAhhhh....stop distracting me!");
						} if (i==6)
						{comment.setText(null);
						comment.setText("\tCould you please leave me alone!!!");
						}
						if (i==7)
						{comment.setText(null);
						comment.setText("\tummm... I am considering punishment if you continue");
						}
						if (i==8)
						{comment.setText(null);
						comment.setFont(new Font("Serif", Font.BOLD, 30));
						comment.setText("<html><font color=red>YOU ARE KICKED OUT OF THIS SIMULATION</font></html>");
						try {
							Thread.sleep(4000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						System.exit(0);
						}
					}
				} 
			});




		}  // end of constructor


		@Override
		public Dimension getPreferredSize() {
			return preferredSize;
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(background,0,0,null);
			if(drawVillage){
				villages=graph.getNodes();
				for (Node village :villages) {
					try {
						village.getData().drawVillage(g);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(drawRoad){
				for (Road road :roads) {
					road.drawRoad(g);
					road.setOut(Color.RED);
					road.setIn(Color.BLUE);
					if(road.isVisible()==false){
						road.setVisible(true);
					}

				}
			}
			if (drawGnome){
				for (Gnome gnome :gnomes) {
					try {
						gnome.drawGnome(g);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			if(deletaVillage){

			}
			if(deleteRoad){

			}
			if(search){

			}

			if(randomWalk){  

			}



			if(targetWalk)
			{

			}

		}  

	} 
	public void moveGnome(Gnome traveler,int x, int y){


		// Current square state, stored as final variables 
		// to avoid repeat invocations of the same methods.
		final int CURR_X =(int) traveler.getLocation().getX();
		final int CURR_Y =(int) traveler.getLocation().getY();
		final int CURR_W = Imgsize;
		final int CURR_H = Imgsize;
		final int OFFSET = 1;


		if ((CURR_X!=x) || (CURR_Y!=y)) {

			// The square is moving, repaint background 
			// over the old square location. 
			display.repaint(CURR_X,CURR_Y,CURR_W+OFFSET,CURR_H+OFFSET);
			// Update coordinates.

			traveler.setLocation(new coordinates(x,y));
			// Repaint the square at the new location.
			display.repaint((int)(traveler.getLocation().getX()),(int)(traveler.getLocation().getY()), Imgsize+OFFSET,  Imgsize+OFFSET);  
		}



	}


	private Node findVillage(String id) {
		// TODO Auto-generated method stub
		for (Node village :villages) {
			if (village.getData().getId().equalsIgnoreCase(id)){
				return village;
			}
		}
		return null;
	}
	private Road findRoad(Village start, Village end) {
		// TODO Auto-generated method stub
		for (Road road :roads) {
			if (road.getStart().getId()==start.getId()&&road.getEnd().getId()==end.getId()){
				return road;
			}
		}
		return null;
	}
	private Gnome findGnome(String id) {
		// TODO Auto-generated method stub
		for (Gnome gnome :gnomes) {
			if (gnome.getID().equals(id)){
				return gnome;
			}
		}
		return null;
	}
	//Gnome Class, some attributes of gnome that you want to show up in your map
	private class Gnome implements Runnable
	{
		private coordinates location;
		private Color color;
		private ArrayList<Node> destination;
		private int type;
		private Village origin;
		private int standing_point;
		private int standing_point1;
		private String ID;
		private boolean found=false;
		private int speed_constant =1;
		private boolean isAtHome=true;
		private boolean isLazy=true;
		private int urgency;

		public Gnome(Color color,int type,Village origin,String id,coordinates location){

			this.color=color;
			this.type=type;
			this.location=location;
			this.origin=origin;

			this.standing_point=(int)(-10+20*Math.random());
			this.standing_point1=(int)(-10+20*Math.random());
			this.ID=id;

		}

		public void drawGnome(Graphics g) throws IOException
		{   Image Gnome0= ImageIO.read(this.getClass().getResource("gnome.png")).getScaledInstance(Imgsize, Imgsize, Image.SCALE_DEFAULT);
		Image Gnome1= ImageIO.read(this.getClass().getResource("gnome.png")).getScaledInstance(Imgsize, Imgsize, Image.SCALE_DEFAULT);
		Image Gnome2= ImageIO.read(this.getClass().getResource("gnome.png")).getScaledInstance(Imgsize, Imgsize, Image.SCALE_DEFAULT);
		g.setColor(color);

		if(isAtHome==true){
			this.standing_point=0;
			this.standing_point1=0;

			if (this.type==0)
				g.drawImage(Gnome0,(int)(this.origin.getPosition().getX())+this.standing_point,(int)(this.origin.getPosition().getY())+this.standing_point1, null);
			if (this.type==1)
				g.drawImage(Gnome1,(int)(this.origin.getPosition().getX())+this.standing_point,(int)(this.origin.getPosition().getX())+this.standing_point1, null);
			if (this.type==2)
				g.drawImage(Gnome2, (int)(this.origin.getPosition().getX())+this.standing_point, (int)(this.origin.getPosition().getY())+this.standing_point1, null);

			isAtHome=false;
		}

		else{
			if (this.type==0)
				g.drawImage(Gnome0,(int)(this.getLocation().getX()),(int)(this.getLocation().getY()), null);
			if (this.type==1)
				g.drawImage(Gnome1,(int)(this.getLocation().getX()),(int)(this.getLocation().getY()), null);
			if (this.type==2)
				g.drawImage(Gnome2, (int)(this.getLocation().getX()),(int)(this.getLocation().getY()), null);


		}
		Image Arrow=ImageIO.read(this.getClass().getResource("down.png"));
		if(found){
			g.drawImage(Arrow, (int) ((int)(this.origin.getPosition().getX())+this.standing_point+0.4*Imgsize),(int)(this.origin.getPosition().getY())+this.standing_point1-16, null);
		}
		else
			return;
		}
		public String getID(){
			return this.ID;
		}
		/**
		 * @param found the found to set
		 */
		public void setFound(boolean found) {
			this.found = found;
		}
		public coordinates getLocation (){
			return this.location;
		}
		public void setLocation(coordinates location){
			this.location=location;
		}
		public Village getOrigin(){
			return this.origin;
		}

		/**
		 * @return the speed_constant
		 */
		public int getSpeed_constant() {
			return speed_constant;
		}
		/**
		 * @param speed_constant the speed_constant to set
		 */
		public void setSpeed_constant(int speed_constant) {
			this.speed_constant = speed_constant;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// Gnome can auto-run by themselves
			Village depart= this.getOrigin();

			if(isLazy){
				this.setUrgency((int)(5*Math.random()));
				this.setSpeed_constant(this.getSpeed_constant()*this.getUrgency());// if the gnome is lazy, he will walk s..l..o..w..l..y..ZZ
				//while the next destination is not crowded and the roads are OK, they will go
				int i=1;
				while(i<=this.destination.size()-1){
					setPeople_on_road(true);
					double   cosValue= depart.getPosition().cos(destination.get(i).getData().getPosition(),this.getLocation());
					double   sinValue= depart.getPosition().sin(destination.get(i).getData().getPosition(),this.getLocation());
					findRoad(depart,destination.get(i).getData()).setCapacity(findRoad(depart,destination.get(i).getData()).getCapacity()-1);
					Road traffic= findRoad(depart,destination.get(i).getData());
					while(destination.get(i).getData().getCapacity()<=0&&traffic.isFull()){
						try {

							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						comment.setText(null);
						comment.setText("There is a traffic between village "+depart.getId()+" and "+ destination.get(i).getData().getId());
					}
					while((Math.abs(this.getLocation().length(this.getLocation(), destination.get(i).getData().getPosition()))>=40||this.getLocation().getX()<=0||this.getLocation().getY()<=0)){
						try {
							Thread.sleep(450);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						moveGnome(this,(int)(this.getLocation().getX()+this.speed_constant*cosValue),
								(int)(this.getLocation().getY()+this.speed_constant*sinValue));

						display.repaint();
					}
					traffic.setCapacity(traffic.getCapacity()+1);
					destination.get(i).getData().setCapacity(destination.get(i).getData().getCapacity()-1);
					this.setOrigin(destination.get(i).getData());

					i++;
				} 
			}
			else{int i=1;
			this.setUrgency((int)(10+10*Math.random()));
			this.setSpeed_constant(this.getSpeed_constant()*this.getUrgency());
			while(i<=this.destination.size()-1){
				setPeople_on_road(true);
				double   cosValue= depart.getPosition().cos(destination.get(i).getData().getPosition(),this.getLocation());
				double   sinValue= depart.getPosition().sin(destination.get(i).getData().getPosition(),this.getLocation());
				//findRoad(depart,destination.get(i).getData()).setCapacity(findRoad(depart,destination.get(i).getData()).getCapacity()-1);
				Road traffic= findRoad(depart,destination.get(i).getData());
				while(destination.get(i).getData().getCapacity()<=0&&traffic.isFull()){
					try {

						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					comment.setText(null);
					comment.setText("There is a traffic between village "+depart.getId()+" and "+ destination.get(i).getData().getId());
				}
				while((Math.abs(this.getLocation().length(this.getLocation(), destination.get(i).getData().getPosition()))>=40||this.getLocation().getX()<=0||this.getLocation().getY()<=0)){
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					moveGnome(this,(int)(this.getLocation().getX()+this.speed_constant*cosValue),
							(int)(this.getLocation().getY()+this.speed_constant*sinValue));

					display.repaint();
				}
				//traffic.setCapacity(traffic.getCapacity()+1);
				destination.get(i).getData().setCapacity(destination.get(i).getData().getCapacity()-1);
				this.setOrigin(destination.get(i).getData());

				i++;
			} 
			}
			setPeople_on_road(false);
		}

		public void setOrigin(Village origin){
			this.origin=origin;
		}
		/**
		 * @param isLazy the isLazy to set
		 */
		public void setLazy(boolean isLazy) {
			this.isLazy = isLazy;
		}

		/**
		 * @param destination the destination to set
		 */
		public void setDestination(ArrayList<Node> destination) {
			this.destination = destination;
		}

		/**
		 * @return the urgency
		 */
		public int getUrgency() {
			return urgency;
		}

		/**
		 * @param urgency the urgency to set
		 */
		public void setUrgency(int urgency) {
			this.urgency = urgency;
		}


	}
	private class Gelbin implements Runnable{
		public Gelbin(){

		}


		@Override
		public void run() {
			// TODO Auto-generated method stub		
			comment.setText(null);
			GelbinTalks=true;
			try{	if(GelbinTalks==true){
				String response=speech.readLine();
				if(response==null){
					comment.append("\tPlease, rebuild the city with me.\n\tPlease select the menu on the right side of the screen.");
					return;
				}		
				comment.append("\t");
				while(response!=null&&!response.equals(";"))
				{ if(response.equalsIgnoreCase(":"))
				{cont=false;
				while(cont==false)
				{Thread.sleep(2000);}
				response=speech.readLine();
				}
				String[] words= response.split(" ");

				for (int i=0;i<=words.length-1;i++)
				{  comment.append(words[i]+" ");
				Thread.sleep(80);
				comment.update(comment.getGraphics());
				}
				comment.append("\n\t");

				response=speech.readLine();
				}
			}}
			catch(IOException | InterruptedException n6){

			}

			GelbinTalks=false;
		}	
	}





	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==OK){
			new Thread(new Gelbin()).start();

		}
		if(e.getSource()==cancel){
			comment.setText(null);
			comment.append("\tPlease, rebuild the city with me.\n\tPlease select the menu on the right side of the screen.\n\tTo hear the story of this "
					+ "city, click OK.");
		}

	}
	
	
	
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MapPanel Mypanel = new MapPanel();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @return the drawVillage
	 */
	public boolean isDrawVillage() {
		return drawVillage;
	}

	/**
	 * @param drawVillage the drawVillage to set
	 */
	public void setDrawVillage(boolean drawVillage) {
		this.drawVillage = drawVillage;
	}

	/**
	 * @return the imgsize
	 */
	public int getImgsize() {
		return Imgsize;
	}

	/**
	 * @param imgsize the imgsize to set
	 */
	public void setImgsize(int imgsize) {
		Imgsize = imgsize;
	}

	/**
	 * @return the people_on_road
	 */
	public boolean isPeople_on_road() {
		return people_on_road;
	}

	/**
	 * @param people_on_road the people_on_road to set
	 */
	public void setPeople_on_road(boolean people_on_road) {
		this.people_on_road = people_on_road;
	}

	/**
	 * @return the move
	 */
	public boolean isMove() {
		return move;
	}

	/**
	 * @param move the move to set
	 */
	public void setMove(boolean move) {
		this.move = move;
	}


}

