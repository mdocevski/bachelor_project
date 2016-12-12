package com.example.rockpapperscissors.fields;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Available extends Sprite {
	
	public int mPositionX, mPositionY;
	private int distanceFromSelectedFigure;
	private boolean available = false;

	public Available(float pX, float pY, int pPositionX, int pPositionY,
			ITextureRegion pTextureRegion,
			VertexBufferObjectManager pSpriteVertexBufferObject) {
		super(pX, pY, pTextureRegion, pSpriteVertexBufferObject);
		mPositionX = pPositionX;
		mPositionY = pPositionY;
		distanceFromSelectedFigure = 999;
		available = false;
	}

	public int getDistanceFromSelectedFigure() {
		return distanceFromSelectedFigure;
	}

	public void setDistanceFromSelectedFigure(int distanceFromSelectedFigure) {
		this.distanceFromSelectedFigure = distanceFromSelectedFigure;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public void resetAvailable (){
		available = false;
	}
	
	public void resetDistanceFromSelectedFigure(){
		distanceFromSelectedFigure = 999;
	}
	
}
