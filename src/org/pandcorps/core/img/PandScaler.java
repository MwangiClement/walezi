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
package org.pandcorps.core.img;

import java.awt.image.*;

import org.pandcorps.core.Imtil;

/*
If we have this input:

.X.
..X
...

We want this output:

..XX..
..XX..
...XXX
....XX
......
......

But if we have this input:

OX.
.OX
...

We can get this output:

OOXX..
OOOX..
..OXXX
..OOXX
......
......

If X is an edge against an O background:

OX.
OOX
...

We want this:

OOXX..
OOXX..
OOOXXX
OOOOXX
......
......

If X is the background:

OXX
.OX
...

We want this:

OOXXXX
OOOXXX
..OOXX
..OOXX
......
......

If it's unclear:

OXX
OOX
...

Could favor the darker color as the edge.

****************************************************************************************************

Similar issues for this:

.X.
.OX
..O

****************************************************************************************************

Ideal case for this:

X..
.X.
..X

Is:

XX....
XXX...
.XXX..
..XXX.
...XXX
....XX

Ideal case for this:

XX....
..XX..
....XX

Is:

XXXX........
XXXXXX......
..XXXXXX....
....XXXXXX..
......XXXXXX
........XXXX

Rule for this:

...
X..
.XX

Is?

......
......
XX....
XXXX..
..XXXX
..XXXX

Rule for this:

XX.
..X
...

Is?

XXXX..
XXXX..
..XXXX
....XX
......
......
*/
public class PandScaler {
    
    protected BufferedImage scale(final BufferedImage in) {
        final int w = in.getWidth(), h = in.getHeight();
        final int w1 = w - 1, h1 = h - 1;
        final BufferedImage out = new BufferedImage(w * 2, h * 2, Imtil.TYPE);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final int out0, out1, out2, out3;
                final int xm1 = x > 0 ? x - 1 : x, xp1 = x < w1 ? x + 1 : x;
                final int ym1 = y > 0 ? y - 1 : y, yp1 = y < h1 ? y + 1 : y;
                final int ina = in.getRGB(xm1, ym1);
                final int inb = in.getRGB(x, ym1);
                final int inc = in.getRGB(xp1, ym1);
                final int ind = in.getRGB(xm1, y);
                final int ine = in.getRGB(x, y);
                final int inf = in.getRGB(xp1, y);
                final int ing = in.getRGB(xm1, yp1);
                final int inh = in.getRGB(x, yp1);
                final int ini = in.getRGB(xp1, yp1);
                if (inb != inh && ind != inf) {
                	//out0 = ind == inb ? ind : ine;
                	if (ind == inb) {
                		if (ind == ine) {
                			out0 = ine;
                		} else if (ine == inc || ine == ing) {
                			final boolean outBg = ina == inb;
                			final boolean inBg = ine == inc && ine == inf || ine == ing && ine == inh;
                			if (inBg && !outBg) {
                				out0 = ind;
                			} else if (!inBg && !outBg) {
                				out0 = ine;
                			} else if (darker(ind, ine)) {
                				out0 = ind;
                			} else {
                				out0 = ine;
                			}
                		} else {
                			out0 = ind;
                		}
                	} else {
                    	out0 = ine;
                	}
                    //out1 = inb == inf ? inf : ine;
                	if (inb == inf) {
                		if (inb == ine) {
                			out1 = ine;
                		} else if (ine == ina || ine == ini) {
                			final boolean outBg = inc == inb;
                			final boolean inBg = ine == ina && ine == ind || ine == ini && ine == inh;
                			if (inBg && !outBg) {
                				out1 = inb;
                			} else if (!inBg && !outBg) {
                				out1 = ine;
                			} else if (darker(inb, ine)) {
                				out1 = inb;
                			} else {
                				out1 = ine;
                			}
                		} else {
                			out1 = inb;
                		}
                	} else {
                    	out1 = ine;
                	}
                    //out2 = ind == inh ? ind : ine;
                	if (ind == inh) {
                		if (ind == ine) {
                			out2 = ine;
                		} else if (ine == ina || ine == ini) {
                			final boolean outBg = ind == ing;
                			final boolean inBg = ine == ina && ine == inb || ine == ini && ine == inf;
                			if (inBg && !outBg) {
                				out2 = ind;
                			} else if (!inBg && !outBg) {
                				out2 = ine;
                			} else if (darker(ind, ine)) {
                				out2 = ind;
                			} else {
                				out2 = ine;
                			}
                		} else {
                			out2 = ind;
                		}
                	} else {
                    	out2 = ine;
                	}
                    //out3 = inh == inf ? inf : ine;
                	if (inh == inf) {
                		if (inh == ine) {
                			out3 = ine;
                		} else if (ine == inc || ine == ing) {
                			final boolean outBg = inf == ini;
                			final boolean inBg = ine == inc && ine == inb || ine == ing && ine == ind;
                			if (inBg && !outBg) {
                				out3 = inh;
                			} else if (!inBg && !outBg) {
                				out3 = ine;
                			} else if (darker(inh, ine)) {
                				out3 = inh;
                			} else {
                				out3 = ine;
                			}
                		} else {
                			out3 = inh;
                		}
                	} else {
                    	out3 = ine;
                	}
                } else {
                    out0 = ine;
                    out1 = ine;
                    out2 = ine;
                    out3 = ine;
                }
                out.setRGB(x * 2, y * 2, out0);
                out.setRGB(x * 2 + 1, y * 2, out1);
                out.setRGB(x * 2, y * 2 + 1, out2);
                out.setRGB(x * 2 + 1, y * 2 + 1, out3);
            }
        }
        return out;
    }
    
    private final static boolean darker(final int o, final int i) {
    	return o < i; // Make better
    }
    
    public final static void main(final String[] args) {
        try {
            final String name = args[0];
            final PandScaler scaler = new PandScaler();
            Imtil.save(scaler.scale(Imtil.load(name)), name + ".Pand.png");
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }
}
