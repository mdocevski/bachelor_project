package com.example.rockpapperscissors.fields;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.example.rockpapperscissors.Managers.ResourceManager;

public class Pickup extends FieldItem {

	int mQuantity;

	public int getQuantity() {
		return mQuantity;
	}

	public void setQuantity(int pQuantity) {
		this.mQuantity = pQuantity;
	}

	private Text mText;

	public Text getmText() {
		return mText;
	}

	public void setmText(Text mText) {
		this.mText = mText;
	}

	public Pickup(int pIndexX, int pIndexY, int pType, int pQuantity, ITextureRegion pTextureRegion,
			Scene pScene) {
		super(pIndexX, pIndexY, pType, pTextureRegion, pScene);
		mQuantity = pQuantity;
	}

	
	public Pickup (Pickup source){
		super(source);
		mQuantity = source.mQuantity;
		mText = source.mText;
	}
}
