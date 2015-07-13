package epicapple.rush;

public enum WrapperGameMode {

	SURVIVAL(0),
	CREATIVE(1),
	ADVENTURE(2),
	SPECTATOR(3);

	private int modeNumber;

	WrapperGameMode(int modeNumber){
		this.modeNumber = modeNumber;
	}

	public int getId(){
		return modeNumber;
	}
}
