/*
 *	Copyright (c) 2003 Jorge García, Unai Aguilera
 *
 *	This file is part of Nu3A.
 *
 *   Nu3A is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   Nu3A is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Nu3A.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *	Authors: Jorge García <bardok@gmail.com>, Unai Aguilera <gkalgan@gmail.com>
 */

package nu3a.material.texture;

import nu3a.names.exception.N3NameException;
import nu3a.render.N3Render;
import nu3a.scene.N3Scene;
import nu3a.util.imageLoader.N3ImageLoader;

public class N3Texture1D extends N3Texture {
	/**
	 * Ancho en pixels de la textura.
	 */
	protected int width;

	/**
	 * Constructor de la textura 2D.
	 */
	public N3Texture1D(N3Scene scene, String name) throws N3NameException {
		super(scene, name);
		width = -1;
	}

	public void genTexture(N3ImageLoader loader, N3Render render) {
		System.out.println("Not implemented yet,:)");
	}

	public void genTexture(byte[] data, int dataFormat, int width, int height,
			N3Render render) {
		System.out.println("Not implemented yet, :)");
	}

	/**
	 * Permite obtener el ancho de la textura.
	 * 
	 * @return Ancho de la textura.
	 */
	public int getWidth() {
		return width;
	}
}
