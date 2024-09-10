//Adithi Vardhan 
//Othello 
//Thomas S. Wootton High School (2021-2022)

import java.awt.*;
import java.util.ArrayList;


import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class othello2 extends JFrame implements ActionListener{

	private PicPanel[][] allPanels;

	private JButton skipButton;

	public  final int[] VERTDISP = {0, -1,-1,-1,0,1,1,1};		//do not modify
	public  final int[] HORZDISP = {-1, -1,0, 1,1,1,0,-1};

	private BufferedImage whitePiece;
	private BufferedImage blackPiece;

	private JLabel blackCountLabel; 
	private int blackCount = 2;

	private JLabel whiteCountLabel;
	private int whiteCount = 2;

	private JLabel turnLabel;
	private boolean blackTurn = true;

	public othello2(){

		setSize(1200,950);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setTitle("Othello");
		getContentPane().setBackground(Color.white);

		allPanels = new PicPanel[8][8];

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(8,8,2,2));
		gridPanel.setBackground(Color.black);
		gridPanel.setBounds(95,50,800,814);
		gridPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		for(int row = 0; row < 8; row++){
			for(int col = 0; col < 8; col++){
				allPanels[row][col] = new PicPanel(row,col,76,100);
				gridPanel.add(allPanels[row][col]);
			}
		}

		try {
			whitePiece = ImageIO.read(new File("white.jpg"));
			blackPiece = ImageIO.read(new File("black.jpg"));

		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "Could not read in the pic");
			System.exit(0);
		}	

		skipButton = new JButton("Skip Turn");
		skipButton.addActionListener(this);
		skipButton.setBounds(925,475,150,50);

		blackCountLabel = new JLabel("Black: 2 ");
		blackCountLabel.setFont(new Font("Calibri",Font.PLAIN,35));
		blackCountLabel.setBounds(925,150,275,50);

		whiteCountLabel = new JLabel("White: 2 ");
		whiteCountLabel.setFont(new Font("Calibri",Font.PLAIN,35));
		whiteCountLabel.setBounds(925,225,275,50);

		turnLabel = new JLabel("Turn: Black ");
		turnLabel.setFont(new Font("Calibri",Font.PLAIN,35));
		turnLabel.setBounds(925,375,275,75);

		add(gridPanel);
		add(skipButton);
		add(blackCountLabel);
		add(whiteCountLabel);
		add(turnLabel);

		//YOUR CODE GOES HERE
		//insert four initial pieces
		allPanels[3][3].addPiece(Color.white);
		allPanels[3][4].addPiece(Color.black);
		allPanels[4][3].addPiece(Color.black);
		allPanels[4][4].addPiece(Color.white);

		setVisible(true);
	}



	private void updateLabels(){
		whiteCountLabel.setText("White: "+whiteCount);
		blackCountLabel.setText("Black: "+blackCount);

		String turn = "Black";

		if(!blackTurn)
			turn = "White";

		turnLabel.setText("Turn: "+turn);
	}

	//determines if the row/col loc is in bounds
	private boolean isValidCell(int row, int col){

		return row >=0 && row < allPanels.length && col >=0 && col < allPanels.length;
	}

	public void actionPerformed(ActionEvent ae){


	}

	private void swapPlayer() {
		blackTurn = !blackTurn; 
	}

	private Color determineColor() {
		if(blackTurn) {
			return Color.black; 
		}
		else {
			return Color.white; 
		}

	}



	class PicPanel extends JPanel implements MouseListener{

		private int row;
		private int col;

		private Color myColor;
		private ArrayList<Integer> neighbors; 		//neighboring cells that have pieces in them.
		//each element corresponds to index of neighborhood array.
		//used to reduce which neighbors need to be examined
		//when trying to add a new piece

		public PicPanel(int r, int c, int w, int h){
			setBackground(Color.white);
			row = r;
			col = c;
			neighbors = new ArrayList<Integer>();
			myColor = null;
			this.addMouseListener(this);
			setLayout(null);
		}		


		//this will draw the image
		public void paintComponent(Graphics g){
			super.paintComponent(g);

			if(myColor == null){
				setBackground(new Color(0,108,89));
			}
			else if(myColor == Color.white){
				g.drawImage(whitePiece,0,0,this);
			}
			else{
				g.drawImage(blackPiece,0,0,this);
			}

		}

		//call this to place a piece into an empty cell.
		//updates neighboring elements neighbors AL.
		//this should only be called once you have confirmed
		//that a piece is allowed to be placed in this panel.
		public void addPiece(Color pieceColor){

			myColor = pieceColor;

			//for all 8 possible neighbors
			for(int i = 0; i < HORZDISP.length; i++){
				int nextRow = row + VERTDISP[i];
				int nextCol = col + HORZDISP[i];

				//if a neighbor exists
				if(isValidCell(nextRow,nextCol)){

					//we are updating the neighbor's AL so we want the opposite
					//direction of where we currently are
					//displacement arrays have been rigged so that the opposite direction is 4
					//indicies away
					int neighbor = i+4;
					if(neighbor >= HORZDISP.length)
						neighbor = i - 4;

					allPanels[nextRow][nextCol].neighbors.add(neighbor);
				}

			}

			repaint();
		}

		//changes the color of an existing piece to the opposite
		public void flip() {
			if(myColor == null)
				return;
			if(myColor == Color.black)
				myColor = Color.white;
			else
				myColor = Color.black;

			repaint();
		}



		//add code here to react to the user clicking on the panel
		public void mouseClicked(MouseEvent arg0) {
			if(!checkEnemyColor()) {

				displayMessage("Must place next to enemy color.");
			}
			
			else if(!canFlip()) {
				displayMessage("Must flank");

			}

			else {

				for(int i = 0; i < neighbors.size(); i++) {
					
					System.out.print(beFlipped(i) + "\n"); 

					if(beFlipped(i)) {

						int oop = neighbors.get(i); 

						int newRow = row + VERTDISP[oop];

						int newCol = col + HORZDISP[oop]; 

						while(allPanels[newRow][newCol].myColor == oppColor()) {

							allPanels[newRow][newCol].flip(); 

							newRow += VERTDISP[oop]; 
							newCol += HORZDISP[oop];

						}

						allPanels[row][col].addPiece(determineColor());
						swapPlayer(); 
						updateLabels();

					}


				}
			}
		}

		private boolean canFlip() {

			for(int i = 0; i < neighbors.size(); i++) {

				if(beFlipped(i)) {
					return true;
				}
			}

			return false; 
		}



		private boolean beFlipped(int i) {


			if(allPanels[row+ VERTDISP[neighbors.get(i)]][col +HORZDISP[neighbors.get(i)]].myColor == oppColor()) {

				int index = neighbors.get(i); 

				int newR = row + VERTDISP[index]; 

				int newC = col+ HORZDISP[index];; 

				while(allPanels[newR][newC].myColor == oppColor()) {

					newR += VERTDISP[index]; 
					newC += HORZDISP[index];
				}
				
				if(allPanels[newR][newC].myColor == determineColor()) {
					return true; 
				}


			}
			
			return false; 

		}

		private boolean checkEnemyColor() {

			for(int i = 0; i < neighbors.size(); i++) {
				
				PicPanel toCheck = allPanels[row+ VERTDISP[neighbors.get(i)]][col +HORZDISP[neighbors.get(i)]]; 

				if(toCheck.isValid() && toCheck.myColor == oppColor()) {

					return true;
				}
			}

			return false;
		}
		

		private Color oppColor() {

			if(blackTurn) {
				return Color.white;
			}

			else {
				return Color.black;

			}
		}


		private void displayMessage(String message){
			JOptionPane.showMessageDialog(null, message);
		}


		@Override
		public void mouseEntered(MouseEvent arg0) {
			// do not implement

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// do not implement

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// do not implement

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// do not implement

		}
	}



	public static void main(String[] args){
		new othello2();
	}
}






