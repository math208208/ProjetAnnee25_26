package jeu;


public enum Direction implements Commande {
    NORD("N", "N (aller au nord)"),
    
    SUD("S", "S (aller au sud)"),
    
    EST("E", "E (aller à l'est)"),
    
    OUEST("O", "O (aller à l'ouest)");
    
   
	
	private final String abreviation;
	
    private final String description;

   
    private Direction(String a, String d) {
        this.abreviation = a;
        this.description = d;
    }

    @Override
    public String getAbreviation() {
        return abreviation;
    }

    
    @Override
    public String getDescription() {
        return description;
    }
}
