package de.thb.chocolate;

import java.util.Random;
import java.util.Scanner;

/**
 * YuckyChocolate - Contains the whole Yucky Chocolate game logic
 * @see https://en.wikipedia.org/wiki/Chomp
 * 
 * @author Maximilian Mewes
 * @since 2020-06-018
 * @version 1.2
 * 
 */
public class YuckyChocolate {
	
	// simple game configuration
	// could be moved into a separate config class or something but for this game it should do the job
	private int maxWidth = 10;
	private int maxHeight = 10;
	
	private char soapPiece = '*';
	private char chocolatePiece = 'o';
	
	private boolean singleplayer = false;
	private char[][] chocolateBar;
    
	
	
	public static void main(String[] args) {
		YuckyChocolate game = new YuckyChocolate();
		game.start();
	}
	
	
	/*
	 * Constructors
	 */
	public YuckyChocolate() {
		this.init();
	}
	
	public YuckyChocolate(char[][] chocolateBar, boolean singleplayer) {
		this.setChocolateBar(chocolateBar);
		this.setSingleplayer(singleplayer);
	}

	public YuckyChocolate(int width, int height, boolean singleplayer) {
		this.setChocolateBar(this.generateChocolateBar(width, height));
		this.setSingleplayer(singleplayer);
	}
	
	
	
	/*
	 * General utility methods
	 */

	/**
	 * This method is used to generate the chocolate 
	 * bar if there was no bar given.
	 * 
	 * @return void
	 */
    protected void init() {
    	Scanner sc = new Scanner(System.in);
    	
    	// size[0] -> width
    	// size[1] -> height
    	int[] size = this.getChocolateBarSize();
    	this.chocolateBar = this.generateChocolateBar(size[0], size[1]);
    	
    	System.out.print("Anzahl der menschlichen Spieler (1 oder 2): ");
    	if(sc.nextInt() == 1)
    		this.singleplayer = true;
    }
	
    /**
     * Actually starts the game
     * 
     * @return void
     */
    public void start() {
    	// print initial chocolate bar
    	System.out.println("\nai aktiv: " + this.singleplayer);
    	System.out.println("Initiale Tafel:");
    	this.printChocolateBar();
    	
    	this.gameLoop();
    }
    
    /**
     * main game loop
     */
    private void gameLoop() {
    	/* 0 = Player 1, 
    	 * 1 = Player 2 or Ai
    	 */
    	// Part of Ambrosius's win strategy
    	// if bar is square then opponent will start
    	int currentPlayer = (this.chocolateBar.length==this.chocolateBar[0].length&&singleplayer)||!singleplayer?0:1;
    	boolean vertical = false;
    	String[] players = {"Player 1", "Player 2", "Computer"}; 
    	Scanner sc = new Scanner(System.in);
    	
    	
    	while(!(this.chocolateBar.length <= 1 && this.chocolateBar[0].length <= 1)) {
    		int size = 0;
    		
    		if(!(singleplayer && currentPlayer == 1)) {
    			// "Human" move
	    		System.out.print(players[currentPlayer] +": Horizontal oder Vertikal abbrechen (H oder V)? -> ");
	    		int in = sc.next(".").charAt(0);
	    		
	    		if(in == 'v' || in == 'V') {
	    			vertical = true;
	    		} else if(in == 'h' || in == 'H') {
	    			vertical = false;
	    		} else {
	    			// TODO error handling
	    		}
	    		
	    		System.out.print(players[currentPlayer] +": Wie groß ist das abgebrochende Stück? -> ");
	    		size = sc.nextInt();
    		} else {
    			// "Computer" move
    			int m = this.chocolateBar.length;	// width
    			int n = this.chocolateBar[0].length;// height
    			
    			if(m==1 && n>1) {
    				size = n-1;
    				vertical = false;
    				
    			} else if(m>1 && n==1) {
    				size = m-1;
    				vertical = true;
    			
    			} else if(m>1 && n>1) {
    				if(m>n) {
    					size = m-n;
    					vertical = true;
    				} else {
    					size = n-m;
    					vertical = false;
    				}
    			}
    			System.out.println("Computers Turn");
    		}
    		
	    	this.removePiece(size, vertical);
	    	this.printChocolateBar();
	    	// debugging: System.out.println(this.chocolateBar.length +"_"+this.chocolateBar[0].length);

    		
    		
    		currentPlayer = currentPlayer==0?1:0;
    	}
    	
    	System.out.println("Game has ended.");
    	if(singleplayer && currentPlayer == 0) {
    		System.out.println(players[2] +" hat Gewonnen!");
    	} else {
    		System.out.println(players[0] +" hat Gewonnen!");
    	}
    	
    	if(!singleplayer)
    		System.out.println(players[currentPlayer==0?0:1] +" hat Gewonnen!");   	
    }
    
    
    /**
     * This method lets the Player enter width and 
     * height of the chocolate bar.
     * 
     * @return int[] of width and height for the chocolate bar 
     */
    protected int[] getChocolateBarSize() {
    	int width = 1;
    	int height = 1;
		Scanner sc = new Scanner(System.in);
        
        
        // get the width of the chocolate bar
        System.out.print("Breite der Tafel (1-"+ this.maxWidth +"): ");
        width = sc.nextInt();
        // check if user input is in the acceptable range
        if(width > this.maxWidth || width < 1) {
        	System.out.println("Ungültige Breite eingegeben!\n\n");
        	getChocolateBarSize();
        }
        
        // get the height of the chocolate bar
        System.out.print("Höhe der Tafel (1-"+ this.maxHeight +"): ");
        height = sc.nextInt();
        // check if user input is in the acceptable range
        if(height > this.maxHeight || height < 1) {
        	System.out.println("Ungültige Höhe eingegeben!\n\n");
        	getChocolateBarSize();
        }
        
        // create array of width and height
        return new int[] {width, height};
    }
    
    
    /**
     * generate the playing board aka. the chocolate 
     * bar with given width and height.
     * generate a random number if width and or height is less than 1
     *  
     * @param width of the chocolate bar
     * @param height of the chocolate bar
     * @return char[][] chocolateBar / playing board
     */
    protected char[][] generateChocolateBar(int width, int height) {
    	
    	char[][] board = new char[width][height];
    	Random rand = new Random();
    	
    	
    	// generate a random number if width and or height is less than 1
    	if(width < 1)
    		width = rand.nextInt(this.maxWidth)+1;
    	if(height < 1)
    		height = rand.nextInt(this.maxHeight)+1;
    	
    	// fill the 2-dim array with the character 'o'
        for (int i=0; i < width; i++) {
            for (int j=0; j < height; j++) {
                board[i][j] = this.chocolatePiece;
            }
        }
        // the soap is at the top right of the bar
        board[0][0] = this.soapPiece;
        
        return board; 
    }
    
    
    /**
	 * Print the chocolate bar to the Player(s)
     * 
     * @return void
     */
    protected void printChocolateBar() {
        for (int i=0; i < this.chocolateBar[0].length; i++) {
            for (int j=0; j < this.chocolateBar.length; j++) {
                System.out.print(this.chocolateBar[j][i] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    
    /**
     * 
     * @param size of piece that the player wants to remove
     * @param vertical 1 = vertical piece(s), 0 = horizontal piece(s)
     * @return void
     */
    protected void removePiece(int size, boolean vertical) {
    	if(size < 1)
    		// not really necessary TODO handle error    	
    		return;
    	
    	int width = this.chocolateBar.length;
    	int height = this.chocolateBar[0].length;
    	
    	// TODO could be done more efficient by not generating a whole new chocolate bar
    	// calculate the new size
    	if(vertical == true) {
    		width -= size;
    	} else {
    		height -= size;
    	}
    	
    	if(width < 1)
    		width = 1;
    	if(height < 1)
    		height = 1;
    	
    	this.chocolateBar = this.generateChocolateBar(width, height);
    }
    
    
    /**
     * Calculates size of Decision 
     * tree recursively
     * 
     * @param n
     * @return int
     */
    public int calculateTreeSize(int m, int n) {
		if(n==1 && m==1)
			return 1;
		
		int erg=0;
		for(int i=1; i<=(m-1); i++)
			erg += calculateTreeSize(m-i,n);
		for(int i=1; i<=(n-1); i++)
			erg += calculateTreeSize(m,n-i);
		
		// +1 = root
		return erg+1;
    }

	
    
    
    /*
     * Getter and Setter
     */
	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}
	public char getSoapPiece() {
		return soapPiece;
	}

	public void setSoapPiece(char soapPiece) {
		this.soapPiece = soapPiece;
	}

	public char getChocolatePiece() {
		return chocolatePiece;
	}

	public void setChocolatePiece(char chocolatePiece) {
		this.chocolatePiece = chocolatePiece;
	}
	
	public boolean isSingleplayer() {
		return singleplayer;
	}

	public void setSingleplayer(boolean singleplayer) {
		this.singleplayer = singleplayer;
	}
	
	public char[][] getChocolateBar() {
		return chocolateBar;
	}
	
	public void setChocolateBar(char[][] chocolateBar) {
		// checks if chocolate bar is valid
		if(chocolateBar.length >= this.maxWidth || chocolateBar[0].length >= this.maxHeight) {
			this.chocolateBar = null;
			return;
		}
		
		// Count Soap pieces and checking if chocolate pieces are right
		int soapCounter = 0;
        for (int i=0; i < chocolateBar[0].length; i++) {
            for (int j=0; j < chocolateBar.length; j++) {
            	char currentPiece = chocolateBar[j][i];
            	
            	// check if pieces are correctly set
            	if(currentPiece != this.chocolatePiece && currentPiece != this.soapPiece) {
            		// not really necessary TODO display error message
            		this.chocolateBar = null;
            		return;
            	}
            	
            	// count Soap pieces
                if(currentPiece == this.soapPiece)
                	soapCounter++;
                
                if(soapCounter > 1) {
                	// not really necessary TODO display error message
                	this.chocolateBar = null;
                	return;
                }
            }
        }
            
		this.chocolateBar = chocolateBar;
	}
}
