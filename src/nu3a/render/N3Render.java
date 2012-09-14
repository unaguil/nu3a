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

package nu3a.render;

import java.awt.Component;
import java.awt.Rectangle;

import nu3a.geometry.N3NormalData;
import nu3a.geometry.N3VertexData;
import nu3a.material.color.N3ColorData;
import nu3a.material.color.N3ColorRGBA;
import nu3a.material.texture.N3TexCoordData;
import nu3a.material.texture.N3Texture;
import nu3a.math.N3Matrix4D;
import nu3a.render.exception.N3CreateRenderException;

/**
 * Clase abtracta que representa un render generico. Define los metodos que debe
 * implementar cualquier render.
 */
public abstract class N3Render {

	/**
	 * Indica sobre que cara se realiza el culling.
	 */
	public final static int N3_FRONT_CULL = 0;
	public final static int N3_BACK_CULL = 1;
	/**
	 * Modos de textura
	 */
	public int N3_DECAL;
	public int N3_MODULATE;
	public int N3_REPLACE;
	public int N3_BLEND;

	/**
	 * Formato de la informaci�n de textura.
	 */
	public int N3_RGBA;
	public int N3_RGB;

	/**
	 * Tipo de la informaci�n de textura.
	 */
	public int N3_UNSIGNED_BYTE;
	public int N3_BYTE;
	public int N3_UNSIGNED_SHORT;
	public int N3_SHORT;
	public int N3_UNSIGNED_INT;
	public int N3_INT;
	public int N3_FLOAT;

	/**
	 * NO DEBE SER REDEFINIDOS Definen el tipo de dato que se pasa a la funci�n
	 * draw gen�rica, DEBERIAN ser definidos en las clases que implementen el
	 * render.
	 */
	public int N3_POINTS_DATA;
	public int N3_LINES_DATA;
	public int N3_TRIANGLES_DATA;
	public int N3_TRIANGLE_FAN_DATA;

	/**
	 * Definen en que caras se aplican los materiales.
	 */
	public int N3_FRONT;
	public int N3_BACK;
	public int N3_FRONT_AND_BACK;

	/**
	 * Definen las diferentes propiedades de las luces.
	 */
	public int N3_AMBIENT;
	public int N3_DIFFUSE;
	public int N3_SPECULAR;
	public int N3_POSITION;
	public int N3_SPOT_DIRECTION;
	public int N3_SPOT_CUTOFF;
	public int N3_SPOT_EXPONENT;
	public int N3_CONSTANT_ATTENUATION;
	public int N3_LINEAR_ATTENUATION;
	public int N3_QUADRATIC_ATTENUATION;

	/**
	 * Indica si esta activado el Z buffer.
	 */
	protected boolean zBuffer = true;

	/**
	 * Indica si esta activado el doble buffer;
	 */
	protected boolean doubleBuffer = true;

	/**
	 * Indica si estamos las transformaciones afectan a la matriz de proyecci�n
	 * o a la de modelado.
	 */
	protected boolean projection = false;

	/**
	 * Zona de visi�n.
	 */
	protected Rectangle vp;

	/**
	 * Indica si esta activado el texturizado.
	 */
	protected boolean texturing = true;

	/**
	 * Indica el modo de texturizado
	 */
	protected int textureMode;

	/**
	 * Indica si esta activado el cull facing.
	 */

	protected boolean cull_facing = false;

	/**
	 * Indica sobre que cara se realiza el culling
	 */
	protected int cullFace;

	/**
	 * Indica si esta activada la iluminaci�n.
	 */
	protected boolean lighting = false;

	/**
	 * Indica si el material es aplicado mediante color
	 */
	protected boolean colorMaterial = false;

	/**
	 * Inversa de la transformaci�n de la camara.
	 */
	protected N3Matrix4D initialTransform;

	/**
	 * Matriz temporal para realizar calculos.
	 */
	private N3Matrix4D tempMatrix;

	/**
	 * Representa la superficie donde se realiza el render.
	 */
	protected Component renderComponent = null;

	/**
	 * Constructor del render. Es necesario que el componente se este mostrando
	 * en pantalla, de lo contrario no se puede crear el contexto y se lanza una
	 * excepcion.
	 * 
	 * @param renderComponent
	 *            Componente sobre el que se realiza el render.
	 * @param width
	 *            Ancho en pixels de la zona de render.
	 * @param height
	 *            Alto en pixels de la zona de render.
	 * @param doubleBuffer
	 *            Indica si se usa o no doble buffer.
	 * @exception N3CreateRenderException
	 *                Indica que se ha producido algun error en la creacion del
	 *                render.
	 */
	public N3Render(Component renderComponent, boolean doubleBuffer)
			throws N3CreateRenderException {
		if (renderComponent.isVisible()) {
			this.doubleBuffer = doubleBuffer;
			this.renderComponent = renderComponent;
			initialTransform = new N3Matrix4D();
			tempMatrix = new N3Matrix4D();
		} else
			throw (new N3CreateRenderException(
					"Render component is not visible. Make it visible before create N3Render"));
	}

	/**
	 * Obtiene el componente donde se realiza el render.
	 * 
	 * @return Componente donde se realiza el render.
	 */
	public Component getRenderComponent() {
		return renderComponent;
	}

	/**
	 * Obtiene informacion general sobre el render que se esta utilizando.
	 */
	abstract public String getRenderInfo();

	/**
	 * DEBE ser llamado antes de empezar a dibujar en el contexto, y solo se
	 * puede dibujar en el si devuelve true. Es necesario para poder obtener el
	 * contexto de render del componente.
	 * 
	 * @return True si se puede dibujar con exito en el contexto.
	 */
	abstract public boolean beginDraw();

	/**
	 * DEBE ser llamado tras dibujar en el componente. Es necesario llamar a
	 * este metodo para liberar el contexto grafico del componente. Este metodo
	 * tambien se encarga si esta activado el doble buffer de intercambiar los
	 * buffers de dibujado.
	 */
	abstract public void endDraw();

	// ////Transformaci�n del espacio.
	/**
	 * Carga la matriz de transformaci�n con la matriz especificara.
	 * 
	 * @param matrix
	 *            Matriz
	 */
	abstract public void loadMatrix(N3Matrix4D matrix);

	/**
	 * Especifica la transformaci�n de la c�mara que observa la escena a
	 * renderiza.Premultiplicar� a la matriz de transformaci�n indicada en el
	 * metodo setObjectTransformation.
	 * 
	 * @param matrix
	 *            Matriz que representa la transformaci�n de la camara.
	 */
	public void setCameraTransformation(N3Matrix4D matrix) {
		initialTransform.setData(matrix);
	}

	/**
	 * Permite especificar la transformaci�n de un objeto concreto. La matriz
	 * indicada se postmultiplica por la inversa de la matriz indicada con
	 * setCameraTransform() y se carga en el mundo mediante loadMatrix(). Si no
	 * se ha especificado una transformaci�n de camara la matriz indicada en el
	 * m�todo se carga directamente con loadMatrix().
	 */
	public void setObjectTransformation(N3Matrix4D matrix) {
		if (initialTransform != null) {
			tempMatrix.setData(initialTransform);
			tempMatrix.mult(matrix);
			loadMatrix(tempMatrix);
		} else
			loadMatrix(matrix);
	}

	/**
	 * Permite aplicar de nuevo al mundo la transformacion inicial de la camara
	 * si esta existe.
	 */
	public void resetCameraTransformation() {
		if (initialTransform != null)
			loadMatrix(initialTransform);
	}

	/**
	 * Permite pasar al modo de control de la matriz de proyeccion.
	 */
	public void setProjectionMode() {
		projection = true;
	}

	/**
	 * Permite pasar al modo de control de la matriz de modelado.
	 */
	public void setModelViewMode() {
		projection = false;
	}

	abstract public void setIdentityMatrix();

	// Visualizacion
	/**
	 * Permite activar y desactivar el z buffer. El estado es registrado en la
	 * propiedad zBuffer.
	 * 
	 * @param Indica
	 *            si se activa o no el z buffer;
	 */
	public void setZBuffer(boolean zBuffer) {
		this.zBuffer = zBuffer;
	}

	/**
	 * Obtiene si esta activo el z buffer o no.
	 * 
	 * @return Indica si esta activo o no el z buffer.
	 */
	public boolean getZBuffer() {
		return zBuffer;
	}

	/**
	 * Activa o desactiva la ocultaci�n de caras traseras.
	 * 
	 * @param cull_facing
	 *            True para activarla; False en caso contrario
	 */
	public void setCullFacing(boolean cull_facing) {
		this.cull_facing = cull_facing;
	}

	/**
	 * Establece qu� cara se considera como trasera.
	 * 
	 * @param cullFace
	 *            Valor del tipo de cara trasera
	 */
	public void setCullingFace(int cullFace) {
		this.cullFace = cullFace;
	}

	/**
	 * Establece la ventana de visualizaci�n.
	 * 
	 * @param vp
	 *            Ventana de visualizaci�n
	 */
	public void setViewport(Rectangle vp) {
		this.vp = vp;
	}

	// //////Dibujado
	/**
	 * Establece el color de borrado.
	 * 
	 * @param c
	 *            Color de borrado
	 */
	abstract public void setClearColor(N3ColorRGBA c);

	/**
	 * Borra el contexto de renderizado.
	 */
	abstract public void clear();

	/**
	 * Establece el color activo en el contexto de renderizado
	 * 
	 * @param c
	 *            Color
	 */
	abstract public void setColor(N3ColorRGBA c);

	/**
	 * Renderiza los datos indicados.
	 * 
	 * @param vertexdata
	 *            Datos de los v�rtices
	 * @param dataType
	 *            Forma en que se interpretar�n los datos
	 * @param colorData
	 *            Datos de color de los v�rtices
	 */
	abstract public void drawData(N3VertexData vertexData, int dataType,
			N3ColorData colorData);

	/**
	 * Renderiza los datos indicados.
	 * 
	 * @param vertexdata
	 *            Datos de los v�rtices
	 * @param dataType
	 *            Forma en que se interpretar�n los datos
	 * @param colorData
	 *            Datos de color de los v�rtices
	 * @param normalData
	 *            Datos de las normales de los v�rtices
	 */
	abstract public void drawData(N3VertexData vertexData, int dataType,
			N3ColorData colorData, N3NormalData normalData);

	/**
	 * Renderiza los datos indicados.
	 * 
	 * @param vertexdata
	 *            Datos de los v�rtices
	 * @param dataType
	 *            Forma en que se interpretar�n los datos
	 * @param texCoordData
	 *            Datos de las coordenadas de textura de los v�rtices
	 */
	abstract public void drawData(N3VertexData vertexData, int dataType,
			N3TexCoordData texCoordData);

	/**
	 * Renderiza los datos indicados.
	 * 
	 * @param vertexdata
	 *            Datos de los v�rtices
	 * @param dataType
	 *            Forma en que se interpretar�n los datos
	 * @param colorData
	 *            Datos de color de los v�rtices
	 * @param texCoordData
	 *            Datos de las coordenadas de textura de los v�rtices
	 * @param normalData
	 *            Datos de las normales de los v�rtices
	 */
	abstract public void drawData(N3VertexData vertexData, int dataType,
			N3ColorData colorData, N3TexCoordData texCoordData,
			N3NormalData normalData);

	// /////Texturas
	/**
	 * Permite activar y desactivar el render con texturas.
	 * 
	 * @param texturing
	 *            Indica si se activa o no el texturizado.
	 */
	public void setTexturing(boolean texturing) {
		this.texturing = texturing;
	}

	/**
	 * Permite saber si el texturizado esta activado o no;
	 */
	public boolean getTexturing() {
		return texturing;
	}

	/**
	 * Establece el modo de textura.
	 * 
	 * @param mode
	 *            Modo de textura
	 */
	public void setTextureMode(int mode) {
		this.textureMode = mode;
	}

	/**
	 * Elimina la textura especificada.
	 * 
	 * @param id
	 *            Identificador de la textura
	 */
	abstract public void deleteTexture(int id);

	/**
	 * Genera una textura 2D para el render.
	 * 
	 * @param data
	 *            Datos de la textura
	 * @param dataFormat
	 *            Formato de color de la textura
	 * @param dataType
	 *            Tipo de datos de la textura
	 * @param width
	 *            Anchura
	 * @param height
	 *            Altura
	 * @return c�digo de la textura
	 */
	abstract public int genTexture2D(byte[] data, int dataFormat, int dataType,
			int width, int height);

	/**
	 * Copia un trozo de la textura sobre la textura activa.
	 * 
	 * @param xOffset
	 *            Inicio en x de la copia
	 * @param yOffset
	 *            Inicio en y de la copia
	 * @param width
	 *            Anchura de la copia
	 * @param height
	 *            Altura de la copia
	 * @param data
	 *            Datos a copiar
	 */
	abstract public void copySubTexture(int xOffset, int yOffset, int width,
			int height, byte[] data);

	/**
	 * Selecciona la textura activa.
	 * 
	 * @param texture
	 *            Textura
	 */
	abstract public void selectTexture(N3Texture texture);

	/**
	 * Establece las actuales coordenadas de textura.
	 * 
	 * @param u
	 *            Componente u
	 * @param v
	 *            Componente v
	 */
	abstract public void setTextureCoord2D(float u, float v);

	// ///Materiales
	/**
	 * Indica si se aplican los colores de los materiales.
	 * 
	 * @param True
	 *            para aplicarlos.
	 */
	public void setColorMaterial(boolean colorMaterial) {
		this.colorMaterial = colorMaterial;
	}

	/**
	 * Establece la componente ambiental de color del material.
	 * 
	 * @param face
	 *            Caras a las que afecta
	 * @param color
	 *            Color
	 */
	abstract public void setColorMaterialAmbient(int face, N3ColorRGBA color);

	/**
	 * Establece la componente difusa de color del material.
	 * 
	 * @param face
	 *            Caras a las que afecta
	 * @param color
	 *            Color
	 */
	abstract public void setColorMaterialDiffuse(int face, N3ColorRGBA color);

	/**
	 * Establece la componente ambiental y difusa de color del material.
	 * 
	 * @param face
	 *            Caras a las que afecta
	 * @param color
	 *            Color
	 */
	abstract public void setColorMaterialAmbientAndDiffuse(int face,
			N3ColorRGBA color);

	/**
	 * Establece la componente especular de color del material.
	 * 
	 * @param face
	 *            Caras a las que afecta
	 * @param color
	 *            Color
	 */
	abstract public void setColorMaterialSpecular(int face, N3ColorRGBA color);

	/**
	 * Establece la emisi�n de luz del material.
	 * 
	 * @param face
	 *            Caras a las que afecta
	 * @param color
	 *            Color
	 */
	abstract public void setColorMaterialEmission(int face, N3ColorRGBA color);

	/**
	 * Establece la componente ambiental de color del material.
	 * 
	 * @param face
	 *            Caras a las que afecta
	 * @param color
	 *            Color
	 */
	abstract public void setMaterialAmbient(int face, N3ColorRGBA color);

	/**
	 * Establece la componente difusa de color del material.
	 * 
	 * @param face
	 *            Caras a las que afecta
	 * @param color
	 *            Color
	 */
	abstract public void setMaterialDiffuse(int face, N3ColorRGBA color);

	/**
	 * Establece la componente ambiental y difusa de color del material.
	 * 
	 * @param face
	 *            Caras a las que afecta
	 * @param color
	 *            Color
	 */
	abstract public void setMaterialAmbientAndDiffuse(int face,
			N3ColorRGBA color);

	/**
	 * Establece la componente especular de color del material.
	 * 
	 * @param face
	 *            Caras a las que afecta
	 * @param color
	 *            Color
	 * @param shininess
	 *            Factor de brillo
	 */
	abstract public void setMaterialSpecular(int face, N3ColorRGBA color,
			float shininess);

	/**
	 * Establece la emisi�n de luz del material.
	 * 
	 * @param face
	 *            Caras a las que afecta
	 * @param color
	 *            Color
	 */
	abstract public void setMaterialEmission(int face, N3ColorRGBA color);

	// /Luces

	/**
	 * Activa o desactiva la iluminaci�n.
	 * 
	 * @param status
	 *            True para activarla; False en caso contrario
	 */
	public void setLighting(boolean status) {
		this.lighting = status;
	}

	/**
	 * Indica si la iluminaci�n est� o no activada.
	 * 
	 * @return True si est� activada; False en caso contrario
	 */
	public boolean isLighting() {
		return lighting;
	}

	/**
	 * Obtiene el m�ximo n�mero de luces que soporta el render.
	 * 
	 * @return M�ximo n�mero de luces que soporta el render
	 */
	public abstract int getMaxLights();

	/**
	 * Establece un par�metro de una determinada luz.
	 * 
	 * @param paramType
	 *            Par�metro a establecer
	 * @param values
	 *            Valores del par�metro
	 * @param n
	 *            Luz para la que se activa el par�metro
	 */
	public abstract void setLightParam(int paramType, float[] values, int n);

	/**
	 * Establece un par�metro de una determinada luz.
	 * 
	 * @param paramType
	 *            Par�metro a establecer
	 * @param value
	 *            Valor del par�metro
	 * @param n
	 *            Luz para la que se activa el par�metro
	 */
	public abstract void setLightParam(int paramType, float value, int n);

	/**
	 * Establece el valor de la luz ambiental para la escena, independiente del
	 * resto de luces.
	 * 
	 * @param c
	 *            Color de la luz ambiental
	 */
	public abstract void setAmbientLightValue(N3ColorRGBA c);

	/**
	 * Activa una luz.
	 * 
	 * @param n
	 *            N�mero de luz a activar.
	 */
	public abstract void enableLight(int n);

	/**
	 * Desactiva una luz.
	 * 
	 * @param n
	 *            N�mero de luz a desactivar.
	 */
	public abstract void disableLight(int n);
}
