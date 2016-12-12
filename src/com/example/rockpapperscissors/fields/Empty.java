package com.example.rockpapperscissors.fields;

import org.andengine.entity.scene.Scene;

import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.Managers.ResourceManager;

public class Empty extends FieldItem {

	public Empty(int pIndexX, int pIndexY, Scene pScene) {
		super (pIndexX, pIndexY, CONSTANTS.EMPTY,ResourceManager.fieldTextureRegion,pScene);
	}
	
	public Empty(Empty source){
		super(source);
	}
}
