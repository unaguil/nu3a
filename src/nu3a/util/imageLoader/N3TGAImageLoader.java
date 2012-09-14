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

import java.io.DataInputStream;
import java.io.InputStream;

import nu3a.render.N3Render;

import org.w3c.dom.Element;

class TGATextureLoader {
	byte[] pixel;
	boolean error;
	int imageWidth, imageHeight;

	public TGATextureLoader() {
		error = false;
	}

	public boolean isOk() {
		return !error;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public byte[] getTexture() {
		return pixel;
	}

	protected boolean readTexture(InputStream is) {
		try {
			int cc;

			DataInputStream reader = new DataInputStream(is);

			// write TGA header
			reader.readByte(); // ID length, 0 because no image id field
			reader.readByte(); // no color map
			cc = reader.readByte(); // image type (24 bit RGB, uncompressed)
			if (cc != 2) {
				reader.close();
				System.out
						.println("TGATextureLoader: File is not 24bit RGB Data !");
				error = true;
				return false;
			}
			reader.readShort(); // color map origin, ignore because no color map
			reader.readShort(); // color map length, ignore because no color map
			reader.readByte(); // color map entry size, ignore because no color
								// map
			reader.readShort(); // x origin
			reader.readShort(); // y origin

			cc = reader.readByte(); // image width low byte
			short s = (short) ((short) cc & 0x00ff);
			cc = reader.readByte(); // image width high byte
			s = (short) ((short) (((short) cc & 0x00ff) << 8) | s);
			imageWidth = (int) s;

			cc = reader.readByte(); // image height low byte
			s = (short) ((short) cc & 0x00ff);
			cc = reader.readByte(); // image height high byte
			s = (short) ((short) (((short) cc & 0x00ff) << 8) | s);
			imageHeight = (int) s;

			cc = reader.readByte(); // 24bpp
			if (cc != 24) {
				reader.close();
				System.out
						.println("TGATextureLoader: File is not 24bpp Data !");
				error = true;
				return false;
			}
			reader.readByte(); // description bits

			pixel = new byte[imageWidth * imageHeight * 3];

			// read TGA image data
			// reader.read(pixel, 0, pixel.length);

			int available = pixel.length;
			int offset = 0;
			int readbytes;
			while (available > 0) {
				readbytes = is.read(pixel, offset, available);
				offset += readbytes;
				available -= readbytes;
			}

			// process image data:
			// TGA pixels should be written in BGR format,
			// so R en B should be switched
			byte tmp;
			for (int i = 0; i < imageWidth * imageHeight * 3; i += 3) {
				tmp = pixel[i];
				pixel[i] = pixel[i + 2];
				pixel[i + 2] = tmp;
			}

			reader.close();
			return true;

		} catch (Exception ex) {
			System.out
					.println("An exception occured, while loading a TGATexture");
			System.out.println(ex);
			error = true;
		}
		return false;
	}
}

/**
 * Clase que permite cargar texturas TGA.
 */
public class N3TGAImageLoader implements N3ImageLoader {

	TGATextureLoader loader;
	int width, height;
	byte[] data;

	/**
	 * Constructor de la clase.
	 * 
	 * @param is
	 *            Stream del que se leer� la textura
	 */
	public N3TGAImageLoader(InputStream is) {
		loader = new TGATextureLoader();
		data = readImage(is);
	}

	protected byte[] readImage(InputStream is) {
		loader.readTexture(is);
		width = loader.getImageWidth();
		height = loader.getImageWidth();
		return loader.getTexture();
	}

	public byte[] getData() {
		return data;
	}

	public int getDataFormat() {
		return RGB;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
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
	public static N3TGAImageLoader parseInstance(Element info, N3Render render)
			throws Exception {

		Element data = (Element) info.getElementsByTagName("filename").item(0);
		String path = data.getAttribute("value");

		InputStream is = Class
				.forName("nu3a.util.imageLoader.N3TGAImageLoader")
				.getResourceAsStream("/" + path);

		N3TGAImageLoader result = new N3TGAImageLoader(is);

		is.close();

		return result;
	}
}
