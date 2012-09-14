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

package nu3a.util.imageLoader;

/**
 * Define las caracteristicas de un cargador de imagenes.
 */
public interface N3ImageLoader {
	// Formato de la informaci�n de textura.
	final static int RGB = 0;
	final static int RGBA = 1;

	// Tipo de la informaci�n de textura.
	final static int UNSIGNED_BYTE = 0;

	/**
	 * Permite obtener la informacion cargada del InputStream.
	 */
	public byte[] getData();

	/**
	 * Indica el tipo de datos de la imagen leida.
	 * 
	 * @return Formato de la informaci�n
	 */
	public int getDataFormat();

	/**
	 * Permite obtener el ancho de la imagen.
	 */
	public int getWidth();

	/**
	 * Permite obtener el alto de la imagen.
	 */
	public int getHeight();
}
