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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.PixelGrabber;
import java.io.InputStream;
import java.util.Hashtable;

import nu3a.render.N3Render;

import org.w3c.dom.Element;

/**
 * Clase que permite cargar una im�gen de cualquier tipo de formato soportado
 * por Java.
 */
public class N3JDKImageLoader implements N3ImageLoader, ImageConsumer {

	Image internal;
	PixelGrabber grabber;
	int width, height;
	byte[] data;
	boolean read = false;;

	/**
	 * Crea una instancia de la clase.
	 * 
	 * @param is
	 *            Stream de entrada desde el que se leeran los datos de la
	 *            im�gen
	 */
	public N3JDKImageLoader(InputStream is) {
		try {
			int available = is.available();
			byte[] tmp = new byte[available];
			int offset = 0;
			int readbytes;
			while (available > 0) {
				readbytes = is.read(tmp, offset, available);
				offset += readbytes;
				available -= readbytes;
			}
			internal = Toolkit.getDefaultToolkit().createImage(tmp);
			internal.getSource().startProduction(this);
			while (!read) {
				Thread.currentThread().sleep(20);
			}
		} catch (Exception e) {
			System.out.println("Could not load image.\n");
			e.printStackTrace(System.out);
		}
	}

	public byte[] getData() {
		return data;
	}

	public int getDataFormat() {
		return RGBA;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void imageComplete(int status) {
		try {
			int[] pixels = new int[width * height];
			grabber = new PixelGrabber(internal, 0, 0, width, height, pixels,
					0, width);
			grabber.grabPixels();
			data = new byte[width * height * 4];
			int imagey;
			int imagex;
			int datapos;
			for (int i = 0; i < pixels.length; i++) {
				imagex = i % width;
				imagey = (height - 1) - (int) (i / width);
				datapos = imagex + imagey * width;
				data[4 * datapos] = (byte) ((pixels[i] >> 16) & 0xFF);
				data[4 * datapos + 1] = (byte) ((pixels[i] >> 8) & 0xFF);
				data[4 * datapos + 2] = (byte) ((pixels[i]) & 0xFF);
				data[4 * datapos + 3] = (byte) ((pixels[i] >> 24) & 0xFF);
			}
			read = true;
		} catch (Exception e) {
			System.out.println("Could not load image.\n");
			e.printStackTrace(System.out);
		}
	}

	public void setColorModel(ColorModel model) {
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setHints(int hintflags) {
	}

	public void setPixels(int x, int y, int w, int h, ColorModel model,
			byte[] pixels, int off, int scansize) {
	}

	public void setPixels(int x, int y, int w, int h, ColorModel model,
			int[] pixels, int off, int scansize) {
	}

	public void setProperties(Hashtable props) {
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
	public static N3JDKImageLoader parseInstance(Element info, N3Render render)
			throws Exception {

		Element data = (Element) info.getElementsByTagName("filename").item(0);
		String path = data.getAttribute("value");

		InputStream is = Class
				.forName("nu3a.util.imageLoader.N3JDKImageLoader")
				.getResourceAsStream("/" + path);

		N3JDKImageLoader result = new N3JDKImageLoader(is);

		is.close();

		return result;
	}
}
