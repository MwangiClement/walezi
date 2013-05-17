/*
Copyright (c) 2009-2011, Andrew M. Martin
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
   disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of Pandam nor the names of its contributors may be used to endorse or promote products derived from this
   software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/
package org.pandcorps.platform;

import org.pandcorps.core.*;
import org.pandcorps.game.core.ImtilX;
import org.pandcorps.pandam.*;
import org.pandcorps.pandam.impl.FinPanple;
import org.pandcorps.pandax.tile.*;
import org.pandcorps.pandax.tile.Tile.TileMapImage;

public class Map {
	private final static byte TILE_HORIZ = 2;
	private final static byte TILE_VERT = 3;
	private final static byte TILE_LEFTUP = 4;
	private final static byte TILE_RIGHTUP = 5;
	private final static byte TILE_LEFTDOWN = 6;
	private final static byte TILE_RIGHTDOWN = 7;
	private final static byte TILE_MARKER = 8;
	private static Panroom room = null;
	private static Panmage timg = null;
	private static TileMap tm = null;
	private static TileMapImage[][] imgMap = null;
	
	protected final static class MapScreen extends Panscreen {
		@Override
        protected final void load() throws Exception {
			loadMap();
			PlatformGame.fadeIn(room);
		}
		
		@Override
	    protected final void destroy() {
	        Panmage.destroy(timg);
	    }
	}
	
	protected final static class Marker extends Panctor {
		{
			setView(PlatformGame.marker);
		}
	}
	
	protected final static class Player extends TileWalker {
		private boolean disabled = false;
		
		{
			setView(PlatformGame.guyMap);
			setSpeed(2);
		}
		
		@Override
		protected void onStill() {
			if (disabled) {
				return;
			}
			final Panteraction interaction = Pangine.getEngine().getInteraction();
			// Similar to Guy4Controller
	        if (interaction.KEY_DOWN.isActive()) {
	            walk(Direction.South);
	        } else if (interaction.KEY_UP.isActive()) {
	        	walk(Direction.North);
	        } else if (interaction.KEY_LEFT.isActive()) {
	        	walk(Direction.West);
	        } else if (interaction.KEY_RIGHT.isActive()) {
	        	walk(Direction.East);
	        } else if (interaction.KEY_SPACE.isActive()) {
	        	/*if (room.getBlendColor().getA() > Pancolor.MIN_VALUE) {
	        		return;
	        	}*/
	        	disabled = true;
	        	PlatformGame.fadeOut(room, new PlatformGame.PlatformScreen());
			}
		}
		
		@Override
		protected void onWalked() {
			final byte b = getTile().getBehavior();
			switch (b) {
				case TILE_MARKER :
					return;
				case TILE_VERT : {
					final Direction d1 = getDirection();
					final byte b2 = getDestination(d1).getBehavior();
					if (b2 == TILE_LEFTUP || b2 == TILE_LEFTDOWN) {
						walk(Direction.West, d1);
					} else if (b2 == TILE_RIGHTUP || b2 == TILE_RIGHTDOWN) {
						walk(Direction.East, d1);
					} else {
						walk(d1);
					}
					return;
				}
				case TILE_HORIZ : {
					final Direction d1 = getDirection();
					final byte b2 = getDestination(d1).getBehavior();
					if (b2 == TILE_LEFTUP || b2 == TILE_RIGHTUP) {
						walk(Direction.North, d1);
					} else if (b2 == TILE_LEFTDOWN || b2 == TILE_RIGHTDOWN) {
						walk(Direction.South, d1);
					} else {
						walk(d1);
					}
					return;
				}
			}
	    }
	}
	
	private final static void loadMap() {
		final Pangine engine = Pangine.getEngine();
		PlatformGame.room.destroy();
		room = engine.createRoom(Pantil.vmid(), new FinPanple(256, 192, 0));
		PlatformGame.room = room;
		Pangame.getGame().setCurrentRoom(room);
		tm = new DynamicTileMap("act.tilemap", room, ImtilX.DIM, ImtilX.DIM);
		room.addActor(tm);
		timg = PlatformGame.createImage("Map", "org/pandcorps/platform/res/bg/Map.png", 128);
		tm.setImageMap(timg);
		imgMap = tm.splitImageMap();
		tm.fillBackground(imgMap[0][6], true);
		for (int i = 2; i < 14; i++) {
			for (int j = 1; j < 4; j++) {
				tm.initTile(i, j).setBackground(imgMap[0][4]);
			}
			tm.initTile(i, 1).setForeground(imgMap[1][4]);
			tm.initTile(i, 3).setForeground(imgMap[2][1]);
			for (int j = 4; j < 10; j++) {
				tm.initTile(i, j).setBackground(Mathtil.rand(75) ? imgMap[1][1] : imgMap[4][Mathtil.randi(0, 3)]);
			}
			tm.initTile(i, 10).setForeground(imgMap[0][1]);
		}
		for (int j = 1; j < 4; j++) {
			tm.initTile(1, j).setBackground(imgMap[0][3]);
			tm.initTile(14, j).setBackground(imgMap[0][5]);
		}
		tm.initTile(1, 1).setForeground(imgMap[1][3]);
		tm.initTile(14, 1).setForeground(imgMap[1][5]);
		for (int j = 4; j < 10; j++) {
			tm.initTile(1, j).setForeground(imgMap[1][0]);
			tm.initTile(14, j).setForeground(imgMap[1][2]);
		}
		tm.initTile(1, 3).setForeground(imgMap[2][0]);
		tm.initTile(14, 3).setForeground(imgMap[2][2]);
		tm.initTile(1, 10).setForeground(imgMap[0][0]);
		tm.initTile(14, 10).setForeground(imgMap[0][2]);
		
		marker(2, 6);
		tm.initTile(2, 7).setBackground(imgMap[3][2], TILE_VERT);
		tm.initTile(2, 8).setBackground(imgMap[3][6], TILE_RIGHTDOWN);
		tm.initTile(3, 8).setBackground(imgMap[3][1], TILE_HORIZ);
		marker(4, 8);
		for (int i = 3; i < 6; i++) {
			tm.initTile(i, 6).setBackground(imgMap[3][1], TILE_HORIZ);
		}
		marker(6, 6);
		
		final Player player = new Player();
		player.setPosition(tm.getTile(2, 6));
		player.getPosition().setZ(tm.getForegroundDepth() + 1);
		room.addActor(player);
	}
	
	private static void marker(final int i, final int j) {
		final Tile tile = tm.initTile(i, j);
		tile.setBackground(imgMap[3][0], TILE_MARKER);
		final Marker m = new Marker();
		//m.setPosition(tile);
		m.getPosition().set(tile.getPosition());
		room.addActor(m);
	}
}
