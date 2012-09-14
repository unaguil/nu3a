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

package nu3a.render.software;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.Vector;

import nu3a.geometry.N3Point3D;
import nu3a.material.color.N3ColorRGBA;
import nu3a.material.texture.N3TexCoord2D;

/**
 * Clase que proporciona el contexto de dibujado para renderizar la escena a
 * trav�s de una interfaz de renderizado software. Crea el contexto para un
 * determinado componente, y lo bloquea, permitiendo dibujar en �ste a la
 * librer�a Nu3A a trav�s del API que proporciona.
 */
public class N3SoftwareRenderContext implements ImageProducer {

	private class N3InternalTexture {
		byte[] data;
		int dataFormat;
		int dataType;
		int width;
		int height;
	}

	private static final int N3_SWR_STATUS_INIT = 0;

	private static final int N3_SWR_STATUS_DRAW_VERTEXES = 1;
	private static final int N3_SWR_STATUS_DRAW_LINES = 2;
	private static final int N3_SWR_STATUS_DRAW_TRIANGLES = 3;
	private static final int N3_SWR_STATUS_DRAW_POLYGON = 4;

	public static final int N3_POINTS = 100;
	public static final int N3_LINES = 101;
	public static final int N3_TRIANGLES = 102;
	public static final int N3_POLYGON = 103;

	public static final int N3_SWR_REPLACE = 200;
	public static final int N3_SWR_MODULATE = 201;

	public static final int N3_RGBA = 21;
	public static final int N3_RGB = 22;

	/**
	 * Componente para el que se proporciona el contexto de renderizado.
	 */
	protected Component component;

	/**
	 * Indica si el dibujado se realiza a trav�s de un doble buffer.
	 */
	protected boolean doubleBuffer;

	/**
	 * Pixels del contexto de redibujado.
	 */
	protected int[] pixels;

	/**
	 * Z Buffer, para determiterminar si un p�xel debe o no dibujarse en
	 * pantalla
	 */
	protected float[] zBuffer;

	/**
	 * Im�gen en la que se dibujar� el contenido renderizado.
	 */
	protected Image image;

	/**
	 * Anchura del contexto de dibujado.
	 */
	protected int height;

	/**
	 * Altura del contexto de dibujado.
	 */
	protected int width;

	/**
	 * Tama�o en p�xels del contexto de dibujado.
	 */
	protected int size;

	/**
	 * �ltimo color asignado.
	 */
	private N3ColorRGBA lastColor;

	/**
	 * �ltima coordenada de textura asignada.
	 */
	private N3TexCoord2D lastUV;

	/**
	 * �ltimo v�rtice asignado.
	 */
	private N3Point3D lastVertex;

	/**
	 * Almac�n de vertices no dibujados
	 */
	protected N3Point3D[] vertexPool;

	/**
	 * Almac�n de los colores de los v�rtices no dibujados
	 */
	protected N3ColorRGBA[] colorPool;

	/**
	 * Almac�n de las coordenadas de textura de los v�rtices no dibujados
	 */
	protected N3TexCoord2D[] uvPool;

	/**
	 * N�mero de v�rtices asignados
	 */
	private int nVertex;

	/**
	 * Indica si es necesario hacer una actualizaci�n de la im�gen antes del
	 * repintado.
	 */
	private boolean needUpdate;

	/**
	 * Establece si para dibujar se utilizar� el test de profundidad.
	 */
	protected boolean depthtest;

	/**
	 * Establece si para dibujar se utilizar�n texturas
	 */
	protected boolean texturing;

	/**
	 * �ndice de la textura activa
	 */
	protected int selectedTexture;

	/**
	 * �ndice de la textura activa
	 */
	protected int textureMode;

	/**
	 * Modelo de color utilizado para indicar al sistema el modelo de color que
	 * se est� utilizando. Los colores se guardar�n en formato RGBA en enteros
	 * de 32 bits, de modo que los 2 bytes de menor peso corresponden al color
	 * rojo, los dos siguientes al verde, los siguientes a al az�l, y los de
	 * mayor peso al canal Alpha.
	 */
	protected ColorModel colormodel;

	/**
	 * Consumidor de los p�xels de la im�gen. Ser� a traves de este objeto
	 * mediante el que se actualizar� la im�gen interna del contexto. Este
	 * contexto es creado y asignado internamente por el sistema, y manejado a
	 * trav�s de los m�todos de la interfaz ImageProducer.
	 */
	private ImageConsumer consumer;

	/**
	 * Estado en el que se encuentra el contexto.
	 */
	private int status;

	/**
	 * Vector de texturas para este contexto
	 */
	protected Vector textures;

	/**
	 * Crea un contexto de renderizado para el componente. Si se especifica, el
	 * dibujado se realizar� a trav�s de un doble-buffer para evitar parpadeos
	 * en el dibujado.
	 * 
	 * @param c
	 *            Component sobre el que se construir� el contexto
	 * @param dBuffer
	 *            Indica si se utilizar� o no Doble Buffer
	 */
	public N3SoftwareRenderContext(Component c, boolean dBuffer) {
		component = c;
		component.setIgnoreRepaint(true);
		width = component.getWidth();
		height = component.getHeight();
		size = width * height;
		pixels = new int[size];
		zBuffer = new float[size];
		image = Toolkit.getDefaultToolkit().createImage(this);
		colormodel = new DirectColorModel(32, 0x000000FF, 0x0000FF00,
				0x00FF0000, 0xFF000000);
		consumer = null;
		lastColor = new N3ColorRGBA(1, 1, 1);
		lastUV = new N3TexCoord2D(0, 0);
		lastVertex = null;
		status = N3_SWR_STATUS_INIT;
		needUpdate = true;
		depthtest = false;
		textures = new Vector(0, 1);
		selectedTexture = -1;
		textureMode = N3_SWR_REPLACE;
	}

	/**
	 * Obtiene la anchura del contexto.
	 * 
	 * @return Anchura del contexto
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Obtiene la altura del contexto.
	 * 
	 * @return Altura del contexto
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Establece las caracter�sticas del render seg�n su estado.
	 */
	protected void stablishStatusEnvironment() {
		switch (status) {
		case (N3_SWR_STATUS_DRAW_VERTEXES):
			break;
		case (N3_SWR_STATUS_DRAW_LINES):
			nVertex = 0;
			vertexPool = new N3Point3D[2];
			colorPool = new N3ColorRGBA[2];
			uvPool = new N3TexCoord2D[2];
			break;
		case (N3_SWR_STATUS_DRAW_TRIANGLES):
		case (N3_SWR_STATUS_DRAW_POLYGON):
			nVertex = 0;
			vertexPool = new N3Point3D[3];
			colorPool = new N3ColorRGBA[3];
			uvPool = new N3TexCoord2D[3];
			break;
		}
	}

	/**
	 * Actualiza el contenido del componente con la im�gen creada en el contexto
	 * de renderizado.
	 */
	public synchronized void paint() {
		if (needUpdate)
			update();
		component.getGraphics().drawImage(image, 0, 0, null);
	}

	/**
	 * Actualiza la im�gen interna con los datos actuales.
	 */
	public synchronized void update() {
		if (consumer != null) {
			consumer.setPixels(0, 0, width, height, colormodel, (int[]) pixels,
					0, width);
			consumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
			needUpdate = false;
		} else
			needUpdate = true;
	}

	/**
	 * Establece el modo de dibujado (puntos, l�neas, tri�ngulos).
	 */
	public void beginDrawingMode(int drawMode) {
		switch (status) {
		case (N3_SWR_STATUS_INIT):
			switch (drawMode) {
			case (N3_POINTS):
				status = N3_SWR_STATUS_DRAW_VERTEXES;
				stablishStatusEnvironment();
				break;
			case (N3_LINES):
				status = N3_SWR_STATUS_DRAW_LINES;
				stablishStatusEnvironment();
				break;
			case (N3_TRIANGLES):
				status = N3_SWR_STATUS_DRAW_TRIANGLES;
				stablishStatusEnvironment();
				break;
			case (N3_POLYGON):
				status = N3_SWR_STATUS_DRAW_POLYGON;
				stablishStatusEnvironment();
				break;
			}
			break;
		default:
			break;
		}
	}

	public void endDrawingMode() {
		switch (status) {
		case (N3_SWR_STATUS_DRAW_VERTEXES):
		case (N3_SWR_STATUS_DRAW_LINES):
		case (N3_SWR_STATUS_DRAW_TRIANGLES):
		case (N3_SWR_STATUS_DRAW_POLYGON):
			status = N3_SWR_STATUS_INIT;
			break;
		}
	}

	/**
	 * Procesa el �ltimo v�rtice, teniendo en cuenta el estado, y el n�mero de
	 * v�rtices asignados anteriormente. Si procede, dibujar� los �ltimos
	 * v�rtices a�adidos al render.
	 */
	protected void processVertex() {
		switch (status) {
		case (N3_SWR_STATUS_DRAW_VERTEXES):
			drawFlatVertex();
			break;
		case (N3_SWR_STATUS_DRAW_LINES):
			vertexPool[nVertex] = lastVertex;
			colorPool[nVertex] = lastColor;
			nVertex++;
			if (nVertex == 2) {
				/* dibujado de la l�nea con el pool */
				int color1 = colorPool[0].getPackedValue();
				int color2 = colorPool[1].getPackedValue();
				int y1 = (int) vertexPool[0].y;
				int y2 = (int) vertexPool[1].y;
				if (color1 == color2) {
					if (y1 == y2)
						drawFlatHorizontalLine();
					else {
						int x1 = (int) vertexPool[0].x;
						int x2 = (int) vertexPool[1].x;
						if (x1 == x2)
							drawFlatVerticalLine();
						else {
							int dx = x2 - x1;
							int dy = y2 - y1;
							dx = (dx < 0) ? -dx : dx;
							dy = (dy < 0) ? -dy : dy;
							if (dx > dy)
								drawFlatHorizontalIncLine();
							else
								drawFlatVerticalIncLine();
						}
					}
				} else {
					if (y1 == y2)
						drawGoraudHorizontalLine();
					else {
						int x1 = (int) vertexPool[0].x;
						int x2 = (int) vertexPool[1].x;
						if (x1 == x2)
							drawGoraudVerticalLine();
						else {
							int dx = x2 - x1;
							int dy = y2 - y1;
							dx = (dx < 0) ? -dx : dx;
							dy = (dy < 0) ? -dy : dy;
							if (dx > dy)
								drawGoraudHorizontalIncLine();
							else
								drawGoraudVerticalIncLine();
						}
					}
				}
				nVertex = 0;
			}
			break;
		case (N3_SWR_STATUS_DRAW_TRIANGLES):
			vertexPool[nVertex] = lastVertex;
			colorPool[nVertex] = lastColor;
			uvPool[nVertex] = lastUV;
			nVertex++;
			if (nVertex == 3) {
				/* dibujado del tri�ngulo con el pool */
				int color1 = colorPool[0].getPackedValue();
				int color2 = colorPool[1].getPackedValue();
				int color3 = colorPool[2].getPackedValue();
				if ((color1 == color2) && (color2 == color3)) {
					if ((!texturing) || (selectedTexture >= textures.size())
							|| selectedTexture == -1)
						drawFlatTriangle();
					else if (textureMode == N3_SWR_REPLACE)
						drawReplaceTexturedTriangle();
					else
						drawFlatTexturedTriangle();
				} else {
					if ((!texturing) || (selectedTexture >= textures.size())
							|| selectedTexture == -1)
						drawGoraudTriangle();
					else if (textureMode == N3_SWR_REPLACE)
						drawReplaceTexturedTriangle();
					else
						drawGoraudTexturedTriangle();
				}
				nVertex = 0;
			}
			break;
		case (N3_SWR_STATUS_DRAW_POLYGON):
			vertexPool[nVertex] = lastVertex;
			colorPool[nVertex] = lastColor;
			uvPool[nVertex] = lastUV;
			nVertex++;
			if (nVertex == 3) {
				/* dibujado del tri�ngulo con el pool */
				int color1 = colorPool[0].getPackedValue();
				int color2 = colorPool[1].getPackedValue();
				int color3 = colorPool[2].getPackedValue();
				if ((color1 == color2) && (color2 == color3)) {
					if ((!texturing) || (selectedTexture >= textures.size())
							|| selectedTexture == -1)
						drawFlatTriangle();
					else if (textureMode == N3_SWR_REPLACE)
						drawReplaceTexturedTriangle();
					else
						drawFlatTexturedTriangle();
				} else {
					if ((!texturing) || (selectedTexture >= textures.size())
							|| selectedTexture == -1)
						drawGoraudTriangle();
					else if (textureMode == N3_SWR_REPLACE)
						drawReplaceTexturedTriangle();
					else
						drawGoraudTexturedTriangle();
				}
				nVertex = 2;
				vertexPool[1] = vertexPool[2];
				colorPool[1] = colorPool[2];
				uvPool[1] = uvPool[2];
			}
			break;
		}
	}

	/**
	 * Establece el color del pr�ximo v�rtice a dibujar.
	 * 
	 * @param color
	 *            Color del pr�ximo v�rtice a dibujar
	 */
	public void setColor(N3ColorRGBA color) {
		lastColor = color;
	}

	/**
	 * Establece las coordenadas de textura del pr�ximo v�rtice a dibujar.
	 * 
	 * @param uv
	 *            Coordenadas de textura del pr�ximo v�rtice a dibujar
	 */
	public void setUV(N3TexCoord2D uv) {
		lastUV = uv;
	}

	/**
	 * Establece el pr�ximo v�rtice a dibujar
	 * 
	 * @param vertex
	 *            Pr�ximo v�rtice a dibujar
	 */
	public void setVertex(N3Point3D vertex) {
		lastVertex = vertex;
		processVertex();
	}

	/**
	 * Inicializa el contenido del z buffer a la distancia m�xima.
	 */
	public void cleanZBuffer() {
		if (status == N3_SWR_STATUS_INIT) {
			if (size > 0) {
				int half = size >> 1;
				zBuffer[0] = 1;

				for (int i = 1; i < size;) {
					System.arraycopy(zBuffer, 0, zBuffer, i, (i < half) ? i
							: size - i);
					i <<= 1;
				}
			}
		}
	}

	/**
	 * Establece si el dibujado se realizar� utilizando el test de profundidad
	 * 
	 * @param test
	 *            true en el caso de que se desee realizar; false en caso
	 *            contrario
	 */
	public void setDepthTest(boolean test) {
		depthtest = test;
	}

	/**
	 * Obtiene si se est� realizando el test de profundidad.
	 * 
	 * @return true si se est� realizando; false en caso contrario
	 */
	public boolean getDepthTest() {
		return depthtest;
	}

	/**
	 * Establece si se utilizar�n o no texturas.
	 * 
	 * @param value
	 *            True para utilizar texturas; false en caso contrario
	 */
	public void setTexturing(boolean value) {
		texturing = value;
	}

	/**
	 * Obtiene si se est�n utilizando texturas.
	 * 
	 * @return True si es est�n utilizando texturas; false en caso contrario
	 */
	public boolean getTextuting() {
		return texturing;
	}

	/**
	 * A�ade una textura al contexto.
	 * 
	 * @param t
	 *            Textura a a�adir
	 * @return C�digo de la textura dentro del contexto
	 */
	public int createTexture2D(byte[] data, int dataFormat, int dataType,
			int width, int height) {
		N3InternalTexture t = new N3InternalTexture();
		if (dataFormat == N3_RGB) {
			t.data = new byte[data.length + width * height];
			int j = 0;
			for (int i = 0; i < data.length; i += 3) {
				t.data[j++] = data[i];
				t.data[j++] = data[i + 1];
				t.data[j++] = data[i + 2];
				t.data[j++] = -1;
			}
		} else {
			t.data = new byte[data.length];
			System.arraycopy(data, 0, t.data, 0, data.length);
		}
		t.dataFormat = dataFormat;
		t.dataType = dataType;
		t.width = width;
		t.height = height;
		textures.add(t);
		return textures.size() - 1;
	}

	/**
	 * Selecciona la textura activa
	 * 
	 * @param index
	 *            �ndice de la textura
	 */
	public void selectTexture(int index) {
		selectedTexture = index;
	}

	/**
	 * Especif�ca el modo de textura.
	 * 
	 * @param mode
	 *            Modo de aplicaci�n de la textura
	 */
	public void setTextureMode(int mode) {
		textureMode = mode;
	}

	/**
	 * Indica si un punto deber�a de dibujarse, teniendo en cuenta si hay otros
	 * puntos entre �l y la c�mara. En el caso de que el z buffer est� activo y
	 * la comprobaci�n sea verdadera, se actualiza el contenido del zBuffer;
	 * 
	 * @param index
	 *            Posici�n de la im�gen (de manera lineal) en la que se
	 *            dibujar�a el punto
	 * @param z
	 *            Profundidad del punto
	 * @return true si hay que dibujar el punto. False en caso contrario.
	 */
	public boolean depthTest(int index, float z) {
		if (!depthtest)
			return (z >= 0.0f);
		else if (zBuffer[index] > z) {
			zBuffer[index] = z;
			return true;
		}
		return false;
	}

	/*---------------------------------------------------------------------------------
	M�todos de dibujado
	---------------------------------------------------------------------------------*/

	/**
	 * Dibuja un v�rtice con el �ltimo color especificado.
	 */
	public void drawFlatVertex() {
		int x, y;
		x = (int) lastVertex.x;
		y = (int) lastVertex.y;
		int color = lastColor.getPackedValue();
		if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
			float z = lastVertex.z;
			int pos = x + y * width;
			if (depthTest(pos, z))
				pixels[pos] = color;
			needUpdate = true;
		}
	}

	/**
	 * Dibuja una l�nea horizontal con el �ltimo color especificado.
	 */
	public void drawFlatHorizontalLine() {
		int y = (int) vertexPool[0].y;
		if ((y >= 0) && (y < height)) {
			int color = lastColor.getPackedValue();
			int x1 = (int) vertexPool[0].x;
			int x2 = (int) vertexPool[1].x;
			float z1 = vertexPool[0].z;
			float z2 = vertexPool[1].z;
			if (x1 > x2) {
				int tmpx = x2;
				x2 = x1;
				x1 = tmpx;
				float tmpz = z2;
				z2 = z1;
				z1 = tmpz;
			}
			int pos = x1 + width * y;
			float dz;
			if ((int) (x2 - x1) != 0)
				dz = (float) (z2 - z1) / (x2 - x1);
			else
				dz = 0;
			float z = z1;
			for (int x = x1; x <= x2; x++) {
				if ((x >= 0) && (x < width)) {
					if (depthTest(pos, z))
						pixels[pos] = color;
				}
				z += dz;
				pos++;
			}
			needUpdate = true;
		}
	}

	/**
	 * Dibuja una l�nea vertical con el �ltimo color especificado.
	 */
	public void drawFlatVerticalLine() {
		int x = (int) vertexPool[0].x;
		if ((x >= 0) && (x < width)) {
			int color = lastColor.getPackedValue();
			int y1 = (int) vertexPool[0].y;
			int y2 = (int) vertexPool[1].y;
			float z1 = vertexPool[0].z;
			float z2 = vertexPool[1].z;
			if (y1 > y2) {
				int tmpy = y2;
				y2 = y1;
				y1 = tmpy;
				float tmpz = z2;
				z2 = z1;
				z1 = tmpz;
			}
			int pos = x + width * y1;
			float dz;
			if ((int) (y2 - y1) != 0)
				dz = (float) (z2 - z1) / (y2 - y1);
			else
				dz = 0;
			float z = z1;
			for (int y = y1; y <= y2; y++) {
				if ((y >= 0) && (y < height)) {
					if (depthTest(pos, z))
						pixels[pos] = color;
				}
				z += dz;
				pos += width;
			}
			needUpdate = true;
		}
	}

	/**
	 * Dibuja una l�nea horizontal con el �ltimo color especificado, en la cual
	 * la x aumenta m�s r�pidamente que la y.
	 */
	public void drawFlatHorizontalIncLine() {
		int color = lastColor.getPackedValue();
		int x1 = (int) vertexPool[0].x;
		int x2 = (int) vertexPool[1].x;
		int y1 = (int) vertexPool[0].y;
		int y2 = (int) vertexPool[1].y;
		float z1 = vertexPool[0].z;
		float z2 = vertexPool[1].z;
		int pos;
		if (x1 > x2) {
			int tmpx = x2;
			x2 = x1;
			x1 = tmpx;
			int tmpy = y2;
			y2 = y1;
			y1 = tmpy;
			float tmpz = z2;
			z2 = z1;
			z1 = tmpz;
		}
		float dz, dy;
		if ((int) (x2 - x1) != 0) {
			dz = (float) (z2 - z1) / (x2 - x1);
			dy = (float) (y2 - y1) / (x2 - x1);
		} else {
			dz = 0;
			dy = 0;
		}
		float z = z1;
		float y = y1;
		for (int x = x1; x <= x2; x++) {
			if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
				pos = x + width * (int) y;
				if (depthTest(pos, z))
					pixels[pos] = color;
			}
			z += dz;
			y += dy;
		}
		needUpdate = true;
	}

	/**
	 * Dibuja una l�nea vertical con el �ltimo color especificado, en la cual la
	 * x aumenta m�s r�pidamente que la y.
	 */
	public void drawFlatVerticalIncLine() {
		int color = lastColor.getPackedValue();
		int x1 = (int) vertexPool[0].x;
		int x2 = (int) vertexPool[1].x;
		int y1 = (int) vertexPool[0].y;
		int y2 = (int) vertexPool[1].y;
		float z1 = vertexPool[0].z;
		float z2 = vertexPool[1].z;
		int pos;
		if (y1 > y2) {
			int tmpx = x2;
			x2 = x1;
			x1 = tmpx;
			int tmpy = y2;
			y2 = y1;
			y1 = tmpy;
			float tmpz = z2;
			z2 = z1;
			z1 = tmpz;
		}
		float dz, dx;
		if ((int) (y2 - y1) != 0) {
			dz = (float) (z2 - z1) / (y2 - y1);
			dx = (float) (x2 - x1) / (y2 - y1);
		} else {
			dz = 0;
			dx = 0;
		}
		float z = z1;
		float x = x1;
		for (int y = y1; y <= y2; y++) {
			if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
				pos = (int) x + width * y;
				if (depthTest(pos, z))
					pixels[pos] = color;
			}
			z += dz;
			x += dx;
		}
		needUpdate = true;
	}

	/**
	 * Dibuja una l�nea horizontal, interpolando los colores de los v�rtices.
	 */
	public void drawGoraudHorizontalLine() {
		int y = (int) vertexPool[0].y;
		if ((y >= 0) && (y < height)) {
			int c1 = colorPool[0].getPackedValue();
			int c2 = colorPool[1].getPackedValue();
			int x1 = (int) vertexPool[0].x;
			int x2 = (int) vertexPool[1].x;
			float z1 = vertexPool[0].z;
			float z2 = vertexPool[1].z;
			if (x1 > x2) {
				int tmpx = x2;
				x2 = x1;
				x1 = tmpx;
				int tmpc = c2;
				c2 = c1;
				c1 = tmpc;
				float tmpz = z2;
				z2 = z1;
				z1 = tmpz;
			}
			int pos = x1 + width * y;
			float dz, dr, dg, db, da;
			if ((int) (x2 - x1) != 0) {
				dz = (float) (z2 - z1) / (x2 - x1);
				dr = (float) ((c2 & 0x000000FF) - (c1 & 0x000000FF))
						/ (x2 - x1);
				dg = (float) (((c2 & 0x0000FF00) >> 8) - ((c1 & 0x0000FF00) >> 8))
						/ (x2 - x1);
				db = (float) (((c2 & 0x00FF0000) >> 16) - ((c1 & 0x00FF0000) >> 16))
						/ (x2 - x1);
				da = (float) (((c2 & 0xFF000000) >> 24) - ((c1 & 0xFF000000) >> 24))
						/ (x2 - x1);
			} else {
				dz = 0;
				dr = 0;
				dg = 0;
				db = 0;
				da = 0;
			}
			float z = z1;
			float r = (c1 & 0x000000FF);
			float g = (c1 & 0x0000FF00) >> 8;
			float b = (c1 & 0x00FF0000) >> 16;
			float a = (c1 & 0xFF000000) >> 24;
			int color = c1;
			for (int x = x1; x <= x2; x++) {
				if ((x >= 0) && (x < width)) {
					if (depthTest(pos, z))
						pixels[pos] = color;
				}
				z += dz;
				r += dr;
				g += dg;
				b += db;
				a += da;
				color = (int) r + ((int) g << 8) + ((int) b << 16)
						+ ((int) a << 24);
				pos++;
			}
			needUpdate = true;
		}
	}

	/**
	 * Dibuja una l�nea vertical, interpolando los colores de los v�rtices.
	 */
	public void drawGoraudVerticalLine() {
		int x = (int) vertexPool[0].x;
		if ((x >= 0) && (x < width)) {
			int c1 = colorPool[0].getPackedValue();
			int c2 = colorPool[1].getPackedValue();
			int y1 = (int) vertexPool[0].y;
			int y2 = (int) vertexPool[1].y;
			float z1 = vertexPool[0].z;
			float z2 = vertexPool[1].z;
			if (y1 > y2) {
				int tmpy = y2;
				y2 = y1;
				y1 = tmpy;
				int tmpc = c2;
				c2 = c1;
				c1 = tmpc;
				float tmpz = z2;
				z2 = z1;
				z1 = tmpz;
			}
			int pos = x + width * y1;
			float dz, dr, dg, db, da;
			if ((int) (y2 - y1) != 0) {
				dz = (float) (z2 - z1) / (y2 - y1);
				dr = (float) ((c2 & 0x000000FF) - (c1 & 0x000000FF))
						/ (y2 - y1);
				dg = (float) (((c2 & 0x0000FF00) >> 8) - ((c1 & 0x0000FF00) >> 8))
						/ (y2 - y1);
				db = (float) (((c2 & 0x00FF0000) >> 16) - ((c1 & 0x00FF0000) >> 16))
						/ (y2 - y1);
				da = (float) (((c2 & 0xFF000000) >> 24) - ((c1 & 0xFF000000) >> 24))
						/ (y2 - y1);
			} else {
				dz = 0;
				dr = 0;
				dg = 0;
				db = 0;
				da = 0;
			}
			float z = z1;
			float r = (c1 & 0x000000FF);
			float g = (c1 & 0x0000FF00) >> 8;
			float b = (c1 & 0x00FF0000) >> 16;
			float a = (c1 & 0xFF000000) >> 24;
			int color = c1;
			for (int y = y1; y <= y2; y++) {
				if ((y >= 0) && (y < height)) {
					if (depthTest(pos, z))
						pixels[pos] = color;
				}
				z += dz;
				r += dr;
				g += dg;
				b += db;
				a += da;
				color = (int) r + ((int) g << 8) + ((int) b << 16)
						+ ((int) a << 24);
				pos += width;
			}
			needUpdate = true;
		}
	}

	/**
	 * Dibuja una l�nea que crece m�s r�pido horizontalmente, interpolando los
	 * colores de los v�rtices.
	 */
	public void drawGoraudHorizontalIncLine() {
		int c1 = colorPool[0].getPackedValue();
		int c2 = colorPool[1].getPackedValue();
		int x1 = (int) vertexPool[0].x;
		int x2 = (int) vertexPool[1].x;
		int y1 = (int) vertexPool[0].y;
		int y2 = (int) vertexPool[1].y;
		float z1 = vertexPool[0].z;
		float z2 = vertexPool[1].z;
		int pos;
		if (x1 > x2) {
			int tmpx = x2;
			x2 = x1;
			x1 = tmpx;
			int tmpy = y2;
			y2 = y1;
			y1 = tmpy;
			int tmpc = c2;
			c2 = c1;
			c1 = tmpc;
			float tmpz = z2;
			z2 = z1;
			z1 = tmpz;
		}
		float dz, dy, dr, dg, db, da;
		if ((int) (x2 - x1) != 0) {
			dz = (float) (z2 - z1) / (x2 - x1);
			dy = (float) (y2 - y1) / (x2 - x1);
			dr = (float) ((c2 & 0x000000FF) - (c1 & 0x000000FF)) / (x2 - x1);
			dg = (float) (((c2 & 0x0000FF00) >> 8) - ((c1 & 0x0000FF00) >> 8))
					/ (x2 - x1);
			db = (float) (((c2 & 0x00FF0000) >> 16) - ((c1 & 0x00FF0000) >> 16))
					/ (x2 - x1);
			da = (float) (((c2 & 0xFF000000) >> 24) - ((c1 & 0xFF000000) >> 24))
					/ (x2 - x1);
		} else {
			dz = 0;
			dy = 0;
			dr = 0;
			dg = 0;
			db = 0;
			da = 0;
		}
		float z = z1;
		float y = y1;
		float r = (c1 & 0x000000FF);
		float g = (c1 & 0x0000FF00) >> 8;
		float b = (c1 & 0x00FF0000) >> 16;
		float a = (c1 & 0xFF000000) >> 24;
		int color = c1;
		for (int x = x1; x <= x2; x++) {
			if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
				pos = x + width * (int) y;
				if (depthTest(pos, z))
					pixels[pos] = color;
			}
			z += dz;
			y += dy;
			r += dr;
			g += dg;
			b += db;
			a += da;
			color = (int) r + ((int) g << 8) + ((int) b << 16)
					+ ((int) a << 24);
		}
		needUpdate = true;
	}

	/**
	 * Dibuja una l�nea que crece m�s r�pido verticalmente, interpolando los
	 * colores de los v�rtices.
	 */
	public void drawGoraudVerticalIncLine() {
		int c1 = colorPool[0].getPackedValue();
		int c2 = colorPool[1].getPackedValue();
		int x1 = (int) vertexPool[0].x;
		int x2 = (int) vertexPool[1].x;
		int y1 = (int) vertexPool[0].y;
		int y2 = (int) vertexPool[1].y;
		float z1 = vertexPool[0].z;
		float z2 = vertexPool[1].z;
		int pos;
		if (y1 > y2) {
			int tmpx = x2;
			x2 = x1;
			x1 = tmpx;
			int tmpy = y2;
			y2 = y1;
			y1 = tmpy;
			int tmpc = c2;
			c2 = c1;
			c1 = tmpc;
			float tmpz = z2;
			z2 = z1;
			z1 = tmpz;
		}
		float dz, dx, dr, dg, db, da;
		if ((int) (y2 - y1) != 0) {
			dz = (float) (z2 - z1) / (y2 - y1);
			dx = (float) (x2 - x1) / (y2 - y1);
			dr = (float) ((c2 & 0x000000FF) - (c1 & 0x000000FF)) / (y2 - y1);
			dg = (float) (((c2 & 0x0000FF00) >> 8) - ((c1 & 0x0000FF00) >> 8))
					/ (y2 - y1);
			db = (float) (((c2 & 0x00FF0000) >> 16) - ((c1 & 0x00FF0000) >> 16))
					/ (y2 - y1);
			da = (float) (((c2 & 0xFF000000) >> 24) - ((c1 & 0xFF000000) >> 24))
					/ (y2 - y1);
		} else {
			dz = 0;
			dx = 0;
			dr = 0;
			dg = 0;
			db = 0;
			da = 0;
		}
		float z = z1;
		float x = x1;
		float r = (c1 & 0x000000FF);
		float g = (c1 & 0x0000FF00) >> 8;
		float b = (c1 & 0x00FF0000) >> 16;
		float a = (c1 & 0xFF000000) >> 24;
		int color = c1;
		for (int y = y1; y <= y2; y++) {
			if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
				pos = (int) x + width * y;
				if (depthTest(pos, z))
					pixels[pos] = color;
			}
			z += dz;
			x += dx;
			r += dr;
			g += dg;
			b += db;
			a += da;
			color = (int) r + ((int) g << 8) + ((int) b << 16)
					+ ((int) a << 24);
		}
		needUpdate = true;
	}

	/**
	 * Borra la im�gen y establece el �ltimo color establecido.
	 */
	public void clear() {
		if (status == N3_SWR_STATUS_INIT) {
			if (size > 0) {
				int c = lastColor.getPackedValue();
				int half = size >> 1;
				pixels[0] = c;

				for (int i = 1; i < size;) {
					System.arraycopy(pixels, 0, pixels, i, (i < half) ? i
							: size - i);
					i <<= 1;
				}
				needUpdate = true;
			}
		}
	}

	/**
	 * Borra la im�gen y establece el color definido.
	 * 
	 * @param color
	 *            Color de relleno
	 */
	public void clearToColor(N3ColorRGBA color) {
		if (status == N3_SWR_STATUS_INIT) {
			if (size > 0) {
				int c = color.getPackedValue();
				int half = size >> 1;
				pixels[0] = c;

				for (int i = 1; i < size;) {
					System.arraycopy(pixels, 0, pixels, i, (i < half) ? i
							: size - i);
					i <<= 1;
				}
				needUpdate = true;
			}
		}
	}

	/**
	 * Dibuja un tri�ngulo utilizando el pool, con el �ltimo color definido.
	 */
	public void drawFlatTriangle() {
		int color = lastColor.getPackedValue();
		/* Buscamos el lado cuya proyecci�n sobre el eje Y es m�s larga */
		int dy01 = ((int) vertexPool[1].y - (int) vertexPool[0].y);
		dy01 = (dy01 < 0) ? -dy01 : dy01;
		int dy02 = ((int) vertexPool[2].y - (int) vertexPool[0].y);
		dy02 = (dy02 < 0) ? -dy02 : dy02;
		int dy12 = ((int) vertexPool[2].y - (int) vertexPool[1].y);
		dy12 = (dy12 < 0) ? -dy12 : dy12;
		int dy;
		int bx1, bx2, by1, by2, sx, sy;
		float bz1, bz2, sz;
		if ((dy01 >= dy02) && (dy01 >= dy12)) {
			/* El lado base ser� el 0-1 */
			bx1 = (int) vertexPool[0].x;
			by1 = (int) vertexPool[0].y;
			bz1 = vertexPool[0].z;
			bx2 = (int) vertexPool[1].x;
			by2 = (int) vertexPool[1].y;
			bz2 = vertexPool[1].z;
			sx = (int) vertexPool[2].x;
			sy = (int) vertexPool[2].y;
			sz = vertexPool[2].z;
			dy = dy01;
		} else if ((dy02 >= dy01) && (dy02 >= dy12)) {
			/* El lado base ser� el 0-2 */
			bx1 = (int) vertexPool[0].x;
			by1 = (int) vertexPool[0].y;
			bz1 = vertexPool[0].z;
			bx2 = (int) vertexPool[2].x;
			by2 = (int) vertexPool[2].y;
			bz2 = vertexPool[2].z;
			sx = (int) vertexPool[1].x;
			sy = (int) vertexPool[1].y;
			sz = vertexPool[1].z;
			dy = dy02;
		} else {
			/* El lado base ser� el 1-2 */
			bx1 = (int) vertexPool[1].x;
			by1 = (int) vertexPool[1].y;
			bz1 = vertexPool[1].z;
			bx2 = (int) vertexPool[2].x;
			by2 = (int) vertexPool[2].y;
			bz2 = vertexPool[2].z;
			sx = (int) vertexPool[0].x;
			sy = (int) vertexPool[0].y;
			sz = vertexPool[0].z;
			dy = dy12;
		}
		if (by1 > by2) {
			int tmpi = by1;
			by1 = by2;
			by2 = tmpi;
			tmpi = bx1;
			bx1 = bx2;
			bx2 = tmpi;
			float tmpf = bz1;
			bz1 = bz2;
			bz2 = tmpf;
		}
		float bdx, bdz, s1dx, s1dz, s2dx, s2dz, dsx, dsz;
		if ((int) dy != 0) {
			bdx = (float) (bx2 - bx1) / dy;
			bdz = (float) (bz2 - bz1) / dy;
		} else {
			bdx = 0;
			bdz = 0;
		}
		if ((int) (sy - by1) != 0) {
			s1dx = (float) (sx - bx1) / (sy - by1);
			s1dz = (float) (sz - bz1) / (sy - by1);
			dsx = bx1;
			dsz = bz1;
		} else {
			s1dx = 0;
			s1dz = 0;
			dsx = sx;
			dsz = sz;
		}
		if ((int) (by2 - sy) != 0) {
			s2dx = (float) (bx2 - sx) / (by2 - sy);
			s2dz = (float) (bz2 - sz) / (by2 - sy);
		} else {
			s2dx = 0;
			s2dz = 0;
		}
		float bx = bx1;
		float bz = bz1;
		int pos;
		int init, end;
		float initz, ldz;
		by2 = (by2 > height) ? height : by2;
		if (by1 < 0) {
			float skip = (-by1);
			bx += bdx * skip;
			bz += bdz * skip;
			if (sy > 0) {
				dsx += s1dx * skip;
				dsz += s1dz * skip;
			} else {
				float skips = (-sy);
				dsx = sx + s2dx * skips;
				dsz = sz + s2dz * skips;
			}
			by1 = 0;
		}
		if ((by1 < height) && (by2 > 0)) {
			for (int y = by1; y <= by2; y++) {
				/* Dibujamos la l�nea horizontal */
				if (bx > dsx) {
					init = (int) dsx;
					end = (int) bx;
					initz = dsz;
					ldz = (bz - dsz) / (end - init);
				} else {
					init = (int) bx;
					end = (int) dsx;
					initz = bz;
					ldz = (dsz - bz) / (end - init);
				}
				end = (end > width) ? width : end;
				if (init < 0) {
					float skip = (-init);
					initz += ldz * skip;
					init = 0;
				}
				pos = init + y * width;
				if ((y >= 0) && (y < height) && (init <= width) && (end >= 0)) {
					for (; init <= end; init++) {
						if ((init >= 0) && (init < width))
							if (depthTest(pos, initz))
								pixels[pos] = color;
						pos++;
						initz += ldz;
					}
				}
				/* Actualizamos los valores para la siguiente vuelta */
				bx += bdx;
				bz += bdz;
				dsx += ((y < sy) ? s1dx : s2dx);
				dsz += ((y < sy) ? s1dz : s2dz);
			}
		}
		needUpdate = true;
	}

	/**
	 * Dibuja un tri�ngulo goraud utilizando el pool
	 */
	public void drawGoraudTriangle() {
		int c0 = colorPool[0].getPackedValue();
		int c1 = colorPool[1].getPackedValue();
		int c2 = colorPool[2].getPackedValue();
		/* Buscamos el lado cuya proyecci�n sobre el eje Y es m�s larga */
		int dy01 = ((int) vertexPool[1].y - (int) vertexPool[0].y);
		dy01 = (dy01 < 0) ? -dy01 : dy01;
		int dy02 = ((int) vertexPool[2].y - (int) vertexPool[0].y);
		dy02 = (dy02 < 0) ? -dy02 : dy02;
		int dy12 = ((int) vertexPool[2].y - (int) vertexPool[1].y);
		dy12 = (dy12 < 0) ? -dy12 : dy12;
		int dy;
		int bx1, bx2, by1, by2, sx, sy, bc1, bc2, sc;
		float bz1, bz2, sz;
		if ((dy01 >= dy02) && (dy01 >= dy12)) {
			/* El lado base ser� el 0-1 */
			bx1 = (int) vertexPool[0].x;
			by1 = (int) vertexPool[0].y;
			bz1 = vertexPool[0].z;
			bx2 = (int) vertexPool[1].x;
			by2 = (int) vertexPool[1].y;
			bz2 = vertexPool[1].z;
			sx = (int) vertexPool[2].x;
			sy = (int) vertexPool[2].y;
			sz = vertexPool[2].z;
			bc1 = c0;
			bc2 = c1;
			sc = c2;
		} else if ((dy02 >= dy01) && (dy02 >= dy12)) {
			/* El lado base ser� el 0-2 */
			bx1 = (int) vertexPool[0].x;
			by1 = (int) vertexPool[0].y;
			bz1 = vertexPool[0].z;
			bx2 = (int) vertexPool[2].x;
			by2 = (int) vertexPool[2].y;
			bz2 = vertexPool[2].z;
			sx = (int) vertexPool[1].x;
			sy = (int) vertexPool[1].y;
			sz = vertexPool[1].z;
			bc1 = c0;
			bc2 = c2;
			sc = c1;
		} else {
			/* El lado base ser� el 1-2 */
			bx1 = (int) vertexPool[1].x;
			by1 = (int) vertexPool[1].y;
			bz1 = vertexPool[1].z;
			bx2 = (int) vertexPool[2].x;
			by2 = (int) vertexPool[2].y;
			bz2 = vertexPool[2].z;
			sx = (int) vertexPool[0].x;
			sy = (int) vertexPool[0].y;
			sz = vertexPool[0].z;
			bc1 = c1;
			bc2 = c2;
			sc = c0;
		}
		if (by1 > by2) {
			int tmpi = by1;
			by1 = by2;
			by2 = tmpi;
			tmpi = bx1;
			bx1 = bx2;
			bx2 = tmpi;
			tmpi = bc1;
			bc1 = bc2;
			bc2 = tmpi;
			float tmpf = bz1;
			bz1 = bz2;
			bz2 = tmpf;
		}
		float bdx, bdz, s1dx, s1dz, s2dx, s2dz, bda, bdr, bdg, bdb, s1da, s1dr, s1dg, s1db, s2da, s2dr, s2dg, s2db, dsx, dsz;
		int dscolor;
		dy = (int) (by2 - by1);
		if ((int) dy != 0) {
			bdx = (float) (bx2 - bx1) / dy;
			bdz = (float) (bz2 - bz1) / dy;
			bdr = (float) ((bc2 & 0x000000FF) - (bc1 & 0x000000FF)) / dy;
			bdg = (float) (((bc2 & 0x0000FF00) >> 8) - ((bc1 & 0x0000FF00) >> 8))
					/ dy;
			bdb = (float) (((bc2 & 0x00FF0000) >> 16) - ((bc1 & 0x00FF0000) >> 16))
					/ dy;
			bda = (float) (((bc2 >> 24) & 0x000000FF) - ((bc1 >> 24) & 0x000000FF))
					/ dy;
		} else {
			bdx = 0;
			bdz = 0;
			bdr = 0;
			bdg = 0;
			bdb = 0;
			bda = 0;
		}
		float ds1y = (sy - by1);
		if ((int) ds1y != 0) {
			s1dx = (float) (sx - bx1) / ds1y;
			s1dz = (float) (sz - bz1) / ds1y;
			s1dr = (float) ((sc & 0x000000FF) - (bc1 & 0x000000FF)) / ds1y;
			s1dg = (float) (((sc & 0x0000FF00) >> 8) - ((bc1 & 0x0000FF00) >> 8))
					/ ds1y;
			s1db = (float) (((sc & 0x00FF0000) >> 16) - ((bc1 & 0x00FF0000) >> 16))
					/ ds1y;
			s1da = (float) (((sc >> 24) & 0x000000FF) - ((bc1 >> 24) & 0x000000FF))
					/ ds1y;
			dsx = bx1;
			dsz = bz1;
			dscolor = bc1;
		} else {
			s1dx = 0;
			s1dz = 0;
			s1dr = 0;
			s1dg = 0;
			s1db = 0;
			s1da = 0;
			dsx = sx;
			dsz = sz;
			dscolor = sc;
		}
		float ds2y = (by2 - sy);
		if ((int) ds2y != 0) {
			s2dx = (float) (bx2 - sx) / ds2y;
			s2dz = (float) (bz2 - sz) / ds2y;
			s2dr = (float) ((bc2 & 0x000000FF) - (sc & 0x000000FF)) / ds2y;
			s2dg = (float) (((bc2 & 0x0000FF00) >> 8) - ((sc & 0x0000FF00) >> 8))
					/ ds2y;
			s2db = (float) (((bc2 & 0x00FF0000) >> 16) - ((sc & 0x00FF0000) >> 16))
					/ ds2y;
			s2da = (float) (((bc2 >> 24) & 0x000000FF) - ((sc >> 24) & 0x000000FF))
					/ ds2y;
		} else {
			s2dx = 0;
			s2dz = 0;
			s2dr = 0;
			s2dg = 0;
			s2db = 0;
			s2da = 0;
		}
		float bx = bx1;
		float bz = bz1;

		float br = (bc1 & 0x000000FF);
		float bg = (bc1 & 0x0000FF00) >> 8;
		float bb = (bc1 & 0x00FF0000) >> 16;
		float ba = (bc1 >> 24) & 0x000000FF;
		int bcolor = bc1;

		float dsr = (dscolor & 0x000000FF);
		float dsg = (dscolor & 0x0000FF00) >> 8;
		float dsb = (dscolor & 0x00FF0000) >> 16;
		float dsa = (dscolor >> 24) & 0x000000FF;

		int pos;
		int init, end;
		float initz, ldz, initr, initg, initb, inita, ldr, ldg, ldb, lda;
		by2 = (by2 > height) ? height : by2;
		if (by1 < 0) {
			float skip = (-by1);
			bx += bdx * skip;
			bz += bdz * skip;
			br += bdr * skip;
			bg += bdg * skip;
			bb += bdb * skip;
			ba += bda * skip;
			bcolor = (int) br + ((int) bg << 8) + ((int) bb << 16)
					+ ((int) ba << 24);
			if (sy > 0) {
				dsx += s1dx * skip;
				dsz += s1dz * skip;
				dsr += s1dr * skip;
				dsg += s1dg * skip;
				dsb += s1db * skip;
				dsa += s1da * skip;
				dscolor = (int) dsr + ((int) dsg << 8) + ((int) dsb << 16)
						+ ((int) dsa << 24);
			} else {
				float skips = (-sy);
				dsx = sx + s2dx * skips;
				dsz = sz + s2dz * skips;
				dsr = (sc & 0xFF) + s2dr * skips;
				dsg = ((sc >> 8) & 0xFF) + s2dg * skips;
				dsb = ((sc >> 16) & 0xFF) + s2db * skips;
				dsa = ((sc >> 24) & 0xFF) + s2da * skips;
				dscolor = (int) dsr + ((int) dsg << 8) + ((int) dsb << 16)
						+ ((int) dsa << 24);
			}
			by1 = 0;
		}
		if ((by1 < height) && (by2 > 0)) {
			for (int y = by1; y <= by2; y++) {
				/* Dibujamos la l�nea horizontal */
				if (bx > dsx) {
					init = (int) dsx;
					end = (int) bx;
					initz = dsz;
					ldz = (bz - dsz) / (end - init);
					initr = dsr;
					initg = dsg;
					initb = dsb;
					inita = dsa;
					ldr = (br - dsr) / (end - init);
					ldg = (bg - dsg) / (end - init);
					ldb = (bb - dsb) / (end - init);
					lda = (ba - dsa) / (end - init);
				} else {
					init = (int) bx;
					end = (int) dsx;
					initz = bz;
					ldz = (dsz - bz) / (end - init);
					initr = br;
					initg = bg;
					initb = bb;
					inita = ba;
					ldr = (dsr - br) / (end - init);
					ldg = (dsg - bg) / (end - init);
					ldb = (dsb - bb) / (end - init);
					lda = (dsa - ba) / (end - init);
				}
				end = (end > width) ? width : end;
				if (init < 0) {
					float skip = (-init);
					initz += ldz * skip;
					initr += ldr * skip;
					initg += ldg * skip;
					initb += ldb * skip;
					inita += lda * skip;
					init = 0;
				}
				pos = init + y * width;
				if ((y >= 0) && (y < height) && (init <= width) && (end >= 0))
					for (; init <= end; init++) {
						if ((init >= 0) && (init < width))
							if (depthTest(pos, initz))
								pixels[pos] = (int) initr + ((int) initg << 8)
										+ ((int) initb << 16)
										+ ((int) inita << 24);
						pos++;
						initz += ldz;
						initr += ldr;
						initg += ldg;
						initb += ldb;
						inita += lda;
					}
				/* Actualizamos los valores para la siguiente vuelta */
				bx += bdx;
				bz += bdz;
				br += bdr;
				bg += bdg;
				bb += bdb;
				ba += bda;
				bcolor = (int) br + ((int) bg << 8) + ((int) bb << 16)
						+ ((int) ba << 24);

				dsx += ((y < sy) ? s1dx : s2dx);
				dsz += ((y < sy) ? s1dz : s2dz);
				dsr += ((y < sy) ? s1dr : s2dr);
				dsg += ((y < sy) ? s1dg : s2dg);
				dsb += ((y < sy) ? s1db : s2db);
				dsa += ((y < sy) ? s1da : s2da);
				dscolor = (int) dsr + ((int) dsg << 8) + ((int) dsb << 16)
						+ ((int) dsa << 24);
			}
		}
		needUpdate = true;
	}

	/**
	 * Dibuja un tri�ngulo utilizando el pool, con la textura seleccionada.
	 */
	public void drawReplaceTexturedTriangle() {
		int color = lastColor.getPackedValue();
		/* Buscamos el lado cuya proyecci�n sobre el eje Y es m�s larga */
		int dy01 = ((int) vertexPool[1].y - (int) vertexPool[0].y);
		dy01 = (dy01 < 0) ? -dy01 : dy01;
		int dy02 = ((int) vertexPool[2].y - (int) vertexPool[0].y);
		dy02 = (dy02 < 0) ? -dy02 : dy02;
		int dy12 = ((int) vertexPool[2].y - (int) vertexPool[1].y);
		dy12 = (dy12 < 0) ? -dy12 : dy12;
		int dy;
		int bx1, bx2, by1, by2, sx, sy, btx1, bty1, btx2, bty2, stx, sty;
		float bz1, bz2, sz;
		N3InternalTexture texture = (N3InternalTexture) textures
				.elementAt(selectedTexture);
		if ((dy01 >= dy02) && (dy01 >= dy12)) {
			/* El lado base ser� el 0-1 */
			bx1 = (int) vertexPool[0].x;
			by1 = (int) vertexPool[0].y;
			bz1 = vertexPool[0].z;
			bx2 = (int) vertexPool[1].x;
			by2 = (int) vertexPool[1].y;
			bz2 = vertexPool[1].z;
			sx = (int) vertexPool[2].x;
			sy = (int) vertexPool[2].y;
			sz = vertexPool[2].z;

			btx1 = (int) (uvPool[0].u * (texture.width - 1));
			bty1 = (int) (uvPool[0].v * (texture.height - 1));
			btx2 = (int) (uvPool[1].u * (texture.width - 1));
			bty2 = (int) (uvPool[1].v * (texture.height - 1));
			stx = (int) (uvPool[2].u * (texture.width - 1));
			sty = (int) (uvPool[2].v * (texture.height - 1));

			dy = dy01;
		} else if ((dy02 >= dy01) && (dy02 >= dy12)) {
			/* El lado base ser� el 0-2 */
			bx1 = (int) vertexPool[0].x;
			by1 = (int) vertexPool[0].y;
			bz1 = vertexPool[0].z;
			bx2 = (int) vertexPool[2].x;
			by2 = (int) vertexPool[2].y;
			bz2 = vertexPool[2].z;
			sx = (int) vertexPool[1].x;
			sy = (int) vertexPool[1].y;
			sz = vertexPool[1].z;

			btx1 = (int) (uvPool[0].u * (texture.width - 1));
			bty1 = (int) (uvPool[0].v * (texture.height - 1));
			btx2 = (int) (uvPool[2].u * (texture.width - 1));
			bty2 = (int) (uvPool[2].v * (texture.height - 1));
			stx = (int) (uvPool[1].u * (texture.width - 1));
			sty = (int) (uvPool[1].v * (texture.height - 1));

			dy = dy02;
		} else {
			/* El lado base ser� el 1-2 */
			bx1 = (int) vertexPool[1].x;
			by1 = (int) vertexPool[1].y;
			bz1 = vertexPool[1].z;
			bx2 = (int) vertexPool[2].x;
			by2 = (int) vertexPool[2].y;
			bz2 = vertexPool[2].z;
			sx = (int) vertexPool[0].x;
			sy = (int) vertexPool[0].y;
			sz = vertexPool[0].z;

			btx1 = (int) (uvPool[1].u * (texture.width - 1));
			bty1 = (int) (uvPool[1].v * (texture.height - 1));
			btx2 = (int) (uvPool[2].u * (texture.width - 1));
			bty2 = (int) (uvPool[2].v * (texture.height - 1));
			stx = (int) (uvPool[0].u * (texture.width - 1));
			sty = (int) (uvPool[0].v * (texture.height - 1));

			dy = dy12;
		}
		if (by1 > by2) {
			int tmpi = by1;
			by1 = by2;
			by2 = tmpi;
			tmpi = bx1;
			bx1 = bx2;
			bx2 = tmpi;
			float tmpf = bz1;
			bz1 = bz2;
			bz2 = tmpf;
			tmpi = btx1;
			btx1 = btx2;
			btx2 = tmpi;
			tmpi = bty1;
			bty1 = bty2;
			bty2 = tmpi;
		}
		float bdx, bdz, s1dx, s1dz, s2dx, s2dz, dsx, dsz, btdx, btdy, s1tdx, s1tdy, s2tdx, s2tdy, dstx, dsty;
		if ((int) dy != 0) {
			bdx = (float) (bx2 - bx1) / dy;
			bdz = (float) (bz2 - bz1) / dy;
			btdx = (float) (btx2 - btx1) / dy;
			btdy = (float) (bty2 - bty1) / dy;
		} else {
			bdx = 0;
			bdz = 0;
			btdx = 0;
			btdy = 0;
		}
		if ((int) (sy - by1) != 0) {
			s1dx = (float) (sx - bx1) / (sy - by1);
			s1dz = (float) (sz - bz1) / (sy - by1);
			s1tdx = (float) (stx - btx1) / (sy - by1);
			s1tdy = (float) (sty - bty1) / (sy - by1);
			dsx = bx1;
			dsz = bz1;
			dstx = btx1;
			dsty = bty1;
		} else {
			s1dx = 0;
			s1dz = 0;
			s1tdx = 0;
			s1tdy = 0;
			dsx = sx;
			dsz = sz;
			dstx = stx;
			dsty = sty;
		}
		if ((int) (by2 - sy) != 0) {
			s2dx = (float) (bx2 - sx) / (by2 - sy);
			s2dz = (float) (bz2 - sz) / (by2 - sy);
			s2tdx = (float) (btx2 - stx) / (by2 - sy);
			s2tdy = (float) (bty2 - sty) / (by2 - sy);
		} else {
			s2dx = 0;
			s2dz = 0;
			s2tdx = 0;
			s2tdy = 0;
		}
		float bx = bx1;
		float bz = bz1;
		float btx = btx1;
		float bty = bty1;
		int pos;
		int init, end;
		float initz, ldz, inittx, initty, ltdx, ltdy;
		by2 = (by2 > height) ? height : by2;
		if (by1 < 0) {
			float skip = (-by1);
			bx += bdx * skip;
			bz += bdz * skip;
			btx += btdx * skip;
			bty += btdy * skip;
			if (sy > 0) {
				dsx += s1dx * skip;
				dsz += s1dz * skip;
				dstx += s1tdx * skip;
				dsty += s1tdy * skip;
			} else {
				float skips = (-sy);
				dsx = sx + s2dx * skips;
				dsz = sz + s2dz * skips;
				dstx = stx + s2tdx * skips;
				dsty = sty + s2tdy * skips;
			}
			by1 = 0;
		}
		int texdatapos;
		if ((by1 < height) && (by2 > 0)) {
			for (int y = by1; y <= by2; y++) {
				/* Dibujamos la l�nea horizontal */
				if (bx > dsx) {
					init = (int) dsx;
					end = (int) bx;
					initz = dsz;
					inittx = dstx;
					initty = dsty;
					ldz = (bz - dsz) / (end - init);
					ltdx = (btx - dstx) / (end - init);
					ltdy = (bty - dsty) / (end - init);
				} else {
					init = (int) bx;
					end = (int) dsx;
					inittx = btx;
					initty = bty;
					initz = bz;
					ldz = (dsz - bz) / (end - init);
					ltdx = (dstx - btx) / (end - init);
					ltdy = (dsty - bty) / (end - init);
				}
				end = (end > width) ? width : end;
				if (init < 0) {
					float skip = (-init);
					initz += ldz * skip;
					inittx += ltdx * skip;
					initty += ltdy * skip;
					init = 0;
				}
				pos = init + y * width;
				if ((y >= 0) && (y < height) && (init <= width) && (end >= 0)) {
					for (; init <= end; init++) {
						texdatapos = ((int) inittx + ((int) initty)
								* texture.width) * 4;
						if ((init >= 0) && (init < width))
							if (depthTest(pos, initz)) {
								color = ((int) texture.data[texdatapos])
										+ (((int) texture.data[texdatapos + 1]) << 8)
										+ (((int) texture.data[texdatapos + 2]) << 16)
										+ (((int) texture.data[texdatapos + 3]) << 24);
								pixels[pos] = color;
							}
						pos++;
						initz += ldz;
						inittx += ltdx;
						initty += ltdy;
					}
				}
				/* Actualizamos los valores para la siguiente vuelta */
				bx += bdx;
				bz += bdz;
				dsx += ((y < sy) ? s1dx : s2dx);
				dsz += ((y < sy) ? s1dz : s2dz);
				btx += btdx;
				bty += btdy;
				dstx += ((y < sy) ? s1tdx : s2tdx);
				dsty += ((y < sy) ? s1tdy : s2tdy);
			}
		}
		needUpdate = true;
	}

	/**
	 * Dibuja un tri�ngulo utilizando el pool, con la textura seleccionada y el
	 * �ltimo color especificado.
	 */
	public void drawFlatTexturedTriangle() {
		int color = lastColor.getPackedValue();
		/* Buscamos el lado cuya proyecci�n sobre el eje Y es m�s larga */
		int dy01 = ((int) vertexPool[1].y - (int) vertexPool[0].y);
		dy01 = (dy01 < 0) ? -dy01 : dy01;
		int dy02 = ((int) vertexPool[2].y - (int) vertexPool[0].y);
		dy02 = (dy02 < 0) ? -dy02 : dy02;
		int dy12 = ((int) vertexPool[2].y - (int) vertexPool[1].y);
		dy12 = (dy12 < 0) ? -dy12 : dy12;
		int dy;
		int bx1, bx2, by1, by2, sx, sy, btx1, bty1, btx2, bty2, stx, sty;
		float bz1, bz2, sz;
		N3InternalTexture texture = (N3InternalTexture) textures
				.elementAt(selectedTexture);
		if ((dy01 >= dy02) && (dy01 >= dy12)) {
			/* El lado base ser� el 0-1 */
			bx1 = (int) vertexPool[0].x;
			by1 = (int) vertexPool[0].y;
			bz1 = vertexPool[0].z;
			bx2 = (int) vertexPool[1].x;
			by2 = (int) vertexPool[1].y;
			bz2 = vertexPool[1].z;
			sx = (int) vertexPool[2].x;
			sy = (int) vertexPool[2].y;
			sz = vertexPool[2].z;

			btx1 = (int) (uvPool[0].u * (texture.width - 1));
			bty1 = (int) (uvPool[0].v * (texture.height - 1));
			btx2 = (int) (uvPool[1].u * (texture.width - 1));
			bty2 = (int) (uvPool[1].v * (texture.height - 1));
			stx = (int) (uvPool[2].u * (texture.width - 1));
			sty = (int) (uvPool[2].v * (texture.height - 1));

			dy = dy01;
		} else if ((dy02 >= dy01) && (dy02 >= dy12)) {
			/* El lado base ser� el 0-2 */
			bx1 = (int) vertexPool[0].x;
			by1 = (int) vertexPool[0].y;
			bz1 = vertexPool[0].z;
			bx2 = (int) vertexPool[2].x;
			by2 = (int) vertexPool[2].y;
			bz2 = vertexPool[2].z;
			sx = (int) vertexPool[1].x;
			sy = (int) vertexPool[1].y;
			sz = vertexPool[1].z;

			btx1 = (int) (uvPool[0].u * (texture.width - 1));
			bty1 = (int) (uvPool[0].v * (texture.height - 1));
			btx2 = (int) (uvPool[2].u * (texture.width - 1));
			bty2 = (int) (uvPool[2].v * (texture.height - 1));
			stx = (int) (uvPool[1].u * (texture.width - 1));
			sty = (int) (uvPool[1].v * (texture.height - 1));

			dy = dy02;
		} else {
			/* El lado base ser� el 1-2 */
			bx1 = (int) vertexPool[1].x;
			by1 = (int) vertexPool[1].y;
			bz1 = vertexPool[1].z;
			bx2 = (int) vertexPool[2].x;
			by2 = (int) vertexPool[2].y;
			bz2 = vertexPool[2].z;
			sx = (int) vertexPool[0].x;
			sy = (int) vertexPool[0].y;
			sz = vertexPool[0].z;

			btx1 = (int) (uvPool[1].u * (texture.width - 1));
			bty1 = (int) (uvPool[1].v * (texture.height - 1));
			btx2 = (int) (uvPool[2].u * (texture.width - 1));
			bty2 = (int) (uvPool[2].v * (texture.height - 1));
			stx = (int) (uvPool[0].u * (texture.width - 1));
			sty = (int) (uvPool[0].v * (texture.height - 1));

			dy = dy12;
		}
		if (by1 > by2) {
			int tmpi = by1;
			by1 = by2;
			by2 = tmpi;
			tmpi = bx1;
			bx1 = bx2;
			bx2 = tmpi;
			float tmpf = bz1;
			bz1 = bz2;
			bz2 = tmpf;
			tmpi = btx1;
			btx1 = btx2;
			btx2 = tmpi;
			tmpi = bty1;
			bty1 = bty2;
			bty2 = tmpi;
		}
		float bdx, bdz, s1dx, s1dz, s2dx, s2dz, dsx, dsz, btdx, btdy, s1tdx, s1tdy, s2tdx, s2tdy, dstx, dsty;
		if ((int) dy != 0) {
			bdx = (float) (bx2 - bx1) / dy;
			bdz = (float) (bz2 - bz1) / dy;
			btdx = (float) (btx2 - btx1) / dy;
			btdy = (float) (bty2 - bty1) / dy;
		} else {
			bdx = 0;
			bdz = 0;
			btdx = 0;
			btdy = 0;
		}
		if ((int) (sy - by1) != 0) {
			s1dx = (float) (sx - bx1) / (sy - by1);
			s1dz = (float) (sz - bz1) / (sy - by1);
			s1tdx = (float) (stx - btx1) / (sy - by1);
			s1tdy = (float) (sty - bty1) / (sy - by1);
			dsx = bx1;
			dsz = bz1;
			dstx = btx1;
			dsty = bty1;
		} else {
			s1dx = 0;
			s1dz = 0;
			s1tdx = 0;
			s1tdy = 0;
			dsx = sx;
			dsz = sz;
			dstx = stx;
			dsty = sty;
		}
		if ((int) (by2 - sy) != 0) {
			s2dx = (float) (bx2 - sx) / (by2 - sy);
			s2dz = (float) (bz2 - sz) / (by2 - sy);
			s2tdx = (float) (btx2 - stx) / (by2 - sy);
			s2tdy = (float) (bty2 - sty) / (by2 - sy);
		} else {
			s2dx = 0;
			s2dz = 0;
			s2tdx = 0;
			s2tdy = 0;
		}
		float bx = bx1;
		float bz = bz1;
		float btx = btx1;
		float bty = bty1;
		int pos;
		int init, end;
		float initz, ldz, inittx, initty, ltdx, ltdy;
		by2 = (by2 > height) ? height : by2;
		if (by1 < 0) {
			float skip = (-by1);
			bx += bdx * skip;
			bz += bdz * skip;
			btx += btdx * skip;
			bty += btdy * skip;
			if (sy >= 0) {
				dsx += s1dx * skip;
				dsz += s1dz * skip;
				dstx += s1tdx * skip;
				dsty += s1tdy * skip;
			} else {
				float skips = (-sy);
				dsx = sx + s2dx * skips;
				dsz = sz + s2dz * skips;
				dstx = stx + s2tdx * skips;
				dsty = sty + s2tdy * skips;
			}
			by1 = 0;
		}
		int texdatapos;
		if ((by1 < height) && (by2 > 0)) {
			for (int y = by1; y <= by2; y++) {
				/* Dibujamos la l�nea horizontal */
				if (bx > dsx) {
					init = (int) dsx;
					end = (int) bx;
					initz = dsz;
					ldz = (bz - dsz) / (end - init);
					inittx = dstx;
					initty = dsty;
					ltdx = (btx - dstx) / (end - init);
					ltdy = (bty - dsty) / (end - init);
				} else {
					init = (int) bx;
					end = (int) dsx;
					initz = bz;
					ldz = (dsz - bz) / (end - init);
					inittx = btx;
					initty = bty;
					ltdx = (dstx - btx) / (end - init);
					ltdy = (dsty - bty) / (end - init);
				}
				end = (end > width) ? width : end;
				if (init < 0) {
					float skip = (-init);
					initz += ldz * skip;
					inittx += ltdx * skip;
					initty += ltdy * skip;
					init = 0;
				}
				pos = init + y * width;
				if ((y >= 0) && (y < height) && (init <= width) && (end >= 0)) {
					for (; init <= end; init++) {
						if ((init >= 0) && (init < width))
							if (depthTest(pos, initz)) {
								texdatapos = ((int) inittx + ((int) initty)
										* texture.width) * 4;
								pixels[pos] = (int) (((int) texture.data[texdatapos] & 0xFF) * ((color & 0x000000FF) / 255))
										| (int) ((((int) texture.data[texdatapos + 1] & 0xFF) * (((color & 0x0000FF00) >> 8) / 255)) << 8)
										| (int) ((((int) texture.data[texdatapos + 2] & 0xFF) * (((color & 0x00FF0000) >> 16) / 255)) << 16)
										| (int) ((((int) texture.data[texdatapos + 3] & 0xFF) * (((color >> 24) & 0x000000FF) / 255)) << 24);
								;
							}
						pos++;
						initz += ldz;
						inittx += ltdx;
						initty += ltdy;
					}
				}
				/* Actualizamos los valores para la siguiente vuelta */
				bx += bdx;
				bz += bdz;
				dsx += ((y < sy) ? s1dx : s2dx);
				dsz += ((y < sy) ? s1dz : s2dz);
				btx += btdx;
				bty += btdy;
				dstx += ((y < sy) ? s1tdx : s2tdx);
				dsty += ((y < sy) ? s1tdy : s2tdy);
			}
		}
		needUpdate = true;
	}

	/**
	 * Dibuja un tri�ngulo utilizando el pool, con la textura seleccionada, y
	 * los colores del pool
	 */
	public void drawGoraudTexturedTriangle() {
		int c0 = colorPool[0].getPackedValue();
		int c1 = colorPool[1].getPackedValue();
		int c2 = colorPool[2].getPackedValue();
		/* Buscamos el lado cuya proyecci�n sobre el eje Y es m�s larga */
		int dy01 = ((int) vertexPool[1].y - (int) vertexPool[0].y);
		dy01 = (dy01 < 0) ? -dy01 : dy01;
		int dy02 = ((int) vertexPool[2].y - (int) vertexPool[0].y);
		dy02 = (dy02 < 0) ? -dy02 : dy02;
		int dy12 = ((int) vertexPool[2].y - (int) vertexPool[1].y);
		dy12 = (dy12 < 0) ? -dy12 : dy12;
		int dy;
		int bx1, bx2, by1, by2, sx, sy, bc1, bc2, sc, btx1, bty1, btx2, bty2, stx, sty;
		float bz1, bz2, sz;
		N3InternalTexture texture = (N3InternalTexture) textures
				.elementAt(selectedTexture);
		if ((dy01 >= dy02) && (dy01 >= dy12)) {
			/* El lado base ser� el 0-1 */
			bx1 = (int) vertexPool[0].x;
			by1 = (int) vertexPool[0].y;
			bz1 = vertexPool[0].z;
			bx2 = (int) vertexPool[1].x;
			by2 = (int) vertexPool[1].y;
			bz2 = vertexPool[1].z;
			sx = (int) vertexPool[2].x;
			sy = (int) vertexPool[2].y;
			sz = vertexPool[2].z;
			bc1 = c0;
			bc2 = c1;
			sc = c2;

			btx1 = (int) (uvPool[0].u * (texture.width - 1));
			bty1 = (int) (uvPool[0].v * (texture.height - 1));
			btx2 = (int) (uvPool[1].u * (texture.width - 1));
			bty2 = (int) (uvPool[1].v * (texture.height - 1));
			stx = (int) (uvPool[2].u * (texture.width - 1));
			sty = (int) (uvPool[2].v * (texture.height - 1));

		} else if ((dy02 >= dy01) && (dy02 >= dy12)) {
			/* El lado base ser� el 0-2 */
			bx1 = (int) vertexPool[0].x;
			by1 = (int) vertexPool[0].y;
			bz1 = vertexPool[0].z;
			bx2 = (int) vertexPool[2].x;
			by2 = (int) vertexPool[2].y;
			bz2 = vertexPool[2].z;
			sx = (int) vertexPool[1].x;
			sy = (int) vertexPool[1].y;
			sz = vertexPool[1].z;
			bc1 = c0;
			bc2 = c2;
			sc = c1;

			btx1 = (int) (uvPool[0].u * (texture.width - 1));
			bty1 = (int) (uvPool[0].v * (texture.height - 1));
			btx2 = (int) (uvPool[2].u * (texture.width - 1));
			bty2 = (int) (uvPool[2].v * (texture.height - 1));
			stx = (int) (uvPool[1].u * (texture.width - 1));
			sty = (int) (uvPool[1].v * (texture.height - 1));

		} else {
			/* El lado base ser� el 1-2 */
			bx1 = (int) vertexPool[1].x;
			by1 = (int) vertexPool[1].y;
			bz1 = vertexPool[1].z;
			bx2 = (int) vertexPool[2].x;
			by2 = (int) vertexPool[2].y;
			bz2 = vertexPool[2].z;
			sx = (int) vertexPool[0].x;
			sy = (int) vertexPool[0].y;
			sz = vertexPool[0].z;
			bc1 = c1;
			bc2 = c2;
			sc = c0;

			btx1 = (int) (uvPool[1].u * (texture.width - 1));
			bty1 = (int) (uvPool[1].v * (texture.height - 1));
			btx2 = (int) (uvPool[2].u * (texture.width - 1));
			bty2 = (int) (uvPool[2].v * (texture.height - 1));
			stx = (int) (uvPool[0].u * (texture.width - 1));
			sty = (int) (uvPool[0].v * (texture.height - 1));

		}
		if (by1 > by2) {
			int tmpi = by1;
			by1 = by2;
			by2 = tmpi;
			tmpi = bx1;
			bx1 = bx2;
			bx2 = tmpi;
			tmpi = bc1;
			bc1 = bc2;
			bc2 = tmpi;
			float tmpf = bz1;
			bz1 = bz2;
			bz2 = tmpf;
			tmpi = btx1;
			btx1 = btx2;
			btx2 = tmpi;
			tmpi = bty1;
			bty1 = bty2;
			bty2 = tmpi;
		}
		float bdx, bdz, s1dx, s1dz, s2dx, s2dz, bda, bdr, bdg, bdb, s1da, s1dr, s1dg, s1db, s2da, s2dr, s2dg, s2db, dsx, dsz, btdx, btdy, s1tdx, s1tdy, s2tdx, s2tdy, dstx, dsty;
		int dscolor;
		dy = (int) (by2 - by1);
		if ((int) dy != 0) {
			bdx = (float) (bx2 - bx1) / dy;
			bdz = (float) (bz2 - bz1) / dy;
			bdr = (float) ((bc2 & 0x000000FF) - (bc1 & 0x000000FF)) / dy;
			bdg = (float) (((bc2 & 0x0000FF00) >> 8) - ((bc1 & 0x0000FF00) >> 8))
					/ dy;
			bdb = (float) (((bc2 & 0x00FF0000) >> 16) - ((bc1 & 0x00FF0000) >> 16))
					/ dy;
			bda = (float) (((bc2 >> 24) & 0x000000FF) - ((bc1 >> 24) & 0x000000FF))
					/ dy;
			btdx = (float) (btx2 - btx1) / dy;
			btdy = (float) (bty2 - bty1) / dy;
		} else {
			bdx = 0;
			bdz = 0;
			bdr = 0;
			bdg = 0;
			bdb = 0;
			bda = 0;
			btdx = 0;
			btdy = 0;
		}
		float ds1y = (sy - by1);
		if ((int) ds1y != 0) {
			s1dx = (float) (sx - bx1) / ds1y;
			s1dz = (float) (sz - bz1) / ds1y;
			s1dr = (float) ((sc & 0x000000FF) - (bc1 & 0x000000FF)) / ds1y;
			s1dg = (float) (((sc & 0x0000FF00) >> 8) - ((bc1 & 0x0000FF00) >> 8))
					/ ds1y;
			s1db = (float) (((sc & 0x00FF0000) >> 16) - ((bc1 & 0x00FF0000) >> 16))
					/ ds1y;
			s1da = (float) (((sc >> 24) & 0x000000FF) - ((bc1 >> 24) & 0x000000FF))
					/ ds1y;
			dsx = bx1;
			dsz = bz1;
			dscolor = bc1;
			s1tdx = (float) (stx - btx1) / ds1y;
			s1tdy = (float) (sty - bty1) / ds1y;
			dstx = btx1;
			dsty = bty1;
		} else {
			s1dx = 0;
			s1dz = 0;
			s1dr = 0;
			s1dg = 0;
			s1db = 0;
			s1da = 0;
			dsx = sx;
			dsz = sz;
			dscolor = sc;
			s1tdx = 0;
			s1tdy = 0;
			dstx = stx;
			dsty = sty;
		}
		float ds2y = (by2 - sy);
		if ((int) ds2y != 0) {
			s2dx = (float) (bx2 - sx) / ds2y;
			s2dz = (float) (bz2 - sz) / ds2y;
			s2dr = (float) ((bc2 & 0x000000FF) - (sc & 0x000000FF)) / ds2y;
			s2dg = (float) (((bc2 & 0x0000FF00) >> 8) - ((sc & 0x0000FF00) >> 8))
					/ ds2y;
			s2db = (float) (((bc2 & 0x00FF0000) >> 16) - ((sc & 0x00FF0000) >> 16))
					/ ds2y;
			s2da = (float) (((bc2 >> 24) & 0x000000FF) - ((sc >> 24) & 0x000000FF))
					/ ds2y;
			s2tdx = (float) (btx2 - stx) / ds2y;
			s2tdy = (float) (bty2 - sty) / ds2y;
		} else {
			s2dx = 0;
			s2dz = 0;
			s2dr = 0;
			s2dg = 0;
			s2db = 0;
			s2da = 0;
			s2tdx = 0;
			s2tdy = 0;
		}
		float bx = bx1;
		float bz = bz1;
		float btx = btx1;
		float bty = bty1;

		float br = (bc1 & 0x000000FF);
		float bg = (bc1 & 0x0000FF00) >> 8;
		float bb = (bc1 & 0x00FF0000) >> 16;
		float ba = (bc1 >> 24) & 0x000000FF;
		int bcolor = bc1;

		float dsr = (dscolor & 0x000000FF);
		float dsg = (dscolor & 0x0000FF00) >> 8;
		float dsb = (dscolor & 0x00FF0000) >> 16;
		float dsa = (dscolor >> 24) & 0x000000FF;

		int pos;
		int init, end;
		float initz, ldz, initr, initg, initb, inita, ldr, ldg, ldb, lda, inittx, initty, ltdx, ltdy;
		by2 = (by2 > height) ? height : by2;
		if (by1 < 0) {
			float skip = (-by1);
			bx += bdx * skip;
			bz += bdz * skip;
			br += bdr * skip;
			bg += bdg * skip;
			bb += bdb * skip;
			ba += bda * skip;
			bcolor = (int) br + ((int) bg << 8) + ((int) bb << 16)
					+ ((int) ba << 24);
			btx += btdx * skip;
			bty += btdy * skip;
			if (sy > 0) {
				dsx += s1dx * skip;
				dsz += s1dz * skip;
				dsr += s1dr * skip;
				dsg += s1dg * skip;
				dsb += s1db * skip;
				dsa += s1da * skip;
				dscolor = (int) dsr + ((int) dsg << 8) + ((int) dsb << 16)
						+ ((int) dsa << 24);
				dstx += s1tdx * skip;
				dsty += s1tdy * skip;
			} else {
				float skips = (-sy);
				dsx = sx + s2dx * skips;
				dsz = sz + s2dz * skips;
				dsr = (sc & 0xFF) + s2dr * skips;
				dsg = ((sc >> 8) & 0xFF) + s2dg * skips;
				dsb = ((sc >> 16) & 0xFF) + s2db * skips;
				dsa = ((sc >> 24) & 0xFF) + s2da * skips;
				dscolor = (int) dsr + ((int) dsg << 8) + ((int) dsb << 16)
						+ ((int) dsa << 24);
				dstx = stx + s2tdx * skips;
				dsty = sty + s2tdy * skips;
			}
			by1 = 0;
		}
		int texdatapos;
		if ((by1 < height) && (by2 > 0)) {
			for (int y = by1; y <= by2; y++) {
				/* Dibujamos la l�nea horizontal */
				if (bx > dsx) {
					init = (int) dsx;
					end = (int) bx;
					initz = dsz;
					ldz = (bz - dsz) / (end - init);
					initr = dsr;
					initg = dsg;
					initb = dsb;
					inita = dsa;
					ldr = (br - dsr) / (end - init);
					ldg = (bg - dsg) / (end - init);
					ldb = (bb - dsb) / (end - init);
					lda = (ba - dsa) / (end - init);
					inittx = dstx;
					initty = dsty;
					ltdx = (btx - dstx) / (end - init);
					ltdy = (bty - dsty) / (end - init);
				} else {
					init = (int) bx;
					end = (int) dsx;
					initz = bz;
					ldz = (dsz - bz) / (end - init);
					initr = br;
					initg = bg;
					initb = bb;
					inita = ba;
					ldr = (dsr - br) / (end - init);
					ldg = (dsg - bg) / (end - init);
					ldb = (dsb - bb) / (end - init);
					lda = (dsa - ba) / (end - init);
					inittx = btx;
					initty = bty;
					ltdx = (dstx - btx) / (end - init);
					ltdy = (dsty - bty) / (end - init);
				}
				end = (end > width) ? width : end;
				if (init < 0) {
					float skip = (-init);
					initz += ldz * skip;
					initr += ldr * skip;
					initg += ldg * skip;
					initb += ldb * skip;
					inita += lda * skip;
					inittx += ltdx * skip;
					initty += ltdy * skip;
					init = 0;
				}
				pos = init + y * width;
				if ((y >= 0) && (y < height) && (init <= width) && (end >= 0))
					for (; init <= end; init++) {
						if ((init >= 0) && (init < width))
							if (depthTest(pos, initz)) {
								texdatapos = ((int) inittx + ((int) initty)
										* texture.width) * 4;
								pixels[pos] = (int) (((int) texture.data[texdatapos] & 0xFF) * (initr / 255))
										| ((int) ((((int) texture.data[texdatapos + 1] & 0xFF) * (initg / 255))) << 8)
										| ((int) ((((int) texture.data[texdatapos + 2] & 0xFF) * (initb / 255))) << 16)
										| ((int) ((((int) texture.data[texdatapos + 3] & 0xFF) * (inita / 255))) << 24);
							}
						pos++;
						initz += ldz;
						initr += ldr;
						initg += ldg;
						initb += ldb;
						inita += lda;
						inittx += ltdx;
						initty += ltdy;
					}
				/* Actualizamos los valores para la siguiente vuelta */
				bx += bdx;
				bz += bdz;
				br += bdr;
				bg += bdg;
				bb += bdb;
				ba += bda;
				bcolor = (int) br + ((int) bg << 8) + ((int) bb << 16)
						+ ((int) ba << 24);

				dsx += ((y < sy) ? s1dx : s2dx);
				dsz += ((y < sy) ? s1dz : s2dz);
				dsr += ((y < sy) ? s1dr : s2dr);
				dsg += ((y < sy) ? s1dg : s2dg);
				dsb += ((y < sy) ? s1db : s2db);
				dsa += ((y < sy) ? s1da : s2da);
				dscolor = (int) dsr + ((int) dsg << 8) + ((int) dsb << 16)
						+ ((int) dsa << 24);

				btx += btdx;
				bty += btdy;
				dstx += ((y < sy) ? s1tdx : s2tdx);
				dsty += ((y < sy) ? s1tdy : s2tdy);
			}
		}
		needUpdate = true;
	}

	/*---------------------------------------------------------------------------------
	M�todos de la interfaz ImageProducer
	---------------------------------------------------------------------------------*/

	public synchronized void addConsumer(ImageConsumer ic) {
		consumer = ic;
		consumer.setDimensions(width, height);
		consumer.setHints(ImageConsumer.TOPDOWNLEFTRIGHT
				| ImageConsumer.COMPLETESCANLINES | ImageConsumer.SINGLEPASS
				| ImageConsumer.SINGLEFRAME);
		consumer.setColorModel(colormodel);
	}

	public synchronized boolean isConsumer(ImageConsumer ic) {
		return ic == consumer;
	}

	public synchronized void removeConsumer(ImageConsumer ic) {
		if (ic == consumer)
			consumer = null;
	}

	public void requestTopDownLeftRightResend(ImageConsumer ic) {
	}

	public void startProduction(ImageConsumer ic) {
		addConsumer(ic);
	}
}
