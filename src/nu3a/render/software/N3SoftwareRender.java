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
import java.awt.Rectangle;

import nu3a.geometry.N3NormalData;
import nu3a.geometry.N3Point3D;
import nu3a.geometry.N3VertexData;
import nu3a.material.color.N3ColorData;
import nu3a.material.color.N3ColorRGBA;
import nu3a.material.texture.N3TexCoordData;
import nu3a.material.texture.N3Texture;
import nu3a.math.N3Matrix4D;
import nu3a.math.N3Vector3D;
import nu3a.render.N3Render;
import nu3a.render.exception.N3CreateRenderException;

public class N3SoftwareRender extends N3Render {

	/**
	 * Contexto de render
	 */
	N3SoftwareRenderContext renderContext;

	N3ColorRGBA clearColor;

	/**
	 * Matriz de proyecci�n del render.
	 */
	protected N3Matrix4D projectionMatrix;

	/**
	 * Matriz de modelado del render.
	 */
	protected N3Matrix4D modelViewMatrix;

	/**
	 * Maximo de luces soportado por el render.
	 */
	protected static int MAX_LIGHTS = 1;

	/**
	 * Vector de luces del render
	 */
	N3SoftwareLight lights[];

	// ///////////////Para optimizaci�n.
	// Normal
	private N3Vector3D vect1, vect2;
	private N3Vector3D d;

	// Factores para el ancho y el alto
	private float wF;
	private float hF;

	// Puntos con los que trabajar.
	private N3Point3D v1, v2, v3, cV1, cV2, cV3;

	// Planos zNear,zFar
	private float zNear = -0.1f;
	private float zFar = -100.0f;

	// Para las luces.
	private N3ColorRGBA c1, c2, c3;

	private N3Vector3D normal;

	private void define_const() {
		//
		N3_POINTS_DATA = 0;
		N3_LINES_DATA = 1;
		N3_TRIANGLES_DATA = 2;
		N3_TRIANGLE_FAN_DATA = 3;
		//
		N3_AMBIENT = 4;
		N3_DIFFUSE = 5;
		N3_SPECULAR = 6;
		N3_POSITION = 7;
		N3_SPOT_DIRECTION = 8;
		N3_SPOT_CUTOFF = 9;
		N3_SPOT_EXPONENT = 10;
		N3_CONSTANT_ATTENUATION = 11;
		N3_LINEAR_ATTENUATION = 12;
		N3_QUADRATIC_ATTENUATION = 13;

		N3_FRONT = 14;
		N3_BACK = 15;
		N3_FRONT_AND_BACK = 16;
		//
		N3_DECAL = 17;
		N3_REPLACE = 18;
		N3_MODULATE = 19;
		N3_BLEND = 20;
		//
		N3_RGBA = N3SoftwareRenderContext.N3_RGBA;
		N3_RGB = N3SoftwareRenderContext.N3_RGB;
		//
		N3_UNSIGNED_BYTE = 23;
		N3_BYTE = 24;
		N3_UNSIGNED_SHORT = 25;
		N3_SHORT = 26;
		N3_UNSIGNED_INT = 27;
		N3_INT = 28;
		N3_FLOAT = 29;
	}

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
	public N3SoftwareRender(Component renderComponent, boolean doubleBuffer)
			throws N3CreateRenderException {
		super(renderComponent, doubleBuffer);
		define_const();
		renderContext = new N3SoftwareRenderContext(renderComponent,
				doubleBuffer);

		projectionMatrix = new N3Matrix4D();
		modelViewMatrix = new N3Matrix4D();
		cullFace = N3Render.N3_BACK_CULL;
		clearColor = new N3ColorRGBA(0, 0, 0);
		lights = new N3SoftwareLight[MAX_LIGHTS];
		for (int i = 0; i < lights.length; i++)
			lights[i] = new N3SoftwareLight();

		// /Optimizaciones
		vect1 = new N3Vector3D();
		vect2 = new N3Vector3D();
		d = new N3Vector3D();
		v1 = new N3Point3D();
		v2 = new N3Point3D();
		v3 = new N3Point3D();
		c1 = new N3ColorRGBA();
		c2 = new N3ColorRGBA();
		c3 = new N3ColorRGBA();
		cV1 = new N3Point3D();
		cV2 = new N3Point3D();
		cV3 = new N3Point3D();
		normal = new N3Vector3D();
		renderContext.setTextureMode(renderContext.N3_SWR_MODULATE);
	}

	public String getRenderInfo() {
		String result = "Nu3A Software Render v0.1\n"
				+ "====================\n" + "Supported extensions:\n"
				+ "not even vertex drawing  :-P\n";
		return new String(result);
	}

	public void setIdentityMatrix() {
		if (projection)
			projectionMatrix.identity();
		else
			modelViewMatrix.identity();
	}

	public boolean beginDraw() {
		return true;
	}

	public void endDraw() {
		renderContext.paint();
	}

	public void setViewport(Rectangle vp) {
		super.setViewport(vp);
		wF = vp.width / 2.0f - 1.0f;
		hF = vp.height / 2.0f - 1.0f;
	}

	public void setZBuffer(boolean zBuffer) {
		super.setZBuffer(zBuffer);
		renderContext.setDepthTest(zBuffer);
	}

	public void clear() {
		renderContext.clearToColor(clearColor);
		if (zBuffer)
			renderContext.cleanZBuffer();
	}

	public void setClearColor(N3ColorRGBA c) {
		clearColor = c;
	}

	public void setColor(N3ColorRGBA c) {
		renderContext.setColor(c);
	}

	public void loadMatrix(N3Matrix4D matrix) {
		if (projection) {
			projectionMatrix.setData(matrix);
			float[] m = projectionMatrix.getMatrix();
			zNear = -(m[14] / (m[10] - 1));
			zFar = -(m[14] / (m[10] + 1));
		} else
			modelViewMatrix.setData(matrix);
	}

	public boolean testFrustrum(N3Point3D p) {
		if (p.z > zNear || p.z < zFar)
			return false;

		return true;
	}

	boolean clip;

	protected void doPerspective(N3Point3D point) {
		if (testFrustrum(point)) {
			N3Matrix4D.mult(projectionMatrix, point);
			// Normalizamos
			point.x /= point.w;
			point.y /= point.w;
			point.z /= point.w;
		} else
			clip = true;
	}

	protected void doScreenCoordinates(N3Point3D point) {
		point.x = wF * (1.0f + point.x);
		point.y = hF * (1.0f - point.y);
	}

	protected void doProjectionOnScreen(N3Point3D point) {
		doPerspective(point);
		doScreenCoordinates(point);
	}

	protected void applyShading(N3Point3D v, N3ColorRGBA c, N3Vector3D n) {
		normal.x = n.x;
		normal.y = n.y;
		normal.z = n.z;
		N3Matrix4D.mult(modelViewMatrix, normal);
		N3SoftwareLight.applyIndependantLight(c);
		for (int i = 0; i < MAX_LIGHTS; i++)
			if (lights[i].isEnable())
				lights[i].applyLight(v, c, normal);
	}

	protected void drawTriangles(N3VertexData vertexData,
			N3ColorData colorData, N3NormalData normalData) {
		if (vertexData.hasNext()) {
			clip = false;
			renderContext
					.beginDrawingMode(N3SoftwareRenderContext.N3_TRIANGLES);
			while (vertexData.hasNext()) {
				N3Point3D t1 = (N3Point3D) vertexData.next();
				N3Point3D t2 = (N3Point3D) vertexData.next();
				N3Point3D t3 = (N3Point3D) vertexData.next();
				v1.x = t1.x;
				v1.y = t1.y;
				v1.z = t1.z;
				v2.x = t2.x;
				v2.y = t2.y;
				v2.z = t2.z;
				v3.x = t3.x;
				v3.y = t3.y;
				v3.z = t3.z;
				N3Matrix4D.mult(modelViewMatrix, v1);
				N3Matrix4D.mult(modelViewMatrix, v2);
				N3Matrix4D.mult(modelViewMatrix, v3);
				N3Vector3D n1 = (N3Vector3D) normalData.next();
				N3Vector3D n2 = (N3Vector3D) normalData.next();
				N3Vector3D n3 = (N3Vector3D) normalData.next();
				c1.setData(colorData.next());
				c2.setData(colorData.next());
				c3.setData(colorData.next());
				if (cull_facing) {
					cV1.x = v1.x;
					cV1.y = v1.y;
					cV1.z = v1.z;
					cV2.x = v2.x;
					cV2.y = v2.y;
					cV2.z = v2.z;
					cV3.x = v3.x;
					cV3.y = v3.y;
					cV3.z = v3.z;
					doPerspective(cV1);
					doPerspective(cV2);
					doPerspective(cV3);
					if (!clip) {
						vect1.x = cV2.x - cV1.x;
						vect1.y = cV2.y - cV1.y;
						vect1.z = cV2.z - cV1.z;
						vect2.x = cV3.x - cV1.x;
						vect2.y = cV3.y - cV1.y;
						vect2.z = cV3.z - cV1.z;
						vect1.crossProduct(vect2);
						vect1.normalize();
						d.x = d.y = 0.0f;
						d.z = -1.0f;
						float dir = d.dotProduct(vect1);
						if ((cullFace == N3_BACK_CULL && dir < 0)
								|| (cullFace == N3_FRONT_CULL && dir > 0)) {
							if (lighting) {
								applyShading(v1, c1, n1);
								applyShading(v2, c2, n2);
								applyShading(v3, c3, n3);
							}
							doScreenCoordinates(cV1);
							doScreenCoordinates(cV2);
							doScreenCoordinates(cV3);
							renderContext.setColor(c1);
							renderContext.setVertex(cV1);
							renderContext.setColor(c2);
							renderContext.setVertex(cV2);
							renderContext.setColor(c3);
							renderContext.setVertex(cV3);
						}
					}
				} else {
					cV1.x = v1.x;
					cV1.y = v1.y;
					cV1.z = v1.z;
					cV2.x = v2.x;
					cV2.y = v2.y;
					cV2.z = v2.z;
					cV3.x = v3.x;
					cV3.y = v3.y;
					cV3.z = v3.z;
					doPerspective(cV1);
					doPerspective(cV2);
					doPerspective(cV3);
					if (!clip) {
						if (lighting) {
							applyShading(v1, c1, n1);
							applyShading(v2, c2, n2);
							applyShading(v3, c3, n3);
						}
						doScreenCoordinates(cV1);
						doScreenCoordinates(cV2);
						doScreenCoordinates(cV3);
						renderContext.setColor(c1);
						renderContext.setVertex(cV1);
						renderContext.setColor(c2);
						renderContext.setVertex(cV2);
						renderContext.setColor(c3);
						renderContext.setVertex(cV3);
					}
				}
			}
			renderContext.endDrawingMode();
		}
	}

	protected void drawTriangles(N3VertexData vertexData,
			N3ColorData colorData, N3NormalData normalData,
			N3TexCoordData texCoordData) {
		if (vertexData.hasNext()) {
			clip = false;
			renderContext
					.beginDrawingMode(N3SoftwareRenderContext.N3_TRIANGLES);
			while (vertexData.hasNext()) {
				N3Point3D t1 = (N3Point3D) vertexData.next();
				N3Point3D t2 = (N3Point3D) vertexData.next();
				N3Point3D t3 = (N3Point3D) vertexData.next();
				v1.x = t1.x;
				v1.y = t1.y;
				v1.z = t1.z;
				v2.x = t2.x;
				v2.y = t2.y;
				v2.z = t2.z;
				v3.x = t3.x;
				v3.y = t3.y;
				v3.z = t3.z;
				N3Matrix4D.mult(modelViewMatrix, v1);
				N3Matrix4D.mult(modelViewMatrix, v2);
				N3Matrix4D.mult(modelViewMatrix, v3);
				N3Vector3D n1 = (N3Vector3D) normalData.next();
				N3Vector3D n2 = (N3Vector3D) normalData.next();
				N3Vector3D n3 = (N3Vector3D) normalData.next();
				c1.setData(colorData.next());
				c2.setData(colorData.next());
				c3.setData(colorData.next());
				if (cull_facing) {
					cV1.x = v1.x;
					cV1.y = v1.y;
					cV1.z = v1.z;
					cV2.x = v2.x;
					cV2.y = v2.y;
					cV2.z = v2.z;
					cV3.x = v3.x;
					cV3.y = v3.y;
					cV3.z = v3.z;
					doPerspective(cV1);
					doPerspective(cV2);
					doPerspective(cV3);
					if (!clip) {
						vect1.x = cV2.x - cV1.x;
						vect1.y = cV2.y - cV1.y;
						vect1.z = cV2.z - cV1.z;
						vect2.x = cV3.x - cV1.x;
						vect2.y = cV3.y - cV1.y;
						vect2.z = cV3.z - cV1.z;
						vect1.crossProduct(vect2);
						vect1.normalize();
						d.x = d.y = 0.0f;
						d.z = -1.0f;
						float dir = d.dotProduct(vect1);
						if ((cullFace == N3_BACK_CULL && dir < 0)
								|| (cullFace == N3_FRONT_CULL && dir > 0)) {
							if (lighting) {
								applyShading(v1, c1, n1);
								applyShading(v2, c2, n2);
								applyShading(v3, c3, n3);
							}
							doScreenCoordinates(cV1);
							doScreenCoordinates(cV2);
							doScreenCoordinates(cV3);
							renderContext.setColor(c1);
							renderContext.setUV(texCoordData.next());
							renderContext.setVertex(cV1);
							renderContext.setColor(c2);
							renderContext.setUV(texCoordData.next());
							renderContext.setVertex(cV2);
							renderContext.setColor(c3);
							renderContext.setUV(texCoordData.next());
							renderContext.setVertex(cV3);
						}
					}
				} else {
					cV1.x = v1.x;
					cV1.y = v1.y;
					cV1.z = v1.z;
					cV2.x = v2.x;
					cV2.y = v2.y;
					cV2.z = v2.z;
					cV3.x = v3.x;
					cV3.y = v3.y;
					cV3.z = v3.z;
					doPerspective(cV1);
					doPerspective(cV2);
					doPerspective(cV3);
					if (!clip) {
						if (lighting) {
							applyShading(v1, c1, n1);
							applyShading(v2, c2, n2);
							applyShading(v3, c3, n3);
						}
						doScreenCoordinates(cV1);
						doScreenCoordinates(cV2);
						doScreenCoordinates(cV3);
						renderContext.setColor(c1);
						renderContext.setVertex(cV1);
						renderContext.setColor(c2);
						renderContext.setUV(texCoordData.next());
						renderContext.setVertex(cV2);
						renderContext.setColor(c3);
						renderContext.setUV(texCoordData.next());
						renderContext.setVertex(cV3);
					}
				}
			}
			renderContext.endDrawingMode();
		}
	}

	protected void drawLines(N3VertexData vertexData, N3ColorData colorData,
			N3NormalData normalData) {
		if (vertexData.hasNext()) {
			clip = false;
			renderContext.beginDrawingMode(N3SoftwareRenderContext.N3_LINES);
			while (vertexData.hasNext()) {
				N3Point3D t1 = (N3Point3D) vertexData.next();
				N3Point3D t2 = (N3Point3D) vertexData.next();
				v1.x = t1.x;
				v1.y = t1.y;
				v1.z = t1.z;
				v2.x = t2.x;
				v2.y = t2.y;
				v2.z = t2.z;
				N3Matrix4D.mult(modelViewMatrix, v1);
				N3Matrix4D.mult(modelViewMatrix, v2);
				c1.setData(colorData.next());
				c2.setData(colorData.next());
				cV1.x = v1.x;
				cV1.y = v1.y;
				cV1.z = v1.z;
				cV2.x = v2.x;
				cV2.y = v2.y;
				cV2.z = v2.z;
				doPerspective(cV1);
				doPerspective(cV2);
				if (!clip) {
					if (lighting) {
						N3Vector3D n1 = (N3Vector3D) normalData.next();
						N3Vector3D n2 = (N3Vector3D) normalData.next();
						applyShading(v1, c1, n1);
						applyShading(v2, c2, n2);
					}
					doScreenCoordinates(cV1);
					doScreenCoordinates(cV2);
					renderContext.setColor(c1);
					renderContext.setVertex(cV1);
					renderContext.setColor(c2);
					renderContext.setVertex(cV2);
				}
			}
			renderContext.endDrawingMode();
		}
	}

	protected void drawLines(N3VertexData vertexData, N3ColorData colorData,
			N3NormalData normalData, N3TexCoordData texCoordData) {
		if (vertexData.hasNext()) {
			clip = false;
			renderContext.beginDrawingMode(N3SoftwareRenderContext.N3_LINES);
			while (vertexData.hasNext()) {
				N3Point3D t1 = (N3Point3D) vertexData.next();
				N3Point3D t2 = (N3Point3D) vertexData.next();
				v1.x = t1.x;
				v1.y = t1.y;
				v1.z = t1.z;
				v2.x = t2.x;
				v2.y = t2.y;
				v2.z = t2.z;
				N3Matrix4D.mult(modelViewMatrix, v1);
				N3Matrix4D.mult(modelViewMatrix, v2);
				c1.setData(colorData.next());
				c2.setData(colorData.next());
				cV1.x = v1.x;
				cV1.y = v1.y;
				cV1.z = v1.z;
				cV2.x = v2.x;
				cV2.y = v2.y;
				cV2.z = v2.z;
				doPerspective(cV1);
				doPerspective(cV2);
				if (!clip) {
					if (lighting) {
						N3Vector3D n1 = (N3Vector3D) normalData.next();
						N3Vector3D n2 = (N3Vector3D) normalData.next();
						applyShading(v1, c1, n1);
						applyShading(v2, c2, n2);
					}
					doScreenCoordinates(cV1);
					doScreenCoordinates(cV2);
					renderContext.setColor(c1);
					renderContext.setUV(texCoordData.next());
					renderContext.setVertex(cV1);
					renderContext.setColor(c2);
					renderContext.setUV(texCoordData.next());
					renderContext.setVertex(cV2);
				}
			}
			renderContext.endDrawingMode();
		}
	}

	protected void drawPoints(N3VertexData vertexData, N3ColorData colorData,
			N3NormalData normalData) {
		if (vertexData.hasNext()) {
			clip = false;
			renderContext.beginDrawingMode(N3SoftwareRenderContext.N3_POINTS);
			while (vertexData.hasNext()) {
				N3Point3D t1 = (N3Point3D) vertexData.next();
				v1.x = t1.x;
				v1.y = t1.y;
				v1.z = t1.z;
				cV1.x = v1.x;
				cV1.y = v1.y;
				cV1.z = v1.z;
				N3Matrix4D.mult(modelViewMatrix, v1);
				c1.setData(colorData.next());
				doPerspective(cV1);
				if (!clip) {
					if (lighting) {
						N3Vector3D n1 = (N3Vector3D) normalData.next();
						applyShading(v1, c1, n1);
					}
					doScreenCoordinates(cV1);
					renderContext.setColor(c1);
					renderContext.setVertex(cV1);
				}
			}
			renderContext.endDrawingMode();
		}
	}

	protected void drawPoints(N3VertexData vertexData, N3ColorData colorData,
			N3NormalData normalData, N3TexCoordData coordData) {
		if (vertexData.hasNext()) {
			clip = false;
			renderContext.beginDrawingMode(N3SoftwareRenderContext.N3_POINTS);
			while (vertexData.hasNext()) {
				N3Point3D t1 = (N3Point3D) vertexData.next();
				v1.x = t1.x;
				v1.y = t1.y;
				v1.z = t1.z;
				cV1.x = v1.x;
				cV1.y = v1.y;
				cV1.z = v1.z;
				N3Matrix4D.mult(modelViewMatrix, v1);
				c1.setData(colorData.next());
				doPerspective(cV1);
				if (!clip) {
					if (lighting) {
						N3Vector3D n1 = (N3Vector3D) normalData.next();
						applyShading(v1, c1, n1);
					}
					doScreenCoordinates(cV1);
					renderContext.setColor(c1);
					renderContext.setUV(coordData.next());
					renderContext.setVertex(cV1);
				}
			}
			renderContext.endDrawingMode();
		}
	}

	public void drawData(N3VertexData vertexData, int dataType,
			N3ColorData colorData) {
		drawData(vertexData, dataType, colorData, new N3NormalData());
	}

	public void drawData(N3VertexData vertexData, int dataType,
			N3ColorData colorData, N3NormalData normalData) {
		if (dataType == this.N3_TRIANGLES_DATA)
			drawTriangles(vertexData, colorData, normalData);
		else if (dataType == this.N3_LINES_DATA)
			drawLines(vertexData, colorData, normalData);
		else
			drawPoints(vertexData, colorData, normalData);
	}

	public void drawData(N3VertexData vertexData, int dataType,
			N3TexCoordData texCoordData) {
	}

	public void drawData(N3VertexData vertexData, int dataType,
			N3ColorData colorData, N3TexCoordData texCoordData,
			N3NormalData normalData) {
		if (dataType == this.N3_TRIANGLES_DATA)
			drawTriangles(vertexData, colorData, normalData, texCoordData);
		else if (dataType == this.N3_LINES_DATA)
			drawLines(vertexData, colorData, normalData, texCoordData);
		else
			drawPoints(vertexData, colorData, normalData, texCoordData);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////

	public void setTexturing(boolean status) {
		super.setTexturing(status);
		if (texturing)
			renderContext.setTexturing(true);
		else
			renderContext.setTexturing(false);
	}

	public void deleteTexture(int id) {
	}

	public int genTexture2D(byte[] data, int dataFormat, int dataType,
			int width, int height) {
		return renderContext.createTexture2D(data, dataFormat, dataType, width,
				height);
	}

	public void copySubTexture(int xOffset, int yOffset, int width, int height,
			byte[] data) {
	}

	public void setTextureCoord2D(float u, float v) {
	}

	public void selectTexture(N3Texture texture) {
		renderContext.selectTexture(texture.getID());
	}

	public void setTextureMode(int mode) {
		if (mode == N3_REPLACE) {
			renderContext
					.setTextureMode(N3SoftwareRenderContext.N3_SWR_REPLACE);
			return;
		}
		if (mode == N3_MODULATE) {
			renderContext
					.setTextureMode(N3SoftwareRenderContext.N3_SWR_MODULATE);
			return;
		}
		renderContext.setTextureMode(N3SoftwareRenderContext.N3_SWR_REPLACE);
	}

	// Manejo de materiales
	public void setMaterialAmbient(int face, N3ColorRGBA color) {
	}

	public void setMaterialDiffuse(int face, N3ColorRGBA color) {
	}

	public void setMaterialAmbientAndDiffuse(int face, N3ColorRGBA color) {
	}

	public void setMaterialSpecular(int face, N3ColorRGBA color, float shininess) {
	}

	public void setMaterialEmission(int face, N3ColorRGBA color) {
	}

	public void setColorMaterialAmbient(int face, N3ColorRGBA color) {
		N3SoftwareLight.setMatAmbient(color);
	}

	public void setColorMaterialDiffuse(int face, N3ColorRGBA color) {
		N3SoftwareLight.setMatDiffuse(color);
	}

	public void setColorMaterialAmbientAndDiffuse(int face, N3ColorRGBA color) {
		setColorMaterialAmbient(face, color);
		setColorMaterialDiffuse(face, color);
	}

	public void setColorMaterialSpecular(int face, N3ColorRGBA color) {
		N3SoftwareLight.setMatSpecular(color);
	}

	public void setColorMaterialEmission(int face, N3ColorRGBA color) {
		N3SoftwareLight.setMatEmission(color);
	}

	// Manejo de luces
	public int getMaxLights() {
		return MAX_LIGHTS;
	}

	protected int getLightConstant(int n) {
		return n;
	}

	public void setLightParam(int paramType, float[] values, int n) {
		if (n < MAX_LIGHTS) {
			if (paramType == N3_AMBIENT) {
				c1.R = values[0];
				c1.G = values[1];
				c1.B = values[2];
				c1.A = values[3];
				lights[n].setAmbiental(c1);
				return;
			}
			if (paramType == N3_DIFFUSE) {
				c1.R = values[0];
				c1.G = values[1];
				c1.B = values[2];
				c1.A = values[3];
				lights[n].setDiffuse(c1);
				return;
			}
			if (paramType == N3_SPECULAR) {
				c1.R = values[0];
				c1.G = values[1];
				c1.B = values[2];
				c1.A = values[3];
				lights[n].setSpecular(c1);
				return;
			}
			if (paramType == N3_SPOT_DIRECTION) {
				d.x = values[0];
				d.y = values[1];
				d.z = values[2];
				N3Matrix4D.mult(modelViewMatrix, d);
				lights[n].setDirection(d);
				return;
			}
			if (paramType == N3_POSITION) {
				v1.x = values[0];
				v1.y = values[1];
				v1.z = values[2];
				v1.w = values[3];
				N3Matrix4D.mult(modelViewMatrix, v1);
				lights[n].setPosition(v1);
				return;
			}
		}
	}

	public void setLightParam(int paramType, float value, int n) {
		if (n < MAX_LIGHTS) {
			if (paramType == N3_SPOT_CUTOFF) {
				lights[n].setCutOff(value);
				return;
			}
			if (paramType == N3_SPOT_EXPONENT) {
				lights[n].setSpotExp(value);
				return;
			}
			if (paramType == N3_CONSTANT_ATTENUATION) {
				lights[n].setConstantAtten(value);
				return;
			}
			if (paramType == N3_LINEAR_ATTENUATION) {
				lights[n].setLinearAtten(value);
				return;
			}
			if (paramType == N3_QUADRATIC_ATTENUATION) {
				lights[n].setQuadAtten(value);
				return;
			}
		}
	}

	public void setAmbientLightValue(N3ColorRGBA c) {
		N3SoftwareLight.setAmbientalLight(c);
	}

	public void enableLight(int n) {
		if (n < MAX_LIGHTS)
			lights[n].enable = true;
	}

	public void disableLight(int n) {
		if (n < MAX_LIGHTS)
			lights[n].enable = false;
	}
}
