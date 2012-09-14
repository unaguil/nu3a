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

import java.io.InputStream;

import nu3a.render.N3Render;

import org.w3c.dom.Element;

/**
 * Clase que permite cargar texturas en formato RAW
 */
public class N3RawImageLoader implements N3ImageLoader {
	protected int dataFormat;
	protected int width;
	protected int height;
	protected byte[] data;

	/**
	 * Crea una instancia de la clase.
	 */
	public N3RawImageLoader() {
	}

	/**
	 * Crea una instancia de la clase.
	 * 
	 * @param filename
	 *            Path del archivo de la im�gen
	 * @param dataFormat
	 *            Formato de color de la textura
	 * @param width
	 *            Anchura
	 * @param height
	 *            Altura
	 */
	public N3RawImageLoader(String filename, int dataFormat, int width,
			int height) throws Exception {
		this.dataFormat = dataFormat;
		this.width = width;
		this.height = height;
		InputStream is = getClass().getResourceAsStream(filename);
		read(is);
		is.close();
	}

	public byte[] getData() {
		return data;
	}

	public int getDataFormat() {
		return dataFormat;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Obtiene los datos de la textura desde el Stream de entrada
	 */
	public void read(InputStream is) throws Exception {
		int available = is.available();
		data = new byte[available];
		int offset = 0;
		int readbytes;
		while (available > 0) {
			readbytes = is.read(data, offset, available);
			offset += readbytes;
			available -= readbytes;
		}
	}

	/**
	 * M�todo que permite crear una instancia de la clase a partir de un
	 * elemento DOM con su descripci�n XML.
	 * 
	 * @param info
	 *            Descripci�n XML del cargador
	 * @param render
	 *            render con el que se cargar�n las texturas
	 * @return Instancia del cargador
	 */
	public static N3RawImageLoader parseInstance(Element info, N3Render render)
			throws Exception {
		N3RawImageLoader result = new N3RawImageLoader();

		Element data = (Element) info.getElementsByTagName("filename").item(0);
		String path = data.getAttribute("value");

		data = (Element) info.getElementsByTagName("dataformat").item(0);
		result.dataFormat = Integer.parseInt(data.getAttribute("value"));

		data = (Element) info.getElementsByTagName("size").item(0);
		result.width = Integer.parseInt(data.getAttribute("width"));
		result.height = Integer.parseInt(data.getAttribute("height"));

		InputStream is = result.getClass().getResourceAsStream("/" + path);
		result.read(is);
		is.close();

		return result;
	}
}
