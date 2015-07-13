package epicapple.rush.game;

import epicapple.rush.GameState;

public class StateManager {

	private GameState state;

	public StateManager(GameState state){
		this.state = state;
	}

	public void setState(GameState state){
		this.state = state;
	}

	public GameState getState(){
		return state;
	}
}
