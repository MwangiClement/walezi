/*
Copyright (c) 2009-2016, Andrew M. Martin
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
package org.pandcorps.pandam.impl;

import org.pandcorps.core.*;
import org.pandcorps.pandam.*;

public abstract class BasePantity implements Pantity {
	private final String id;

	public BasePantity(final String id) {
		this.id = id;
	}

	@Override
	public final String getId() {
		return id;
	}
	
	public final static String getId(final Pantity entity) {
	    return (entity == null) ? null : entity.getId();
	}
	
	@Override
	public void destroy() {
	    Pangine.getEngine().unregister(this);
	}
	
	public final static void destroy(final Pantity entity) {
	    if (entity != null) {
	    	entity.destroy();
	    }
	}
	
	public final static void destroyAll(final Pantity... entities) {
	    if (entities == null) {
	        return;
	    }
	    for (final Pantity entity : entities) {
	        destroy(entity);
	    }
	}
	
	public final static void destroyAll(final Panimation animation) {
	    if (animation != null) {
	        animation.destroyAll();
	    }
	}
	
	public final static boolean contains(final Panimation animation, final Panmage image) {
	    if (animation == null) {
	        return false;
	    }
	    for (final Panframe frame : animation.getFrames()) {
	        if (frame.getImage() == image) {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override
	public String toString() {
		return Pantil.getSimpleName(getClass()) + "(" + id + ")";
	}
}
