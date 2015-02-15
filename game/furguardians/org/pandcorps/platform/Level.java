/*
Copyright (c) 2009-2014, Andrew M. Martin
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

import java.util.*;

import org.pandcorps.core.*;
import org.pandcorps.core.img.*;
import org.pandcorps.game.core.*;
import org.pandcorps.pandam.*;
import org.pandcorps.pandax.*;
import org.pandcorps.pandax.tile.*;
import org.pandcorps.pandax.tile.Tile.*;
import org.pandcorps.platform.Enemy.*;
import org.pandcorps.platform.Player.*;
import org.pandcorps.platform.Spawner.*;

public class Level {
    protected final static int ROOM_H = 256;
    
    protected final static int HOB_TROLL = 0;
    protected final static int HOB_OGRE = 1;
    protected final static int TROLL = 2;
    protected final static int OGRE = 3;
    protected final static int TROLL_COLOSSUS = 4;
    protected final static int OGRE_BEHEMOTH = 5;
    protected final static int IMP = 6;
    protected final static int ARMORED_IMP = 7;
    protected final static int SPIKED_IMP = 8;
    protected final static int DROWID = 9;
    protected final static int DROLOCK = 10;
    protected final static int ICE_WISP = 11;
    protected final static int FIRE_WISP = 12;
    protected final static int BLACK_BLOB = 13;
    
    private final static byte FLOOR_GRASSY = 0;
    private final static byte FLOOR_BLOCK = 1;
    private final static byte FLOOR_BRIDGE = 2;
    
    protected final static PixelFilter terrainDarkener = new BrightnessPixelFilter((short) -40, (short) -24, (short) -32);
    
    private final static Set<Class<? extends Template>> oneUseTemplates = new HashSet<Class<? extends Template>>();
    
    protected static TileMapImage[] flashBlock = null;
    private static TileMapImage[] extraAnimBlock = null;
    
    protected static long seed = -1;
    protected static Panroom room = null;
    protected static Theme theme = null;
    protected static BackgroundBuilder backgroundBuilder = null;
    protected static Panmage timg = null;
    protected static Panmage bgimg = null;
    protected static TileMap tm = null;
    protected static TileMap bgtm1 = null;
    protected static TileMap bgtm2 = null;
    protected static TileMap bgtm3 = null;
    protected static TileMapImage[][] imgMap = null;
    protected static TileMapImage[][] bgMap = null;
    private static int w = 0;
    private static int ng = 0;
    private static int nt = 0;
    private static int floor = 0;
    protected static int goalIndex = 0;
    private static byte floorMode = FLOOR_GRASSY;
    private static Pancolor topSkyColor = null;
    private static Pancolor bottomSkyColor = null;
    protected static Tile tileGem = null;
    protected static int numEnemies = 0;
    protected static int currLetter = 0;
    protected static List<Panctor> collectedLetters = null;
    protected static List<Panctor> uncollectedLetters = null;
    protected static boolean victory = false;
    
    static {
    	oneUseTemplates.add(GemMsgTemplate.class);
    	oneUseTemplates.add(GiantTemplate.class);
    }
    
    protected abstract static class Theme {
    	private final static String[] MSG = {"PLAYER", "GEMS!!!", "HURRAY", "GO GO!", "YAY", "GREAT", "PERFECT"};
    	private final static int[] NORMAL_ENEMIES = {HOB_TROLL, HOB_OGRE, TROLL, OGRE, IMP, ARMORED_IMP};
    	private final static int[] getNormalEnemies(final int... extra) {
    		final int nlen = NORMAL_ENEMIES.length, elen = extra.length;
    		final int[] a = new int[nlen + elen];
    		System.arraycopy(NORMAL_ENEMIES, 0, a, 0, nlen);
    		System.arraycopy(extra, 0, a, nlen, elen);
    		return a;
    	}
    	public final static Theme Normal = new Theme(null, MSG) {
    	    @Override protected final int[] getEnemyIndices(final int worlds, final int levels) {
    	        switch (worlds) {
    	            case 0 :
    	            	if (levels == 0) {
    	            		return new int[] {HOB_TROLL};
    	            	} else if (levels == 1) {
    	            		return new int[] {IMP};
    	            	} else if (levels == 2) {
    	            		return new int[] {HOB_TROLL, HOB_OGRE};
    	            	}
    	            case 1 : return new int[] {HOB_TROLL, HOB_OGRE, IMP}; // 2nd world is Snow
    	            case 2 : // After 2nd world
    	            case 3 : return new int[] {HOB_TROLL, HOB_OGRE, IMP, ARMORED_IMP}; // 4th world is Sand
    	            default: return NORMAL_ENEMIES; // After 4rd
    	            // troll colossus and ogre behemoth after 5th (see addGiantTemplate)
    	        }
    	    }
    	    
    		@Override protected final BackgroundBuilder getRandomBackground() {
    			return Mathtil.rand(new HillBackgroundBuilder(), new ForestBackgroundBuilder(), new TownBackgroundBuilder());
    		}
    		
    		@Override protected final Builder getRandomBuilder() {
    			if (backgroundBuilder instanceof HillBackgroundBuilder) {
    				return Mathtil.rand() ? new GrassyBuilder() : new PlatformBuilder();
    			} else if (backgroundBuilder instanceof TownBackgroundBuilder) {
    				return new FlatBuilder();
    			} else {
    				return new GrassyBuilder();
    			}
    		}
    	};
    	public final static Theme Snow = new Theme("Snow", null, MSG, PlatformGame.TILE_ICE) {
    	    @Override protected final int[] getEnemyIndices(final int worlds, final int levels) {
    	        return worlds < 2 ? new int[] {HOB_TROLL, HOB_OGRE, IMP, ICE_WISP} : getNormalEnemies(ICE_WISP);
    	    }
    	    
    		@Override protected final BackgroundBuilder getRandomBackground() {
    			return Mathtil.rand() ? new HillBackgroundBuilder() : new MountainBackgroundBuilder();
    		}
    		
    		@Override protected final Builder getRandomBuilder() {
    			return new GrassyBuilder();
    		}
    		
    		@Override protected final TileMapImage[] getExtraAnimBlock() {
    			final TileMapImage[] row = imgMap[1];
    	        return new TileMapImage[] {row[5], row[6], row[7]};
        	}
    		
    		@Override protected final void flash(final long i) {
    			if (i < 3 && extraAnimBlock != null) {
    				Tile.animate(extraAnimBlock);
    			}
        	}
    	};
    	public final static Theme Sand = new Theme("Sand", null, MSG, PlatformGame.TILE_SAND) {
    	    @Override protected final int[] getEnemyIndices(final int worlds, final int levels) {
    	    	return worlds < 4 ? new int[] {HOB_TROLL, HOB_OGRE, IMP, ARMORED_IMP, FIRE_WISP} : getNormalEnemies(FIRE_WISP);
    	    }
    	    
    		@Override protected final BackgroundBuilder getRandomBackground() {
    			return new HillBackgroundBuilder();
    		}
    		
    		@Override protected final Builder getRandomBuilder() {
    			return new GrassyBuilder();
    		}
    		
    		@Override protected final TileMapImage[] getExtraAnimBlock() {
    			final TileMapImage[] row = imgMap[1];
    	        return new TileMapImage[] {row[5], row[6], row[7]};
        	}
    		
    		@Override protected final void step(final long clock) {
    			if (clock % 6 == 0 && extraAnimBlock != null) {
    				Tile.animate(extraAnimBlock);
    			}
        	}
    	};
    	public final static Theme Bridge = new Theme("Bridge", MSG) {
            @Override protected final int[] getEnemyIndices(final int worlds, final int levels) {
                return Map.theme.levelTheme.getEnemyIndices(worlds, levels);
            }
            
            @Override protected final BackgroundBuilder getRandomBackground() {
                return new HillBackgroundBuilder();
            }
            
            @Override protected final Builder getRandomBuilder() {
                return new BridgeBuilder();
            }
            
            @Override protected final String getBgImg() {
                return Map.theme.levelTheme.bgImg;
            }
        };
        public final static Theme Night = new Theme("Night", MSG) {
            @Override protected final int[] getEnemyIndices(final int worlds, final int levels) {
            	if (worlds < 3) {
	                if (Map.theme == Map.MapTheme.Snow) {
	                	return new int[] {BLACK_BLOB, ICE_WISP};
	                } else if (Map.theme == Map.MapTheme.Sand) {
	                	return new int[] {BLACK_BLOB, FIRE_WISP};
	                }
	            	return new int[] {BLACK_BLOB};
            	} else {
            		if (Map.theme == Map.MapTheme.Snow) {
	                	return new int[] {BLACK_BLOB, ICE_WISP, ARMORED_IMP};
	                } else if (Map.theme == Map.MapTheme.Sand) {
	                	return new int[] {BLACK_BLOB, FIRE_WISP, ARMORED_IMP};
	                }
	            	return new int[] {BLACK_BLOB, ARMORED_IMP};
            	}
            }
            
            @Override protected final BackgroundBuilder getRandomBackground() {
                return new HillBackgroundBuilder();
            }
            
            @Override protected final Builder getRandomBuilder() {
                return new GrassyBuilder();
            }
            
            @Override protected final String getBgImg() {
                return Map.theme.levelTheme.bgImg;
            }
            
            @Override protected final PixelFilter getSkyFilter() {
                final ReplacePixelFilter f = new ReplacePixelFilter();
                for (int i = 0; i < 5; i++) {
                    final short grey = (short) (48 - (i * 8));
                    f.put((short) (64 + (i * 16)), (short) (64 + (i * 32)), Pancolor.MAX_VALUE, grey, grey, grey);
                }
                return f;
            }
        };
    	private final static String[] MSG_CHAOS = {"CHAOS", "HAVOC", "BEWARE", "FEAR", "DANGER"};
    	public final static Theme Chaos = new Theme("Chaos", MSG_CHAOS) {
    	    @Override protected final int[] getEnemyIndices(final int worlds, final int levels) {
                switch (worlds) {
                    case 0 :
                    case 1 : return new int[] {DROWID, DROLOCK, IMP};
                    case 2 : return new int[] {DROWID, DROLOCK, IMP, ARMORED_IMP};
                    default: return new int[] {DROWID, DROLOCK, IMP, ARMORED_IMP, SPIKED_IMP};
                }
            }
    	    
    		@Override protected final BackgroundBuilder getRandomBackground() {
    			return new HillBackgroundBuilder();
    		}
    		
    		@Override protected final Builder getRandomBuilder() {
    			return new GrassyBuilder();
    		}
    		
    		@Override protected Pansound getMusic() {
        		return PlatformGame.musicHeartbeat;
        	}
    	};
    	
    	protected final String img;
    	protected final String bgImg;
    	protected final String[] gemMessages;
    	protected final byte specialGroundBehavior;
    	
    	private Theme(final String img, final String[] gemMessages) {
    		this(img, img, gemMessages, Tile.BEHAVIOR_SOLID);
    	}
    	
    	private Theme(final String img, final String bgImg, final String[] gemMessages, final byte specialGroundBehavior) {
    		this.img = img;
    		this.bgImg = bgImg;
    		this.gemMessages = gemMessages;
    		this.specialGroundBehavior = specialGroundBehavior;
    	}
    	
    	private final List<EnemyDefinition> getEnemies() {
    	    final int[] enemies = getEnemyIndices(getDefeatedWorlds(), getDefeatedLevels());
    		final List<EnemyDefinition> list = new ArrayList<EnemyDefinition>(enemies.length);
    		for (final int enemy : enemies) {
    		    list.add(PlatformGame.allEnemies.get(enemy));
    		}
    		return list;
    	}
    	
    	protected abstract int[] getEnemyIndices(final int worlds, final int levels);
    	
    	protected abstract BackgroundBuilder getRandomBackground();
    	
    	protected abstract Builder getRandomBuilder();
    	
    	protected String getBgImg() {
    	    return bgImg;
    	}
    	
    	protected PixelFilter getSkyFilter() {
    	    return Map.theme.getSkyFilter();
    	}
    	
    	protected TileMapImage[] getExtraAnimBlock() {
    		return null;
    	}
    	
    	protected Pansound getMusic() {
    		return PlatformGame.musicHappy;
    	}
    	
    	protected void step(final long clock) {
    	}
    	
    	protected void flash(final long i) {
    	}
    }
    
    protected static void setTheme(final Theme theme) {
    	Level.theme = theme;
    	PlatformGame.enemies = theme.getEnemies();
    }
    
    protected static void initTheme() {
    	setTheme(Map.theme.levelTheme);
    }
    
    protected static boolean isNormalTheme() {
    	return theme != Theme.Chaos;
    }
    
    private final static Profile getProfile() {
        final PlayerContext pc = Coltil.get(PlatformGame.pcs, 0);
        return (pc == null) ? null : pc.profile;
    }
    
    private final static int getDefeatedWorlds() {
        final Profile prf = getProfile();
        return (prf == null) ? 0 : prf.stats.defeatedWorlds;
    }
    
    private final static int getDefeatedLevels() {
        final Profile prf = getProfile();
        return (prf == null) ? 0 : prf.stats.defeatedLevels;
    }
    
    protected final static boolean isFlash(final Tile tile) {
        final Object bg = DynamicTileMap.getRawForeground(tile);
        for (int i = 0; i < 4; i++) {
            if (flashBlock[i] == bg) {
                return true;
            }
        }
        return false;
    }
    
    protected final static void applyDirtTexture(final Img tileImg, final int ix, final int iy, final int fx, final int fy) {
        final Img dirt = PlatformGame.dirts[Map.bgTexture];
        final PixelMask tileMask = new AntiPixelMask(new ColorPixelMask(224, 112, 0, Pancolor.MAX_VALUE));
        for (int x = ix; x < fx; x += 16) {
            for (int y = iy; y < fy; y += 16) {
                Imtil.copy(dirt, tileImg, 0, 0, 16, 16, x, y, null, tileMask);
            }
        }
        if (Map.theme.dirtFilter != null) {
        	Imtil.filterImg(tileImg, ix, iy, fx - ix, fy - iy, Map.theme.dirtMask, Map.theme.dirtFilter);
        }
    }
    
    protected final static Img getTerrainTexture() {
        return PlatformGame.terrains[Map.bgTexture];
    }
    
    protected final static PixelMask getTerrainMask(final int _z) {
    	final int z = _z + backgroundBuilder.getPreDarken();
        return new AntiPixelMask(new ColorPixelMask(196 - 40 * z, 220 - 24 * z, 208 - 32 * z, Pancolor.MAX_VALUE));
    }
    
    protected final static Img getDarkenedTerrain(final Img terrain) {
        return Imtil.filter(terrain, terrainDarkener);
    }
    
    protected final static void applyTerrainTexture(final Img backImg, final int ix, final int iy, final int fx, final int fy, final Img terrain, final PixelMask backMask) {
        for (int x = ix; x < fx; x += 16) {
            for (int y = iy; y < fy; y += 16) {
                Imtil.copy(terrain, backImg, 0, 0, 16, 16, x, y, null, backMask);
            }
        }
    }
    
    protected final static void applyColoredTerrain(final Img backImg, final int x, final int y, final int w, final int h) {
        Imtil.filterImg(backImg, x, y, w, h, getHillFilter(Map.bgColor));
    }
    
    private final static Theme getDayTheme() {
    	return (theme == Theme.Night) ? Map.theme.levelTheme : theme;
    }
    
    private final static Img loadTileImage() {
    	final Img tileImg = ImtilX.loadImage("org/pandcorps/platform/res/bg/Tiles.png", 128, null);
    	final Theme theme = getDayTheme();
    	if (theme != Theme.Normal) {
    		final Img ext = ImtilX.loadImage("org/pandcorps/platform/res/bg/Tiles" + theme.img + ".png", false);
    		Imtil.copy(ext, tileImg, 0, 0, 128, 112, 0, 16);
    		ext.close();
    	}
    	return tileImg;
    }
    
    protected final static Panmage getTileImage() {
    	final Img tileImg = loadTileImage();
    	if (isNormalTheme() && theme != Theme.Bridge) {
    		applyDirtTexture(tileImg, 0, 16, 80, 128);
    	}
        return Pangine.getEngine().createImage("img.tiles", tileImg);
    }
    
    protected final static void loadLayers() {
        room = PlatformGame.createRoom(w, ROOM_H);
        room.setClearDepthEnabled(false);
        tm = new TileMap("act.tilemap", room, ImtilX.DIM, ImtilX.DIM);
        room.addActor(tm);
        
        timg = getTileImage();
        imgMap = tm.splitImageMap(timg);
        final TileMapImage[] row = imgMap[0];
        flashBlock = new TileMapImage[] {row[0], row[1], row[2], row[3]};
        extraAnimBlock = theme.getExtraAnimBlock();
        
        final Panlayer bg1 = PlatformGame.createParallax(room, 2);
        bg1.setClearDepthEnabled(false);
        bgtm1 = newBackgroundTileMap(1, bg1);
        Img backImg = backgroundBuilder.getImage();
        bgimg = Pangine.getEngine().createImage("img.bg", backImg);
        bgMap = bgtm1.splitImageMap(bgimg);
        
        /*
        It would look strange if layers 1 and 3 moved without 2.
        So it's probably best if each layer's master is the one directly above it
        instead of basing all on the foreground with different divisors.
        */
        final Panlayer bg2 = PlatformGame.createParallax(bg1, 2);
        bg2.setClearDepthEnabled(false);
        bgtm2 = newBackgroundTileMap(2, bg2);
        bgtm2.setImageMap(bgtm1);
        
        final Panlayer bg3 = PlatformGame.createParallax(bg2, 2);
        bgtm3 = newBackgroundTileMap(3, bg3);
        bgtm3.setImageMap(bgtm1);
    }
    
    private final static TileMap newBackgroundTileMap(final int i, final Panlayer bg) {
    	final TileMap bgtm = new TileMap("act.bgmap" + i, bg, ImtilX.DIM, ImtilX.DIM);
    	final float d = -10 * i;
    	bgtm.getPosition().setZ(d);
    	bgtm.setForegroundDepth(d + 5);
    	bg.addActor(bgtm);
    	return bgtm;
    }
    
    protected final static void clear() {
        numEnemies = 0;
        currLetter = 0;
        Coltil.clear(collectedLetters);
    }
    
    protected final static void loadLevel() {
        Mathtil.setSeed(seed);
        clear();
        victory = false;
    	floorMode = FLOOR_GRASSY;
    	topSkyColor = null;
	    bottomSkyColor = null;
	    tileGem = null;
    	backgroundBuilder = theme.getRandomBackground();
    	final Builder b = theme.getRandomBuilder();
    	w = b.getW();
    	nt = w / ImtilX.DIM;
    	ng = nt;
    	floor = b.getFloor();
    	loadLayers();
    	addPlayers(); // Add Players while floor has initial value before build() changes it
    	b.build();
    	/*tm.info();
    	bgtm1.info();
    	bgtm2.info();
    	bgtm3.info();*/
    }
    
    private static interface Builder {
    	public int getW();
    	
    	public int getFloor();
    	
    	public void build();
    }
    
    protected final static class DemoBuilder implements Builder {
    	@Override
    	public int getW() {
    		return 768;
    	}
    	
    	@Override
    	public int getFloor() {
    		return 0;
    	}
    	
    	@Override
    	public void build() {
    		buildDemo();
    	}
    }
    
    protected final static void buildDemo() {
        hill(bgtm1, 1, 4, 8, 0, 0);
        hill(bgtm1, 15, 5, 6, 3, 0);
        hill(bgtm1, 24, 4, 4, 0, 0);
        
        hill(bgtm2, 0, 6, 4, 3, 2);
        hill(bgtm2, 7, 8, 7, 0, 2);
        
        buildSky(bgtm3);
        cloud(bgtm3, 10, 10, 7);
        hill(bgtm3, 2, 9, 4, 0, 4);
        cloud(bgtm3, 4, 6, 3);
        hill(bgtm3, 13, 10, 5, 3, 4);
        
        for (int i = 0; i < nt; i++) {
            tm.setForeground(i, 0, imgMap[1][1], Tile.BEHAVIOR_SOLID);
        }
        tm.removeTile(0, 0);
        tm.removeTile(1, 0);
        tm.setForeground(2, 0, imgMap[1][0], Tile.BEHAVIOR_SOLID);
        
        step(13, 0, 1, 1);
        bush(4, 1, 0);
        ramp(27, 0, 6, 3);
        wall(32, 4, 2, 1);
        naturalRise(18, 1, 4, 3);
        naturalRise(17, 1, 1, 1);
        colorRise(25, 1, 0, 2, 0);
        breakableBlock(2, 3);
        breakableBlock(3, 3);
        bumpableBlock(4, 3);
        bumpableBlock(5, 3);
        solidBlock(6, 3);
        upBlock(2, 6);
        downBlock(6, 6);
        gem(9, 4);
        gem(14, 5);
        gem(34, 7);
        upBlock(8, 1);
        solidBlock(9, 1);
        downBlock(10, 1);
        slantUp(42, 1, 1, 3);
        goalBlock(42, 8);
        
        final EnemyDefinition def = PlatformGame.enemies.get(0);
        new Enemy(def, 80, 64);
        new Enemy(def, 232, 48);
        new Enemy(def, 360, 16);
    }
    
    private static int bx;
    private static int px;
    
    protected abstract static class RandomBuilder implements Builder {
    	protected final ArrayList<Template> templates = new ArrayList<Template>();
        protected final ArrayList<GoalTemplate> goals = new ArrayList<GoalTemplate>();
        
        protected abstract void loadTemplates();
        
        protected final void addTemplate(final Template... a) {
        	templates.add(a.length == 1 ? a[0] : new ChoiceTemplate(a));
        }
        
        protected final void addGiantTemplate() {
            if (isNormalTheme() && getDefeatedWorlds() >= 5) {
                addTemplate(new GiantTemplate());
            }
        }
        
        protected final void addNormalGoals() {
            goals.add(new SlantGoal());
            goals.add(new UpBlockGoal());
            goals.add(new RiseGoal());
            goals.add(new ColorRiseGoal());
        }
        
    	@Override
    	public int getW() {
    		return 3200;
    	}
    	
    	@Override
    	public int getFloor() {
    		return Mathtil.randi(3, 5);
    	}
    	
    	protected int getMaxFloorChange() {
    		return 3;
    	}
    	
    	protected int getMaxFloor() {
    		return 6;
    	}
    	
    	protected boolean changeFloor() {
    		return Mathtil.rand(33);
    	}
    	
    	@Override
    	public void build() {
    	    loadTemplates();
    	    
    		backgroundBuilder.build();
    		
    		final GoalTemplate goal = Mathtil.rand(goals);
    		ng = nt - goal.getWidth();
    		
    		px = 0;
    		final int floorLim = getMaxFloor(), bxStart = 8;
    		for (bx = bxStart; bx < ng; ) {
    			/*
    			Raise/lower floor with 1-way ramps
    			Some templates should allow any other template on top of it
    			Some templates should allow decorations on top
    			Bonus blocks can go in front of background rises/slants
    			Block gap patterns, 2x2 block patterns
    			Natural step stairs
    			Valleys
    			Rises with ramps in them
    			Rises woven w/ pit edges
    			Slant groups
    			Block letter patterns
    			Checkered, diagonal stripe gem patterns
    			*/
    		    final int numLetters = PlatformGame.blockWord.length(), ibx = bx;
    		    Template template = null;
    		    for (int i = 0; i < 4; i++) {
    		    	bx = ibx;
    		    	if (bx == bxStart) {
    		    		if (getDefeatedLevels() == 0) {
    		    			// Start first level with Player's name in Gems
        		    	    template = new GemMsgTemplate();
        		    	} else {
	    		    		// Always start with pit to make fall goal easier
	    		    		template = new AnyPitTemplate();
        		    	}
    		    	} else if (currLetter < numLetters && bx >= ng * (currLetter + 1) / (numLetters + 1)) {
	    		    	template = new BlockLetterTemplate();
	    		    } else if (i == 3) {
	    		    	template = Mathtil.rand() ? new BlockBonusTemplate(1) : new GemTemplate(1);
	    		    } else {
	    		    	template = Mathtil.rand(templates);
	    		    }
	    		    if (oneUseTemplates.contains(template.getClass())) {
	    		    	templates.remove(template);
	    		    }
    		    	template.plan();
    		    	if (bx < ng) {
    		    		break;
    		    	}
    		    }
    		    ground();
    		    if (bx < ng) {
    		    	template.build();
    		    }
    		    if (changeFloor()) {
    		    	if (bx + 3 < ng) {
    		    		boolean up = Mathtil.rand();
    		    		final int h = Mathtil.randi(0, getMaxFloorChange() - 1);
    		    		if (up) {
    		    			up = (floor + h) < floorLim;
    		    		} else {
    		    			up = (floor - h) < 1;
    		    		}
	    		    	if (up) {
	    		    		upStep(bx + 1, floor, h);
	    		    		floor += (h + 1);
	    		    	} else {
	    		    		floor -= (h + 1);
	    		    		downStep(bx + 1, floor, h);
	    		    	}
    		    	}
    		    	bx += 3;
    		    }
   		    	bx += Mathtil.randi(1, 4);
    		}
    		bx = nt;
    		ground();
    		goal.build();
    	}
    	
    	protected final void ground() {
    		final int stop = Math.min(bx + 1, nt - 1);
        	ground(px, stop);
        	px = bx + 2;
    	}
    	
    	protected abstract void ground(final int start, final int stop);
    	
    	protected abstract void upStep(final int x, final int y, final int h);
    	
    	protected abstract void downStep(final int x, final int y, final int h);
    }
    
    private static interface BackgroundBuilder {
    	public Img getImage();
    	
    	public int getPreDarken();
    	
    	public void build();
    }
    
    protected final static class HillBackgroundBuilder implements BackgroundBuilder {
    	@Override
    	public final Img getImage() {
    		final Img backImg = ImtilX.loadImage("org/pandcorps/platform/res/bg/Hills" + Chartil.unnull(theme.getBgImg()) + ".png", 128, null);
            if (isNormalTheme()) {
            	final PixelFilter skyFilter = theme.getSkyFilter();
            	if (skyFilter != null) {
            		Imtil.filterImg(backImg, 96, 0, 16, 48, skyFilter);
            	}
            	applyTerrainTexture(backImg, 0, 0, 48, 32);
            	applyColoredTerrain(backImg, 0, 0, 96, 96);
            } else {
            	extractSkyColors(backImg);
            }
            return backImg;
    	}
    	
    	@Override
    	public final int getPreDarken() {
    		return 0;
    	}
    	
    	@Override
    	public final void build() {
    		buildHills(bgtm1, 4, 6, 0, false); // Nearest
    		buildBackHills();
    	}
    }
    
    private final static void buildBackHills() {
    	buildHills(bgtm2, 7, 9, 2, false);
		buildHills(bgtm3, 10, 12, 4, true); // Farthest
    }
    
    protected final static class TownBackgroundBuilder implements BackgroundBuilder {
    	@Override
    	public final Img getImage() {
    		final Img backImg = ImtilX.loadImage("org/pandcorps/platform/res/bg/Town.png", 128, null);
    		applyTerrainTexture(backImg, 112, 96, 128, 128, 0, 1);
        	applyTerrainTexture(backImg, 0, 0, 48, 32, 1, 3);
        	applyColoredTerrain(backImg, 0, 32, 96, 64);
        	applyColoredTerrain(backImg, 112, 96, 16, 32);
            return backImg;
    	}
    	
    	@Override
    	public final int getPreDarken() {
    		return 0;
    	}
    	
    	@Override
    	public final void build() {
    		final int y = 2;
    		bgtm1.fillBackground(bgMap[7][7], 0, y);
    		bgtm1.fillBackground(bgMap[6][7], y, 1);
    		int i = Mathtil.randi(0, 2);
    		while (i < bgtm1.getWidth()) {
    			//i = house(bgtm1, i, y, 1, 1, 1);
    			final int win = Mathtil.randi(1, 3), winLeft = Mathtil.randi(0, win);
    			i = house(bgtm1, i, y, Mathtil.randi(0, 1), winLeft, win - winLeft);
    			i += Mathtil.randi(2, 5);
    		}
    		buildBackHills();
    	}
    }
    
    private final static class ForestBackgroundBuilder implements BackgroundBuilder {
    	@Override
    	public final Img getImage() {
    		final Img backImg = Imtil.load("org/pandcorps/platform/res/bg/Forest.png");
    		applyTerrainTexture(backImg, 32, 16, 48, 32);
    		return backImg;
    	}
    	
    	@Override
    	public final int getPreDarken() {
    		return 2;
    	}
    	
    	@Override
    	public final void build() {
    		buildForest(bgtm1, 0, false);
    		buildForest(bgtm2, 1, false);
    		buildForest(bgtm3, 2, true);
    	}
    }
    
    private final static class MountainBackgroundBuilder implements BackgroundBuilder {
    	@Override
    	public final Img getImage() {
    		final Img backImg = ImtilX.loadImage("org/pandcorps/platform/res/bg/Mountains.png", 128, null);
    		applyTerrainTexture(backImg, 0, 0, 32, 32);
    		applyTerrainTexture(backImg, 112, 0, 128, 16);
    		applyColoredTerrain(backImg, 0, 0, 64, 96);
    		applyColoredTerrain(backImg, 112, 0, 16, 96);
    		return backImg;
    	}
    	
    	@Override
    	public final int getPreDarken() {
    		return 0;
    	}
    	
    	@Override
    	public final void build() {
    		buildMountains(bgtm1, 6, 0, false);
    		buildMountains(bgtm2, 9, 1, false);
    		buildMountains(bgtm3, 12, 2, true);
    	}
    }
    
    private static void applyTerrainTexture(final Img backImg, final int ix, final int iy, final int fx, final int fy) {
    	applyTerrainTexture(backImg, ix, iy, fx, fy, 0, 3);
    }
    
    private static void applyTerrainTexture(final Img backImg, final int ix, final int iy, final int fx, final int fy, int skip, final int size) {
        extractSkyColors(backImg);
    	Img terrain = getTerrainTexture();
    	for (int i = backgroundBuilder.getPreDarken(); i > 0; i--) {
    		terrain = getDarkenedTerrain(terrain);
    	}
    	final int h = fy - iy;
        for (int z = 0; z < size; z++) {
            if (z > 0) {
                terrain = getDarkenedTerrain(terrain);
            }
            final int yoff = z * h;
            if (skip <= 0) {
            	applyTerrainTexture(backImg, ix, iy + yoff, fx, fy + yoff, terrain, getTerrainMask(z));
            } else {
            	skip--;
            }
        }
        terrain.closeIfTemporary();
    }
    
    private static void ground(final int px, final int stop) {
    	for (int i = px; i <= stop; i++) {
            tm.setForeground(i, floor, imgMap[1][1], Tile.BEHAVIOR_SOLID);
            for (int j = 0; j < floor; j++) {
            	tm.setForeground(i, j, getDirtImage(), Tile.BEHAVIOR_SOLID);
            }
        }
    }
    
    private final static int DEFAULT_ENEMY_PROBABILITY = 40;
    
    private static int enemyProbability = DEFAULT_ENEMY_PROBABILITY;
    
    private final static void enemy(final int x, final int y, final int w) {
    	if (w < 3 || (numEnemies > 0 && Mathtil.rand(enemyProbability))) {
    		if (enemyProbability > 0) {
    			enemyProbability -= 5;
    		}
    		return;
    	} else if (y >= (tm.getHeight() - 1)) { // Player needs two tiles
    	    return; // Don't drop Enemy with only one tile so Player can't reach
    	}
    	new Spawner(tm.getTileWidth() * (x + Mathtil.randi(1, w - 2)), tm.getTileHeight() * y);
    	numEnemies++;
    	enemyProbability = DEFAULT_ENEMY_PROBABILITY;
    }
    
    private final static void enemy(final EnemyDefinition def, final int x, final int y) {
    	new SpecificSpawner(def, tm.getTileWidth() * x, tm.getTileHeight() * y);
    	numEnemies++;
    }
    
    private final static int[] scratch = new int[128];
    
    private final static void swapScratch(final int i, final int j) {
    	Coltil.swap(scratch, i, j);
    }
    
    private static class GrassyBuilder extends RandomBuilder {
    	@Override
	    protected void loadTemplates() {
	        addTemplate(new NaturalRiseTemplate());
	        addTemplate(new ColorRiseTemplate());
	        addTemplate(new WallTemplate());
	        addTemplate(new StepTemplate());
	        addTemplate(new RampTemplate());
	        final Theme theme = getDayTheme();
	        if (theme == Theme.Snow || theme == Theme.Sand) {
	        	addTemplate(new SpecialGroundTemplate());
	        } else {
	        	addTemplate(new BushTemplate(), new TreeTemplate());
	        }
	        addTemplate(new AnyPitTemplate());
	        addTemplate(new UpBlockStepTemplate(), new DownBlockStepTemplate(), new BlockWallTemplate(), new BlockGroupTemplate());
	        addTemplate(new BlockBonusTemplate());
	        addTemplate(new GemTemplate());
	        addTemplate(new GemMsgTemplate());
	        addTemplate(new SlantTemplate(true), new SlantTemplate(false));
	        addGiantTemplate();
	        addNormalGoals();
	    }
    	
    	@Override
    	protected final void ground(final int start, final int stop) {
    		Level.ground(start, stop);
    	}
    	
    	@Override
    	protected final void upStep(final int x, final int y, final int h) {
    		Level.upStep(x, y, h);
    	}
    	
    	@Override
    	protected final void downStep(final int x, final int y, final int h) {
    		Level.downStep(x, y, h);
    	}
    }
    
    private final static class FlatBuilder extends GrassyBuilder {
    	@Override
	    protected final void loadTemplates() {
    		//TODO Multi-level block patterns
	        addTemplate(new WallTemplate());
	        addTemplate(new BushTemplate(), new TreeTemplate());
	        addTemplate(new AnyPitTemplate());
	        addTemplate(new UpBlockStepTemplate(), new DownBlockStepTemplate(), new BlockWallTemplate(), new BlockGroupTemplate());
	        addTemplate(new BlockBonusTemplate());
	        addTemplate(new GemTemplate());
	        addTemplate(new GemMsgTemplate());
	        addTemplate(new SlantTemplate(true), new SlantTemplate(false));
	        addGiantTemplate();
	        addNormalGoals();
	    }
    	
    	@Override
    	public final int getFloor() {
    		return 3;
    	}
    	
    	@Override
    	protected final int getMaxFloorChange() {
    		return (floor) > 1 ? 2 : 1;
    	}
    	
    	@Override
    	protected final int getMaxFloor() {
    		return 1;
    	}
    	
    	@Override
    	protected final boolean changeFloor() {
    		return (floor > 1) || super.changeFloor();
    	}
    }
    
    private static class PlatformBuilder extends RandomBuilder {
    	@Override
	    protected final void loadTemplates() {
    		floorMode = getFloorMode();
	        addTemplate(new WallTemplate());
	        addTemplate(new PitTemplate());
	        addTemplate(new BridgePitTemplate());
	        addTemplate(new BlockPitTemplate());
	        addTemplate(new UpBlockStepTemplate(), new DownBlockStepTemplate(), new BlockWallTemplate(), new BlockGroupTemplate());
	        addTemplate(new BlockBonusTemplate());
	        addTemplate(new GemTemplate());
	        addTemplate(new GemMsgTemplate());
	        addGiantTemplate();
	        goals.add(new UpBlockGoal());
	    }
    	
    	protected byte getFloorMode() {
    	    return FLOOR_BLOCK;
    	}
    	
    	@Override
    	protected void ground(final int start, final int stop) {
    		Level.blockWall(start, floor, stop - start + 1, 1);
    	}
    	
    	@Override
    	protected final void upStep(final int x, final int y, final int h) {
    	}
    	
    	@Override
    	protected final void downStep(final int x, final int y, final int h) {
    	}
    }
    
    private final static class BridgeBuilder extends PlatformBuilder {
        @Override
        protected final byte getFloorMode() {
            return FLOOR_BRIDGE;
        }
        
        @Override
        protected final void ground(final int start, final int stop) {
            for (int i = start; i <= stop; i++) {
                final int imgCol;
                if (i == start) {
                    if (i == 0) {
                        imgCol = 1;
                    } else if (DynamicTileMap.getRawBackground(tm.getTile(i - 1, floor)) == imgMap[2][0]) {
                        imgCol = 1;
                        tm.setBackground(i - 1, floor + 1, imgMap[1][imgCol], Tile.BEHAVIOR_OPEN);
                        tm.setBackground(i - 1, floor, imgMap[2][imgCol], Tile.BEHAVIOR_SOLID);
                    } else {
                        imgCol = 0;
                    }
                } else if (i == stop) {
                    imgCol = (i == nt - 1) ? 1 : 0;
                } else {
                    imgCol = 1;
                }
                tm.setBackground(i, floor + 1, imgMap[1][imgCol], Tile.BEHAVIOR_OPEN);
                tm.setBackground(i, floor, imgMap[2][imgCol], Tile.BEHAVIOR_SOLID);
            }
        }
    }
    
    private abstract static class GoalTemplate {
    	protected abstract int getWidth();
    	
    	protected abstract void build();
    }
    
    private final static class SlantGoal extends GoalTemplate {
    	private int stop;
    	private int h;
    	private int w;
    	
    	@Override
    	protected final int getWidth() {
    		stop = 1;
    		h = 3;
    		w = getSlantWidth(getSlantBase(stop), h);
    		return w;
    	}
    	
    	@Override
    	protected final void build() {
    		final int x = getSlantStart(ng, w, h, true);
    		slantUp(x, floor + 1, stop, h);
            goalBlock(x, floor + 8);
            solidBlock(x - 2, floor + 1);
    	}
    }
    
    private final static class UpBlockGoal extends GoalTemplate {
    	@Override
    	protected final int getWidth() {
    		return 5;
    	}
    	
    	@Override
    	protected final void build() {
    		upBlockStep(ng, floor + 1, 3, Mathtil.rand());
            goalBlock(ng + 3, floor + 7);
    	}
    }
    
    private static class RiseGoal extends GoalTemplate {
        @Override
        protected final int getWidth() {
            return 6; // 2 for 1st step + 1 for 2nd + 1 for 3rd + 1 for goal + 1 for gap
        }
        
        @Override
        protected final void build() {
            init();
            for (int i = 2; i >= 0; i--) {
                rise(ng + i, floor + 1, 0, i);
            }
            goalBlock(ng + 4, floor + 7);
        }
        
        protected void init() {
        }
        
        protected void rise(final int x, final int y, final int w, final int h) {
            naturalRise(x, y, w, h);
        }
    }
    
    private final static class ColorRiseGoal extends RiseGoal {
        @Override
        protected final void init() {
            ColorRiseTemplate.initStatic();
        }
        
        @Override
        protected final void rise(final int x, final int y, final int w, final int h) {
            ColorRiseTemplate.riseStatic(x, y, w, h);
        }
    }
    
    private abstract static class Template {
    	protected abstract void plan();
    	
        protected abstract void build();
        
        @Override
        public final boolean equals(final Object o) {
            return (this == o) || ((o != null) && (getClass() == o.getClass()));
        }
        
        @Override
        public final int hashCode() {
            return getClass().hashCode();
        }
    }
    
    private static class ChoiceTemplate extends Template {
    	private final Template[] choices;
    	private Template curr = null;
    	
    	private ChoiceTemplate(final Template... choices) {
    		this.choices = choices;
    	}
    	
    	@Override
    	protected final void plan() {
    		curr = Mathtil.rand(choices);
    		curr.plan();
    	}
    	
    	@Override
    	protected final void build() {
    		curr.build();
    	}
    }
    
    private abstract static class RiseTemplate extends Template {
    	private int amt;
    	private int x;
    	
        @Override
        protected final void plan() {
        	x = bx;
            amt = Mathtil.randi(1, 3);
            for (int i = 0; i < amt; i++) {
                scratch[i] = ((i == 0) ? -1 : scratch[i - 1]) + Mathtil.randi(2, 3);
            }
            final int stop = amt * 3;
            int start = bx;
            for (int i = amt; i < stop; i += 2) {
                scratch[i] = start;
                int w = Mathtil.randi(0, 8);
                if (i > amt) {
                    final int min = scratch[i - 2] + scratch[i - 1];
                    if (start + w <= min) {
                        w = min + 1 - start;
                    }
                }
                start += (Mathtil.randi(1, w + 1));
                if (i > amt && start == scratch[i - 2] + scratch[i - 1] + 2) {
                    start++;
                    w++;
                }
                scratch[i + 1] = w;
                bx = start + w + 2;
            }
        }
        
        @Override
        protected final void build() {
            for (int i = 0; i < amt; i++) {
                final int r = Mathtil.randi(0, amt - 1);
                final int io = amt + i * 2, ro = amt + r * 2;
                swapScratch(io, ro);
                swapScratch(io + 1, ro + 1);
            }
            init();
            for (int i = 0; i < amt; i++) {
                final int xo = amt + i * 2, x = scratch[xo], y = floor + 1, w = scratch[xo + 1], h = scratch[amt - i - 1];
                rise(x, y, w, h);
                enemy(x, y + h + 1, w);
            }
            enemy(x, floor + 1, bx - x - 2);
        }
        
        protected void init() {
        }
        
        protected abstract void rise(final int x, final int y, final int w, final int h);
    }
    
    private final static class NaturalRiseTemplate extends RiseTemplate {
    	@Override
    	protected final void rise(final int x, final int y, final int w, final int h) {
    		naturalRise(x, y, w, h);
    	}
    }
    
    private static int[] colors = new int[3];
    private static int colorIndex = 0;
    
    private final static class ColorRiseTemplate extends RiseTemplate {
    	@Override
    	protected final void init() {
    	    initStatic();
    	}
    	
    	private final static void initStatic() {
    		for (int i = 0; i < 3; i++) {
    			colors[i] = i;
    		}
    		colorIndex = 0;
    		Mathtil.shuffle(colors);
    	}
    	
        @Override
        protected final void rise(final int x, final int y, final int w, final int h) {
            riseStatic(x, y, w, h);
        }
        
        private final static void riseStatic(final int x, final int y, final int w, final int h) {
            colorRise(x, y, w, h, colors[colorIndex]);
            colorIndex = (colorIndex + 1) % 3;
        }
    }
    
    private abstract static class SimpleTemplate extends Template {
    	private final int minW;
    	private final int maxW;
    	private final int ext;
    	protected int x;
    	protected int w;
    	
    	protected SimpleTemplate() {
    		this(0, 8);
    	}
    	
    	protected SimpleTemplate(final int minW, final int maxW) {
    		this(minW, maxW, 2);
    	}
    	
    	protected SimpleTemplate(final int minW, final int maxW, final int ext) {
    		this.minW = minW;
    		this.maxW = maxW;
    		this.ext = ext;
    	}
    	
        @Override
        protected final void plan() {
        	w = Mathtil.randi(minW, maxW);
        	x = bx;
        	bx += (w + ext);
        }
    }
    
    private final static class WallTemplate extends SimpleTemplate {
    	@Override
        protected final void build() {
    		final int y = floor + 1, h = Mathtil.randi(1, 3);
        	wall(x, y, w, h);
        	enemy(x, y + h, w);
        }
    }
    
    private final static class StepTemplate extends SimpleTemplate {
        @Override
        protected final void build() {
        	final int h = Mathtil.randi(0, 2);
        	step(x, floor, w, h);
        	enemy(x, floor + h + 2, w);
        }
    }
    
    private final static class RampTemplate extends Template {
    	private int w;
    	private int h;
    	private int x;
    	
        @Override
        protected final void plan() {
        	w = Mathtil.randi(0, 6);
        	h = Mathtil.randi(1, 4);
        	x = bx;
        	bx += (w + h * 2 + 2);
        }
        
        @Override
        protected final void build() {
        	ramp(x, floor, w, h);
        }
    }
    
    private final static class SlantTemplate extends Template {
        private final boolean up;
        private int stop;
        private int h;
        private int x;
        
        private SlantTemplate(final boolean up) {
            this.up = up;
        }
        
        @Override
        protected final void plan() {
            stop = Mathtil.randi(0, 2);
            h = Mathtil.randi(2, 4);
            final int w = getSlantBase(stop);
            x = getSlantStart(bx, w, h, up);
            bx += getSlantWidth(w, h);
        }
        
        @Override
        protected final void build() {
            slant(x, floor + 1, stop, h, up);
            enemy(x, floor + 1, bx - x - 2);
        }
    }
    
    private final static int getSlantBase(final int stop) {
    	return (stop + 1) * 2 - 1;
    }
    
    private final static int getSlantStart(final int x, final int w, final int h, final boolean up) {
        return x + (up ? (h - 1) : (w + 1));
    }
    
    private final static int getSlantWidth(final int w, final int h) {
        return Math.max(h + w + 1, 2);
    }
    
    private final static class AnyPitTemplate extends ChoiceTemplate {
    	private AnyPitTemplate() {
    		super(new PitTemplate(), new BridgePitTemplate(), new BlockPitTemplate());
    	}
    }
    
    private final static class PitTemplate extends SimpleTemplate {
    	protected PitTemplate() {
    		super(2, 4);
    	}
    	
        @Override
        protected final void build() {
        	pit(x, floor, w);
        }
    }
    
    private final static class BridgePitTemplate extends SimpleTemplate {
    	protected BridgePitTemplate() {
    		super(5, 10);
    	}
    	
        @Override
        protected final void build() {
        	pit(x, floor, w);
        	final int stop = x + w;
        	for (int i = x + 2; i < stop; i++) {
        		solidBlock(i, floor + 3);
        	}
        	enemy(x + 2, floor + 4, w);
        }
    }
    
    private final static class UpBlockStepTemplate extends SimpleTemplate {
    	protected UpBlockStepTemplate() {
    		super(1, 3, 0);
    	}
    	
        @Override
        protected final void build() {
        	upBlockStep(x, floor + 1, w, Mathtil.rand());
        }
    }
    
    private final static class DownBlockStepTemplate extends SimpleTemplate {
    	protected DownBlockStepTemplate() {
    		super(1, 3, 0);
    	}
    	
        @Override
        protected final void build() {
        	downBlockStep(x, floor + 1, w, Mathtil.rand());
        }
    }
    
    private final static class BlockWallTemplate extends SimpleTemplate {
    	protected BlockWallTemplate() {
    		super(1, 8, 0);
    	}
    	
        @Override
        protected final void build() {
        	final int y = floor + 1, h = Mathtil.randi(1, 3);
        	blockWall(x, y, w, h);
        	enemy(x, y + h, w);
        }
    }
    
    private abstract static class BlockBaseTemplate extends Template {
    	private int x;
    	protected int wSlope;
    	private int wFlat;
    	
    	@Override
        protected final void plan() {
        	wSlope = Mathtil.randi(1, 3);
        	wFlat = Mathtil.randi(getMinFlat(), getMaxFlat());
        	x = bx;
        	bx += (wSlope * 2 + wFlat);
        }
        
        @Override
        protected final void build() {
        	final boolean ramp = Mathtil.rand();
        	final int f = floor + 1;
        	upBlockStep(x, f, wSlope, ramp);
        	x += wSlope;
        	center(x, wFlat, ramp);
        	downBlockStep(x + wFlat, f, wSlope, ramp);
        }
        
        protected abstract int getMinFlat();
        
        protected abstract int getMaxFlat();
        
        protected abstract void center(final int x, final int w, final boolean ramp);
    }
    
    private final static class BlockGroupTemplate extends BlockBaseTemplate {
    	@Override
    	protected final int getMinFlat() {
    		return 0;
    	}
        
    	@Override
        protected final int getMaxFlat() {
    		return 6;
    	}
        
    	@Override
    	protected final void center(final int x, final int w, final boolean ramp) {
    		blockWall(x, floor + 1, w, wSlope + (ramp ? 0 : 1));
    	}
    }
    
    private final static class BlockPitTemplate extends BlockBaseTemplate {
    	@Override
    	protected final int getMinFlat() {
    		return 2;
    	}
        
    	@Override
        protected final int getMaxFlat() {
    		return 4;
    	}
    	
    	@Override
    	protected final void center(final int x, final int w, final boolean ramp) {
    		pit(x - 1, floor, w);
    	}
    }
    
    private final static class BlockBonusTemplate extends SimpleTemplate {
    	protected BlockBonusTemplate() {
    		this(8);
    	}
    	
    	protected BlockBonusTemplate(final int maxW) {
    		super(1, maxW, 0);
    	}
    	
        @Override
        protected final void build() {
        	final int stop = x + w;
        	final boolean flag = Mathtil.rand();
        	for (int i = x; i < stop; i++) {
        		if (flag) {
        			bumpableBlock(i, floor + 3);
        		} else {
        			breakableBlock(i, floor + 3);
        		}
        	}
        	enemy(x, floor + 4, w);
        }
    }
    
    private final static class BlockLetterTemplate extends SimpleTemplate {
    	protected BlockLetterTemplate() {
    		super(1, 1, 0);
    	}
    	
        @Override
        protected final void build() {
        	letterBlock(x, floor + 3);
        }
    }
    
    private final static class GemTemplate extends SimpleTemplate {
    	protected GemTemplate() {
    		this(10);
    	}
    	
    	protected GemTemplate(final int maxW) {
    		super(1, maxW, 0);
    	}
    	
    	@Override
		protected final void build() {
    		final int stop = x + w;
    		final boolean block = Mathtil.rand();
    		for (int i = x; i < stop; i++) {
    			if (block) {
    				solidBlock(i, floor + 3);
    				gem(i, floor + 4);
    			} else {
    				gem(i, floor + 3);
    			}
    		}
    		enemy(x, floor + 1, w);
    	}
    }
    
    private final static class GemMsgTemplate extends Template {
    	private int x;
    	private String msg;
    	
		@Override
		protected final void plan() {
			x = bx;
			if (getDefeatedLevels() == 0) {
			    msg = PlatformGame.pcs.get(0).getBonusName();
			} else {
    			msg = Mathtil.rand(theme.gemMessages);
    			if ("PLAYER".equals(msg)) {
    				msg = Mathtil.rand(PlatformGame.pcs).getBonusName();
    			}
			}
			bx += gemMsg(x, floor + 1, msg, false) + 2;
		}

		@Override
		protected final void build() {
			gemMsg(x + 1, floor + 1, msg, true);
		}
    }
    
    private final static class SpecialGroundTemplate extends SimpleTemplate {
    	protected SpecialGroundTemplate() {
    		super(4, 10);
    	}
    	
    	@Override
        protected final void build() {
    		final int stop = x + w + 1, y;
    		final boolean sunken = floor >= 2;
    		if (sunken) {
    			y = floor - 1;
    			for (int i = 0; i <= 2; i++) {
	    			tm.setForeground(x, floor - i, imgMap[1 + i][2], Tile.BEHAVIOR_SOLID);
	    			tm.setForeground(stop, floor - i, imgMap[1 + i][0], Tile.BEHAVIOR_SOLID);
    			}
    		} else {
	    		for (int i = 1; i <= 2; i++) {
	    			solidBlock(x, floor + i);
	    			solidBlock(stop, floor + i);
	    		}
	    		y = floor + 1;
    		}
    		for (int i = x + 1; i < stop; i++) {
    			if (sunken) {
    				tm.removeTile(i, floor);
    				tm.setForeground(i, y - 1, imgMap[1][1], Tile.BEHAVIOR_SOLID);
    			}
    			tm.setForeground(i, y, imgMap[1][5], theme.specialGroundBehavior);
    		}
    	}
    }
    
    private final static class BushTemplate extends SimpleTemplate {
    	protected BushTemplate() {
    		super(0, 6);
    	}
    	
    	@Override
        protected final void build() {
        	bush(x, floor + 1, w);
        	enemy(x, floor + 1, w);
        }
    }
    
    private final static class TreeTemplate extends SimpleTemplate {
    	protected TreeTemplate() {
    		super(4, 4, 0);
    	}
    	
    	@Override
        protected final void build() {
        	tree(x, floor + 1);
        	enemy(x, floor + 1, w);
        }
    }
    
    private final static class GiantTemplate extends SimpleTemplate {
    	protected GiantTemplate() {
    		super(12, 12, 0);
    	}
    	
    	@Override
        protected final void build() {
    		blockWall(x, floor + 1, 1, 2);
    		blockWall(x + 11, floor + 1, 1, 2);
    		enemy(Mathtil.rand() ? PlatformGame.trollColossus : PlatformGame.ogreBehemoth, x + 5, floor + 1);
    	}
    }
    
    private final static void extractSkyColors(final Img img) {
    	if (topSkyColor == null) {
	        topSkyColor = Imtil.getColor(img, 96, 0);
	        bottomSkyColor = Imtil.getColor(img, 96, 32);
    	}
    }
    
    private final static void buildSky(final TileMap tm, final int base, final int mid) {
        final int topHeight = tm.getHeight() - (mid + 1), bottomHeight = mid - base;
        if (topHeight < bottomHeight) {
            tm.fillBackground(bgMap[0][6], mid + 1, topHeight);
            Pangine.getEngine().setBgColor(bottomSkyColor);
        } else {
            tm.fillBackground(bgMap[2][6], base, bottomHeight);
            Pangine.getEngine().setBgColor(topSkyColor);
        }
        tm.fillBackground(bgMap[1][6], mid, 1);
    }
    
    private final static void buildSky(final TileMap tm) {
        buildSky(tm, 0, 8);
    }
    
    private final static void buildHills(final TileMap tm, final int miny, final int maxy, final int iy, final boolean cloud) {
        if (cloud) {
            buildSky(tm);
        }
    	final int maxx = tm.getWidth() + 1;
    	int x = Mathtil.randi(-1, 4);
    	boolean c = Mathtil.rand();
    	while (x < maxx) {
    		final int y = Mathtil.randi(miny, maxy), w = Mathtil.randi(0, 8);
    		int cx = -100, cy = cx, cw = cx, g = Mathtil.randi(3, 5);
    		boolean cb = false;
    		if (cloud) {
    			if (c) {
    				final int o = Mathtil.randi(1, 3);
    				if (Mathtil.rand()) {
    					cx = x;
    					x += o;
    				} else {
    					cx = x + o;
    					g += o;
    				}
    				if (w == 0 || w + 1 == o) {
    					cy = y - Mathtil.randi(2, 4);
    				} else {
    					cy = y - (Mathtil.rand() ? 0 : 2);
    				}
    				cw = Math.max(1, w);
    				cb = Mathtil.rand();
    			}
    			c = !c;
    		}
    		if (cb && cx != -100) {
    			cloud(tm, cx, cy, cw);
    		}
    		hill(tm, x, y, w, Mathtil.rand() ? 0 : 3, iy);
    		if (!cb && cx != -100) {
    			cloud(tm, cx, cy, cw);
    		}
    		x += (w + g);
    	}
    }
    
    private final static void buildForest(final TileMap tm, final int off, final boolean sky) {
    	final int iy = off * 2;
    	tm.fillBackground(bgMap[iy + 1][2], 0, off + 1);
    	tm.fillBackground(bgMap[iy][2], off + 1, 1);
    	final int tmw = tm.getWidth(), tmh = tm.getHeight();
    	if (sky) {
    	    buildSky(tm, off + 2, tmh - 5);
    	}
    	int i = Mathtil.randi(-1, 2);
    	while (i < tmw) {
    		if (i >= 0) {
    			tm.fillBackground(bgMap[iy + 1][0], i, off + 2, 1, tmh - (off * 2) - 3);
    			tm.setBackground(0, iy, i, tmh - off - 2);
    		}
    		if (i < (tmw - 1)) {
    			tm.fillBackground(bgMap[iy + 1][1], i + 1, off + 2, 1, tmh - (off * 2) - 3);
    			tm.setBackground(1, iy, i + 1, tmh - off - 2);
    		}
    		i += Mathtil.randi(3, 6);
    	}
    }
    
    private final static void buildMountains(final TileMap tm, final int maxy, final int v, final boolean sky) {
    	final int miny = maxy - 3;
    	final int tmw = tm.getWidth();
    	if (sky) {
    		buildSky(tm);
    	}
    	int i = Mathtil.randi(-2, 2);
    	do {
    		final int y = Mathtil.randi(miny, maxy);
    		mountain(tm, i, y, v, Mathtil.rand());
    		i = i + y * 2 + Mathtil.randi(2, 4);
    	} while (i < tmw);
    }
    
    private final static void addPlayers() {
    	final int size = PlatformGame.pcs.size();
        final ArrayList<Player> players = new ArrayList<Player>(size);
        for (int i = 0; i < size; i++) {
        	final PlayerContext pc = PlatformGame.pcs.get(i);
        	Goal.initGoals(pc);
            final Player player = new Player(pc);
            room.addActor(player);
            PlatformGame.setPosition(player, 40 + (20 * i), (floor + 1) * 16);
            players.add(player);
        }
        Pangine.getEngine().track(Panverage.getArithmeticMean(players));
    }
    
    private final static void setBg(final TileMap tm, final int i, final int j, final TileMapImage[][] imgMap, final int iy, final int ix) {
    	if (tm.isBad(i, j)) {
    		return;
    	}
        tm.setTile(i, j, imgMap[iy][ix], null, Tile.BEHAVIOR_OPEN);
    }
    
    private final static void setFg(final TileMap tm, final int i, final int j, final TileMapImage[][] imgMap, final int iy, final int ix) {
    	if (tm.isBad(i, j)) {
    		return;
    	}
    	tm.setForeground(i, j, imgMap[iy][ix]);
    }
    
    private final static int house(final TileMap tm, final int x, final int y, final int border, final int winLeft, final int winRight) {
    	final int sectionSize = 3, winLeftMult = winLeft * sectionSize, winRightMult = winRight * sectionSize;
    	final int last = x + 5 + border + winLeftMult + sectionSize + winRightMult + border;
    	if (last >= tm.getWidth()) {
    		return last;
    	}
    	tm.rectangleBackground(4, 7, x + 1, y, 3, 2);
    	tm.rectangleBackground(1, 1, x + 1, y + 2, 3, 1);
    	tm.rectangleBackground(4, 7, x + 1, y + 3, 3, 1);
    	tm.rectangleBackground(0, 0, x, y + 4, 2, 1);
    	setBg(tm, x + 2, y + 4, bgMap, 6, 5);
    	tm.rectangleForeground(3, 0, x + 3, y + 4, 2, 1);
    	setBg(tm, x + 1, y + 5, bgMap, 5, 6);
    	tm.rectangleBackground(5, 1, x + 2, y + 5, 1, 2);
    	setFg(tm, x + 3, y + 5, bgMap, 5, 7);
    	int xb = x + 4;
    	for (int i = 0; i < border; i++) {
    		houseMid(tm, xb + i, y);
    	}
    	for (int i = 0; i < winLeft; i++) {
    		houseSection(tm, xb + border + (i * sectionSize), y, false);
    	}
    	houseSection(tm, xb + border + winLeftMult, y, true);
    	xb += sectionSize;
    	for (int i = 0; i < winRight; i++) {
    		houseSection(tm, xb + border + winLeftMult + (i * sectionSize), y, false);
    	}
    	for (int i = 0; i < border; i++) {
    		houseMid(tm, xb + border + winLeftMult + winRightMult + i, y);
    	}
    	houseRight(tm, xb + border + winLeftMult + winRightMult + border, y);
    	tm.fillBackground(bgMap[1][4], x + 4, y + 4, 1 + border + winLeftMult + sectionSize + winRightMult + border, 1);
    	setBg(tm, last, y + 4, bgMap, 0, 4);
    	tm.fillBackground(bgMap[1][0], x + 3, y + 5, 1 + border + winLeftMult + sectionSize + winRightMult + border, 1);
    	setBg(tm, x + 4 + border + winLeftMult + sectionSize + winRightMult + border, y + 5, bgMap, 5, 7);
    	tm.fillBackground(bgMap[4][6], x + 3, y + 6, border + winLeftMult + sectionSize + winRightMult + border, 1);
    	setBg(tm, x + 3 + border + winLeftMult + sectionSize + winRightMult + border, y + 6, bgMap, 4, 7);
    	tm.fillBackground(bgMap[6][7], x + 1, y - 1, 4 + border + winLeftMult + sectionSize + winRightMult + border, 1);
    	return last;
    }
    
    private final static void houseSection(final TileMap tm, final int x, final int y, final boolean door) {
    	houseRight(tm, x, y);
    	houseMid(tm, x + 1, y);
    	if (door) {
    		tm.rectangleBackground(3, 7, x + 1, y, 1, 2);
    	} else {
    		setBg(tm, x + 1, y + 2, bgMap, 0, 2); // Window
    	}
    	houseLeft(tm, x + 2, y);
    }
    
    private final static void houseLeft(final TileMap tm, final int x, final int y) {
    	houseCol(tm, x, y, -1);
    }
    
    private final static void houseMid(final TileMap tm, final int x, final int y) {
    	houseCol(tm, x, y, 0);
    }
    
    private final static void houseRight(final TileMap tm, final int x, final int y) {
    	houseCol(tm, x, y, 1);
    }
    
    private final static void houseCol(final TileMap tm, final int x, final int y, final int off) {
    	tm.rectangleBackground(5 + off, 7, x, y, 1, 2);
		setBg(tm, x, y + 2, bgMap, 1, 2 + off);
		setBg(tm, x, y + 3, bgMap, 7, 5 + off);
    }
    
    private final static void hill(final TileMap tm, final int x, final int y, final int w, final int ix, final int iy) {
        for (int j = 0; j < y; j++) {
            setBg(tm, x, j, bgMap, iy + 1, ix);
            setBg(tm, x + w + 1, j, bgMap, iy + 1, ix + 2);
        }
        final int stop = x + w;
        for (int i = x + 1; i <= stop; i++) {
            setBg(tm, i, y, bgMap, iy, ix + 1);
            for (int j = 0; j < y; j++) {
                setBg(tm, i, j, bgMap, iy + 1, ix + 1);
            }
        }
        setFg(tm, x, y, bgMap, iy, ix);
        setFg(tm, stop + 1, y, bgMap, iy, ix + 2);
    }
    
    private final static void mountain(final TileMap tm, final int x, final int y, final int v, final boolean tex) {
    	final int ix = tex ? 0 : 2, ix1 = ix + 1, iy = v * 2, iy1 = iy + 1;
    	final int m = (tex ? 0 : 3) + v, mx = 7;
    	final int is = 4, is1 = 5, ms = 3 + v, mxs = 6;
    	final int ys = y * 3 / 4;
    	for (int j = 0; j < y; j++) {
    		final int xj = x + j, xy2j = x + y * 2 - j - 1;
    		final int ixc, ixc1;
    		final int mc, mxc;
    		if (j < ys) {
	    		ixc = ix;
	    		ixc1 = ix1;
	    		mc = m;
	    		mxc = mx;
    		} else {
    			ixc = is;
	    		ixc1 = is1;
	    		mc = ms;
	    		mxc = mxs;
    		}
    		setFg(tm, xj, j, bgMap, iy, ixc);
    		if (j < y - 1) {
    			final int xy2j1 = xy2j - 1;
    			setBg(tm, xj + 1, j, bgMap, iy1, ixc);
    			for (int i = xj + 2; i < xy2j1; i++) {
    				setBg(tm, i, j, bgMap, mc, mxc);
    			}
    			setBg(tm, xy2j1, j, bgMap, iy1, ixc1);
    		}
    		setFg(tm, xy2j, j, bgMap, iy, ixc1);
    		if (j == ys - 1) {
    			for (int i = xj + 1; i < xy2j; i += 2) {
    				setFg(tm, i, j, bgMap, 7, v * 2);
    				setFg(tm, i + 1, j, bgMap, 7, v * 2 + 1);
    			}
    		} else if (j == ys) {
    			for (int i = xj + 1; i < xy2j; i += 2) {
    				setFg(tm, i, j, bgMap, 6, v * 2 + 1);
   					setFg(tm, i + 1, j, bgMap, 6, v * 2);
    			}
    		}
    	}
    }
    
    private final static void cloud(final TileMap tm, final int x, final int y, final int w) {
        final int stop = x + w;
        for (int i = x + 1; i <= stop; i++) {
            setBg(tm, i, y, bgMap, 7, 1);
            setBg(tm, i, y + 1, bgMap, 6, 1);
        }
        setFg(tm, x, y, bgMap, 7, 0);
        setFg(tm, x, y + 1, bgMap, 6, 0);
        setFg(tm, stop + 1, y, bgMap, 7, 2);
        setFg(tm, stop + 1, y + 1, bgMap, 6, 2);
    }
    
    private final static void addShadow(final int x, final int y) {
        if (y == 0 || floorMode != FLOOR_BRIDGE) {
            return;
        }
        final int y1 = y - 1;
        final Object bg = DynamicTileMap.getRawBackground(tm.getTile(x, y1));
        for (int col = 0; col < 2; col++) {
            if (bg == imgMap[2][col]) {
                tm.setBackground(x, y1, imgMap[3][col]);
                break;
            }
        }
    }
    
    private final static void setFgShadowed(final int x, final int y, final int iy, final int ix, final byte behavior) {
        tm.setForeground(x, y, imgMap[iy][ix], behavior);
        addShadow(x, y);
    }
    
    private final static void solidBlock(final int x, final int y) {
        setFgShadowed(x, y, 0, 4, Tile.BEHAVIOR_SOLID);
    }
    
    private final static void bumpableBlock(final int x, final int y) {
        tm.setForeground(x, y, imgMap[0][0], PlatformGame.TILE_BUMP);
    }
    
    private final static void breakableBlock(final int x, final int y) {
        tm.setForeground(x, y, imgMap[0][5], PlatformGame.TILE_BREAK);
    }
    
    private final static void letterBlock(final int x, final int y) {
        tm.setForeground(x, y, PlatformGame.getBlockWordLetter(currLetter++), PlatformGame.TILE_BUMP);
    }
    
    private final static void upBlock(final int x, final int y) {
        setFgShadowed(x, y, 0, 6, PlatformGame.TILE_UPSLOPE);
    }
    
    private final static void downBlock(final int x, final int y) {
        setFgShadowed(x, y, 0, 7, PlatformGame.TILE_DOWNSLOPE);
    }
    
    private final static void goalBlock(final int x, final int y) {
        goalIndex = tm.getIndex(x, y);
        tm.setForeground(goalIndex, imgMap[7][0], PlatformGame.TILE_BUMP);
    }
    
    private final static void step(final int x, final int y, final int w, final int h) {
    	step(x, y, w, h, 1);
    }
    
    private final static void upStep(final int x, final int y, final int h) {
    	step(x, y, 0, h, 0);
    }
    
    private final static void downStep(final int x, final int y, final int h) {
    	step(x, y, -1, h, 2);
    }
    
    private final static void step(final int x, final int y, final int w, final int h, final int mode) {
        // Will also want 1-way steps going up and 1-way down; same with ramps
    	if (mode != 2) {
    		tm.setForeground(x, y, imgMap[3][0], Tile.BEHAVIOR_SOLID);
    	}
        final int stop = x + w + 1, ystop = y + h + 1;
        for (int j = y + 1; j < ystop; j++) {
        	if (mode != 2) {
        		tm.setForeground(x, j, imgMap[2][0], Tile.BEHAVIOR_SOLID);
        	}
        	if (mode != 0) {
        		tm.setForeground(stop, j, imgMap[2][2], Tile.BEHAVIOR_SOLID);
        	}
        	if (mode == 1) {
	            for (int i = x + 1; i < stop; i++) {
	                tm.setForeground(i, j, getDirtImage(), Tile.BEHAVIOR_SOLID);
	            }
        	}
        }
        if (mode != 2) {
        	tm.setForeground(x, ystop, imgMap[1][0], Tile.BEHAVIOR_SOLID);
        }
        if (mode == 1) {
	        for (int i = x + 1; i < stop; i++) {
	            tm.setForeground(i, ystop, imgMap[1][1], Tile.BEHAVIOR_SOLID);
	            tm.setForeground(i, y, getDirtImage(), Tile.BEHAVIOR_SOLID);
	        }
        }
        if (mode != 0) {
	        tm.setForeground(stop, ystop, imgMap[1][2], Tile.BEHAVIOR_SOLID);
	        tm.setForeground(stop, y, imgMap[3][2], Tile.BEHAVIOR_SOLID);
        }
    }
    
    private final static void ramp(final int x, final int y, final int w, final int h) {
        final int fstop = x + w + h * 2, ystop = y + h;
        for (int jo = y; jo <= ystop; jo++) {
            final int jb = jo - y, stop = fstop - jb;
            if (jb != 0) {
                tm.setForeground(x + jb, jo, imgMap[3][3], PlatformGame.TILE_UPSLOPE);
                tm.setForeground(stop + 1, jo, imgMap[3][4], PlatformGame.TILE_DOWNSLOPE);
            }
            if (jo == ystop) {
                for (int i = x + jb + 1; i <= stop; i++) {
                    tm.setForeground(i, jo, imgMap[1][1], Tile.BEHAVIOR_SOLID);
                }
            } else {
                tm.setForeground(x + jb + 1, jo, imgMap[3][0], Tile.BEHAVIOR_SOLID);
                for (int i = x + jb + 2; i < stop; i++) {
                    tm.setForeground(i, jo, getDirtImage(), Tile.BEHAVIOR_SOLID);
                }
                tm.setForeground(stop, jo, imgMap[3][2], Tile.BEHAVIOR_SOLID);
            }
        }
    }
    
    private final static void blockWall(final int x, final int y, final int w, final int h) {
    	for (int i = 0; i < w; i++) {
    		final int xi = x + i;
    		for (int j = 0; j < h; j++) {
    			solidBlock(xi, y + j);
    		}
    	}
    }
    
    private final static void upBlockStep(final int x, final int y, final int w, final boolean ramp) {
    	for (int i = 0; i < w; i++) {
    		final int xi = x + i;
    		for (int j = 0; j <= i; j++) {
    			if (ramp && j == i) {
    				upBlock(xi, y + j);
    			} else {
    				solidBlock(xi, y + j);
    			}
    		}
    	}
    }
    
    private final static void downBlockStep(final int x, final int y, final int w, final boolean ramp) {
    	for (int i = 0; i < w; i++) {
    		final int xi = x + w - i - 1;
    		for (int j = 0; j <= i; j++) {
    			if (ramp && j == i) {
    				downBlock(xi, y + j);
    			} else {
    				solidBlock(xi, y + j);
    			}
    		}
    	}
    }
    
    private final static void naturalRise(final int x, final int y, final int w, final int h) {
        final int ystop = y + h;
        for (int j = y; j < ystop; j++) {
            tm.setBackground(x, j, imgMap[2][3]);
            tm.setBackground(x + w + 1, j, imgMap[2][4]);
        }
        final int stop = x + w;
        for (int i = x + 1; i <= stop; i++) {
            tm.setBackground(i, ystop, imgMap[1][1], PlatformGame.TILE_FLOOR);
            for (int j = y; j < ystop; j++) {
                tm.setBackground(i, j, getDirtImage());
            }
        }
        tm.setForeground(x, ystop, imgMap[1][3], PlatformGame.TILE_FLOOR);
        tm.setForeground(stop + 1, ystop, imgMap[1][4], PlatformGame.TILE_FLOOR);
    }
    
    private final static void colorRise(final int x, final int y, final int w, final int h, final int _o) {
        final int o = _o * 2 + 2, o1 = o + 1, ystop = y + h;
        for (int j = y; j < ystop; j++) {
            tm.setBackground(x, j, imgMap[o1][5]);
            tm.setBackground(x + w + 1, j, imgMap[o1][7]);
        }
        final int stop = x + w;
        for (int i = x + 1; i <= stop; i++) {
            tm.setBackground(i, ystop, imgMap[o][6], PlatformGame.TILE_FLOOR);
            for (int j = y; j < ystop; j++) {
                tm.setBackground(i, j, imgMap[o1][6]);
            }
        }
        tm.setForeground(x, ystop, imgMap[o][5], PlatformGame.TILE_FLOOR);
        tm.setForeground(stop + 1, ystop, imgMap[o][7], PlatformGame.TILE_FLOOR);
    }
    
    private final static void wall(final int x, final int y, final int w, final int h) {
        final int ystop = y + h - 1, xstop = x + w + 1;
        for (int j = y; j <= ystop; j++) {
        	final int iy = (floorMode == FLOOR_BRIDGE && j < ystop) ? 5 : 4;
            setFgShadowed(x, j, iy, 0, Tile.BEHAVIOR_SOLID);
            for (int i = x + 1; i < xstop; i++) {
                setFgShadowed(i, j, iy, 1, Tile.BEHAVIOR_SOLID);
            }
            setFgShadowed(xstop, j, iy, 2, Tile.BEHAVIOR_SOLID);
        }
    }
    
    // x - h + 2 to x + ((stop + 1) * 2 - 1) + 1
    private final static void slantUp(final int x, final int y, final int stop, final int h) {
        slant(x, y, stop, h, true);
    }
    
    private final static void slant(final int x, final int y, final int stop, final int h, final boolean up) {
        final int ystop = y + h, w = getSlantBase(stop), m, c1, c2, c3;
        final byte b;
        if (up) {
            m = 1;
            c1 = 3;
            c2 = 4;
            c3 = 0;
            b = PlatformGame.TILE_UPSLOPE_FLOOR;
        } else {
            m = -1;
            c1 = 4;
            c2 = 3;
            c3 = 2;
            b = PlatformGame.TILE_DOWNSLOPE_FLOOR;
        }
        for (int jo = y; jo < ystop; jo++) {
            final int jb = jo - y;
            tm.setForeground(x - m * jb, jo, imgMap[jb == (h - 1) ? 7 : 5][c1]);
            for (int i = 1; i <= w; i++) {
                tm.setForeground(x + m * (i - jb), jo, getDirtImage());
            }
            tm.setForeground(x + m * (w + 1 - jb), jo, imgMap[4][c2]);
        }
        for (int jb = 0; jb <= stop; jb++) {
            final int jo = jb + ystop, off = jb + 3 - h;
            tm.setForeground(x + m * (off - 2), jo, imgMap[3][c1], b);
            tm.setForeground(x + m * (off - 1), jo, jb == stop ? imgMap[6][c2] : imgMap[3][c3]);
            if (jb < stop) {
                for (int i = jb; i <= w - 3 - jb; i++) {
                    tm.setForeground(x + m * (i + 3 - h), jo, getDirtImage());
                }
                tm.setForeground(x + m * (w + 1 - h - jb), jo, imgMap[4][c2]);
            }
        }
    }
    
    private final static TileMapImage getDirtImage() {
        return imgMap[Mathtil.rand(90) ? 2 : 3][1];
    }
    
    private final static void pit(final int x, final int y, final int w) {
    	final int stop = x + w + 1, ystop = (floorMode == FLOOR_BRIDGE) ? (y + 1) : y;
    	for (int j = 0; j <= ystop; j++) {
    		if (floorMode == FLOOR_GRASSY) {
	    		final int iy = (j == y) ? 1 : 2;
		    	tm.setForeground(x, j, imgMap[iy][2], Tile.BEHAVIOR_SOLID);
		    	tm.setForeground(stop, j, imgMap[iy][0], Tile.BEHAVIOR_SOLID);
    		} else if (floorMode == FLOOR_BLOCK) {
    		    if (j == y) {
        			solidBlock(x, j);
        			solidBlock(stop, j);
    		    }
    		} else if (floorMode == FLOOR_BRIDGE) {
    		    if (j == y) {
                    tm.setBackground(x, j, imgMap[Tile.getBehavior(tm.getTile(x, j + 1)) != Tile.BEHAVIOR_OPEN ? 3 : 2][0], Tile.BEHAVIOR_SOLID);
                    tm.setBackground(stop, j, imgMap[2][0], Tile.BEHAVIOR_SOLID);
    		    } else if (j == ystop) {
    		        tm.setBackground(x, j, imgMap[1][0]); // Might have a block at pit edge; don't make it open
    		        tm.setBackground(stop, j, imgMap[1][0]);
    		    }
    		} else {
    		    throw new IllegalStateException("Unexpected floorMode " + floorMode);
    		}
	    	for (int i = x + 1; i < stop; i++) {
	    		tm.removeTile(i, j);
	    	}
    	}
    }
    
    private final static void bush(final int x, final int y, final int w) {
        tm.setForeground(x, y, imgMap[1][5]);
        final int stop = x + w;
        for (int i = x + 1; i <= stop; i++) {
            tm.setForeground(i, y, imgMap[1][6]);
        }
        tm.setForeground(stop + 1, y, imgMap[1][7]);
    }
    
    private final static void tree(final int x, final int y) {
    	for (int j = 0; j < 2; j++) {
    		tm.setForeground(x + 1, y + j, imgMap[7][1]);
    		tm.setForeground(x + 2, y + j, imgMap[7][2]);
    		tm.setForeground(x + 1 + j, y + 2, imgMap[6][2]);
    		tm.setForeground(x + 1 + j, y + 3, imgMap[5][2]);
    		tm.setForeground(x + j, y + 3 + j, imgMap[5][0]);
    		tm.setForeground(x + 3 - j, y + 3 + j, imgMap[5][1]);
    	}
    	tm.setForeground(x, y + 2, imgMap[6][0]);
		tm.setForeground(x + 3, y + 2, imgMap[6][1]);
    }
    
    private final static void gem(final int x, final int y) {
        if (tileGem == null) {
        	tileGem = tm.getTile(null, PlatformGame.gem[0], PlatformGame.TILE_GEM);
        }
        tm.setTile(x, y, tileGem);
    }
    
    private final static String[] gemFont = {
    	" *\n" +
        "* *\n" +
        "***\n" +
        "* *\n" +
        "* *",
        
        "**\n" +
        "* *\n" +
        "**\n" +
        "* *\n" +
        "**",
        
        "***\n" +
        "*\n" +
        "*\n" +
        "*\n" +
        "***",
        
        "**\n" +
        "* *\n" +
        "* *\n" +
        "* *\n" +
        "**",
        
        "***\n" +
        "*\n" +
        "***\n" +
        "*\n" +
        "***",
        
        "***\n" +
        "*\n" +
        "***\n" +
        "*\n" +
        "*",
        
        " ***\n" +
        "*\n" +
        "* **\n" +
        "*  *\n" +
        " ***",
        
        "* *\n" +
        "* *\n" +
        "***\n" +
        "* *\n" +
        "* *",
        
        "***\n" +
        " *\n" +
        " *\n" +
        " *\n" +
        "***",
        
        "  *\n" +
        "  *\n" +
        "  *\n" +
        "* *\n" +
        "***",
        
        "*  *\n" +
        "* *\n" +
        "** \n" +
        "* *\n" +
        "*  *",
        
        "*\n" +
        "*\n" +
        "*\n" +
        "*\n" +
        "***",
        
        "*   *\n" +
        "*   *\n" +
        "** **\n" +
        "* * *\n" +
        "* * *",
        
        "*  *\n" +
        "** *\n" +
        "* **\n" +
        "*  *\n" +
        "*  *",
        
        "***\n" +
        "* *\n" +
        "* *\n" +
        "* *\n" +
        "***",
        
        "**\n" +
        "* *\n" +
        "**\n" +
        "*\n" +
        "*",
        
        "****\n" +
        "*  *\n" +
        "*  *\n" +
        "* *\n" +
        "** *",
        
        "**\n" +
        "* *\n" +
        "**\n" +
        "* *\n" +
        "* *",
        
        "***\n" +
        "*\n" +
        "***\n" +
        "  *\n" +
        "***",
        
        "***\n" +
        " *\n" +
        " *\n" +
        " *\n" +
        " *",
        
        "* *\n" +
        "* *\n" +
        "* *\n" +
        "* *\n" +
        "***",
        
        "* *\n" +
        "* *\n" +
        "* *\n" +
        "* *\n" +
        " *",
        
        "* * *\n" +
        "* * *\n" +
        "** **\n" +
        "** **\n" +
        "*   *",
        
        "* *\n" +
        "* *\n" +
        " *\n" +
        "* *\n" +
        "* *",
        
        "* *\n" +
        "* *\n" +
        "***\n" +
        " *\n" +
        " *",
        
        "***\n" +
        "  *\n" +
        " *\n" +
        "*\n" +
        "***",
        
        "*\n" +
        "*\n" +
        "*\n" +
        "\n" +
        "*\n"
    };
    
    private final static int gemMsg(final int x, final int y, final String msg, final boolean render) {
    	final int size = msg.length();
    	int xc = x;
    	for (int i = 0; i < size; i++) {
    		xc += (1 + gemChr(xc, y, msg.charAt(i), render));
    	}
    	return xc - x - 1;
    }
    
    private final static int gemChr(final int x, int y, final char chr, final boolean render) {
        if (chr == ' ') {
            return 1;
        }
    	final int c = (chr == '!') ? 26 : (java.lang.Character.toUpperCase(chr) - 'A');
    	if (c < 0 || c >= gemFont.length) {
    		return -1;
    	}
    	final String s = gemFont[c];
    	final int size = s.length();
    	if (floorMode == FLOOR_BRIDGE) {
    	    y++;
    	}
    	int xc = x, yc = y + 4, max = 0;
    	for (int i = 0; i < size; i++) {
    		final char t = s.charAt(i);
    		if (t == '\n') {
    			max = Math.max(max, xc - x);
    			xc = x;
    			yc--;
    			continue;
    		} else if (render && t == '*') {
    			gem(xc, yc);
    		}
    		xc++;
    	}
    	return Math.max(max, xc - x);
    }
    
    private final static PixelFilter getHillFilter(final int mode) {
        switch (mode) {
            case 0 : return Map.theme.getHillFilter0();
            case 1 : return Map.theme.getHillFilter1();
            case 2 : return Map.theme.getHillFilter2();
        }
        throw new IllegalArgumentException(String.valueOf(mode));
    }
}